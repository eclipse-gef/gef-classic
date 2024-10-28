/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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

import static org.junit.Assert.fail;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.draw2d.ImageUtilities;

import org.junit.Test;

/**
 * @author Pratik Shah
 */
public class ImageUtilitiesTest {

	@SuppressWarnings("static-method")
	@Test
	public void testImageRotation() {
		Image result1 = null, result2 = null, result3 = null;
		Image img1 = new Image(null, 80, 80);
		Image img2 = new Image(null, 801, 69);
		Image img3 = new Image(null, 501, 799);

		try {
			result1 = ImageUtilities.createRotatedImage(img1);
			result2 = ImageUtilities.createRotatedImage(img2);
			result3 = ImageUtilities.createRotatedImage(img3);
		} catch (Throwable error) {
			fail(error.getMessage() + "\n" + error.getStackTrace()); //$NON-NLS-1$
		} finally {
			img1.dispose();
			img2.dispose();
			img3.dispose();
			if (result1 != null && !result1.isDisposed()) {
				result1.dispose();
			}
			if (result2 != null && !result2.isDisposed()) {
				result2.dispose();
			}
			if (result3 != null && !result3.isDisposed()) {
				result3.dispose();
			}
		}
	}

	@Test
	public void testRotatingImagesWithDifferentDepths() {
		Image result1 = null, result2 = null, result3 = null, result4 = null;
		Image img1 = ImageDescriptor.createFromFile(getClass(), "icons/bits1.bmp").createImage(); //$NON-NLS-1$
		Image img2 = ImageDescriptor.createFromFile(getClass(), "icons/bits4.bmp").createImage(); //$NON-NLS-1$ ;
		Image img3 = ImageDescriptor.createFromFile(getClass(), "icons/bits8.gif").createImage(); //$NON-NLS-1$ ;
		Image img4 = ImageDescriptor.createFromFile(getClass(), "icons/bits24.jpg").createImage(); //$NON-NLS-1$ ;

		try {
			result1 = ImageUtilities.createRotatedImage(img1);
			result2 = ImageUtilities.createRotatedImage(img2);
			result3 = ImageUtilities.createRotatedImage(img3);
			result4 = ImageUtilities.createRotatedImage(img4);
		} catch (Throwable error) {
			fail(error.toString() + "\n" + error.getStackTrace()[0]); //$NON-NLS-1$
		} finally {
			img1.dispose();
			img2.dispose();
			img3.dispose();
			img4.dispose();
			if (result1 != null && !result1.isDisposed()) {
				result1.dispose();
			}
			if (result2 != null && !result2.isDisposed()) {
				result2.dispose();
			}
			if (result3 != null && !result3.isDisposed()) {
				result3.dispose();
			}
			if (result4 != null && !result4.isDisposed()) {
				result4.dispose();
			}
		}
	}

}
