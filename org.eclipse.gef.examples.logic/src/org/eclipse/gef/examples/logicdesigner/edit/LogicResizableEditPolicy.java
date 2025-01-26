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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.handles.ResizeHandle;
import org.eclipse.gef.tools.ResizeTracker;

import org.eclipse.gef.examples.logicdesigner.figures.AndGateFeedbackFigure;
import org.eclipse.gef.examples.logicdesigner.figures.CircuitFeedbackFigure;
import org.eclipse.gef.examples.logicdesigner.figures.GroundFeedbackFigure;
import org.eclipse.gef.examples.logicdesigner.figures.LEDFeedbackFigure;
import org.eclipse.gef.examples.logicdesigner.figures.LabelFeedbackFigure;
import org.eclipse.gef.examples.logicdesigner.figures.LiveOutputFeedbackFigure;
import org.eclipse.gef.examples.logicdesigner.figures.LogicColorConstants;
import org.eclipse.gef.examples.logicdesigner.figures.LogicFlowFeedbackFigure;
import org.eclipse.gef.examples.logicdesigner.figures.OrGateFeedbackFigure;
import org.eclipse.gef.examples.logicdesigner.figures.XOrGateFeedbackFigure;
import org.eclipse.gef.examples.logicdesigner.model.AndGate;
import org.eclipse.gef.examples.logicdesigner.model.Circuit;
import org.eclipse.gef.examples.logicdesigner.model.GroundOutput;
import org.eclipse.gef.examples.logicdesigner.model.LED;
import org.eclipse.gef.examples.logicdesigner.model.LiveOutput;
import org.eclipse.gef.examples.logicdesigner.model.LogicFlowContainer;
import org.eclipse.gef.examples.logicdesigner.model.LogicLabel;
import org.eclipse.gef.examples.logicdesigner.model.OrGate;
import org.eclipse.gef.examples.logicdesigner.model.SimpleOutput;
import org.eclipse.gef.examples.logicdesigner.model.XORGate;
import org.eclipse.gef.examples.logicdesigner.tools.LogicResizeTracker;

/**
 *
 */
public class LogicResizableEditPolicy extends ResizableEditPolicy {

	/**
	 * Creates the figure used for feedback.
	 *
	 * @return the new feedback figure
	 */
	@Override
	protected IFigure createDragSourceFeedbackFigure() {
		IFigure figure = createFigure(getHost(), null);

		figure.setBounds(getInitialFeedbackBounds());
		figure.validate();
		addFeedback(figure);
		return figure;
	}

	protected IFigure createFigure(GraphicalEditPart part, IFigure parent) {
		IFigure child = getCustomFeedbackFigure(part.getModel());

		if (parent != null) {
			parent.add(child);
		}

		Rectangle childBounds = part.getFigure().getBounds().getCopy();

		IFigure walker = part.getFigure().getParent();

		while (walker != ((GraphicalEditPart) part.getParent()).getFigure()) {
			walker.translateToParent(childBounds);
			walker = walker.getParent();
		}

		child.setBounds(childBounds);
		part.getChildren().forEach(ep -> createFigure(ep, child));
		return child;
	}

	private static IFigure getCustomFeedbackFigure(Object modelPart) {
		IFigure figure;

		if (modelPart instanceof Circuit) {
			figure = new CircuitFeedbackFigure();
		} else if (modelPart instanceof LogicFlowContainer) {
			figure = new LogicFlowFeedbackFigure();
		} else if (modelPart instanceof LogicLabel) {
			figure = new LabelFeedbackFigure();
		} else if (modelPart instanceof LED) {
			figure = new LEDFeedbackFigure();
		} else if (modelPart instanceof OrGate) {
			figure = new OrGateFeedbackFigure();
		} else if (modelPart instanceof XORGate) {
			figure = new XOrGateFeedbackFigure();
		} else if (modelPart instanceof GroundOutput) {
			figure = new GroundFeedbackFigure();
		} else if (modelPart instanceof LiveOutput) {
			figure = new LiveOutputFeedbackFigure();
		} else if (modelPart instanceof AndGate) {
			figure = new AndGateFeedbackFigure();
		} else {
			figure = new RectangleFigure();
			((RectangleFigure) figure).setXOR(true);
			((RectangleFigure) figure).setFill(true);
			figure.setBackgroundColor(LogicColorConstants.ghostFillColor);
			figure.setForegroundColor(ColorConstants.white);
		}

		return figure;
	}

	@Override
	public int getResizeDirections() {
		if (getHost().getModel() instanceof LED || getHost().getModel() instanceof SimpleOutput) {
			return PositionConstants.NONE;
		}
		if (getHost().getModel() instanceof LogicLabel) {
			return PositionConstants.EAST | PositionConstants.WEST;
		}
		return PositionConstants.NORTH | PositionConstants.SOUTH | PositionConstants.EAST | PositionConstants.WEST;
	}

	@Override
	protected List<Handle> createSelectionHandles() {
		List<Handle> list = new ArrayList<>();

		// Use the modified move handle
		list.add(new LogicElementMoveHandle(getHost()));

		if (getResizeDirections() != 0) {
			addResizeHandles(list);
		}

		return list;
	}

	private void addResizeHandles(List<Handle> handles) {
		GraphicalEditPart part = getHost();

		if ((getResizeDirections() & PositionConstants.NORTH) != 0) {
			handles.add(new ResizeHandle(part, PositionConstants.NORTH));
		}
		if ((getResizeDirections() & PositionConstants.SOUTH) != 0) {
			handles.add(new ResizeHandle(part, PositionConstants.SOUTH));
		}
		if ((getResizeDirections() & PositionConstants.EAST) != 0) {
			handles.add(new ResizeHandle(part, PositionConstants.EAST));
		}
		if ((getResizeDirections() & PositionConstants.WEST) != 0) {
			handles.add(new ResizeHandle(part, PositionConstants.WEST));
		}

		if ((getResizeDirections() & PositionConstants.NORTH_EAST) != 0) {
			handles.add(new ResizeHandle(part, PositionConstants.NORTH_EAST));
		}
		if ((getResizeDirections() & PositionConstants.NORTH_WEST) != 0) {
			handles.add(new ResizeHandle(part, PositionConstants.NORTH_WEST));
		}
		if ((getResizeDirections() & PositionConstants.SOUTH_EAST) != 0) {
			handles.add(new ResizeHandle(part, PositionConstants.SOUTH_EAST));
		}
		if ((getResizeDirections() & PositionConstants.SOUTH_WEST) != 0) {
			handles.add(new ResizeHandle(part, PositionConstants.SOUTH_WEST));
		}
	}

	/**
	 * Returns the layer used for displaying feedback.
	 *
	 * @return the feedback layer
	 */
	@Override
	protected IFigure getFeedbackLayer() {
		return getLayer(LayerConstants.SCALED_FEEDBACK_LAYER);
	}

	/**
	 * @see org.eclipse.gef.editpolicies.NonResizableEditPolicy#getInitialFeedbackBounds()
	 */
	@Override
	protected Rectangle getInitialFeedbackBounds() {
		return getHostFigure().getBounds();
	}

	/**
	 * Overwritten to ensure size constraints are respected.
	 */
	@Override
	protected ResizeTracker getResizeTracker(int direction) {
		return new LogicResizeTracker(getHost(), direction);
	}
}
