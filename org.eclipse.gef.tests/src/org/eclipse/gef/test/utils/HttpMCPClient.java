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

package org.eclipse.gef.test.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Display;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

/**
 * Primitive HTTP client for communicating with a locally hosted MCP server.
 */
public class HttpMCPClient {
	private static final Pattern SSE_DATA = Pattern.compile("data:\\s*(\\{.*\\})"); //$NON-NLS-1$
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final HttpClient httpClient;
	private final ObjectMapper mapper;
	private final String endpoint;

	private String sessionId;
	private int requestId = 1;

	public HttpMCPClient(String endpoint) {
		this.endpoint = endpoint;
		this.httpClient = HttpClient.newHttpClient();
		this.mapper = new ObjectMapper();
	}

	public void initialize() throws Exception {
		ObjectNode request = mapper.createObjectNode();

		request.put("jsonrpc", "2.0"); //$NON-NLS-1$ //$NON-NLS-2$
		request.put("method", "initialize"); //$NON-NLS-1$ //$NON-NLS-2$
		request.put("id", requestId++); //$NON-NLS-1$

		ObjectNode params = request.putObject("params"); //$NON-NLS-1$
		params.put("protocolVersion", "2025-06-18"); //$NON-NLS-1$ //$NON-NLS-2$
		params.putObject("capabilities"); //$NON-NLS-1$

		ObjectNode clientInfo = params.putObject("clientInfo"); //$NON-NLS-1$
		clientInfo.put("name", "junit-test"); //$NON-NLS-1$ //$NON-NLS-2$
		clientInfo.put("version", "1.0.0"); //$NON-NLS-1$//$NON-NLS-2$

		HttpResponse<String> response = send(request);
		sessionId = response.headers()//
				.firstValue("Mcp-Session-Id").orElseThrow(() -> new RuntimeException("MCP session ID missing")); //$NON-NLS-1$//$NON-NLS-2$

		// Complete the MCP initialization handshake.
		ObjectNode notification = mapper.createObjectNode();
		notification.put("jsonrpc", "2.0"); //$NON-NLS-1$ //$NON-NLS-2$
		notification.put("method", "notifications/initialized"); //$NON-NLS-1$ //$NON-NLS-2$

		send(notification);
	}

	public JsonNode callTool(String toolName, String arguments) throws Exception {

		ObjectNode request = mapper.createObjectNode();

		request.put("jsonrpc", "2.0"); //$NON-NLS-1$ //$NON-NLS-2$
		request.put("method", "tools/call"); //$NON-NLS-1$//$NON-NLS-2$
		request.put("id", requestId++); //$NON-NLS-1$

		ObjectNode params = request.putObject("params"); //$NON-NLS-1$
		params.put("name", toolName); //$NON-NLS-1$
		if (arguments != null) {
			params.set("arguments", mapper.readTree(arguments)); //$NON-NLS-1$
		}

		HttpResponse<String> response = send(request); // $NON-NLS-1$

		String body = response.body();
		return mapper.readTree(getData(body));
	}

	private static String getData(String sse) {
		Matcher matcher = SSE_DATA.matcher(sse);

		if (!matcher.find()) {
			throw new RuntimeException("No JSON data found in SSE response"); //$NON-NLS-1$
		}
		return matcher.group(1);
	}

	private HttpResponse<String> send(ObjectNode request) throws Exception {
		Display display = Display.getCurrent();

		if (display == null) {
			return doSend(request);
		}

		Future<HttpResponse<String>> response = executor.submit(() -> doSend(request));

		while (!display.isDisposed() && !response.isDone()) {
			while (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return response.get();
	}

	private HttpResponse<String> doSend(ObjectNode request) throws Exception {
		HttpRequest.Builder builder = HttpRequest.newBuilder() //
				.uri(URI.create(endpoint)) //
				.timeout(Duration.ofSeconds(30)) //
				.header("Content-Type", "application/json") // //$NON-NLS-1$ //$NON-NLS-2$
				.header("Accept", "application/json, text/event-stream") //$NON-NLS-1$ //$NON-NLS-2$
				.POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(request)));

		if (sessionId != null) {
			builder.header("Mcp-Session-Id", sessionId); //$NON-NLS-1$
			builder.header("MCP-Protocol-Version", "2025-06-18"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
	}
}
