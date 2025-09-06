/*******************************************************************************
 * Copyright (c) 2004, 2025 IBM Corporation and others.
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

/**
 * Encapsulates the conversion of a long edge to multiple short edges and back.
 *
 * @since 3.1
 */
class VirtualNodeCreation extends RevertableChange {

	private final Edge edge;
	private final DirectedGraph graph;
	private final Node[] nodes;
	private final Edge[] edges;

	private static final int INNER_EDGE_X = 2;
	private static final int LONG_EDGE_X = 8;

	/**
	 * Breaks a single edge into multiple edges containing virtual nodes.
	 *
	 * @since 3.1
	 * @param edge  The edge to convert
	 * @param graph the graph containing the edge
	 */
	@SuppressWarnings("removal")
	public VirtualNodeCreation(Edge edge, DirectedGraph graph) {
		this.edge = edge;
		this.graph = graph;

		int size = edge.target.rank - edge.source.rank - 1;
		int offset = edge.source.rank + 1;

		Node prevNode = edge.source;
		Node currentNode;
		Edge currentEdge;
		nodes = new Node[size];
		edges = new Edge[size + 1];

		Insets padding = new Insets(0, edge.getPadding(), 0, edge.getPadding());

		Subgraph s = GraphUtilities.getCommonAncestor(edge.source, edge.target);

		for (int i = 0; i < size; i++) {
			nodes[i] = currentNode = new VirtualNode("Virtual" + i + ':' + edge, s); //$NON-NLS-1$
			currentNode.width = edge.getWidth();
			if (s != null) {
				currentNode.nestingIndex = s.nestingIndex;
			}

			currentNode.height = 0;
			currentNode.setPadding(padding);
			currentNode.rank = offset + i;
			graph.ranks.getRank(offset + i).add(currentNode);

			currentEdge = new Edge(prevNode, currentNode, 1, edge.weight * LONG_EDGE_X);
			if (i == 0) {
				currentEdge.weight = edge.weight * INNER_EDGE_X;
				currentEdge.setSourceOffset(edge.offsetSource);
			}
			graph.edges.add(edges[i] = currentEdge);
			graph.nodes.add(currentNode);
			prevNode = currentNode;
		}

		currentEdge = new Edge(prevNode, edge.target, 1, edge.weight * INNER_EDGE_X);
		currentEdge.setTargetOffset(edge.offsetTarget);
		edges[edges.length - 1] = currentEdge;
		graph.edges.add(currentEdge);
		graph.removeEdge(edge);
	}

	@Override
	@SuppressWarnings("removal")
	void revert() {
		edge.start = edges[0].start;
		edge.end = edges[edges.length - 1].end;
		edge.vNodes = new NodeList();
		for (Edge edge2 : edges) {
			graph.removeEdge(edge2);
		}
		for (Node node : nodes) {
			edge.vNodes.add(node);
			graph.removeNode(node);
		}
		edge.source.outgoing.add(edge);
		edge.target.incoming.add(edge);

		graph.edges.add(edge);
	}

}
