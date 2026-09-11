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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;

public class GraphicsStringsComposite extends AbstractGraphicsComposite {
	private static final Dimension SIZE = new Dimension(300, 100);

	public GraphicsStringsComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Dimension getGraphicsSize() {
		return SIZE;
	}

	@Override
	protected void paint(Graphics g) {
		FontData fd = getFont().getFontData()[0];
		fd.setHeight(16);
		fd.setStyle(SWT.BOLD | SWT.ITALIC);

		Font f = new Font(getDisplay(), fd);
		Font tmp = g.getFont();

		g.setFont(f);
		g.setForegroundColor(ColorConstants.red);
		g.setBackgroundColor(ColorConstants.blue);

		g.drawText(Messages.getString("GraphicsStringsComposite.HelloWorld"), 0, 0); //$NON-NLS-1$
		g.fillText(Messages.getString("GraphicsStringsComposite.HelloWorld"), 150, 0); //$NON-NLS-1$

		g.translate(0, 50);
		g.drawString(Messages.getString("GraphicsStringsComposite.HelloWorld"), 0, 0); //$NON-NLS-1$
		g.fillString(Messages.getString("GraphicsStringsComposite.HelloWorld"), 150, 0); //$NON-NLS-1$

		g.setFont(tmp);
		f.dispose();
	}
}
