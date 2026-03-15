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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;

import org.eclipse.gef.GEFColorProvider;
import org.eclipse.gef.internal.ui.palette.editparts.PinFigure;

/**
 * Default colors used by the {@link PaletteViewer} which are used when painting
 * the palette figures. Clients may extend this class to define their own
 * colors. The color provider can be set via
 * {@link PaletteViewer#setColorProvider(PaletteColorProvider)}.
 *
 * <em>Important</em> This class is still work-in-progress and new methods might
 * be added in the future.
 *
 * @since 3.20
 */
public class PaletteColorProvider extends GEFColorProvider {
	private static final Color HOVER_COLOR_HICONTRAST = new Color(null, 0, 128, 0);
	private static final Color SELECTED_COLOR_HICONTRAST = new Color(null, 128, 0, 128);
	private final Map<Double, Color> mixedButtonDarker = new HashMap<>();
	private final Map<Double, Color> mixedListBackground = new HashMap<>();
	private Color hotspotColor;

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

	@Override
	public Color getButton() {
		return ColorConstants.button;
	}

	@Override
	public Color getButtonDarker() {
		return ColorConstants.buttonDarker;
	}

	@Override
	public Color getButtonDarkest() {
		return ColorConstants.buttonDarkest;
	}

	@Override
	public Color getListBackground() {
		return ColorConstants.listBackground;
	}

	@Override
	public Color getListForeground() {
		return ColorConstants.listForeground;
	}

	public Color getTitleBackgroundStart() {
		return getListForeground();
	}

	public Color getTitleBackgroundEnd() {
		return getButton();
	}

	/**
	 * Returns the mix of {@link #getButton()} with {@link #getButtonDarker()} with
	 * weight {@code weight}. This weight must be within the interval [0, 1].
	 *
	 * The mixed color is only calculated once and then cached. It is therefore
	 * recommended to only call this method with constants, to avoid rounding errors
	 * due to floating point arithmetic.
	 *
	 * @throws IllegalArgumentException if {@code weight} is outside the valid
	 *                                  range.
	 */
	public final Color getButtonDarker(double weight) {
		if (weight < 0 || weight > 1) {
			throw new IllegalArgumentException("The weight %f must be within [0, 1]".formatted(weight)); //$NON-NLS-1$
		}
		return mixedButtonDarker.computeIfAbsent(weight,
				ignore -> FigureUtilities.mixColors(getButton(), getButtonDarker(), weight));
	}

	/**
	 * Returns the mix of {@link #getButton()} with {@link #getListBackground()}
	 * with weight {@code weight}. This weight must be within the interval [0, 1].
	 *
	 * The mixed color is only calculated once and then cached. It is therefore
	 * recommended to only call this method with constants, to avoid rounding errors
	 * due to floating point arithmetic.
	 *
	 * @throws IllegalArgumentException if {@code weight} is outside the valid
	 *                                  range.
	 * @since 3.21
	 */
	public final Color getListBackground(double weight) {
		if (weight < 0 || weight > 1) {
			throw new IllegalArgumentException("The weight %f must be within [0, 1]".formatted(weight)); //$NON-NLS-1$
		}
		return mixedListBackground.computeIfAbsent(weight,
				ignore -> FigureUtilities.mixColors(getButton(), getListBackground(), weight));
	}

	/**
	 * Returns the foreground color of the {@link PinFigure} when the cursor is over
	 * the button.
	 */
	public final Color getHotspotColor() {
		if (hotspotColor == null) {
			hotspotColor = FigureUtilities.mixColors(getListBackground(), getButtonDarker(), 0.60);
		}
		return hotspotColor;
	}
}
