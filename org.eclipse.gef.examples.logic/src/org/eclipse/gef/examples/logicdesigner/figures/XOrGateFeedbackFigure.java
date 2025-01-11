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

public class XOrGateFeedbackFigure extends XOrGateFigure {

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
		g.drawLine(r.x + 4, r.y + 4, r.x + 4, r.y - 4);
		g.drawLine(r.right() - 6, r.y + 4, r.right() - 6, r.y - 4);

		// fix it
		g.drawPoint(r.x + 4, r.y + 4);
		g.drawPoint(r.right() - 6, r.y + 4);

		// Draw an oval that represents the bottom arc
		r.y += 8;

		/*
		 * Draw the bottom gate arc. This is done with an oval. The oval overlaps the
		 * top arc of the gate, so this region is clipped.
		 */
		g.pushState();
		r.y++;
		g.clipRect(r);
		r.y--;

		r.width--;
		r.height--;
		g.fillArc(r, 180, 180);
		r.width--;
		r.height--;
		g.drawArc(r, 180, 180);
		g.drawPoint(r.x, r.y + 8);
		g.popState();
		g.drawLine(r.x + r.width / 2, r.bottom(), r.x + r.width / 2, r.bottom() + 4);
		g.drawPoint(r.x + r.width / 2, r.bottom());

		// Draw the gate outline and top curve
		g.translate(getLocation());
		g.drawPolyline(GATE_TOP);
		g.fillPolygon(GATE_OUTLINE);
		g.drawPolyline(GATE_OUTLINE);
		g.translate(getLocation().negate());
	}

}
