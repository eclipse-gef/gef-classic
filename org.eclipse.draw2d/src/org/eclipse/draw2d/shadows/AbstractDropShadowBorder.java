/*******************************************************************************
 * Copyright (c) 2026 Johannes Kepler University Linz and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alois Zoitl - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d.shadows;

import org.eclipse.swt.graphics.Color;

import org.eclipse.pde.api.tools.annotations.NoExtend;
import org.eclipse.pde.api.tools.annotations.NoInstantiate;
import org.eclipse.pde.api.tools.annotations.NoReference;

import org.eclipse.draw2d.AbstractBackground;
import org.eclipse.draw2d.ColorProvider;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A base class for implementing CSS style drop shadow border figures.
 *
 * The idea is that shadow consist of a halo and a drop shadow. This class
 * features all base properties for such shadows consisting of the shadow color,
 * alpha, softness, size of the halo and the drop shadow.
 *
 * Subclasses need to implement the exact drawing of the shadow (e.g., @see
 * RectDropShadowBorder).
 *
 * This class is currently in development its API may change.
 *
 * @since 3.22 (provisional)
 */
@NoExtend
@NoReference
@NoInstantiate
public abstract class AbstractDropShadowBorder extends AbstractBackground {

	/**
	 * default value for the drop shadow size as suited for a figure in an diagram
	 *
	 */
	protected static final int DEFAULT_DROP_SHADOW_SIZE = 6;
	/**
	 * default value for the halo size as suited for a figure in an diagram
	 *
	 */
	protected static final int DEFAULT_HALO_SIZE = 3;
	protected static final int DEFAULT_SHADOW_ALPHA = 25;
	protected static final double DEFAULT_SOFTNESS = 4.0;
	/**
	 * Per default the shadow insets are empty so that the shadow will be outside of
	 * the figure
	 */
	private static final Insets DEFAULT_SHADOW_INSETS = new Insets();
	private static final Rectangle CLIP_RECT_CACHE = new Rectangle();
	private int dropShadowSize;
	private int haloSize;
	private Insets insets;
	private int shadowAlpha;
	private Color shadowColor;
	private double softness;

	protected AbstractDropShadowBorder() {
		dropShadowSize = DEFAULT_DROP_SHADOW_SIZE;
		haloSize = DEFAULT_HALO_SIZE;
		insets = DEFAULT_SHADOW_INSETS;
		shadowAlpha = DEFAULT_SHADOW_ALPHA;
		shadowColor = ColorProvider.SystemColorFactory.getColorProvider().getShadowColor();
		softness = DEFAULT_SOFTNESS;
	}

	protected int calcAlphaValue(final double progress) {
		return Math.max(1, (int) (shadowAlpha * Math.exp(-softness * progress)));
	}

	private void doHalo(Graphics graphics, final Rectangle shadowRect, double absScale) {
		int shadowAlphaBuf = shadowAlpha;
		int haloSizeToUse = haloSize;
		if (absScale * haloSize < 1.0) {
			// halo is to small draw just one darker shadow
			haloSizeToUse = 1;
			shadowAlpha = 3 * shadowAlpha;
		}
		paintHalo(graphics, shadowRect, haloSizeToUse);
		shadowAlpha = shadowAlphaBuf;
	}

	public void doDropShadow(Graphics graphics, final Rectangle shadowRect, double absScale) {
		int shadowAlphaBuf = shadowAlpha;
		int dropShadowSizeUse = dropShadowSize;
		if (absScale * dropShadowSize < 1.0) {
			// dropshadow is to small draw just one darker shadow
			dropShadowSizeUse = 1;
			shadowAlpha = 3 * shadowAlpha;
		}
		paintDropShadow(graphics, shadowRect, dropShadowSizeUse);
		shadowAlpha = shadowAlphaBuf;
	}

	@Override
	public Insets getInsets(final IFigure figure) {
		return insets;
	}

	@Override
	public boolean isOpaque() {
		return true;
	}

	@Override
	public void paintBackground(IFigure figure, Graphics graphics, Insets insets) {
		// for performance reasons cache the main Graphics properties to be reset
		// afterwards
		final var foregroundColor = graphics.getForegroundColor();
		final var alpha = graphics.getAlpha();
		final var lineWidth = graphics.getLineWidth();
		graphics.getClip(CLIP_RECT_CACHE);

		graphics.setForegroundColor(shadowColor);
		graphics.setLineWidth(2);

		final Rectangle shadowRect = getPaintRectangle(figure, getInsets(figure));
		updateClip(graphics, shadowRect);

		double absScale = graphics.getAbsoluteScale();
		doHalo(graphics, shadowRect, absScale);
		doDropShadow(graphics, shadowRect, absScale);

		graphics.setForegroundColor(foregroundColor);
		graphics.setAlpha(alpha);
		graphics.setLineWidth(lineWidth);
		graphics.clipRect(CLIP_RECT_CACHE);
	}

	protected abstract void paintDropShadow(final Graphics graphics, final Rectangle shadowRect, final int size);

	protected abstract void paintHalo(final Graphics graphics, final Rectangle shadowRect, final int size);

	/**
	 * Sets the size of the directional shadow that simulates a light source.
	 *
	 * This shadow is drawn down and right to provide a sense of depth and
	 * orientation. **Suggested Value:** 5px for low elevation, 15px+ for high
	 * elevation.
	 *
	 * @param dropShadowSize The size in pixels
	 */
	public void setDropShadowSize(int dropShadowSize) {
		this.dropShadowSize = dropShadowSize;
	}

	/**
	 * Sets the size of the ambient shadow (halo) that surrounds the figure
	 * uniformly.
	 *
	 * The halo simulates **Ambient Occlusion**, providing a subtle lift from the
	 * background regardless of light direction. **Suggested Value:** 25% to 50% of
	 * the Drop Shadow size.
	 *
	 * @param haloSize The size in pixels
	 */
	public void setHaloSize(int haloSize) {
		this.haloSize = haloSize;
	}

	/**
	 * This allows to define where the drop shadow is drawn. Larger the insets
	 * values will reduce the size of the figure and the shadow will move inside the
	 * bounds.
	 *
	 * @param insets The insets to be used for the drop shadow
	 */
	public void setInsets(Insets insets) {
		this.insets = insets;
	}

	/**
	 * Sets the initial opacity of the shadow passes.
	 *
	 * This value determines the "darkness" of the shadow at its point of origin.
	 * The renderer will use this as the starting point for the exponential decay
	 * calculation (see also {@link #setSoftness(double)}.
	 *
	 * @param shadowAlpha The maximum alpha (0-255)
	 */
	public void setShadowAlpha(int shadowAlpha) {
		this.shadowAlpha = shadowAlpha;
	}

	/**
	 * Set the color to be used for the shadow. For example light neon style colors
	 * can be used to indicate a glow below a figure.
	 *
	 * @param shadowColor The color to be used for drawing the drop shadow
	 */
	public void setShadowColor(Color shadowColor) {
		this.shadowColor = shadowColor;
	}

	/**
	 * Sets the decay rate of the shadow transparency.
	 *
	 * This simulates the "blur" of the light. A value of **2.0** produces a
	 * hard-edged shadow, while **8.0** creates a very diffuse, misty effect.
	 *
	 * @param softness The exponential decay factor (suggested range: 2.0 - 8.0)
	 */
	public void setSoftness(double softness) {
		this.softness = softness;
	}

	private void updateClip(Graphics graphics, Rectangle shadowRect) {
		int shadowSize = Math.max(haloSize, dropShadowSize) + 1;
		shadowRect.expand(shadowSize, shadowSize);
		graphics.setClip(shadowRect);
		shadowRect.shrink(shadowSize, shadowSize);
	}

}