/*******************************************************************************
 * Copyright (c) 2003, 2026 IBM Corporation and others.
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
package org.eclipse.gef.examples.flow.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.CompoundDirectedGraph;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.Subgraph;

import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.tools.DirectEditManager;

import org.eclipse.gef.examples.flow.model.Activity;
import org.eclipse.gef.examples.flow.model.FlowElement;
import org.eclipse.gef.examples.flow.model.Transition;
import org.eclipse.gef.examples.flow.policies.ActivityDirectEditPolicy;
import org.eclipse.gef.examples.flow.policies.ActivityEditPolicy;
import org.eclipse.gef.examples.flow.policies.ActivityNodeEditPolicy;
import org.eclipse.gef.examples.flow.policies.ActivitySourceEditPolicy;

/**
 * @author hudsonr Created on Jun 30, 2003
 */
public abstract class ActivityPart extends AbstractGraphicalEditPart implements PropertyChangeListener, NodeEditPart {

	protected DirectEditManager manager;

	/**
	 * @see org.eclipse.gef.EditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		getModel().addPropertyChangeListener(this);
	}

	@SuppressWarnings("unchecked") // we know that we only have TransitionParts as connections
	@Override
	public List<? extends TransitionPart> getSourceConnections() {
		return (List<? extends TransitionPart>) super.getSourceConnections();
	}

	protected void applyGraphResults(CompoundDirectedGraph graph, Map<AbstractGraphicalEditPart, Object> map) {
		Node n = (Node) map.get(this);
		getFigure().setBounds(new Rectangle(n.x, n.y, n.width, n.height));

		getSourceConnections().forEach(tr -> tr.applyGraphResults(graph, map));
	}

	public void contributeEdgesToGraph(CompoundDirectedGraph graph, Map<AbstractGraphicalEditPart, Object> map) {
		getSourceConnections().forEach(tr -> tr.contributeToGraph(graph, map));
		getChildren().forEach(child -> child.contributeEdgesToGraph(graph, map));
	}

	public abstract void contributeNodesToGraph(CompoundDirectedGraph graph, Subgraph s,
			Map<AbstractGraphicalEditPart, Object> map);

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new ActivityNodeEditPolicy());
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ActivitySourceEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ActivityEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ActivityDirectEditPolicy());
	}

	/**
	 * @see org.eclipse.gef.EditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		super.deactivate();
		getModel().removePropertyChangeListener(this);
	}

	/**
	 * Returns the Activity model associated with this EditPart
	 *
	 * @return the Activity model
	 */
	@Override
	public Activity getModel() {
		return (Activity) super.getModel();
	}

	@SuppressWarnings("unchecked") // children of an ActivityPart are ActivityParts
	@Override
	public List<? extends ActivityPart> getChildren() {
		return (List<? extends ActivityPart>) super.getChildren();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
	 */
	@Override
	protected List<Transition> getModelSourceConnections() {
		return getModel().getOutgoingTransitions();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
	 */
	@Override
	protected List<Transition> getModelTargetConnections() {
		return getModel().getIncomingTransitions();
	}

	abstract int getAnchorOffset();

	/**
	 * @see NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new BottomAnchor(getFigure(), getAnchorOffset());
	}

	/**
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new BottomAnchor(getFigure(), getAnchorOffset());
	}

	/**
	 * @see NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new TopAnchor(getFigure(), getAnchorOffset());
	}

	/**
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new TopAnchor(getFigure(), getAnchorOffset());
	}

	protected void performDirectEdit() {
	}

	/**
	 * @see org.eclipse.gef.EditPart#performRequest(org.eclipse.gef.Request)
	 */
	@Override
	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
			performDirectEdit();
		}
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (prop != null) {
			switch (prop) {
			case FlowElement.CHILDREN:
				refreshChildren();
				break;
			case FlowElement.INPUTS:
				refreshTargetConnections();
				break;
			case FlowElement.OUTPUTS:
				refreshSourceConnections();
				break;
			case Activity.NAME:
				refreshVisuals();
				break;
			default:
				break;
			}
		}

		// Causes Graph to re-layout
		((GraphicalEditPart) (getViewer().getContents())).getFigure().revalidate();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#setFigure(org.eclipse.draw2d.IFigure)
	 */
	@Override
	protected void setFigure(IFigure figure) {
		figure.getBounds().setSize(0, 0);
		super.setFigure(figure);
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#toString()
	 */
	@Override
	public String toString() {
		return getModel().toString();
	}

}
