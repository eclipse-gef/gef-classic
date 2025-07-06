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

package org.eclipse.draw2d.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.ImageLoader;

/**
 * This utility class is used to handle the migration from GIF/PNG to SVG-based
 * images. If {@link #getEffectiveFileName(String)} called with a {@code .svg}
 * file, the file extension is automatically changed to {@code .png} if SVGs are
 * not yet supported by the active SWT instance. This allows Draw2D to be
 * executed in both the latest and older Eclipse releases.
 */
public final class ImageUtils {
	private static Boolean isSvgSupported;

	private ImageUtils() {
		throw new IllegalStateException("Must never be instantiated"); //$NON-NLS-1$
	}

	/**
	 * If {@code fileName} ends with {@code .svg}, the extension is changed to
	 * {@code .png} if not yet supported by SWT.
	 */
	public static String getEffectiveFileName(String fileName) {
		// If the SWT version doesn't yet support SVGs, fall back to PNG
		if (!isSvgSupported() && fileName.endsWith(".svg")) { //$NON-NLS-1$
			return fileName.replaceFirst("\\.svg$", ".png"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return fileName;
	}

	/**
	 * Convenience method to check whether SVGs are supported by the current
	 * application. This method returns {@code true}, if there is at least one
	 * active bundle that satisfies the (optional) {@code (image.format=svg)}
	 * capability.
	 */
	public static boolean isSvgSupported() {
		if (isSvgSupported != null) {
			return isSvgSupported;
		}
		String svg = """
				<?xml version="1.0" encoding="UTF-8"?>
				<svg width="1" height="1" version="1.1" viewBox="0 0 0 0" xmlns="http://www.w3.org/2000/svg"></svg>
				"""; //$NON-NLS-1$
		try (InputStream is = new ByteArrayInputStream(svg.getBytes(StandardCharsets.UTF_8))) {
			new ImageLoader().load(is);
			isSvgSupported = true;
		} catch (IOException ignore) {
			// Should never happen
			isSvgSupported = false;
		} catch (SWTException e) {
			// SVGs unsupported
			isSvgSupported = false;
		}
		return isSvgSupported;
	}
}
