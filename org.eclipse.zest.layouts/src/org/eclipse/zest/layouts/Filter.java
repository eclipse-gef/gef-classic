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

/**
 * A filter is used to filter objects. Once implemented, interested parties can
 * ask this filter whether or not a specific object is filtered.
 *
 * For example, in a visualization tool, only unfiltered objects should be
 * displayed. Before displaying an object, the display can ask this filter if
 * the object is filtered.
 *
 * @author Casey Best
 * @deprecated Use {@link org.eclipse.zest.core.widgets.LayoutFilter} instead.
 *             This interface will be removed in a future release.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @noreference This interface is not intended to be referenced by clients.
 */
@SuppressWarnings({ "javadoc", "removal" })
@Deprecated(since = "2.0", forRemoval = true)
public interface Filter {

	/**
	 * Returns true if the object is filtered, or false if it's not filtered.
	 */
	@Deprecated
	public boolean isObjectFiltered(LayoutItem object);
}
