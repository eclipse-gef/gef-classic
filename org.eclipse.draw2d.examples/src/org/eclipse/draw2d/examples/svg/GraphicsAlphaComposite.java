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

public class GraphicsAlphaComposite extends AbstractGraphicsComposite {
	private static final Dimension SIZE = new Dimension(200, 200);

	public GraphicsAlphaComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Dimension getGraphicsSize() {
		return SIZE;
	}

	@Override
	protected void paint(Graphics g) {
		g.setBackgroundColor(ColorConstants.red);
		g.fillRectangle(50, 50, 100, 100);
		g.setAlpha(200);
		g.setBackgroundColor(ColorConstants.green);
		g.fillRectangle(75, 75, 125, 125);
		g.setAlpha(100);
		g.setBackgroundColor(ColorConstants.blue);
		g.fillRectangle(0, 0, 125, 125);
	}

}
