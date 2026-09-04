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

import org.eclipse.swt.widgets.Composite;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

public class GraphicsClippingComposite extends AbstractGraphicsComposite {
	private static final Dimension SIZE = new Dimension(250, 100);

	public GraphicsClippingComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Dimension getGraphicsSize() {
		return SIZE;
	}

	@Override
	protected void paint(Graphics g) {
		g.setClip(new Rectangle(0, 0, SIZE.width, SIZE.height));
		g.setBackgroundColor(ColorConstants.red);
		g.fillRectangle(0, 0, 100, 100);

		g.pushState();
		g.clipRect(new Rectangle(10, 10, 80, 80));
		g.translate(10, 10);
		g.setBackgroundColor(ColorConstants.green);
		g.fillRectangle(10, 10, 80, 80);

		g.pushState();
		g.translate(10, 10);
		g.clipRect(new Rectangle(20, 20, 60, 60));
		g.setBackgroundColor(ColorConstants.blue);
		g.fillRectangle(20, 20, 60, 60);

		g.pushState();
		g.clipRect(new Rectangle(30, 30, 40, 40));
		g.translate(10, 10);
		g.setBackgroundColor(ColorConstants.yellow);
		g.fillRectangle(30, 30, 40, 40);

		g.pushState();
		g.clipRect(new Rectangle(40, 40, 20, 20));
		g.translate(10, 10);
		g.setBackgroundColor(ColorConstants.gray);
		g.fillRectangle(40, 40, 20, 20);
		g.popState();
		g.popState();
		g.popState();
		g.popState();

		g.translate(150, 0);

		g.pushState();
		g.setBackgroundColor(ColorConstants.red);
		g.fillOval(0, 0, 100, 100);

		g.translate(10, 10);
		g.clipRect(new Rectangle(0, 0, 80, 80));
		g.setBackgroundColor(ColorConstants.green);
		g.fillOval(0, 0, 80, 80);

		g.translate(10, 10);
		g.clipRect(new Rectangle(0, 0, 60, 60));
		g.setBackgroundColor(ColorConstants.blue);
		g.fillOval(0, 0, 60, 60);

		g.translate(10, 10);
		g.clipRect(new Rectangle(0, 0, 40, 40));
		g.setBackgroundColor(ColorConstants.yellow);
		g.fillOval(0, 0, 40, 40);

		g.translate(10, 10);
		g.clipRect(new Rectangle(0, 0, 20, 20));
		g.setBackgroundColor(ColorConstants.gray);
		g.fillOval(0, 0, 20, 20);
		g.popState();
	}
}
