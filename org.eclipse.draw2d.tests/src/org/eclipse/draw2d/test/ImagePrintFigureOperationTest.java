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

package org.eclipse.draw2d.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImagePrintFigureOperation;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ImagePrintFigureOperationTest {
	private Shell shell;
	private IFigure figure;

	@BeforeEach
	public void setUp() {
		figure = new Figure();
		figure.setBounds(new Rectangle(10, 20, 16, 32));
		shell = new Shell();
	}

	@AfterEach
	public void tearDown() {
		shell.dispose();
	}

	@Test
	public void testPrintWithAutoScaling() {
		testPrintWithOperation(new ImagePrintFigureOperation(shell, figure) {
			@Override
			protected boolean isDraw2DAutoScalingEnabled() {
				return true;
			}
		});
	}

	/**
	 * @see <a href="https://github.com/eclipse-gef/gef-classic/issues/1146">Issue
	 *      1146</a>
	 */
	@Test
	public void testPaintWithAutoScaling() {
		figure.setSize(310, 310);
		Image image = new ImagePrintFigureOperation(shell, figure) {
			@Override
			protected boolean isDraw2DAutoScalingEnabled() {
				return true;
			}
		}.run();

		GC gc = new GC(shell);
		gc.drawImage(image, 0, 0, 310, 310, 0, 0, 100, 100);
		gc.dispose();

		image.dispose();
	}

	/**
	 * SWT uses {@link Math#round(double)} for rounding, while Draw2D uses
	 * {@link Math#floor(double)}. For a zoom < 100% this leads to a mismatch which
	 * results in an {@link IllegalArgumentException} when trying to paint the
	 * image, as the "scaled" image doesn't match the expected size due to an
	 * off-by-one rounding error.
	 *
	 * @see <a href="https://github.com/eclipse-gef/gef-classic/issues/1146">Issue
	 *      1146</a>
	 */
	@Test
	public void testImageBoundsWithAutoScaling() {
		figure.setSize(310, 310);
		Image image = new ImagePrintFigureOperation(shell, figure) {
			@Override
			protected boolean isDraw2DAutoScalingEnabled() {
				return true;
			}
		}.run();

		for (int i = 1; i < 100; ++i) {
			ImageData imageData = image.getImageData(i);
			assertEquals(Math.round(310 * i / 100.0), imageData.width);
			assertEquals(Math.round(310 * i / 100.0), imageData.height);
		}

		image.dispose();
	}

	@Test
	public void testPrintWithoutAutoScaling() {
		testPrintWithOperation(new ImagePrintFigureOperation(shell, figure) {
			@Override
			protected boolean isDraw2DAutoScalingEnabled() {
				return false;
			}
		});
	}

	private static void testPrintWithOperation(ImagePrintFigureOperation printer) {
		Image image = printer.run();
		try {
			assertImageDataSize(image.getImageData(400), 64, 128);
			assertImageDataSize(image.getImageData(200), 32, 64);
			assertImageDataSize(image.getImageData(150), 24, 48);
			assertImageDataSize(image.getImageData(100), 16, 32);
			assertImageDataSize(image.getImageData(50), 8, 16);
			assertImageDataSize(image.getImageData(25), 4, 8);
			// Image is too small (width=0 and height=0)
			IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
					() -> assertImageDataSize(image.getImageData(1), 1, 1));
			assertEquals("Argument not valid", e.getMessage()); //$NON-NLS-1$
		} finally {
			image.dispose();
		}

	}

	private static void assertImageDataSize(ImageData data, int width, int height) {
		assertEquals(width, data.width, "Unexpected width of image data."); //$NON-NLS-1$
		assertEquals(height, data.height, "Unexpected height of image data."); //$NON-NLS-1$
	}
}
