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

import org.eclipse.swt.widgets.Composite;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

public class GraphicsShapesComposite extends AbstractGraphicsComposite {
	private static final Dimension SIZE = new Dimension(150, 400);
	private static final Rectangle SHAPE_BOUNDS = new Rectangle(0, 0, 50, 50);

	public GraphicsShapesComposite(Composite parent, int style) {
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

		g.drawRectangle(SHAPE_BOUNDS);
		g.fillRectangle(SHAPE_BOUNDS.getTranslated(100, 0));

		g.translate(0, 60);
		g.drawOval(SHAPE_BOUNDS);
		g.fillOval(SHAPE_BOUNDS.getTranslated(100, 0));

		g.translate(0, 60);
		g.drawArc(SHAPE_BOUNDS, 0, 60);
		g.fillArc(SHAPE_BOUNDS.getTranslated(100, 0), 0, 60);

		g.translate(0, 60);
		g.drawRoundRectangle(SHAPE_BOUNDS, 30, 60);
		g.fillRoundRectangle(SHAPE_BOUNDS.getTranslated(100, 0), 30, 60);

		g.translate(0, 60);
		g.drawPolygon(new int[] { 10, 10, 25, 15, 50, 30, 25, 45 });
		g.fillPolygon(new int[] { 110, 10, 125, 15, 150, 30, 125, 45 });

		g.translate(0, 60);
		g.fillGradient(SHAPE_BOUNDS, true);
		g.fillGradient(SHAPE_BOUNDS.getTranslated(100, 0), false);
	}
}
