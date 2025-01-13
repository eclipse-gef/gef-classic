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

	static {
		connector.addPoint(-4, 0);
		connector.addPoint(2, 0);
		connector.addPoint(4, 2);
		connector.addPoint(4, 10);
		connector.addPoint(-2, 10);
		connector.addPoint(-2, 2);

		bottomConnector.addPoint(-4, -1);
		bottomConnector.addPoint(2, -1);
		bottomConnector.addPoint(4, -2);
		bottomConnector.addPoint(4, -12);
		bottomConnector.addPoint(-2, -12);
		bottomConnector.addPoint(-2, -2);
	}

	private static void drawConnectors(Graphics g, Rectangle rec) {
		g.setBackgroundColor(LogicColorConstants.connectorGreen);
		for (int i = 0; i < 4; i++) {
			int x1 = rec.x + (2 * i + 1) * rec.width / 8;

			// Draw the "gap" for the connector
			g.setForegroundColor(ColorConstants.listBackground);
			g.drawLine(x1 - 4, rec.y + 4, x1 + 6, rec.y + 4);

			// Draw the connectors
			g.setForegroundColor(LogicColorConstants.connectorGreen);
			connector.translate(x1, rec.y);
			g.fillPolygon(connector);
			g.drawPolygon(connector);
			connector.translate(-x1, -rec.y);
			g.setForegroundColor(ColorConstants.listBackground);
			g.drawLine(x1 - 4, rec.bottom() - 6, x1 + 6, rec.bottom() - 6);
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

		// Draw the sides of the border
		g.fillRectangle(r.x, r.y + 4, r.width, 12);
		g.fillRectangle(r.x, r.bottom() - 16, r.width, 12);
		g.fillRectangle(r.x, r.y + 4, 12, r.height - 8);
		g.fillRectangle(r.right() - 12, r.y + 4, 12, r.height - 8);

		// Outline the border
		g.setForegroundColor(LogicColorConstants.connectorGreen);
		g.drawLine(r.x, r.y + 4, r.right() - 1, r.y + 4);
		g.drawLine(r.x, r.bottom() - 6, r.right() - 1, r.bottom() - 6);
		g.drawLine(r.x, r.y + 4, r.x, r.bottom() - 6);
		g.drawLine(r.right() - 1, r.bottom() - 6, r.right() - 1, r.y + 4);

		r.shrink(new Insets(2, 2, 0, 0));
		r.expand(2, 2);
		r.shrink(getInsets(figure));
		drawConnectors(g, figure.getBounds().getShrinked(in));
	}

}
