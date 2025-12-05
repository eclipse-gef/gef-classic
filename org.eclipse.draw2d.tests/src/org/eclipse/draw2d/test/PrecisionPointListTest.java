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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.PrecisionPointList;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit Tests for {@link PrecisionPointList}.
 */
public class PrecisionPointListTest {
	private static final double EPSILON = 1e-14;

	private static PrecisionPointList create(double... doublePoints) {
		if (doublePoints.length % 2 != 0) {
			throw new IllegalArgumentException("The number of points must be event: " + doublePoints.length); //$NON-NLS-1$
		}
		int[] intPoints = new int[doublePoints.length];
		PrecisionPointList p = new PrecisionPointList(intPoints);
		double[] decimalFractions = p.toDoubleArray();
		for (int i = 0; i < doublePoints.length; i += 2) {
			intPoints[i] = (int) Math.floor(doublePoints[i]);
			intPoints[i + 1] = (int) Math.floor(doublePoints[i + 1]);
			decimalFractions[i] = doublePoints[i] - intPoints[i];
			decimalFractions[i + 1] = doublePoints[i + 1] - intPoints[i + 1];
		}
		return p;
	}

	private PrecisionPointList source;
	private Point p;

	@BeforeEach
	public void setUp() {
		source = create(1.5, 2.33, 8.14, 6.91);
		p = new PrecisionPoint();
	}

	@Test
	public void testAddAll() {
		source.addAll(create(1.33, 7.19));
		assertArrayEquals(new int[] { 1, 2, 8, 6, 1, 7 }, source.toIntArray());
		assertArrayEquals(new double[] { 0.5, 0.33, 0.14, 0.91, 0.33, 0.19 }, source.toDoubleArray(), EPSILON);
	}

	@Test
	public void testAddPoint() {
		source.addPoint(new PrecisionPoint(3.4, 8.53));
		assertArrayEquals(new int[] { 1, 2, 8, 6, 3, 8 }, source.toIntArray());
		assertArrayEquals(new double[] { 0.5, 0.33, 0.14, 0.91, 0.4, 0.53 }, source.toDoubleArray(), EPSILON);
	}

	@Test
	public void testGetCopy() {
		PrecisionPointList copy = (PrecisionPointList) source.getCopy();
		assertNotSame(source, copy);
		assertNotSame(source.toIntArray(), copy.toIntArray());
		assertNotSame(source.toDoubleArray(), copy.toDoubleArray());
		assertArrayEquals(new int[] { 1, 2, 8, 6 }, source.toIntArray());
		assertArrayEquals(new double[] { 0.5, 0.33, 0.14, 0.91 }, source.toDoubleArray(), EPSILON);
	}

	@Test
	public void testGetPointI() {
		p = source.getPoint(0);
		assertEquals(1.5, p.preciseX(), EPSILON);
		assertEquals(2.33, p.preciseY(), EPSILON);
		p = source.getPoint(1);
		assertEquals(8.14, p.preciseX(), EPSILON);
		assertEquals(6.91, p.preciseY(), EPSILON);
	}

	@Test
	public void testGetPointII() {
		source.getPoint(p, 0);
		assertEquals(1.5, p.preciseX(), EPSILON);
		assertEquals(2.33, p.preciseY(), EPSILON);
		source.getPoint(p, 1);
		assertEquals(8.14, p.preciseX(), EPSILON);
		assertEquals(6.91, p.preciseY(), EPSILON);
	}

	@Test
	public void testInsertPoint() {
		source.insertPoint(new PrecisionPoint(4.13, 7.31), 1);
		assertArrayEquals(new int[] { 1, 2, 4, 7, 8, 6 }, source.toIntArray());
		assertArrayEquals(new double[] { 0.5, 0.33, 0.13, 0.31, 0.14, 0.91 }, source.toDoubleArray(), EPSILON);
	}

	@Test
	public void testPerformScale() {
		source.performScale(7.13);
		assertArrayEquals(new int[] { 10, 16, 58, 49 }, source.toIntArray());
		assertArrayEquals(new double[] { 0.695, 0.6129, 0.0382, 0.2683 }, source.toDoubleArray(), EPSILON);
	}

	@Test
	public void testRemovePoint() {
		p = source.removePoint(1);
		assertEquals(8.14, p.preciseX(), EPSILON);
		assertEquals(6.91, p.preciseY(), EPSILON);
		assertArrayEquals(new int[] { 1, 2 }, source.toIntArray());
		assertArrayEquals(new double[] { 0.5, 0.33 }, source.toDoubleArray(), EPSILON);
	}

	@Test
	public void testReverse() {
		source.reverse();
		assertArrayEquals(new int[] { 8, 6, 1, 2 }, source.toIntArray());
		assertArrayEquals(new double[] { 0.14, 0.91, 0.5, 0.33 }, source.toDoubleArray(), EPSILON);
	}

	@Test
	public void testSetPoint() {
		source.setPoint(new PrecisionPoint(2.33, 1.5), 0);
		source.setPoint(new PrecisionPoint(8.14, 7.33), 1);
		assertArrayEquals(new int[] { 2, 1, 8, 7 }, source.toIntArray());
		assertArrayEquals(new double[] { 0.33, 0.5, 0.14, 0.33 }, source.toDoubleArray(), EPSILON);
	}

	@Test
	public void testSetSizeI() {
		source.setSize(3);
		assertEquals(source.size(), 3);
		assertArrayEquals(new int[] { 1, 2, 8, 6, 0, 0 }, source.toIntArray());
		assertArrayEquals(new double[] { 0.5, 0.33, 0.14, 0.91, 0.0, 0.0 }, source.toDoubleArray(), EPSILON);
	}

	@Test
	public void testSetSizeII() {
		source.setSize(1);
		assertEquals(source.size(), 1);
		assertArrayEquals(new int[] { 1, 2 }, source.toIntArray());
		assertArrayEquals(new double[] { 0.5, 0.33 }, source.toDoubleArray(), EPSILON);
	}

	@Test
	public void testTranspose() {
		source.transpose();
		assertArrayEquals(new int[] { 2, 1, 6, 8 }, source.toIntArray());
		assertArrayEquals(new double[] { 0.33, 0.5, 0.91, 0.14 }, source.toDoubleArray(), EPSILON);
	}

	@Test
	public void testGetBounds() {
		Rectangle r = source.getBounds();
		assertEquals(1.5, r.preciseX(), EPSILON);
		assertEquals(2.33, r.preciseY(), EPSILON);
		assertEquals(6.64, r.preciseWidth(), EPSILON);
		assertEquals(4.58, r.preciseHeight(), EPSILON);
	}
}
