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

import org.eclipse.swt.SWT;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

public class OrGateFeedbackFigure extends OrGateFigure {

	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
	 */
	@Override
	protected void paintFigure(Graphics g) {
		g.setBackgroundColor(LogicColorConstants.feedbackFill);
		g.setForegroundColor(LogicColorConstants.feedbackOutline);
		g.setAntialias(SWT.ON);
		g.setLineWidth(2);

		Rectangle r = getBounds().getCopy();
		r.translate(4, 4);
		r.setSize(22, 18);

		// Draw terminals, 2 at top
		g.drawLine(r.x + 4, r.y + 4, r.x + 4, r.y - 4);
		g.drawLine(r.right() - 6, r.y + 4, r.right() - 6, r.y - 4);

		// Draw the bottom arc of the gate
		r.y += 8;

		r.width--;
		g.fillOval(r);
		r.width--;
		r.height--;

		g.drawOval(r);
		g.drawLine(r.x + r.width / 2, r.bottom(), r.x + r.width / 2, r.bottom() + 4);

		// draw gate
		g.translate(getLocation());
		g.fillPolygon(GATE_OUTLINE);
		g.drawPolyline(GATE_OUTLINE);
		g.translate(getLocation().getNegated());
	}

}
