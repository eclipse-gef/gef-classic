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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.colors.HSL;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;

public class GraphicsMandelbrotComposite extends AbstractGraphicsComposite {
	private static final Rectangle MANDELBROT_BOUNDS = new PrecisionRectangle(-2.0, -1.12, 2.47, 2.24);
	private static final Dimension SIZE = new Dimension(200, 200);

	public GraphicsMandelbrotComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Dimension getGraphicsSize() {
		return SIZE;
	}

	@Override
	protected void paint(Graphics g) {
		for (int w = 0; w < SIZE.width; ++w) {
			for (int h = 0; h < SIZE.height; ++h) {
				double x = MANDELBROT_BOUNDS.preciseX() + w * MANDELBROT_BOUNDS.preciseWidth() / SIZE.width;
				double y = MANDELBROT_BOUNDS.preciseY() + h * MANDELBROT_BOUNDS.preciseHeight() / SIZE.height;
				Color color = getColor(x, y);
				g.setForegroundColor(color);
				g.drawPoint(w, h);
			}
		}
	}

	private static Color getColor(double x0, double y0) {
		int count = 0;
		int maxCount = 1000;

		double x = 0;
		double y = 0;

		while ((x * x + y * y) <= 4 && count < maxCount) {
			double tmp = x * x - y * y + x0;
			y = 2 * x * y + y0;
			x = tmp;
			count++;
		}

		if (count == maxCount) {
			return ColorConstants.black;
		}

		double hue = (count * 6) % 360f;
		double saturation = 0.8f;
		double lightness = 0.5f;
		return new HSL(hue, saturation, lightness).toColor();
	}
}
