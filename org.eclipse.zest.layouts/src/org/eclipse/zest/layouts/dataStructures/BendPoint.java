/*******************************************************************************
 * Copyright 2006, 2025 CHISEL Group, University of Victoria, Victoria,
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
package org.eclipse.zest.layouts.dataStructures;

import org.eclipse.zest.layouts.LayoutBendPoint;

/**
 * Implements a single bend point in a graph relationship.
 *
 * @author Ian Bull
 * @author Chris Bennett
 * @deprecated No longer used in Zest 2.x. This class will be removed in a
 *             future release.
 * @noextend This class is not intended to be subclassed by clients.
 * @noreference This class is not intended to be referenced by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@SuppressWarnings("removal")
@Deprecated(since = "2.0", forRemoval = true)
public class BendPoint extends DisplayIndependentPoint implements LayoutBendPoint {

	private boolean isControlPoint = false; // is this a control point (for use in curves)

	@Deprecated
	public BendPoint(double x, double y) {
		super(x, y);
	}

	@Deprecated
	public BendPoint(double x, double y, boolean isControlPoint) {
		this(x, y);
		this.isControlPoint = isControlPoint;
	}

	@Override
	@Deprecated
	public double getX() {
		return x;
	}

	@Override
	@Deprecated
	public double getY() {
		return y;
	}

	@Deprecated
	public void setX(double x) {
		this.x = x;
	}

	@Deprecated
	public void setY(double y) {
		this.y = y;
	}

	@Override
	@Deprecated
	public boolean getIsControlPoint() {
		return isControlPoint;
	}

}
