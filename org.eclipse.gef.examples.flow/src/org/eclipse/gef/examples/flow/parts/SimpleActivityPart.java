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

import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.graph.CompoundDirectedGraph;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.Subgraph;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import org.eclipse.gef.examples.flow.FlowImages;
import org.eclipse.gef.examples.flow.figures.SimpleActivityLabel;

/**
 * @author hudsonr Created on Jul 17, 2003
 */
public class SimpleActivityPart extends ActivityPart {

	@Override
	public void contributeNodesToGraph(CompoundDirectedGraph graph, Subgraph s,
			Map<AbstractGraphicalEditPart, Object> map) {
		LayoutAnimator.getDefault().invalidate(getFigure());
		Node n = new Node(this, s);
		n.outgoingOffset = getAnchorOffset();
		n.incomingOffset = getAnchorOffset();
		n.width = getFigure().getPreferredSize().width;
		n.height = getFigure().getPreferredSize().height;
		n.setPadding(new Insets(10, 8, 10, 12));
		map.put(this, n);
		graph.nodes.add(n);
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		Label l = new SimpleActivityLabel();
		l.addLayoutListener(LayoutAnimator.getDefault());
		l.setLabelAlignment(PositionConstants.LEFT);
		l.setIcon(FlowImages.GEAR);
		return l;
	}

	@Override
	int getAnchorOffset() {
		return 9;
	}

	@Override
	public Label getFigure() {
		return (Label) super.getFigure();
	}

	@Override
	protected void performDirectEdit() {
		if (manager == null) {
			Label l = getFigure();
			manager = new ActivityDirectEditManager(this, new ActivityCellEditorLocator(l), l);
		}
		manager.show();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		getFigure().setText(getModel().getName());
	}

}
