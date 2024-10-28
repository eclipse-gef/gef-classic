/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.draw2d.geometry.Vector;

import org.junit.jupiter.api.Test;

/**
 * Vector's tests
 *
 * @author aboyko
 *
 */
public class VectorTest {

	@Test
	public void testGetLength() {
		testLengthValues(3, 4, 5);
		testLengthValues(0, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetOrthoComplement() {
		Vector a = new Vector(3, -5);
		assertTrue(a.getOrthogonalComplement().equals(new Vector(5, 3)));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetDotProduct() {
		Vector a = new Vector(3, 2);
		Vector b = new Vector(2, -2);
		assertTrue(a.getDotProduct(b) == 2);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetAngle() {
		Vector a = new Vector(24.03809869102058, -6.868028197434448);
		Vector b = new Vector(-24.038098691020593, 6.868028197434448);
		assertTrue(a.getAngle(b) == 180.0);
	}

	@SuppressWarnings("static-method")
	private void testLengthValues(int x, int y, double expectedLength) {
		Vector vector = new Vector(x, y);
		assertEquals(expectedLength, vector.getLength(), 0);
	}
}
