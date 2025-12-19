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

import java.util.HashMap;
import java.util.Map;

/**
 * Sorts nodes in a compound directed graph.
 *
 * @author Randy Hudson
 * @since 2.1.2
 */
class CompoundRankSorter extends RankSorter {

	static class RowEntry {
		double contribution;
		int count;

		void reset() {
			count = 0;
			contribution = 0;
		}
	}

	static record RowKey(int rank, Subgraph s) {

	}

	boolean init;
	Map<RowKey, RowEntry> map = new HashMap<>();

	void addRowEntry(Subgraph s, int row) {
		map.computeIfAbsent(new RowKey(row, s), k -> new RowEntry());
	}

	@Override
	protected void assignIncomingSortValues() {
		super.assignIncomingSortValues();
	}

	@Override
	protected void assignOutgoingSortValues() {
		super.assignOutgoingSortValues();
	}

	@Override
	void optimize(DirectedGraph g) {
		CompoundDirectedGraph graph = (CompoundDirectedGraph) g;
		graph.containment.forEach(graph::removeEdge);
		graph.containment.clear();
		new LocalOptimizer().visit(graph);
	}

	@Override
	double evaluateNodeOutgoing() {
		double result = super.evaluateNodeOutgoing();
		// result += Math.random() * rankSize * (1.0 - progress) / 3.0;
		if (progress > 0.2) {
			Subgraph s = node.getParent();
			double connectivity = mergeConnectivity(s, node.rank + 1, result, progress);
			result = connectivity;
		}
		return result;
	}

	@Override
	double evaluateNodeIncoming() {
		double result = super.evaluateNodeIncoming();
		// result += Math.random() * rankSize * (1.0 - progress) / 3.0;
		if (progress > 0.2) {
			Subgraph s = node.getParent();
			double connectivity = mergeConnectivity(s, node.rank - 1, result, progress);
			result = connectivity;
		}
		return result;
	}

	double mergeConnectivity(Subgraph s, int row, double result, double scaleFactor) {
		while (s != null && getRowEntry(s, row) == null) {
			s = s.getParent();
		}
		if (s != null) {
			RowEntry entry = getRowEntry(s, row);
			double connectivity = entry.contribution / entry.count;
			result = connectivity * 0.3 + (0.7) * result;
		}
		return result;
	}

	RowEntry getRowEntry(Subgraph s, int row) {
		return map.get(new RowKey(row, s));
	}

	@SuppressWarnings("removal")
	void copyConstraints(NestingTree tree) {
		if (tree.subgraph != null) {
			tree.sortValue = tree.subgraph.getRowConstraint();
		}
		for (Object child : tree.contents) {
			if (child instanceof Node n) {
				n.sortValue = n.getRowConstraint();
			} else {
				copyConstraints((NestingTree) child);
			}
		}
	}

	@Override
	@SuppressWarnings("removal")
	public void init(DirectedGraph g) {
		super.init(g);
		init = true;

		for (int row = 0; row < g.ranks.size(); row++) {
			Rank rank = g.ranks.getRank(row);

			NestingTree tree = NestingTree.buildNestingTreeForRank(rank);
			copyConstraints(tree);
			tree.recursiveSort(true);
			rank.clear();
			tree.repopulateRank(rank);

			for (Node n : rank) {
				Subgraph s = n.getParent();
				while (s != null) {
					addRowEntry(s, row);
					s = s.getParent();
				}
			}
		}
	}

	@Override
	protected void postSort() {
		super.postSort();
		if (init) {
			updateRank(rank);
		}
	}

	void updateRank(Rank rank) {
		for (Node n : rank) {
			Subgraph s = n.getParent();
			while (s != null) {
				getRowEntry(s, currentRow).reset();
				s = s.getParent();
			}
		}
		for (Node n : rank) {
			Subgraph s = n.getParent();
			while (s != null) {
				RowEntry entry = getRowEntry(s, currentRow);
				entry.count++;
				entry.contribution += n.index;
				s = s.getParent();
			}
		}
	}

}
