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

package org.eclipse.draw2d.examples.image;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.internal.FileImageDataProvider;

/**
 * Small example to show a side-by-side comparison of the different
 * {@code drawImage} methods in {@link Graphics}.
 */
public class ImageExample {
	private static final Image LOGO = FileImageDataProvider.createImage(ImageExample.class, "images/GEF.svg"); //$NON-NLS-1$

	public static void main(String[] args) {
		Point size = new Point(540, 540);

		Shell shell = new Shell();
		shell.setSize(size);

		ImageData imageData = LOGO.getImageData(100);
		int width = imageData.width;
		int height = imageData.height;

		shell.addPaintListener(event -> {
			GC gc = event.gc;
			gc.setBackground(ColorConstants.gray);
			gc.fillRectangle(0, 0, size.x, size.y / 3);
			gc.setBackground(ColorConstants.white);
			gc.fillRectangle(0, size.y / 3, size.x, size.y / 3);
			gc.setBackground(ColorConstants.gray);
			gc.fillRectangle(0, size.y * 2 / 3, size.x, size.y / 3);

			int x = 0;
			SWTGraphics g = new SWTGraphics(gc);
			for (int i = 1; i <= 5; ++i) {
				g.scale(i);
				g.drawImage(LOGO, x / i, 0);
				g.scale(1.0 / i);

				int scaledWidth = width * i;
				int scaledHeight = height * i;
				g.drawImage(LOGO, x, 180, scaledWidth, scaledHeight);
				g.drawImage(LOGO, 0, 0, width, height, x, 360, scaledWidth, scaledHeight);

				x += width * i;
			}
			g.dispose();
		});

		shell.open();

		Display d = shell.getDisplay();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
