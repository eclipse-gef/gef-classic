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

public class AndGateFeedbackFigure extends AndGateFigure {

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
		r.translate(4, 5);
		r.setSize(22, 18);

		// Draw terminals, 2 at top
		g.drawLine(r.x + 4, r.y, r.x + 4, r.y - 6);
		g.drawLine(r.right() - 5, r.y, r.right() - 5, r.y - 5);

		// draw main area
		r.width++;
		r.height++;
		g.fillRoundRectangle(r, 5, 5);

		// outline main area
		Rectangle outlineRect = r.getCopy();
		outlineRect.width--;
		g.drawRoundRectangle(outlineRect, 5, 5);

		// draw and outline the arc
		r.height = 18;
		r.y += 7;
		g.fillArc(r, 180, 180);
		r.width--;
		r.height--;
		g.drawArc(r, 180, 180);
		g.drawLine(r.x + r.width / 2, r.bottom(), r.x + r.width / 2, r.bottom() + 4);
	}

}
