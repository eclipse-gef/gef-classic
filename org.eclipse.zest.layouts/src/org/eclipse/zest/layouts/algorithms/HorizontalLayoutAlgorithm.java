/*******************************************************************************
 * Copyright 2005, 2025 CHISEL Group, University of Victoria, Victoria,
 *                      BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Ian Bull - updated and modified
 *******************************************************************************/
package org.eclipse.zest.layouts.algorithms;

import org.eclipse.swt.SWT;

import org.eclipse.zest.layouts.LayoutStyles;

/**
 * @version 2.0
 * @author Casey Best and Rob Lintern
 * @deprecated No longer used in Zest 2.x. This class will be removed in a
 *             future release. Use {@link BoxLayoutAlgorithm} with
 *             {@link SWT#HORIZONTAL} instead.
 * @noextend This class is not intended to be subclassed by clients.
 * @noreference This class is not intended to be referenced by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@SuppressWarnings("removal")
@Deprecated(since = "2.0", forRemoval = true)
public class HorizontalLayoutAlgorithm extends GridLayoutAlgorithm.Zest1 {

	@Deprecated
	public HorizontalLayoutAlgorithm(int styles) {
		super(styles);
	}

	/**
	 * Horizontal Layout Algorithm constructor. Sets the Style to none.
	 */
	@Deprecated
	public HorizontalLayoutAlgorithm() {
		this(LayoutStyles.NONE);
	}

	/**
	 * Calculates and returns an array containing the number of columns, followed by
	 * the number of rows
	 */
	@Override
	@Deprecated
	protected int[] calculateNumberOfRowsAndCols(int numChildren, double boundX, double boundY, double boundWidth,
			double boundHeight) {
		int rows = 1;
		int cols = numChildren;
		int[] result = { cols, rows };
		return result;
	}

	@Override
	@Deprecated
	protected boolean isValidConfiguration(boolean asynchronous, boolean continueous) {
		if (asynchronous && continueous) {
			return false;
		} else if (asynchronous && !continueous) {
			return true;
		} else if (!asynchronous && continueous) {
			return false;
		} else if (!asynchronous && !continueous) {
			return true;
		}

		return false;
	}
}
