/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef3.handles;

import org.eclipse.draw2dl.IFigure;
import org.eclipse.draw2dl.RelativeLocator;
import org.eclipse.draw2dl.geometry.Rectangle;

/**
 * Similar to {@link RelativeLocator}, but adds support for
 * the {@link org.eclipse.gef3.handles.HandleBounds} interface. If the reference
 * figure implements <code>HandleBounds</code>, then
 * {@link HandleBounds#getHandleBounds()} will be used as the reference box. If
 * not, the behavior is the same as that of the superclass.
 * 
 * @author hudsonr
 */
public class RelativeHandleLocator extends RelativeLocator {

	/**
	 * Constructs a new locator using the given reference figure and location.
	 * 
	 * @param reference
	 *            the reference figure
	 * @param location
	 *            one of NORTH, NORTH_EAST, etc.
	 */
	public RelativeHandleLocator(IFigure reference, int location) {
		super(reference, location);
	}

	/**
	 * Overridden to check for reference figures implementing the
	 * <code>HandleBounds</code> interface.
	 * 
	 * @see RelativeLocator#getReferenceBox()
	 */
	protected Rectangle getReferenceBox() {
		IFigure f = getReferenceFigure();
		if (f instanceof HandleBounds)
			return ((HandleBounds) f).getHandleBounds();
		return super.getReferenceBox();
	}

}
