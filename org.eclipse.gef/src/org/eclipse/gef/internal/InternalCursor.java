/*******************************************************************************
 * Copyright (c) 2025 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/

package org.eclipse.gef.internal;

import java.util.Objects;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * This class defines the shape of the default GEF-cursor used for the plug/tree
 * images. The cursor can't be part of the SVG itself, as it is a)
 * platform-specific and b) because SWT doesn't respect the
 * {@code non-scaling-stroke} vector effect, which ensures that the cursor
 * always has a stroke-width of 1px.
 */
public class InternalCursor {
	/**
	 * Local cache to store the cursor data for each zoom level.
	 */
	private static final ImageDescriptor CURSOR_AT_100_ZOOM = InternalImages.createDescriptor("icons/cursor@1x.svg"); //$NON-NLS-1$
	private static final ImageDescriptor CURSOR_AT_150_ZOOM = InternalImages.createDescriptor("icons/cursor@1.5x.svg"); //$NON-NLS-1$
	private static final ImageDescriptor CURSOR_AT_200_ZOOM = InternalImages.createDescriptor("icons/cursor@2x.svg"); //$NON-NLS-1$
	/**
	 * The default cursor that is constructed using {link {@link #CURSOR_POINTS}.
	 * May be replaced with a custom cursor by calling
	 * {@link #setCursorDescriptor(ImageDescriptor)}.
	 */
	private static ImageDescriptor CURRENT_CURSOR_DESCRIPTOR = ImageDescriptor.createFromImageDataProvider(zoom -> {
		if (zoom < 150) {
			return CURSOR_AT_100_ZOOM.getImageData(100);
		}
		if (zoom < 200) {
			return CURSOR_AT_150_ZOOM.getImageData(100);
		}
		return CURSOR_AT_200_ZOOM.getImageData(100);
	});

	/**
	 * Returns the image descriptor for the GEF cursor. Never {@code null}.
	 *
	 * @return Either the default or a custom cursor descriptor.
	 */
	public static ImageDescriptor getCursorDescriptor() {
		return CURRENT_CURSOR_DESCRIPTOR;
	}

	/**
	 * Convenience method to allow replacing the default cursor descriptor. An
	 * exception is thrown if {@code cursorDescriptor} is {@code null}.
	 *
	 * @param cursorDescriptor The new cursor descriptor.
	 */
	public static void setCursorDescriptor(ImageDescriptor cursorDescriptor) {
		Objects.requireNonNull(cursorDescriptor, "The new cursor descriptor must not be null!"); //$NON-NLS-1$
		CURRENT_CURSOR_DESCRIPTOR = cursorDescriptor;
	}
}
