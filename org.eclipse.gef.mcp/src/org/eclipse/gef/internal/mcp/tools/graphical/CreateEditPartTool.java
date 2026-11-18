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

package org.eclipse.gef.internal.mcp.tools.graphical;

import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

import org.eclipse.draw2d.mcp.ErrorToolResponse;
import org.eclipse.draw2d.mcp.ITool;
import org.eclipse.draw2d.mcp.IToolRequest;
import org.eclipse.draw2d.mcp.IToolResponse;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.internal.mcp.tools.AbstractGraphicalViewerTool;
import org.eclipse.gef.internal.mcp.tools.Messages;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.tools.CreationTool;

/**
 * Tool for creating a new edit part using the currently selected tool.
 */
public final class CreateEditPartTool extends AbstractEditPartTool {
	/**
	 * @see ITool#getName()
	 */
	@Override
	public String getName() {
		return "org.eclipse.gef.graphical.parts.create"; //$NON-NLS-1$
	}

	/**
	 * @see ITool#getDescription()
	 */
	@Override
	public String getDescription() {
		return Messages.DESCRIPTION_CreateEditPartTool;
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
		ToolEntry toolEntry = viewer.getEditDomain().getPaletteViewer().getActiveTool();

		if (!(toolEntry instanceof CreationToolEntry)) {
			return new ErrorToolResponse(NLS.bind(Messages.ERROR_NoCreationTool, toolEntry.getLabel()));
		}

		CreationFactory factory = (CreationFactory) toolEntry.getToolProperty(CreationTool.PROPERTY_CREATION_FACTORY);

		CreateRequest createRequest = new CreateRequest();
		createRequest.setFactory(factory);

		return execute(viewer, Collections.singletonList(viewer.getContents()), createRequest);
	}
}
