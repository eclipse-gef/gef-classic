/*******************************************************************************
 * Copyright (c) 2024 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/

package org.eclipse.gef.examples.shapes.palette;

import org.eclipse.swt.graphics.Color;

import org.eclipse.draw2d.ColorConstants;

import org.eclipse.gef.ui.palette.DefaultColorPalette;

/**
 * Defines arbitrary colors that distinguish themselves from the default
 * palette.
 */
public class ShapesColorPalette extends DefaultColorPalette {
	@Override
	public Color getSelectedColor() {
		return ColorConstants.darkGreen;
	}

	@Override
	public Color getHoverColor() {
		return ColorConstants.cyan;
	}
}
