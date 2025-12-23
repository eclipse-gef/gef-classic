/*******************************************************************************
 * Copyright 2005-2010, 2025 CHISEL Group, University of Victoria, Victoria,
 *                           BC, Canada.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 ******************************************************************************/
package org.eclipse.zest.core.widgets.internal;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import org.eclipse.zest.core.widgets.IStyleableFigure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Overrides the Draw2D Label Figure class to ensure that the text is never
 * truncated. Also draws a rounded rectangle border.
 *
 * @author Chris Callendar
 */
public class GraphLabel extends CachedLabel implements IStyleableFigure {

	private Color borderColor;
	private int borderWidth;
	private int arcWidth;

	private final boolean painting = false;

	/**
	 * Creates a GraphLabel
	 *
	 * @param cacheLabel Determine if the text should be cached. This will make it
	 *                   faster, but the text is not as clear
	 */
	public GraphLabel(boolean cacheLabel) {
		this("", cacheLabel); //$NON-NLS-1$
	}

	/**
	 * Creates a graph label with text
	 *
	 * @param text       The text
	 * @param cacheLabel Determine if the text should be cached. This will make it
	 *                   faster, but the
	 */
	public GraphLabel(String text, boolean cacheLabel) {
		this(text, null, cacheLabel);
	}

	/**
	 * Creates the graph label with an image
	 *
	 * @param i          The Image
	 * @param cacheLabel Determine if the text should be cached. This will make it
	 *                   faster, but the
	 */
	public GraphLabel(Image i, boolean cacheLabel) {
		this("", i, cacheLabel); //$NON-NLS-1$
	}

	/**
	 * Creates a graph label with an image and text
	 *
	 * @param text       The text
	 * @param i          The Image
	 * @param cacheLabel Determine if the text should be cached. This will make it
	 *                   faster, but the
	 */
	public GraphLabel(String text, Image i, boolean cacheLabel) {
		super(cacheLabel);
		initLabel();
		setText(text);
		setIcon(i);
		adjustBoundsToFit();
	}

	/**
	 * Initialises the border colour, border width, and sets the layout manager.
	 * Also sets the font to be the default system font.
	 */
	protected void initLabel() {
		super.setFont(Display.getDefault().getSystemFont());
		this.borderColor = ColorConstants.black;
		this.borderWidth = 0;
		this.arcWidth = 8;
		this.setLayoutManager(new StackLayout());
		this.setBorder(new MarginBorder(1));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.draw2d.Figure#setFont(org.eclipse.swt.graphics.Font)
	 */
	@Override
	public void setFont(Font f) {
		super.setFont(f);
		adjustBoundsToFit();
	}

	/**
	 * Adjust the bounds to make the text fit without truncation.
	 */
	protected void adjustBoundsToFit() {
		String text = getText();
		if ((text != null)) {
			Font font = getFont();
			if (font != null) {
				Dimension minSize = FigureUtilities.getTextExtents(text, font);
				if (getIcon() != null) {
					org.eclipse.swt.graphics.Rectangle imageRect = getIcon().getBounds();
					int expandHeight = Math.max(imageRect.height - minSize.height, 0);
					minSize.expand(imageRect.width + 4, expandHeight);
				}
				minSize.expand(10 + (2 * borderWidth), 4 + (2 * borderWidth));
				setBounds(new Rectangle(getLocation(), minSize));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	@Override
	public void paint(Graphics graphics) {
		int blue = getBackgroundColor().getBlue();
		blue = (int) (blue - (blue * 0.20));
		blue = blue > 0 ? blue : 0;

		int red = getBackgroundColor().getRed();
		red = (int) (red - (red * 0.20));
		red = red > 0 ? red : 0;

		int green = getBackgroundColor().getGreen();
		green = (int) (green - (green * 0.20));
		green = green > 0 ? green : 0;

		Color lightenColor = new Color(Display.getCurrent(), new RGB(red, green, blue));
		graphics.setForegroundColor(lightenColor);
		graphics.setBackgroundColor(getBackgroundColor());

		graphics.pushState();

		Rectangle bounds = getBounds().getCopy();
		bounds.shrink(1, 1);

		Path p = new Path(Display.getCurrent());
		p.addArc(bounds.left(), bounds.top(), arcWidth, arcWidth, 90, 90);
		p.addArc(bounds.left(), bounds.bottom() - arcWidth, arcWidth, arcWidth, 180, 90);
		p.addArc(bounds.right() - arcWidth, bounds.bottom() - arcWidth, arcWidth, arcWidth, 270, 90);
		p.addArc(bounds.right() - arcWidth, bounds.top(), arcWidth, arcWidth, 0, 90);
		graphics.clipPath(p);

		graphics.setBackgroundColor(lightenColor);
		graphics.setForegroundColor(getBackgroundColor());
		graphics.fillGradient(bounds, true);

		// Paint the border
		if (borderWidth > 0) {
			graphics.setClip(getBounds());
			graphics.setForegroundColor(borderColor);
			graphics.setBackgroundColor(borderColor);
			graphics.setLineWidth(borderWidth);
			graphics.drawRoundRectangle(bounds, arcWidth, arcWidth);
		}

		graphics.restoreState();

		p.dispose();

		super.paint(graphics);

		graphics.popState();

		lightenColor.dispose();
	}

	@Override
	protected Color getBackgroundTextColor() {
		return getBackgroundColor();
	}

	/**
	 * This method is overridden to ensure that it doesn't get called while the
	 * super.paintFigure() is being called. Otherwise NullPointerExceptions can
	 * occur because the icon or text locations are cleared *after* they were
	 * calculated.
	 *
	 * @see org.eclipse.draw2d.Label#invalidate()
	 */
	@Override
	public void invalidate() {
		if (!painting) {
			super.invalidate();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.draw2d.Label#setText(java.lang.String)
	 */
	@Override
	public void setText(String s) {
		super.setText(s);
		adjustBoundsToFit();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.draw2d.Label#setIcon(org.eclipse.swt.graphics.Image)
	 */
	@Override
	public void setIcon(Image image) {
		super.setIcon(image);
		adjustBoundsToFit();
	}

	public Color getBorderColor() {
		return borderColor;
	}

	@Override
	public void setBorderColor(Color c) {
		this.borderColor = c;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	@Override
	public void setBorderWidth(int width) {
		this.borderWidth = width;
	}

	public int getArcWidth() {
		return arcWidth;
	}

	public void setArcWidth(int arcWidth) {
		this.arcWidth = arcWidth;
	}
}
