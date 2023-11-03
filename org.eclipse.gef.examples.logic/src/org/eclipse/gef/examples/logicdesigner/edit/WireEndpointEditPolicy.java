/*******************************************************************************
 * Copyright (c) 2000, 2022 IBM Corporation and others.
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

import org.eclipse.gef.GraphicalEditPart;

public class WireEndpointEditPolicy extends org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy {

	@Override
	protected void addSelectionHandles() {
		super.addSelectionHandles();
		getConnectionFigure().setLineWidth(2);
	}

	protected PolylineConnection getConnectionFigure() {
		return (PolylineConnection) ((GraphicalEditPart) getHost()).getFigure();
	}

	@Override
	protected void removeSelectionHandles() {
		super.removeSelectionHandles();
		getConnectionFigure().setLineWidth(0);
	}

}
