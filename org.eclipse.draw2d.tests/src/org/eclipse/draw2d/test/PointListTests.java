/*******************************************************************************
 * Copyright (c) 2005, 2023 IBM Corporation and others.
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
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.Test;

public class PointListTests extends BaseTestCase {

	@SuppressWarnings("static-method")
	@Test
	public void testIntersects() {
		PointList points = new PointList();
		points.addPoint(0, 0);
		points.addPoint(0, 3);
		// simple case
		assertTrue(points.intersects(new Rectangle(-10, 0, 20, 3)));
		// line starts and ends on opposite edges of the rectangle
		assertTrue(points.intersects(new Rectangle(-2, 1, 4, 1)));
		// parallel
		assertTrue(points.intersects(new Rectangle(0, 0, 10, 10)));
		// vertical
		assertTrue(points.intersects(new Rectangle(0, 3, 10, 10)));
		// line is next to the rectangle (left side)
		assertFalse(points.intersects(new Rectangle(1, 1, 50, 50)));
		// line is next to the rectangle (right side)
		assertFalse(points.intersects(new Rectangle(-5, 0, 5, 5)));
		// empty rect located on the line
		assertFalse(points.intersects(new Rectangle(0, 1, 0, 0)));

		points.addPoint(10, 10);
		// line cuts through two adjacent sides of the rectangle
		assertTrue(points.intersects(new Rectangle(-50, 5, 58, 100)));
		points.removePoint(2);

		points.addPoint(-1, -3);
		// line doesn't intersect with the rect's diagonals
		assertTrue(points.intersects(new Rectangle(-10, 2, 40, 50)));
		points.removeAllPoints();

		points.addPoint(10, 10);
		points.addPoint(8, 12);
		// line starts on rectangle's bottom-right corner
		assertTrue(points.intersects(new Rectangle(0, 0, 11, 11)));
		// line is tangential to the rectangle's bottom-right corner
		assertTrue(points.intersects(new Rectangle(1, 1, 9, 11)));

		points.addPoint(new PrecisionPoint(1.49, 1.49));
		assertFalse(points.intersects(new Rectangle(0, 0, 1, 1)));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testAddPointGetPointInt() {
		PointList list = new PointList();
		list.addPoint(10, 20);
		list.addPoint(new Point(-90, 0));
		list.addPoint(120, -70);
		//
		// check add null point
		assertThrows(NullPointerException.class, () -> list.addPoint(null));
		//
		// check work getPoint()
		assertEquals(3, list.size());
		assertEquals(10, 20, list.getPoint(0));
		assertEquals(-90, 0, list.getPoint(1));
		assertEquals(120, -70, list.getPoint(2));
		//
		// check work getPoint() with wrong index
		assertThrows(IndexOutOfBoundsException.class, () -> list.getPoint(-1));
		//
		// check work getPoint() with wrong index
		assertThrows(IndexOutOfBoundsException.class, () -> list.getPoint(3));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testInsertPoint() {
		PointList list = new PointList();
		//
		// check insert point use wrong index
		assertThrows(IndexOutOfBoundsException.class, () -> list.insertPoint(new Point(), 1));
		//
		// check work insertPoint()
		list.insertPoint(new Point(), 0);
		list.addPoint(10, 20);
		list.addPoint(new Point(-90, 0));
		list.insertPoint(new Point(-1, -1), 1);
		//
		assertEquals(4, list.size());
		assertEquals(0, 0, list.getPoint(0));
		assertEquals(-1, -1, list.getPoint(1));
		assertEquals(10, 20, list.getPoint(2));
		assertEquals(-90, 0, list.getPoint(3));
		//
		// check insert null point
		assertThrows(NullPointerException.class, () -> list.insertPoint(null, 0));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testRemovePoint() {
		PointList list = new PointList();
		//
		list.addPoint(10, -20);
		list.addPoint(-90, 0);
		list.addPoint(120, 120);
		assertEquals(3, list.size());
		//
		// check remove point use wrong index
		assertThrows(IndexOutOfBoundsException.class, () -> list.removePoint(4));
		//
		// check work removePoint()
		list.removePoint(2);
		assertEquals(2, list.size());
		assertEquals(10, -20, list.getPoint(0));
		assertEquals(-90, 0, list.getPoint(1));
		//
		// check work removePoint()
		list.removePoint(0);
		assertEquals(1, list.size());
		assertEquals(-90, 0, list.getPoint(0));
		//
		// check work removePoint()
		list.removePoint(0);
		assertEquals(0, list.size());
		//
		// check remove point use wrong index
		assertThrows(IndexOutOfBoundsException.class, () -> list.removePoint(0));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testRemoveAllPoints() {
		PointList list = new PointList();
		//
		// check work removeAllPoints() when not childrens
		list.removeAllPoints();
		assertEquals(0, list.size());
		//
		list.addPoint(10, -20);
		list.addPoint(-90, 0);
		list.addPoint(120, 120);
		assertEquals(3, list.size());
		//
		// check work removeAllPoints()
		list.removeAllPoints();
		assertEquals(0, list.size());
		//
		// check work removeAllPoints() when not childrens
		list.removeAllPoints();
		assertEquals(0, list.size());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testSizeSetSize() {
		assertEquals(0, new PointList().size());
		assertEquals(0, new PointList(7).size());
		//
		PointList list = new PointList();
		//
		list.addPoint(10, -20);
		list.addPoint(-90, 0);
		list.addPoint(120, 120);
		assertEquals(3, list.size());
		//
		// check work setSize()
		list.setSize(5);
		assertEquals(5, list.size());
		assertEquals(120, 120, list.getPoint(2));
		assertEquals(0, 0, list.getPoint(3));
		assertEquals(0, 0, list.getPoint(4));
		//
		// check work setSize()
		list.setSize(1);
		assertEquals(1, list.size());
		assertEquals(10, -20, list.getPoint(0));
		//
		// check work setSize()
		list.setSize(0);
		assertEquals(0, list.size());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testToIntArray() {
		PointList list = new PointList(5);
		list.addPoint(10, -20);
		list.addPoint(-90, 0);
		list.addPoint(120, 120);
		//
		int[] points = list.toIntArray();
		assertNotNull(points);
		assertEquals(6, points.length);
		//
		for (int i = 0; i < points.length; i += 2) {
			assertEquals(points[i], points[i + 1], list.getPoint(i / 2));
		}
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetCopy() {
		PointList list = new PointList();
		list.addPoint(10, -20);
		list.addPoint(-90, 0);
		list.addPoint(120, 120);
		//
		PointList copy = list.getCopy();
		assertNotNull(copy);
		assertNotSame(list, copy);
		assertEquals(list.size(), copy.size());
		//
		for (int i = 0; i < list.size(); i++) {
			assertEquals(copy.getPoint(i), copy.getPoint(i));
		}
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetFirstPoint() {
		PointList list = new PointList();
		//
		list.addPoint(10, -20);
		assertEquals(10, -20, list.getFirstPoint());
		//
		list.addPoint(-90, 0);
		list.addPoint(120, 120);
		assertEquals(10, -20, list.getFirstPoint());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetLastPoint() {
		PointList list = new PointList();
		//
		list.addPoint(10, -20);
		assertEquals(10, -20, list.getLastPoint());
		//
		list.addPoint(-90, 0);
		assertEquals(-90, 0, list.getLastPoint());
		//
		list.addPoint(120, 120);
		assertEquals(120, 120, list.getLastPoint());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetMidpoint() {
		PointList list = new PointList();
		list.addPoint(10, -20);
		list.addPoint(-90, 0);
		list.addPoint(120, 120);
		assertEquals(-90, 0, list.getMidpoint());
		//
		list.removeAllPoints();
		list.addPoint(10, 10);
		list.addPoint(20, 20);
		list.addPoint(40, 40);
		list.addPoint(50, 50);
		assertEquals(30, 30, list.getMidpoint());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetPointPointInt() {
		PointList list = new PointList();
		list.addPoint(10, -20);
		list.addPoint(40, 40);
		//
		// check work getPoint() use wrong index
		assertThrows(IndexOutOfBoundsException.class, () -> list.getPoint(new Point(), -1));
		//
		// check work getPoint() use wrong index
		assertThrows(IndexOutOfBoundsException.class, () -> list.getPoint(new Point(), 2));
		//
		// check work getPoint() for null point argument
		assertThrows(NullPointerException.class, () -> list.getPoint(null, 0));
		//
		// check work getPoint()
		Point point = new Point();
		//
		list.getPoint(point, 1);
		assertEquals(40, 40, point);
		//
		list.getPoint(point, 0);
		assertEquals(10, -20, point);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testSetPointPointInt() {
		PointList list = new PointList();
		list.addPoint(10, -20);
		list.addPoint(40, 40);
		//
		// check work setPoint() use wrong index
		assertThrows(IndexOutOfBoundsException.class, () -> list.setPoint(new Point(), -1));
		//
		// check work setPoint() use wrong index
		assertThrows(IndexOutOfBoundsException.class, () -> list.setPoint(new Point(), 2));
		//
		// check work setPoint() for null point argument
		assertThrows(NullPointerException.class, () -> list.setPoint(null, 0));
		//
		// check work setPoint()
		Point point = new Point(3, 4);
		list.setPoint(point, 1);
		assertEquals(3, 4, point);
		assertNotSame(point, list.getPoint(1));
		assertEquals(point, list.getPoint(1));
		//
		// check work setPoint()
		point = new Point(-1, 2);
		list.setPoint(point, 0);
		assertEquals(-1, 2, point);
		assertNotSame(point, list.getPoint(0));
		assertEquals(point, list.getPoint(0));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetBounds() {
		PointList list = new PointList();
		//
		list.addPoint(10, 10);
		list.addPoint(20, 20);
		Rectangle bounds = list.getBounds();
		assertEquals(10, 10, 11, 11, bounds);
		assertSame(bounds, list.getBounds());
		//
		list.addPoint(40, 40);
		Rectangle boundsNew = list.getBounds();
		assertEquals(10, 10, 31, 31, boundsNew);
		assertNotSame(bounds, boundsNew);
		assertSame(boundsNew, list.getBounds());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetSmallestBounds() {
		PointList list = new PointList();
		//
		list.addPoint(10, 10);
		list.addPoint(20, 20);
		assertEquals(10, 10, 11, 11, list.getBounds());

		list.setPoint(new Point(20, 20), 0);
		assertEquals(20, 20, 1, 1, list.getBounds());
		//
		list.removeAllPoints();

		list.addPoint(10, 10);
		list.addPoint(20, 20);
		assertEquals(10, 10, 11, 11, list.getBounds());

		list.removePoint(1);
		assertEquals(10, 10, 1, 1, list.getBounds());
		//
		list.removeAllPoints();

		list.addPoint(10, 10);
		assertEquals(10, 10, 1, 1, list.getBounds());

		list.addPoint(0, 0);
		assertEquals(0, 0, 11, 11, list.getBounds());

		list.addPoint(20, 20);
		assertEquals(0, 0, 21, 21, list.getBounds());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testTranslate() {
		PointList list = new PointList();
		for (int i = 0; i < 5; i++) {
			list.addPoint(i, -i);
		}
		/*
		 * ====== int, int ======
		 */
		list.translate(0, 0);
		assertEquals(0, -4, 5, 5, list.getBounds());
		//
		for (int i = 0; i < list.size(); i++) {
			assertEquals(i, -i, list.getPoint(i));
		}
		/*
		 * ====== int, int ======
		 */
		list.translate(10, -10);
		assertEquals(10, -14, 5, 5, list.getBounds());
		//
		for (int i = 0; i < list.size(); i++) {
			assertEquals(10 + i, -10 - i, list.getPoint(i));
		}
		/*
		 * ====== Point ======
		 */
		Point point = new Point(-5, 5);
		list.translate(point);
		assertEquals(-5, 5, point);
		assertEquals(5, -9, 5, 5, list.getBounds());
		//
		for (int i = 0; i < list.size(); i++) {
			assertEquals(5 + i, -5 - i, list.getPoint(i));
		}
		/*
		 * ====== Dimension ======
		 */
		Dimension dimension = new Dimension(1, 2);
		list.performTranslate(dimension);
		assertEquals(1, 2, dimension);
		assertEquals(6, -7, 5, 5, list.getBounds());
		//
		for (int i = 0; i < list.size(); i++) {
			assertEquals(6 + i, -3 - i, list.getPoint(i));
		}
		/*
		 * ====== Insets ======
		 */
		Insets insets = new Insets(1, -2, 3, 4);
		list.performTranslate(insets);
		assertEquals(1, -2, 3, 4, insets);
		assertEquals(4, -6, 5, 5, list.getBounds());
		//
		for (int i = 0; i < list.size(); i++) {
			assertEquals(4 + i, -2 - i, list.getPoint(i));
		}
	}

}