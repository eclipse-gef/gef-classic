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

package org.eclipse.gef.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.FigureUtilities;

import org.eclipse.gef.internal.ui.rulers.RulerFigure;
import org.eclipse.gef.rulers.RulerProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RulerFigureTest {
	// The height might vary across different systems and is only an estimate.
	// Though in general, the height should be roughly the same among all systems.
	// Especially between Windows/Linux and MacOS, because they are using a
	// different DPI.
	private static final int HEIGHT_IN_INCH = 10;
	private RulerFigureMock figure;

	@BeforeEach
	public void setUp() {
		figure = new RulerFigureMock(false, new RulerProviderMock());
	}

	@Test
	public void testGetDPU_px() {
		figure.setUnit(RulerProvider.UNIT_PIXELS);
		assertEquals(1.0, figure.getDPU());
	}

	@Test
	public void testGetDPU_in() {
		figure.setUnit(RulerProvider.UNIT_INCHES);
		double heightInInch = getDialogFontHeight() / figure.getDPU();
		assertEquals(HEIGHT_IN_INCH, heightInInch, 0.15 * heightInInch,
				() -> "DPU: %s, Font Height: %s".formatted(figure.getDPU(), getDialogFontHeight())); //$NON-NLS-1$
	}

	@Test
	public void testGetDPU_cm() {
		figure.setUnit(RulerProvider.UNIT_CENTIMETERS);
		double heightInCentimeter = getDialogFontHeight() / figure.getDPU();
		assertEquals(HEIGHT_IN_INCH * 2.54, heightInCentimeter, 0.15 * heightInCentimeter,
				() -> "DPU: %s, Font Height: %s".formatted(figure.getDPU(), getDialogFontHeight())); //$NON-NLS-1$
	}

	/**
	 * Use the font height to make sure that the DPU is scaled by the same factor.
	 * This is because the DPU depends on the screen DPI, which is not consistent
	 * across different platforms. On Windows, it's generally 92, on MacOS 72 and on
	 * Linux it depends on the system scaling.
	 */
	private static int getDialogFontHeight() {
		return Display.getDefault().syncCall(() -> {
			Font f = new Font(null, "Dialog", 600, SWT.NORMAL); //$NON-NLS-1$
			int height = FigureUtilities.getFontMetrics(f).getHeight();
			f.dispose();
			return height;
		});
	}

	private static class RulerProviderMock extends RulerProvider {
		// no-op
	}

	private static class RulerFigureMock extends RulerFigure {
		public RulerFigureMock(boolean isHorizontal, RulerProvider rulerProvider) {
			super(isHorizontal, rulerProvider);
		}

		@Override
		protected double getDPU() {
			return super.getDPU();
		}
	}
}
