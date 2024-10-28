/*******************************************************************************
 * Copyright (c) 2003, 2023 IBM Corporation and others.
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

package org.eclipse.draw2d.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Deque;

import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.Rank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the swapping of adjacent nodes in a directed graph. since 3.0
 *
 */
public class LocalOptimizerTest {

	private DirectedGraph graph;
	private Node a, b, c, d, e, f, g, h, i, j, k, l;

	@BeforeEach
	public void setUp() {
		graph = new DirectedGraph();

		// create nodes and populate their ranks
		a = createNode("a"); //$NON-NLS-1$
		b = createNode("b"); //$NON-NLS-1$
		c = createNode("c"); //$NON-NLS-1$
		d = createNode("d"); //$NON-NLS-1$
		e = createNode("e"); //$NON-NLS-1$
		f = createNode("f"); //$NON-NLS-1$
		g = createNode("g"); //$NON-NLS-1$
		h = createNode("h"); //$NON-NLS-1$
		i = createNode("i"); //$NON-NLS-1$
		j = createNode("j"); //$NON-NLS-1$
		k = createNode("k"); //$NON-NLS-1$
		l = createNode("l"); //$NON-NLS-1$

		rankNodes(new Node[] { a, b, c, d }, 0);
		rankNodes(new Node[] { e, f, g, h }, 1);
		rankNodes(new Node[] { i, j, k, l }, 2);

		createDirectedGraphLayoutWithSelectedStepOnly(new String[] { "org.eclipse.draw2d.graph.PopulateRanks" }) //$NON-NLS-1$
				.visit(graph);
	}

	@Test
	public void testIncomingSwapNeeded() {
		/*
		 * A B C D |\| \ | | X \| | |\ | E F G H
		 */
		createEdge(a, e);
		createEdge(b, f);
		createEdge(a, g);
		createEdge(c, h);
		createEdge(d, h);

		createDirectedGraphLayoutWithSelectedStepOnly(new String[] { "org.eclipse.draw2d.graph.LocalOptimizer" }) //$NON-NLS-1$
				.visit(graph);

		checkResults(new Node[][] { new Node[] { a, b, c, d }, new Node[] { e, g, f, h } });
	}

	@Test
	public void testOutgoingSwapNeeded() {
		/*
		 * A BC D \ | X \|/ \ E F G H
		 */
		createEdge(a, f);
		createEdge(b, f);
		createEdge(d, f);
		createEdge(c, h);

		createDirectedGraphLayoutWithSelectedStepOnly(new String[] { "org.eclipse.draw2d.graph.LocalOptimizer" }) //$NON-NLS-1$
				.visit(graph);

		checkResults(new Node[][] { new Node[] { a, b, d, c }, new Node[] { e, f, g, h } });
	}

	@Test
	public void testIncomingOffsetSwapNeeded() {
		/*
		 * A B C (C should swap twice to first position) \ \ / \ X X \ / \ \
		 * [-----E----] F G H \ / / \ / / X / / \ / I J K L (Should not swap)
		 */
		createEdge(a, e);
		createEdge(b, e).setTargetOffset(30);
		createEdge(c, e).setTargetOffset(20);

		createEdge(e, k).setSourceOffset(20);
		createEdge(e, j).setSourceOffset(30);
		createEdge(f, k);

		createDirectedGraphLayoutWithSelectedStepOnly(new String[] { "org.eclipse.draw2d.graph.LocalOptimizer" }) //$NON-NLS-1$
				.visit(graph);

		checkResults(new Node[][] { new Node[] { c, a, b, d }, new Node[] { e, f, g, h }, new Node[] { i, j, k, l } });
	}

	@Test
	public void testBidirectionalSwapNeeded() {
		/*
		 * A B C D \ |\ \| \ E F G H (F and G should swap) X| / X | |\ I J K L
		 *
		 *
		 * A B C D (Which causes A&B on previous rank to swap) \ /| X | / \| E G F H |\
		 * \ | \ \ I J K L
		 */
		createEdge(a, f);
		createEdge(b, f);
		createEdge(b, g);
		createEdge(f, l);
		createEdge(g, j);
		createEdge(g, k);

		createDirectedGraphLayoutWithSelectedStepOnly(new String[] { "org.eclipse.draw2d.graph.LocalOptimizer" }) //$NON-NLS-1$
				.visit(graph);

		checkResults(new Node[][] { new Node[] { b, a, c, d }, new Node[] { e, g, f, h }, new Node[] { i, j, k, l } });
	}

	/**
	 * LocalOptimizer and other GraphVisitors are package private, so we cannot
	 * instantiate them directly. Instead, we use a DirectedGraphLayout and remove
	 * all other steps from it.
	 *
	 * @return A DirectedGraphLayout containing only the selected steps.
	 */
	private static DirectedGraphLayout createDirectedGraphLayoutWithSelectedStepOnly(String[] graphVisitorClassNames) {
		try {
			DirectedGraphLayout layout = new DirectedGraphLayout();
			Field stepsField = DirectedGraphLayout.class.getDeclaredField("steps"); //$NON-NLS-1$
			stepsField.setAccessible(true);
			Deque<?> steps = (Deque<?>) stepsField.get(layout);
			Deque<Object> filteredSteps = new ArrayDeque<>();
			for (Object graphVisitor : steps) {
				for (String graphVisitorClassName : graphVisitorClassNames) {
					if (graphVisitorClassName.equals(graphVisitor.getClass().getName())) {
						filteredSteps.add(graphVisitor);
					}
				}
			}
			assertEquals(graphVisitorClassNames.length, filteredSteps.size());
			stepsField.set(layout, filteredSteps);
			return layout;
		} catch (Exception e) {
			fail(e.getMessage());
			return null;
		}
	}

	private Node createNode(String label) {
		Node node = new Node(label);
		graph.nodes.add(node);
		return node;
	}

	private Edge createEdge(Node n1, Node n2) {
		Edge edge = new Edge(n1, n2);
		graph.edges.add(edge);
		return edge;
	}

	private static void rankNodes(Node[] nodes, int rank) {
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			try {
				// node.rank = rank;
				Field rankField = Node.class.getDeclaredField("rank"); //$NON-NLS-1$
				rankField.setAccessible(true);
				rankField.set(node, Integer.valueOf(rank));
				// node.index = i;
				Field indexField = Node.class.getDeclaredField("index"); //$NON-NLS-1$
				indexField.setAccessible(true);
				indexField.set(node, Integer.valueOf(i));
			} catch (Exception e) {
				fail(e.getMessage());
			}
		}
	}

	private void checkResults(Node[][] nodes) {
		for (int r = 0; r < nodes.length; r++) {
			Node[] row = nodes[r];
			Rank rank = graph.ranks.getRank(r);
			assertEquals(rank.size(), row.length);
			for (int n = 0; n < row.length; n++) {
				Node node = row[n];
				assertEquals(node, rank.get(n), "Unexpected node encountered at:" + r + "," + n); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
}
