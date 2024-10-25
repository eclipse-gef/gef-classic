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

/**
 * This interface defines the colors that are used for painting the figures of
 * the {@link PaletteViewer}. Clients may implement this class and assign it to
 * their palette via {@link PaletteViewer#setColorPalette(ColorPalette)} to set
 * their own color theme.
 *
 * <EM>IMPORTANT</EM>: This interface is <EM>not</EM> intended to be implemented
 * by clients. Clients should inherit from {@link DefaultColorPalette}. New
 * methods may be added in the future.
 *
 * @since 3.20
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ColorPalette {
	/**
	 * Gets the color to be used when hovering over palette items. The color may
	 * differ in high contrast mode.
	 *
	 * @return the hover color.
	 */
	Color getSelectedColor();

	/**
	 * Gets the color to be used when selecting palette items. The color may differ
	 * in high contrast mode.
	 *
	 * @return the selected color.
	 */
	Color getHoverColor();
}
