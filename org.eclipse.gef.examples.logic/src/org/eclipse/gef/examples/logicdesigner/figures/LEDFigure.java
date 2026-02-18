/*******************************************************************************
 * Copyright (c) 2000, 2022 IBM Corporation and others.
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
package org.eclipse.gef.examples.logicdesigner.figures;

import static org.eclipse.draw2d.FigureUtilities.getTextExtents;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.handles.HandleBounds;

import org.eclipse.gef.examples.logicdesigner.model.LED;

/**
 * @author danlee
 */
public class LEDFigure extends NodeFigure implements HandleBounds {

	public static final Dimension SIZE = new Dimension(92, 71);
	protected static PointList connector = new PointList();
	protected static PointList bottomConnector = new PointList();
	protected static Rectangle displayRectangle = new Rectangle(14, 17, 65, 38);
	protected static Rectangle displayShadow = new Rectangle(12, 15, 66, 39);
	protected static Rectangle displayHighlight = new Rectangle(14, 17, 66, 39);
	protected static Point valuePoint = new Point(24, 15);
	private static final int HORIZONTAL_PADDING = 3;
	private static final int CORNER_RADIUS = 6;

	static {
		connector.addPoint(-3, 0);
		connector.addPoint(2, 0);
		connector.addPoint(3, 2);
		connector.addPoint(3, 8);
		connector.addPoint(-1, 8);
		connector.addPoint(-1, 2);

		bottomConnector.addPoint(-3, 0);
		bottomConnector.addPoint(2, 0);
		bottomConnector.addPoint(3, -1);
		bottomConnector.addPoint(3, -7);
		bottomConnector.addPoint(-1, -7);
		bottomConnector.addPoint(-1, -1);
	}

	protected static final int[] GAP_CENTERS_X = { 12, 35, 57, 80 };
	protected static final int Y1 = 3;
	protected static final int Y2 = 66;

	protected String value;

	/**
	 * Creates a new LEDFigure
	 */
	public LEDFigure() {
		FixedConnectionAnchor c;
		c = new FixedConnectionAnchor(this);
		c.offsetH = 77;
		connectionAnchors.put(LED.TERMINAL_1_IN, c);
		inputConnectionAnchors.add(c);
		c = new FixedConnectionAnchor(this);
		c.offsetH = 54;
		connectionAnchors.put(LED.TERMINAL_2_IN, c);
		inputConnectionAnchors.add(c);
		c = new FixedConnectionAnchor(this);
		c.offsetH = 32;
		connectionAnchors.put(LED.TERMINAL_3_IN, c);
		inputConnectionAnchors.add(c);
		c = new FixedConnectionAnchor(this);
		c.offsetH = 9;
		connectionAnchors.put(LED.TERMINAL_4_IN, c);
		inputConnectionAnchors.add(c);
		c = new FixedConnectionAnchor(this);
		c.offsetH = 77;
		c.topDown = false;
		connectionAnchors.put(LED.TERMINAL_1_OUT, c);
		outputConnectionAnchors.add(c);
		c = new FixedConnectionAnchor(this);
		c.offsetH = 54;
		c.topDown = false;
		connectionAnchors.put(LED.TERMINAL_2_OUT, c);
		outputConnectionAnchors.add(c);
		c = new FixedConnectionAnchor(this);
		c.offsetH = 32;
		c.topDown = false;
		connectionAnchors.put(LED.TERMINAL_3_OUT, c);
		outputConnectionAnchors.add(c);
		c = new FixedConnectionAnchor(this);
		c.offsetH = 9;
		c.topDown = false;
		connectionAnchors.put(LED.TERMINAL_4_OUT, c);
		outputConnectionAnchors.add(c);
	}

	/**
	 * @see org.eclipse.gef.handles.HandleBounds#getHandleBounds()
	 */
	@Override
	public Rectangle getHandleBounds() {
		return getBounds().getShrinked(new Insets(3, 0, 3, 0));
	}

	/**
	 * @see org.eclipse.draw2d.Figure#getPreferredSize(int, int)
	 */
	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		return SIZE;
	}

	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
	 */
	@Override
	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());

		g.setBackgroundColor(LogicColorConstants.logicGreen);
		Rectangle mainBody = new Rectangle(0, 2, r.width, r.height - 4);
		g.fillRoundRectangle(mainBody, CORNER_RADIUS, CORNER_RADIUS);
		drawConnectors(g, r);

		// Draw display
		g.setBackgroundColor(ColorConstants.black);
		g.fillRoundRectangle(displayRectangle, CORNER_RADIUS, CORNER_RADIUS);

		// Draw text
		if (value != null) {
			drawModernText(g);
		}

	}

	private static void drawConnectors(Graphics g, Rectangle r) {
		for (int i = 0; i < 4; i++) {
			// Draw the gaps for the connectors
			g.setForegroundColor(ColorConstants.listBackground);

			// Draw the connectors
			g.setForegroundColor(LogicColorConstants.connectorGreen);
			g.setBackgroundColor(LogicColorConstants.connectorGreen);

			connector.translate(GAP_CENTERS_X[i], 0);
			g.fillPolygon(connector);
			connector.translate(-GAP_CENTERS_X[i], 0);

			bottomConnector.translate(GAP_CENTERS_X[i], r.height - 1);
			g.fillPolygon(bottomConnector);
			bottomConnector.translate(-GAP_CENTERS_X[i], -(r.height - 1));
		}
	}

	private void drawModernText(Graphics g) {
		// Calculate centered position within display including padding
		Dimension textExtents = getTextExtents(value, getFont());
		int x = displayRectangle.x + HORIZONTAL_PADDING
				+ ((displayRectangle.width - 2 * HORIZONTAL_PADDING) - textExtents.width) / 2;
		int y = displayRectangle.y + (displayRectangle.height - textExtents.height) / 2;

		// Draw the value
		g.setForegroundColor(LogicColorConstants.displayTextLED);
		g.drawText(value, x, y);
	}

	/**
	 * Sets the value of the LEDFigure to val.
	 *
	 * @param val The value to set on this LEDFigure
	 */
	public void setValue(int val) {
		String newValue = String.valueOf(val);
		if (val < 10) {
			newValue = "0" + newValue; //$NON-NLS-1$
		}
		if (newValue.equals(value)) {
			return;
		}
		value = newValue;
		repaint();
	}

	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LEDFigure"; //$NON-NLS-1$
	}

}