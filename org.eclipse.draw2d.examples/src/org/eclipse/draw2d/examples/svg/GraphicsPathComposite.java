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

import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;

public class GraphicsPathComposite extends AbstractGraphicsComposite {
	private static final Dimension SIZE = new Dimension(200, 200);
	private static final Path PATH = new Path(Display.getCurrent());
	static {
		PATH.moveTo(5, 50);
		PATH.lineTo(25, 50);
		PATH.quadTo(37.5f, 5, 50, 50);
		PATH.cubicTo(62.5f, 100, 62.5f, 200, 37.5f, 190);
		PATH.lineTo(12.5f, 190);
		PATH.close();

		PATH.addRectangle(62.5f, 40, 25, 60);
		PATH.addArc(42.5f, 100, 25, 70, 45, 270);
		PATH.addString(Messages.getString("GraphicsPathComposite.HelloWorld"), 12.5f, 170, Display.getCurrent().getSystemFont()); //$NON-NLS-1$
	}

	public GraphicsPathComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Dimension getGraphicsSize() {
		return SIZE;
	}

	@Override
	protected void paint(Graphics g) {
		g.setForegroundColor(ColorConstants.red);
		g.setBackgroundColor(ColorConstants.blue);
		g.drawPath(PATH);
		g.translate(100, 0);
		g.fillPath(PATH);
	}

}
