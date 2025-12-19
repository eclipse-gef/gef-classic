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
package org.eclipse.zest.layouts.constraints;

/**
 *
 * @author Chris Bennett
 *
 * @deprecated No longer used in Zest 2.x. This class will be removed in a
 *             future release.
 * @noextend This class is not intended to be subclassed by clients.
 * @noreference This class is not intended to be referenced by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@Deprecated(since = "2.0", forRemoval = true)
public class BasicEntityConstraint implements LayoutConstraint {

	@Deprecated
	public boolean hasPreferredLocation = false;

	@Deprecated
	public double preferredX;
	@Deprecated
	public double preferredY;

	@Deprecated
	public boolean hasPreferredSize = false;
	@Deprecated
	public double preferredWidth;
	@Deprecated
	public double preferredHeight;

	@Deprecated
	public BasicEntityConstraint() {
		clear();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.zest.layouts.constraints.LayoutConstraint#clear()
	 */
	@Override
	@Deprecated
	public void clear() {
		this.hasPreferredLocation = false;
		this.hasPreferredSize = false;
		this.preferredX = 0.0;
		this.preferredY = 0.0;
		this.preferredWidth = 0.0;
		this.preferredHeight = 0.0;
	}
}
