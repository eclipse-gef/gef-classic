/*******************************************************************************
 * Copyright (c) 2024, 2025 Patrick Ziegler and others.
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

package org.eclipse.zest.tests.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.zest.core.viewers.INestedContentProvider;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.internal.ContainerFigure;
import org.eclipse.zest.examples.jface.GraphJFaceSnippet1;
import org.eclipse.zest.examples.jface.GraphJFaceSnippet2;
import org.eclipse.zest.examples.jface.GraphJFaceSnippet3;
import org.eclipse.zest.examples.jface.GraphJFaceSnippet4;
import org.eclipse.zest.examples.jface.GraphJFaceSnippet5;
import org.eclipse.zest.examples.jface.GraphJFaceSnippet6;
import org.eclipse.zest.examples.jface.GraphJFaceSnippet7;
import org.eclipse.zest.examples.jface.GraphJFaceSnippet8;
import org.eclipse.zest.examples.uml.UMLClassFigure;
import org.eclipse.zest.tests.utils.SWTBotGraphConnection;
import org.eclipse.zest.tests.utils.SWTBotGraphContainer;
import org.eclipse.zest.tests.utils.SWTBotGraphNode;
import org.eclipse.zest.tests.utils.Snippet;

import org.junit.jupiter.api.Test;

/**
 * This class instantiates the {@link GraphViewer}-based Zest examples and tests
 * the correctness of the functionality they are supposed to show.
 */
@SuppressWarnings("nls")
public class GraphJFaceTests extends AbstractGraphTest {
	protected GraphViewer viewer;

	@Override
	protected Graph getGraph(Lookup lookup, Snippet snippet) throws ReflectiveOperationException {
		VarHandle varHandle = lookup.findStaticVarHandle(snippet.type(), "viewer", GraphViewer.class); //$NON-NLS-1$
		viewer = (GraphViewer) varHandle.get();
		return viewer.getGraphControl();
	}

	@Override
	protected boolean hasGraph(Lookup lookup, Snippet snippet) throws ReflectiveOperationException {
		try {
			lookup.findStaticVarHandle(snippet.type(), "viewer", GraphViewer.class); //$NON-NLS-1$
			return true;
		} catch (NoSuchFieldException ignore) {
			return false;
		}
	}

	/**
	 * Test using the {@link IGraphEntityContentProvider} when building a graph.
	 */
	@Test
	@Snippet(type = GraphJFaceSnippet1.class)
	public void testGraphJFaceSnippet1() {
		assertNotNull(graphRobot.getNode("First"));
		assertNotNull(graphRobot.getNode("Second"));
		assertNotNull(graphRobot.getNode("Third"));
		assertEquals(graphRobot.getNodes().size(), 3);

		assertNotNull(graphRobot.getConnection("Second", "Third"));
		assertNotNull(graphRobot.getConnection("Third", "First"));
		assertNotNull(graphRobot.getConnection("First", "Second"));
		assertEquals(graphRobot.getConnections().size(), 3);
	}

	/**
	 * Test using the {@link IGraphContentProvider} and {@link LabelProvider} when
	 * building a graph.
	 */
	@Test
	@Snippet(type = GraphJFaceSnippet2.class)
	public void testGraphJFaceSnippet2() {
		assertNotNull(graphRobot.getNode("Paper"));
		assertNotNull(graphRobot.getNode("Rock"));
		assertNotNull(graphRobot.getNode("Scissors"));
		assertEquals(graphRobot.getNodes().size(), 3);

		SWTBotGraphConnection connection1 = graphRobot.getConnection(0);
		SWTBotGraphConnection connection2 = graphRobot.getConnection(1);
		SWTBotGraphConnection connection3 = graphRobot.getConnection(2);
		assertEquals(graphRobot.getConnections().size(), 3);

		assertEquals(connection1.getSource().getText(), "Paper");
		assertEquals(connection1.getDestination().getText(), "Rock");
		assertEquals(connection2.getSource().getText(), "Scissors");
		assertEquals(connection2.getDestination().getText(), "Paper");
		assertEquals(connection3.getSource().getText(), "Rock");
		assertEquals(connection3.getDestination().getText(), "Scissors");

		assertEquals(connection1.getText(), "Rock2Paper");
		assertEquals(connection2.getText(), "Paper2Scissors");
		assertEquals(connection3.getText(), "Scissors2Rock");
	}

	/**
	 * Test reading the graph content from the file system.
	 */
	@Test
	@Snippet(type = GraphJFaceSnippet3.class)
	public void testGraphJFaceSnippet3() {
		// Explicitly checking 30 nodes and 29 connections is just busy work...
		assertEquals(graphRobot.getNodes().size(), 30);
		assertEquals(graphRobot.getConnections().size(), 29);
	}

	/**
	 * Test proper handling of selection events.
	 */
	@Test
	@Snippet(type = GraphJFaceSnippet4.class)
	public void testGraphJFaceSnippet4() {
		assertNotNull(graphRobot.getNode("Paper"));
		assertNotNull(graphRobot.getNode("Rock"));
		assertNotNull(graphRobot.getNode("Scissors"));
		assertEquals(graphRobot.getNodes().size(), 3);

		SWTBotGraphConnection connection1 = graphRobot.getConnection(0);
		SWTBotGraphConnection connection2 = graphRobot.getConnection(1);
		SWTBotGraphConnection connection3 = graphRobot.getConnection(2);
		assertEquals(graphRobot.getConnections().size(), 3);

		assertEquals(connection1.getSource().getText(), "Paper");
		assertEquals(connection1.getDestination().getText(), "Rock");
		assertEquals(connection2.getSource().getText(), "Scissors");
		assertEquals(connection2.getDestination().getText(), "Paper");
		assertEquals(connection3.getSource().getText(), "Rock");
		assertEquals(connection3.getDestination().getText(), "Scissors");

		assertEquals(connection1.getText(), "Rock2Paper");
		assertEquals(connection2.getText(), "Paper2Scissors");
		assertEquals(connection3.getText(), "Scissors2Rock");

		AtomicReference<Object> selection = new AtomicReference<>();
		viewer.addSelectionChangedListener(event -> {
			IStructuredSelection structuredSelection = event.getStructuredSelection();
			assertEquals(structuredSelection.size(), 1);
			Object data = structuredSelection.getFirstElement();
			selection.set(data);
		});

		for (SWTBotGraphNode node : graphRobot.getNodes()) {
			graphRobot.select(node);
			assertEquals(node.getData(), selection.get());
		}
	}

	/**
	 * Test refreshing the graph.
	 */
	@Test
	@Snippet(type = GraphJFaceSnippet5.class)
	public void testGraphJFaceSnippet5() {
		assertNotNull(graphRobot.getConnection("Paper", "Rock"));
		assertNotNull(graphRobot.getConnection("Scissors", "Paper"));
		assertNotNull(graphRobot.getConnection("Rock", "Scissors"));
		assertEquals(graphRobot.getConnections().size(), 3);
		assertEquals(graphRobot.getNodes().size(), 3);

		robot.button("Refresh").click();

		assertNotNull(graphRobot.getConnection("Paper", "Rock"));
		assertNotNull(graphRobot.getConnection("Scissors", "Paper"));
		assertNotNull(graphRobot.getConnection("Rock", "Scissors"));
		assertEquals(graphRobot.getConnections().size(), 3);
		assertEquals(graphRobot.getNodes().size(), 3);
	}

	/**
	 * The using the {@link INestedContentProvider} when building a nested graph.
	 */
	@Test
	@Snippet(type = GraphJFaceSnippet6.class)
	public void testGraphJFaceSnippet6() {
		assertNotNull(graphRobot.getNode("First"));
		assertNotNull(graphRobot.getNode("Second"));
		assertNotNull(graphRobot.getNode("Third"));
		assertEquals(graphRobot.getNodes().size(), 3);

		SWTBotGraphContainer container = (SWTBotGraphContainer) graphRobot.getNode(0);
		container.getNode("Rock");
		container.getNode("Paper");
		container.getNode("Scissors");
		assertEquals(container.getNodes().size(), 3);

		assertNotNull(graphRobot.getConnection("Rock", "Paper"));
		assertNotNull(graphRobot.getConnection("Second", "Third"));
		assertNotNull(graphRobot.getConnection("Second", "Rock"));
		assertNotNull(graphRobot.getConnection("Third", "First"));
		assertNotNull(graphRobot.getConnection("First", "Second"));
		assertEquals(graphRobot.getConnections().size(), 5);
	}

	/**
	 * Test building a graph with custom figures.
	 */
	@Test
	@Snippet(type = GraphJFaceSnippet7.class)
	public void testGraphJFaceSnippet7() {
		SWTBotGraphNode node1 = graphRobot.getNode(0);
		SWTBotGraphNode node2 = graphRobot.getNode(1);
		SWTBotGraphNode node3 = graphRobot.getNode(2);

		assertInstanceOf(node1.getNodeFigure(), ContainerFigure.class);
		assertInstanceOf(node2.getNodeFigure(), UMLClassFigure.class);
		assertInstanceOf(node3.getNodeFigure(), UMLClassFigure.class);
	}

	/**
	 * Test building a graph with curved edges.
	 */
	@Test
	@Snippet(type = GraphJFaceSnippet8.class)
	public void testGraphJFaceSnippet8() throws ReflectiveOperationException {
		assertNotNull(graphRobot.getNode("First"));
		assertNotNull(graphRobot.getNode("Second"));
		assertNotNull(graphRobot.getNode("Third"));
		assertEquals(graphRobot.getNodes().size(), 3);

		SWTBotGraphConnection connection1 = graphRobot.getConnection(0);
		SWTBotGraphConnection connection2 = graphRobot.getConnection(1);
		SWTBotGraphConnection connection3 = graphRobot.getConnection(2);
		SWTBotGraphConnection connection4 = graphRobot.getConnection(3);
		assertEquals(graphRobot.getConnections().size(), 4);

		assertEquals(connection1.getSource().getText(), "Third");
		assertEquals(connection1.getDestination().getText(), "Second");
		assertEquals(connection2.getSource().getText(), "Second");
		assertEquals(connection2.getDestination().getText(), "Third");
		assertEquals(connection3.getSource().getText(), "First");
		assertEquals(connection3.getDestination().getText(), "First");
		assertEquals(connection4.getSource().getText(), "First");
		assertEquals(connection4.getDestination().getText(), "Second");

		assertCurve(connection1, 20);
		assertCurve(connection2, 20);
		assertCurve(connection3, 40);
		assertCurve(connection4, 0);

		assertNoOverlap(graphRobot);
	}
}
