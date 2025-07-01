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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;

import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.draw2d.ColorConstants;

/**
 * This class defines the shape of the default GEF-cursor used for the plug/tree
 * images. The cursor can't be part of the SVG itself, as it is a)
 * platform-specific and b) because SWT doesn't respect the
 * {@code non-scaling-stroke} vector effect, which ensures that the cursor
 * always has a stroke-width of 1px.
 */
public class InternalCursor {
	/**
	 * Defines the shape of the cursor at 100% zoom.
	 */
	//@formatter:off
	private static final float[] CURSOR_POINTS = {
			 0f,  0f,
			 0f, 17f,
			 4f, 13f,
			 7f, 19f,
			 9f, 18f,
			 7f, 13f,
			 7f, 12f,
			12f, 12f,
			 0f,  0f
	};
	//@formatter:on
	/**
	 * Local cache to store the cursor data for each zoom level.
	 */
	private static final Map<Integer, ImageData> CURSOR_AT_ZOOM = new HashMap<>();
	/**
	 * The default cursor that is constructed using {link {@link #CURSOR_POINTS}.
	 * May be replaced with a custom cursor by calling
	 * {@link #setCursorDescriptor(ImageDescriptor)}.
	 */
	private static ImageDescriptor CURRENT_CURSOR_DESCRIPTOR = ImageDescriptor
			.createFromImageDataProvider(zoom -> CURSOR_AT_ZOOM.computeIfAbsent(zoom, InternalCursor::getCursorAtZoom));

	/**
	 * This method generates the image data for the cursor at the given zoom level.
	 * The points defined with {@link #CURSOR_POINTS} are scaled by the given zoom
	 * and painted onto an image.
	 *
	 * @param zoom The zoom level. e.g. 100, 125, 200
	 * @return The cursor image data at the given zoom level.
	 */
	private static ImageData getCursorAtZoom(int zoom) {
		float maxWidth = 0f;
		float maxHeight = 0f;

		for (int i = 0; i < CURSOR_POINTS.length; i += 2) {
			maxWidth = Math.max(maxWidth, CURSOR_POINTS[i]);
			maxHeight = Math.max(maxHeight, CURSOR_POINTS[i + 1]);
		}

		float zoomFactor = zoom / 100.0f;

		int width = 1 + (int) Math.ceil(zoomFactor * maxWidth);
		int height = 1 + (int) Math.ceil(zoomFactor * maxHeight);

		//
		Display display = Display.getDefault();
		// Construct path
		Path path = new Path(display);
		for (int i = 0; i < CURSOR_POINTS.length; i += 2) {
			float x = zoomFactor * CURSOR_POINTS[i];
			float y = zoomFactor * CURSOR_POINTS[i + 1];
			if (i == 0) {
				path.moveTo(x, y);
			} else {
				path.lineTo(x, y);
			}
		}
		// Construct image
		ImageData imageData = new ImageData(width, height, 32, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF));
		imageData.alphaData = new byte[width * height];
		Image image = new Image(display, imageData);
		GC gc = new GC(image);
		gc.setAlpha(0);
		gc.fillRectangle(0, 0, width, height);
		gc.setAlpha(255);
		gc.setAntialias(SWT.ON);
		gc.setLineWidth(1);
		gc.setBackground(ColorConstants.white);
		gc.fillPath(path);
		gc.setBackground(ColorConstants.black);
		gc.drawPath(path);
		gc.dispose();
		path.dispose();
		// Image is already scaled to expected zoom level
		imageData = image.getImageData(100);
		image.dispose();
		return imageData;
	}

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
