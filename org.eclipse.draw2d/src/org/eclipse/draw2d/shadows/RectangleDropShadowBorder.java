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

import org.eclipse.pde.api.tools.annotations.NoExtend;
import org.eclipse.pde.api.tools.annotations.NoInstantiate;
import org.eclipse.pde.api.tools.annotations.NoReference;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A versatile border that provides a CSS-style drop shadow effect for both
 * rectangular and rounded-rectangular figures.
 *
 * This border simulates visual depth by layering semi-transparent shapes using
 * multi-pass exponential decay. It is designed to work "out of the box" for
 * standard diagramming nodes while remaining highly tunable.
 *
 * This class is currently in development its API may change.
 *
 * @since 3.22 (provisional)
 */
@NoExtend
@NoReference
@NoInstantiate
public class RectangleDropShadowBorder extends AbstractDropShadowBorder {

	/**
	 * The default value for the corner radius is suited for rectangles
	 *
	 */
	private static final int DEFAULT_CORNER_START_DIAMETER = 4;

	private int startCornerArcSize;

	/**
	 * Default constructor that set everything for a rectangular shaped figure.
	 */
	public RectangleDropShadowBorder() {
		this(DEFAULT_CORNER_START_DIAMETER);

	}

	/**
	 * Create a shadow border for a rounded rectangle with the given corner arc
	 * diamiter.
	 *
	 * @param arcSize The corner arc diameter
	 */
	public RectangleDropShadowBorder(int arcSize) {
		startCornerArcSize = arcSize;
	}

	@Override
	protected void paintDropShadow(final Graphics graphics, final Rectangle shadowRect, final int size) {
		int cornerRadius = startCornerArcSize / 2;
		int bottomXStart = shadowRect.x + cornerRadius;
		int bottomY = shadowRect.y + 1 + shadowRect.height;
		final int bottomXEnd = shadowRect.x + shadowRect.width + 1 - cornerRadius;

		int rightX = shadowRect.x + shadowRect.width + 1;
		int rightYStart = shadowRect.y + cornerRadius;
		final int rightYEnd = shadowRect.y + shadowRect.height + 1 - cornerRadius;
		int cornerDiameter = startCornerArcSize;

		for (int i = 0; i <= size; i++) {
			final double progress = (double) i / size;
			graphics.setAlpha(calcAlphaValue(progress));

			graphics.drawLine(bottomXStart, bottomY, bottomXEnd, bottomY);
			graphics.drawLine(rightX, rightYStart, rightX, rightYEnd);
			graphics.drawArc(rightX - cornerDiameter, bottomY - cornerDiameter, cornerDiameter, cornerDiameter, 270,
					90);

			bottomXStart++;
			bottomY++;
			rightX++;
			rightYStart++;
			cornerDiameter += 2;
		}
	}

	@Override
	protected void paintHalo(final Graphics graphics, final Rectangle shadowRect, final int size) {
		final Rectangle r = shadowRect.getCopy();
		int cornerArcSize = startCornerArcSize;
		for (int i = 0; i < size; i++) {
			final double progress = (double) i / size;
			graphics.setAlpha(calcAlphaValue(progress));
			graphics.drawRoundRectangle(r, cornerArcSize, cornerArcSize);
			cornerArcSize += 2;
			r.expand(1, 1);
		}
	}

	/**
	 * Set the corner arc size for the drop shadow. The default value is suited for
	 * rectangles for rounded rectangles it should be set to the corner diameter of
	 * the rounded rectangle this shadow is bounding.
	 *
	 * @param cornerArcSize
	 */
	public void setCornerArcSize(int cornerArcSize) {
		startCornerArcSize = cornerArcSize;
	}

}
