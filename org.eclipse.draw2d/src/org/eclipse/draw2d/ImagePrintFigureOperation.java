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
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d;

import java.util.Objects;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.internal.InternalDraw2dUtils;

/**
 * Implementation of Draw2D's image drawing capabilities.
 *
 * @since 3.22
 */
public class ImagePrintFigureOperation {
	private final Device device;
	private IFigure printSource;

	/**
	 * Constructor for PrintFigureOperation.
	 *
	 * @param device      device to to print on
	 * @param printSource figure to print
	 */
	public ImagePrintFigureOperation(Device device, IFigure printSource) {
		this(device);
		setPrintSource(printSource);
	}

	/**
	 * Constructor for PrintFigureOperation.
	 * <p>
	 * Note: Descendants must call setPrintSource(IFigure) to set the IFigure that
	 * is to be printed.
	 * </p>
	 *
	 * @param device device to print on
	 */
	protected ImagePrintFigureOperation(Device device) {
		this.device = device;
	}

	/**
	 * This method contains all operations performed to {@link #printSource} prior
	 * to being printed.
	 */
	protected void preparePrintSource() {
		// may be overridden by subclasses.
	}

	/**
	 * This method contains all operations performed to sourceFigure after being
	 * printed.
	 */
	protected void restorePrintSource() {
		// may be overridden by subclasses.
	}

	/**
	 * Sets the printSource.
	 *
	 * @param printSource The printSource to set
	 */
	protected void setPrintSource(IFigure printSource) {
		this.printSource = printSource;
	}

	/**
	 * Sets the print job into motion.
	 */
	public Image run() {
		Objects.requireNonNull(printSource, "Print source must not be null"); //$NON-NLS-1$
		try {
			preparePrintSource();
			if (InternalDraw2dUtils.isAutoScaleEnabled()) {
				return new Image(device, new CachedImageDataProvider(device, printSource));
			}
			return createImageAtCurrentZoom();
		} finally {
			restorePrintSource();
		}
	}

	private Image createImageAtCurrentZoom() {
		Rectangle bounds = printSource.getBounds();
		Image image = new Image(device, bounds.width, bounds.height);
		GC gc = new GC(image);
		SWTGraphics graphics = new SWTGraphics(gc);
		printSource.paint(graphics);
		graphics.dispose();
		gc.dispose();
		return image;
	}

	private static class CachedImageDataProvider implements ImageDataProvider {
		private final IFigure figure;
		private final Device device;
		private ImageData cachedImageData;
		private int cachedZoom = -1;

		public CachedImageDataProvider(Device device, IFigure figure) {
			this.device = device;
			this.figure = figure;
		}

		@Override
		public ImageData getImageData(int zoom) {
			if (zoom == cachedZoom) {
				return cachedImageData;
			}
			cachedImageData = printAtZoom(zoom / 100.0);
			cachedZoom = zoom;
			return cachedImageData;
		}

		private ImageData printAtZoom(double scale) {
			Rectangle bounds = figure.getBounds().getCopy().scale(scale);
			Image image = new Image(device, Math.max(bounds.width, 1), Math.max(bounds.height, 1));

			GC gc = new GC(image);
			image.getImageData(100);

			SWTGraphics graphics = new SWTGraphics(gc);
			graphics.scale(scale);
			figure.paint(graphics);
			graphics.dispose();
			gc.dispose();

			ImageData imageData = image.getImageData(100);
			image.dispose();
			return imageData;
		}
	}
}
