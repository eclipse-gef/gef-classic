/*******************************************************************************
 * Copyright 2005, 2026, CHISEL Group, University of Victoria, Victoria,
 *                       BC, Canada and others.
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
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.backgrounds.shadows.RectangleDropShadowBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Overrides the Draw2D Label Figure class to ensure that the text is never
 * truncated. Also draws a rounded rectangle border.
 *
 * @author Chris Callendar
 */
public class GraphLabel extends CachedLabel {

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
		this.setLayoutManager(new StackLayout());
		this.setOpaque(true);
		RectangleDropShadowBorder border = new RectangleDropShadowBorder(8);
		border.setShadowColor(ColorConstants.black);
		border.setHaloSize(6);
		border.setSoftness(4.5);
		this.setBorder(border);
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
				minSize.expand(10, 4);
				setBounds(new Rectangle(getLocation(), minSize));
			}
		}
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

	public void setArcWidth(int arcWidth) {
		((RectangleDropShadowBorder) getBorder()).setDropShadowSize(arcWidth);
	}
}
