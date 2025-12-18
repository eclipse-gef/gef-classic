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
 * Contributors: The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.zest.layouts.algorithms;

import org.eclipse.swt.SWT;

import org.eclipse.zest.layouts.LayoutStyles;

/**
 * @version 2.0
 * @author Casey Best and Rob Lintern (version 1.0 by Rob Lintern)
 * @deprecated No longer used in Zest 2.x. This class will be removed in a
 *             future release. Use {@link BoxLayoutAlgorithm} with
 *             {@link SWT#VERTICAL} instead.
 * @noextend This class is not intended to be subclassed by clients.
 * @noreference This class is not intended to be referenced by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@SuppressWarnings("removal")
@Deprecated(since = "2.0", forRemoval = true)
public class VerticalLayoutAlgorithm extends GridLayoutAlgorithm.Zest1 {

	/**
	 * Veertical Layout Algorithm constructor with no styles.
	 *
	 */
	@Deprecated
	public VerticalLayoutAlgorithm() {
		this(LayoutStyles.NONE);
	}

	@Deprecated
	public VerticalLayoutAlgorithm(int styles) {
		super(styles);
	}

	/**
	 * Calculates and returns an array containing the number of columns, followed by
	 * the number of rows
	 */
	@Override
	@Deprecated
	protected int[] calculateNumberOfRowsAndCols(int numChildren, double boundX, double boundY, double boundWidth,
			double boundHeight) {
		int cols = 1;
		int rows = numChildren;
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
