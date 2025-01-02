/*******************************************************************************
 * Copyright (c) 2024 Patrick Ziegler and others.
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

package org.eclipse.gef.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.eclipse.gef.ui.palette.PaletteColorProvider;

import org.junit.jupiter.api.Test;

public class PaletteColorProviderTest {
	private final PaletteColorProvider provider = PaletteColorProvider.INSTANCE;

	@Test
	public void testInvalidMixedButtonDarker1() {
		assertThrows(IllegalArgumentException.class, () -> provider.getButtonDarker(-0.1));
	}

	@Test
	public void testInvalidMixedButtonDarker2() {
		assertThrows(IllegalArgumentException.class, () -> provider.getButtonDarker(1.1));
	}

	@Test
	public void testInvalidMixedListBackground1() {
		assertThrows(IllegalArgumentException.class, () -> provider.getListBackground(-0.1));
	}

	@Test
	public void testInvalidMixedListBackground2() {
		assertThrows(IllegalArgumentException.class, () -> provider.getListBackground(1.1));
	}

	@Test
	public void testValidMixedListBackground() {
		assertEquals(provider.getListBackground(0.0), provider.getListBackground());
		assertEquals(provider.getListBackground(1.0), provider.getButton());
		// Colors are reused
		assertSame(provider.getListBackground(0.5), provider.getListBackground(0.5));
	}

	@Test
	public void testValidMixedButtonDarker() {
		assertEquals(provider.getButtonDarker(0.0), provider.getButtonDarker());
		assertEquals(provider.getButtonDarker(1.0), provider.getButton());
		// Colors are reused
		assertSame(provider.getButtonDarker(0.5), provider.getButtonDarker(0.5));
	}
}
