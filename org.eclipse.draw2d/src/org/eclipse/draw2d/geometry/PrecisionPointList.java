/*******************************************************************************
 * Copyright (c) 2025 Patrick Ziegler and others.
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

package org.eclipse.draw2d.geometry;

import java.util.Arrays;

/**
 * A PointList implementation using floating point values which are truncated
 * into the inherited integer fields. The use of floating point prevents
 * rounding errors from accumulating.
 *
 * <strong>EXPERIMENTAL</strong> This class has been added as part of a work in
 * progress and there is no guarantee that this API will remain unchanged. This
 * is likely to not function properly outside some very specific use-cases.
 *
 * @since 3.21
 * @noreference This class is not intended to be referenced by clients.
 */
public final class PrecisionPointList extends PointList {
	private static final Point PRIVATE_POINT = new Point();
	private double[] precisePoints = {};

	/**
	 * Constructs a PrecisionPointList with the given points.
	 *
	 * @param points int array where two consecutive ints form the coordinates of a
	 *               point
	 */
	public PrecisionPointList(int[] points) {
		super(points);
		precisePoints = Arrays.stream(points).asDoubleStream().toArray();
	}

	/**
	 * Constructs a PrecisionPointList with the given points.
	 *
	 * @param points PointList from which the initial values are taken
	 */
	public PrecisionPointList(PointList points) {
		this(points.getCopy().toIntArray());
	}

	@Override
	public void performScale(double factor) {
		for (int i = 0; i < precisePoints.length; ++i) {
			precisePoints[i] *= factor;
		}
		for (int i = 0; i < size(); ++i) {
			updateIntPoint(i);
		}
	}

	/**
	 * Updates the int-point at the given index using its precise coordinates.
	 */
	private void updateIntPoint(int i) {
		getPoint(PRIVATE_POINT, i);
		int preciseX = PrecisionGeometry.doubleToInteger(precisePoints[i * 2]);
		int preciseY = PrecisionGeometry.doubleToInteger(precisePoints[i * 2 + 1]);
		if (preciseX != PRIVATE_POINT.x || preciseY != PRIVATE_POINT.y) {
			PRIVATE_POINT.x = preciseX;
			PRIVATE_POINT.y = preciseY;
			setPoint(PRIVATE_POINT, i);
		}
	}
}
