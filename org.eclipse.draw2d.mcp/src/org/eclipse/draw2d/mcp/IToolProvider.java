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

package org.eclipse.draw2d.mcp;

import java.util.List;

/**
 * This interface is used to contribute custom tools to the MCP server via the
 * {@code org.eclipse.draw2d.mcp.tools} extension point. May be implemented by
 * clients.
 */
public interface IToolProvider {
	/**
	 * @return An unmodifiable list of all tools contributed by this provider.
	 */
	List<ITool> getTools();
}
