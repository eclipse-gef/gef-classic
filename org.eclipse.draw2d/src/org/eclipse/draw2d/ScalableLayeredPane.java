/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.geometry.Translatable;
import org.eclipse.draw2d.internal.InternalDraw2dUtils;

/**
 * A non-freeform, scalable layered pane.
 *
 * @author Eric Bordeau
 * @since 2.1.1
 */
public class ScalableLayeredPane extends LayeredPane implements IScalablePane {

	private double scale = 1.0;

	private final boolean useScaledGraphics;

	public ScalableLayeredPane() {
		this(true);
	}

	/**
	 * Constructor which allows to configure if scaled graphics should be used.
	 *
	 * @since 3.13
	 */
	public ScalableLayeredPane(boolean useScaledGraphics) {
		this.useScaledGraphics = useScaledGraphics;
	}

	/** @see IFigure#getClientArea(Rectangle) */
	@Override
	public Rectangle getClientArea(Rectangle rect) {
		return IScalablePaneHelper.getClientArea(this, super::getClientArea, rect);
	}

	/** @see Figure#getMinimumSize(int, int) */
	@Override
	public Dimension getMinimumSize(int wHint, int hHint) {
		return IScalablePaneHelper.getMinimumSize(this, super::getMinimumSize, wHint, hHint);
	}

	/** @see Figure#getPreferredSize(int, int) */
	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		return IScalablePaneHelper.getPreferredSize(this, super::getPreferredSize, wHint, hHint);
	}

	/**
	 * Returns the scale level, default is 1.0.
	 *
	 * @return the scale level
	 */
	@Override
	public double getScale() {
		return scale;
	}

	/** @see org.eclipse.draw2d.Figure#paintClientArea(Graphics) */
	@Override
	protected void paintClientArea(Graphics graphics) {
		IScalablePane.IScalablePaneHelper.paintClientArea(this, super::paintClientArea, graphics);
	}

	/**
	 * Make this method publicly accessible for IScaleablePane.
	 *
	 * @since 3.13
	 */
	@Override
	public boolean optimizeClip() {
		return super.optimizeClip();
	}

	/**
	 * Sets the zoom level
	 *
	 * @param newZoom The new zoom level
	 */
	@Override
	public void setScale(double newZoom) {
		if (scale == newZoom) {
			return;
		}
		scale = newZoom;
		fireMoved(); // for AncestorListener compatibility
		revalidate();
		repaint();
	}

	/**
	 * @since 3.13
	 */
	@Override
	public boolean useScaledGraphics() {
		return useScaledGraphics;
	}

	/** @see org.eclipse.draw2d.Figure#translateToParent(Translatable) */
	@Override
	public void translateToParent(Translatable t) {
		IScalablePaneHelper.translateToParent(this, t);
	}

	/** @see org.eclipse.draw2d.Figure#translateFromParent(Translatable) */
	@Override
	public void translateFromParent(Translatable t) {
		IScalablePaneHelper.translateFromParent(this, t);
	}

	/** @see org.eclipse.draw2d.IFigure#isCoordinateSystem() */
	@Override
	public boolean isCoordinateSystem() {
		return true;
	}

	/**
	 * @since 3.21
	 * @see org.eclipse.draw2d.Figure#useDoublePrecision()
	 */
	@Override
	protected boolean useDoublePrecision() {
		return InternalDraw2dUtils.isAutoScaleEnabled();
	}

}
