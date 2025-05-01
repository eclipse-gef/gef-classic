/*******************************************************************************
 * Copyright (c) 2003, 2025 IBM Corporation and others.
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

import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.graph.CompoundDirectedGraph;
import org.eclipse.draw2d.graph.Subgraph;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.DirectEditRequest;

import org.eclipse.gef.examples.flow.figures.SubgraphFigure;
import org.eclipse.gef.examples.flow.model.Activity;
import org.eclipse.gef.examples.flow.model.StructuredActivity;
import org.eclipse.gef.examples.flow.policies.ActivityContainerEditPolicy;
import org.eclipse.gef.examples.flow.policies.ActivityContainerHighlightEditPolicy;
import org.eclipse.gef.examples.flow.policies.ActivityEditPolicy;
import org.eclipse.gef.examples.flow.policies.ActivityNodeEditPolicy;
import org.eclipse.gef.examples.flow.policies.StructuredActivityDirectEditPolicy;
import org.eclipse.gef.examples.flow.policies.StructuredActivityLayoutEditPolicy;

/**
 * @author hudsonr Created on Jun 30, 2003
 */
public abstract class StructuredActivityPart extends ActivityPart implements NodeEditPart {

	static final Insets PADDING = new Insets(8, 6, 8, 6);
	static final Insets INNER_PADDING = new Insets(0);

	protected void applyChildrenResults(CompoundDirectedGraph graph, Map<AbstractGraphicalEditPart, Object> map) {
		getChildren().forEach(part -> part.applyGraphResults(graph, map));
	}

	@Override
	protected void applyGraphResults(CompoundDirectedGraph graph, Map<AbstractGraphicalEditPart, Object> map) {
		applyOwnResults(graph, map);
		applyChildrenResults(graph, map);
	}

	protected void applyOwnResults(CompoundDirectedGraph graph, Map<AbstractGraphicalEditPart, Object> map) {
		super.applyGraphResults(graph, map);
	}

	/**
	 * @see org.eclipse.gef.examples.flow.parts.ActivityPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new ActivityNodeEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ActivityEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ActivityContainerHighlightEditPolicy());
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ActivityContainerEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new StructuredActivityLayoutEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new StructuredActivityDirectEditPolicy());
	}

	@Override
	public void contributeNodesToGraph(CompoundDirectedGraph graph, Subgraph s,
			Map<AbstractGraphicalEditPart, Object> map) {
		LayoutAnimator.getDefault().invalidate(getFigure());
		Subgraph me = new Subgraph(this, s);
		me.outgoingOffset = 5;
		me.incomingOffset = 5;
		if (getFigure() instanceof SubgraphFigure sgFig) {
			me.width = sgFig.getPreferredSize(me.width, me.height).width;
			int tagHeight = sgFig.getHeader().getPreferredSize().height;
			me.insets.top = tagHeight;
			me.insets.left = 0;
			me.insets.bottom = tagHeight;
		}
		me.innerPadding = INNER_PADDING;
		me.setPadding(PADDING);
		map.put(this, me);
		graph.nodes.add(me);
		getChildren().forEach(activity -> activity.contributeNodesToGraph(graph, me, map));
	}

	private boolean directEditHitTest(Point requestLoc) {
		IFigure header = ((SubgraphFigure) getFigure()).getHeader();
		header.translateToRelative(requestLoc);
		return header.containsPoint(requestLoc);
	}

	/**
	 * @see org.eclipse.gef.EditPart#performRequest(org.eclipse.gef.Request)
	 */
	@Override
	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
			if (request instanceof DirectEditRequest der && !directEditHitTest(der.getLocation().getCopy())) {
				return;
			}
			performDirectEdit();
		}
	}

	@Override
	int getAnchorOffset() {
		return -1;
	}

	@Override
	public IFigure getContentPane() {
		if (getFigure() instanceof SubgraphFigure sgFig) {
			return sgFig.getContents();
		}
		return getFigure();
	}

	@Override
	protected List<Activity> getModelChildren() {
		return getStructuredActivity().getChildren();
	}

	StructuredActivity getStructuredActivity() {
		return (StructuredActivity) getModel();
	}

	/**
	 * @see org.eclipse.gef.examples.flow.parts.ActivityPart#performDirectEdit()
	 */
	@Override
	protected void performDirectEdit() {
		if (manager == null) {
			Label l = ((Label) ((SubgraphFigure) getFigure()).getHeader());
			manager = new ActivityDirectEditManager(this, new ActivityCellEditorLocator(l), l);
		}
		manager.show();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		((Label) ((SubgraphFigure) getFigure()).getHeader()).setText(getModel().getName());
		((Label) ((SubgraphFigure) getFigure()).getFooter()).setText("/" + getModel().getName()); //$NON-NLS-1$
	}

}
