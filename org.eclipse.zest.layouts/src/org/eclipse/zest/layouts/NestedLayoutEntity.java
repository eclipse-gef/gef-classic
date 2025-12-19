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
package org.eclipse.zest.layouts;

import java.util.List;

import org.eclipse.zest.layouts.interfaces.SubgraphLayout;

/**
 * Extends LayoutEntity to provide methods for dealing with nested entities.
 *
 * @author Chris Callendar
 * @deprecated Use {@link SubgraphLayout} instead. This interface will be
 *             removed in a future release.
 * @noextend This interface is not intended to be extended by clients.
 * @noreference This interface is not intended to be referenced by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
@SuppressWarnings("rawtypes")
@Deprecated(since = "2.0", forRemoval = true)
public interface NestedLayoutEntity extends LayoutEntity {

	/** Returns the parent entity. */
	@Deprecated
	NestedLayoutEntity getParent();

	/** Returns the list of children. */
	@Deprecated
	List getChildren();

	/** Returns true if this entity has children. */
	@Deprecated
	boolean hasChildren();

}
