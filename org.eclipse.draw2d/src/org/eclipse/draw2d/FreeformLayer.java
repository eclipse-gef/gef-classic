/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
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
package org.eclipse.draw2d;

import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A Layer that can extend in all 4 directions.
 */
public class FreeformLayer extends Layer implements FreeformFigure {

	private final FreeformHelper helper = new FreeformHelper(this);

	/**
	 * @see IFigure#add(IFigure, Object, int)
	 */
	@Override
	public void add(IFigure child, Object constraint, int index) {
		super.add(child, constraint, index);
		helper.hookChild(child);
	}

	/**
	 * @see FreeformFigure#addFreeformListener(FreeformListener)
	 */
	@Override
	public void addFreeformListener(FreeformListener listener) {
		addListener(FreeformListener.class, listener);
	}

	/**
	 * @see FreeformFigure#fireExtentChanged()
	 */
	@Override
	public void fireExtentChanged() {
		getListenersIterable(FreeformListener.class).forEach(FreeformListener::notifyFreeformExtentChanged);
	}

	/**
	 * Overrides to do nothing.
	 *
	 * @see Figure#fireMoved()
	 */
	@Override
	protected void fireMoved() {
	}

	/**
	 * @see FreeformFigure#getFreeformExtent()
	 */
	@Override
	public Rectangle getFreeformExtent() {
		return helper.getFreeformExtent();
	}

	/**
	 * @see Figure#primTranslate(int, int)
	 */
	@Override
	public void primTranslate(int dx, int dy) {
		bounds.x += dx;
		bounds.y += dy;
	}

	/**
	 * @see IFigure#remove(IFigure)
	 */
	@Override
	public void remove(IFigure child) {
		helper.unhookChild(child);
		super.remove(child);
	}

	/**
	 * @see FreeformFigure#removeFreeformListener(FreeformListener)
	 */
	@Override
	public void removeFreeformListener(FreeformListener listener) {
		removeListener(FreeformListener.class, listener);
	}

	/**
	 * @see FreeformFigure#setFreeformBounds(Rectangle)
	 */
	@Override
	public void setFreeformBounds(Rectangle bounds) {
		helper.setFreeformBounds(bounds);
	}

}
