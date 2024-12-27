/*******************************************************************************
 * Copyright (c) 2011, 2023 Google, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Google, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.geometry;

import java.io.Serializable;

/**
 * Represents interval in 1-dimensional space.
 *
 * @author mitin_aa
 * @author lobas_av
 * @author scheglov_ke
 * @since 3.13
 */
public record Interval(int begin, int length) implements Serializable {

	public static final Interval INFINITE = new Interval(0, Integer.MAX_VALUE);

	/**
	 * Creates a new Interval with zero length and starting at the origin.
	 *
	 * @since 3.13
	 */
	public Interval() {
		this(0, 0);
	}

	/**
	 * Creates a new Interval exact same parameters as the given Interval.
	 *
	 * @param interval The interval to copy
	 * @since 3.13
	 */
	public Interval(Interval interval) {
		this(interval.begin, interval.length);
	}

	/**
	 * Returns the center of this Interval. The center is the middle of the begin
	 * and the end.
	 *
	 * @return the center of this {@link Interval}.
	 * @since 3.13
	 */
	public int center() {
		return begin + length / 2;
	}

	/**
	 * Returns whether the given value is within [begin, begin + length).
	 *
	 * @param value A position somewhere within 1-dimensional space.
	 * @return <code>true</code> if the value is contained by this interval.
	 * @since 3.13
	 */
	public boolean contains(int value) {
		return begin <= value && value < end();
	}

	/**
	 * Calculates the distance to given <code>point</code>.
	 *
	 * Example:
	 *
	 * <pre>
	 *  Let x=[10, 100]:
	 *  	distance for point 3 is 7.
	 *  	distance for point 10 is 0.
	 *  	distance for point 50 is 0.
	 *  	distance for point 110 is 0.
	 *  	distance for point 150 is 40.
	 * </pre>
	 *
	 * @param point the point to calculate the distance to.
	 * @return the calculated distance.
	 * @since 3.13
	 */
	public int distance(int point) {
		if (point < begin) {
			return begin - point;
		} else if (point > end()) {
			return point - end();
		}
		return 0;
	}

	/**
	 * Returns the end of this Interval. The end is the sum of the begin and the
	 * length of this Interval.
	 *
	 * @return end of this Interval
	 * @since 3.13
	 */
	public int end() {
		return begin + length;
	}

	/**
	 * Returns a new Interval which has the exact same parameters as this Interval.
	 *
	 * @return copy of this Interval
	 * @since 3.13
	 */
	public Interval getCopy() {
		return new Interval(begin, length);
	}

	/**
	 * Calculates the intersection of the input {@link Interval} and this
	 * {@link Interval}. I.e. the interval that is shared by both Intervals. If both
	 * Intervals are disjoint, an empty Interval is returned.
	 *
	 * @param interval The Interval to intersect with.
	 * @return The intersection of the input Interval and this Interval.
	 * @since 3.13
	 */
	public Interval getIntersection(Interval interval) {
		int x1 = Math.max(begin, interval.begin);
		int x2 = Math.min(end(), interval.end());
		if (x2 - x1 < 0) {
			// no intersection
			return new Interval();
		}
		return new Interval(x1, x2 - x1);
	}

	/**
	 * Calculates a new interval where its leading direction has been increased by
	 * the given {@code delta} (ex., grow left for horizontal interval).
	 *
	 * @param delta the value to increase the interval to.
	 * @return The new interval with adjusted {@code begin} and {@code length}.
	 * @since 3.13
	 */
	public Interval growLeading(int delta) {
		return new Interval(begin + delta, length - delta);
	}

	/**
	 * Calculates a new interval where its trailing direction has been increased by
	 * the given {@code delta} (ex., grow right for horizontal interval).
	 *
	 * @param delta the value to increase the interval to.
	 * @return The new interval with adjusted {@code length}.
	 * @since 3.13
	 */
	public Interval growTrailing(int delta) {
		return new Interval(begin, length + delta);
	}

	/**
	 * Checks whether the Interval has zero length.
	 *
	 * @return <code>true</code> if this {@link Interval} is empty.
	 * @since 3.13
	 */
	public boolean isEmpty() {
		return length == 0;
	}

	/**
	 * Returns whether the input {@link Interval} intersects this {@link Interval}.
	 *
	 * @param interval The Interval to intersect with.
	 * @return <code>true</code> if the input {@link Interval} intersects this
	 *         {@link Interval}.
	 * @since 3.13
	 */
	public boolean intersects(Interval interval) {
		return interval.begin < end() && begin < interval.end();
	}

	/**
	 * Checks whether the input {@link Interval} starts <b>before</b> this
	 * {@link Interval}.
	 *
	 * @param interval The Interval to compare with.
	 * @return <code>true</code> if this interval leads the given
	 *         <code>interval</code>, i.e. for horizontal interval
	 *         <code>isLeadingOf()</code> returns <code>true</code> if this interval
	 *         begins at the left of the given interval.
	 * @since 3.13
	 */
	public boolean isLeadingOf(Interval interval) {
		return begin < interval.begin;
	}

	/**
	 * Checks whether the input {@link Interval} starts <b>after</b> this
	 * {@link Interval}.
	 *
	 * @param interval The Interval to compare with.
	 * @return <code>true</code> if this interval trails the given
	 *         <code>interval</code>, i.e. for horizontal interval
	 *         <code>isTrailingOf()</code> returns <code>true</code> if this
	 *         interval ends at the right of the given interval.
	 * @since 3.13
	 */
	public boolean isTrailingOf(Interval interval) {
		return end() > interval.end();
	}

	/**
	 * We can use this method for example for span support.
	 *
	 * @param intervals sorted, disjoint array of intervals
	 * @return index of rightmost interval that contains given value in its right
	 *         half or <code>-1</code> if there is not such interval.
	 * @since 3.13
	 */
	public static int getRightMostIntervalIndex(Interval[] intervals, int value) {
		int index = -1;
		for (int i = 0; i < intervals.length; i++) {
			Interval interval = intervals[i];
			if (interval.begin + interval.length / 2 < value) {
				index = i;
			}
		}
		return index;
	}
}
