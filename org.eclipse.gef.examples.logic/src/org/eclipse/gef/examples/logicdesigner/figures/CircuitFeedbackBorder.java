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

import static org.eclipse.gef.examples.logicdesigner.figures.CircuitBorder.CORNER_RADIUS;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

public class CircuitFeedbackBorder extends CircuitBorder {

	private static void drawConnectors(Graphics g, Rectangle rec) {
		for (int i = 0; i < 4; i++) {
			int x1 = rec.x + (2 * i + 1) * rec.width / 8;

			// Draw the "gap" for the connector
			g.drawLine(x1 - 2, rec.y + 2, x1 + 3, rec.y + 2);

			// Draw the connectors
			connector.translate(x1, rec.y);
			g.fillPolygon(connector);
			g.drawPolygon(connector);
			connector.translate(-x1, -rec.y);
			g.drawLine(x1 - 2, rec.bottom() - 3, x1 + 3, rec.bottom() - 3);
			bottomConnector.translate(x1, rec.bottom());
			g.fillPolygon(bottomConnector);
			g.drawPolygon(bottomConnector);
			bottomConnector.translate(-x1, -rec.bottom());
		}
	}

	@Override
	public void paint(IFigure figure, Graphics g, Insets in) {
		g.setForegroundColor(LogicColorConstants.feedbackFill);
		g.setBackgroundColor(LogicColorConstants.feedbackFill);

		Rectangle r = figure.getBounds().getShrinked(in);

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
