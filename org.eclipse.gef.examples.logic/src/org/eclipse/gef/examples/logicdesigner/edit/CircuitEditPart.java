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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.IScrollableFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.AccessibleAnchorProvider;
import org.eclipse.gef.AutoexposeHelper;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.ExposeHelper;
import org.eclipse.gef.MouseWheelHelper;
import org.eclipse.gef.editparts.IScrollableEditPart;
import org.eclipse.gef.editparts.ViewportAutoexposeHelper;
import org.eclipse.gef.editparts.ViewportExposeHelper;
import org.eclipse.gef.editparts.ViewportMouseWheelHelper;
import org.eclipse.gef.editpolicies.ScrollableSelectionFeedbackEditPolicy;

import org.eclipse.gef.examples.logicdesigner.figures.CircuitFigure;

/**
 * Holds a circuit, which is a container capable of holding other
 * LogicEditParts.
 */
public class CircuitEditPart extends LogicContainerEditPart implements IScrollableEditPart {

	private static final String SCROLLABLE_SELECTION_FEEDBACK = "SCROLLABLE_SELECTION_FEEDBACK"; //$NON-NLS-1$

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new LogicXYLayoutEditPolicy((XYLayout) getContentPane().getLayoutManager()));
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ContainerHighlightEditPolicy());
		installEditPolicy(SCROLLABLE_SELECTION_FEEDBACK, new ScrollableSelectionFeedbackEditPolicy());
	}

	/**
	 * Creates a new Circuit Figure and returns it.
	 *
	 * @return Figure representing the circuit.
	 */
	@Override
	protected IFigure createFigure() {
		return new CircuitFigure();
	}

	@Override
	public <T> T getAdapter(final Class<T> key) {
		if (key == AutoexposeHelper.class) {
			return key.cast(new ViewportAutoexposeHelper(this));
		}
		if (key == ExposeHelper.class) {
			return key.cast(new ViewportExposeHelper(this));
		}
		if (key == AccessibleAnchorProvider.class) {
			return key.cast(new DefaultAccessibleAnchorProvider() {
				@Override
				public List<Point> getSourceAnchorLocations() {
					List<Point> list = new ArrayList<>();
					List<ConnectionAnchor> sourceAnchors = getNodeFigure().getSourceConnectionAnchors();
					List<ConnectionAnchor> targetAnchors = getNodeFigure().getTargetConnectionAnchors();
					for (int i = 0; i < sourceAnchors.size(); i++) {
						ConnectionAnchor sourceAnchor = sourceAnchors.get(i);
						ConnectionAnchor targetAnchor = targetAnchors.get(i);
						list.add(new Rectangle(sourceAnchor.getReferencePoint(), targetAnchor.getReferencePoint())
								.getCenter());
					}
					return list;
				}

				@Override
				public List<Point> getTargetAnchorLocations() {
					return getSourceAnchorLocations();
				}
			});
		}
		if (key == MouseWheelHelper.class) {
			return key.cast(new ViewportMouseWheelHelper(this));
		}
		return super.getAdapter(key);
	}

	/**
	 * Returns the Figure of this as a CircuitFigure.
	 *
	 * @return CircuitFigure of this.
	 */
	protected CircuitFigure getCircuitBoardFigure() {
		return (CircuitFigure) getFigure();
	}

	@Override
	public IFigure getContentPane() {
		return getCircuitBoardFigure().getContentsPane();
	}

	@Override
	public IScrollableFigure getScrollableFigure() {
		return (IScrollableFigure) getFigure();
	}

}
