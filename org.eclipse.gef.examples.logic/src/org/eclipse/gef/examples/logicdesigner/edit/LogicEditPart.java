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
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.DropRequest;

import org.eclipse.gef.examples.logicdesigner.figures.NodeFigure;
import org.eclipse.gef.examples.logicdesigner.model.LogicElement;
import org.eclipse.gef.examples.logicdesigner.model.LogicSubpart;
import org.eclipse.gef.examples.logicdesigner.model.Wire;

/**
 * Provides support for
 */
public abstract class LogicEditPart extends org.eclipse.gef.editparts.AbstractGraphicalEditPart
		implements NodeEditPart, PropertyChangeListener {

	private AccessibleEditPart acc;

	@Override
	public void activate() {
		if (isActive()) {
			return;
		}
		super.activate();
		getLogicSubpart().addPropertyChangeListener(this);
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new LogicElementEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new LogicNodeEditPolicy());
	}

	protected abstract AccessibleEditPart createAccessible();

	/**
	 * Makes the EditPart insensible to changes in the model by removing itself from
	 * the model's list of listeners.
	 */
	@Override
	public void deactivate() {
		if (!isActive()) {
			return;
		}
		super.deactivate();
		getLogicSubpart().removePropertyChangeListener(this);
	}

	@Override
	protected AccessibleEditPart getAccessibleEditPart() {
		if (acc == null) {
			acc = createAccessible();
		}
		return acc;
	}

	/**
	 * Returns the model associated with this as a LogicSubPart.
	 *
	 * @return The model of this as a LogicSubPart.
	 */
	protected LogicSubpart getLogicSubpart() {
		return (LogicSubpart) getModel();
	}

	/**
	 * Returns a list of connections for which this is the source.
	 *
	 * @return List of connections.
	 */
	@Override
	protected List<Wire> getModelSourceConnections() {
		return getLogicSubpart().getSourceConnections();
	}

	/**
	 * Returns a list of connections for which this is the target.
	 *
	 * @return List of connections.
	 */
	@Override
	protected List<Wire> getModelTargetConnections() {
		return getLogicSubpart().getTargetConnections();
	}

	/**
	 * Returns the Figure of this, as a node type figure.
	 *
	 * @return Figure as a NodeFigure.
	 */
	protected NodeFigure getNodeFigure() {
		return (NodeFigure) getFigure();
	}

	/**
	 * Returns the connection anchor for the given ConnectionEditPart's source.
	 *
	 * @return ConnectionAnchor.
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connEditPart) {
		Wire wire = (Wire) connEditPart.getModel();
		return getNodeFigure().getConnectionAnchor(wire.getSourceTerminal());
	}

	/**
	 * Returns the connection anchor of a source connection which is at the given
	 * point.
	 *
	 * @return ConnectionAnchor.
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		Point pt = new Point(((DropRequest) request).getLocation());
		return getNodeFigure().getSourceConnectionAnchorAt(pt);
	}

	/**
	 * Returns the connection anchor for the given ConnectionEditPart's target.
	 *
	 * @return ConnectionAnchor.
	 */
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connEditPart) {
		Wire wire = (Wire) connEditPart.getModel();
		return getNodeFigure().getConnectionAnchor(wire.getTargetTerminal());
	}

	/**
	 * Returns the connection anchor of a terget connection which is at the given
	 * point.
	 *
	 * @return ConnectionAnchor.
	 */
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		Point pt = new Point(((DropRequest) request).getLocation());
		return getNodeFigure().getTargetConnectionAnchorAt(pt);
	}

	/**
	 * Returns the name of the given connection anchor.
	 *
	 * @return The name of the ConnectionAnchor as a String.
	 */
	protected final String mapConnectionAnchorToTerminal(ConnectionAnchor c) {
		return getNodeFigure().getConnectionAnchorName(c);
	}

	/**
	 * Handles changes in properties of this. It is activated through the
	 * PropertyChangeListener. It updates children, source and target connections,
	 * and the visuals of this based on the property changed.
	 *
	 * @param evt Event which details the property change.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (prop != null) {
			switch (prop) {
			case LogicElement.CHILDREN:
				if (evt.getOldValue() instanceof Integer intVal) {
					// new child
					addChild(createChild(evt.getNewValue()), intVal.intValue());
				} else {
					// remove child
					removeChild(getViewer().getEditPartForModel(evt.getOldValue()));
				}
				break;
			case LogicElement.INPUTS:
				refreshTargetConnections();
				break;
			case LogicElement.OUTPUTS:
				refreshSourceConnections();
				break;
			case LogicSubpart.ID_SIZE:
			case LogicSubpart.ID_LOCATION:
				refreshVisuals();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Updates the visual aspect of this.
	 */
	@Override
	protected void refreshVisuals() {
		Point loc = getLogicSubpart().getLocation();
		Dimension size = getLogicSubpart().getSize();
		Rectangle r = new Rectangle(loc, size);

		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), r);
	}

}
