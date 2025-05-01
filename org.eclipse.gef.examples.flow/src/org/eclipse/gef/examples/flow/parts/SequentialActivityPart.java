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
import org.eclipse.draw2d.graph.CompoundDirectedGraph;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import org.eclipse.gef.examples.flow.figures.SequentialActivityFigure;

/**
 * @author hudsonr Created on Jul 18, 2003
 */
public class SequentialActivityPart extends StructuredActivityPart {

	/**
	 * @see org.eclipse.gef.examples.flow.parts.StructuredActivityPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		IFigure activity = new SequentialActivityFigure();
		activity.addLayoutListener(LayoutAnimator.getDefault());
		return activity;
	}

	/**
	 * @see ActivityPart#contributeEdgesToGraph(org.eclipse.draw2d.graph.CompoundDirectedGraph,
	 *      java.util.Map)
	 */
	@Override
	public void contributeEdgesToGraph(CompoundDirectedGraph graph, Map<AbstractGraphicalEditPart, Object> map) {
		super.contributeEdgesToGraph(graph, map);
		Node prev = null;
		for (ActivityPart a : getChildren()) {
			Node node = (Node) map.get(a);
			if (prev != null) {
				Edge e = new Edge(prev, node);
				e.weight = 50;
				graph.edges.add(e);
			}
			prev = node;
		}
	}

	/**
	 * @see org.eclipse.gef.examples.flow.parts.StructuredActivityPart#getAnchorOffset()
	 */
	@Override
	int getAnchorOffset() {
		return 15;
	}

}
