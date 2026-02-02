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
package org.eclipse.draw2d.graph;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Randy Hudson
 */
class RouteEdges extends GraphVisitor {

	/**
	 * @see GraphVisitor#visit(DirectedGraph)
	 */
	@Override
	@SuppressWarnings("removal")
	public void revisit(DirectedGraph g) {
		for (Edge edge : g.edges) {
			edge.start = new Point(edge.getSourceOffset() + edge.source.x, edge.source.y + edge.source.height);
			if (edge.source instanceof SubgraphBoundary boundary) {
				if (boundary.getParent().head == boundary) {
					edge.start.y = boundary.getParent().y + boundary.getParent().insets.top;
				}
			}
			edge.end = new Point(edge.getTargetOffset() + edge.target.x, edge.target.y);

			if (edge.vNodes != null) {
				routeLongEdge(edge, g);
			} else {
				PointList list = new PointList();
				list.addPoint(edge.start);
				list.addPoint(edge.end);
				edge.setPoints(list);
			}
		}
	}

	@SuppressWarnings("removal")
	static void routeLongEdge(Edge edge, DirectedGraph g) {
		ShortestPathRouter router = new ShortestPathRouter();
		Path path = new Path(edge.start, edge.end);
		router.addPath(path);
		Rectangle o;
		Insets padding;
		for (Node node : edge.vNodes) {
			Node neighbor;
			if (node.left != null) {
				neighbor = node.left;
				o = new Rectangle(neighbor.x, neighbor.y, neighbor.width, neighbor.height);
				padding = g.getPadding(neighbor);
				o.width += padding.right + padding.left;
				o.width += (edge.getPadding() * 2);
				o.x -= (padding.left + edge.getPadding());
				o.union(o.getLocation().translate(-100000, 2));
				router.addObstacle(o);
			}
			if (node.right != null) {
				neighbor = node.right;
				o = new Rectangle(neighbor.x, neighbor.y, neighbor.width, neighbor.height);
				padding = g.getPadding(neighbor);
				o.width += padding.right + padding.left;
				o.width += (edge.getPadding() * 2);
				o.x -= (padding.left + edge.getPadding());
				o.union(o.getLocation().translate(100000, 2));
				router.addObstacle(o);
			}
		}
		router.setSpacing(0);
		router.solve();
		edge.setPoints(path.getPoints());
	}

}
