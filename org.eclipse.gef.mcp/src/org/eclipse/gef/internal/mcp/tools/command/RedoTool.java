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

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.draw2d.mcp.ErrorToolResponse;
import org.eclipse.draw2d.mcp.ITool;
import org.eclipse.draw2d.mcp.IToolRequest;
import org.eclipse.draw2d.mcp.IToolResponse;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.internal.mcp.tools.AbstractGraphicalViewerTool;
import org.eclipse.gef.internal.mcp.tools.Messages;

/**
 * Tool for redoing the most recent command.
 */
public class RedoTool extends AbstractCommandTool {

	/**
	 * @see ITool#getName()
	 */
	@Override
	public String getName() {
		return "org.eclipse.gef.command.redo"; //$NON-NLS-1$
	}

	/**
	 * @see ITool#getDescription()
	 */
	@Override
	public String getDescription() {
		return Messages.DESCRIPTION_RedoTool;
	}

	/**
	 * @see ITool#getSchema()
	 */
	@Override
	public String getSchema() {
		return """
				{
					"type": "object",
					"properties": {
						"editorName": {
							"type": "string",
							"description": "Name of the graphical editor."
						}
					},
					"required": ["editorName"]
				}"""; //$NON-NLS-1$
	}

	/**
	 * @see AbstractGraphicalViewerTool#execute(GraphicalViewer, IToolRequest,
	 *      IProgressMonitor)
	 */
	@Override
	protected IToolResponse execute(GraphicalViewer viewer, IToolRequest request, IProgressMonitor monitor) {
		CommandStack commandStack = viewer.getEditDomain().getCommandStack();
		if (!commandStack.canRedo()) {
			return new ErrorToolResponse(Messages.ERROR_NoRedo);
		}
		Command command = commandStack.getRedoCommand();
		getDisplay().syncExec(commandStack::redo);
		return new CommandToolResponse(command);
	}

}
