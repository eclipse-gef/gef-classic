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

package org.eclipse.draw2d.colors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import org.eclipse.pde.api.tools.annotations.NoExtend;
import org.eclipse.pde.api.tools.annotations.NoInstantiate;
import org.eclipse.pde.api.tools.annotations.NoReference;

/**
 * Instances of this class are descriptions of colors in terms of the HSL (Hue,
 * Saturation, Lightness) color space. A color may be described by its type
 * (hue) on the color wheel (0° = red, 120° = green and 240° = blue), its
 * saturation (0% = grey, 100% = full color) and its lightness (0% = black, 50%
 * = normal, 100% = white).
 *
 * This class is currently in development its API may change.
 *
 * @param h The color hue in degrees, in the range {@code [0.0, 360.0)}. 0 is
 *          red, 120 is green, and 240 is blue.
 * @param s The intensity of the color, in the range {@code [0.0, 1.0]}. 0.0 is
 *          grayscale and 1.0 is fully saturated.
 * @param l The brightness of the color, in the range {@code [0.0, 1.0]}. 0.0 is
 *          black, 0.5 is the pure color, and 1.0 is white.
 *
 * @since 3.22 (provisional)
 */
@NoExtend
@NoReference
@NoInstantiate
public record HSL(double h, double s, double l) {

	private static final double MAX_RGB_VALUE = 255.0;

	public HSL {
		if (h < 0 || h > 360) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		if (s < 0 || s > 1.0) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		if (l < 0 || l > 1.0) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
	}

	/**
	 * Create a darker version of the color by moving it towards black.
	 *
	 * @param percentage The percentage how much darker the color should be
	 *                   (0.0..1.0).
	 * @return The lighter color
	 */
	public HSL darker(double percentage) {
		double newL = l * (1 - Math.max(0.0, Math.min(1.0, percentage)));
		return new HSL(h, s, newL);
	}

	/**
	 * Test if the given color is dark.
	 *
	 * This can be used to identify dark mode or find the right contrast color
	 * (e.g., white text on dark background or opposite).
	 *
	 * @return True of the lightness value is below 0.5
	 */
	public boolean isDark() {
		return l < 0.5;
	}

	/**
	 * Create a lighter version of the color by moving it towards white.
	 *
	 * @param percentage The percentage how much lighter the color should be
	 *                   (0.0..1.0).
	 * @return The lighter color
	 */
	public HSL lighter(double percentage) {
		double newL = l + (1.0 - l) * Math.max(0.0, Math.min(1.0, percentage));
		return new HSL(h, s, newL);
	}

	/**
	 * Transforms the RGB color into the according HSL color space.
	 *
	 * @param col The RGB color to be transformed.
	 * @return The color in HSL space.
	 */
	public static HSL fromColor(Color col) {
		return fromRGB(col.getRed(), col.getGreen(), col.getBlue());
	}

	/**
	 * Transforms the RGB color into the according HSL color space.
	 *
	 * @param rgb The RGB color to be transformed.
	 * @return The color in HSL space.
	 */
	public static HSL fromRGB(RGB rgb) {
		return fromRGB(rgb.red, rgb.green, rgb.blue);
	}

	/**
	 * Transforms the RGB color into the according HSL color space.
	 *
	 * The used algorithm is based on the Book: Computer Graphics: Principles and
	 * Practice in C. James D. Foley, Andries van Dam, Steven K. Feiner, John F.
	 * Hughes, 2nd Edition, Published Aug 4, 1995 by Addison-Wesley Professional.
	 * ISBN-13: 978-0-201-84840-3
	 *
	 * @param r The red component of the source color (0..255).
	 * @param g The green component of the source color (0..255).
	 * @param b The blue component of the source color (0..255).
	 * @return The color in HSL color space.
	 */
	public static HSL fromRGB(int r, int g, int b) {
		final double rRel = r / MAX_RGB_VALUE;
		final double gRel = g / MAX_RGB_VALUE;
		final double bRel = b / MAX_RGB_VALUE;
		final double max = Math.max(Math.max(rRel, gRel), bRel);
		final double min = Math.min(Math.min(rRel, gRel), bRel);

		double h = 0.0;
		double s = 0.0;
		double l = (max + min) / 2.0;

		if (max != min) {
			// we are not just grey
			final double delta = max - min;

			if (l <= 0.5) {
				s = (delta / (max + min));
			} else {
				s = (delta / (2.0 - (max + min)));
			}

			if (Math.abs(max - rRel) <= Double.MIN_VALUE) { // how to check equality for doubles
				h = (gRel - bRel) / delta;
			} else if (Math.abs(max - gRel) <= Double.MIN_VALUE) {
				h = 2.0 + ((bRel - rRel) / delta);
			} else if (Math.abs(max - bRel) <= Double.MIN_VALUE) {
				h = 4.0 + ((rRel - gRel) / delta);
			}
			h *= 60.0;

			if (h < 0.0) {
				h += 360.0;
			}
		}
		return new HSL(h, s, l);
	}

	/**
	 * Transforms the HSL color into the according RGB color space.
	 *
	 * @return The color in RGB color space.
	 */
	public Color toColor() {
		return new Color(toRGB());
	}

	/**
	 * Transforms the HSL color into the according RGB color space.
	 *
	 * The used algorithm is based on the Book: Computer Graphics: Principles and
	 * Practice in C. James D. Foley, Andries van Dam, Steven K. Feiner, John F.
	 * Hughes, 2nd Edition, Published Aug 4, 1995 by Addison-Wesley Professional.
	 * ISBN-13: 978-0-201-84840-3
	 *
	 * @return The color in RGB color space.
	 */
	public RGB toRGB() {
		final RGB retVal = new RGB(0, 0, 0);

		if (s == 0.0) {
			if (h == 0.0) {
				retVal.red = retVal.green = retVal.blue = (int) (l * MAX_RGB_VALUE);
			} else {
				// in the achromatic (grey) case h must not have a value
				SWT.error(SWT.ERROR_INVALID_ARGUMENT);
			}
		} else {
			final double m2 = ((l <= 0.5) ? (l * (1.0 + s)) : ((l + s) - (l * s)));
			final double m1 = (2.0 * l) - m2;

			retVal.red = hslValue(m1, m2, h + 120.0);
			retVal.green = hslValue(m1, m2, h);
			retVal.blue = hslValue(m1, m2, h - 120.0);
		}
		return retVal;
	}

	private static int hslValue(final double m1, final double m2, double hue) {
		double retVal = m1;

		if (hue > 360.0) {
			hue -= 360.0;
		} else if (hue < 0.0) {
			hue += 360.0;
		}

		if (hue < 60.0) {
			retVal = m1 + (((m2 - m1) * hue) / 60.0);
		} else if (hue < 180.0) {
			retVal = m2;
		} else if (hue < 240.0) {
			retVal = m1 + (((m2 - m1) * (240.0 - hue)) / 60.0);
		}

		return (int) Math.round(MAX_RGB_VALUE * retVal);
	}
}
