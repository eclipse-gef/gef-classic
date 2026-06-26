/*******************************************************************************
 * Copyright (c) 2011, 2025 Fabian Steeg. All rights reserved.
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.core.widgets.internal.GraphLabel;
import org.eclipse.zest.core.widgets.internal.GraphLightweightSystem;
import org.eclipse.zest.core.widgets.internal.ImageRegistry;
import org.eclipse.zest.core.widgets.internal.ZestRootLayer;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.SWTGraphics;

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

	private GraphConnection connection;

	Shell shell;

	@BeforeEach
	public void setUp() throws Exception {
		shell = new Shell();
		graph = new Graph(shell, SWT.NONE);
		nodes = new GraphNode[] { new GraphNode(graph, SWT.NONE), new GraphNode(graph, SWT.NONE) };
		connection = new GraphConnection(graph, SWT.NONE, nodes[0], nodes[1]);
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

	@Test
	public void testDisposeCachedLabel() throws Exception {
		GraphLabel label = new GraphLabel("Test", true); //$NON-NLS-1$
		graph.getRootLayer().add(label);

		GC gc = new GC(shell);
		SWTGraphics graphics = new SWTGraphics(gc);
		label.paint(graphics);
		graphics.dispose();
		gc.dispose();

		Map<String, Image> registry = getCachedImages();
		assertEquals(1, registry.size());
		graph.dispose();
		assertTrue(registry.isEmpty());
	}

	private Map<String, Image> getCachedImages() throws Exception {
		MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(ImageRegistry.class, MethodHandles.lookup());
		VarHandle handle = lookup.findVarHandle(ImageRegistry.class, "registry", Map.class); //$NON-NLS-1$

		ImageRegistry registry = ((GraphLightweightSystem) graph.getLightweightSystem()).internalGetImageRegistry();
		return (Map<String, Image>) handle.get(registry);
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
}
