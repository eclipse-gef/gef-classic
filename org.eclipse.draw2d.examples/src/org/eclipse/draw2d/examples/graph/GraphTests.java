/*******************************************************************************
 * Copyright (c) 2005, 2023 IBM Corporation and others.
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
package org.eclipse.draw2d.examples.graph;

import java.util.Random;

import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.EdgeList;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;

/**
 * @author hudsonr
 * @since 2.1
 */
public class GraphTests {
	private static final String ROOT = "root"; //$NON-NLS-1$
	private static final String HEAD = "Head"; //$NON-NLS-1$
	private static final String TOP_1 = "Top 1"; //$NON-NLS-1$
	private static final String TOP_2 = "Top 2"; //$NON-NLS-1$
	private static final String BOTTOM_1 = "Bottom 1"; //$NON-NLS-1$
	private static final String BOTTOM_2 = "Bottom 2"; //$NON-NLS-1$

	private static final String NODE_A = "node a"; //$NON-NLS-1$
	private static final String NODE_A0 = "node a0"; //$NON-NLS-1$
	private static final String NODE_A1 = "node a1"; //$NON-NLS-1$
	private static final String NODE_A2 = "node a2"; //$NON-NLS-1$
	private static final String NODE_A3 = "node a3"; //$NON-NLS-1$
	private static final String NODE_A4 = "node a4"; //$NON-NLS-1$
	private static final String NODE_B = "node b"; //$NON-NLS-1$
	private static final String NODE_B0 = "node b0"; //$NON-NLS-1$
	private static final String NODE_B1 = "node b1"; //$NON-NLS-1$
	private static final String NODE_B2 = "node b2"; //$NON-NLS-1$
	private static final String NODE_B3 = "node b3"; //$NON-NLS-1$
	private static final String NODE_B4 = "node b4"; //$NON-NLS-1$
	private static final String NODE_B5 = "node b5"; //$NON-NLS-1$
	private static final String NODE_B6 = "node b6"; //$NON-NLS-1$
	private static final String NODE_C = "node c"; //$NON-NLS-1$
	private static final String NODE_C0 = "node c0"; //$NON-NLS-1$
	private static final String NODE_C1 = "node c1"; //$NON-NLS-1$
	private static final String NODE_C2 = "node c2"; //$NON-NLS-1$
	private static final String NODE_C3 = "node c3"; //$NON-NLS-1$
	private static final String NODE_C4 = "node c4"; //$NON-NLS-1$
	private static final String NODE_D = "node d"; //$NON-NLS-1$
	private static final String NODE_E = "node e"; //$NON-NLS-1$
	private static final String NODE_F = "node f"; //$NON-NLS-1$
	private static final String NODE_G = "node g"; //$NON-NLS-1$
	private static final String NODE_I = "node i"; //$NON-NLS-1$
	private static final String NODE_J = "node j"; //$NON-NLS-1$
	private static final String NODE_K = "node k"; //$NON-NLS-1$
	private static final String NODE_H = "node h"; //$NON-NLS-1$
	private static final String H1 = "h1"; //$NON-NLS-1$
	private static final String H2 = "h2"; //$NON-NLS-1$
	private static final String H3 = "h3"; //$NON-NLS-1$
	private static final String H4 = "h4"; //$NON-NLS-1$
	private static final String NODE_X = "node X"; //$NON-NLS-1$

	public static DirectedGraph offsetTest(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node head = new Node(HEAD);
		nodes.add(head);
		head.width = 100;
		Node a1 = new Node(NODE_A1);
		nodes.add(a1);
		Node a2 = new Node(NODE_A2);
		nodes.add(a2);
		Node a3 = new Node(NODE_A3);
		nodes.add(a3);
		Node a4 = new Node(NODE_A4);
		nodes.add(a4);

		Node b1 = new Node(NODE_B1);
		nodes.add(b1);
		Node b2 = new Node(NODE_B2);
		nodes.add(b2);
		Node b3 = new Node(NODE_B3);
		nodes.add(b3);
		Node b4 = new Node(NODE_B4);
		nodes.add(b4);

		Edge e = new Edge(head, a1);
		e.setSourceOffset(10);
		edges.add(e);

		e = new Edge(head, b1);
		e.setSourceOffset(90);
		edges.add(e);

		a1.incomingOffset = 40;
		b1.incomingOffset = 10;
		a1.outgoingOffset = 10;
		a2.incomingOffset = 40;

		a3.outgoingOffset = 10;
		a4.incomingOffset = 40;

		edges.add(new Edge(b1, b2));
		edges.add(new Edge(b2, b3));
		edges.add(new Edge(b3, b4));

		edges.add(new Edge(a1, a2));
		e = new Edge(a2, a3);
		edges.add(e);
		e.setSourceOffset(10);
		e.setTargetOffset(40);
		edges.add(new Edge(a3, a4));

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);
		return graph;
	}

	static Random rand = new Random(90);

	/**
	 * @param nodes
	 * @param row
	 */
	private static void addNodes(NodeList nodes, Node[] row) {
		for (Node element : row) {
			if (element != null) {
				nodes.add(element);
			}
		}
	}

	public static DirectedGraph anotherTour(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node[] firstRow = new Node[4];
		firstRow[1] = new Node(H1);
		firstRow[2] = new Node(H2);
		firstRow[3] = new Node(H3);
		addNodes(nodes, firstRow);

		Node[] row = joinRows(nodes, edges, firstRow, new int[] { 1, 1, 1, 2, 1, 3, 2, 3, 2, 4, 3, 3, 3, 4 });

		row = joinRows(nodes, edges, row, new int[] { 1, 1, 3, 4, 2, 1, 2, 3, 3, 2, 4, 4 });

		row = joinRows(nodes, edges, row, new int[] { 1, 1, 1, 2, 2, 1, 2, 4, 3, 3, 3, 4, 4, 3, 4, 5 });

		joinRows(nodes, edges, row, new int[] { 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 1, 2, 4, 3, 5, 4 });

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);

		return graph;
	}

	public static DirectedGraph balanceThis1(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node b1 = new Node(NODE_B1);
		nodes.add(b1);
		Node b2 = new Node(NODE_B2);
		nodes.add(b2);
		Node b3 = new Node(NODE_B3);
		nodes.add(b3);
		Node b4 = new Node(NODE_B4);
		nodes.add(b4);
		Node b5 = new Node(NODE_B5);
		nodes.add(b5);
		Node c = new Node(NODE_C);
		nodes.add(c);
		Node d = new Node(NODE_D);
		nodes.add(d);
		Node e = new Node(NODE_E);
		nodes.add(e);
		Node f = new Node(NODE_F);
		nodes.add(f);
		Node g = new Node(NODE_G);
		nodes.add(g);
		Node a = new Node(NODE_A);
		nodes.add(a);

		b3.width = b2.width = 90;

		edges.add(new Edge(c, d));
		edges.add(new Edge(c, e));
		edges.add(new Edge(e, f));
		edges.add(new Edge(e, g));
		edges.add(new Edge(a, b1));
		edges.add(new Edge(a, b2));
		edges.add(new Edge(a, b3));
		edges.add(new Edge(a, b4));
		edges.add(new Edge(a, b5));

		edges.add(new Edge(b3, d));
		edges.add(new Edge(b4, f));

		edges.add(new Edge(a, c));

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);
		return graph;
	}

	public static DirectedGraph balanceThis2(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node b2 = new Node(NODE_B2);
		nodes.add(b2);
		Node b3 = new Node(NODE_B3);
		nodes.add(b3);
		Node b4 = new Node(NODE_B4);
		nodes.add(b4);
		Node c = new Node(NODE_C);
		nodes.add(c);
		Node d = new Node(NODE_D);
		nodes.add(d);
		Node e = new Node(NODE_E);
		nodes.add(e);
		Node f = new Node(NODE_F);
		nodes.add(f);
		Node g = new Node(NODE_G);
		nodes.add(g);
		Node a = new Node(NODE_A);
		nodes.add(a);

		edges.add(new Edge(d, c));
		edges.add(new Edge(e, c));
		edges.add(new Edge(f, e));
		edges.add(new Edge(g, e));
		edges.add(new Edge(b2, a));
		edges.add(new Edge(b3, a));
		edges.add(new Edge(b4, a, 1, 2));

		edges.add(new Edge(d, b3));
		edges.add(new Edge(f, b4));

		edges.add(new Edge(c, a));

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);
		return graph;
	}

	public static DirectedGraph balanceThis3(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node top1 = new Node(TOP_1);
		nodes.add(top1);
		top1.width = 400;

		Node top2 = new Node(TOP_2);
		nodes.add(top2);
		top2.width = 400;

		Node a1 = new Node(NODE_A1);
		nodes.add(a1);
		Node a2 = new Node(NODE_A2);
		nodes.add(a2);
		Node a3 = new Node(NODE_A3);
		nodes.add(a3);
		Node a4 = new Node(NODE_A4);
		nodes.add(a4);
		Node b1 = new Node(NODE_B1);
		nodes.add(b1);
		Node b2 = new Node(NODE_B2);
		nodes.add(b2);
		Node b3 = new Node(NODE_B3);
		nodes.add(b3);
		Node b4 = new Node(NODE_B4);
		nodes.add(b4);
		Node b5 = new Node(NODE_B5);
		nodes.add(b5);
		Node b6 = new Node(NODE_B6);
		nodes.add(b6);
		Node c1 = new Node(NODE_C1);
		nodes.add(c1);
		Node c2 = new Node(NODE_C2);
		nodes.add(c2);
		Node c3 = new Node(NODE_C3);
		nodes.add(c3);
		Node c4 = new Node(NODE_C4);
		nodes.add(c4);

		Node bottom1 = new Node(BOTTOM_1);
		nodes.add(bottom1);
		bottom1.width = 400;

		Node bottom2 = new Node(BOTTOM_2);
		nodes.add(bottom2);
		bottom2.width = 400;

		edges.add(new Edge(top1, a1));
		edges.add(new Edge(top2, a4));
		edges.add(new Edge(a1, b1));
		edges.add(new Edge(a2, b1));
		edges.add(new Edge(a2, b3));
		edges.add(new Edge(a3, b3));
		edges.add(new Edge(a3, b5));
		edges.add(new Edge(a4, b5));
		edges.add(new Edge(a4, b6));
		edges.add(new Edge(b1, c1));
		edges.add(new Edge(b2, c1));
		edges.add(new Edge(b2, c2));
		edges.add(new Edge(b4, c2));
		edges.add(new Edge(b4, c3));
		edges.add(new Edge(b6, c3));
		edges.add(new Edge(b6, c4));
		edges.add(new Edge(c1, bottom1));
		edges.add(new Edge(c4, bottom2));

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);
		return graph;

	}

	public static DirectedGraph fourLevelBinaryTree(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node[] firstRow = new Node[2];
		firstRow[1] = new Node(ROOT);
		addNodes(nodes, firstRow);

		Node[] row = joinRows(nodes, edges, firstRow, new int[] { 1, 1, 1, 2 });

		row = joinRows(nodes, edges, row, new int[] { 1, 1, 1, 2, 2, 3, 2, 4 });

		joinRows(nodes, edges, row, new int[] { 1, 1, 1, 2, 2, 3, 2, 4, 3, 5, 3, 6, 4, 7, 4, 8 });

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);

		return graph;
	}

	public static DirectedGraph graph1(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node[] firstRow = new Node[3];
		firstRow[1] = new Node(H1);
		firstRow[2] = new Node(H2);
		addNodes(nodes, firstRow);
		Node[] row = joinRows(nodes, edges, firstRow,
				new int[] { 1, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 2, 7, 2, 8, 2, 9, 2, 10 });

		row = joinRows(nodes, edges, row,
				new int[] { 1, 1, 2, 2, 2, 3, 3, 1, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 9, 10, 10, 11 });

		// Row 3->4
		row = joinRows(nodes, edges, row,
				new int[] { 1, 1, 1, 2, 2, 3, 2, 6, 3, 6, 4, 4, 5, 5, 6, 7, 7, 6, 8, 8, 9, 6, 10, 9, 10, 10, 11, 11 });

		// Row 4->5
		row = joinRows(nodes, edges, row,
				new int[] { 1, 1, 1, 2, 1, 3, 2, 3, 3, 4, 4, 5, 5, 6, 7, 3, 8, 7, 9, 8, 10, 9, 10, 10, 11, 11 });
		// Row 5->6
		row = joinRows(nodes, edges, row, new int[] { 1, 1, 2, 2, 4, 3, 5, 4, 6, 5, 7, 6, 8, 7, 9, 7, 10, 7, 11, 7 });
		// Row 6->7
		row = joinRows(nodes, edges, row, new int[] { 1, 1, 2, 2, 3, 4, 4, 4, 5, 3, 6, 4, 7, 4, 7, 5 });
		// Row 7->8
		row = joinRows(nodes, edges, row, new int[] { 1, 2, 2, 1, 3, 2, 4, 2, 4, 3, 5, 4 });

		// Row 8->9
		joinRows(nodes, edges, row, new int[] { 1, 1, 2, 1, 3, 1, 4, 1 });

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);

		return graph;
	}

	public static DirectedGraph graph2(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node[] firstRow = new Node[5];
		firstRow[1] = new Node(H1);
		firstRow[2] = new Node(H2);
		firstRow[3] = new Node(H3);
		firstRow[4] = new Node(H4);
		firstRow[1].width = firstRow[2].width = firstRow[3].width = firstRow[4].width = 160;
		addNodes(nodes, firstRow);
		Node[] row = joinRows(nodes, edges, firstRow, new int[] { 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5 });

		row = joinRows(nodes, edges, row, new int[] { 1, 1, 3, 3, 5, 2 });

		row = joinRows(nodes, edges, row, new int[] { 1, 1, 2, 2 });

		joinRows(nodes, edges, row, new int[] { 1, 1, 1, 2, 2, 2, 2, 1 });

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);

		return graph;
	}

	public static DirectedGraph graph3(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node[] firstRow = new Node[4];
		firstRow[1] = new Node(H1);
		firstRow[2] = new Node(H2);
		firstRow[3] = new Node(H3);
		firstRow[1].width = firstRow[2].width = firstRow[3].width = 120;
		addNodes(nodes, firstRow);
		Node[] row = joinRows(nodes, edges, firstRow, new int[] { 1, 1, 1, 2, 1, 3, 2, 3, 2, 4, 3, 3, 3, 4 });

		row = joinRows(nodes, edges, row, new int[] { 1, 1, 3, 4, 2, 1, 2, 3, 3, 2, 4, 4 });

		row = joinRows(nodes, edges, row, new int[] { 1, 1, 1, 2, 2, 1, 2, 4, 3, 3, 3, 4, 4, 3, 4, 5 });

		joinRows(nodes, edges, row, new int[] { 1, 1, 2, 2, 4, 4, 5, 5, 1, 2, 4, 3, 5, 4 });

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);

		return graph;
	}

	/**
	 * @param nodes
	 * @param edges
	 * @param firstRow
	 * @param is
	 * @return
	 */
	private static Node[] joinRows(NodeList nodes, EdgeList edges, Node[] firstRow, int[] conns) {
		shuffleConnections(conns);
		Node[] secondRow = new Node[20];
		for (int i = 0; i < conns.length; i += 2) {
			Node head = firstRow[conns[i]];
			Node tail = secondRow[conns[i + 1]];
			if (tail == null) {
				tail = secondRow[conns[i + 1]] = new Node("node" + conns[i + 1]); //$NON-NLS-1$
				tail.width = 78;
			}
			edges.add(new Edge(head, tail));
		}
		addNodes(nodes, secondRow);
		return secondRow;
	}

	/**
	 * @param conns
	 */
	private static void shuffleConnections(int[] conns) {
		for (int i = 0; i < conns.length; i += 2) {
			int swap = rand.nextInt(conns.length / 2) * 2;
			int temp = conns[i];
			conns[i] = conns[swap];
			conns[swap] = temp;

			temp = conns[i + 1];
			conns[i + 1] = conns[swap + 1];
			conns[swap + 1] = temp;
		}
	}

	public static DirectedGraph simpleGraph(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node a = new Node(NODE_A);
		nodes.add(a);
		Node e = new Node(NODE_E);
		nodes.add(e);
		Node b = new Node(NODE_B);
		nodes.add(b);
		Node c = new Node(NODE_C);
		nodes.add(c);
		Node d = new Node(NODE_D);
		nodes.add(d);
		Node x = new Node(NODE_X);
		nodes.add(x);
		Node f = new Node(NODE_F);
		nodes.add(f);
		Node g = new Node(NODE_G);
		nodes.add(g);
		Node h = new Node(NODE_H);
		nodes.add(h);
		Node i = new Node(NODE_I);
		nodes.add(i);

		edges.add(new Edge(a, i));
		edges.add(new Edge(i, g));
		edges.add(new Edge(i, h));
		edges.add(new Edge(c, i));

		edges.add(new Edge(a, b));
		edges.add(new Edge(b, c));
		edges.add(new Edge(c, d));
		edges.add(new Edge(d, x));
		edges.add(new Edge(x, e));
		edges.add(new Edge(a, f));
		edges.add(new Edge(f, g));
		edges.add(new Edge(f, h));
		edges.add(new Edge(g, e));
		edges.add(new Edge(h, e));

		edges.add(new Edge(b, g));
		edges.add(new Edge(f, d));

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);
		return graph;
	}

	public static DirectedGraph test1(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node a = new Node(NODE_A);
		nodes.add(a);
		Node b = new Node(NODE_B);
		nodes.add(b);
		Node c = new Node(NODE_C);
		nodes.add(c);
		Node d = new Node(NODE_D);
		nodes.add(d);
		Node e = new Node(NODE_E);
		nodes.add(e);
		Node f = new Node(NODE_F);
		nodes.add(f);
		Node g = new Node(NODE_G);
		nodes.add(g);

		edges.add(new Edge(a, d));
		edges.add(new Edge(b, d));
		edges.add(new Edge(c, d));
		edges.add(new Edge(d, e));
		edges.add(new Edge(d, f));
		edges.add(new Edge(d, g));
		edges.add(new Edge(c, g));

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);
		return graph;
	}

	public static DirectedGraph test2(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node a = new Node(NODE_A);
		nodes.add(a);
		Node b = new Node(NODE_B);
		nodes.add(b);
		Node c = new Node(NODE_C);
		nodes.add(c);
		Node d = new Node(NODE_D);
		nodes.add(d);
		Node e = new Node(NODE_E);
		nodes.add(e);
		Node f = new Node(NODE_F);
		nodes.add(f);

		edges.add(new Edge(a, b));
		edges.add(new Edge(a, c));
		edges.add(new Edge(d, b));

		edges.add(new Edge(a, f));
		edges.add(new Edge(e, b));

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);
		return graph;
	}

	public static DirectedGraph test3(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node a = new Node(NODE_A);
		nodes.add(a);
		Node b = new Node(NODE_B);
		nodes.add(b);
		Node c = new Node(NODE_C);
		nodes.add(c);
		Node d = new Node(NODE_D);
		nodes.add(d);

		a.width = 80;
		d.width = 75;

		edges.add(new Edge(a, b));
		edges.add(new Edge(a, c));
		edges.add(new Edge(c, d));

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);
		return graph;
	}

	public static DirectedGraph test4(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node a = new Node(NODE_A);
		nodes.add(a);
		Node b = new Node(NODE_B);
		nodes.add(b);
		Node c = new Node(NODE_C);
		nodes.add(c);
		Node d = new Node(NODE_D);
		nodes.add(d);
		Node e = new Node(NODE_E);
		nodes.add(e);
		Node f = new Node(NODE_F);
		nodes.add(f);
		Node g = new Node(NODE_G);
		nodes.add(g);
		Node h = new Node(NODE_H);
		nodes.add(h);
		Node i = new Node(NODE_I);
		nodes.add(i);
		Node j = new Node(NODE_J);
		nodes.add(j);
		Node k = new Node(NODE_K);
		nodes.add(k);

		edges.add(new Edge(a, b));
		edges.add(new Edge(a, c));
		edges.add(new Edge(a, d));
		edges.add(new Edge(b, e));
		edges.add(new Edge(c, e));

		edges.add(new Edge(d, e));
		edges.add(new Edge(d, f));
		edges.add(new Edge(d, g));
		edges.add(new Edge(e, h));
		edges.add(new Edge(f, h));

		edges.add(new Edge(g, h));
		edges.add(new Edge(g, i));
		edges.add(new Edge(g, j));
		edges.add(new Edge(h, k));
		edges.add(new Edge(i, k));
		edges.add(new Edge(j, k));

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);
		return graph;
	}

	public static DirectedGraph tinyGraph(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node a = new Node(NODE_A);
		nodes.add(a);
		Node b = new Node(NODE_B);
		nodes.add(b);
		Node c = new Node(NODE_C);
		nodes.add(c);
		Node d = new Node(NODE_D);
		nodes.add(d);
		Node e = new Node(NODE_E);
		nodes.add(e);
		Node g = new Node(NODE_G);
		nodes.add(g);

		edges.add(new Edge(a, d));
		edges.add(new Edge(c, b));
		edges.add(new Edge(b, g, 1, 3));
		edges.add(new Edge(c, d));
		edges.add(new Edge(d, e));
		edges.add(new Edge(d, g));

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);
		return graph;
	}

	public static DirectedGraph unstableGraph(int direction) {
		NodeList nodes = new NodeList();
		EdgeList edges = new EdgeList();

		Node a0 = new Node(NODE_A0);
		nodes.add(a0);
		Node b0 = new Node(NODE_B0);
		nodes.add(b0);
		Node c0 = new Node(NODE_C0);
		nodes.add(c0);
		Node a1 = new Node(NODE_A1);
		nodes.add(a1);
		Node b1 = new Node(NODE_B1);
		nodes.add(b1);
		Node c1 = new Node(NODE_C1);
		nodes.add(c1);
		Node a2 = new Node(NODE_A2);
		nodes.add(a2);
		Node b2 = new Node(NODE_B2);
		nodes.add(b2);
		Node c2 = new Node(NODE_C2);
		nodes.add(c2);

		edges.add(new Edge(a0, a1, 1, 9));
		edges.add(new Edge(a1, a2, 1, 9));

		edges.add(new Edge(b0, b1, 1, 9));
		edges.add(new Edge(b1, b2, 1, 9));

		edges.add(new Edge(c0, c1, 1, 9));
		edges.add(new Edge(c1, c2, 1, 9));

		edges.add(new Edge(a0, b2));
		edges.add(new Edge(b0, c2));
		edges.add(new Edge(c0, a2));

		DirectedGraph graph = new DirectedGraph();
		graph.setDirection(direction);
		graph.nodes = nodes;
		graph.edges = edges;

		new DirectedGraphLayout().visit(graph);
		return graph;

	}

	private GraphTests() {
		throw new UnsupportedOperationException();
	}
}
