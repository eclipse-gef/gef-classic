/*******************************************************************************
 * Copyright (c) 2000, 2023 IBM Corporation and others.
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
package org.eclipse.gef.examples.flow.policies;

import org.eclipse.swt.graphics.Color;

import org.eclipse.draw2d.IFigure;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;

/**
 * @author Daniel Lee
 */
public class ActivityContainerHighlightEditPolicy extends GraphicalEditPolicy {

	private Color revertColor;
	private static Color highLightColor = new Color(200, 200, 240);

	/**
	 * @see org.eclipse.gef.EditPolicy#eraseTargetFeedback(org.eclipse.gef.Request)
	 */
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

	/**
	 * @see org.eclipse.gef.EditPolicy#getTargetEditPart(org.eclipse.gef.Request)
	 */
	@Override
	public EditPart getTargetEditPart(Request request) {
		return request.getType().equals(RequestConstants.REQ_SELECTION_HOVER) ? getHost() : null;
	}

	private void setContainerBackground(Color c) {
		getContainerFigure().setBackgroundColor(c);
	}

	/**
	 * Changes the background color of the container to the highlight color
	 */
	protected void showHighlight() {
		if (revertColor == null) {
			revertColor = getContainerBackground();
			setContainerBackground(highLightColor);
		}
	}

	/**
	 * @see org.eclipse.gef.EditPolicy#showTargetFeedback(org.eclipse.gef.Request)
	 */
	@Override
	public void showTargetFeedback(Request request) {
		if (request.getType().equals(RequestConstants.REQ_CREATE)
				|| request.getType().equals(RequestConstants.REQ_ADD)) {
			showHighlight();
		}
	}

}
