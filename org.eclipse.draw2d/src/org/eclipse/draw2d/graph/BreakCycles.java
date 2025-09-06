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

import java.util.ArrayList;
import java.util.List;

/**
 * This visitor eliminates cycles in the graph using a "greedy" heuristic. Nodes
 * which are sources and sinks are marked and placed in a source and sink list,
 * leaving only nodes involved in cycles. A remaining node with the highest
 * (outgoing-incoming) edges score is then chosen greedily as if it were a
 * source. The process is repeated until all nodes have been marked and placed
 * in a list. The lists are then concatenated, and any edges which go backwards
 * in this list will be inverted during the layout procedure.
 *
 * @author Daniel Lee
 * @since 2.1.2
 */
class BreakCycles extends GraphVisitor {

	// Used in identifying cycles and in cycle removal.
	// Flag field indicates "presence". If true, the node has been removed from
	// the list.
	NodeList graphNodes = new NodeList();

	private boolean allNodesFlagged() {
		return graphNodes.stream().noneMatch(n -> !n.flag);
	}

	private void breakCycles(DirectedGraph g) {
		initializeDegrees(g);
		greedyCycleRemove(g);
		invertEdges(g);
	}

	/*
	 * Returns true if g contains cycles, false otherwise
	 */
	private boolean containsCycles(DirectedGraph g) {
		List<Node> noLefts = new ArrayList<>();
		// Identify all initial nodes for removal
		for (Node node : graphNodes) {
			if (getIncomingCount(node) == 0) {
				sortedInsert(noLefts, node);
			}
		}

		while (!noLefts.isEmpty()) {
			Node node = noLefts.remove(noLefts.size() - 1);
			node.flag = true;
			for (Edge e : node.outgoing) {
				Node right = e.target;
				setIncomingCount(right, getIncomingCount(right) - 1);
				if (getIncomingCount(right) == 0) {
					sortedInsert(noLefts, right);
				}
			}
		}

		return !allNodesFlagged();
	}

	/*
	 * Returns the node in graphNodes with the largest (outgoing edge count -
	 * incoming edge count) value
	 */
	private Node findNodeWithMaxDegree() {
		int max = Integer.MIN_VALUE;
		Node maxNode = null;

		for (Node node : graphNodes) {
			if (getDegree(node) >= max && !node.flag) {
				max = getDegree(node);
				maxNode = node;
			}
		}
		return maxNode;
	}

	private static int getDegree(Node n) {
		return n.workingInts[3];
	}

	private static int getIncomingCount(Node n) {
		return n.workingInts[0];
	}

	private static int getInDegree(Node n) {
		return n.workingInts[1];
	}

	private static int getOrderIndex(Node n) {
		return n.workingInts[0];
	}

	private static int getOutDegree(Node n) {
		return n.workingInts[2];
	}

	private void greedyCycleRemove(DirectedGraph g) {
		NodeList sL = new NodeList();
		NodeList sR = new NodeList();

		do {
			// Add all sinks and isolated nodes to sR
			boolean hasSink;
			do {
				hasSink = false;
				for (Node node : graphNodes) {
					if (getOutDegree(node) == 0 && !node.flag) {
						hasSink = true;
						node.flag = true;
						updateIncoming(node);
						sR.add(node);
						break;
					}
				}
			} while (hasSink);

			// Add all sources to sL
			boolean hasSource;
			do {
				hasSource = false;
				for (Node node : graphNodes) {
					if (getInDegree(node) == 0 && !node.flag) {
						hasSource = true;
						node.flag = true;
						updateOutgoing(node);
						sL.add(node);
						break;
					}
				}
			} while (hasSource);

			// When all sinks and sources are removed, choose a node with the
			// maximum degree (outDegree - inDegree) and add it to sL
			Node max = findNodeWithMaxDegree();
			if (max != null) {
				sL.add(max);
				max.flag = true;
				updateIncoming(max);
				updateOutgoing(max);
			}
		} while (!allNodesFlagged());

		// Assign order indexes
		int orderIndex = 0;
		for (Node element : sL) {
			setOrderIndex(element, orderIndex);
			orderIndex++;
		}
		for (int i = sR.size() - 1; i >= 0; i--) {
			setOrderIndex(sR.get(i), orderIndex);
			orderIndex++;
		}
	}

	private void initializeDegrees(DirectedGraph g) {
		graphNodes.resetFlags();
		for (int i = 0; i < g.nodes.size(); i++) {
			Node n = graphNodes.get(i);
			setInDegree(n, n.incoming.size());
			setOutDegree(n, n.outgoing.size());
			setDegree(n, n.outgoing.size() - n.incoming.size());
		}
	}

	@SuppressWarnings("removal")
	private static void invertEdges(DirectedGraph g) {
		for (Edge e : g.edges) {
			if (getOrderIndex(e.source) > getOrderIndex(e.target)) {
				e.invert();
				e.isFeedback = true;
			}
		}
	}

	private static void setDegree(Node n, int deg) {
		n.workingInts[3] = deg;
	}

	private static void setIncomingCount(Node n, int count) {
		n.workingInts[0] = count;
	}

	private static void setInDegree(Node n, int deg) {
		n.workingInts[1] = deg;
	}

	private static void setOutDegree(Node n, int deg) {
		n.workingInts[2] = deg;
	}

	private static void setOrderIndex(Node n, int index) {
		n.workingInts[0] = index;
	}

	private static void sortedInsert(List<Node> list, Node node) {
		int insert = 0;
		while (insert < list.size() && (list.get(insert)).sortValue > node.sortValue) {
			insert++;
		}
		list.add(insert, node);
	}

	/*
	 * Called after removal of n. Updates the degree values of n's incoming nodes.
	 */
	private static void updateIncoming(Node n) {
		for (Edge e : n.incoming) {
			Node in = e.source;
			if (!in.flag) {
				setOutDegree(in, getOutDegree(in) - 1);
				setDegree(in, getOutDegree(in) - getInDegree(in));
			}
		}
	}

	/*
	 * Called after removal of n. Updates the degree values of n's outgoing nodes.
	 */
	private static void updateOutgoing(Node n) {
		for (Edge e : n.outgoing) {
			Node out = e.target;
			if (!out.flag) {
				setInDegree(out, getInDegree(out) - 1);
				setDegree(out, getOutDegree(out) - getInDegree(out));
			}
		}
	}

	@Override
	public void revisit(DirectedGraph g) {
		g.edges.stream().filter(Edge::isFeedback).forEach(Edge::invert);
	}

	/**
	 * @see GraphVisitor#visit(org.eclipse.draw2d.graph.DirectedGraph)
	 */
	@Override
	public void visit(DirectedGraph g) {
		// put all nodes in list, initialize index
		graphNodes.resetFlags();
		for (Node n : g.nodes) {
			setIncomingCount(n, n.incoming.size());
			graphNodes.add(n);
		}
		if (containsCycles(g)) {
			breakCycles(g);
		}
	}

}
