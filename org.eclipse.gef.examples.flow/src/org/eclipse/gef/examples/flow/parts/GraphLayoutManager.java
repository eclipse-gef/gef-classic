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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.CompoundDirectedGraph;
import org.eclipse.draw2d.graph.CompoundDirectedGraphLayout;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

class GraphLayoutManager extends AbstractLayout {

	private final ActivityDiagramPart diagram;

	GraphLayoutManager(ActivityDiagramPart diagram) {
		this.diagram = diagram;
	}

	@Override
	protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint) {
		container.validate();
		Rectangle result = new Rectangle().setLocation(container.getClientArea().getLocation());
		container.getChildren().forEach(child -> result.union(child.getBounds()));
		result.resize(container.getInsets().getWidth(), container.getInsets().getHeight());
		return result.getSize();
	}

	@Override
	public void layout(IFigure container) {
		if (LayoutAnimator.getDefault().layout(container)) {
			return;
		}

		CompoundDirectedGraph graph = new CompoundDirectedGraph();
		Map<AbstractGraphicalEditPart, Object> partsToNodes = new HashMap<>();
		diagram.contributeNodesToGraph(graph, null, partsToNodes);
		diagram.contributeEdgesToGraph(graph, partsToNodes);
		new CompoundDirectedGraphLayout().visit(graph);
		diagram.applyGraphResults(graph, partsToNodes);
	}

}
