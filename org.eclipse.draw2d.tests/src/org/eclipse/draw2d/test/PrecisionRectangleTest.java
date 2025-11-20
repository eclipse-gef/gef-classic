/*******************************************************************************
 * Copyright (c) 2006, 2025 IBM Corporation and others.
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

package org.eclipse.draw2d.test;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.Test;

/**
 * @author sshaw
 *
 */
public class PrecisionRectangleTest extends BaseTestCase {

	@SuppressWarnings("static-method")
	@Test
	public void testShrink() {
		Insets insets = new Insets(2, 2, 2, 2);

		PrecisionRectangle r = new PrecisionRectangle(new Rectangle(100, 100, 250, 250));
		PrecisionRectangle copy = r.getPreciseCopy();
		r.performTranslate(30, 30);
		r.performScale(2f);
		r.shrink(insets);
		r.performScale(1 / 2f);
		r.performTranslate(-30, -30);

		assertTrue(!r.equals(copy));

		insets = new Insets(1, 1, -1, -1);

		r = new PrecisionRectangle(new Rectangle(0, 0, 3, 3));
		copy = r.getPreciseCopy();
		r.performTranslate(1, 1);
		r.performScale(4f);
		r.shrink(insets);
		r.performScale(1 / 4f);
		r.performTranslate(-1, -1);

		assertTrue(!r.equals(copy));

		r = new PrecisionRectangle(-9.486614173228347, -34.431496062992125, 41.99055118110236, 25.92755905511811);
		r.performScale(26.458333333333332);
		assertEquals(-251.0, r.preciseX(), 0);
		assertEquals(-910.9999999999999, r.preciseY(), 0);
		assertEquals(1111.0, r.preciseWidth(), 0);
		assertEquals(686.0, r.preciseHeight(), 0);
		r.performScale(1.0 / 26.458333333333332);
		assertEquals(-9.486614173228347, r.preciseX(), 0);
		assertEquals(-34.431496062992125, r.preciseY(), 0);
		assertEquals(41.99055118110236, r.preciseWidth(), 0);
		assertEquals(25.92755905511811, r.preciseHeight(), 0);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testExpand() {
		PrecisionRectangle r = new PrecisionRectangle(new Rectangle(100, 100, 250, 250));
		PrecisionRectangle copy = r.getPreciseCopy();
		r.expand(0.1, 0.1);
		assertEquals(r, new PrecisionRectangle(99.9, 99.9, 250.2, 250.2));
		assertEquals(r, copy.getExpanded(0.1, 0.1));
		r.shrink(0.1, 0.1);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testUnion() {
		PrecisionRectangle r = new PrecisionRectangle(-9.486614173228347, -34.431496062992125, 41.99055118110236,
				25.92755905511811);
		r.union(100.5, 100.5);
		assertEquals(new PrecisionRectangle(-9.486614173228347, -34.431496062992125, 100.5 + 9.486614173228347,
				100.5 + 34.431496062992125), r);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testResize() {
		PrecisionRectangle r = new PrecisionRectangle(-9.486614173228347, -34.431496062992125, 41.99055118110236,
				25.92755905511811);
		r.resize(100.1, 100.1);
		assertEquals(new PrecisionRectangle(-9.486614173228347, -34.431496062992125, 41.99055118110236 + 100.1,
				25.92755905511811 + 100.1), r);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testContains() {
		PrecisionRectangle r = new PrecisionRectangle(-9.486614173228347, -34.431496062992125, 41.99055118110236,
				25.92755905511811);
		assertTrue(r.contains(-9.486614173228347, -34.431496062992125));
		assertTrue(r.contains(-9.486614173228347 + 41.99055118110235, -34.431496062992125 + 25.92755905511810));
	}
	// contains

	@SuppressWarnings("static-method")
	@Test
	public void testScale() {
		// check work scale(double) with rounding errors
		PrecisionRectangle r = new PrecisionRectangle(10, 10, 52, 52);
		assertSame(r, r.scale(1.75));
		assertEquals(17.5, r.preciseX(), 0);
		assertEquals(17.5, r.preciseY(), 0);
		assertEquals(91, r.preciseWidth(), 0);
		assertEquals(91, r.preciseHeight(), 0);
		// Different rounding behavior between Rectangle and PrecisionRectangle.
		assertEquals(17, 17, 91, 91, r);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testScaleLocation() {
		// check work scale(double) with rounding errors
		PrecisionRectangle r = new PrecisionRectangle(-9.47, -34.43, 41.95, 25.92);
		assertEquals(r.getCopy().preciseLocation().scale(1.153), r.getCopy().scale(1.153).preciseLocation());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testScaleDimension() {
		// check work scale(double) with rounding errors
		PrecisionRectangle r = new PrecisionRectangle(-9.47, -34.43, 41.95, 25.92);
		assertEquals(r.getCopy().preciseSize().scale(1.153), r.getCopy().scale(1.153).preciseSize());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetSize() {
		PrecisionRectangle r = new PrecisionRectangle(0, 0, 41.95, 25.92);
		assertEquals(r.getSize().getClass(), Dimension.class);
		assertEquals(r.width, r.getSize().width);
		assertEquals(r.height, r.getSize().height);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetSize_Zero() {
		PrecisionRectangle r = new PrecisionRectangle(0.5, 0.5, 0, 0);
		assertEquals(r.width, r.getSize().width);
		assertEquals(r.height, r.getSize().height);
		assertEquals(0, r.getSize().width);
		assertEquals(0, r.getSize().height);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetLocation() {
		PrecisionRectangle r = new PrecisionRectangle(8.94, -34.37, 0, 0);
		assertEquals(r.getLocation().getClass(), Point.class);
		assertEquals(r.x, r.getLocation().x);
		assertEquals(r.y, r.getLocation().y);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetBottomRight() {
		Rectangle rect = new PrecisionRectangle(100.5, 100.5, 250, 250);
		assertEquals(rect.getBottomRight().x - rect.getTopLeft().x, rect.width);
		assertEquals(rect.getBottomRight().y - rect.getTopLeft().y, rect.height);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testConsistencyGetBottom() {
		Rectangle rect = new PrecisionRectangle(100.5, 100.5, 250.5, 250.5);
		assertEquals(rect.getBottomRight().y, rect.getBottom().y);
		assertEquals(rect.getBottomLeft().y, rect.getBottom().y);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testConsistencyGetTop() {
		Rectangle rect = new PrecisionRectangle(100.5, 100.5, 250.5, 250.5);
		assertEquals(rect.getTopRight().y, rect.getTop().y);
		assertEquals(rect.getTopLeft().y, rect.getTop().y);
	}
}
