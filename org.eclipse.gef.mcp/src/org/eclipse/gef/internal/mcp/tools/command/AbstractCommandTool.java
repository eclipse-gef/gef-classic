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

package org.eclipse.gef.internal.mcp.tools.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.mcp.AbstractToolResponse;
import org.eclipse.draw2d.mcp.IContent;
import org.eclipse.draw2d.mcp.TextContent;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.internal.mcp.tools.AbstractGraphicalViewerTool;

/**
 * Abstract base class for all command-related tools.
 */
public abstract class AbstractCommandTool extends AbstractGraphicalViewerTool {
	/**
	 * Default response after interacting with a single command.
	 */
	protected static class CommandToolResponse extends AbstractToolResponse {
		private final List<IContent> content = new ArrayList<>();

		public CommandToolResponse(Command command) {
			content.add(new TextContent(command.getLabel()));
		}

		@Override
		public List<IContent> getContent() {
			return Collections.unmodifiableList(content);
		}
	}
}
