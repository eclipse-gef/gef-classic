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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.draw2d.mcp.ITool;
import org.eclipse.draw2d.mcp.IToolRequest;
import org.eclipse.draw2d.mcp.IToolResponse;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.internal.mcp.tools.AbstractGraphicalViewerTool;
import org.eclipse.gef.internal.mcp.tools.Messages;

/**
 * Tool for selecting the specified edit-parts in a graphical viewer.
 */
public final class SelectEditPartTool extends AbstractEditPartTool {
	/**
	 * @see ITool#getName()
	 */
	@Override
	public String getName() {
		return "org.eclipse.gef.graphical.parts.select"; //$NON-NLS-1$
	}

	/**
	 * @see ITool#getDescription()
	 */
	@Override
	public String getDescription() {
		return Messages.DESCRIPTION_SelectEditPartTool;
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
						"editPartNames": {
							"type": "array",
							"items": {
								"type": "string"
							},
							"description": "Names of the edit parts to select."
						}
					},
					"required": ["editorName", "editPartNames"]
				}"""; //$NON-NLS-1$ ;
	}

	/**
	 * @see AbstractGraphicalViewerTool#execute(GraphicalViewer, IToolRequest,
	 *      IProgressMonitor)
	 */
	@Override
	protected IToolResponse execute(GraphicalViewer viewer, IToolRequest request, IProgressMonitor monitor) {
		List<EditPart> editParts = getEditParts(viewer, request);

		getDisplay().syncExec(() -> viewer.setSelection(new StructuredSelection(editParts)));

		return new EditPartToolResponse(editParts);
	}
}
