/*******************************************************************************
 * Copyright (c) 2011, 2023 Google, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Google, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.test;

import org.eclipse.draw2d.geometry.Insets;

import org.junit.jupiter.api.Test;

/**
 * @author lobas_av
 *
 */
public class InsetsTest extends BaseTestCase {

	@SuppressWarnings("static-method")
	@Test
	public void testConstructors() {
		// check create object use constructor()
		assertEquals(0, 0, 0, 0, new Insets());
		//
		// check create object use constructor(int)
		assertEquals(7, 7, 7, 7, new Insets(7));
		//
		// check create object use constructor(int, int, int, int)
		assertEquals(1, 2, 3, 4, new Insets(1, 2, 3, 4));
		//
		// check create object use constructor(Insets)
		assertEquals(1, 2, 3, 4, new Insets(new Insets(1, 2, 3, 4)));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testEqualsObject() {
		Insets testInsets = new Insets(1, 2, 3, 4);
		assertNotNull(testInsets);
		assertFalse(testInsets.equals(new Object()));
		assertTrue(testInsets.equals(testInsets));
		assertTrue(testInsets.equals(new Insets(testInsets)));
		assertFalse(testInsets.equals(new Insets()));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testToString() {
		assertNotNull(new Insets().toString());
		assertNotNull(new Insets(3).toString());
		assertNotNull(new Insets(1, 2, 3, 4).toString());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetHeight() {
		assertEquals(11, new Insets(1, 1, 10, 5).getHeight());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetWidth() {
		assertEquals(6, new Insets(1, 1, 10, 5).getWidth());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testIsEmpty() {
		assertTrue(new Insets().isEmpty());
		assertFalse(new Insets(7).isEmpty());
		assertFalse(new Insets(1, 0, 0, 0).isEmpty());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testAddInsets() {
		Insets testInsets = new Insets(1, 2, 3, 4);
		assertSame(testInsets, testInsets.add(new Insets(4, 3, 2, 1)));
		assertEquals(5, 5, 5, 5, testInsets);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testTranspose() {
		int top = 1;
		int left = 2;
		int bottom = 3;
		int right = 4;
		Insets testInsets = new Insets(top, left, bottom, right);
		assertSame(testInsets, testInsets.transpose());
		assertEquals(left, top, right, bottom, testInsets);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetAdded() {
		Insets template = new Insets(1, 2, 3, 4);
		Insets testInsets = template.getAdded(new Insets(4, 3, 2, 1));
		assertNotSame(template, testInsets);
		assertEquals(5, 5, 5, 5, testInsets);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetTransposed() {
		int top = 1;
		int left = 2;
		int bottom = 3;
		int right = 4;
		Insets template = new Insets(top, left, bottom, right);
		Insets testInsets = template.getTransposed();
		assertNotSame(template, testInsets);
		assertEquals(left, top, right, bottom, testInsets);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetNegated() {
		Insets template = new Insets(1, 2, 3, 4);
		Insets testInsets = template.getNegated();
		assertNotSame(template, testInsets);
		assertEquals(1, 2, 3, 4, template);
		assertEquals(-1, -2, -3, -4, testInsets);
	}
}