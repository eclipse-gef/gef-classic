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

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.geometry.Translatable;
import org.eclipse.draw2d.internal.InternalDraw2dUtils;

/**
 * @author hudsonr
 * @since 2.1
 */
public class ScalableFreeformLayeredPane extends FreeformLayeredPane implements IScalablePane {

	private double scale = 1.0;

	/**
	 * @deprecated will be deleted after the 2028-03 release (see
	 *             {@link ScaledGraphics}).
	 */
	@Deprecated(forRemoval = true, since = "2026-03")
	private final boolean useScaledGraphics;

	public ScalableFreeformLayeredPane() {
		this(InternalDraw2dUtils.useScaledGraphicsByDefault());
	}

	/**
	 * Constructor which allows to configure if scaled graphics should be used.
	 *
	 * @since 3.13
	 * @deprecated will be deleted after the 2028-03 release (see
	 *             {@link ScaledGraphics}).
	 */
	@Deprecated(forRemoval = true, since = "2026-03")
	public ScalableFreeformLayeredPane(boolean useScaledGraphics) {
		this.useScaledGraphics = useScaledGraphics;
	}

	/** @see org.eclipse.draw2d.Figure#getClientArea() */
	@Override
	public Rectangle getClientArea(Rectangle rect) {
		return IScalablePaneHelper.getClientArea(this, super::getClientArea, rect);
	}

	/**
	 * Returns the current zoom scale level.
	 *
	 * @return the scale
	 */
	@Override
	public double getScale() {
		return scale;
	}

	/**
	 * @see org.eclipse.draw2d.IFigure#isCoordinateSystem()
	 */
	@Override
	public boolean isCoordinateSystem() {
		return true;
	}

	/** @see org.eclipse.draw2d.Figure#paintClientArea(Graphics) */
	@Override
	protected void paintClientArea(final Graphics graphics) {
		IScalablePaneHelper.paintClientArea(this, super::paintClientArea, graphics);
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
		superFireMoved(); // For AncestorListener compatibility
		getFreeformHelper().invalidate();
		repaint();
	}

	/**
	 * @since 3.13
	 * @deprecated will be deleted after the 2028-03 release (see
	 *             {@link ScaledGraphics}).
	 */
	@Deprecated(forRemoval = true, since = "2026-03")
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

	/**
	 * @since 3.21
	 * @see org.eclipse.draw2d.Figure#useDoublePrecision()
	 */
	@Override
	protected boolean useDoublePrecision() {
		return InternalDraw2dUtils.isAutoScaleEnabled();
	}
}
