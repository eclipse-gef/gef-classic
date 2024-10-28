/*******************************************************************************
 * Copyright (c) 2023 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d.graph.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.EdgeList;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
public class DirectedGraphLayoutTest {
	DirectedGraphLayout layout;
	DirectedGraph g;

	Node n1;
	Node n2;
	Node n3;

	Edge e1;
	Edge e2;
	Edge e3;

	@BeforeEach
	public void setUp() {
		n1 = new Node("n1");
		n2 = new Node("n2");
		n3 = new Node("n3");

		e1 = new Edge(n1, n2);
		e2 = new Edge(n2, n3);
		e3 = new Edge(n3, n1);

		NodeList nodes = new NodeList();
		nodes.addAll(List.of(n1, n2, n3));

		EdgeList edges = new EdgeList();
		edges.addAll(List.of(e1, e2, e3));

		g = new DirectedGraph();
		g.nodes = nodes;
		g.edges = edges;

		layout = new DirectedGraphLayout();
	}

	@Test
	public void test_detectCycles() {
		assertFalse(e1.isFeedback());
		assertFalse(e2.isFeedback());
		assertFalse(e3.isFeedback());

		layout.visit(g);

		assertFalse(e1.isFeedback());
		assertTrue(e2.isFeedback());
		assertFalse(e3.isFeedback());
	}

	@Test
	public void test_populateRanks() {
		assertEquals(getRank(n1), 0);
		assertEquals(getRank(n2), 0);
		assertEquals(getRank(n3), 0);

		layout.visit(g);

		assertEquals(getRank(n1), 1);
		assertEquals(getRank(n2), 2);
		assertEquals(getRank(n3), 0);
	}

	private static final int getRank(Node n) {
		try {
			Field f = Node.class.getDeclaredField("rank");
			f.setAccessible(true);
			return f.getInt(n);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
