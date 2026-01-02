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
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author danlee
 */
public class OrGateFigure extends GateFigure {
	public static final Dimension SIZE = new Dimension(30, 34);
	protected static final PointList GATE_OUTLINE = new PointList();

	static {
		GATE_OUTLINE.addPoint(5, 20);
		GATE_OUTLINE.addPoint(5, 4);
		GATE_OUTLINE.addPoint(9, 8);
		GATE_OUTLINE.addPoint(13, 10);
		GATE_OUTLINE.addPoint(15, 10);
		GATE_OUTLINE.addPoint(17, 10);
		GATE_OUTLINE.addPoint(21, 8);
		GATE_OUTLINE.addPoint(25, 4);
		GATE_OUTLINE.addPoint(25, 20);
	}

	/**
	 * Creates a new OrGateFigure
	 */
	public OrGateFigure() {
		setBackgroundColor(LogicColorConstants.orGate);
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
		r.translate(5, 4);
		r.setSize(22, 18);

		g.setAntialias(SWT.ON);
		g.setLineWidth(2);

		// Draw terminals, 2 at top
		g.drawLine(r.x + 4, r.y + 4, r.x + 4, r.y - 4);
		g.drawLine(r.right() - 6, r.y + 4, r.right() - 6, r.y - 4);

		// Draw the bottom arc of the gate
		r.y += 7;

		r.width -= 2;
		g.setAlpha(getAlpha());
		g.fillArc(r, 180, 180);
		g.setAlpha(ALPHA_OPAQUE);

		g.drawArc(r, 180, 180);
		g.drawLine(r.x + r.width / 2, r.bottom(), r.x + r.width / 2, r.bottom() + 4);

		// draw gate
		g.translate(getLocation());
		g.setAlpha(getAlpha());
		g.fillPolygon(GATE_OUTLINE);
		g.setAlpha(ALPHA_OPAQUE);
		g.drawPolyline(GATE_OUTLINE);
		g.translate(getLocation().getNegated());
	}

}