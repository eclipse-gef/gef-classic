/*******************************************************************************
 * Copyright (c) 2025 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d;

import org.eclipse.swt.widgets.Canvas;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.geometry.Translatable;
import org.eclipse.draw2d.internal.InternalDraw2dUtils;

/**
 * Custom lightweight-system that is used in combination with the
 * {@code draw2d.enableAutoscale} environment variable. The root figure of this
 * class implements the {@link IScalablePane} interface and is automatically
 * scaled to match the monitor zoom.<br>
 * <b>Important:</b> This class is only public so that it can be referencd in
 * the other GEF plugins. This class must <b>not</b> be used together with a
 * {@link Viewport}. Otherwise the scrollbar and content are always rendered as
 * if at 100% zoom (at least on Windows}.
 *
 * @noreference This class is not intended to be referenced by clients.
 */
public class ScalableLightweightSystem extends LightweightSystem {

	@Override
	public void setControl(Canvas c) {
		if (c == null) {
			return;
		}
		InternalDraw2dUtils.configureForAutoscalingMode(c, getRootFigure()::setScale);
		super.setControl(c);
	}

	@Override
	protected ScalableRootFigure createRootFigure() {
		ScalableRootFigure f = new ScalableRootFigure();
		f.addNotify();
		f.setOpaque(true);
		f.setLayoutManager(new StackLayout());
		return f;
	}

	@Override
	public ScalableFigure getRootFigure() {
		return (ScalableFigure) super.getRootFigure();
	}

	private class ScalableRootFigure extends RootFigure implements IScalablePane {
		private double scale = 1.0;

		@Override
		public void setScale(double scale) {
			if (this.scale == scale) {
				return;
			}
			this.scale = scale;
			fireFigureMoved();
			fireCoordinateSystemChanged();
			revalidate();
			repaint();
		}

		@Override
		public double getScale() {
			return scale;
		}

		/**
		 * @deprecated will be deleted after the 2028-03 release (see
		 *             {@link ScaledGraphics}).
		 */
		@Deprecated(forRemoval = true, since = "2026-03")
		@Override
		public boolean useScaledGraphics() {
			return false;
		}

		@Override
		public boolean optimizeClip() {
			return super.optimizeClip();
		}

		@Override
		public Rectangle getClientArea(Rectangle rect) {
			return IScalablePaneHelper.getClientArea(this, super::getClientArea, rect);
		}

		@Override
		public Dimension getMinimumSize(int wHint, int hHint) {
			return IScalablePaneHelper.getMinimumSize(this, super::getMinimumSize, wHint, hHint);
		}

		@Override
		public Dimension getPreferredSize(int wHint, int hHint) {
			return IScalablePaneHelper.getPreferredSize(this, super::getPreferredSize, wHint, hHint);
		}

		@Override
		protected void paintClientArea(Graphics graphics) {
			IScalablePaneHelper.paintClientArea(this, super::paintClientArea, graphics);
		}

		@Override
		public void translateToParent(Translatable t) {
			IScalablePaneHelper.translateToParent(this, t);
		}

		@Override
		public void translateFromParent(Translatable t) {
			IScalablePaneHelper.translateFromParent(this, t);
		}

		@Override
		protected boolean useDoublePrecision() {
			return true;
		}
	}
}
