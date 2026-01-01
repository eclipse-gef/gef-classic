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

import org.eclipse.swt.SWT;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author danlee
 */
public class AndGateFigure extends GateFigure {
	private static final int CORNER_ARC = 6; // Must be divisible by 2 to be symmetric
	public static final Dimension SIZE = new Dimension(30, 34);

	/**
	 * Constructor for AndGateFigure.
	 */
	public AndGateFigure() {
		setBackgroundColor(LogicColorConstants.andGate);
		setForegroundColor(LogicColorConstants.outlineColor);
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
		r.translate(4, 5);
		r.setSize(22, 18);

		g.setAntialias(SWT.ON);
		g.setLineWidth(2);

		// Draw terminals, 2 at top
		g.drawLine(r.x + 4, r.y, r.x + 4, r.y - 6);
		g.drawLine(r.right() - 5, r.y, r.right() - 5, r.y - 5);

		r.height = 15;
		// draw main area
		g.setAlpha(getAlpha());
		g.fillArc(r.x, r.y, CORNER_ARC, CORNER_ARC, 180, -90);
		g.fillArc(r.right() - CORNER_ARC, r.y, CORNER_ARC, CORNER_ARC, 90, -90);
		g.fillRectangle(r.x + CORNER_ARC / 2, r.y, r.width - CORNER_ARC, CORNER_ARC / 2);
		g.fillRectangle(r.x, r.y + CORNER_ARC / 2, r.width, r.height - CORNER_ARC / 2);
		g.setAlpha(ALPHA_OPAQUE);

		// outline main area
		g.drawArc(r.x, r.y, CORNER_ARC, CORNER_ARC, 180, -90);
		g.drawArc(r.right() - CORNER_ARC, r.y, CORNER_ARC, CORNER_ARC, 90, -90);
		g.drawLine(r.x + CORNER_ARC / 2, r.y, r.right() - CORNER_ARC / 2, r.y);
		g.drawLine(r.x, r.y + CORNER_ARC / 2, r.x, r.bottom());
		g.drawLine(r.right(), r.y + CORNER_ARC / 2, r.right(), r.bottom());

		// draw and outline the arc
		r.y += 6;
		r.height = 18;
		g.setAlpha(getAlpha());
		g.fillArc(r, 180, 180);
		g.setAlpha(ALPHA_OPAQUE);
		g.drawArc(r, 180, 180);
		g.drawLine(r.x + r.width / 2, r.bottom(), r.x + r.width / 2, r.bottom() + 4);
	}

}