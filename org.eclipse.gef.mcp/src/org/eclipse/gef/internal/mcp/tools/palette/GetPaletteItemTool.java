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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.draw2d.mcp.AbstractToolResponse;
import org.eclipse.draw2d.mcp.IContent;
import org.eclipse.draw2d.mcp.ITool;
import org.eclipse.draw2d.mcp.IToolRequest;
import org.eclipse.draw2d.mcp.IToolResponse;
import org.eclipse.draw2d.mcp.TextContent;

import org.eclipse.gef.internal.mcp.tools.Messages;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;

/**
 * Tool for getting all available palette items.
 */
public final class GetPaletteItemTool extends AbstractPaletteViewerTool {

	/**
	 * @see ITool#getName()
	 */
	@Override
	public String getName() {
		return "org.eclipse.gef.palette.items"; //$NON-NLS-1$
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
		return Messages.DESCRIPTION_GetPaletteItemTool;
	}

	/**
	 * @see AbstractPaletteViewerTool#execute(PaletteViewer, IToolRequest,
	 *      IProgressMonitor)
	 */
	@Override
	protected IToolResponse execute(PaletteViewer viewer, IToolRequest request, IProgressMonitor monitor) {
		List<IContent> elements = getElements(viewer.getPaletteRoot());
		return new AbstractToolResponse() {
			@Override
			public List<IContent> getContent() {
				return elements;
			}
		};
	}

	/**
	 * Helper function for converting all tool entries within the palette root to
	 * MCP content values. Each value contains the human-readable display name of
	 * the tool.
	 *
	 * @param root The palette root.
	 * @return An unmodifiable list of content values.
	 */
	private List<IContent> getElements(PaletteRoot root) {
		List<IContent> elements = new ArrayList<>();
		for (ToolEntry tool : getTools(root, tool -> true)) {
			elements.add(new TextContent(tool.getLabel()));
		}
		return Collections.unmodifiableList(elements);
	}
}
