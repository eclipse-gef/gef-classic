/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d.mcp.internal;

import java.net.InetSocketAddress;
import java.util.Map;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;

import org.eclipse.draw2d.mcp.IContent;
import org.eclipse.draw2d.mcp.ITool;
import org.eclipse.draw2d.mcp.IToolResponse;

import io.modelcontextprotocol.json.McpJsonMapperSupplier;
import io.modelcontextprotocol.json.schema.JsonSchemaValidatorSupplier;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import io.modelcontextprotocol.spec.McpStreamableServerTransportProvider;
import jakarta.servlet.Servlet;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.MapType;

@Component
public class MCPServer {
	private static final Bundle BUNDLE = FrameworkUtil.getBundle(ToolExtensionRegistry.class);
	private static final ILog LOGGER = Platform.getLog(BUNDLE);
	private McpSyncServer server;
	private Server httpServer;

	@Reference
	private JsonSchemaValidatorSupplier validatorSupplier;

	@Reference
	private McpJsonMapperSupplier mapperSupplier;

	@Activate
	public void activate() {
		// Create transport
		McpStreamableServerTransportProvider transportProvider = HttpServletStreamableServerTransportProvider.builder()
				.jsonMapper(mapperSupplier.get()) //
				.mcpEndpoint("/mcp") //
				.build();

		// Create MCP server
		server = McpServer.sync(transportProvider) //
				.jsonMapper(mapperSupplier.get()) //
				.jsonSchemaValidator(validatorSupplier.get()) //
				.serverInfo("eclipse-draw2d", BUNDLE.getVersion().toString()) //
				.capabilities(ServerCapabilities.builder().tools(true).build()) //
				.build();

		// Define tool input schema
		ObjectMapper mapper = new ObjectMapper();
		MapType schemaType = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);

		// Register tools
		for (ITool tool : ToolExtensionRegistry.getTools()) {
			Map<String, Object> schema = mapper.readValue(tool.getSchema(), schemaType);
			Tool mcpTool = Tool.builder(tool.getName(), schema) //
					.description(tool.getDescription()) //
					.build();

			SyncToolSpecification mcpToolSpecification = SyncToolSpecification.builder() //
					.tool(mcpTool).callHandler((exchange, request) -> callback(tool, exchange, request)).build();

			server.addTool(mcpToolSpecification);
		}

		// Create embedded Jetty server
		Server httpServer = new Server(new InetSocketAddress("127.0.0.1", 8080));

		// Register MCP servlet
		ServletHolder servletHolder = new ServletHolder((Servlet) transportProvider);
		servletHolder.setAsyncSupported(true);

		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/");
		context.addServlet(servletHolder, "/mcp");

		httpServer.setHandler(context);

		// Start HTTP server
		try {
			httpServer.start();
			LOGGER.info("GEF Classic MCP Server listening on http://localhost:8080/mcp");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Deactivate
	public void deactivate() {
		if (httpServer != null) {
			httpServer.destroy();
			httpServer = null;
		}

		if (server != null) {
			server.closeGracefully();
			server = null;
		}
	}

	private static CallToolResult callback(ITool tool, McpSyncServerExchange exchange, CallToolRequest request) {
		MCPToolRequest requestWrapper = new MCPToolRequest(request);
		IToolResponse responseWrapper = tool.execute(requestWrapper, new NullProgressMonitor());
		return unwrapResponse(responseWrapper);
	}

	private static CallToolResult unwrapResponse(IToolResponse responseWrapper) {
		CallToolResult.Builder responseBuilder = CallToolResult.builder();
		responseBuilder.isError(responseWrapper.isError());
		Object structuredContent = responseWrapper.getStructuredContent();
		if (structuredContent != null) {
			responseBuilder.structuredContent(structuredContent);
		}
		for (IContent contentWrapper : responseWrapper.getContent()) {
			Content content = unwrapContent(contentWrapper);
			responseBuilder.addContent(content);
		}
		return responseBuilder.build();
	}

	private static Content unwrapContent(IContent contentWrapper) {
		String data = contentWrapper.data();
		return switch (contentWrapper.type()) {
		case TEXT -> TextContent.builder(data).build();
		};
	}
}
