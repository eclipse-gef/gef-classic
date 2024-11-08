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

import org.eclipse.gef.ui.palette.PaletteColorProvider;

/**
 * Defines arbitrary colors that distinguish themselves from the default
 * palette.
 */
public class ShapesColorProvider extends PaletteColorProvider {
	@Override
	public Color getListSelectedBackgroundColor() {
		return ColorConstants.darkGreen;
	}

	@Override
	public Color getListHoverBackgroundColor() {
		return ColorConstants.cyan;
	}

	@Override
	public Color getButton() {
		return ColorConstants.lightGray;
	}

	@Override
	public Color getButtonDarker() {
		return ColorConstants.gray;
	}

	@Override
	public Color getButtonDarkest() {
		return ColorConstants.darkGray;
	}
}
