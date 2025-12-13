/*******************************************************************************
 * Copyright (c) 2000, 2023 IBM Corporation and others.
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

/**
 * A scalable graphics object used to print to a printer.
 *
 * @author danlee
 * @deprecated will be deleted after the 2028-03 release (see
 *             {@link ScaledGraphics}).
 */
@Deprecated(forRemoval = true, since = "2026-03")
public class PrinterGraphics extends ScaledGraphics {

	@Deprecated
	Map<Image, Image> imageCache = new HashMap<>();

	@Deprecated
	Printer printer;

	/**
	 * Creates a new PrinterGraphics with Graphics g, using Printer p
	 *
	 * @param g Graphics object to draw with
	 * @param p Printer to print to
	 */
	@Deprecated
	public PrinterGraphics(Graphics g, Printer p) {
		super(g);
		printer = p;
	}

	@Deprecated
	@Override
	Font createFont(FontData data) {
		return new Font(printer, data);
	}

	private Image printerImage(Image image) {
		Image result = imageCache.get(image);
		if (result != null) {
			return result;
		}

		result = new Image(printer, image.getImageData());
		imageCache.put(image, result);
		return result;
	}

	/**
	 * @see org.eclipse.draw2d.ScaledGraphics#dispose()
	 */
	@Deprecated
	@Override
	public void dispose() {
		super.dispose();

		for (Image printerImage : imageCache.values()) {
			printerImage.dispose();
		}

		imageCache.clear();
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#drawImage(Image, int, int)
	 */
	@Deprecated
	@Override
	public void drawImage(Image srcImage, int x, int y) {
		super.drawImage(printerImage(srcImage), x, y);
	}

	/**
	 * @see Graphics#drawImage(Image, int, int, int, int, int, int, int, int)
	 */
	@Deprecated
	@Override
	public void drawImage(Image srcImage, int sx, int sy, int sw, int sh, int tx, int ty, int tw, int th) {
		super.drawImage(printerImage(srcImage), sx, sy, sw, sh, tx, ty, tw, th);
	}

	@Deprecated
	@Override
	int zoomFontHeight(int height) {
		return (int) (height * zoom * Display.getCurrent().getDPI().y / printer.getDPI().y + 0.0000001);
	}

	/**
	 * @see org.eclipse.draw2d.ScaledGraphics#zoomLineWidth(float)
	 */
	@Deprecated
	@Override
	float zoomLineWidth(float w) {
		return (float) (w * zoom);
	}

	/**
	 * Overridden to translate dashes to printer specific values.
	 *
	 * @see org.eclipse.draw2d.ScaledGraphics#setLineAttributes(org.eclipse.swt.graphics.LineAttributes)
	 */
	@Deprecated
	@Override
	public void setLineAttributes(LineAttributes attributes) {
		if (attributes.style == SWT.LINE_CUSTOM && attributes.dash != null && attributes.dash.length > 0) {
			float[] newDashes = new float[attributes.dash.length];
			float printerDot = (float) printer.getDPI().y / Display.getCurrent().getDPI().y + 0.0000001f;
			for (int i = 0; i < attributes.dash.length; i++) {
				newDashes[i] = attributes.dash[i] * printerDot;
			}
			// make a copy of attributes, we dont's want it changed on figure
			// (or display will be affected)
			super.setLineAttributes(new LineAttributes(attributes.width, attributes.cap, attributes.join,
					attributes.style, newDashes, attributes.dashOffset * printerDot, attributes.miterLimit));
		} else {
			super.setLineAttributes(attributes);
		}
	}

}
