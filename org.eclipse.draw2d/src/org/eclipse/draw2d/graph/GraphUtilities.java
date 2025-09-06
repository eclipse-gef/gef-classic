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
 * Some utility methods for graphs.
 *
 * @author Eric Bordeau
 * @since 2.1.2
 */
class GraphUtilities {

	static Subgraph getCommonAncestor(Node left, Node right) {
		Subgraph parent;
		if (right instanceof Subgraph s) {
			parent = s;
		} else {
			parent = right.getParent();
		}
		while (parent != null) {
			if (parent.isNested(left)) {
				return parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

	/**
	 * Returns <code>true</code> if the given graph contains at least one cycle.
	 *
	 * @param graph the graph to test
	 * @return whether the graph is cyclic
	 */
	public static boolean isCyclic(DirectedGraph graph) {
		return isCyclic(new NodeList(graph.nodes));
	}

	/**
	 * Recursively removes leaf nodes from the list until there are no nodes
	 * remaining (acyclic) or there are no leaf nodes but the list is not empty
	 * (cyclic), then returns the result.
	 *
	 * @param nodes the list of nodes to test
	 * @return whether the graph is cyclic
	 */
	public static boolean isCyclic(NodeList nodes) {
		if (nodes.isEmpty()) {
			return false;
		}
		int size = nodes.size();
		// remove all the leaf nodes from the graph
		for (Node node : nodes) {
			if (node.outgoing == null || node.outgoing.isEmpty()) {
				// this is a leaf node
				nodes.remove(node);
				node.incoming.forEach(e -> e.source.outgoing.remove(e));
			}
		}
		// if no nodes were removed, that means there are no leaf nodes and the
		// graph is cyclic
		if (nodes.size() == size) {
			return true;
		}
		// leaf nodes were removed, so recursively call this method with the new
		// list
		return isCyclic(nodes);
	}

	/**
	 * Counts the number of edge crossings in a DirectedGraph
	 *
	 * @param graph the graph whose crossed edges are counted
	 * @return the number of edge crossings in the graph
	 */
	@SuppressWarnings("removal")
	public static int numberOfCrossingsInGraph(DirectedGraph graph) {
		int crossings = 0;
		for (Rank rank : graph.ranks) {
			crossings += numberOfCrossingsInRank(rank);
		}
		return crossings;
	}

	/**
	 * Counts the number of edge crossings in a Rank
	 *
	 * @param rank the rank whose crossed edges are counted
	 * @return the number of edge crossings in the rank
	 */
	public static int numberOfCrossingsInRank(Rank rank) {
		int crossings = 0;
		for (int i = 0; i < rank.size() - 1; i++) {
			Node currentNode = rank.get(i);
			for (int j = i + 1; j < rank.size(); j++) {
				Node nextNode = rank.get(j);
				for (Edge currentEdge : currentNode.outgoing) {
					for (Edge nextEdge : nextNode.outgoing) {
						if (nextEdge.getIndexForRank(currentNode.rank + 1) < currentEdge
								.getIndexForRank(currentNode.rank + 1)) {
							crossings++;
						}
					}
				}
			}
		}
		return crossings;
	}

	private static NodeList search(Node node, NodeList list) {
		if (node.flag) {
			return list;
		}
		node.flag = true;
		list.add(node);
		node.outgoing.forEach(e -> search(e.target, list));
		return list;
	}

	/**
	 * Returns <code>true</code> if adding an edge between the 2 given nodes will
	 * introduce a cycle in the containing graph.
	 *
	 * @param source the potential source node
	 * @param target the potential target node
	 * @return whether an edge between the 2 given nodes will introduce a cycle
	 */
	public static boolean willCauseCycle(Node source, Node target) {
		NodeList nodes = search(target, new NodeList());
		nodes.resetFlags();
		return nodes.contains(source);
	}

	static boolean isConstrained(Node left, Node right) {
		Subgraph common = left.getParent();
		while (common != null && !common.isNested(right)) {
			left = left.getParent();
			common = left.getParent();
		}
		while (right.getParent() != common) {
			right = right.getParent();
		}
		return (left.getRowConstraint() != -1 && right.getRowConstraint() != -1)
				&& left.getRowConstraint() != right.getRowConstraint();
	}

}
