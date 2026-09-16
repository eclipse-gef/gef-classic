/*******************************************************************************
 * Copyright (c) 2011, 2026 Fabian Steeg. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Fabian Steeg - initial tests
 *******************************************************************************/
package org.eclipse.zest.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.core.widgets.internal.ZestRootLayer;
import org.eclipse.zest.layouts.interfaces.LayoutContext;
import org.eclipse.zest.tests.utils.SWTBotGraph;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * General tests for the {@link Graph} class.
 *
 * @author Fabian Steeg (fsteeg)
 *
 */
public class GraphTests {

	private GraphNode[] nodes;

	private Graph graph;
	private SWTBotGraph graphRobot;

	private GraphConnection connection;

	Shell shell;

	@BeforeEach
	public void setUp() throws Exception {
		shell = new Shell();
		graph = new Graph(shell, SWT.NONE);
		graph.setSize(400, 400);
		graphRobot = new SWTBotGraph(graph);
		nodes = new GraphNode[] { new GraphNode(graph, SWT.NONE), new GraphNode(graph, SWT.NONE) };
		connection = new GraphConnection(graph, SWT.NONE, nodes[0], nodes[1]);
	}

	@AfterEach
	public void tearDown() {
		shell.dispose();
	}

	@Test
	public void testGraphData() {
		graph.setData("graph data"); //$NON-NLS-1$
		assertEquals("graph data", graph.getData()); //$NON-NLS-1$
	}

	@Test
	public void testNodeItemData() {
		GraphItem item = graph.getNodes().get(0);
		item.setData("node item data"); //$NON-NLS-1$
		assertEquals("node item data", item.getData()); //$NON-NLS-1$
	}

	@Test
	public void testConnectionItemData() {
		GraphItem item = graph.getConnections().get(0);
		item.setData("connection item data"); //$NON-NLS-1$
		assertEquals("connection item data", item.getData()); //$NON-NLS-1$
	}

	/**
	 * Avoid infinite loop by disposed nodes during graph disposal.
	 *
	 * @See https://bugs.eclipse.org/bugs/show_bug.cgi?id=361541
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testDisposeGraphWithDisposedNode() {
		nodes[0].dispose(); // removes the node from the graph's nodes list
		((List<GraphNode>) graph.getNodes()).add(nodes[0]); // but we're malicious and add it back
		assertTrue(nodes[0].isDisposed(), "Node should be disposed"); //$NON-NLS-1$
		graph.dispose();
		assertTrue(graph.isDisposed(), "Graph should be disposed"); //$NON-NLS-1$
	}

	/**
	 * Avoid infinite loop by disposed connections during graph disposal.
	 *
	 * @See https://bugs.eclipse.org/bugs/show_bug.cgi?id=361541
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testDisposeGraphWithDisposedConnection() {
		connection.dispose();
		((List<GraphConnection>) graph.getConnections()).add(connection);
		assertTrue(connection.isDisposed(), "Connection should be disposed"); //$NON-NLS-1$
		graph.dispose();
		assertTrue(graph.isDisposed(), "Graph should be disposed"); //$NON-NLS-1$
	}

	/**
	 * Avoid issues when un-highlighting non-existent nodes on
	 * {@link ZestRootLayer}.
	 *
	 * @See https://bugs.eclipse.org/bugs/show_bug.cgi?id=361525
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testUnHighlightNode() {
		new ZestRootLayer().unHighlightNode(new Figure());
	}

	/**
	 * Avoid issues when un-highlighting non-existent connections on
	 * {@link ZestRootLayer}.
	 *
	 * @See https://bugs.eclipse.org/bugs/show_bug.cgi?id=361525
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testUnHighlightConnection() {
		new ZestRootLayer().unHighlightConnection(new Figure());
	}

	/**
	 * Check that Graph resources are cleaned up when parent is disposed (see
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=373191)
	 */
	@Test
	public void testDisposal() {
		GraphNode n = graph.getNodes().get(0);
		GraphConnection c = graph.getConnections().get(0);
		shell.dispose();
		assertTrue(n.isDisposed(), "The graph's nodes should be disposed"); //$NON-NLS-1$
		assertTrue(c.isDisposed(), "The graph's edges should be disposed"); //$NON-NLS-1$

	}

	/**
	 * Check that the {@link ZestStyles#IGNORE_INVISIBLE_LAYOUT} style can be set
	 * and used correctly.
	 *
	 * @See https://bugs.eclipse.org/bugs/show_bug.cgi?id=254584
	 */
	@Test
	public void testInvisibleLayoutStyle() {
		LayoutContext layoutContext = graph.getLayoutContext();
		GraphNode node = graph.getNodes().get(0);
		node.setVisible(false);
		GraphConnection conn = graph.getConnections().get(0);
		conn.setVisible(false);

		assertEquals(layoutContext.getNodes().length, 2);
		assertEquals(layoutContext.getConnections().length, 1);
		assertEquals(layoutContext.getEntities().length, 2);
		graph.setGraphStyle(ZestStyles.IGNORE_INVISIBLE_LAYOUT);
		assertEquals(layoutContext.getNodes().length, 1);
		assertEquals(layoutContext.getConnections().length, 0);
		assertEquals(layoutContext.getEntities().length, 1);
		graph.setGraphStyle(ZestStyles.NONE);
		assertEquals(layoutContext.getNodes().length, 2);
		assertEquals(layoutContext.getConnections().length, 1);
		assertEquals(layoutContext.getEntities().length, 2);
	}

	/**
	 * Check that the {@link ZestStyles#GESTURES_DISABLED} style can be set and used
	 * correctly.
	 *
	 * @See https://bugs.eclipse.org/bugs/show_bug.cgi?id=254584
	 */
	@Test
	public void testGestureStyle() {
		assertEquals(graph.getListeners(SWT.Gesture).length, 2);
		graph.setGraphStyle(ZestStyles.GESTURES_DISABLED);
		assertEquals(graph.getListeners(SWT.Gesture).length, 0);
		graph.setGraphStyle(ZestStyles.NONE);
		assertEquals(graph.getListeners(SWT.Gesture).length, 2);
	}

	@Test
	public void testDoubleClickOnGraphContainer() {
		GraphContainer c = new GraphContainer(graph, SWT.NONE);
		IFigure containerFigure = c.getNodeFigure();
		containerFigure.setBounds(new Rectangle(0, 0, 100, 100));

		GraphNode n = new GraphNode(c, SWT.NONE);
		n.setText("Hello World"); //$NON-NLS-1$
		IFigure nodeFigure = n.getNodeFigure();

		Point containerCenter = containerFigure.getBounds().getCenter();
		graphRobot.mouseDoubleClick(containerCenter.x, containerCenter.y);
		// no changes to the SWT structure
		assertSame(graph, c.getGraphModel());
		assertSame(graph, n.getGraphModel());
		// Graph only shows the container figures
		ZestRootLayer rootLayer = (ZestRootLayer) graph.getRootLayer().getChildren().get(0);
		assertEquals(rootLayer.getChildren(), List.of(nodeFigure));
	}
}
