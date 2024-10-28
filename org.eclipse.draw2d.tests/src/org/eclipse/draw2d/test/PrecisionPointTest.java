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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.draw2d.geometry.PrecisionPoint;

import org.junit.jupiter.api.Test;

/**
 * JUnit Tests for PrecisionPoint.
 *
 * @author Anthony Hunter
 */
public class PrecisionPointTest {

	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=227977
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testEquals() {
		PrecisionPoint p1 = new PrecisionPoint(0.1, 0.1);
		PrecisionPoint p2 = new PrecisionPoint(0.2, 0.2);
		assertFalse(p1.equals(p2));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testTranslate() {
		PrecisionPoint p1 = new PrecisionPoint(0.1, 0.1);
		PrecisionPoint p2 = new PrecisionPoint(0.2, 0.2);
		assertTrue(p2.equals(p1.getTranslated(0.1, 0.1)));
		assertTrue(p2.equals(p1.getTranslated(p1)));

	}
}
