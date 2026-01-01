/*******************************************************************************
 * Copyright (c) 2024, 2026 Patrick Ziegler and others.
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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;

/**
 * This class behaves similarly to the JFace ImageDescriptors in that it allows
 * loading different versions of the same image, depending on the zoom level.
 * For example, at 100% device zoom the image {@code foo.png} is loaded. But at
 * 200% device zoom, the image {@code foo@2x.png} is loaded instead. When passed
 * as an argument to an image, the image data is automatically reloaded upon DPI
 * changes.
 */
public class FileImageDataProvider implements ImageDataProvider {
	private static final Logger LOGGER = Logger.getLogger(FileImageDataProvider.class);
	private final Class<?> clazz;
	private final String basePath;
	private final String fileExtension;

	private FileImageDataProvider(Class<?> clazz, String path) {
		int separator = path.lastIndexOf("."); //$NON-NLS-1$
		this.clazz = clazz;
		this.basePath = path.substring(0, separator);
		this.fileExtension = path.substring(separator + 1);
	}

	@Override
	public ImageData getImageData(int zoom) {
		if (zoom == 100) {
			return createImageData(basePath + '.' + fileExtension);
		}
		if (zoom == 200) {
			return createImageData(basePath + "@2x." + fileExtension); //$NON-NLS-1$
		}
		return null;
	}

	private ImageData createImageData(String name) {
		try (InputStream stream = clazz.getResourceAsStream(name)) {
			return new ImageData(stream);
		} catch (IOException ioe) {
			LOGGER.error(ioe.getLocalizedMessage(), ioe);
			return null;
		}
	}

	/**
	 * Convenience method for creating DPI-aware images. The returned image is owned
	 * by the called method, which needs to make sure that the resource is disposed
	 * at an appropriate time.
	 */
	public static Image createImage(Class<?> clazz, String name) {
		String fileName = ImageUtils.getEffectiveFileName(name);
		if (fileName.endsWith(".svg")) { //$NON-NLS-1$
			return createSvgImage(clazz, fileName);
		}
		return new Image(null, new FileImageDataProvider(clazz, fileName));
	}

	/**
	 * When an SVG-based image is created, we should create pass the file as an
	 * input stream and let SWT handle the scaling. Note that this currently only
	 * works on Windows.
	 *
	 * @see <a href=
	 *      "https://github.com/eclipse-platform/eclipse.platform.swt/issues/291">here</a>
	 */
	private static Image createSvgImage(Class<?> clazz, String name) {
		try (InputStream stream = clazz.getResourceAsStream(name)) {
			return new Image(null, stream);
		} catch (IOException ioe) {
			LOGGER.error(ioe.getLocalizedMessage(), ioe);
			return null;
		}
	}
}
