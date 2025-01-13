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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

public class LogicFlowBorder extends org.eclipse.draw2d.LineBorder {

	protected int grabBarWidth = 40;
	protected Dimension grabBarSize = new Dimension(grabBarWidth, 36);

	public LogicFlowBorder() {
	}

	public LogicFlowBorder(int width) {
		setGrabBarWidth(width);
		grabBarSize = new Dimension(width, 36);
	}

	@Override
	public Insets getInsets(IFigure figure) {
		return new Insets(getWidth() + 4, grabBarWidth + 4, getWidth() + 4, getWidth() + 4);

	}

	public Dimension getPreferredSize() {
		return grabBarSize;
	}

	@Override
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
		Rectangle bounds = figure.getBounds();
		tempRect.setBounds(new Rectangle(bounds.x, bounds.y, grabBarWidth, bounds.height));
		graphics.setBackgroundColor(LogicColorConstants.logicGreen);
		graphics.fillRectangle(tempRect);
		super.paint(figure, graphics, insets);
	}

	public void setGrabBarWidth(int width) {
		grabBarWidth = width;
	}

}
