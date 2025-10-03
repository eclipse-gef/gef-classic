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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

public class LogicFlowFeedbackBorder extends LogicFlowBorder {

	public LogicFlowFeedbackBorder() {
	}

	public LogicFlowFeedbackBorder(int width) {
		super(width);
	}

	@Override
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
		graphics.setBackgroundColor(LogicColorConstants.feedbackFill);
		graphics.setForegroundColor(LogicColorConstants.feedbackOutline);

		Rectangle r = figure.getBounds();

		graphics.drawRectangle(r.x, r.y, r.width - 1, r.height - 1);

		tempRect.setBounds(new Rectangle(r.x, r.y, grabBarWidth, r.height));

		graphics.fillRectangle(tempRect);
	}
}
