/*******************************************************************************
 * Copyright (c) 2004, 2023 IBM Corporation and others.
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

import java.util.Arrays;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Interval;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.Assertions;

/**
 * @author Pratik Shah
 */
public abstract class BaseTestCase extends Assertions {

	protected static final Font TAHOMA = new Font(null, "Tahoma", 8, 0);//$NON-NLS-1$
	protected static final Font SERIF = new Font(null, "Serif", 8, 0);//$NON-NLS-1$

	public static void assertEquals(Image expected, Image actual) {
		assertTrue(Arrays.equals(expected.getImageData().data, actual.getImageData().data),
				"The given images did not match"); //$NON-NLS-1$
	}

	/**
	 * Asserts that two objects are equal. Expected object
	 * <code>(width, height)</code>. Actual object <code>{@link Dimension}</code>.
	 * If they are not an AssertionFailedError is thrown.
	 */
	public static void assertEquals(int width, int height, Dimension dimension) {
		assertEquals(width, dimension.width);
		assertEquals(height, dimension.height);
	}

	/**
	 * Asserts that two objects are equal. Expected object <code>(x, y)</code>.
	 * Actual object <code>{@link Point}</code>. If they are not an
	 * AssertionFailedError is thrown.
	 */
	public static void assertEquals(int x, int y, Point point) {
		assertEquals(x, point.x);
		assertEquals(y, point.y);
	}

	/**
	 * Asserts that two objects are equal. Expected object
	 * <code>(top, left, bottom, right)</code>. Actual object
	 * <code>{@link Insets}</code>. If they are not an AssertionFailedError is
	 * thrown.
	 */
	public static void assertEquals(int top, int left, int bottom, int right, Insets insets) {
		assertEquals(top, insets.top);
		assertEquals(left, insets.left);
		assertEquals(bottom, insets.bottom);
		assertEquals(right, insets.right);
	}

	/**
	 * Asserts that two objects are equal. Expected object
	 * <code>(x, y, width, height)</code>. Actual object
	 * <code>{@link Rectangle}</code>. If they are not an AssertionFailedError is
	 * thrown.
	 */
	public static void assertEquals(int x, int y, int width, int height, Rectangle rectangle) {
		assertEquals(x, rectangle.x);
		assertEquals(y, rectangle.y);
		assertEquals(width, rectangle.width);
		assertEquals(height, rectangle.height);
	}

	/**
	 * Asserts that two objects are equal. Expected object
	 * <code>(begin, length)</code>. Actual object <code>{@link Interval}</code>. If
	 * they are not an AssertionFailedError is thrown.
	 */
	public static void assertEquals(int begin, int length, Interval interval) {
		assertEquals(begin, interval.begin());
		assertEquals(length, interval.length());
	}

	/**
	 * Waits given number of milliseconds and pumps the SWT event queue.<br>
	 * At least one events loop will be executed.
	 */
	protected static void waitEventLoop(Shell shell, int timeMillis) {
		long start = System.currentTimeMillis();
		do {
			// One of the events might dispose the shell...
			while (!shell.isDisposed() && shell.getDisplay().readAndDispatch()) {
				// dispatch all updates
			}
		} while (System.currentTimeMillis() - start < timeMillis);
	}
}
