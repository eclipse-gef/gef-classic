/*******************************************************************************
 * Copyright (c) 2026 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d.backgrounds;

import org.eclipse.pde.api.tools.annotations.NoImplement;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;

/**
 * An interface for a special border which can paint both underneath and on top
 * of a Figure. Normal borders only paint on top of a figure and its children. A
 * background has the opportunity to paint both first, and optionally last.
 * <P>
 * WARNING: This interface is not intended to be implemented by clients. Use
 * {@link AbstractBackgroundBorder}.
 *
 * @since 3.23
 */
@NoImplement
public interface BackgroundBorder extends Border {

	/**
	 * Called when this Background should paint. If the background is being painted
	 * inside another border or background, the insets indicate how far inside the
	 * target figure the background should be painted. In most cases, the insets
	 * will be all zero.
	 *
	 * @param figure   The figure on which the background is being painted
	 * @param graphics The graphics
	 * @param insets   Amount to inset from the figure's bounds
	 */
	void paintBackground(IFigure figure, Graphics graphics, Insets insets);

}
