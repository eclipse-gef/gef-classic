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
public class LabelLayoutConstraint implements LayoutConstraint {

	// These should be accessed directly
	@Deprecated
	public String label;
	@Deprecated
	public int pointSize;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.zest.layouts.constraints.LayoutConstraint#clear()
	 */
	@Override
	@Deprecated
	public void clear() {
		label = null;
		pointSize = 1;
	}

}
