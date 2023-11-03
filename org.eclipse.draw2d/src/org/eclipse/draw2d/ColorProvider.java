/*******************************************************************************
 * Copyright (c) 2000, 2022 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Georgii Gvinepadze - georgii.gvinepadze@dbeaver.com
 *     Serge Rider - serge@dbeaver.com
 *******************************************************************************/
package org.eclipse.draw2d;

import org.eclipse.swt.graphics.Color;

/**
 * A collection of color-related constants.
 *
 * @since 3.13
 */
public interface ColorProvider {

	Color getButtonLightest();

	Color getButton();

	Color getButtonDarker();

	Color getButtonDarkest();

	Color getListBackground();

	Color getListForeground();

	Color getLineForeground();

	Color getMenuBackground();

	Color getMenuForeground();

	Color getMenuBackgroundSelected();

	Color getMenuForegroundSelected();

	Color getTitleBackground();

	Color getTitleGradient();

	Color getTitleForeground();

	Color getTitleInactiveForeground();

	Color getTitleInactiveBackground();

	Color getTitleInactiveGradient();

	Color getTooltipForeground();

	Color getTooltipBackground();

	Color getListHoverBackgroundColor();

	Color getListSelectedBackgroundColor();

	class SystemColorFactory {
		static ColorProvider colorProvider = new BasicColorProvider();

		public static ColorProvider getColorProvider() {
			return colorProvider;
		}

		public static void setColorProvider(ColorProvider colorProvider) {
			SystemColorFactory.colorProvider = colorProvider;
		}
	}
}
