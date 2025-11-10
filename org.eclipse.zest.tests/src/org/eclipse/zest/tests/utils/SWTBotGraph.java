/*******************************************************************************
 * Copyright (c) 2025 Patrick Ziegler and others.
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
package org.eclipse.zest.tests.utils;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBotControl;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.HideNodeHelper;
import org.eclipse.zest.core.widgets.internal.GraphLabel;

import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.IFigure;

/**
 * Represents a Zest {@link Graph}.
 */
public class SWTBotGraph extends AbstractSWTBotControl<Graph> implements ISWTBotGraphContainer {
	/**
	 * Constructs an instance of this object with the given browser
	 *
	 * @param graph the widget.
	 * @throws WidgetNotFoundException if the widget is {@code null} or widget has
	 *                                 been disposed.
	 */
	public SWTBotGraph(Graph graph) throws WidgetNotFoundException {
		super(graph);
	}

	@Override
	public List<? extends GraphNode> internalGetNodes() {
		return syncExec(widget::getNodes);
	}

	/**
	 * This method simulates the selection of the given graph node. It first selects
	 * the given {@code node}, followed by sending an {@link SWT#Selection} event on
	 * the current {@link #widget}.
	 *
	 * @param node The graph node to select.
	 */
	public void select(SWTBotGraphNode node) {
		super.syncExec(() -> {
			Event event = createGraphMouseEvent(SWT.Selection, 1, 1);
			widget.setSelection(new GraphItem[] { node.getWidget() });
			widget.notifyListeners(event.type, event);
		});
	}

	/**
	 * This method simulates clicking the {@code hide} button of the
	 * {@link HideNodeHelper} figure, corresponding to the given node. The
	 * hide-nodes feature must be enabled on the current {@link #widget}.
	 *
	 * @param node The node to hide.
	 */
	public void clickHide(SWTBotGraphNode node) {
		syncExec(() -> {
			Assert.isTrue(node.getWidget().getGraphModel().getHideNodesEnabled(), "Hide-feature is not enabled"); //$NON-NLS-1$
			HideNodeHelper helper = node.getWidget().getHideNodeHelper();
			Clickable hideButton = (Clickable) helper.getChildren().get(0);
			hideButton.doClick();
		});
	}

	/**
	 * This method simulates clicking the {@code reveal} button of the
	 * {@link HideNodeHelper} figure, corresponding to the given node. The
	 * hide-nodes feature must be enabled on the current {@link #widget}.
	 *
	 * @param node The node to reveal.
	 */
	public void clickReveal(SWTBotGraphNode node) {
		syncExec(() -> {
			Assert.isTrue(node.getWidget().getGraphModel().getHideNodesEnabled(), "Hide-feature is not enabled"); //$NON-NLS-1$
			HideNodeHelper helper = node.getWidget().getHideNodeHelper();
			Clickable revealButton = (Clickable) helper.getChildren().get(1);
			revealButton.doClick();
		});
	}

	/**
	 * This method simulates a {@link SWT#KeyDown} event using the given
	 * {@code character}. Events are sent to the current {@link #widget} instance.
	 *
	 * @param character The character that has been typed.
	 */
	public final void keyDown(char character) {
		keyDown(0, character);
	}

	/**
	 * This method simulates a {@link SWT#KeyDown} event using the given
	 * {@code character} and {@code state mask}. Events are sent to the current
	 * {@link #widget} instance.
	 *
	 * @param stateMask The (optional) keyboard modified that has been pressed.
	 * @param character The character that has been typed.
	 */
	public void keyDown(int stateMask, char character) {
		syncExec(() -> {
			Event event = createGraphKeyEvent(SWT.KeyDown, stateMask, character);
			widget.notifyListeners(event.type, event);
		});
	}

	private static Event createGraphKeyEvent(int type, int stateMask, char character) {
		Event event = new Event();
		event.type = type;
		event.stateMask = stateMask;
		event.keyCode = character;
		event.character = character;
		return event;
	}

	/**
	 * This method simulates a {@link SWT#MouseHover} event using the given
	 * {@code x} and {@code y} coordinates. Events are sent to the current
	 * {@link #widget} instance. The coordinates must be inside the graph.
	 *
	 * @param x The x coordinate of the simulated mouse cursor.
	 * @param y The y coordinate of the simulated mouse cursor.
	 */
	public void mouseHover(int x, int y) {
		Assert.isTrue(getBounds().contains(x, y), "Coordinates are not inside the graph."); //$NON-NLS-1$
		syncExec(() -> {
			Event event = createGraphMouseEvent(SWT.MouseHover, x, y);
			widget.notifyListeners(event.type, event);
		});
	}

	/**
	 * This method simulates a {@link SWT#MouseMove} event using the given {@code x}
	 * and {@code y} coordinates. Events are sent to the current {@link #widget}
	 * instance. The coordinates must be inside the graph.
	 *
	 * @param x The x coordinate of the simulated mouse cursor.
	 * @param y The y coordinate of the simulated mouse cursor.
	 */
	public void mouseMove(int x, int y) {
		Assert.isTrue(getBounds().contains(x, y), "Coordinates are not inside the graph."); //$NON-NLS-1$
		syncExec(() -> {
			Event event = createGraphMouseEvent(SWT.MouseMove, x, y);
			widget.notifyListeners(event.type, event);
		});
	}

	/**
	 * This method simulates a {@link SWT#MouseDown} event using the given {@code x}
	 * and {@code y} coordinates. Events are sent to the current {@link #widget}
	 * instance. The coordinates must be inside the graph.
	 *
	 * @param x The x coordinate of the simulated mouse cursor.
	 * @param y The y coordinate of the simulated mouse cursor.
	 */
	public void mouseDown(int x, int y) {
		Assert.isTrue(getBounds().contains(x, y), "Coordinates are not inside the graph."); //$NON-NLS-1$
		syncExec(() -> {
			Event event = createGraphMouseEvent(SWT.MouseDown, x, y);
			widget.notifyListeners(event.type, event);
		});
	}

	private static Event createGraphMouseEvent(int type, int x, int y) {
		Event event = new Event();
		event.x = x;
		event.y = y;
		event.type = type;
		return event;
	}

	/**
	 * @param source      The name of the source node.
	 * @param destination The name of the destination node.
	 * @return The connection between {@code source} and {@code destination}.
	 */
	public SWTBotGraphConnection getConnection(String source, String destination) {
		return getConnectionsStream() //
				.filter(connection -> connection.getSource().getText().equals(source)) //
				.filter(connection -> connection.getDestination().getText().equals(destination)) //
				.findFirst().orElseThrow();
	}

	/**
	 * @param i The position of the graph connection.
	 * @return The graph connection at that position
	 * @throws IndexOutOfBoundsException If the index is invalid.
	 */
	public SWTBotGraphConnection getConnection(int i) throws IndexOutOfBoundsException {
		return getConnections().get(i);
	}

	/**
	 * @return An unmodifiable list of all connections in this graph.
	 */
	public List<SWTBotGraphConnection> getConnections() {
		return getConnectionsStream().toList();
	}

	private Stream<SWTBotGraphConnection> getConnectionsStream() {
		return internalGetConnections().stream().map(SWTBotGraphConnection::new);
	}

	private List<? extends GraphConnection> internalGetConnections() {
		return syncExec(widget::getConnections);
	}

	/**
	 * Selects all nodes in the graph.
	 */
	public void selectAll() {
		syncExec(widget::selectAll);
	}

	/**
	 * @return the figure at the location X, Y in the graph
	 */
	public IFigure getFigureAt(int x, int y) {
		return syncExec(() -> widget.getFigureAt(x, y));
	}

	/**
	 * Returns the fish-eye figure at the given coordinates. Note that those figures
	 * are on a separate layer.
	 *
	 * @param x The x coordinate of the fish-eye figure.
	 * @param y The x coordinate of the fish-eye figure.
	 */
	public GraphLabel getFishEyeFigureAt(int x, int y) {
		return syncExec(() -> {
			IFigure fishEyeLayer = widget.getRootLayer().getChildren().get(1);
			return (GraphLabel) fishEyeLayer.findFigureAt(x, y);
		});
	}

	/**
	 * Runs the layout on this graph.
	 */
	public void applyLayout() {
		syncExec(widget::applyLayout);
	}

	/**
	 * @return the bounds of this widget relative to its parent
	 */
	@Override
	public Rectangle getBounds() {
		return super.getBounds();
	}

	/**
	 * @return an unmodifiable list of currently selected graph items
	 */
	public List<? extends SWTBotGraphItem> getSelection() {
		return syncExec(widget::getSelection).stream().map(ISWTBotGraphContainer::internalConvert).toList();
	}
}
