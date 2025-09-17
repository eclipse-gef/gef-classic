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

import java.util.Objects;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;

import org.eclipse.draw2d.geometry.Dimension;

/**
 * Provides miscellaneous Figure operations calculated with the zoom context of
 * the the provided {@code Drawable}.
 *
 * All GC related operations are mirrored from {@code FigureUtilities}
 */
public class DrawableFigureUtilities {
	private final GC gc;
	private Font appliedFont;
	private FontMetrics metrics;

	public DrawableFigureUtilities(Control source) {
		gc = new GC(source);
		source.addDisposeListener(e -> {
			gc.dispose();
		});
		appliedFont = gc.getFont();
	}

	/**
	 * Returns the FontMetrics associated with the passed Font.
	 *
	 * @param f the font
	 * @return the FontMetrics for the given font
	 * @see GC#getFontMetrics()
	 */
	public FontMetrics getFontMetrics(Font f) {
		setFont(f);
		if (metrics == null) {
			metrics = gc.getFontMetrics();
		}
		return metrics;
	}

	/**
	 * Returns the dimensions of the String <i>s</i> using the font <i>f</i>. Tab
	 * expansion and carriage return processing are performed.
	 *
	 * @param s the string
	 * @param f the font
	 * @return the text's dimensions
	 * @see GC#textExtent(String)
	 */
	protected org.eclipse.swt.graphics.Point getTextDimension(String s, Font f) {
		setFont(f);
		return gc.textExtent(s);
	}

	/**
	 * Returns the dimensions of the String <i>s</i> using the font <i>f</i>. No tab
	 * expansion or carriage return processing will be performed.
	 *
	 * @param s the string
	 * @param f the font
	 * @return the string's dimensions
	 * @see GC#stringExtent(java.lang.String)
	 */
	protected org.eclipse.swt.graphics.Point getStringDimension(String s, Font f) {
		setFont(f);
		return gc.stringExtent(s);
	}

	/**
	 * Returns the Dimensions of the given text, converting newlines and tabs
	 * appropriately.
	 *
	 * @param text the text
	 * @param f    the font
	 * @return the dimensions of the given text
	 */
	public Dimension getTextExtents(String text, Font f) {
		return new Dimension(getTextDimension(text, f));
	}

	/**
	 * Returns the Dimensions of <i>s</i> in Font <i>f</i>.
	 *
	 * @param s the string
	 * @param f the font
	 * @return the dimensions of the given string
	 */
	public Dimension getStringExtents(String s, Font f) {
		return new Dimension(getStringDimension(s, f));
	}

	/**
	 * Returns the Dimensions of the given text, converting newlines and tabs
	 * appropriately.
	 *
	 * @param s      the string
	 * @param f      the font
	 * @param result the Dimension that will contain the result of this calculation
	 */
	public void getTextExtents(String s, Font f, Dimension result) {
		org.eclipse.swt.graphics.Point pt = getTextDimension(s, f);
		result.width = pt.x;
		result.height = pt.y;
	}

	/**
	 * Returns the width of <i>s</i> in Font <i>f</i>.
	 *
	 * @param s the string
	 * @param f the font
	 * @return the width
	 */
	public int getTextWidth(String s, Font f) {
		return getTextDimension(s, f).x;
	}

	private void setFont(Font f) {
		if (Objects.equals(appliedFont, f)) {
			return;
		}
		gc.setFont(f);
		appliedFont = f;
		metrics = null;
	}
}
