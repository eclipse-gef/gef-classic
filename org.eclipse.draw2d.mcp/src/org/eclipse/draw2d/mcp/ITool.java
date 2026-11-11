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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.api.tools.annotations.NoImplement;

/**
 * Represents a tool that the server provides. Clients should extend
 * {@link AbstractTool}. New methods may be added in the future.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 */
@NoImplement
public interface ITool {
	/**
	 * @return The unique identifier for the tool.
	 */
	String getName();

	/**
	 * @return A human-readable description of what the tool does.
	 */
	String getDescription();

	/**
	 * @return A JSON Schema object that describes the expected structure of the
	 *         arguments when calling this tool.
	 */
	String getSchema();

	/**
	 * The callback function to perform the requested action.
	 *
	 * @param request The request sent by the language model.
	 * @param monitor A progress monitor
	 * @return The server response sent back to the language model.
	 */
	IToolResponse execute(IToolRequest request, IProgressMonitor monitor);
}
