/*******************************************************************************
 * Copyright (c) 2000, 2026 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.examples.logicdesigner.edit;

import org.eclipse.swt.graphics.Color;

import org.eclipse.draw2d.IFigure;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;

import org.eclipse.gef.examples.logicdesigner.figures.LogicEditorColors;

/**
 */
public class ContainerHighlightEditPolicy extends org.eclipse.gef.editpolicies.GraphicalEditPolicy {

	private Color revertColor;

	@Override
	public void eraseTargetFeedback(Request request) {
		if (revertColor != null) {
			setContainerBackground(revertColor);
			revertColor = null;
		}
	}

	private Color getContainerBackground() {
		return getContainerFigure().getBackgroundColor();
	}

	private IFigure getContainerFigure() {
		return getHost().getFigure();
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		return request.getType().equals(RequestConstants.REQ_SELECTION_HOVER) ? getHost() : null;
	}

	private void setContainerBackground(Color c) {
		getContainerFigure().setBackgroundColor(c);
	}

	protected void showHighlight() {
		if (revertColor == null) {
			revertColor = getContainerBackground();
			setContainerBackground(LogicEditorColors.INSTANCE.getLogicBackground());
		}
	}

	@Override
	public void showTargetFeedback(Request request) {
		if (request.getType().equals(RequestConstants.REQ_MOVE) || request.getType().equals(RequestConstants.REQ_ADD)
				|| request.getType().equals(RequestConstants.REQ_CLONE)
				|| request.getType().equals(RequestConstants.REQ_CONNECTION_START)
				|| request.getType().equals(RequestConstants.REQ_CONNECTION_END)
				|| request.getType().equals(RequestConstants.REQ_CREATE)) {
			showHighlight();
		}
	}

}
