/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef;

import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.gef.internal.InternalImages;

/**
 * A class containing shared Images and ImageDescriptors for use by clients.
 *
 * @author hudsonr
 * @since 2.1
 */
public class SharedImages {

	/**
	 * A 16x16 icon representing the Selection Tool
	 */
	public static final ImageDescriptor DESC_SELECTION_TOOL_16;

	/**
	 * A 24x24 icon representing the Selection Tool
	 */
	public static final ImageDescriptor DESC_SELECTION_TOOL_24;

	/**
	 * A 16x16 icon representing the Marquee Tool (nodes and connections)
	 */
	public static final ImageDescriptor DESC_MARQUEE_TOOL_16;

	/**
	 * A 24x24 icon representing the Marquee Tool (nodes and connections)
	 */
	public static final ImageDescriptor DESC_MARQUEE_TOOL_24;

	/**
	 * A 16x16 icon representing the Marquee Tool (nodes only)
	 *
	 * @since 3.7
	 */
	public static final ImageDescriptor DESC_MARQUEE_TOOL_NODES_16;

	/**
	 * A 24x24 icon representing the Marquee Tool (nodes only).
	 *
	 * @since 3.7
	 */
	public static final ImageDescriptor DESC_MARQUEE_TOOL_NODES_24;

	/**
	 * A 16x16 icon representing the Marquee Tool (connections only)
	 *
	 * @since 3.7
	 */
	public static final ImageDescriptor DESC_MARQUEE_TOOL_CONNECTIONS_16;

	/**
	 * A 24x24 icon representing the Marquee Tool (connections only).
	 *
	 * @since 3.7
	 */
	public static final ImageDescriptor DESC_MARQUEE_TOOL_CONNECTIONS_24;

	static {
		DESC_SELECTION_TOOL_16 = InternalImages.createDescriptor("icons/arrow16.gif"); //$NON-NLS-1$
		DESC_SELECTION_TOOL_24 = InternalImages.createDescriptor("icons/arrow24.gif"); //$NON-NLS-1$
		DESC_MARQUEE_TOOL_16 = InternalImages.createDescriptor("icons/marquee16.svg"); //$NON-NLS-1$
		DESC_MARQUEE_TOOL_24 = InternalImages.createDescriptor("icons/marquee24.svg"); //$NON-NLS-1$
		DESC_MARQUEE_TOOL_NODES_16 = InternalImages.createDescriptor("icons/marquee_nodes16.svg"); //$NON-NLS-1$
		DESC_MARQUEE_TOOL_NODES_24 = InternalImages.createDescriptor("icons/marquee_nodes24.svg"); //$NON-NLS-1$
		DESC_MARQUEE_TOOL_CONNECTIONS_16 = InternalImages.createDescriptor("icons/marquee_wires16.svg"); //$NON-NLS-1$
		DESC_MARQUEE_TOOL_CONNECTIONS_24 = InternalImages.createDescriptor("icons/marquee_wires24.svg"); //$NON-NLS-1$
	}

}
