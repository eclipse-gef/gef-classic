/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.examples.svg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;

public class GraphicsLinesComposite extends AbstractGraphicsComposite {
	private static final Dimension SIZE = new Dimension(220, 200);

	public GraphicsLinesComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Dimension getGraphicsSize() {
		return SIZE;
	}

	@Override
	protected void paint(Graphics g) {
		g.setForegroundColor(ColorConstants.red);
		g.setBackgroundColor(ColorConstants.blue);
		g.setLineAttributes(new LineAttributes(10.f));
		g.translate(0, 5);
		g.pushState();

		g.setLineCap(SWT.CAP_ROUND);
		g.drawLine(0, 0, 200, 0);

		g.setLineCap(SWT.CAP_SQUARE);
		g.drawLine(0, 20, 200, 20);

		g.setLineCap(SWT.CAP_FLAT);
		g.drawLine(0, 40, 200, 40);
		g.restoreState();

		g.setLineStyle(Graphics.LINE_DASH);
		g.drawLine(0, 60, 200, 60);

		g.setLineStyle(Graphics.LINE_DASHDOT);
		g.drawLine(0, 80, 200, 80);

		g.setLineStyle(Graphics.LINE_DASHDOTDOT);
		g.drawLine(0, 100, 200, 100);

		g.setLineStyle(Graphics.LINE_DOT);
		g.drawLine(0, 120, 200, 120);

		g.setLineDash(new int[] { 10, 5, 30, 5, 20, 5, 15, 15 });
		g.setLineStyle(Graphics.LINE_CUSTOM);
		g.drawLine(0, 140, 200, 140);

		g.setLineStyle(Graphics.LINE_SOLID);
		g.drawLine(0, 160, 200, 160);

		g.drawPolyline(new int[] { 0, 180, 40, 175, 80, 185, 120, 175, 160, 185, 200, 175 });

	}

}
