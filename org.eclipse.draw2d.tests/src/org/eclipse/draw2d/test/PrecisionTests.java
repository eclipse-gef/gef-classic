/*******************************************************************************
 * Copyright (c) 2025 Patrick Ziegler and others.
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

package org.eclipse.draw2d.test;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case to ensure rounding errors don't accumulate when translating
 * coordinates through multiple scalable layers.
 */
public class PrecisionTests extends BaseTestCase {
	private IFigure fig;

	@BeforeEach
	public void setUp() {
		fig = new Figure();
		IFigure layers = createLayers();
		layers.add(fig);
		IFigure root = new Figure();
		root.add(FigureUtilities.getRoot(layers));
	}

	private static IFigure createLayers() {
		ScalableLayeredPane f1 = createLayer(1.33);
		ScalableLayeredPane f2 = createLayer(5.79);
		ScalableLayeredPane f3 = createLayer(0.87);
		ScalableLayeredPane f4 = createLayer(3.88);
		ScalableLayeredPane f5 = createLayer(1.46);

		f1.add(f2);
		f2.add(f3);
		f3.add(f4);
		f4.add(f5);

		return f5;
	}

	private static ScalableLayeredPane createLayer(double scale) {
		ScalableLayeredPane figure = new ScalableLayeredPane();
		figure.setScale(scale);
		figure.setOpaque(true);
		return figure;
	}

	@Test
	public void testPreciseTranslateToAbsolute() {
		Rectangle r1 = new Rectangle(13, 37, 163, 377);
		Rectangle r2 = new PrecisionRectangle(13, 37, 163, 377);
		fig.translateToAbsolute(r1);
		fig.translateToAbsolute(r2);
		assertEquals(493, 1404, 6187, 14309, r1);
		assertEquals(r1.x, r2.x);
		assertEquals(r1.y, r2.y);
		assertEquals(r1.width, r2.width);
		assertEquals(r1.height, r2.height);
	}

	@Test
	public void testPreciseTranslateToRelative() {
		Rectangle r1 = new Rectangle(753, 891, 353, 564);
		Rectangle r2 = new PrecisionRectangle(753, 891, 353, 564);
		fig.translateToRelative(r1);
		fig.translateToRelative(r2);
		assertEquals(19, 23, 11, 16, r1);
		assertEquals(r1.x, r2.x);
		assertEquals(r1.y, r2.y);
		assertEquals(r1.width, r2.width);
		assertEquals(r1.height, r2.height);
	}
}
