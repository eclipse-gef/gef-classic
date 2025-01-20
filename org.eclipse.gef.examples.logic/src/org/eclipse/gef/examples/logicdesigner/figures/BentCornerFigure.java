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
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A figure that has a bent corner in the top right hand. Typically used for
 * sticky notes.
 */
public class BentCornerFigure extends Figure {

	/**
	 * The default amount of pixels subtracted from the figure's height and width to
	 * determine the size of the corner.
	 */
	protected static int DEFAULT_CORNER_SIZE = 10;

	private int cornerSize;

	/**
	 * Constructs an empty BentCornerFigure with default background color of
	 * ColorConstants.tooltipBackground and default corner size.
	 */
	public BentCornerFigure() {
		setBackgroundColor(ColorConstants.tooltipBackground);
		setForegroundColor(ColorConstants.tooltipForeground);
		setCornerSize(DEFAULT_CORNER_SIZE);
	}

	/**
	 * Returns the size, in pixels, that the figure should use to draw its bent
	 * corner.
	 *
	 * @return size of the corner
	 */
	public int getCornerSize() {
		return cornerSize;
	}

	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	@Override
	protected void paintFigure(Graphics graphics) {
		Rectangle rect = getBounds().getCopy();

		graphics.translate(getLocation());

		// fill the note
		PointList outline = new PointList();

		outline.addPoint(0, 0);
		outline.addPoint(rect.width - cornerSize, 0);
		outline.addPoint(rect.width - 1, cornerSize);
		outline.addPoint(rect.width - 1, rect.height - 1);
		outline.addPoint(0, rect.height - 1);

		graphics.fillPolygon(outline);

		graphics.pushState();
		graphics.setLineWidth(2);
		graphics.drawRoundRectangle(new Rectangle(0, 0, rect.width - 1, rect.height - 1), 8, 8);

		// draw a rectangle over the bent corner area to hide the rounded corner
		graphics.setBackgroundColor(ColorConstants.white);
		graphics.fillRectangle(rect.width - cornerSize - 1, 0, cornerSize + 2, cornerSize + 1);
		graphics.popState();

		// Draw bent corner lines
		graphics.drawLine(rect.width - cornerSize - 1, 0, rect.width - cornerSize - 1, cornerSize);
		graphics.drawLine(rect.width - cornerSize - 1, cornerSize, rect.width - 1, cornerSize);
		graphics.drawLine(rect.width - cornerSize - 1, 0, rect.width - 1, cornerSize);
		graphics.translate(getLocation().getNegated());
	}

	/**
	 * Sets the size of the figure's corner to the given offset.
	 *
	 * @param newSize the new size to use.
	 */
	public void setCornerSize(int newSize) {
		cornerSize = newSize;
	}

}
