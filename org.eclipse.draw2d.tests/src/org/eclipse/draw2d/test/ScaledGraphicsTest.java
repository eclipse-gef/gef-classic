/*******************************************************************************
 * Copyright (c) 2025 IBM Corporation and others.
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

package org.eclipse.draw2d.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageGcDrawer;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.ScaledGraphics;
import org.eclipse.draw2d.geometry.PrecisionPoint;

import org.junit.jupiter.api.Test;

public class ScaledGraphicsTest {

	@Test
	@SuppressWarnings("static-method")
	public void testDrawLineWithInt() {
		Display display = Display.getDefault();

		ImageGcDrawer imageGcDrawer = (gc, width, height) -> {
			SWTGraphics graphics = new SWTGraphics(gc);
			ScaledGraphics scaledGraphics = new ScaledGraphics(graphics);
			scaledGraphics.scale(2);
			ScaledGraphics scaledGraphics2 = new ScaledGraphics(scaledGraphics);
			scaledGraphics2.scale(2.5);
			scaledGraphics2.drawLine(15, 0, 15, 20);
			scaledGraphics2.drawLine(20, 0, 20, 20);
		};

		Image image = new Image(display, imageGcDrawer, 150, 150);
		try {
			ImageData imageData = image.getImageData();
			assertEquals(imageData.getPixel(75, 0), 0, "Pixel at 75 must be 0"); //$NON-NLS-1$
			assertEquals(imageData.getPixel(100, 0), 0, "Pixel at 100 must be 0"); //$NON-NLS-1$
		} finally {
			image.dispose();
		}
	}

	@Test
	@SuppressWarnings("static-method")
	public void testDrawLineWithPoint() {
		Display display = Display.getDefault();

		ImageGcDrawer imageGcDrawer = (gc, width, height) -> {
			SWTGraphics graphics = new SWTGraphics(gc);
			ScaledGraphics scaledGraphics = new ScaledGraphics(graphics);
			scaledGraphics.scale(2.5);
			ScaledGraphics scaledGraphics2 = new ScaledGraphics(scaledGraphics);
			scaledGraphics2.scale(2);
			scaledGraphics2.drawLine(new PrecisionPoint(15, 0), new PrecisionPoint(15, 20));
			scaledGraphics2.drawLine(new PrecisionPoint(20, 0), new PrecisionPoint(20, 20));
		};

		Image image = new Image(display, imageGcDrawer, 150, 150);
		try {
			ImageData imageData = image.getImageData();
			assertEquals(imageData.getPixel(75, 0), 0, "Pixel at 75 must be 0"); //$NON-NLS-1$
			assertEquals(imageData.getPixel(100, 0), 0, "Pixel at 100 must be 0"); //$NON-NLS-1$
		} finally {
			image.dispose();
		}
	}
}
