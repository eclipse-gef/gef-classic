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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

public class AndGateFeedbackFigure extends AndGateFigure {

	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
	 */
	@Override
	protected void paintFigure(Graphics g) {
		g.setXORMode(true);
		g.setForegroundColor(ColorConstants.white);
		g.setBackgroundColor(LogicColorConstants.ghostFillColor);

		Rectangle r = getBounds().getCopy();
		r.translate(4, 4);
		r.setSize(22, 18);

		// Draw terminals, 2 at top
		g.drawLine(r.x + 4, r.y, r.x + 4, r.y - 4);
		g.drawLine(r.right() - 6, r.y, r.right() - 6, r.y - 4);
		g.drawPoint(r.x + 4, r.y);
		g.drawPoint(r.right() - 6, r.y);

		// outline main area
		g.drawLine(r.x, r.y, r.right() - 1, r.y);
		g.drawLine(r.right() - 1, r.y, r.right() - 1, r.bottom() - 1);
		g.drawLine(r.x, r.y, r.x, r.bottom() - 1);

		g.drawPoint(r.x, r.y);
		g.drawPoint(r.right() - 1, r.y);

		g.fillRectangle(r);

		// draw and outline the arc
		r.height = 18;
		r.y += 8;
		g.setForegroundColor(LogicColorConstants.ghostFillColor);
		g.drawLine(r.x, r.y + 8, r.x + 20, r.y + 8);
		g.setForegroundColor(ColorConstants.white);

		g.drawPoint(r.x, r.y + 8);

		g.fillArc(r, 180, 180);

		r.width--;
		r.height--;

		g.drawArc(r, 180, 180);
		g.drawLine(r.x + r.width / 2, r.bottom(), r.x + r.width / 2, r.bottom() + 4);

		g.drawPoint(r.x + r.width / 2, r.bottom());
	}

}
