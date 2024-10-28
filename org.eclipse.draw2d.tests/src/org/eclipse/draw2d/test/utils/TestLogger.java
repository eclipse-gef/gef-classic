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
package org.eclipse.draw2d.test.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;

/**
 * Helper class for logging into test cases.
 */
public class TestLogger extends Assertions {
	private final List<String> events = new ArrayList<>();

	/**
	 * Removes all logged events.
	 */
	public void clear() {
		events.clear();
	}

	/**
	 * Asserts that this {@link TestLogger} has no events.
	 */
	public void assertEmpty() {
		assertTrue(events.isEmpty());
	}

	/**
	 * Asserts that this {@link TestLogger} contains same events as in expected one.
	 */
	public void assertEquals(TestLogger expectedLogger) {
		assertEquals(expectedLogger, this);
	}

	/**
	 * Asserts that two objects are equal. If they are not an AssertionFailedError
	 * is thrown.
	 */
	public static void assertEquals(TestLogger expectedLogger, TestLogger actualLoogger) {
		assertEquals(expectedLogger.events, actualLoogger.events);
		expectedLogger.clear();
		actualLoogger.clear();
	}

	/**
	 * Logs new event.
	 */
	public void log(String message) {
		events.add(message);
	}
}