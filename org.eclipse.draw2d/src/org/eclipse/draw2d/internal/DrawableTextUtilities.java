/*******************************************************************************
 * Copyright (c) 2025 Yatta and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Yatta - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d.internal;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.widgets.Control;

import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * Provides miscellaneous text operations calculated with the zoom context of
 * the the provided {@code Drawable}.
 */
public class DrawableTextUtilities extends TextUtilities {

	private final DrawableFigureUtilities figureUtilities;

	public DrawableTextUtilities(Control source) {
		figureUtilities = new DrawableFigureUtilities(source);
	}

	/**
	 * Returns the Dimensions of <i>s</i> in Font <i>f</i>.
	 *
	 * @param s the string
	 * @param f the font
	 * @return the dimensions of the given string
	 */
	@Override
	public Dimension getStringExtents(String s, Font f) {
		return figureUtilities.getStringExtents(s, f);
	}

	/**
	 * Returns the Dimensions of the given text, converting newlines and tabs
	 * appropriately.
	 *
	 * @param s the text
	 * @param f the font
	 * @return the dimensions of the given text
	 */
	@Override
	public Dimension getTextExtents(String s, Font f) {
		return figureUtilities.getTextExtents(s, f);
	}

	/**
	 * Gets the font's ascent.
	 *
	 * @param font
	 * @return the font's ascent
	 */
	@Override
	public int getAscent(Font font) {
		FontMetrics fm = figureUtilities.getFontMetrics(font);
		return fm.getHeight() - fm.getDescent();
	}

	/**
	 * Gets the font's descent.
	 *
	 * @param font
	 * @return the font's descent
	 */
	@Override
	public int getDescent(Font font) {
		return figureUtilities.getFontMetrics(font).getDescent();
	}
}
