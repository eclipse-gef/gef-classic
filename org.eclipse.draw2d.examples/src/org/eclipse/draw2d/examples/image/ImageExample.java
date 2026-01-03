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
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.internal.FileImageFileNameProvider;

public class ImageExample {
	private static final Image LOGO1 = FileImageFileNameProvider.createImage(ImageExample.class, "images/GEF.svg"); //$NON-NLS-1$
	private static final Dimension LOGO_SIZE;

	static {
		ImageData data = LOGO1.getImageData(100);
		LOGO_SIZE = new Dimension(data.width, data.height);
	}

	public static void main(String[] args) {
		Point size = new Point(600, 500);

		Shell shell = new Shell();
		shell.setSize(size);

		shell.addPaintListener(event -> {
			GC gc = event.gc;
			gc.setBackground(ColorConstants.blue);
			gc.fillRectangle(0, 0, size.x, size.y / 2);
			gc.setBackground(ColorConstants.green);
			gc.fillRectangle(0, size.y / 2, size.x, size.y / 2);

			int x1 = 0;
			for (int i = 1; i <= 5; ++i) {
				SWTGraphics g = new SWTGraphics(gc);
				g.scale(i);
				g.drawImage(LOGO1, x1, 0);
				g.dispose();
				gc.setTransform(null);
				x1 += LOGO_SIZE.width / 2;
			}

			int x2 = 0;
			for (int i = 1; i <= 5; ++i) {
				gc.drawImage(LOGO1, x2, 250, LOGO_SIZE.width * i, LOGO_SIZE.height * i);
				x2 += LOGO_SIZE.width * i;
			}
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
