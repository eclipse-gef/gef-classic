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

/**
 * This graph visitor examines all adjacent pairs of nodes and determines if
 * swapping the two nodes provides improved graph aesthetics.
 *
 * @author Daniel Lee
 * @since 2.1.2
 */
class LocalOptimizer extends GraphVisitor {

	@SuppressWarnings("static-method")
	boolean shouldSwap(Node current, Node next) {
		if (GraphUtilities.isConstrained(current, next)) {
			return false;
		}
		int crossCount = 0;
		int invertedCrossCount = 0;

		int rank = current.rank - 1;

		for (Edge currentEdge : current.incoming) {
			int iCurrent = currentEdge.getIndexForRank(rank);
			for (Edge nextEdge : next.incoming) {
				int iNext = nextEdge.getIndexForRank(rank);
				if (iNext < iCurrent) {
					crossCount++;
				} else if (iNext > iCurrent) {
					invertedCrossCount++;
				} else {
					// edges go to the same location
					int offsetDiff = nextEdge.getSourceOffset() - currentEdge.getSourceOffset();
					if (offsetDiff < 0) {
						crossCount++;
					} else if (offsetDiff > 0) {
						invertedCrossCount++;
					}
				}
			}
		}

		rank = current.rank + 1;

		for (Edge currentEdge : current.outgoing) {
			int iCurrent = currentEdge.getIndexForRank(rank);
			for (Edge nextEdge : next.outgoing) {
				int iNext = nextEdge.getIndexForRank(rank);
				if (iNext < iCurrent) {
					crossCount++;
				} else if (iNext > iCurrent) {
					invertedCrossCount++;
				} else {
					// edges go to the same location
					int offsetDiff = nextEdge.getTargetOffset() - currentEdge.getTargetOffset();
					if (offsetDiff < 0) {
						crossCount++;
					} else if (offsetDiff > 0) {
						invertedCrossCount++;
					}
				}
			}
		}

		return (invertedCrossCount < crossCount);
	}

	private static void swapNodes(Node current, Node next, Rank rank) {
		int index = rank.indexOf(current);
		rank.set(index + 1, current);
		rank.set(index, next);
		index = current.index;
		current.index = next.index;
		next.index = index;
	}

	/**
	 * @see GraphVisitor#visit(org.eclipse.draw2d.graph.DirectedGraph)
	 */
	@Override
	@SuppressWarnings("removal")
	public void visit(DirectedGraph g) {
		boolean flag;
		do {
			flag = false;
			for (Rank rank : g.ranks) {
				for (int n = 0; n < rank.count() - 1; n++) {
					Node currentNode = rank.get(n);
					Node nextNode = rank.get(n + 1);
					if (shouldSwap(currentNode, nextNode)) {
						swapNodes(currentNode, nextNode, rank);
						flag = true;
						n = Math.max(0, n - 2);
					}
				}
			}
		} while (flag);
	}

}
