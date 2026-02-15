/*******************************************************************************
 * Copyright (c) 2000, 2026 IBM Corporation and others.
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

import org.eclipse.draw2d.ColorConstants;
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
			g.drawPolygon(connector);
			connector.translate(-x1, -rec.y);
			g.drawLine(x1 - 2, rec.bottom() - 3, x1 + 3, rec.bottom() - 3);
			bottomConnector.translate(x1, rec.bottom());
			g.drawPolygon(bottomConnector);
			bottomConnector.translate(-x1, -rec.bottom());
		}
	}

	@Override
	public void paint(IFigure figure, Graphics g, Insets in) {
		g.setXORMode(true);
		g.setForegroundColor(ColorConstants.white);
		g.setBackgroundColor(LogicEditorColors.INSTANCE.getGhostFill());

		Rectangle r = figure.getBounds().getShrinked(in);

		// Draw the sides of the border
		g.fillRectangle(r.x, r.y + 2, r.width, 6);
		g.fillRectangle(r.x, r.bottom() - 8, r.width, 6);
		g.fillRectangle(r.x, r.y + 2, 6, r.height - 4);
		g.fillRectangle(r.right() - 6, r.y + 2, 6, r.height - 4);

		g.fillRectangle(r.x, r.y + 2, 6, 6);
		g.fillRectangle(r.x, r.bottom() - 8, 6, 6);
		g.fillRectangle(r.right() - 6, r.y + 2, 6, 6);
		g.fillRectangle(r.right() - 6, r.bottom() - 8, 6, 6);

		// Outline the border
		g.drawPoint(r.x, r.y + 2);
		g.drawPoint(r.x, r.bottom() - 3);
		g.drawPoint(r.right() - 1, r.y + 2);
		g.drawPoint(r.right() - 1, r.bottom() - 3);
		g.drawLine(r.x, r.y + 2, r.right() - 1, r.y + 2);
		g.drawLine(r.x, r.bottom() - 3, r.right() - 1, r.bottom() - 3);
		g.drawLine(r.x, r.y + 2, r.x, r.bottom() - 3);
		g.drawLine(r.right() - 1, r.bottom() - 3, r.right() - 1, r.y + 2);

		r.shrink(new Insets(1, 1, 0, 0));
		r.expand(1, 1);
		r.shrink(getInsets(figure));
		drawConnectors(g, figure.getBounds().getShrinked(in));
	}

}
