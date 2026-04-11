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
			assertImageDataSize(image.getImageData(1), 1, 1);
		} finally {
			image.dispose();
		}

	}

	private static void assertImageDataSize(ImageData data, int width, int height) {
		assertEquals(width, data.width, "Unexpected width of image data."); //$NON-NLS-1$
		assertEquals(height, data.height, "Unexpected height of image data."); //$NON-NLS-1$
	}
}
