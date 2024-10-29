/*******************************************************************************
 * Copyright (c) 2024 Patrick Ziegler and others.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.HideNodeHelper;
import org.eclipse.zest.core.widgets.internal.GraphLabel;
import org.eclipse.zest.core.widgets.internal.NodeSearchDialog;
import org.eclipse.zest.examples.swt.AnimationSnippet;
import org.eclipse.zest.examples.swt.CustomLayout;
import org.eclipse.zest.examples.swt.GraphSnippet1;
import org.eclipse.zest.examples.swt.GraphSnippet10;
import org.eclipse.zest.examples.swt.GraphSnippet11;
import org.eclipse.zest.examples.swt.GraphSnippet12;
import org.eclipse.zest.examples.swt.GraphSnippet13;
import org.eclipse.zest.examples.swt.GraphSnippet14;
import org.eclipse.zest.examples.swt.GraphSnippet2;
import org.eclipse.zest.examples.swt.GraphSnippet3;
import org.eclipse.zest.examples.swt.GraphSnippet4;
import org.eclipse.zest.examples.swt.GraphSnippet5;
import org.eclipse.zest.examples.swt.GraphSnippet6;
import org.eclipse.zest.examples.swt.GraphSnippet7;
import org.eclipse.zest.examples.swt.GraphSnippet8;
import org.eclipse.zest.examples.swt.GraphSnippet9;
import org.eclipse.zest.examples.swt.HelloWorld;
import org.eclipse.zest.examples.swt.LayoutExample;
import org.eclipse.zest.examples.swt.NestedGraphSnippet;
import org.eclipse.zest.examples.swt.NestedGraphSnippet2;
import org.eclipse.zest.examples.swt.PaintSnippet;
import org.eclipse.zest.examples.swt.ZoomSnippet;
import org.eclipse.zest.layouts.Filter;
import org.eclipse.zest.layouts.interfaces.ConnectionLayout;
import org.eclipse.zest.tests.utils.Snippet;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;

import org.junit.Ignore;
import org.junit.Test;

/**
 * This class instantiates the {@link Graph}-based Zest examples and tests the
 * correctness of the functionality they are supposed to show.
 */
@SuppressWarnings("nls")
public class GraphSWTTests extends AbstractGraphTest {

	@Override
	protected Graph getGraph(Lookup lookup, Snippet snippet) throws ReflectiveOperationException {
		VarHandle varHandle = lookup.findStaticVarHandle(snippet.type(), snippet.field(), Graph.class); // $NON-NLS-1$
		return (Graph) varHandle.get();
	}

	@Override
	protected boolean hasGraph(Lookup lookup, Snippet snippet) throws ReflectiveOperationException {
		try {
			lookup.findStaticVarHandle(snippet.type(), snippet.field(), Graph.class);
			return true;
		} catch (NoSuchFieldException ignore) {
			return false;
		}
	}

	/**
	 * Tests whether the animations are drawn.
	 *
	 * @see <a href="https://github.com/eclipse-gef/gef-classic/issues/376">here</a>
	 */
	@Test
	@Ignore
	@Snippet(type = AnimationSnippet.class)
	public void testAnimationSnippet() {
		AtomicInteger repaintCount = new AtomicInteger();
		graph.addPaintListener(event -> repaintCount.incrementAndGet());

		robot.button("Animate").click();

		assertTrue("Animation was likely not drawn!", repaintCount.get() > 0);
		assertNoOverlap(graph);
	}

	/**
	 * Tests whether custom layouts are properly executed. This layout simply lays
	 * the nodes out vertically on the same Y-Axis as they currently have.
	 */
	@Test
	@Snippet(type = CustomLayout.class)
	public void testCustomLayout() {
		GraphNode node1 = graph.getNodes().get(0);
		GraphNode node2 = graph.getNodes().get(1);
		GraphNode node3 = graph.getNodes().get(2);
		assertEquals(graph.getNodes().size(), 3);

		assertNode(node1, "Paper");
		assertNode(node2, "Rock");
		assertNode(node3, "Scissors");

		assertConnection(graph.getConnections().get(0), "Paper", "Rock");
		assertConnection(graph.getConnections().get(1), "Rock", "Scissors");
		assertConnection(graph.getConnections().get(2), "Scissors", "Paper");
		assertEquals(graph.getConnections().size(), 3);

		assertEquals(node1.getLocation().y, node2.getLocation().y);
		assertEquals(node1.getLocation().y, node3.getLocation().y);

		waitEventLoop(5);

		for (GraphNode node : graph.getNodes()) {
			Point p = node.getLocation();
			Rectangle bounds = graph.getBounds();
			assertTrue("Node outside of bounds", p.x >= 0 && p.x <= bounds.width);
			assertTrue("Node outside of bounds", p.y >= 0 && p.y <= bounds.height);
		}

		assertNoOverlap(graph);
	}

	/**
	 * Tests whether the correct graph nodes are selected by the
	 * {@link NodeSearchDialog}.
	 */
	@Test
	@Snippet(type = GraphSnippet1.class)
	public void testGraphSnippet1() {
		GraphNode node1 = graph.getNodes().get(0);
		GraphNode node2 = graph.getNodes().get(1);
		GraphNode node3 = graph.getNodes().get(2);
		assertEquals(graph.getNodes().size(), 3);

		assertNode(node1, "Paper");
		assertNode(node2, "Rock");
		assertNode(node3, "Scissors");

		Shell dialogShell = null;
		try {
			robot.keyDown(SWT.CONTROL, 'f');

			// The NodeSearchDialog should now be active
			dialogShell = graph.getDisplay().getActiveShell();
			SWTBot dialogRobot = new SWTBot(dialogShell);
			SWTBotText textRobot = dialogRobot.text();
			SWTBotButton buttonRobot = dialogRobot.button("Next");

			textRobot.setText("Paper");
			focusOut(textRobot);
			buttonRobot.click();
			assertEquals(graph.getSelection(), List.of(node1));

			textRobot.setText("Rock");
			focusOut(textRobot);
			buttonRobot.click();
			assertEquals(graph.getSelection(), List.of(node2));

			textRobot.setText("Scissors");
			focusOut(textRobot);
			buttonRobot.click();
			assertEquals(graph.getSelection(), List.of(node3));
		} finally {
			if (dialogShell != null) {
				dialogShell.dispose();
			}
		}
	}

	/**
	 * This method simulates a {@link SWT#FocusOut} event using the given
	 * {@code widget}. Events are sent to the argument.
	 *
	 * @param widget The widget whose focus has been lost.
	 */
	private static void focusOut(SWTBotText robot) {
		robot.widget.notifyListeners(SWT.FocusOut, new Event());
	}

	/**
	 * Tests whether images are properly handled.
	 */
	@Test
	@Snippet(type = GraphSnippet2.class)
	public void testGraphSnippet2() {
		GraphNode node1 = graph.getNodes().get(0);
		GraphNode node2 = graph.getNodes().get(1);
		GraphNode node3 = graph.getNodes().get(2);
		assertEquals(graph.getNodes().size(), 3);

		assertNode(node1, "Information");
		assertNode(node2, "Warning");
		assertNode(node3, "Error");

		assertConnection(graph.getConnections().get(0), "Information", "Warning");
		assertConnection(graph.getConnections().get(1), "Warning", "Error");
		assertConnection(graph.getConnections().get(2), "Error", "Error");
		assertEquals(graph.getConnections().size(), 3);

		Image image1 = Display.getDefault().getSystemImage(SWT.ICON_INFORMATION);
		Image image2 = Display.getDefault().getSystemImage(SWT.ICON_WARNING);
		Image image3 = Display.getDefault().getSystemImage(SWT.ICON_ERROR);

		assertEquals(node1.getImage(), image1);
		assertEquals(node2.getImage(), image2);
		assertEquals(node3.getImage(), image3);

		GraphLabel figure1 = (GraphLabel) node1.getNodeFigure();
		GraphLabel figure2 = (GraphLabel) node2.getNodeFigure();
		GraphLabel figure3 = (GraphLabel) node3.getNodeFigure();

		assertEquals(figure1.getIcon(), image1);
		assertEquals(figure2.getIcon(), image2);
		assertEquals(figure3.getIcon(), image3);
	}

	/**
	 * Test proper handling of selection events.
	 */
	@Test
	@Snippet(type = GraphSnippet3.class)
	public void testGraphSnippet3() {
		assertNode(graph.getNodes().get(0), "Information");
		assertNode(graph.getNodes().get(1), "Warning");
		assertNode(graph.getNodes().get(2), "Error");
		assertEquals(graph.getNodes().size(), 3);

		assertConnection(graph.getConnections().get(0), "Information", "Warning");
		assertConnection(graph.getConnections().get(1), "Warning", "Error");
		assertEquals(graph.getConnections().size(), 2);

		AtomicReference<Object> selection = new AtomicReference<>();
		graph.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				selection.set(event.data);
			}
		});

		for (GraphNode node : graph.getNodes()) {
			robot.select(node);
			assertEquals(node.getData(), selection.get());
		}
	}

	/**
	 * Tests custom tooltips when hovering over an edge.
	 */
	@Test
	@Snippet(type = GraphSnippet4.class)
	public void testGraphSnippet4() throws Throwable {
		GraphConnection connection = graph.getConnections().get(1);
		assertConnection(connection, "Warning", "Error");

		Connection connectionFigure = connection.getConnectionFigure();
		Point center = connectionFigure.getBounds().getCenter();

		robot.mouseHover(center.x, center.y);
		waitEventLoop(0);

		IFigure figure = graph.getFigureAt(center.x, center.y);
		Label label = (Label) getToolTip(figure);
		assertEquals(label.getText(), "Warning to Error");

		assertNoOverlap(graph);
	}

	/**
	 * Tests whether the graph is updated on user input.
	 */
	@Test
	@Snippet(type = GraphSnippet5.class)
	public void testGraphSnippet5() {
		GraphNode node1 = graph.getNodes().get(0);
		GraphNode node2 = graph.getNodes().get(1);
		GraphNode node3 = graph.getNodes().get(2);
		assertEquals(graph.getNodes().size(), 3);

		assertNode(node1, "org.eclipse.Information");
		assertNode(node2, "org.eclipse.Warning");
		assertNode(node3, "org.eclipse.Error");

		robot.keyDown('f');
		assertEquals(graph.getSelection(), List.of(node1));

		robot.keyDown(SWT.BS);
		robot.keyDown('w');
		assertEquals(graph.getSelection(), List.of(node2));

		robot.keyDown(SWT.BS);
		robot.keyDown('e');
		robot.keyDown('r');
		assertEquals(graph.getSelection(), List.of(node3));

		robot.keyDown(SWT.BS);
		robot.keyDown(SWT.BS);
		assertTrue(graph.getSelection().isEmpty());
	}

	/**
	 * Tests whether the fisheye figure is shown when hovering over a node.
	 */
	@Test
	@Snippet(type = GraphSnippet6.class)
	public void testGraphSnippet6() {
		assertEquals(graph.getNodes().size(), 240);

		List<String> labels = List.of("Information", "Warning", "Error");
		for (int i = 0; i < labels.size(); ++i) {
			GraphNode node = graph.getNodes().get(i);
			Point location = node.getLocation();

			GraphLabel figure = (GraphLabel) graph.getFigureAt(location.x + 1, location.y + 1);
			assertEquals(figure.getText(), "");

			robot.mouseMove(location.x + 1, location.y + 1);
			GraphLabel fishEyeFigure = getFishEyeFigure(location.x + 1, location.y + 1);
			assertEquals(fishEyeFigure.getText(), labels.get(i));
		}
	}

	/**
	 * Tests whether {@link Graph#getFigureAt(int, int)} can find both nodes and
	 * connections.
	 */
	@Test
	@Snippet(type = GraphSnippet7.class, random = true)
	public void testGraphSnippet7() {
		for (GraphNode node : graph.getNodes()) {
			IFigure nodeFigure = node.getNodeFigure();
			Point nodeCenter = nodeFigure.getBounds().getCenter();
			assertEquals(nodeFigure, graph.getFigureAt(nodeCenter.x, nodeCenter.y));
		}
		for (GraphConnection connection : graph.getConnections()) {
			IFigure connectionFigure = connection.getConnectionFigure();
			Point connectionCenter = connectionFigure.getBounds().getCenter();
			assertEquals(connectionFigure, graph.getFigureAt(connectionCenter.x, connectionCenter.y));
		}
	}

	/**
	 * Tests whether connections are hidden when the {@link Filter} is used.
	 */
	@Test
	@Snippet(type = GraphSnippet8.class, field = "graph")
	public void testGraphSnippet8() throws ReflectiveOperationException {
		assertEquals(graph.getConnections().size(), 13);
		assertNoOverlap(graph);

		ConnectionLayout[] connections = graph.getLayoutContext().getConnections();
		assertEquals(connections.length, 8);

		for (GraphConnection connection : graph.getConnections()) {
			connection.setData(Boolean.TRUE);
		}

		graph.applyLayout();
		waitEventLoop(0);

		connections = graph.getLayoutContext().getConnections();
		assertEquals(connections.length, 0);
	}

	/**
	 * Tests a graph whose nodes contain self-loops.
	 */
	@Test
	@Snippet(type = GraphSnippet9.class, field = "graph")
	public void testGraphSnippet9() {
		assertNode(graph.getNodes().get(0), "Root");
		assertEquals(graph.getNodes().size(), 1);

		GraphConnection connection = graph.getConnections().get(0);
		assertConnection(connection, "Root", "Root");
		assertEquals(connection.getText(), "A to A");
		assertEquals(graph.getConnections().size(), 1);

		assertNoOverlap(graph);
	}

	/**
	 * Tests a graph with curved connections. The curve changes on each button
	 * press.
	 */
	@Test
	@Snippet(type = GraphSnippet10.class, random = true)
	public void testGraphSnippet10() throws ReflectiveOperationException {
		assertNode(graph.getNodes().get(0), "Paper");
		assertNode(graph.getNodes().get(1), "Rock");
		assertNode(graph.getNodes().get(2), "Scissors");
		assertEquals(graph.getNodes().size(), 3);

		GraphConnection connection1 = graph.getConnections().get(0);
		GraphConnection connection2 = graph.getConnections().get(1);
		GraphConnection connection3 = graph.getConnections().get(2);
		assertEquals(graph.getConnections().size(), 3);

		assertConnection(connection1, "Paper", "Rock");
		assertConnection(connection2, "Rock", "Scissors");
		assertConnection(connection3, "Scissors", "Paper");

		for (int i = 0; i < 4; ++i) {
			if (i > 0) {
				robot.button("Change Curve").click();
			}
			// Old connection is removed and a new one is added
			assertCurve(graph.getConnections().get(2), i * 10);
		}
	}

	/**
	 * Tests a graph with curved connections.
	 */
	@Test
	@Snippet(type = GraphSnippet11.class, random = true)
	public void testGraphSnippet11() throws ReflectiveOperationException {
		assertNode(graph.getNodes().get(0), "Node 1");
		assertNode(graph.getNodes().get(1), "Node 2");
		assertEquals(graph.getNodes().size(), 2);
		assertEquals(graph.getConnections().size(), 7);

		for (GraphConnection connection : graph.getConnections()) {
			assertConnection(connection, "Node 1", "Node 2");
		}

		assertCurve(graph.getConnections().get(0), 20);
		assertCurve(graph.getConnections().get(1), -20);
		assertCurve(graph.getConnections().get(2), 40);
		assertCurve(graph.getConnections().get(3), -40);
		assertCurve(graph.getConnections().get(4), 60);
		assertCurve(graph.getConnections().get(5), -60);
		assertCurve(graph.getConnections().get(6), 0);

		assertNoOverlap(graph);
	}

	/**
	 * Tests the usage of complex node figures.
	 */
	@Test
	@Snippet(type = GraphSnippet12.class)
	public void testGraphSnippet12() {
		assertEquals(graph.getNodes().size(), 5);
		assertEquals(graph.getConnections().size(), 7);

		assertNode(graph.getNodes().get(2), "PDE");
		assertNode(graph.getNodes().get(3), "Zest");
		assertNode(graph.getNodes().get(4), "PDE Viz tool");

		GraphNode xz = graph.getNodes().get(0);
		IFigure xzFigure = xz.getNodeFigure();
		robot.select(xz);
		waitEventLoop(0);
		assertEquals(xzFigure.getForegroundColor(), ColorConstants.blue);
		assertEquals(xzFigure.getBackgroundColor(), ColorConstants.blue);
		assertEquals(xzFigure.getChildren().size(), 6);

		GraphNode ibull = graph.getNodes().get(1);
		IFigure ibullFigure = ibull.getNodeFigure();
		robot.select(ibull);
		waitEventLoop(0);
		assertEquals(ibullFigure.getForegroundColor(), ColorConstants.blue);
		assertEquals(ibullFigure.getBackgroundColor(), ColorConstants.blue);
		assertEquals(ibullFigure.getChildren().size(), 6);

		assertEquals(xzFigure.getForegroundColor(), ColorConstants.black);
		assertEquals(xzFigure.getBackgroundColor(), ColorConstants.black);
	}

	/**
	 * Tests the usage of advanced tooltips.
	 */
	@Test
	@Snippet(type = GraphSnippet13.class)
	public void testGraphSnippet13() throws Throwable {
		assertNode(graph.getNodes().get(0), "Canada");
		assertNode(graph.getNodes().get(1), "USA");
		assertEquals(graph.getNodes().size(), 2);
		assertEquals(graph.getConnections().size(), 2);

		GraphContainer container = (GraphContainer) graph.getNodes().get(1);
		container.open(false);
		waitEventLoop(0);

		assertNode(container.getNodes().get(0), "Chris A.");
		assertEquals(container.getNodes().size(), 1);

		GraphNode node = container.getNodes().get(0);
		IFigure nodeFigure = node.getNodeFigure();
		Point center = nodeFigure.getBounds().getCenter();
		nodeFigure.translateToAbsolute(center);

		robot.mouseHover(center.x, center.y);
		waitEventLoop(0);

		IFigure figure = graph.getFigureAt(center.x, center.y);
		IFigure tooltip = getToolTip(figure);
		assertEquals(tooltip.getChildren().size(), 3);
		assertEquals(((Label) tooltip.getChildren().get(1)).getText(), "Name: Chris Aniszczyk");
		assertEquals(((Label) tooltip.getChildren().get(2)).getText(), "Location: Austin, Texas");
	}

	/**
	 * Tests dynamically hiding or revealing nodes using the {@link HideNodeHelper}.
	 */
	@Test
	@Snippet(type = GraphSnippet14.class)
	public void testGraphSnippet14() {
		assertEquals(graph.getNodes().size(), 3);
		assertEquals(graph.getConnections().size(), 3);

		GraphNode node1 = graph.getNodes().get(0);
		GraphNode node2 = graph.getNodes().get(1);
		GraphNode node3 = graph.getNodes().get(2);

		assertTrue(node1.isVisible());
		assertTrue(node2.isVisible());
		assertTrue(node3.isVisible());

		robot.clickHide(node1);
		robot.clickHide(node3);
		graph.applyLayout();
		waitEventLoop(0);

		assertFalse(node1.isVisible());
		assertTrue(node2.isVisible());
		assertFalse(node3.isVisible());

		robot.clickReveal(node2);
		graph.applyLayout();
		waitEventLoop(0);

		assertTrue(node1.isVisible());
		assertTrue(node2.isVisible());
		assertTrue(node3.isVisible());
	}

	/**
	 * Test the obligatory "Hello World" example.
	 */
	@Test
	@Snippet(type = HelloWorld.class)
	public void testHelloWorld() {
		assertNode(graph.getNodes().get(0), "Hello");
		assertNode(graph.getNodes().get(1), "World");
		assertEquals(graph.getNodes().size(), 2);

		assertConnection(graph.getConnections().get(0), "Hello", "World");
		assertEquals(graph.getConnections().size(), 1);
	}

	/**
	 * Tests whether the layout is correctly calculated when using constraints.
	 */
	@Test
	@Snippet(type = LayoutExample.class)
	public void testLayoutExample() {
		double sumLengthInner = 0;
		int countInner = 0;
		double sumLengthOuter = 0;
		int countOuter = 0;

		for (GraphConnection connection : graph.getConnections()) {
			if ("Root".equals(connection.getSource().getText())) {
				sumLengthInner += getLength(connection);
				countInner++;
			} else {
				sumLengthOuter += getLength(connection);
				countOuter++;
			}
		}

		assertEquals(countInner, 3);
		assertEquals(countOuter, 9);

		double avgLengthInner = sumLengthInner / countInner;
		double avgLengthOuter = sumLengthOuter / countOuter;

		// The inner nodes have a higher weight and are thus (a lot) shorter
		assertTrue(avgLengthInner + ", " + avgLengthOuter, (avgLengthInner * 1.5) < avgLengthOuter);
	}

	/**
	 * Test whether nested nodes can be accessed correctly.
	 */
	@Test
	@Snippet(type = NestedGraphSnippet.class)
	public void testNestedGraphSnippet() {
		assertEquals(graph.getNodes().size(), 21);
		assertEquals(graph.getConnections().size(), 440);

		GraphContainer container = (GraphContainer) graph.getNodes().get(0);
		container.open(false);
		waitEventLoop(0);

		assertNode(container.getNodes().get(0), "SomeClass.java");
		assertEquals(container.getNodes().size(), 21);

		GraphNode node = container.getNodes().get(0);
		IFigure nodeFigure = node.getNodeFigure();

		Point nodeLocation = nodeFigure.getBounds().getCenter();
		nodeFigure.translateToAbsolute(nodeLocation);

		robot.mouseDown(nodeLocation.x, nodeLocation.y);
		assertEquals(graph.getSelection(), List.of(node));
	}

	/**
	 * Test whether nested nodes can be accessed correctly.
	 */
	@Test
	@Snippet(type = NestedGraphSnippet2.class)
	public void testNestedGraphSnippet2() {
		assertNode(graph.getNodes().get(0), "Machine 1 (prop:1)");
		assertNode(graph.getNodes().get(1), "Machine 2");
		assertNode(graph.getNodes().get(2), "Machine 3");
		assertEquals(graph.getNodes().size(), 3);

		GraphContainer container3 = (GraphContainer) graph.getNodes().get(2);
		container3.open(false);
		waitEventLoop(0);

		assertNode(container3.getNodes().get(0), "Host 4");
		assertEquals(container3.getNodes().size(), 1);

		GraphContainer container2 = (GraphContainer) container3.getNodes().get(0);
		container2.open(false);

		assertNode(container2.getNodes().get(0), "JSP Object 5");
		assertEquals(container2.getNodes().size(), 1);

		GraphNode node = container2.getNodes().get(0);
		IFigure nodeFigure = node.getNodeFigure();

		Point nodeLocation = nodeFigure.getBounds().getCenter();
		nodeFigure.translateToAbsolute(nodeLocation);

		robot.mouseDown(nodeLocation.x, nodeLocation.y);
		assertEquals(graph.getSelection(), List.of(node));

		GraphLabel nodeLabel = (GraphLabel) graph.getFigureAt(nodeLocation.x, nodeLocation.y);
		assertEquals(nodeLabel.getText(), "JSP Object 5");

		assertNoOverlap(container2);
		assertNoOverlap(container3);
	}

	/**
	 * Tests whether nodes are painted correctly.
	 */
	@Test
	@Snippet(type = PaintSnippet.class, random = true)
	public void testPaintSnippet() {
		assertEquals(graph.getNodes().size(), 3);

		graph.selectAll();
		waitEventLoop(0);

		robot.button("Take Screenshot").click();
		waitEventLoop(0);

		Shell popupShell = graph.getDisplay().getActiveShell();
		SWTBot popupRobot = new SWTBot(popupShell);
		Canvas popupCanvas = popupRobot.canvas(1).widget;

		Rectangle bounds1 = popupCanvas.getBounds();
		GC gc1 = new GC(popupCanvas);
		Image image1 = new Image(null, bounds1.width, bounds1.height);

		Rectangle bounds2 = graph.getBounds();
		GC gc2 = new GC(graph);
		Image image2 = new Image(null, bounds2.width, bounds2.height);

		try {
			gc1.copyArea(image1, 0, 0);
			ImageData imageData1 = image1.getImageData();

			gc2.copyArea(image2, 0, 0);
			ImageData imageData2 = image2.getImageData();

			for (GraphNode node : graph.getNodes()) {
				Point location = node.getNodeFigure().getBounds().getCenter();
				int pixelValue1 = imageData2.getPixel(location.x, location.y);
				RGB pixelColor1 = imageData1.palette.getRGB(pixelValue1);

				int pixelValue2 = imageData2.getPixel(location.x, location.y);
				RGB pixelColor2 = imageData2.palette.getRGB(pixelValue2);

				assertEquals(pixelColor1, pixelColor2);
			}
		} finally {
			gc1.dispose();
			image1.dispose();
			gc2.dispose();
			image2.dispose();
			popupShell.close();
			popupShell.dispose();
		}
	}

	/**
	 * Test whether nested nodes can be accessed correctly (This time check fisheye
	 * figure).
	 */
	@Test
	@Snippet(type = ZoomSnippet.class)
	public void testZoomSnippet() {
		assertEquals(graph.getNodes().size(), 21);
		assertEquals(graph.getConnections().size(), 440);

		GraphContainer container = (GraphContainer) graph.getNodes().get(0);
		robot.select(container);
		robot.keyDown(SWT.SPACE);
		container.open(false);

		graph.applyLayout();
		waitEventLoop(0);

		GraphNode node = container.getNodes().get(0);
		robot.select(node);

		graph.applyLayout();
		waitEventLoop(0);

		IFigure nodeFigure = node.getNodeFigure();
		Point location = nodeFigure.getBounds().getCenter();
		nodeFigure.translateToAbsolute(location);

		robot.mouseMove(location.x, location.y);
		GraphLabel fishEyeFigure = getFishEyeFigure(location.x + 1, location.y + 1);
		assertEquals(fishEyeFigure.getText(), "SomeClass.java");
	}
}
