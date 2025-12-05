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
 * A PointList implementation using floating point values. The use of floating
 * point prevents rounding errors from accumulating. For the sake of
 * compatibility with the integer-precision {@link PointList}, the integer
 * coordinates and their decimal part are stored in two separate arrays.
 *
 * @since 3.22
 */
public final class PrecisionPointList extends PointList {
	private static final PrecisionPoint PRIVATE_POINT = new PrecisionPoint();
	private double[] decimalFractions = {};

	/**
	 * Constructs an empty PrecisionPointList
	 */
	public PrecisionPointList() {
	}

	/**
	 * Constructs a PrecisionPointList with the given size.
	 *
	 * @param size Number of points to hold.
	 */
	public PrecisionPointList(int size) {
		super(size);
		decimalFractions = new double[size * 2];
	}

	/**
	 * Constructs a PrecisionPointList with the given points.
	 *
	 * @param points int array where two consecutive ints form the coordinates of a
	 *               point
	 */
	public PrecisionPointList(int[] points) {
		super(points);
		decimalFractions = new double[points.length];
	}

	/**
	 * Constructs a PrecisionPointList with the given points.
	 *
	 * @param points PointList from which the initial values are taken
	 */
	public PrecisionPointList(PointList points) {
		this(Arrays.copyOf(points.toIntArray(), points.arraySize()));
		if (points instanceof PrecisionPointList other) {
			System.arraycopy(other.decimalFractions, 0, decimalFractions, 0, arraySize());
		}
	}

	/**
	 * @see org.eclipse.draw2d.geometry.PointList#addAll(PointList)
	 */
	@Override
	public void addAll(PointList points) {
		ensureCapacity(size() + points.size());
		if (points instanceof PrecisionPointList other) {
			System.arraycopy(other.decimalFractions, 0, decimalFractions, arraySize(), other.arraySize());
		}
		super.addAll(points);
	}

	/**
	 * @see org.eclipse.draw2d.geometry.PointList#addPoint(Point)
	 */
	@Override
	public void addPoint(Point point) {
		ensureCapacity(size() + 1);
		if (point instanceof PrecisionPoint) {
			int index = arraySize();
			decimalFractions[index] = point.preciseX() - point.x;
			decimalFractions[index + 1] = point.preciseY() - point.y;
		}
		super.addPoint(point);
	}

	@Override
	/* package */ Rectangle createBounds() {
		return new PrecisionRectangle();
	}

	private void ensureCapacity(int newSize) {
		int arraySize = newSize * 2;
		if (decimalFractions.length < arraySize) {
			decimalFractions = Arrays.copyOf(decimalFractions, Math.max(arraySize, size() * 4));
		}
	}

	/**
	 * @see org.eclipse.draw2d.geometry.PointList#getCopy()
	 */
	@Override
	public PointList getCopy() {
		return new PrecisionPointList(this);
	}

	/**
	 * @see org.eclipse.draw2d.geometry.PointList#getPoint(int)
	 */
	@Override
	public Point getPoint(int index) {
		return getPoint(new PrecisionPoint(), index);
	}

	/**
	 * @see org.eclipse.draw2d.geometry.PointList#getPoint(Point, int)
	 */
	@Override
	public Point getPoint(Point point, int index) {
		super.getPoint(point, index);
		int arrayIndex = index * 2;
		// Ignore decimal part for integer-precision points
		if (point instanceof PrecisionPoint precisePoint) {
			precisePoint.translate(decimalFractions[arrayIndex], decimalFractions[arrayIndex + 1]);
		}
		return point;
	}

	/**
	 * @see org.eclipse.draw2d.geometry.PointList#insertPoint(Point, int)
	 */
	@Override
	public void insertPoint(Point p, int index) {
		super.insertPoint(p, index);
		int length = decimalFractions.length;
		double old[] = decimalFractions;
		decimalFractions = new double[length + 2];
		int arrayIndex = index * 2;
		System.arraycopy(old, 0, decimalFractions, 0, arrayIndex);
		System.arraycopy(old, arrayIndex, decimalFractions, arrayIndex + 2, length - arrayIndex);
		decimalFractions[arrayIndex] = p.preciseX() - p.x;
		decimalFractions[arrayIndex + 1] = p.preciseY() - p.y;
	}

	/**
	 * @see org.eclipse.draw2d.geometry.PointList#performScale(double)
	 */
	@Override
	public void performScale(double factor) {
		for (int i = 0; i < size(); ++i) {
			getPoint(PRIVATE_POINT, i);
			PRIVATE_POINT.scale(factor);
			setPoint(PRIVATE_POINT, i);
		}
	}

	/**
	 * @see org.eclipse.draw2d.geometry.PointList#removePoint(int)
	 */
	@Override
	public Point removePoint(int index) {
		int arraySize = arraySize();
		Point pt1 = super.removePoint(index);
		int arrayIndex = index * 2;
		Point pt2 = new PrecisionPoint(decimalFractions[arrayIndex], decimalFractions[arrayIndex + 1]);
		// If not the last point
		if (arrayIndex != arraySize - 2) {
			System.arraycopy(decimalFractions, arrayIndex + 2, decimalFractions, arrayIndex,
					arraySize - arrayIndex - 2);
		}
		pt2.translate(pt1);
		return pt2;
	}

	/**
	 * @see org.eclipse.draw2d.geometry.PointList#reverse()
	 */
	@Override
	public void reverse() {
		super.reverse();
		double temp;
		for (int i = 0, j = arraySize() - 2; i < size(); i += 2, j -= 2) {
			temp = decimalFractions[i];
			decimalFractions[i] = decimalFractions[j];
			decimalFractions[j] = temp;
			temp = decimalFractions[i + 1];
			decimalFractions[i + 1] = decimalFractions[j + 1];
			decimalFractions[j + 1] = temp;
		}
	}

	/**
	 * @see org.eclipse.draw2d.geometry.PointList#setPoint(Point, int)
	 */
	@Override
	public void setPoint(Point point, int index) {
		super.setPoint(point, index);
		int arrayIndex = index * 2;
		decimalFractions[arrayIndex] = point.preciseX() - point.x;
		decimalFractions[arrayIndex + 1] = point.preciseY() - point.y;
	}

	/**
	 * @see org.eclipse.draw2d.geometry.PointList#setSize(int)
	 */
	@Override
	public void setSize(int newSize) {
		super.setSize(newSize);
		int arraySize = newSize * 2;
		if (decimalFractions.length > arraySize) {
			return;
		}
		decimalFractions = Arrays.copyOf(decimalFractions, arraySize);
	}

	/**
	 * Returns the decimal fractions of this PointList as an double array. The
	 * returned array is by reference. Any changes made to the array will also be
	 * changing the original PointList.
	 *
	 * @return the double array of decimal fractions by reference
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public double[] toDoubleArray() {
		int arraySize = arraySize();
		if (decimalFractions.length != arraySize) {
			decimalFractions = Arrays.copyOf(decimalFractions, arraySize);
		}
		return decimalFractions;
	}

	/**
	 * @see org.eclipse.draw2d.geometry.PointList#transpose()
	 */
	@Override
	public void transpose() {
		super.transpose();
		double temp;
		for (int i = 0; i < arraySize(); i += 2) {
			temp = decimalFractions[i];
			decimalFractions[i] = decimalFractions[i + 1];
			decimalFractions[i + 1] = temp;
		}
	}
}
