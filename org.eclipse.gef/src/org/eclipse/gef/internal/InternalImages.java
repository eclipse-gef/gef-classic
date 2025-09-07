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
package org.eclipse.gef.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.draw2d.internal.ImageUtils;

public class InternalImages {

	public static final ImageDescriptor DESC_ZOOM_IN;
	public static final ImageDescriptor DESC_ZOOM_OUT;

	public static final ImageDescriptor DESC_MATCH_SIZE;
	public static final ImageDescriptor DESC_MATCH_WIDTH;
	public static final ImageDescriptor DESC_MATCH_HEIGHT;

	public static final ImageDescriptor DESC_MATCH_SIZE_DIS;
	public static final ImageDescriptor DESC_MATCH_WIDTH_DIS;
	public static final ImageDescriptor DESC_MATCH_HEIGHT_DIS;

	public static final ImageDescriptor DESC_HORZ_ALIGN_CENTER;
	public static final ImageDescriptor DESC_HORZ_ALIGN_LEFT;
	public static final ImageDescriptor DESC_HORZ_ALIGN_RIGHT;

	public static final ImageDescriptor DESC_VERT_ALIGN_MIDDLE;
	public static final ImageDescriptor DESC_VERT_ALIGN_TOP;
	public static final ImageDescriptor DESC_VERT_ALIGN_BOTTOM;

	public static final ImageDescriptor DESC_HORZ_ALIGN_CENTER_DIS;
	public static final ImageDescriptor DESC_HORZ_ALIGN_LEFT_DIS;
	public static final ImageDescriptor DESC_HORZ_ALIGN_RIGHT_DIS;

	public static final ImageDescriptor DESC_VERT_ALIGN_MIDDLE_DIS;
	public static final ImageDescriptor DESC_VERT_ALIGN_TOP_DIS;
	public static final ImageDescriptor DESC_VERT_ALIGN_BOTTOM_DIS;

	public static final ImageDescriptor DESC_SEPARATOR;
	public static final ImageDescriptor DESC_FOLDER_OPEN;
	public static final ImageDescriptor DESC_FOLDER_CLOSED;

	public static final ImageDescriptor DESC_BOLD;
	public static final ImageDescriptor DESC_ITALIC;
	public static final ImageDescriptor DESC_UNDERLINE;

	public static final ImageDescriptor DESC_BLOCK_LTR;
	public static final ImageDescriptor DESC_BLOCK_RTL;

	public static final ImageDescriptor DESC_BLOCK_ALIGN_LEFT;
	public static final ImageDescriptor DESC_BLOCK_ALIGN_CENTER;
	public static final ImageDescriptor DESC_BLOCK_ALIGN_RIGHT;

	public static final ImageDescriptor DESC_PINNED;
	public static final ImageDescriptor DESC_UNPINNED;

	public static final ImageDescriptor DESC_PALETTE;

	/**
	 * Can be used to access the cached pinned image by using {@link #get(String)}.
	 */
	public static final String IMG_PINNED = "icons/pinned.svg";//$NON-NLS-1$

	/**
	 * Can be used to access the cached pinned image by using {@link #get(String)}.
	 */
	public static final String IMG_UNPINNED = "icons/unpinned.svg";//$NON-NLS-1$

	/**
	 * Can be used to access the cached pinned image by using {@link #get(String)}.
	 */
	public static final String IMG_PALETTE = "icons/palette_view.svg";//$NON-NLS-1$

	private static final Map<String, Image> overloadedImages = new HashMap<>();

	static {
		DESC_BOLD = createDescriptor("icons/style_bold.svg"); //$NON-NLS-1$
		DESC_ITALIC = createDescriptor("icons/style_italic.svg"); //$NON-NLS-1$
		DESC_UNDERLINE = createDescriptor("icons/style_underline.svg"); //$NON-NLS-1$

		DESC_BLOCK_LTR = createDescriptor("icons/style_paragraph_ltr.svg"); //$NON-NLS-1$
		DESC_BLOCK_RTL = createDescriptor("icons/style_paragraph_rtl.svg"); //$NON-NLS-1$

		DESC_BLOCK_ALIGN_LEFT = createDescriptor("icons/style_paragraph_left.svg"); //$NON-NLS-1$
		DESC_BLOCK_ALIGN_CENTER = createDescriptor("icons/style_paragraph_center.svg"); //$NON-NLS-1$
		DESC_BLOCK_ALIGN_RIGHT = createDescriptor("icons/style_paragraph_right.svg"); //$NON-NLS-1$

		DESC_ZOOM_IN = createDescriptor("icons/zoom_in.svg"); //$NON-NLS-1$
		DESC_ZOOM_OUT = createDescriptor("icons/zoom_out.svg"); //$NON-NLS-1$

		DESC_MATCH_SIZE = createDescriptor("icons/matchsize.svg"); //$NON-NLS-1$
		DESC_MATCH_WIDTH = createDescriptor("icons/matchwidth.svg"); //$NON-NLS-1$
		DESC_MATCH_HEIGHT = createDescriptor("icons/matchheight.svg"); //$NON-NLS-1$

		DESC_MATCH_SIZE_DIS = createDescriptor("icons/matchsize_d.png"); //$NON-NLS-1$
		DESC_MATCH_WIDTH_DIS = createDescriptor("icons/matchwidth_d.png"); //$NON-NLS-1$
		DESC_MATCH_HEIGHT_DIS = createDescriptor("icons/matchheight_d.png"); //$NON-NLS-1$

		DESC_VERT_ALIGN_BOTTOM = createDescriptor("icons/alignbottom.svg"); //$NON-NLS-1$
		DESC_HORZ_ALIGN_CENTER = createDescriptor("icons/aligncenter.svg"); //$NON-NLS-1$
		DESC_HORZ_ALIGN_LEFT = createDescriptor("icons/alignleft.svg"); //$NON-NLS-1$
		DESC_VERT_ALIGN_MIDDLE = createDescriptor("icons/alignmid.svg"); //$NON-NLS-1$
		DESC_HORZ_ALIGN_RIGHT = createDescriptor("icons/alignright.svg"); //$NON-NLS-1$
		DESC_VERT_ALIGN_TOP = createDescriptor("icons/aligntop.svg"); //$NON-NLS-1$

		DESC_VERT_ALIGN_BOTTOM_DIS = createDescriptor("icons/alignbottom_d.png"); //$NON-NLS-1$
		DESC_HORZ_ALIGN_CENTER_DIS = createDescriptor("icons/aligncenter_d.png"); //$NON-NLS-1$
		DESC_HORZ_ALIGN_LEFT_DIS = createDescriptor("icons/alignleft_d.png"); //$NON-NLS-1$
		DESC_VERT_ALIGN_MIDDLE_DIS = createDescriptor("icons/alignmid_d.png"); //$NON-NLS-1$
		DESC_HORZ_ALIGN_RIGHT_DIS = createDescriptor("icons/alignright_d.png"); //$NON-NLS-1$
		DESC_VERT_ALIGN_TOP_DIS = createDescriptor("icons/aligntop_d.png"); //$NON-NLS-1$

		DESC_SEPARATOR = createDescriptor("icons/separator.svg"); //$NON-NLS-1$
		DESC_FOLDER_OPEN = createDescriptor("icons/folder_open.svg"); //$NON-NLS-1$
		DESC_FOLDER_CLOSED = createDescriptor("icons/folder_closed.svg"); //$NON-NLS-1$

		DESC_PINNED = createAndCache(IMG_PINNED);
		DESC_UNPINNED = createAndCache(IMG_UNPINNED);

		DESC_PALETTE = createAndCache(IMG_PALETTE);

	}

	/**
	 * Creates and returns an image descriptor from the given file. If the file is
	 * an SVG, it will be automatically swapped out with a PNG if not yet supported
	 * by SWT.
	 */
	public static ImageDescriptor createDescriptor(String filename) {
		return ImageDescriptor.createFromFile(InternalImages.class, ImageUtils.getEffectiveFileName(filename));
	}

	/**
	 * Creates the image descriptor from the filename given and caches it in the
	 * plugin's image registry.
	 *
	 * @param imageName the full filename of the image
	 * @return the new image descriptor
	 */
	private static ImageDescriptor createAndCache(String imageName) {
		ImageDescriptor result = createDescriptor(imageName);
		InternalGEFPlugin.getDefault().getImageRegistry().put(imageName, result);
		return result;
	}

	/**
	 * Gets an image from the image registry. This image should not be disposed of,
	 * that is handled in the image registry. The image descriptor must have
	 * previously been cached in the image registry. The cached images for the
	 * public image names defined in this file can be retrieved using this method.
	 *
	 * @param imageName the full filename of the image
	 * @return the image or null if it has not been cached in the registry
	 */
	public static Image get(String imageName) {
		Image image = overloadedImages.get(imageName);
		if (image != null) {
			return image;
		}
		return InternalGEFPlugin.getDefault().getImageRegistry().get(imageName);
	}

	public static void set(String imageName, Image image) {
		synchronized (overloadedImages) {
			overloadedImages.put(imageName, image);
		}
	}

}
