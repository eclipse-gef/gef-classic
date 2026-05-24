/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
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

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.draw2d.AutoscaleFreeformViewport;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class AutoscaleFreeformViewportTest {
	private static final Rectangle VIEWPORT_BOUNDS = new Rectangle(0, 0, 931, 687);
	private AutoscaleFreeformViewport viewport;

	@BeforeEach
	public void setUp() throws Exception {
		viewport = new AutoscaleFreeformViewport(false);
		viewport.setContents(new FreeformLayer());
		viewport.setBounds(VIEWPORT_BOUNDS);
	}

	/**
	 * @see <a href="https://github.com/eclipse-gef/gef-classic/issues/1116">here</a>
	 */
	@ParameterizedTest
	@ValueSource(doubles = { 1.0, 1.25, 1.33, 1.5, 1.66, 1.75, 2.0, 2.5, 3.0 })
	public void testLayerBoundsWithScale(double scale) {
		viewport.setScale(scale);
		viewport.validate();

		Rectangle layerBounds = viewport.getContents().getBounds().getCopy();
		viewport.getContents().translateToAbsolute(layerBounds);
		assertTrue(layerBounds.width <= VIEWPORT_BOUNDS.width,
				"Layer width should not exceed viewport width: " + layerBounds.width); //$NON-NLS-1$
		assertTrue(layerBounds.height <= VIEWPORT_BOUNDS.height,
				"Layer height should not exceed viewport height: " + layerBounds.height); //$NON-NLS-1$
	}
}
