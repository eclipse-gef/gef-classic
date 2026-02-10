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

/**
 * @author danlee
 */
public class AndGateFigure extends GateFigure {
	public static final Dimension SIZE = new Dimension(30, 34);
	private static final Path GATE_OUTLINE = new Path(null);

	static {
		GATE_OUTLINE.moveTo(4, 9.5f);
		GATE_OUTLINE.lineTo(4, 8);
		GATE_OUTLINE.addArc(4, 5, 6, 6, 180, -90);
		GATE_OUTLINE.lineTo(20, 5);
		GATE_OUTLINE.addArc(20, 5, 6, 6, 90, -90);
		GATE_OUTLINE.lineTo(26, 11);
		GATE_OUTLINE.addArc(4, 11, 22, 18, 0, -180);
		GATE_OUTLINE.lineTo(4, 9.5f);
	}

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
		g.setAntialias(SWT.ON);
		g.setLineWidth(2);

		// Draw terminals, 2 at top and 1 at bottom
		g.translate(getLocation());
		g.drawLine(8, 0, 8, 5);
		g.drawLine(22, 0, 22, 5);
		g.drawLine(15, 29, 15, 29 + 4);

		g.setAlpha(getAlpha());
		g.fillPath(GATE_OUTLINE);
		g.setAlpha(ALPHA_OPAQUE);
		g.drawPath(GATE_OUTLINE);
		g.translate(getLocation().getNegated());
	}

}