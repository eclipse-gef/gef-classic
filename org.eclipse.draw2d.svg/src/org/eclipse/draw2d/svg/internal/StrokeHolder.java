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
package org.eclipse.draw2d.svg.internal;

import java.awt.BasicStroke;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.swt.SWT;

/**
 * Convenience class for creating a {@link BasicStroke}. The fields of those
 * class are unmodifiable, so we keep track of them using this "holder" class
 * and only create an instance when actually needed.
 */
public class StrokeHolder implements Cloneable {
	public float width;
	public int cap;
	public int join;
	public float miterLimit;
	public int[] dashInt;
	public float[] dashFloat;
	public float dashPhase;

	@Override
	public int hashCode() {
		return Objects.hash(width, cap, join, miterLimit, dashInt, dashFloat, dashPhase);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof StrokeHolder other)) {
			return false;
		}
		return width == other.width //
				&& cap == other.cap //
				&& join == other.join //
				&& miterLimit == other.miterLimit //
				&& dashInt == other.dashInt //
				&& dashFloat == other.dashFloat //
				&& dashPhase == other.dashPhase;
	}

	@Override
	public StrokeHolder clone() {
		try {
			return (StrokeHolder) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public BasicStroke build() {
		return new BasicStroke(width, getLineCap(cap), getLineJoin(join), miterLimit, getLineDash(), dashPhase);
	}

	// #################################################################################################################
	//
	// Line Join
	//
	// #################################################################################################################

	private float[] getLineDash() {
		if (dashFloat != null) {
			return dashFloat;
		}
		if (dashInt != null) {
			return toFloatArray(dashInt);
		}
		return null;
	}

	private static float[] toFloatArray(int[] array) {
		float[] result = new float[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = array[i];
		}
		return result;
	}

	// #################################################################################################################
	//
	// Line Join
	//
	// #################################################################################################################

	private static Map<Integer, Integer> SWT_2_AWT_LINE_JOIN = new HashMap<>();
	static {
		SWT_2_AWT_LINE_JOIN.put(SWT.JOIN_BEVEL, BasicStroke.JOIN_BEVEL);
		SWT_2_AWT_LINE_JOIN.put(SWT.JOIN_MITER, BasicStroke.JOIN_MITER);
		SWT_2_AWT_LINE_JOIN.put(SWT.JOIN_ROUND, BasicStroke.JOIN_ROUND);
	}

	private static int getLineJoin(int key) {
		if (SWT_2_AWT_LINE_JOIN.containsKey(key)) {
			return SWT_2_AWT_LINE_JOIN.get(key);
		}
		throw new IllegalArgumentException("Unsupported line join key: " + key); //$NON-NLS-1$
	}

	// #################################################################################################################
	//
	// Line Cap
	//
	// #################################################################################################################

	private static Map<Integer, Integer> SWT_2_AWT_LINE_CAP = new HashMap<>();
	static {
		SWT_2_AWT_LINE_CAP.put(SWT.CAP_FLAT, BasicStroke.CAP_BUTT);
		SWT_2_AWT_LINE_CAP.put(SWT.CAP_ROUND, BasicStroke.CAP_ROUND);
		SWT_2_AWT_LINE_CAP.put(SWT.CAP_SQUARE, BasicStroke.CAP_SQUARE);
	}

	private static int getLineCap(int key) {
		if (SWT_2_AWT_LINE_CAP.containsKey(key)) {
			return SWT_2_AWT_LINE_CAP.get(key);
		}
		throw new IllegalArgumentException("Unsupported line cap key: " + key); //$NON-NLS-1$
	}
}