/*******************************************************************************
 * Copyright (c) 2023 Patrick Ziegler and others.
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

import java.text.Bidi;
import java.util.ServiceLoader;

import org.eclipse.draw2d.text.BidiProvider;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BidiProcessorTest extends BaseTestCase {
	private ClassLoader classLoader;

	@BeforeEach
	public void setUp() {
		classLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
	}

	@AfterEach
	public void tearDown() {
		Thread.currentThread().setContextClassLoader(classLoader);
	}

	@Test
	@SuppressWarnings("static-method")
	public void testSpi() {
		ServiceLoader<BidiProvider> serviceLoader = ServiceLoader.load(BidiProvider.class);
		BidiProvider service = serviceLoader.findFirst().orElse(null);

		assertNotNull(service);
		assertTrue(service instanceof BidiProviderMock);
	}

	public static class BidiProviderMock implements BidiProvider {
		@Override
		public boolean requiresBidi(char[] text, int start, int limit) {
			return Bidi.requiresBidi(text, start, limit);
		}
	}
}
