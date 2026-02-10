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
import org.eclipse.swt.graphics.Path;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PointList;

/**
 * @author danlee
 */
public class XOrGateFigure extends GateFigure {

	public static final Dimension SIZE = new Dimension(30, 34);
	protected static final Path GATE_OUTLINE = new Path(null);
	protected static final PointList GATE_TOP = new PointList();

	static {
		// setup gate outline
		GATE_OUTLINE.moveTo(5, 10);
		GATE_OUTLINE.lineTo(5, 8);
		GATE_OUTLINE.lineTo(9, 12);
		GATE_OUTLINE.lineTo(13, 14);
		GATE_OUTLINE.lineTo(15, 14);
		GATE_OUTLINE.lineTo(17, 14);
		GATE_OUTLINE.lineTo(21, 12);
		GATE_OUTLINE.lineTo(25, 8);
		GATE_OUTLINE.lineTo(25, 20);
		GATE_OUTLINE.addArc(5, 11, 20, 18, 0, -180);
		GATE_OUTLINE.lineTo(5, 10);

		// setup top curve of gate
		GATE_TOP.addPoint(5, 4);
		GATE_TOP.addPoint(9, 8);
		GATE_TOP.addPoint(13, 10);
		GATE_TOP.addPoint(15, 10);
		GATE_TOP.addPoint(17, 10);
		GATE_TOP.addPoint(21, 8);
		GATE_TOP.addPoint(25, 4);
	}

	/**
	 * Constructor for XOrGateFigure.
	 */
	public XOrGateFigure() {
		setBackgroundColor(LogicColorConstants.xorGate);
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
		g.setAntialias(SWT.ON);
		g.setLineWidth(2);

		// Draw terminals, 2 at top and one at bottom
		g.translate(getLocation());
		g.drawLine(9, 0, 9, 8);
		g.drawLine(21, 0, 21, 8);
		g.drawLine(15, 29, 15, 33);

		g.setAlpha(getAlpha());
		g.fillPath(GATE_OUTLINE);
		g.setAlpha(ALPHA_OPAQUE);
		g.drawPolyline(GATE_TOP);
		g.drawPath(GATE_OUTLINE);
		g.translate(getLocation().negate());
	}

}
