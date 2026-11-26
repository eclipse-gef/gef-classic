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

import java.util.Collections;
import java.util.Map;

import org.eclipse.draw2d.mcp.IToolRequest;

import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;

/**
 * Adapter for the internal {@link CallToolRequest} instance.
 */
public record MCPToolRequest(CallToolRequest referee) implements IToolRequest {
	/**
	 * @see IToolRequest#getArguments()
	 */
	@Override
	public Map<String, Object> getArguments() {
		Map<String, Object> arguments = referee.arguments();
		if (arguments == null) {
			return Collections.emptyMap();
		}
		return Collections.unmodifiableMap(arguments);
	}
}
