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

package org.eclipse.gef.internal.mcp.tools.palette;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

import org.eclipse.draw2d.mcp.AbstractToolResponse;
import org.eclipse.draw2d.mcp.ErrorToolResponse;
import org.eclipse.draw2d.mcp.IContent;
import org.eclipse.draw2d.mcp.ITool;
import org.eclipse.draw2d.mcp.IToolRequest;
import org.eclipse.draw2d.mcp.IToolResponse;
import org.eclipse.draw2d.mcp.TextContent;

import org.eclipse.gef.internal.mcp.tools.Messages;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;

/**
 * Tool to select a specific palette item.
 */
public final class SelectPaletteItemTool extends AbstractPaletteViewerTool {

	/**
	 * @see ITool#getName()
	 */
	@Override
	public String getName() {
		return "org.eclipse.gef.palette.items.select"; //$NON-NLS-1$
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
						},
						"toolName": {
							"type": "string",
							"description": "Name of the palette tool."
						}
					},
					"required": ["editorName", "toolName"]
				}"""; //$NON-NLS-1$ ;
	}

	/**
	 * @see ITool#getDescription()
	 */
	@Override
	public String getDescription() {
		return Messages.DESCRIPTION_SelectPaletteItemTool;
	}

	/**
	 * @see AbstractPaletteViewerTool#execute(PaletteViewer, IToolRequest,
	 *      IProgressMonitor)
	 */
	@Override
	protected IToolResponse execute(PaletteViewer viewer, IToolRequest request, IProgressMonitor monitor) {
		String toolName = (String) request.getArguments().get("toolName"); //$NON-NLS-1$

		ToolEntry tool = getTool(viewer, toolName);
		if (tool == null) {
			return new ErrorToolResponse(NLS.bind(Messages.ERROR_NoPaletteTool, toolName));
		}

		getDisplay().syncExec(() -> viewer.setActiveTool(tool));

		return new AbstractToolResponse() {
			@Override
			public List<IContent> getContent() {
				return List.of(new TextContent(tool.getLabel()));
			}
		};
	}
}
