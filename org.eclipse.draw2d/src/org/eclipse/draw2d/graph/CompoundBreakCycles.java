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
 * This visitor eliminates cycles in the graph via a modified implementation of
 * the greedy cycle removal algorithm for directed graphs. The algorithm has
 * been modified to handle the presence of Subgraphs and compound cycles which
 * may result. This algorithm determines a set of edges which can be inverted
 * and result in a graph without compound cycles.
 *
 * @author Daniel Lee
 * @author Randy Hudson
 * @since 2.1.2
 */
class CompoundBreakCycles extends GraphVisitor {

	/*
	 * Caches all nodes in the graph. Used in identifying cycles and in cycle
	 * removal. Flag field indicates "presence". If true, the node has been removed
	 * from the list.
	 */
	private NodeList graphNodes;
	private final NodeList sL = new NodeList();

	private static boolean allFlagged(NodeList nodes) {
		return nodes.stream().allMatch(n -> n.flag);
	}

	private static int buildNestingTreeIndices(NodeList nodes, int base) {
		for (Node node : nodes) {
			if (node instanceof Subgraph s) {
				s.nestingTreeMin = base;
				base = buildNestingTreeIndices(s.members, base);
			}
			node.nestingIndex = base;
			base++;
		}
		return base++;
	}

	private static boolean canBeRemoved(Node n) {
		return !n.flag && getChildCount(n) == 0;
	}

	private static boolean changeInDegree(Node n, int delta) {
		return (n.workingInts[1] += delta) == 0;
	}

	private static boolean changeOutDegree(Node n, int delta) {
		return (n.workingInts[2] += delta) == 0;
	}

	/*
	 * Execution of the modified greedy cycle removal algorithm.
	 */
	private void cycleRemove(NodeList children) {
		NodeList sR = new NodeList();
		do {
			findSinks(children, sR);
			findSources(children);

			// all sinks and sources added, find node with highest
			// outDegree - inDegree
			Node max = findNodeWithMaxDegree(children);
			if (max != null) {
				for (Node child : children) {
					if (child.flag) {
						continue;
					}
					if (child == max) {
						restoreSinks(max, sR);
					} else {
						restoreSources(child);
					}
				}
				remove(max);
			}
		} while (!allFlagged(children));
		while (!sR.isEmpty()) {
			sL.add(sR.remove(sR.size() - 1));
		}
	}

	private static void findInitialSinks(NodeList children, NodeList sinks) {
		for (Node node : children) {
			if (node.flag) {
				continue;
			}
			if (isSink(node) && canBeRemoved(node)) {
				sinks.add(node);
				node.flag = true;
			}
			if (node instanceof Subgraph s) {
				findInitialSinks(s.members, sinks);
			}
		}
	}

	private static void findInitialSources(NodeList children, NodeList sources) {
		for (Node node : children) {
			if (isSource(node) && canBeRemoved(node)) {
				sources.add(node);
				node.flag = true;
			}
			if (node instanceof Subgraph s) {
				findInitialSources(s.members, sources);
			}
		}
	}

	private static Node findNodeWithMaxDegree(NodeList nodes) {
		int max = Integer.MIN_VALUE;
		Node maxNode = null;

		for (Node node : nodes) {
			if (node.flag) {
				continue;
			}
			int degree = getNestedOutDegree(node) - getNestedInDegree(node);
			if (degree >= max && !node.flag) {
				max = degree;
				maxNode = node;
			}
		}
		return maxNode;
	}

	/*
	 * Finds all sinks in graphNodes and adds them to the passed NodeList
	 */
	private static void findSinks(NodeList children, NodeList rightList) {
		NodeList sinks = new NodeList();
		findInitialSinks(children, sinks);
		while (!sinks.isEmpty()) {
			Node sink = sinks.get(sinks.size() - 1);
			rightList.add(sink);
			sinks.remove(sink);
			removeSink(sink, sinks);

			// Check to see if the removal has made the parent node a sink
			if (sink.getParent() != null) {
				Node parent = sink.getParent();
				setChildCount(parent, getChildCount(parent) - 1);
				if (isSink(parent) && canBeRemoved(parent)) {
					sinks.add(parent);
					parent.flag = true;
				}
			}
		}
	}

	/*
	 * Finds all sources in graphNodes and adds them to the sL NodeList.
	 */
	private void findSources(NodeList children) {
		NodeList sources = new NodeList();
		findInitialSources(children, sources);
		while (!sources.isEmpty()) {
			Node source = sources.get(sources.size() - 1);
			sL.add(source);
			sources.remove(source);
			removeSource(source, sources);

			// Check to see if the removal has made the parent node a source
			if (source.getParent() != null) {
				Node parent = source.getParent();
				setChildCount(parent, getChildCount(parent) - 1);
				if (isSource(parent) && canBeRemoved(parent)) {
					sources.add(parent);
					parent.flag = true;
				}
			}
		}
	}

	private static int getChildCount(Node n) {
		return n.workingInts[3];
	}

	private static int getInDegree(Node n) {
		return n.workingInts[1];
	}

	private static int getNestedInDegree(Node n) {
		int result = getInDegree(n);
		if (n instanceof Subgraph s) {
			for (Node node : s.members) {
				if (!node.flag) {
					result += getInDegree(node);
				}
			}
		}
		return result;
	}

	private static int getNestedOutDegree(Node n) {
		int result = getOutDegree(n);
		if (n instanceof Subgraph s) {
			for (Node node : s.members) {
				if (!node.flag) {
					result += getOutDegree(node);
				}
			}
		}
		return result;
	}

	private static int getOrderIndex(Node n) {
		return n.workingInts[0];
	}

	private static int getOutDegree(Node n) {
		return n.workingInts[2];
	}

	private static void initializeDegrees(DirectedGraph g) {
		g.nodes.resetFlags();
		g.edges.resetFlags(false);
		for (Node n : g.nodes) {
			setInDegree(n, n.incoming.size());
			setOutDegree(n, n.outgoing.size());
			if (n instanceof Subgraph s) {
				setChildCount(n, s.members.size());
			} else {
				setChildCount(n, 0);
			}
		}
	}

	@SuppressWarnings("removal")
	private void invertEdges(DirectedGraph g) {
		// Assign order indices
		int orderIndex = 0;
		for (Node element : sL) {
			setOrderIndex(element, orderIndex);
			orderIndex++;
		}
		// Invert edges that are causing a cycle
		for (Edge e : g.edges) {
			if (getOrderIndex(e.source) > getOrderIndex(e.target) && !e.source.isNested(e.target)
					&& !e.target.isNested(e.source)) {
				e.invert();
				e.isFeedback = true;
			}
		}
	}

	/**
	 * Removes all edges connecting the given subgraph to other nodes outside of it.
	 *
	 * @param s
	 * @param n
	 */
	private static void isolateSubgraph(Subgraph subgraph, Node member) {
		for (Edge edge : member.incoming) {
			if (!subgraph.isNested(edge.source) && !edge.flag) {
				removeEdge(edge);
			}
		}
		for (Edge edge : member.outgoing) {
			if (!subgraph.isNested(edge.target) && !edge.flag) {
				removeEdge(edge);
			}
		}
		if (member instanceof Subgraph s) {
			s.members.forEach(n -> isolateSubgraph(subgraph, n));
		}
	}

	private static boolean isSink(Node n) {
		return getOutDegree(n) == 0 && (n.getParent() == null || isSink(n.getParent()));
	}

	private static boolean isSource(Node n) {
		return getInDegree(n) == 0 && (n.getParent() == null || isSource(n.getParent()));
	}

	private void remove(Node n) {
		n.flag = true;
		if (n.getParent() != null) {
			setChildCount(n.getParent(), getChildCount(n.getParent()) - 1);
		}
		removeSink(n, null);
		removeSource(n, null);
		sL.add(n);
		if (n instanceof Subgraph s) {
			isolateSubgraph(s, s);
			cycleRemove(s.members);
		}
	}

	private static boolean removeEdge(Edge e) {
		if (e.flag) {
			return false;
		}
		e.flag = true;
		changeOutDegree(e.source, -1);
		changeInDegree(e.target, -1);
		return true;
	}

	/**
	 * Removes all edges between a parent and any of its children or descendants.
	 */
	private static void removeParentChildEdges(DirectedGraph g) {
		g.edges.stream().filter(e -> (e.source.isNested(e.target) || e.target.isNested(e.source)))
				.forEach(CompoundBreakCycles::removeEdge);
	}

	private static void removeSink(Node sink, NodeList allSinks) {
		for (Edge e : sink.incoming) {
			if (!e.flag) {
				removeEdge(e);
				Node source = e.source;
				if (allSinks != null && isSink(source) && canBeRemoved(source)) {
					allSinks.add(source);
					source.flag = true;
				}
			}
		}
	}

	private static void removeSource(Node n, NodeList allSources) {
		for (Edge e : n.outgoing) {
			if (!e.flag) {
				e.flag = true;
				changeInDegree(e.target, -1);
				changeOutDegree(e.source, -1);

				Node target = e.target;
				if (allSources != null && isSource(target) && canBeRemoved(target)) {
					allSources.add(target);
					target.flag = true;
				}
			}
		}
	}

	/**
	 * Restores an edge if it has been removed, and both of its nodes are not
	 * removed.
	 *
	 * @param e the edge
	 * @return <code>true</code> if the edge was restored
	 */
	private static boolean restoreEdge(Edge e) {
		if (!e.flag || e.source.flag || e.target.flag) {
			return false;
		}
		e.flag = false;
		changeOutDegree(e.source, 1);
		changeInDegree(e.target, 1);
		return true;
	}

	/**
	 * Brings back all nodes nested in the given node.
	 *
	 * @param node the node to restore
	 * @param sr   current sinks
	 */
	private static void restoreSinks(Node node, NodeList sR) {
		if (node.flag && sR.contains(node)) {
			node.flag = false;
			if (node.getParent() != null) {
				setChildCount(node.getParent(), getChildCount(node.getParent()) + 1);
			}
			sR.remove(node);
			node.incoming.forEach(CompoundBreakCycles::restoreEdge);
			node.outgoing.forEach(CompoundBreakCycles::restoreEdge);
		}
		if (node instanceof Subgraph s) {
			s.members.forEach(n -> restoreSinks(n, sR));
		}
	}

	/**
	 * Brings back all nodes nested in the given node.
	 *
	 * @param node the node to restore
	 * @param sr   current sinks
	 */
	private void restoreSources(Node node) {
		if (node.flag && sL.contains(node)) {
			node.flag = false;
			if (node.getParent() != null) {
				setChildCount(node.getParent(), getChildCount(node.getParent()) + 1);
			}
			sL.remove(node);
			node.incoming.forEach(CompoundBreakCycles::restoreEdge);
			node.outgoing.forEach(CompoundBreakCycles::restoreEdge);
		}
		if (node instanceof Subgraph s) {
			s.members.forEach(this::restoreSources);
		}
	}

	@Override
	public void revisit(DirectedGraph g) {
		g.edges.stream().filter(Edge::isFeedback).forEach(Edge::invert);
	}

	private static void setChildCount(Node n, int count) {
		n.workingInts[3] = count;
	}

	private static void setInDegree(Node n, int deg) {
		n.workingInts[1] = deg;
	}

	private static void setOrderIndex(Node n, int index) {
		n.workingInts[0] = index;
	}

	private static void setOutDegree(Node n, int deg) {
		n.workingInts[2] = deg;
	}

	/**
	 * @see GraphVisitor#visit(org.eclipse.draw2d.graph.DirectedGraph)
	 */
	@Override
	public void visit(DirectedGraph g) {
		initializeDegrees(g);
		graphNodes = g.nodes;

		NodeList roots = new NodeList();
		graphNodes.stream().filter(n -> n.getParent() == null).forEach(n -> roots.add(n));
		buildNestingTreeIndices(roots, 0);
		removeParentChildEdges(g);
		cycleRemove(roots);
		invertEdges(g);
	}

}
