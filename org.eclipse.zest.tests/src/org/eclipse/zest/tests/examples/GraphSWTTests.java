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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBotControl;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.zest.core.widgets.Graph;
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
import org.eclipse.zest.tests.utils.SWTBotGraphConnection;
import org.eclipse.zest.tests.utils.SWTBotGraphContainer;
import org.eclipse.zest.tests.utils.SWTBotGraphNode;
import org.eclipse.zest.tests.utils.Snippet;

import org.eclipse.draw2d.Animation;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;

import org.junit.jupiter.api.Test;

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
	@Snippet(type = AnimationSnippet.class)
	public void testAnimationSnippet() {
		AtomicInteger repaintCount = new AtomicInteger();
		UIThreadRunnable.syncExec(() -> graphRobot.widget.addPaintListener(event -> repaintCount.incrementAndGet()));

		robot.button("Animate").click();

		while (Animation.isAnimating()) {
			Thread.yield();
		}

		assertTrue(repaintCount.get() > 0, "Animation was likely not drawn!");
		assertNoOverlap(graphRobot);
	}

	/**
	 * Tests whether custom layouts are properly executed. This layout simply lays
	 * the nodes out vertically on the same Y-Axis as they currently have.
	 */
	@Test
	@Snippet(type = CustomLayout.class)
	public void testCustomLayout() {
		SWTBotGraphNode node1 = graphRobot.getNode("Paper");
		SWTBotGraphNode node2 = graphRobot.getNode("Rock");
		SWTBotGraphNode node3 = graphRobot.getNode("Scissors");
		assertEquals(graphRobot.getNodes().size(), 3);

		assertNotNull(graphRobot.getConnection("Paper", "Rock"));
		assertNotNull(graphRobot.getConnection("Rock", "Scissors"));
		assertNotNull(graphRobot.getConnection("Scissors", "Paper"));
		assertEquals(graphRobot.getConnections().size(), 3);

		assertEquals(node1.getLocation().y, node2.getLocation().y);
		assertEquals(node1.getLocation().y, node3.getLocation().y);

		for (SWTBotGraphNode node : graphRobot.getNodes()) {
			Point p = node.getLocation();
			Rectangle bounds = graphRobot.getBounds();
			assertTrue(p.x >= 0 && p.x <= bounds.width, "Node outside of bounds");
			assertTrue(p.y >= 0 && p.y <= bounds.height, "Node outside of bounds");
		}

		assertNoOverlap(graphRobot);
	}

	/**
	 * Tests whether the correct graph nodes are selected by the
	 * {@link NodeSearchDialog}.
	 */
	@Test
	@Snippet(type = GraphSnippet1.class)
	public void testGraphSnippet1() {
		SWTBotGraphNode node1 = graphRobot.getNode("Paper");
		SWTBotGraphNode node2 = graphRobot.getNode("Rock");
		SWTBotGraphNode node3 = graphRobot.getNode("Scissors");
		assertEquals(graphRobot.getNodes().size(), 3);

		SWTBotShell dialog = null;

		try {
			graphRobot.keyDown(SWT.CONTROL, 'f');

			// The NodeSearchDialog should now be active
			dialog = robot.activeShell();
			SWTBot dialogRobot = dialog.bot();
			SWTBotText textRobot = dialogRobot.text();
			SWTBotButton buttonRobot = dialogRobot.button("Next");

			textRobot.setText("Paper");
			focusOut(textRobot);
			buttonRobot.click();
			assertEquals(graphRobot.getSelection(), List.of(node1));

			textRobot.setText("Rock");
			focusOut(textRobot);
			buttonRobot.click();
			assertEquals(graphRobot.getSelection(), List.of(node2));

			textRobot.setText("Scissors");
			focusOut(textRobot);
			buttonRobot.click();
			assertEquals(graphRobot.getSelection(), List.of(node3));
		} finally {
			if (dialog != null) {
				dialog.close();
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
		UIThreadRunnable.syncExec(() -> robot.widget.notifyListeners(SWT.FocusOut, new Event()));
	}

	/**
	 * Tests whether images are properly handled.
	 */
	@Test
	@Snippet(type = GraphSnippet2.class)
	public void testGraphSnippet2() {
		SWTBotGraphNode node1 = graphRobot.getNode("Information");
		SWTBotGraphNode node2 = graphRobot.getNode("Warning");
		SWTBotGraphNode node3 = graphRobot.getNode("Error");
		assertEquals(graphRobot.getNodes().size(), 3);

		assertNotNull(graphRobot.getConnection("Information", "Warning"));
		assertNotNull(graphRobot.getConnection("Warning", "Error"));
		assertNotNull(graphRobot.getConnection("Error", "Error"));
		assertEquals(graphRobot.getConnections().size(), 3);

		Image image1 = UIThreadRunnable.syncExec(() -> Display.getCurrent().getSystemImage(SWT.ICON_INFORMATION));
		Image image2 = UIThreadRunnable.syncExec(() -> Display.getCurrent().getSystemImage(SWT.ICON_WARNING));
		Image image3 = UIThreadRunnable.syncExec(() -> Display.getCurrent().getSystemImage(SWT.ICON_ERROR));

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
		assertNotNull(graphRobot.getNode("Information"));
		assertNotNull(graphRobot.getNode("Warning"));
		assertNotNull(graphRobot.getNode("Error"));
		assertEquals(graphRobot.getNodes().size(), 3);

		assertNotNull(graphRobot.getConnection("Information", "Warning"));
		assertNotNull(graphRobot.getConnection("Warning", "Error"));
		assertEquals(graphRobot.getConnections().size(), 2);

		AtomicReference<Object> selection = new AtomicReference<>();
		graphRobot.widget.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				selection.set(event.data);
			}
		});

		for (SWTBotGraphNode node : graphRobot.getNodes()) {
			graphRobot.select(node);
			assertEquals(node.getData(), selection.get());
		}
	}

	/**
	 * Tests custom tooltips when hovering over an edge.
	 */
	@Test
	@Snippet(type = GraphSnippet4.class)
	public void testGraphSnippet4() throws Throwable {
		SWTBotGraphConnection connection = graphRobot.getConnection("Warning", "Error");

		Connection connectionFigure = connection.getConnectionFigure();
		Point center = connectionFigure.getBounds().getCenter();

		graphRobot.mouseHover(center.x, center.y);

		IFigure figure = graphRobot.getFigureAt(center.x, center.y);
		Label label = (Label) getToolTip(figure);
		assertEquals(label.getText(), "Warning to Error");

		assertNoOverlap(graphRobot);
	}

	/**
	 * Tests whether the graph is updated on user input.
	 */
	@Test
	@Snippet(type = GraphSnippet5.class)
	public void testGraphSnippet5() {
		SWTBotGraphNode node1 = graphRobot.getNode("org.eclipse.Information");
		SWTBotGraphNode node2 = graphRobot.getNode("org.eclipse.Warning");
		SWTBotGraphNode node3 = graphRobot.getNode("org.eclipse.Error");
		assertEquals(graphRobot.getNodes().size(), 3);

		graphRobot.keyDown('f');
		assertEquals(graphRobot.getSelection(), List.of(node1));

		graphRobot.keyDown(SWT.BS);
		graphRobot.keyDown('w');
		assertEquals(graphRobot.getSelection(), List.of(node2));

		graphRobot.keyDown(SWT.BS);
		graphRobot.keyDown('e');
		graphRobot.keyDown('r');
		assertEquals(graphRobot.getSelection(), List.of(node3));

		graphRobot.keyDown(SWT.BS);
		graphRobot.keyDown(SWT.BS);
		assertTrue(graphRobot.getSelection().isEmpty());
	}

	/**
	 * Tests whether the fisheye figure is shown when hovering over a node.
	 */
	@Test
	@Snippet(type = GraphSnippet6.class)
	public void testGraphSnippet6() {
		assertEquals(graphRobot.getNodes().size(), 240);

		List<String> labels = List.of("Information", "Warning", "Error");
		for (int i = 0; i < labels.size(); ++i) {
			SWTBotGraphNode node = graphRobot.getNode(i);
			Point location = node.getLocation();

			GraphLabel figure = (GraphLabel) graphRobot.getFigureAt(location.x + 1, location.y + 1);
			assertEquals(figure.getText(), "");

			graphRobot.mouseMove(location.x + 1, location.y + 1);
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
		for (SWTBotGraphNode node : graphRobot.getNodes()) {
			IFigure nodeFigure = node.getNodeFigure();
			Point nodeCenter = nodeFigure.getBounds().getCenter();
			assertEquals(nodeFigure, graphRobot.getFigureAt(nodeCenter.x, nodeCenter.y));
		}
		for (SWTBotGraphConnection connection : graphRobot.getConnections()) {
			IFigure connectionFigure = connection.getConnectionFigure();
			Point connectionCenter = connectionFigure.getBounds().getCenter();
			assertEquals(connectionFigure, graphRobot.getFigureAt(connectionCenter.x, connectionCenter.y));
		}
	}

	/**
	 * Tests whether connections are hidden when the {@link Filter} is used.
	 */
	@Test
	@Snippet(type = GraphSnippet8.class, field = "graph")
	public void testGraphSnippet8() {
		assertEquals(graphRobot.getConnections().size(), 13);
		assertNoOverlap(graphRobot);

		ConnectionLayout[] connections = UIThreadRunnable
				.syncExec(() -> graphRobot.widget.getLayoutContext().getConnections());
		assertEquals(connections.length, 8);

		for (SWTBotGraphConnection connection : graphRobot.getConnections()) {
			connection.setData(Boolean.TRUE);
		}

		connections = UIThreadRunnable.syncExec(() -> graphRobot.widget.getLayoutContext().getConnections());
		assertEquals(connections.length, 0);
	}

	/**
	 * Tests a graph whose nodes contain self-loops.
	 */
	@Test
	@Snippet(type = GraphSnippet9.class, field = "graph")
	public void testGraphSnippet9() {
		assertNotNull(graphRobot.getNode("Root"));
		assertEquals(graphRobot.getNodes().size(), 1);

		SWTBotGraphConnection connection = graphRobot.getConnection("Root", "Root");
		assertEquals(connection.getText(), "A to A");
		assertEquals(graphRobot.getConnections().size(), 1);

		assertNoOverlap(graphRobot);
	}

	/**
	 * Tests a graph with curved connections. The curve changes on each button
	 * press.
	 */
	@Test
	@Snippet(type = GraphSnippet10.class, random = true)
	public void testGraphSnippet10() throws ReflectiveOperationException {
		assertNotNull(graphRobot.getNode("Paper"));
		assertNotNull(graphRobot.getNode("Rock"));
		assertNotNull(graphRobot.getNode("Scissors"));
		assertEquals(graphRobot.getNodes().size(), 3);

		assertNotNull(graphRobot.getConnection("Paper", "Rock"));
		assertNotNull(graphRobot.getConnection("Rock", "Scissors"));
		assertNotNull(graphRobot.getConnection("Scissors", "Paper"));
		assertEquals(graphRobot.getConnections().size(), 3);

		for (int i = 0; i < 4; ++i) {
			if (i > 0) {
				robot.button("Change Curve").click();
			}
			// Old connection is removed and a new one is added
			assertCurve(graphRobot.getConnections().get(2), i * 10);
		}
	}

	/**
	 * Tests a graph with curved connections.
	 */
	@Test
	@Snippet(type = GraphSnippet11.class, random = true)
	public void testGraphSnippet11() throws ReflectiveOperationException {
		assertNotNull(graphRobot.getNode("Node 1"));
		assertNotNull(graphRobot.getNode("Node 2"));
		assertEquals(graphRobot.getNodes().size(), 2);
		assertEquals(graphRobot.getConnections().size(), 7);

		for (SWTBotGraphConnection connection : graphRobot.getConnections()) {
			assertEquals(connection.getSource().getText(), "Node 1");
			assertEquals(connection.getDestination().getText(), "Node 2");
		}

		assertCurve(graphRobot.getConnections().get(0), 20);
		assertCurve(graphRobot.getConnections().get(1), -20);
		assertCurve(graphRobot.getConnections().get(2), 40);
		assertCurve(graphRobot.getConnections().get(3), -40);
		assertCurve(graphRobot.getConnections().get(4), 60);
		assertCurve(graphRobot.getConnections().get(5), -60);
		assertCurve(graphRobot.getConnections().get(6), 0);

		assertNoOverlap(graphRobot);
	}

	/**
	 * Tests the usage of complex node figures.
	 */
	@Test
	@Snippet(type = GraphSnippet12.class)
	public void testGraphSnippet12() {
		assertEquals(graphRobot.getNodes().size(), 5);
		assertEquals(graphRobot.getConnections().size(), 7);

		assertNotNull(graphRobot.getNode("PDE"));
		assertNotNull(graphRobot.getNode("Zest"));
		assertNotNull(graphRobot.getNode("PDE Viz tool"));

		SWTBotGraphNode xz = graphRobot.getNode(0);
		IFigure xzFigure = xz.getNodeFigure();
		graphRobot.select(xz);
		assertEquals(xzFigure.getForegroundColor(), ColorConstants.blue);
		assertEquals(xzFigure.getBackgroundColor(), ColorConstants.blue);
		assertEquals(xzFigure.getChildren().size(), 6);

		SWTBotGraphNode ibull = graphRobot.getNode(1);
		IFigure ibullFigure = ibull.getNodeFigure();
		graphRobot.select(ibull);
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
		assertNotNull(graphRobot.getNode("Canada"));
		assertNotNull(graphRobot.getNode("USA"));
		assertEquals(graphRobot.getNodes().size(), 2);
		assertEquals(graphRobot.getConnections().size(), 2);

		SWTBotGraphContainer container = (SWTBotGraphContainer) graphRobot.getNode(1);
		container.open(false);

		assertNotNull(container.getNode("Chris A."));
		assertEquals(container.getNodes().size(), 1);

		SWTBotGraphNode node = container.getNode(0);
		IFigure nodeFigure = node.getNodeFigure();
		Point center = nodeFigure.getBounds().getCenter();
		nodeFigure.translateToAbsolute(center);

		graphRobot.mouseHover(center.x, center.y);

		IFigure figure = graphRobot.getFigureAt(center.x, center.y);
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
		assertEquals(graphRobot.getNodes().size(), 3);
		assertEquals(graphRobot.getConnections().size(), 3);

		SWTBotGraphNode node1 = graphRobot.getNode(0);
		SWTBotGraphNode node2 = graphRobot.getNode(1);
		SWTBotGraphNode node3 = graphRobot.getNode(2);

		assertTrue(node1.isVisible());
		assertTrue(node2.isVisible());
		assertTrue(node3.isVisible());

		graphRobot.clickHide(node1);
		graphRobot.clickHide(node3);
		graphRobot.applyLayout();

		assertFalse(node1.isVisible());
		assertTrue(node2.isVisible());
		assertFalse(node3.isVisible());

		graphRobot.clickReveal(node2);
		graphRobot.applyLayout();

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
		assertNotNull(graphRobot.getNode("Hello"));
		assertNotNull(graphRobot.getNode("World"));
		assertEquals(graphRobot.getNodes().size(), 2);

		assertNotNull(graphRobot.getConnection("Hello", "World"));
		assertEquals(graphRobot.getConnections().size(), 1);
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

		for (SWTBotGraphConnection connection : graphRobot.getConnections()) {
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
		assertTrue((avgLengthInner * 1.5) < avgLengthOuter, avgLengthInner + ", " + avgLengthOuter);
	}

	/**
	 * Test whether nested nodes can be accessed correctly.
	 */
	@Test
	@Snippet(type = NestedGraphSnippet.class)
	public void testNestedGraphSnippet() {
		assertEquals(graphRobot.getNodes().size(), 21);
		assertEquals(graphRobot.getConnections().size(), 440);

		SWTBotGraphContainer container = (SWTBotGraphContainer) graphRobot.getNode(0);
		container.open(false);

		assertNotNull(container.getNode("SomeClass.java"));
		assertEquals(container.getNodes().size(), 21);

		SWTBotGraphNode node = container.getNode(0);
		IFigure nodeFigure = node.getNodeFigure();

		Point nodeLocation = nodeFigure.getBounds().getCenter();
		nodeFigure.translateToAbsolute(nodeLocation);

		graphRobot.mouseDown(nodeLocation.x, nodeLocation.y);
		assertEquals(graphRobot.getSelection(), List.of(node));
	}

	/**
	 * Test whether nested nodes can be accessed correctly.
	 */
	@Test
	@Snippet(type = NestedGraphSnippet2.class)
	public void testNestedGraphSnippet2() {
		assertNotNull(graphRobot.getNode("Machine 1 (prop:1)"));
		assertNotNull(graphRobot.getNode("Machine 2"));
		assertNotNull(graphRobot.getNode("Machine 3"));
		assertEquals(graphRobot.getNodes().size(), 3);

		SWTBotGraphContainer container3 = (SWTBotGraphContainer) graphRobot.getNode(2);
		container3.open(false);

		assertNotNull(container3.getNode("Host 4"));
		assertEquals(container3.getNodes().size(), 1);

		SWTBotGraphContainer container2 = (SWTBotGraphContainer) container3.getNode(0);
		container2.open(false);

		assertNotNull(container2.getNode("JSP Object 5"));
		assertEquals(container2.getNodes().size(), 1);

		SWTBotGraphNode node = container2.getNode(0);
		IFigure nodeFigure = node.getNodeFigure();

		Point nodeLocation = nodeFigure.getBounds().getCenter();
		nodeFigure.translateToAbsolute(nodeLocation);

		graphRobot.mouseDown(nodeLocation.x, nodeLocation.y);
		assertEquals(graphRobot.getSelection(), List.of(node));

		GraphLabel nodeLabel = (GraphLabel) graphRobot.getFigureAt(nodeLocation.x, nodeLocation.y);
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
		assertEquals(graphRobot.getNodes().size(), 3);

		graphRobot.selectAll();

		robot.button("Take Screenshot").click();

		SWTBot popupRobot = robot.activeShell().bot();
		ImageData imageData1 = takeScreenShot(popupRobot.canvas(1));
		ImageData imageData2 = takeScreenShot(graphRobot);

		for (SWTBotGraphNode node : graphRobot.getNodes()) {
			Point location = node.getNodeFigure().getBounds().getCenter();
			int pixelValue1 = imageData2.getPixel(location.x, location.y);
			RGB pixelColor1 = imageData1.palette.getRGB(pixelValue1);

			int pixelValue2 = imageData2.getPixel(location.x, location.y);
			RGB pixelColor2 = imageData2.palette.getRGB(pixelValue2);

			assertEquals(pixelColor1, pixelColor2);
		}
	}

	private static ImageData takeScreenShot(AbstractSWTBotControl<?> bot) {
		return UIThreadRunnable.syncExec(() -> {
			Control w = bot.widget;
			Rectangle bounds = w.getBounds();

			GC gc = new GC(w);
			Image image = new Image(null, bounds.width, bounds.height);
			try {
				gc.copyArea(image, 0, 0);
				return image.getImageData();
			} finally {
				image.dispose();
				gc.dispose();
			}
		});
	}

	/**
	 * Test whether nested nodes can be accessed correctly (This time check fisheye
	 * figure).
	 */
	@Test
	@Snippet(type = ZoomSnippet.class)
	public void testZoomSnippet() {
		assertEquals(graphRobot.getNodes().size(), 21);
		assertEquals(graphRobot.getConnections().size(), 440);

		SWTBotGraphContainer container = (SWTBotGraphContainer) graphRobot.getNode(0);
		graphRobot.select(container);
		graphRobot.keyDown(SWT.SPACE);
		container.open(false);

		SWTBotGraphNode node = container.getNode(0);
		graphRobot.select(node);

		IFigure nodeFigure = node.getNodeFigure();
		Point location = nodeFigure.getBounds().getCenter();
		nodeFigure.translateToAbsolute(location);

		graphRobot.mouseMove(location.x, location.y);
		GraphLabel fishEyeFigure = getFishEyeFigure(location.x + 1, location.y + 1);
		assertEquals(fishEyeFigure.getText(), "SomeClass.java");
	}
}
