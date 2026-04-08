/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
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

package org.eclipse.zest.core.widgets.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import org.eclipse.zest.core.widgets.Graph;

/**
 * An image registry maintains a mapping between symbolic image names and SWT
 * image objects or special image descriptor objects which defer the creation of
 * SWT image objects until they are needed.
 * <p>
 * An image registry owns all of the image objects registered with it, and
 * automatically disposes of them when the SWT Display that creates the images
 * is disposed. Because of this, clients do not need to (indeed, must not
 * attempt to) dispose of these images themselves.
 * </p>
 * <p>
 * Note: This class is heavily inspired by the JFace {@code ImageRegistry},
 * which can't be used without introducing an additional dependency to the Zest
 * {@link Graph}.
 * </p>
 */
public class ImageRegistry {
	private static final ImageRegistry INSTANCE = new ImageRegistry(Display.getDefault());
	private final Map<String, Image> registry = new HashMap<>();
	private final Display display;

	public ImageRegistry(Display display) {
		this.display = display;
		this.display.disposeExec(this::dispose);
	}

	/**
	 * @return The global image registry shared between among all labels.
	 */
	public static ImageRegistry getSharedRegistry() {
		return INSTANCE;
	}

	/**
	 * Adds an image to this registry. This method fails if there is already an
	 * image or descriptor for the given key.
	 * <p>
	 * Note that an image registry owns all of the image objects registered with it,
	 * and automatically disposes of them when the SWT Display is disposed. Because
	 * of this, clients must not register an image object that is managed by another
	 * object.
	 * </p>
	 *
	 * @param key   the key
	 * @param image the image, must not be {@code null}
	 * @exception IllegalArgumentException if the key already exists or if the image
	 *                                     is {@code null}
	 */
	public void put(String key, Image image) {
		if (registry.containsKey(key)) {
			throw new IllegalArgumentException("ImageRegistry key already in use: " + key); //$NON-NLS-1$
		}
		if (image == null) {
			throw new IllegalArgumentException("Image is 'null' for key: " + key); //$NON-NLS-1$
		}
		registry.put(key, image);
	}

	/**
	 * Removes an image from this registry. If an SWT image was allocated, it is
	 * disposed. This method has no effect if there is no image or descriptor for
	 * the given key.
	 *
	 * @param key the key
	 */
	public void remove(String key) {
		registry.remove(key);
	}

	/**
	 * Disposes this image registry, disposing any images that were allocated for
	 * it, and clearing its entries.
	 */
	public void dispose() {
		registry.values().forEach(Image::dispose);
		registry.clear();
	}
}
