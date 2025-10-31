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

import org.eclipse.swt.SWT;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

public class GroundFeedbackFigure extends GroundFigure {

	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
	 */
	@Override
	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds().getCopy();

		g.setBackgroundColor(LogicColorConstants.feedbackFill);
		g.setForegroundColor(LogicColorConstants.feedbackOutline);
		g.setAntialias(SWT.ON);
		g.setLineWidth(2);

		r.x += 1;
		r.y += 1;
		r.width -= 2;
		r.height -= 2;

		g.fillOval(r);
		g.drawOval(r);
		g.translate(r.getLocation());

		// Draw the "V"
		g.drawLine(6, 8, 10, 18);
		g.drawLine(10, 18, 14, 8);

		// Draw the "0"
		g.drawOval(14, 16, 6, 6);
	}

}
