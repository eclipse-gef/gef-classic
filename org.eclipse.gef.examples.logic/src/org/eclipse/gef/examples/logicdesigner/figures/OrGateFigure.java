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

import org.eclipse.swt.graphics.Path;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author danlee
 */
public class OrGateFigure extends GateFigure {
	public static final Dimension SIZE = new Dimension(30, 34);
	protected static final Path GATE_OUTLINE = new Path(null);

	static {
		GATE_OUTLINE.moveTo(5, 20);
		GATE_OUTLINE.lineTo(5, 4);
		GATE_OUTLINE.lineTo(9, 8);
		GATE_OUTLINE.lineTo(13, 10);
		GATE_OUTLINE.lineTo(15, 10);
		GATE_OUTLINE.lineTo(17, 10);
		GATE_OUTLINE.lineTo(21, 8);
		GATE_OUTLINE.lineTo(25, 4);
		GATE_OUTLINE.lineTo(25, 20);
		GATE_OUTLINE.addArc(5, 11, 20, 18, 0, -180);
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

		g.setLineWidth(2);

		// Draw terminals, 2 at top, 1 at bottom
		g.drawLine(r.left() + 9, r.top() + 0, r.left() + 9, r.top() + 8);
		g.drawLine(r.right() - 9, r.top() + 0, r.right() - 9, r.top() + 8);
		g.drawLine(r.left() + r.width() / 2, r.bottom() - 5, r.left() + r.width() / 2, r.bottom() - 1);

		// draw gate
		g.translate(getLocation());
		g.setAlpha(getAlpha());
		g.fillPath(GATE_OUTLINE);
		g.setAlpha(ALPHA_OPAQUE);
		g.drawPath(GATE_OUTLINE);
		g.translate(getLocation().getNegated());
	}

}