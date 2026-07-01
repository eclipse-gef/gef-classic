/*******************************************************************************
 * Copyright (c) 2000, 2023 DBeaver Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Georgii Gvinepadze - georgii.gvinepadze@dbeaver.com initial API and implementation
 *     Serge Rider - serge@dbeaver.com initial API and implementation
 *******************************************************************************/
package org.eclipse.gef;

import org.eclipse.swt.graphics.Color;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.IThemeManager;

import org.eclipse.draw2d.ColorProvider;

/**
 * A collection of color-related constants.
 *
 * @since 3.14
 */
public class GEFColorProvider implements ColorProvider {

	private final IThemeManager themeManager;

	public GEFColorProvider() {
		themeManager = PlatformUI.getWorkbench().getThemeManager();
	}

	@Override
	public Color getButtonLightest() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_BUTTON_LIGHTEST);
	}

	@Override
	public Color getButton() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_BUTTON);
	}

	@Override
	public Color getButtonDarker() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_BUTTON_DARKER);
	}

	@Override
	public Color getButtonDarkest() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_BUTTON_DARKEST);
	}

	@Override
	public Color getListBackground() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_LIST_BACKGROUND);
	}

	@Override
	public Color getListForeground() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_LIST_FOREGROUND);
	}

	@Override
	public Color getLineForeground() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_LINE_FOREGROUND);
	}

	@Override
	public Color getMenuBackground() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_MENU_BACKGROUND);
	}

	@Override
	public Color getMenuForeground() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_MENU_FOREGROUND);
	}

	@Override
	public Color getMenuBackgroundSelected() {
		return themeManager.getCurrentTheme().getColorRegistry()
				.get(ThemeConstants.CONFIG_COLOR_MENU_BACKGROUND_SELECTED);
	}

	@Override
	public Color getMenuForegroundSelected() {
		return themeManager.getCurrentTheme().getColorRegistry()
				.get(ThemeConstants.CONFIG_COLOR_MENU_FOREGROUND_SELECTED);
	}

	@Override
	public Color getShadowColor() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_SHADOW);
	}

	@Override
	public Color getTitleBackground() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_TITLE_BACKGROUND);
	}

	@Override
	public Color getTitleGradient() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_TITLE_GRADIENT);
	}

	@Override
	public Color getTitleForeground() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_TITLE_FOREGROUND);
	}

	@Override
	public Color getTitleInactiveForeground() {
		return themeManager.getCurrentTheme().getColorRegistry()
				.get(ThemeConstants.CONFIG_COLOR_TITLE_INACTIVE_FOREGROUND);
	}

	@Override
	public Color getTitleInactiveBackground() {
		return themeManager.getCurrentTheme().getColorRegistry()
				.get(ThemeConstants.CONFIG_COLOR_TITLE_INACTIVE_BACKGROUND);
	}

	@Override
	public Color getTitleInactiveGradient() {
		return themeManager.getCurrentTheme().getColorRegistry()
				.get(ThemeConstants.CONFIG_COLOR_TITLE_INACTIVE_GRADIENT);
	}

	@Override
	public Color getTooltipForeground() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_TOOLTIP_FOREGROUND);
	}

	@Override
	public Color getTooltipBackground() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_TOOLTIP_BACKGROUND);
	}

	@Override
	public Color getListHoverBackgroundColor() {
		return themeManager.getCurrentTheme().getColorRegistry().get(ThemeConstants.CONFIG_COLOR_LIST_HOVER_BACKGROUND);
	}

	@Override
	public Color getListSelectedBackgroundColor() {
		return themeManager.getCurrentTheme().getColorRegistry()
				.get(ThemeConstants.CONFIG_COLOR_LIST_SELECTED_BACKGROUND);
	}

}
