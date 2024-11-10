/*******************************************************************************
 * Copyright (c) 2000, 2024 IBM Corporation and others.
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
package org.eclipse.gef.editparts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Viewport;

import org.eclipse.gef.GraphicalEditPart;

/**
 * @author hudsonr
 */
abstract class ViewportHelper {

	protected GraphicalEditPart owner;

	protected ViewportHelper(GraphicalEditPart owner) {
		this.owner = owner;
	}

	@SuppressWarnings("static-method")
	protected Viewport findViewport(GraphicalEditPart part) {
		IFigure figure = null;
		Viewport port = null;
		do {
			if (figure == null) {
				figure = part.getContentPane();
			} else {
				figure = figure.getParent();
			}
			if (figure instanceof Viewport vp) {
				port = vp;
				break;
			}
		} while (figure != part.getFigure() && figure != null);
		return port;
	}

}
