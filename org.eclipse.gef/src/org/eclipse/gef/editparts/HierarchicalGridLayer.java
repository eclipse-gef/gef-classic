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

package org.eclipse.gef.editparts;

import org.eclipse.swt.SWT;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * HierarchicalGridLayer is a background layer that provides a dual-level
 * coordinate system (Major/Minor).
 * <p>
 * Unlike the standard {@link GridLayer}, this implementation reduces visual
 * cognitive load by interleaving a secondary "Major" grid over a "Minor"
 * point-based texture.
 *
 * @since 3.26
 */
public class HierarchicalGridLayer extends GridLayer {
	private static final double MIN_ABSOLUTE_INTERLEAVE = 5.0;
	private static final int DEFAULT_MAJOR_INTERLEAVE = 10;
	private static final int MAJOR_ALPHA = 30;
	private static final int MINOR_ALPHA = 70;

	private int majorInterleave = DEFAULT_MAJOR_INTERLEAVE;
	private float[] minorLineStyle;

	public HierarchicalGridLayer() {
		updateMinorLineStyle();
		setForegroundColor(null); // we want the default foreground color
	}

	@Override
	public void setSpacing(final Dimension spacing) {
		super.setSpacing(spacing);
		updateMinorLineStyle();
	}

	public void setMajorInterleave(int majorInterleave) {
		this.majorInterleave = majorInterleave;
		updateMinorLineStyle();
	}

	@Override
	protected void paintGrid(final Graphics g) {
		final int origLineStyle = g.getLineStyle();
		final int alpha = g.getAlpha();
		g.setLineDash(minorLineStyle);
		g.setLineWidth(1);

		final Rectangle clip = g.getClip(Rectangle.SINGLETON);

		if (gridX > 0) {
			drawVerLines(g, clip);
		}

		if (gridY > 0) {
			drawHorLines(g, clip);
		}
		g.setAlpha(alpha);
		g.setLineStyle(origLineStyle);
	}

	private void drawVerLines(final Graphics g, final Rectangle clip) {
		final int majorInterleaveX = gridX * majorInterleave;
		final int realInterleaveX = determineInterleave(gridX, majorInterleaveX, g.getAbsoluteScale());

		if (realInterleaveX > 0) {
			final int startX = clip.x - Math.floorMod(clip.x, gridX);
			final int startY = clip.y - Math.floorMod(clip.y, majorInterleaveX) + gridY;

			for (int x = startX; x <= clip.right(); x += gridX) {
				if (x % majorInterleaveX == 0) {
					g.setLineStyle(SWT.LINE_SOLID);
					g.setAlpha(MAJOR_ALPHA);
					g.drawLine(x, clip.y, x, clip.bottom());
				} else {
					g.setLineStyle(SWT.LINE_CUSTOM);
					g.setAlpha(MINOR_ALPHA);
					g.drawLine(x, startY, x, clip.bottom());
				}
			}
		}
	}

	private void drawHorLines(final Graphics g, final Rectangle clip) {
		final int mojorInterleaveY = gridY * majorInterleave;

		if (mojorInterleaveY * g.getAbsoluteScale() > MIN_ABSOLUTE_INTERLEAVE) {
			final int startY = clip.y - Math.floorMod(clip.y, mojorInterleaveY);

			g.setLineStyle(SWT.LINE_SOLID);
			g.setAlpha(MAJOR_ALPHA);

			for (int y = startY; y <= clip.bottom(); y += mojorInterleaveY) {
				g.drawLine(clip.x, y, clip.right(), y);
			}
		}
	}

	private static int determineInterleave(final int interleave, final int majorInterleave,
			final double absoluteScale) {
		// only draw minor grid at a scale larger then 70%
		if (absoluteScale > 0.7) {
			return interleave;
		}

		if (majorInterleave * absoluteScale > MIN_ABSOLUTE_INTERLEAVE) {
			return majorInterleave;
		}
		return -1;
	}

	private void updateMinorLineStyle() {
		final float normalGap = (float) gridY - 1;
		minorLineStyle = new float[(majorInterleave - 1) * 2];

		for (int i = 0; i < (majorInterleave - 1) * 2; i += 2) {
			minorLineStyle[i] = 1.0f;
			minorLineStyle[i + 1] = normalGap;
		}

		minorLineStyle[minorLineStyle.length - 1] += gridY; // the last one is wider to not draw on the horizontal line

	}

}
