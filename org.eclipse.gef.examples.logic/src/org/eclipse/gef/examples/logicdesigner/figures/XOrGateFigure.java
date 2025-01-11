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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author danlee
 */
public class XOrGateFigure extends GateFigure {

	public static final Dimension SIZE = new Dimension(30, 34);
	protected static final PointList GATE_OUTLINE = new PointList();
	protected static final PointList GATE_TOP = new PointList();

	static {
		// setup gate outline
		GATE_OUTLINE.addPoint(4, 20);
		GATE_OUTLINE.addPoint(4, 8);
		GATE_OUTLINE.addPoint(8, 12);
		GATE_OUTLINE.addPoint(12, 14);
		GATE_OUTLINE.addPoint(14, 14);
		GATE_OUTLINE.addPoint(16, 14);
		GATE_OUTLINE.addPoint(20, 12);
		GATE_OUTLINE.addPoint(24, 8);
		GATE_OUTLINE.addPoint(24, 20);

		// setup top curve of gate
		GATE_TOP.addPoint(4, 4);
		GATE_TOP.addPoint(8, 8);
		GATE_TOP.addPoint(12, 10);
		GATE_TOP.addPoint(14, 10);
		GATE_TOP.addPoint(16, 10);
		GATE_TOP.addPoint(20, 8);
		GATE_TOP.addPoint(24, 4);
	}

	/**
	 * Constructor for XOrGateFigure.
	 */
	public XOrGateFigure() {
		setBackgroundColor(LogicColorConstants.xorGate);
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
		r.translate(4, 4);
		r.setSize(22, 18);

		// Draw terminals, 2 at top
		g.drawLine(r.x + 4, r.y + 4, r.x + 4, r.y - 4);
		g.drawLine(r.right() - 6, r.y + 4, r.right() - 6, r.y - 4);

		// Draw an oval that represents the bottom arc
		r.y += 8;

		/*
		 * Draw the bottom gate arc. This is done with an oval. The oval overlaps the
		 * top arc of the gate, so this region is clipped.
		 */
		g.pushState();
		r.y += 2;
		g.clipRect(r);
		r.y--;
		r.width--;
		r.height--;
		g.fillOval(r);
		r.width--;
		r.height--;
		g.drawOval(r);
		g.popState();
		g.drawLine(r.x + r.width / 2, r.bottom(), r.x + r.width / 2, r.bottom() + 4);

		// Draw the gate outline and top curve
		g.translate(getLocation());
		g.drawPolyline(GATE_TOP);
		g.fillPolygon(GATE_OUTLINE);
		g.drawPolyline(GATE_OUTLINE);
		g.translate(getLocation().negate());
	}

}
