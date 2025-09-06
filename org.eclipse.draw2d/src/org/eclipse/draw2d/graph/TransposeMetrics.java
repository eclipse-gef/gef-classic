/*******************************************************************************
 * Copyright (c) 2005, 2025 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Transposer;

class TransposeMetrics extends GraphVisitor {

	Transposer t = new Transposer();

	@Override
	public void visit(DirectedGraph g) {
		if (g.getDirection() == PositionConstants.SOUTH) {
			return;
		}
		t.setEnabled(true);
		int temp;
		g.setDefaultPadding(t.t(g.getDefaultPadding()));
		for (Node node : g.nodes) {
			temp = node.width;
			node.width = node.height;
			node.height = temp;
			if (node.getPadding() != null) {
				node.setPadding(t.t(node.getPadding()));
			}
		}
	}

	@Override
	@SuppressWarnings("removal")
	public void revisit(DirectedGraph g) {
		if (g.getDirection() == PositionConstants.SOUTH) {
			return;
		}
		int temp;
		g.setDefaultPadding(t.t(g.getDefaultPadding()));
		for (Node node : g.nodes) {
			temp = node.width;
			node.width = node.height;
			node.height = temp;
			temp = node.y;
			node.y = node.x;
			node.x = temp;
			if (node.getPadding() != null) {
				node.setPadding(t.t(node.getPadding()));
			}
		}
		for (Edge edge : g.edges) {
			edge.start.transpose();
			edge.end.transpose();
			edge.getPoints().transpose();
			List<Node> bends = edge.vNodes;
			if (bends == null) {
				continue;
			}
			for (Node bend : bends) {
				VirtualNode vnode = (VirtualNode) bend;
				temp = vnode.y;
				vnode.y = vnode.x;
				vnode.x = temp;
				temp = vnode.width;
				vnode.width = vnode.height;
				vnode.height = temp;
			}
		}
		g.size.transpose();
	}

}
