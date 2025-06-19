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

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

public class CircuitBorder extends AbstractBorder {

	protected static Insets insets = new Insets(16, 12, 16, 12);
	protected static PointList connector = new PointList();
	protected static PointList bottomConnector = new PointList();
	protected static final int CORNER_RADIUS = 6;

	static {
		connector.addPoint(-4, 0);
		connector.addPoint(4, 0);
		connector.addPoint(6, 2);
		connector.addPoint(6, 8);
		connector.addPoint(-4, 8);
		connector.addPoint(-4, 2);

		bottomConnector.addPoint(-4, 0);
		bottomConnector.addPoint(4, 0);
		bottomConnector.addPoint(6, -2);
		bottomConnector.addPoint(6, -8);
		bottomConnector.addPoint(-4, -8);
		bottomConnector.addPoint(-4, -2);
	}

	private static void drawConnectors(Graphics g, Rectangle rec) {
		g.setBackgroundColor(LogicColorConstants.connectorGreen);
		for (int i = 0; i < 4; i++) {
			int x1 = rec.x + (2 * i + 1) * rec.width / 8;

			// Draw the "gap" for the connector
			g.setForegroundColor(ColorConstants.listBackground);
			g.drawLine(x1 - 2, rec.y + 2, x1 + 3, rec.y + 2);

			// Draw the connectors
			g.setForegroundColor(LogicColorConstants.connectorGreen);
			connector.translate(x1, rec.y);
			g.fillPolygon(connector);
			g.drawPolygon(connector);
			connector.translate(-x1, -rec.y);
			g.setForegroundColor(ColorConstants.listBackground);
			g.drawLine(x1 - 2, rec.bottom() - 3, x1 + 3, rec.bottom() - 3);
			g.setForegroundColor(LogicColorConstants.connectorGreen);
			bottomConnector.translate(x1, rec.bottom());
			g.fillPolygon(bottomConnector);
			g.drawPolygon(bottomConnector);
			bottomConnector.translate(-x1, -rec.bottom());
		}
	}

	@Override
	public Insets getInsets(IFigure figure) {
		return insets;
	}

	@Override
	public void paint(IFigure figure, Graphics g, Insets in) {
		Rectangle r = figure.getBounds().getShrinked(in);

		g.setForegroundColor(LogicColorConstants.logicGreen);
		g.setBackgroundColor(LogicColorConstants.logicGreen);

		// Draw top and bottom
		Rectangle topBorder = new Rectangle(r.x, r.y + 4, r.width, 12);
		g.fillRoundRectangle(topBorder, CORNER_RADIUS * 2, CORNER_RADIUS * 2);
		Rectangle bottomBorder = new Rectangle(r.x, r.bottom() - 16, r.width, 12);
		g.fillRoundRectangle(bottomBorder, CORNER_RADIUS * 2, CORNER_RADIUS * 2);

		// Draw left and right side
		g.fillRectangle(r.x, r.y + 4 + CORNER_RADIUS, 8, r.height - 8 - CORNER_RADIUS * 2);
		g.fillRectangle(r.right() - 8, r.y + 4 + CORNER_RADIUS, 8, r.height - 8 - CORNER_RADIUS * 2);

		drawConnectors(g, figure.getBounds().getShrinked(in));
	}

}
