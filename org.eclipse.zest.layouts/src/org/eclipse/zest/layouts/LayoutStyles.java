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

import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

/**
 * @author Ian Bull
 * @deprecated Since Zest 2.0, layout styles are set on the individual layout
 *             algorithms (e.g. {@link TreeLayoutAlgorithm#isResizing()}). This
 *             interface will be removed in a future release.
 * @noextend This interface is not intended to be extended by clients.
 * @noreference This interface is not intended to be referenced by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
@Deprecated(since = "2.0", forRemoval = true)
public interface LayoutStyles {

	/** Default layout style constant. */
	@Deprecated
	public final static int NONE = 0x00;

	/**
	 * Layout constant indicating that the layout algorithm should NOT resize any of
	 * the nodes.
	 */
	@Deprecated
	public final static int NO_LAYOUT_NODE_RESIZING = 0x01;

	/**
	 * Some layouts may prefer to expand their bounds beyond those of the requested
	 * bounds. This flag asks the layout not to do so.
	 */
	@Deprecated
	public static final int ENFORCE_BOUNDS = 0X02;

}
