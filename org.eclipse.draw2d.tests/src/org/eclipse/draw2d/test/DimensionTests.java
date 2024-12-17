/*******************************************************************************
 * Copyright (c) 2012, 2023 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.test;

import org.eclipse.swt.graphics.Image;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import org.junit.jupiter.api.Test;

public class DimensionTests extends BaseTestCase {

	@SuppressWarnings("static-method")
	@Test
	public void testGetExpanded() {
		Dimension d = new Dimension(3, 5);
		assertEquals(new Dimension(6, 7), d.getExpanded(3, 2));
		assertEquals(new Dimension(6, 7), d.getExpanded(3.4, 2.7));

		// check work getExpanded(Dimension)
		Dimension template1 = new Dimension(17, 18);
		Dimension template2 = new Dimension(11, 10);
		Dimension testDimension = template1.getExpanded(template2);
		assertNotNull(testDimension);
		assertNotSame(template1, testDimension);
		assertNotSame(template2, testDimension);
		assertEquals(28, 28, testDimension);
		assertEquals(17, 18, template1);
		assertEquals(11, 10, template2);

		// check work getExpanded(int, int)
		testDimension = template1.getExpanded(-3, 3);
		assertNotNull(testDimension);
		assertNotSame(template1, testDimension);
		assertEquals(14, 21, testDimension);
		assertEquals(17, 18, template1);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetShrinked() {
		Dimension d = new Dimension(6, 7);
		assertEquals(new Dimension(3, 5), d.getShrinked(3, 2));
		assertEquals(new Dimension(3, 5), d.getShrinked(3.4, 2.7));
	}

	@Test
	public void testConstructors() {
		Image image = new Image(null, getClass().getResourceAsStream("icons/recorder.gif")); //$NON-NLS-1$

		assertEquals(0, 0, new Dimension());
		//
		assertEquals(-7, 8, new Dimension(-7, 8));
		//
		assertEquals(-7, 8, new Dimension(new Dimension(-7, 8)));
		//
		assertEquals(-7, 8, new Dimension(new org.eclipse.swt.graphics.Point(-7, 8)));
		//
		assertEquals(16, 16, new Dimension(image));

		image.dispose();
	}

	@SuppressWarnings("static-method")
	@Test
	public void testEqualsObject() {
		Dimension testDimension = new Dimension(-7, 8);
		assertNotNull(testDimension);
		assertFalse(testDimension.equals(new Object()));
		assertTrue(testDimension.equals(testDimension));
		assertTrue(testDimension.equals(new Dimension(testDimension)));
		assertFalse(testDimension.equals(new Dimension()));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testToString() {
		assertNotNull(new Dimension().toString());
		assertNotNull(new Dimension(1, 2).toString());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testContains() {
		Dimension template = new Dimension(-7, 8);
		assertTrue(template.contains(template));
		assertTrue(template.contains(new Dimension(-7, 8)));
		assertTrue(template.contains(new Dimension(-8, 7)));
		assertTrue(template.contains(new Dimension(-8, 8)));
		assertFalse(template.contains(new Dimension()));
		assertFalse(template.contains(new Dimension(10, 10)));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testContainsProper() {
		Dimension template = new Dimension(-7, 8);
		assertFalse(template.containsProper(template));
		assertTrue(template.containsProper(new Dimension(-8, -8)));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetArea() {
		assertEquals(6, new Dimension(2, 3).getArea());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testEqualsIntInt() {
		assertTrue(new Dimension().equals(0, 0));
		assertTrue(new Dimension(1, -2).equals(1, -2));
		assertFalse(new Dimension(1, -2).equals(7, -7));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testIsEmpty() {
		assertTrue(new Dimension().isEmpty());
		assertTrue(new Dimension(1, -2).isEmpty());
		assertFalse(new Dimension(3, 3).isEmpty());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testSetSize() {
		Dimension template = new Dimension(-7, 8);
		Dimension testDimension = new Dimension();
		testDimension.setSize(template);
		assertEquals(template, testDimension);
	}

	@SuppressWarnings({ "static-method", "removal" })
	@Test
	public void testExpand() {
		// check work expand(Dimension)
		Dimension template = new Dimension(-1, 1);
		Dimension testDimension = new Dimension(2, 3);
		assertSame(testDimension, testDimension.expand(template));
		assertEquals(1, 4, testDimension);
		assertEquals(-1, 1, template);
		//
		// check work expand(Point)
		Point point = new Point(5, 1);
		assertSame(testDimension, testDimension.expand(point));
		assertEquals(6, 5, testDimension);
		assertEquals(5, 1, point);
		//
		// check work expand(int, int)
		assertSame(testDimension, testDimension.expand(-3, 5));
		assertEquals(3, 10, testDimension);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testIntersect() {
		Dimension template = new Dimension(-7, 8);
		Dimension testDimension = new Dimension(0, 5);
		assertSame(testDimension, testDimension.intersect(template));
		assertEquals(-7, 5, testDimension);
		assertEquals(-7, 8, template);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testNegate() {
		Dimension testDimension = new Dimension(1, 2);
		assertSame(testDimension, testDimension.negate());
		assertEquals(-1, -2, testDimension);
		assertSame(testDimension, testDimension.negate());
		assertEquals(1, 2, testDimension);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testScale() {
		// check work scale(double)
		Dimension testDimension = new Dimension(10, 20);
		assertSame(testDimension, testDimension.scale(0.5));
		assertEquals(5, 10, testDimension);
		//
		// check work scale(double, double)
		assertSame(testDimension, testDimension.scale(20, 10));
		assertEquals(100, 100, testDimension);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testShrink() {
		Dimension testDimension = new Dimension(3, 5);
		assertSame(testDimension, testDimension.shrink(1, 2));
		assertEquals(2, 3, testDimension);
		assertSame(testDimension, testDimension.shrink(-1, 0));
		assertEquals(3, 3, testDimension);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testTranspose() {
		Dimension testDimension = new Dimension(3, 5);
		assertSame(testDimension, testDimension.transpose());
		assertEquals(5, 3, testDimension);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testUnion() {
		// check work union(Dimension)
		Dimension template = new Dimension(-7, 8);
		Dimension testDimension = new Dimension(3, 5);
		assertSame(testDimension, testDimension.union(template));
		assertEquals(3, 8, testDimension);
		assertEquals(-7, 8, template);
		//
		// check work union(int, int)
		assertSame(testDimension, testDimension.union(5, 0));
		assertEquals(5, 8, testDimension);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetCopy() {
		Dimension template = new Dimension(-7, 8);
		Dimension testDimension = template.getCopy();
		assertNotNull(testDimension);
		assertNotSame(template, testDimension);
		assertEquals(template, testDimension);
	}

	@SuppressWarnings({ "static-method", "removal" })
	@Test
	public void testGetDifference() {
		Dimension template1 = new Dimension(17, 18);
		Dimension template2 = new Dimension(11, 10);
		Dimension testDimension = template1.getDifference(template2);
		assertNotNull(testDimension);
		assertNotSame(template1, testDimension);
		assertNotSame(template2, testDimension);
		assertEquals(6, 8, testDimension);
		assertEquals(17, 18, template1);
		assertEquals(11, 10, template2);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetIntersected() {
		Dimension template1 = new Dimension(-7, 8);
		Dimension template2 = new Dimension(0, 5);
		Dimension testDimension = template1.getIntersected(template2);
		assertNotNull(testDimension);
		assertNotSame(template1, testDimension);
		assertNotSame(template2, testDimension);
		assertEquals(-7, 5, testDimension);
		assertEquals(-7, 8, template1);
		assertEquals(0, 5, template2);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetNegated() {
		Dimension template = new Dimension(-7, 8);
		Dimension testDimension = template.getNegated();
		assertNotNull(testDimension);
		assertNotSame(template, testDimension);
		assertEquals(7, -8, testDimension);
		assertEquals(-7, 8, template);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetScaled() {
		Dimension template = new Dimension(10, 20);
		Dimension testDimension = template.getScaled(0.5);
		assertNotNull(testDimension);
		assertNotSame(template, testDimension);
		assertEquals(5, 10, testDimension);
		assertEquals(10, 20, template);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetTransposed() {
		Dimension template = new Dimension(3, 5);
		Dimension testDimension = template.getTransposed();
		assertNotNull(testDimension);
		assertNotSame(template, testDimension);
		assertEquals(5, 3, testDimension);
		assertEquals(3, 5, template);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetUnioned() {
		// check work getUnioned(Dimension)
		Dimension template1 = new Dimension(-7, 8);
		Dimension template2 = new Dimension(0, 5);
		Dimension testDimension = template1.getUnioned(template2);
		assertNotNull(testDimension);
		assertNotSame(template1, testDimension);
		assertNotSame(template2, testDimension);
		assertEquals(0, 8, testDimension);
		assertEquals(-7, 8, template1);
		assertEquals(0, 5, template2);
		//
		// check work getUnioned(int, int)
		testDimension = template1.getUnioned(0, 5);
		assertNotNull(testDimension);
		assertNotSame(template1, testDimension);
		assertEquals(0, 8, testDimension);
		assertEquals(-7, 8, template1);
	}
}
