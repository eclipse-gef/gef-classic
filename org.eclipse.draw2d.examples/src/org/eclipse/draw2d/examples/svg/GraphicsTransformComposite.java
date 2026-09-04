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
package org.eclipse.draw2d.examples.svg;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.internal.FileImageDataProvider;

public class GraphicsTransformComposite extends AbstractGraphicsComposite {
	private static final Dimension SIZE = new Dimension(200, 200);
	private static final Image IMAGE = FileImageDataProvider.createImage(GraphicsTransformComposite.class, "image.png"); //$NON-NLS-1$

	public GraphicsTransformComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Dimension getGraphicsSize() {
		return SIZE;
	}

	@Override
	protected void paint(Graphics g) {
		g.drawImage(IMAGE, 0, 0);

		g.translate(100, 0);
		g.rotate(45);
		g.drawImage(IMAGE, 0, 0);
		g.rotate(-45);
		g.translate(-100, 0);

		g.translate(0, 100);
		g.shear(0.5f, 0.75f);
		g.drawImage(IMAGE, 0, 0);
		g.shear(-0.5f, -0.75f);
		g.translate(-0, -100);

		g.translate(100, 100);
		g.scale(6f, 3f);
		g.drawImage(IMAGE, 0, 0);
		g.scale(1 / 6f, 1 / 3f);
		g.translate(-100, -100);
	}

}
