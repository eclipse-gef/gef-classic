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
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case to ensure rounding errors don't accumulate when translating
 * coordinates through multiple scalable layers.
 */
public class PrecisionTests extends BaseTestCase {
	private IFigure layer;
	private IFigure fig;
	private Polyline conn;

	@BeforeEach
	public void setUp() {
		fig = new Figure();
		conn = new Polyline();
		layer = createLayers();
		layer.add(fig);
		layer.add(conn);

		IFigure root = new Figure();
		root.setOpaque(true);
		root.add(FigureUtilities.getRoot(layer));

	}

	private static IFigure createLayers() {
		ScalableLayeredPane f1 = createLayer(1.33);
		ScalableLayeredPane f2 = createLayer(1.79);
		ScalableLayeredPane f3 = createLayer(0.87);
		ScalableLayeredPane f4 = createLayer(1.88);
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
	public void testPrecisionDimension() {
		fig.setSize(169, 213);
		Dimension d = fig.getSize().getCopy();

		layer.translateToAbsolute(d);
		layer.translateToRelative(d);
		assertEquals(169, 213, d);
	}

	@Test
	public void testPrecisionPoint() {
		fig.setLocation(new Point(17, 39));
		Point p = fig.getLocation();

		layer.translateToAbsolute(p);
		layer.translateToRelative(p);
		assertEquals(17, 39, p);
	}

	@Test
	public void testPrecisionRectangle() {
		fig.setBounds(new Rectangle(13, 151, 219, 277));
		Rectangle r = fig.getBounds().getCopy();

		layer.translateToAbsolute(r);
		layer.translateToRelative(r);
		assertEquals(13, 151, 219, 277, r);
	}

	@Test
	public void testPrecisionPointList() {
		conn.addPoint(new Point(139, 291));
		conn.addPoint(new Point(157, 302));
		conn.addPoint(new Point(161, 310));
		PointList p = conn.getPoints().getCopy();

		layer.translateToAbsolute(p);
		layer.translateToRelative(p);
		assertArrayEquals(new int[] { 139, 291, 157, 302, 161, 310 }, p.toIntArray());
	}
}
