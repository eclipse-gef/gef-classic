/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Draws a Rectangle whose corners are rounded in appearance. The size of the rectangle is 
 * determined by the bounds set to it.
 */
public class RoundedRectangle extends Shape {

/** The width and height radii applied to each corner. */
protected Dimension corner = new Dimension(8, 8);

/**
 * Constructs a round cornered rectangle.
 */
public RoundedRectangle() { }

/**
 * @see Shape#fillShape(Graphics)
 */
protected void fillShape(Graphics graphics) {
	graphics.fillRoundRectangle(getBounds(), corner.width, corner.height);
}

/**
 * @see Shape#outlineShape(Graphics)
 */
protected void outlineShape(Graphics graphics) {
	int lineInset = (int)Math.ceil(Math.max(1.0, getLineWidthFloat() / 2.0));
	Rectangle r = Rectangle.SINGLETON.setBounds(getBounds());
	r.shrink(lineInset, lineInset);
	
	graphics.drawRoundRectangle(r, Math.max(0, corner.width - lineInset), Math.max(0, corner.height - lineInset));
}

/**
 * Sets the dimensions of each corner. This will form the radii of the arcs which form the
 * corners.
 *
 * @param d the dimensions of the corner
 * @since 2.0
 */
public void setCornerDimensions(Dimension d) {
	corner.width = d.width;
	corner.height = d.height;
}

}
