/*******************************************************************************
 * Copyright (c) 2010, 2025 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d.geometry;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A Utilities class for precise geometry calculations.
 *
 * @author Alexander Nyssen
 * @since 3.7
 */
public class PrecisionGeometry {

	/*
	 * Precise calculations on doubles are performed based on BigDecimals,
	 * converting to 16 digits scale, so there are no undesired rounding effects.
	 */
	private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;
	private static final int SCALE = 16;

	protected static final double preciseAdd(double d1, double d2) {
		return doubleToBigDecimal(d1).add(doubleToBigDecimal(d2)).setScale(SCALE, ROUNDING).doubleValue();
	}

	protected static final double preciseSubtract(double d1, double d2) {
		return doubleToBigDecimal(d1).subtract(doubleToBigDecimal(d2)).setScale(SCALE, ROUNDING).doubleValue();
	}

	protected static final double preciseMultiply(double d1, double d2) {
		return doubleToBigDecimal(d1).multiply(doubleToBigDecimal(d2)).setScale(SCALE, ROUNDING).doubleValue();
	}

	protected static final double preciseDivide(double d1, double d2) {
		return doubleToBigDecimal(d1).divide(doubleToBigDecimal(d2), SCALE, ROUNDING).doubleValue();
	}

	protected static final double preciseNegate(double d) {
		return doubleToBigDecimal(d).negate().setScale(SCALE, ROUNDING).doubleValue();
	}

	protected static final double preciseAbs(double d) {
		return doubleToBigDecimal(d).abs().setScale(SCALE, ROUNDING).doubleValue();
	}

	protected static final BigDecimal doubleToBigDecimal(double d) {
		return BigDecimal.valueOf(d);
	}

	/**
	 * Converts a double value into an integer value, avoiding rounding effects.
	 *
	 * @param doubleValue the double value to convert
	 * @return the integer value for the double.
	 */
	protected static final int doubleToInteger(double doubleValue) {
		return (int) Math.floor(doubleValue + 0.000000001);
	}
}
