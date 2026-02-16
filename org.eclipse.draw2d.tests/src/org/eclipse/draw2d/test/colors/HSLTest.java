/*******************************************************************************
 * Copyright (c) 2026 Johannes Kepler University Linz
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alois Zoitl - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d.test.colors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.eclipse.swt.graphics.RGB;

import org.eclipse.draw2d.colors.HSL;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class HSLTest {

	private static final double UNIT_EPSILON = 0.001;
	private static final double HUE_EPSILON = 0.1;

	static Stream<Arguments> colorProvider() {
		return Stream.of(
				// Format: R, G, B, Hue, Sat, Light
				// well known colors
				Arguments.of(255, 0, 0, 0.0, 1.0, 0.5), // Red
				Arguments.of(0, 255, 0, 120.0, 1.0, 0.5), // Green
				Arguments.of(0, 0, 255, 240.0, 1.0, 0.5), // Blue
				Arguments.of(255, 255, 0, 60.0, 1.0, 0.5), // Yellow
				Arguments.of(0, 255, 255, 180.0, 1.0, 0.5), // Cyan
				Arguments.of(255, 0, 255, 300.0, 1.0, 0.5), // Magenta
				Arguments.of(0, 0, 0, 0.0, 0.0, 0.0), // Black
				Arguments.of(255, 255, 255, 0.0, 0.0, 1.0), // White
				// corner cases:
				Arguments.of(0, 0, 0, 0.0, 0.0, 0.0), // Black
				Arguments.of(255, 255, 255, 0.0, 0.0, 1.0), // White
				Arguments.of(128, 128, 128, 0.0, 0.0, 0.502), // Mid-Gray (128/255 approx 0.502)
				Arguments.of(1, 1, 1, 0.0, 0.0, 0.004), // Almost Black
				Arguments.of(240, 240, 255, 240.0, 1.0, 0.971), // pale blue
				Arguments.of(40, 0, 0, 0.0, 1.0, 0.078), // Deep red, high saturation/low lightness
				Arguments.of(255, 128, 0, 30.1, 1.0, 0.5), // Orange (Mid-segment)
				Arguments.of(255, 254, 254, 0.0, 1.0, 0.998), // Tiny Delta Red
				Arguments.of(127, 128, 127, 120.0, 0.004, 0.5) // Tiny Delta Green
		);
	}

	@ParameterizedTest
	@MethodSource("colorProvider")
	@SuppressWarnings("static-method")
	void testfromRGB(int r, int g, int b, double h, double s, double l) {
		HSL hsl = HSL.fromRGB(r, g, b);

		assertAll(() -> assertEquals(h, hsl.h(), HUE_EPSILON, "Hue mismatch"), //$NON-NLS-1$
				() -> assertEquals(s, hsl.s(), UNIT_EPSILON, "Saturation mismatch"), //$NON-NLS-1$
				() -> assertEquals(l, hsl.l(), UNIT_EPSILON, "Lightness mismatch")); //$NON-NLS-1$
	}

	@ParameterizedTest
	@MethodSource("colorProvider")
	@SuppressWarnings("static-method")
	void testToRgb(int r, int g, int b, double h, double s, double l) {
		HSL hsl = new HSL(h, s, l);
		RGB result = hsl.toRGB();

		assertAll(() -> assertEquals(r, result.red, "Red mismatch"), //$NON-NLS-1$
				() -> assertEquals(g, result.green, "Green mismatch"), //$NON-NLS-1$
				() -> assertEquals(b, result.blue, "Blue mismatch") //$NON-NLS-1$
		);
	}

	@ParameterizedTest
	@MethodSource("colorProvider")
	@SuppressWarnings("static-method")
	void testFromRgbToRgbRoundtrip() {
		RGB original = new RGB(255, 0, 0);
		HSL hsl = HSL.fromRGB(original);
		RGB backToRgb = hsl.toRGB();
		assertEquals(original, backToRgb, "RGB -> HSL -> RGB should match exactly for primary colors"); //$NON-NLS-1$
	}

	@Test
	@SuppressWarnings("static-method")
	void testHueWrap() {
		HSL hueZero = new HSL(0.0, 1.0, 0.5);
		HSL hueThreeSixty = new HSL(360.0, 1.0, 0.5);

		RGB rgbZero = hueZero.toRGB();
		RGB rgbThreeSixty = hueThreeSixty.toRGB();

		assertEquals(rgbZero, rgbThreeSixty, "Hue 360 should wrap to be identical to Hue 0"); //$NON-NLS-1$
		assertEquals(new RGB(255, 0, 0), rgbThreeSixty, "Hue 360 should result in Pure Red"); //$NON-NLS-1$
	}

	@ParameterizedTest
	@CsvSource({ "0.5, 0.2, 0.6", // 0.5 + (1-0.5)*0.2 = 0.6
			"0.0, 0.5, 0.5", // Black (0) + 50% = 0.5
			"0.8, 0.5, 0.9" // 0.8 + (1-0.8)*0.5 = 0.9
	})
	@SuppressWarnings("static-method")
	void testLighterRelative(double initialL, double percentage, double expectedL) {
		HSL color = new HSL(0, 1.0, initialL);
		HSL lighter = color.lighter(percentage);

		assertEquals(expectedL, lighter.l(), UNIT_EPSILON, "Lighter should move toward 1.0 relatively"); //$NON-NLS-1$
	}

	@ParameterizedTest
	@CsvSource({ "0.5, 0.2, 0.4", // 0.5 * (1-0.2) = 0.4
			"1.0, 0.5, 0.5", // White (1) * 0.5 = 0.5
			"0.2, 1.0, 0.0" // 0.2 * 0 = 0.0 (Black)
	})
	@SuppressWarnings("static-method")
	void testDarkerRelative(double initialL, double percentage, double expectedL) {
		HSL color = new HSL(0, 1.0, initialL);
		HSL darker = color.darker(percentage);

		assertEquals(expectedL, darker.l(), UNIT_EPSILON, "Darker should scale toward 0.0"); //$NON-NLS-1$
	}

	@Test
	@SuppressWarnings("static-method")
	void testHuePreservation() {
		HSL color = new HSL(120.0, 0.8, 0.5); // A Green
		HSL lighter = color.lighter(0.3);
		HSL darker = color.darker(0.3);

		assertEquals(color.h(), lighter.h(), "Hue must not change when lightening"); //$NON-NLS-1$
		assertEquals(color.h(), darker.h(), "Hue must not change when darkening"); //$NON-NLS-1$
	}

}
