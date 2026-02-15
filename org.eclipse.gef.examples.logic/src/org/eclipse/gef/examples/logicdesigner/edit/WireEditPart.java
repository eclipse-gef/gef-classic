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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.accessibility.AccessibleEvent;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RelativeBendpoint;

import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;

import org.eclipse.gef.examples.logicdesigner.LogicMessages;
import org.eclipse.gef.examples.logicdesigner.figures.FigureFactory;
import org.eclipse.gef.examples.logicdesigner.model.Wire;
import org.eclipse.gef.examples.logicdesigner.model.WireBendpoint;

/**
 * Implements a Connection Editpart to represent a Wire like connection.
 *
 */
public class WireEditPart extends AbstractConnectionEditPart implements PropertyChangeListener {

	AccessibleEditPart acc;

	@Override
	public void activate() {
		super.activate();
		getModel().addPropertyChangeListener(this);
	}

	@Override
	public void activateFigure() {
		super.activateFigure();
		/*
		 * Once the figure has been added to the ConnectionLayer, start listening for
		 * its router to change.
		 */
		getFigure().addPropertyChangeListener(Connection.PROPERTY_CONNECTION_ROUTER, this);
	}

	/**
	 * Adds extra EditPolicies as required.
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new WireEndpointEditPolicy());
		// Note that the Connection is already added to the diagram and knows
		// its Router.
		refreshBendpointEditPolicy();
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new WireEditPolicy());
	}

	/**
	 * Returns a newly created Figure to represent the connection.
	 *
	 * @return The created Figure.
	 */
	@Override
	protected IFigure createFigure() {
		return FigureFactory.createNewBendableWire(getModel());
	}

	@Override
	public void deactivate() {
		getModel().removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public void deactivateFigure() {
		getFigure().removePropertyChangeListener(Connection.PROPERTY_CONNECTION_ROUTER, this);
		super.deactivateFigure();
	}

	@Override
	public AccessibleEditPart getAccessibleEditPart() {
		if (acc == null) {
			acc = new AccessibleGraphicalEditPart() {
				@Override
				public void getName(AccessibleEvent e) {
					e.result = LogicMessages.Wire_LabelText;
				}
			};
		}
		return acc;
	}

	@Override
	public Wire getModel() {
		return (Wire) super.getModel();
	}

	/**
	 * Returns the Figure associated with this, which draws the Wire.
	 *
	 * @return Figure of this.
	 */
	@Override
	public PolylineConnection getFigure() {
		return (PolylineConnection) super.getFigure();
	}

	/**
	 * Listens to changes in properties of the Wire (like the contents being
	 * carried), and reflects is in the visuals.
	 *
	 * @param event Event notifying the change.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getPropertyName();
		if (Connection.PROPERTY_CONNECTION_ROUTER.equals(property)) {
			refreshBendpoints();
			refreshBendpointEditPolicy();
		}
		if ("value".equals(property)) { //$NON-NLS-1$
			refreshVisuals();
		}
		if ("bendpoint".equals(property)) { //$NON-NLS-1$
			refreshBendpoints();
		}
	}

	/**
	 * Updates the bendpoints, based on the model.
	 */
	protected void refreshBendpoints() {
		if (getConnectionFigure().getConnectionRouter() instanceof ManhattanConnectionRouter) {
			return;
		}
		List<WireBendpoint> modelConstraint = getModel().getBendpoints();
		List<RelativeBendpoint> figureConstraint = new ArrayList<>();
		for (int i = 0; i < modelConstraint.size(); i++) {
			WireBendpoint wbp = modelConstraint.get(i);
			RelativeBendpoint rbp = new RelativeBendpoint(getConnectionFigure());
			rbp.setRelativeDimensions(wbp.getFirstRelativeDimension(), wbp.getSecondRelativeDimension());
			rbp.setWeight((i + 1) / ((float) modelConstraint.size() + 1));
			figureConstraint.add(rbp);
		}
		getConnectionFigure().setRoutingConstraint(figureConstraint);
	}

	private void refreshBendpointEditPolicy() {
		if (getConnectionFigure().getConnectionRouter() instanceof ManhattanConnectionRouter) {
			installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, null);
		} else {
			installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new WireBendpointEditPolicy());
		}
	}

	/**
	 * Refreshes the visual aspects of this, based upon the model (Wire). It changes
	 * the wire color depending on the state of Wire.
	 *
	 */
	@Override
	protected void refreshVisuals() {
		refreshBendpoints();
		FigureFactory.updateWireLook(getModel(), getFigure());
	}

}
