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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.mcp.ITool;
import org.eclipse.draw2d.mcp.IToolRequest;
import org.eclipse.draw2d.mcp.IToolResponse;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.internal.mcp.tools.AbstractGraphicalViewerTool;
import org.eclipse.gef.internal.mcp.tools.Messages;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * Tool for moving the specified edit-parts in a graphical viewer.
 */
public final class MoveEditPartTool extends AbstractEditPartTool {
	/**
	 * @see ITool#getName()
	 */
	@Override
	public String getName() {
		return "org.eclipse.gef.graphical.parts.move"; //$NON-NLS-1$
	}

	/**
	 * @see ITool#getDescription()
	 */
	@Override
	public String getDescription() {
		return Messages.DESCRIPTION_MoveEditPartTool;
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
							"description": "Names of the edit parts to move."
						},
						"moveX": {
							"type": "number",
							"description": "The horizontal change."
						},
						"moveY": {
							"type": "number",
							"description": "The vertical change."
						}
					},
					"required": ["editorName", "editPartNames", "moveX", "moveY"]
				}"""; //$NON-NLS-1$ ;
	}

	/**
	 * @see AbstractGraphicalViewerTool#execute(GraphicalViewer, IToolRequest,
	 *      IProgressMonitor)
	 */
	@Override
	protected IToolResponse execute(GraphicalViewer viewer, IToolRequest request, IProgressMonitor monitor) {
		List<EditPart> editParts = getEditParts(viewer, request);

		int dx = (int) request.getArguments().get("moveX"); //$NON-NLS-1$
		int dy = (int) request.getArguments().get("moveY"); //$NON-NLS-1$

		ChangeBoundsRequest moveRequest = new ChangeBoundsRequest(RequestConstants.REQ_MOVE_CHILDREN);
		moveRequest.setMoveDelta(new Point(dx, dy));
		moveRequest.setEditParts(editParts);

		return execute(viewer, editParts, moveRequest);
	}

	@Override
	protected void executeCommand(EditPartViewer viewer, Command command) {
		super.executeCommand(viewer, command);
		// Apply figure changes
		viewer.flush();
	}

	@Override
	protected Command getCommand(EditPart editPart, Request request) {
		return editPart.getParent().getCommand(request);
	}
}
