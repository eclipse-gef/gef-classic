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

import org.eclipse.swt.graphics.Cursor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.internal.ImageUtils;

import org.eclipse.gef.internal.InternalCursor;
import org.eclipse.gef.internal.InternalGEFPlugin;
import org.eclipse.gef.internal.InternalImages;

/**
 * A shared collection of Cursors.
 *
 * @since 2.0
 */
public class SharedCursors extends Cursors {

	/**
	 * Cursor for valid connection
	 */
	public static final Cursor CURSOR_PLUG;
	/**
	 * Cursor for invalid connection
	 */
	public static final Cursor CURSOR_PLUG_NOT;
	/**
	 * Cursor for adding to a tree
	 */
	public static final Cursor CURSOR_TREE_ADD;
	/**
	 * Cursor for dragging in a tree
	 */
	public static final Cursor CURSOR_TREE_MOVE;

	static {
		CURSOR_PLUG = createCursor("icons/plug-cursor.svg"); //$NON-NLS-1$
		CURSOR_PLUG_NOT = createCursor("icons/plugnot-cursor.svg"); //$NON-NLS-1$
		CURSOR_TREE_ADD = createCursor("icons/tree_add-cursor.svg"); //$NON-NLS-1$
		CURSOR_TREE_MOVE = createCursor("icons/tree_move-cursor.svg"); //$NON-NLS-1$
	}

	private static Cursor createCursor(String sourceName) {
		if (ImageUtils.isSvgSupported()) {
			ImageDescriptor src1 = InternalImages.createDescriptor(sourceName);
			ImageDescriptor src2 = InternalCursor.getCursorDescriptor();
			ImageDescriptor src = new DecorationOverlayIcon(src1, src2, IDecoration.TOP_LEFT);
			return InternalGEFPlugin.createCursor(src, 0, 0);
		}
		ImageDescriptor src = InternalImages.createDescriptor(sourceName);
		return InternalGEFPlugin.createCursor(src, 0, 0);
	}

}
