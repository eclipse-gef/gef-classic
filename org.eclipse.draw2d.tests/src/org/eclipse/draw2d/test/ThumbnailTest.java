/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
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

import static org.junit.Assert.assertTrue;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.parts.Thumbnail;

import org.junit.Test;

public class ThumbnailTest {

	class TestThumbnail extends Thumbnail {
		@Override
		public Image getThumbnailImage() {
			return super.getThumbnailImage();
		}
	}

	@Test
	public void testThumbnail() {
		TestThumbnail thumb = new TestThumbnail();
		IFigure fig = new Ellipse();
		fig.setBounds(new Rectangle(0, 0, 100, 100));
		fig.setFont(Display.getDefault().getSystemFont());
		thumb.setSource(fig);
		thumb.setBounds(new Rectangle(0, 0, 100, 100));
		Image img = thumb.getThumbnailImage();
		assertTrue(img != null);
	}

	@Test
	public void testEmptyThumbnail() {
		TestThumbnail thumb = new TestThumbnail();
		IFigure fig = new Ellipse();
		fig.setBounds(new Rectangle(0, 0, 100, 100));
		fig.setFont(Display.getDefault().getSystemFont());
		thumb.setSource(fig);
		thumb.setBounds(new Rectangle(0, 0, 100, 100));
		Image img = thumb.getThumbnailImage();
		assertTrue(img != null);

		fig.setBounds(new Rectangle(0, 0, 0, 0));
		fig.revalidate();
		img = thumb.getThumbnailImage();

		assertTrue(img == null);
	}

}
