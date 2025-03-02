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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

public class LEDFeedbackFigure extends LEDFigure {

	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
	 */
	@Override
	protected void paintFigure(Graphics g) {
		g.setBackgroundColor(LogicColorConstants.feedbackOutline);

		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());

		Rectangle mainBody = new Rectangle(0, 2, r.width, r.height - 4);
		g.fillRoundRectangle(mainBody, CORNER_RADIUS, CORNER_RADIUS);
		drawConnectors(g, r);

		// Draw display
		g.setBackgroundColor(LogicColorConstants.feedbackFill);
		g.fillRoundRectangle(displayRectangle, CORNER_RADIUS, CORNER_RADIUS);

	}

	private static void drawConnectors(Graphics g, Rectangle r) {
		for (int i = 0; i < 4; i++) {

			connector.translate(GAP_CENTERS_X[i], 0);
			g.fillPolygon(connector);
			connector.translate(-GAP_CENTERS_X[i], 0);

			bottomConnector.translate(GAP_CENTERS_X[i], r.height - 1);
			g.fillPolygon(bottomConnector);
			bottomConnector.translate(-GAP_CENTERS_X[i], -(r.height - 1));
		}
	}

}