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
package org.eclipse.draw2d.graph;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * This class takes a DirectedGraph with an optimal rank assignment and a
 * spanning tree, and populates the ranks of the DirectedGraph. Virtual nodes
 * are inserted for edges that span 1 or more ranks.
 * <P>
 * Ranks are populated using a pre-order depth-first traversal of the spanning
 * tree. For each node, all edges requiring virtual nodes are added to the
 * ranks.
 *
 * @author Randy Hudson
 * @since 2.1.2
 */
class PopulateRanks extends GraphVisitor {

	private final Deque<RevertableChange> changes = new ArrayDeque<>();

	/**
	 * @see GraphVisitor#visit(DirectedGraph)
	 */
	@Override
	@SuppressWarnings("removal")
	public void visit(DirectedGraph g) {
		if (g.forestRoot != null) {
			for (int i = g.forestRoot.outgoing.size() - 1; i >= 0; i--) {
				g.removeEdge(g.forestRoot.outgoing.get(i));
			}
			g.removeNode(g.forestRoot);
		}
		g.ranks = new RankList();
		for (Node node : g.nodes) {
			g.ranks.getRank(node.rank).add(node);
		}
		// The constructor of VirtualNodeCreation may add additional nodes to the graph.
		// If we work on the same list of nodes, this will cause a
		// ConcurrentModificationException. Work on a copy of the node list so that we
		// don't create virtual nodes of virtual nodes.
		for (Node node : List.copyOf(g.nodes)) {
			for (int j = 0; j < node.outgoing.size();) {
				Edge e = node.outgoing.get(j);
				if (e.getLength() > 1) {
					changes.push(new VirtualNodeCreation(e, g));
				} else {
					j++;
				}
			}
		}
	}

	/**
	 * @see GraphVisitor#revisit(DirectedGraph)
	 */
	@Override
	@SuppressWarnings("removal")
	public void revisit(DirectedGraph g) {
		for (Rank rank : g.ranks) {
			Node prev = null;
			for (Node cur : rank) {
				cur.left = prev;
				if (prev != null) {
					prev.right = cur;
				}
				prev = cur;
			}
		}
		changes.forEach(RevertableChange::revert);
	}

}
