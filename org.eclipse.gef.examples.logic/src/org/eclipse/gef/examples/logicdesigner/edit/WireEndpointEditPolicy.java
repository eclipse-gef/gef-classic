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

import org.eclipse.draw2d.PolylineConnection;

import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

import org.eclipse.gef.examples.logicdesigner.figures.FigureFactory;

public class WireEndpointEditPolicy extends ConnectionEndpointEditPolicy {

	@Override
	protected void addSelectionHandles() {
		super.addSelectionHandles();
		getConnectionFigure().setLineWidth(getConnectionFigure().getLineWidth() + 1);
	}

	private PolylineConnection getConnectionFigure() {
		return getHost().getFigure();
	}

	@Override
	public WireEditPart getHost() {
		return (WireEditPart) super.getHost();
	}

	@Override
	protected void removeSelectionHandles() {
		super.removeSelectionHandles();
		FigureFactory.updateWireLook(getHost().getModel(), getConnectionFigure());
	}

}
