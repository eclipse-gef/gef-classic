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

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionDimension;
import org.eclipse.draw2d.geometry.PrecisionPointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.internal.InternalDraw2dUtils;

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
		assumeTrue(InternalDraw2dUtils.isAutoScaleEnabled());
		PrecisionDimension scaleDimension = new PrecisionDimension(0, 1);
		fig.translateToAbsolute(scaleDimension);
		double fullScale = scaleDimension.preciseHeight();
		Rectangle r1 = new Rectangle(13, 37, 163, 377);
		fig.translateToAbsolute(r1);
		Rectangle r2 = new Rectangle(13, 37, 163, 377).scale(fullScale);
		assertEquals(493, 1404, 6187, 14309, r1);
		assertEquals(r1.x, r2.x);
		assertEquals(r1.y, r2.y);
		assertEquals(r1.width, r2.width);
		assertEquals(r1.height, r2.height);
	}

	@Test
	public void testPreciseTranslateToRelative() {
		assumeTrue(InternalDraw2dUtils.isAutoScaleEnabled());
		PrecisionDimension scaleDimension = new PrecisionDimension(0, 1);
		fig.translateToAbsolute(scaleDimension);
		double fullScale = scaleDimension.preciseHeight();
		Rectangle r1 = new Rectangle(753, 891, 353, 564);
		fig.translateToRelative(r1);
		Rectangle r2 = new Rectangle(753, 891, 353, 564).scale(1 / fullScale);
		assertEquals(19, 23, 11, 16, r1);
		assertEquals(r1.x, r2.x);
		assertEquals(r1.y, r2.y);
		assertEquals(r1.width, r2.width);
		assertEquals(r1.height, r2.height);
	}

	@Test
	public void testPreciseTranslateToAbsolute_PointList() {
		assumeTrue(InternalDraw2dUtils.isAutoScaleEnabled());
		PointList p1 = new PointList(new int[] { 13, 29, 32, 5 });
		PointList p2 = new PrecisionPointList(new int[] { 13, 29, 32, 5 });
		fig.translateToAbsolute(p1);
		fig.translateToAbsolute(p2);
		assertArrayEquals(p1.toIntArray(), new int[] { 493, 1100, 1214, 189 });
		assertArrayEquals(p1.toIntArray(), p2.toIntArray());
	}

	@Test
	public void testPreciseTranslateToRelative_PointList() {
		assumeTrue(InternalDraw2dUtils.isAutoScaleEnabled());
		PointList p1 = new PointList(new int[] { 518, 628, 715, 313 });
		PointList p2 = new PrecisionPointList(new int[] { 518, 628, 715, 313 });
		fig.translateToRelative(p1);
		fig.translateToRelative(p2);
		assertArrayEquals(p1.toIntArray(), new int[] { 13, 16, 18, 8 });
		assertArrayEquals(p1.toIntArray(), p2.toIntArray());
	}

	// Ensure that results are compatible with pre-existing behavior for translating
	// a rectangle when only a single scaled layer is present
	@SuppressWarnings("static-method")
	@Test
	public void testPreciseTranslateToAbsolute_singleLayerCompatibility() {
		Figure figure = new Figure();
		ScalableLayeredPane layer = createLayer(1.4);
		IFigure root = new Figure();
		root.add(layer);
		layer.add(figure);
		Rectangle customRectangle = new Rectangle(13, 37, 13, 377) {
		};
		Rectangle plainRectangle = new Rectangle(customRectangle);

		figure.translateToAbsolute(customRectangle);
		figure.translateToAbsolute(plainRectangle);
		assertEquals(customRectangle.x, plainRectangle.x);
		assertEquals(customRectangle.y, plainRectangle.y);
		assertEquals(customRectangle.width, plainRectangle.width);
		assertEquals(customRectangle.height, plainRectangle.height);
	}
}
