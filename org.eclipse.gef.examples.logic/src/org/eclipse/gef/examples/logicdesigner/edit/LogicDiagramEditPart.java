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
package org.eclipse.gef.examples.logicdesigner.edit;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleEvent;

import org.eclipse.draw2d.Animation;
import org.eclipse.draw2d.AutomaticRouter;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.XYLayout;

import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.tools.DeselectAllTracker;
import org.eclipse.gef.tools.MarqueeDragTracker;

import org.eclipse.gef.examples.logicdesigner.LogicMessages;
import org.eclipse.gef.examples.logicdesigner.model.LogicDiagram;

/**
 * Holds all other LogicEditParts under this. It is activated by
 * LogicEditorPart, to hold the entire model. It is sort of a blank board where
 * all other EditParts get added.
 */
public class LogicDiagramEditPart extends LogicContainerEditPart implements LayerConstants {

	@Override
	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {
			@Override
			public void getName(AccessibleEvent e) {
				e.result = LogicMessages.LogicDiagram_LabelText;
			}
		};
	}

	/**
	 * Installs EditPolicies specific to this.
	 */
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();

		installEditPolicy(EditPolicy.NODE_ROLE, null);
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, null);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new LogicXYLayoutEditPolicy((XYLayout) getContentPane().getLayoutManager()));

		installEditPolicy("Snap Feedback", new SnapFeedbackPolicy()); //$NON-NLS-1$
	}

	/**
	 * Returns a Figure to represent this.
	 *
	 * @return Figure.
	 */
	@Override
	protected IFigure createFigure() {
		Figure f = new FreeformLayer();
		// f.setBorder(new GroupBoxBorder("Diagram"));
		f.setLayoutManager(new FreeformLayout());
		f.setBorder(new MarginBorder(5));
		return f;
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public <T> T getAdapter(final Class<T> adapter) {
		if (adapter == SnapToHelper.class) {
			List<SnapToHelper> snapStrategies = new ArrayList<>();
			Boolean val = (Boolean) getViewer().getProperty(RulerProvider.PROPERTY_RULER_VISIBILITY);
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGuides(this));
			}
			val = (Boolean) getViewer().getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED);
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGeometry(this));
			}
			val = (Boolean) getViewer().getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGrid(this));
			}

			if (snapStrategies.isEmpty()) {
				return null;
			}
			if (snapStrategies.size() == 1) {
				return adapter.cast(snapStrategies.get(0));
			}

			return adapter.cast(new CompoundSnapToHelper(snapStrategies.toArray(new SnapToHelper[0])));
		}
		return super.getAdapter(adapter);
	}

	@Override
	public DragTracker getDragTracker(Request req) {
		if (req instanceof SelectionRequest selReq && selReq.getLastButtonPressed() == 3) {
			return new DeselectAllTracker(this);
		}
		return new MarqueeDragTracker();
	}

	/**
	 * Returns <code>NULL</code> as it does not hold any connections.
	 *
	 * @return ConnectionAnchor
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart editPart) {
		return null;
	}

	/**
	 * Returns <code>NULL</code> as it does not hold any connections.
	 *
	 * @return ConnectionAnchor
	 */
	@SuppressWarnings("static-method")
	public ConnectionAnchor getSourceConnectionAnchor(int x, int y) {
		return null;
	}

	/**
	 * Returns <code>NULL</code> as it does not hold any connections.
	 *
	 * @return ConnectionAnchor
	 */
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart editPart) {
		return null;
	}

	/**
	 * Returns <code>NULL</code> as it does not hold any connections.
	 *
	 * @return ConnectionAnchor
	 */
	@SuppressWarnings("static-method")
	public ConnectionAnchor getTargetConnectionAnchor(int x, int y) {
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (LogicDiagram.ID_ROUTER.equals(evt.getPropertyName())) {
			refreshVisuals();
		} else {
			super.propertyChange(evt);
		}
	}

	@Override
	protected void refreshVisuals() {
		Animation.markBegin();
		ConnectionLayer cLayer = (ConnectionLayer) getLayer(CONNECTION_LAYER);
		if ((getViewer().getControl().getStyle() & SWT.MIRRORED) == 0) {
			cLayer.setAntialias(SWT.ON);
		}

		if (getModel().getConnectionRouter().equals(LogicDiagram.ROUTER_MANUAL)) {
			AutomaticRouter router = new FanRouter();
			router.setNextRouter(new BendpointConnectionRouter());
			cLayer.setConnectionRouter(router);
		} else if (getModel().getConnectionRouter().equals(LogicDiagram.ROUTER_MANHATTAN)) {
			cLayer.setConnectionRouter(new ManhattanConnectionRouter());
		} else {
			cLayer.setConnectionRouter(new ShortestPathConnectionRouter(getFigure()));
		}
		Animation.run(400);
	}

}
