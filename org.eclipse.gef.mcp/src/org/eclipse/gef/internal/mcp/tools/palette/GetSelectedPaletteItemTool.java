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

import org.eclipse.draw2d.mcp.AbstractToolResponse;
import org.eclipse.draw2d.mcp.IContent;
import org.eclipse.draw2d.mcp.ITool;
import org.eclipse.draw2d.mcp.IToolRequest;
import org.eclipse.draw2d.mcp.IToolResponse;
import org.eclipse.draw2d.mcp.TextContent;

import org.eclipse.gef.internal.mcp.tools.Messages;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;

/**
 * Tool for getting the currently selected palette item.
 */
public final class GetSelectedPaletteItemTool extends AbstractPaletteViewerTool {

	/**
	 * @see ITool#getName()
	 */
	@Override
	public String getName() {
		return "org.eclipse.gef.palette.items.selected"; //$NON-NLS-1$
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
				}"""; //$NON-NLS-1$ ;
	}

	/**
	 * @see ITool#getDescription()
	 */
	@Override
	public String getDescription() {
		return Messages.DESCRIPTION_GetSelectedPaletteItemTool;
	}

	/**
	 * @see AbstractPaletteViewerTool#execute(PaletteViewer, IToolRequest,
	 *      IProgressMonitor)
	 */
	@Override
	protected IToolResponse execute(PaletteViewer viewer, IToolRequest request, IProgressMonitor monitor) {
		ToolEntry tool = viewer.getActiveTool();
		return new AbstractToolResponse() {
			@Override
			public List<IContent> getContent() {
				return List.of(new TextContent(tool.getLabel()));
			}
		};
	}

}
