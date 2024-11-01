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

package org.eclipse.gef.ui.palette;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.ColorConstants;

import org.eclipse.gef.GEFColorProvider;

/**
 * Default colors used by the {@link PaletteViewer} which are used when painting
 * the palette figures. Clients may extend this class to define their own
 * colors. The color provider can be set via
 * {@link PaletteViewer#setColorProvider(PaletteColorProvider)}.
 *
 * @since 3.20
 */
public class PaletteColorProvider extends GEFColorProvider {
	private static final Color HOVER_COLOR_HICONTRAST = new Color(null, 0, 128, 0);
	private static final Color SELECTED_COLOR_HICONTRAST = new Color(null, 128, 0, 128);

	public static final PaletteColorProvider INSTANCE = new PaletteColorProvider();

	protected PaletteColorProvider() {
		// This class should never be instantiated directly but instead, the singleton
		// should be used. It may however be extended by clients to define their own
		// colors.
	}

	@Override
	public Color getListSelectedBackgroundColor() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		if (display.getHighContrast()) {
			return SELECTED_COLOR_HICONTRAST;
		}
		return ColorConstants.listSelectedBackgroundColor;
	}

	@Override
	public Color getListHoverBackgroundColor() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		if (display.getHighContrast()) {
			return HOVER_COLOR_HICONTRAST;
		}
		return ColorConstants.listHoverBackgroundColor;
	}
}
