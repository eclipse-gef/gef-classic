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
package org.eclipse.zest.layouts;

/**
 * Specifies a single bend point in a graph relationship.
 *
 * @author Ian Bull
 * @author Chris Bennett
 * @deprecated No longer used in Zest 2.x. This interface will be removed in a
 *             future release.
 * @noextend This interface is not intended to be extended by clients.
 * @noreference This interface is not intended to be referenced by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
@Deprecated(since = "2.0", forRemoval = true)
public interface LayoutBendPoint {
	@Deprecated
	public double getX();

	@Deprecated
	public double getY();

	@Deprecated
	public boolean getIsControlPoint();
}
