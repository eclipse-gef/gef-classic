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
	private static final int DEFAULT_LINE_WIDTH = 1;
	private static final int DEFAULT_MAJOR_ALPHA = 30;
	private static final int DEFAULT_MINOR_ALPHA = 70;

	private int majorInterleave = DEFAULT_MAJOR_INTERLEAVE;
	private int lineWidth = DEFAULT_LINE_WIDTH;
	private int majorAlpha = DEFAULT_MAJOR_ALPHA;
	private int minorAlpha = DEFAULT_MINOR_ALPHA;
	private float[] minorLineStyle;

	public HierarchicalGridLayer() {
		updateMinorLineStyle();
		setForegroundColor(null); // we want the default foreground color
	}

	@Override
	public void setSpacing(final Dimension spacing) {
		super.setSpacing(spacing);
		updateMinorLineStyle();
		repaint();
	}

	public void setMajorInterleave(int majorInterleave) {
		this.majorInterleave = majorInterleave;
		updateMinorLineStyle();
		repaint();
	}

	/**
	 * Sets the line widths of the grid to the passed value.
	 *
	 * @param lineWidth the new lineWidth
	 * @since 3.27
	 */
	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
		updateMinorLineStyle();
		repaint();
	}

	/**
	 * Set the alpha value of the major grid
	 *
	 * @param alpha
	 * @since 3.28
	 */
	public void setMajorAlpha(int alpha) {
		majorAlpha = alpha;
		repaint();
	}

	/**
	 * Set the alpha value of the minor grid
	 *
	 * @param alpha
	 * @since 3.28
	 */
	public void setMinorAlpha(int alpha) {
		minorAlpha = alpha;
		repaint();
	}

	@Override
	protected void paintGrid(final Graphics g) {
		final int origLineStyle = g.getLineStyle();
		final int alpha = g.getAlpha();
		g.setLineWidth(lineWidth);

		final Rectangle clip = g.getClip(Rectangle.SINGLETON);

		if (minorLineStyle != null) {
			g.setLineDash(minorLineStyle);
			g.setAlpha(minorAlpha);
			drawMinorGrid(g, clip);
		}

		// clear line dash switch back to solid lines for major grid lines
		g.setLineDash((float[]) null);
		g.setAlpha(majorAlpha);
		drawMajorGrid(g, clip);

		g.setAlpha(alpha);
		g.setLineStyle(origLineStyle);
	}

	private void drawMinorGrid(final Graphics g, final Rectangle clip) {
		if (g.getAbsoluteScale() * lineWidth < 0.7) {
			// for small grids do not draw the minor grid to reduce clutter
			return;
		}

		final int majorInterleaveX = gridX * majorInterleave;
		final int startX = clip.x - Math.floorMod(clip.x, gridX);
		// in order to respect our line pattern we always need to start with the first
		// minor grid dot after a major horizontal line
		final int startY = clip.y - Math.floorMod(clip.y, gridY * majorInterleave) + gridY;

		for (int x = startX; x <= clip.right(); x += gridY) {
			if (x % majorInterleaveX != 0) {
				g.drawLine(x, startY, x, clip.bottom());
			}
		}
	}

	private void drawMajorGrid(final Graphics g, final Rectangle clip) {
		final int majorInterleaveX = gridX * majorInterleave;

		if (majorInterleaveX * g.getAbsoluteScale() > MIN_ABSOLUTE_INTERLEAVE) {
			final int startX = clip.x - Math.floorMod(clip.x, majorInterleaveX);

			for (int x = startX; x <= clip.right(); x += majorInterleaveX) {
				g.drawLine(x, clip.y, x, clip.bottom());
			}
		}

		final int majorInterleaveY = gridY * majorInterleave;
		if (majorInterleaveY * g.getAbsoluteScale() > MIN_ABSOLUTE_INTERLEAVE) {
			final int startY = clip.y - Math.floorMod(clip.y, majorInterleaveY);

			for (int y = startY; y <= clip.bottom(); y += majorInterleaveY) {
				g.drawLine(clip.x, y, clip.right(), y);
			}
		}
	}

	private void updateMinorLineStyle() {
		if (lineWidth <= 0 || gridY - lineWidth <= 0) {
			minorLineStyle = null;
			return;
		}
		final int normalGap = gridY - lineWidth;
		minorLineStyle = new float[(majorInterleave - 1) * 2];

		for (int i = 0; i < (majorInterleave - 1) * 2; i += 2) {
			minorLineStyle[i] = lineWidth;
			minorLineStyle[i + 1] = normalGap;
		}
		minorLineStyle[minorLineStyle.length - 1] += gridY; // the last one is wider to not draw on the horizontal line
	}

}
