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
	private static final int CORNER_RADIUS = 8;

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
		graphics.setLineWidth(4);

		// Draw grab bar
		Rectangle grabBar = new Rectangle(bounds.x, bounds.y, grabBarWidth, bounds.height);
		graphics.setBackgroundColor(LogicColorConstants.logicGreen);
		graphics.fillRoundRectangle(grabBar, CORNER_RADIUS, CORNER_RADIUS);

		// Fill right part
		Rectangle rightPart = grabBar.getCopy();
		rightPart.x += rightPart.width / 2;
		rightPart.width = rightPart.width / 2;
		graphics.fillRectangle(rightPart);

		// Draw main border
		Rectangle mainBorder = bounds.getCopy();
		mainBorder.width--;
		mainBorder.height--;
		graphics.setForegroundColor(LogicColorConstants.logicGreen);
		graphics.drawRoundRectangle(mainBorder, CORNER_RADIUS, CORNER_RADIUS);
	}

	public void setGrabBarWidth(int width) {
		grabBarWidth = width;
	}

}
