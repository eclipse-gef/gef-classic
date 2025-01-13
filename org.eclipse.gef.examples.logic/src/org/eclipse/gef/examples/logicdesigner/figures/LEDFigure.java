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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

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
	public static final Dimension SIZE = new Dimension(122, 94);

	/**
	 * Color of the shadow around the LEDFigure's display
	 */
	public static final Color DISPLAY_SHADOW = new Color(null, 57, 117, 90);

	/**
	 * Color of the LEDFigure's displayed value
	 */
	public static final Color DISPLAY_TEXT = new Color(null, 255, 199, 16);

	protected static final Font DISPLAY_FONT = new Font(null, "", 38, 0); //$NON-NLS-1$
	protected static PointList connector = new PointList();
	protected static PointList bottomConnector = new PointList();
	protected static Rectangle displayRectangle = new Rectangle(30, 22, 62, 50);
	protected static Rectangle displayShadow = new Rectangle(28, 20, 64, 52);
	protected static Rectangle displayHighlight = new Rectangle(30, 22, 64, 52);
	protected static Point valuePoint = new Point(32, 20);

	static {
		connector.addPoint(-4, 0);
		connector.addPoint(2, 0);
		connector.addPoint(4, 2);
		connector.addPoint(4, 10);
		connector.addPoint(-2, 10);
		connector.addPoint(-2, 2);

		bottomConnector.addPoint(-4, 0);
		bottomConnector.addPoint(2, 0);
		bottomConnector.addPoint(4, -2);
		bottomConnector.addPoint(4, -10);
		bottomConnector.addPoint(-2, -10);
		bottomConnector.addPoint(-2, -2);
	}

	protected static final int[] GAP_CENTERS_X = { 16, 46, 76, 106 };
	protected static final int Y1 = 4;
	protected static final int Y2 = 88;

	protected String value;

	/**
	 * Creates a new LEDFigure
	 */
	public LEDFigure() {
		FixedConnectionAnchor c;
		c = new FixedConnectionAnchor(this);
		c.offsetH = 102;
		connectionAnchors.put(LED.TERMINAL_1_IN, c);
		inputConnectionAnchors.add(c);
		c = new FixedConnectionAnchor(this);
		c.offsetH = 72;
		connectionAnchors.put(LED.TERMINAL_2_IN, c);
		inputConnectionAnchors.add(c);
		c = new FixedConnectionAnchor(this);
		c.offsetH = 42;
		connectionAnchors.put(LED.TERMINAL_3_IN, c);
		inputConnectionAnchors.add(c);
		c = new FixedConnectionAnchor(this);
		c.offsetH = 12;
		connectionAnchors.put(LED.TERMINAL_4_IN, c);
		inputConnectionAnchors.add(c);
		c = new FixedConnectionAnchor(this);
		c.offsetH = 102;
		c.topDown = false;
		connectionAnchors.put(LED.TERMINAL_1_OUT, c);
		outputConnectionAnchors.add(c);
		c = new FixedConnectionAnchor(this);
		c.offsetH = 72;
		c.topDown = false;
		connectionAnchors.put(LED.TERMINAL_2_OUT, c);
		outputConnectionAnchors.add(c);
		c = new FixedConnectionAnchor(this);
		c.offsetH = 42;
		c.topDown = false;
		connectionAnchors.put(LED.TERMINAL_3_OUT, c);
		outputConnectionAnchors.add(c);
		c = new FixedConnectionAnchor(this);
		c.offsetH = 12;
		c.topDown = false;
		connectionAnchors.put(LED.TERMINAL_4_OUT, c);
		outputConnectionAnchors.add(c);

	}

	/**
	 * @see org.eclipse.gef.handles.HandleBounds#getHandleBounds()
	 */
	@Override
	public Rectangle getHandleBounds() {
		return getBounds().getShrinked(new Insets(4, 0, 4, 0));
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
		g.setForegroundColor(LogicColorConstants.connectorGreen);
		g.fillRectangle(0, 4, r.width, r.height - 8);
		int right = r.width - 1;
		g.drawLine(0, Y1, right, Y1);
		g.drawLine(0, Y1, 0, Y2);

		g.setForegroundColor(LogicColorConstants.connectorGreen);
		g.drawLine(0, Y2, right, Y2);
		g.drawLine(right, Y1, right, Y2);

		// Draw the gaps for the connectors
		g.setForegroundColor(ColorConstants.listBackground);
		for (int i = 0; i < 4; i++) {
			g.drawLine(GAP_CENTERS_X[i] - 4, Y1, GAP_CENTERS_X[i] + 6, Y1);
			g.drawLine(GAP_CENTERS_X[i] - 4, Y2, GAP_CENTERS_X[i] + 6, Y2);
		}

		// Draw the connectors
		g.setForegroundColor(LogicColorConstants.connectorGreen);
		g.setBackgroundColor(LogicColorConstants.connectorGreen);
		for (int i = 0; i < 4; i++) {
			connector.translate(GAP_CENTERS_X[i], 0);
			g.fillPolygon(connector);
			g.drawPolygon(connector);
			connector.translate(-GAP_CENTERS_X[i], 0);

			bottomConnector.translate(GAP_CENTERS_X[i], r.height - 1);
			g.fillPolygon(bottomConnector);
			g.drawPolygon(bottomConnector);
			bottomConnector.translate(-GAP_CENTERS_X[i], -r.height + 1);
		}

		// Draw the display
		g.setBackgroundColor(LogicColorConstants.logicHighlight);
		g.fillRectangle(displayHighlight);
		g.setBackgroundColor(DISPLAY_SHADOW);
		g.fillRectangle(displayShadow);
		g.setBackgroundColor(ColorConstants.black);
		g.fillRectangle(displayRectangle);

		// Draw the value
		g.setFont(DISPLAY_FONT);
		g.setForegroundColor(DISPLAY_TEXT);
		g.drawText(value, valuePoint);
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