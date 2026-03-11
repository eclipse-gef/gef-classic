/*******************************************************************************
 * Copyright (c) 2000, 2026 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Alois Zoitl     - copied from CompoundBorder and adjusted for the needs
 *                       of an AbstractBackground
 *******************************************************************************/
package org.eclipse.draw2d.backgrounds;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;

/**
 * CompoundBackground allows for the nesting of two borders. The nested borders
 * are referred to as the <i>inner</i> and <i>outer</i> borders.
 *
 * If a given border is an {@link BackgroundBorder} it is drawn as part of the
 * background drawing process of a figure. This is, for example, needed for
 * combining drop shadows with other borders.
 *
 * @since 3.23
 */
public class CompoundBackground extends CompoundBorder implements BackgroundBorder {

	/**
	 * Constructs a default CompoundBackground with no borders under it.
	 */
	public CompoundBackground() {
	}

	/**
	 * Constructs a CompoundBackground with the two borders specified as input.
	 *
	 * @param outer Border which is drawn on the outside
	 * @param inner Border which is drawn inside the outer border
	 *
	 * @since 2.0
	 */
	public CompoundBackground(final Border outer, final Border inner) {
		super(outer, inner);
	}

	@Override
	public void paintBackground(final IFigure figure, final Graphics g, Insets insets) {
		if (outer instanceof final BackgroundBorder outerBg) {
			g.pushState();
			outerBg.paintBackground(figure, g, insets);
			g.popState();
		}
		if (outer != null) {
			insets = insets.getAdded(outer.getInsets(figure));
		}
		if (inner instanceof final BackgroundBorder innerBg) {
			innerBg.paintBackground(figure, g, insets);
		}
	}
}
