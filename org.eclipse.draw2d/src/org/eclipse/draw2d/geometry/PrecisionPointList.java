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
 * @since 3.21
 */
public class PrecisionPointList extends PointList {
	private double[] precisePoints = {};

	public PrecisionPointList() {
		// empty list
	}

	private PrecisionPointList(PrecisionPointList ref) {
		super(Arrays.copyOf(ref.toIntArray(), ref.size() * 2));
		precisePoints = Arrays.copyOf(ref.toDoubleArray(), ref.size() * 2);
	}

	@Override
	public PrecisionPointList getCopy() {
		return new PrecisionPointList(this);
	}

	@Override
	public void addPoint(Point p) {
		addPoint(p.preciseX(), p.preciseY());
		super.addPoint(p);
	}

	@Override
	public void addPoint(int x, int y) {
		addPoint((double) x, (double) y);
		super.addPoint(x, y);
	}

	public void addPoint(double x, double y) {
		int index = size() * 2;
		ensureCapacity(size() + 1);
		precisePoints[index] = x;
		precisePoints[index + 1] = y;
	}

	private void ensureCapacity(int capacity) {
		int newSize = capacity * 2;
		if (precisePoints.length < newSize) {
			double[] old = precisePoints;
			precisePoints = new double[Math.max(newSize, size() * 2)];
			System.arraycopy(old, 0, precisePoints, 0, size() * 2);
		}
	}

	@Override
	public void setPoint(Point pt, int index) {
		super.setPoint(pt, index);
		precisePoints[index * 2] = pt.preciseX();
		precisePoints[index * 2 + 1] = pt.preciseY();
	}

	@Override
	public void performScale(double factor) {
		PrecisionPoint p = new PrecisionPoint();
		for (int i = 0; i < size() * 2; i += 2) {
			p.setPreciseX(precisePoints[i] * factor);
			p.setPreciseY(precisePoints[i + 1] * factor);
			setPoint(p, i / 2);
		}
	}

	/**
	 * Returns the contents of this PointList as a double array. The returned array
	 * is by reference. Any changes made to the array will also be changing the
	 * original PointList.
	 *
	 * @return the double array of points by reference
	 */
	public double[] toDoubleArray() {
		if (precisePoints.length != size() * 2) {
			double[] old = precisePoints;
			precisePoints = new double[size() * 2];
			System.arraycopy(old, 0, precisePoints, 0, size() * 2);
		}
		return precisePoints;
	}
}
