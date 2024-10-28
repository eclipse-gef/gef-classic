/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Research Group Software Construction,
 *     RWTH Aachen University, Germany - Contribution for Bugzilla 245182
 */
package org.eclipse.draw2d.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Straight;
import org.eclipse.draw2d.geometry.Vector;

import org.junit.Test;

/**
 * @author Alexander Nyssen
 *
 */
public class StraightTest {

	@SuppressWarnings("static-method")
	@Test
	public void testGetIntersection() {
		// test integer precision
		Vector p = new Vector(1, 1);
		Vector a = new Vector(2, 1);
		Vector q = new Vector(1, 4);
		Vector b = new Vector(1, -1);
		Straight s1 = new Straight(p, a);
		Straight s2 = new Straight(q, b);
		Vector intersection = s1.getIntersection(s2);
		assertTrue(intersection.equals(new Vector(3, 2)));
		assertTrue(s1.contains(intersection));
		assertTrue(s2.contains(intersection));

		// check straight does not intersect itself
		assertNull(new Straight(p, a).getIntersection(new Straight(p, a)));

		// test double precision
		p = new Vector(0, 0);
		a = new Vector(new PrecisionPoint(0, 0), new PrecisionPoint(5, 5));
		q = new Vector(0, 5);
		b = new Vector(new PrecisionPoint(0, 5), new PrecisionPoint(5, 0));
		s1 = new Straight(p, a);
		s2 = new Straight(q, b);
		intersection = s1.getIntersection(s2);
		assertTrue(intersection.equals(new Vector(2.5, 2.5)));
		assertTrue(s1.contains(intersection));
		assertTrue(s2.contains(intersection));

		PrecisionPoint p1 = new PrecisionPoint(-2, 1);
		PrecisionPoint p2 = new PrecisionPoint(1, 1);
		PrecisionPoint p3 = new PrecisionPoint(0, 0);
		PrecisionPoint p4 = new PrecisionPoint(0, 3);
		s1 = new Straight(p1, p2);
		s2 = new Straight(p3, p4);
		intersection = s1.getIntersection(s2);
		assertTrue(intersection.equals(new Vector(0, 1)));
		assertTrue(s1.contains(intersection));
		assertTrue(s2.contains(intersection));

		// check four rounding effects
		p1 = new PrecisionPoint(-50, 5);
		p2 = new PrecisionPoint(7, 104);
		p3 = new PrecisionPoint(0, 0);
		p4 = new PrecisionPoint(0, 3);

		s1 = new Straight(p1, p2);
		s2 = new Straight(p3, p4);
		assertTrue(s1.intersects(s2));
		intersection = s1.getIntersection(s2);
		assertNotNull(intersection);
		assertTrue(s1.contains(intersection));
		assertTrue(s2.contains(intersection));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testIsParallelTo() {
		Straight s1 = new Straight(new Vector(0, 0), new Vector(3, 3));
		Straight s2 = new Straight(new Vector(0, 4), new Vector(2, 2));
		assertTrue(s1.isParallelTo(s2));

		s1 = new Straight(new Vector(0, 0), new Vector(5, 5));
		s2 = new Straight(new Vector(0, 5), new Vector(0, 5));
		assertFalse(s1.isParallelTo(s2));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetAngle() {
		Straight s1 = new Straight(new Vector(0, 0), new Vector(3, 3));
		Straight s2 = new Straight(new Vector(0, 4), new Vector(2, 2));
		assertTrue(s1.getAngle(s2) == 0.0);

		s1 = new Straight(new Vector(0, 0), new Vector(5, 5));
		s2 = new Straight(new Vector(0, 5), new Vector(0, 5));
		assertTrue((float) s1.getAngle(s2) == 45.0); // rounding effects
	}

	@SuppressWarnings("static-method")
	@Test
	public void testEquals() {
		Straight s1 = new Straight(new Vector(0, 0), new Vector(3, 3));
		Straight s2 = new Straight(new Vector(4, 4), new Vector(2, 2));
		assertTrue(s1.equals(s2));
	}
}
