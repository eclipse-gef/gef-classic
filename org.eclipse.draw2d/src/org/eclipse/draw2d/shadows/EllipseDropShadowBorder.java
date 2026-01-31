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
 * A versatile border that provides a CSS-style drop shadow effect for circular
 * and elliptical figures.
 *
 * This border simulates visual depth by layering semi-transparent shapes using
 * multi-pass exponential decay. It is designed to work "out of the box" for
 * standard diagramming nodes while remaining highly tunable for other use
 * cases.
 *
 * This class is currently in development its API may change.
 *
 * @since 3.22 (provisional)
 */
@NoExtend
@NoReference
@NoInstantiate
public class EllipseDropShadowBorder extends AbstractDropShadowBorder {

	@Override
	protected void paintDropShadow(Graphics graphics, Rectangle shadowRect, int size) {
		final Rectangle r = shadowRect.getCopy();
		for (int i = 0; i < size; i++) {
			final double progress = (double) i / size;
			graphics.setAlpha(calcAlphaValue(progress));
			r.x++;
			r.y++;
			graphics.drawArc(r, 270, 90);
		}
	}

	@Override
	protected void paintHalo(Graphics graphics, Rectangle shadowRect, int size) {
		final Rectangle r = shadowRect.getCopy();
		for (int i = 0; i < size; i++) {
			final double progress = (double) i / size;
			graphics.setAlpha(calcAlphaValue(progress));
			graphics.drawOval(r);
			r.expand(1, 1);
		}
	}

}
