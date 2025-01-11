/*******************************************************************************
 * Copyright (c) 2000, 2022 IBM Corporation and others.
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
package org.eclipse.gef.examples.logicdesigner.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author danlee
 */
public class LiveOutputFigure extends OutputFigure {
	public static final Dimension SIZE = new Dimension(30, 30);

	/**
	 * Constructor for LiveOutputFigure.
	 */
	public LiveOutputFigure() {
	}

	/**
	 * @see org.eclipse.draw2d.Figure#getPreferredSize(int, int)
	 */
	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		return SIZE;
	}

	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
	 */
	@Override
	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds().getCopy();
		g.setBackgroundColor(ColorConstants.yellow);

		g.fillOval(r);
		r.height--;
		r.width--;
		g.drawOval(r);
		g.translate(r.getLocation());

		// Draw the "V"
		g.drawLine(6, 8, 10, 18);
		g.drawLine(10, 18, 14, 8);

		// Draw the "+"
		g.drawLine(18, 14, 18, 22);
		g.drawLine(14, 18, 22, 18);
	}

}
