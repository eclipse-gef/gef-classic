/*******************************************************************************
 * Copyright (c) 2024, 2025 Patrick Ziegler and others.
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageFileNameProvider;

/**
 * This class behaves similarly to the JFace ImageDescriptors in that it allows
 * loading different versions of the same image, depending on the zoom level.
 * For example, at 100% device zoom the image {@code foo.png} is loaded. But at
 * 200% device zoom, the image {@code foo@2x.png} is loaded instead. When passed
 * as an argument to an image, the image data is automatically reloaded upon DPI
 * changes.
 */
public class FileImageFileNameProvider implements ImageFileNameProvider {
	private static final Logger LOGGER = Logger.getLogger(FileImageFileNameProvider.class);
	private final Class<?> clazz;
	private final String basePath;
	private final String fileExtension;
	private final boolean svg;
	private final String imagePath100;
	private final String imagePath200;

	public FileImageFileNameProvider(Class<?> clazz, String path) {
		int separator = path.lastIndexOf("."); //$NON-NLS-1$
		this.clazz = clazz;
		this.basePath = path.substring(0, separator);
		this.fileExtension = path.substring(separator + 1);
		this.svg = "svg".equals(fileExtension); //$NON-NLS-1$
		this.imagePath100 = createImagePath(basePath + '.' + fileExtension);
		this.imagePath200 = svg ? null : createImagePath(basePath + "@2x." + fileExtension); //$NON-NLS-1$
	}

	@Override
	public String getImagePath(int zoom) {
		if (zoom == 100 || svg) {
			return imagePath100;
		}
		if (zoom == 200) {
			return imagePath200;
		}
		return null;
	}

	private String createImagePath(String name) {
		URL resource = clazz.getResource(name);
		if (resource == null) {
			return null;
		}

		if ("file".equals(resource.getProtocol())) { //$NON-NLS-1$
			return resource.getFile();
		}

		try {
			File tmpImage = Files.createTempFile(null, null).toFile();
			tmpImage.deleteOnExit();
			try (FileOutputStream os = new FileOutputStream(tmpImage)) {
				try (InputStream is = resource.openStream()) {
					is.transferTo(os);
				}
			}
			return tmpImage.getAbsolutePath();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Convenience method for creating DPI-aware images. The returned image is owned
	 * by the called method, which needs to make sure that the resource is disposed
	 * at an appropriate time.
	 */
	public static Image createImage(Class<?> clazz, String name) {
		return new Image(null, new FileImageFileNameProvider(clazz, ImageUtils.getEffectiveFileName(name)));
	}
}
