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

package org.eclipse.draw2d;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @since 3.13
 */
public class BasicColorProvider implements ColorProvider {

	@Override
	public Color getButtonLightest() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
	}

	@Override
	public Color getButton() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_WIDGET_BACKGROUND);
	}

	@Override
	public Color getButtonDarker() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
	}

	@Override
	public Color getButtonDarkest() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_WIDGET_DARK_SHADOW);
	}

	@Override
	public Color getListBackground() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_LIST_BACKGROUND);
	}

	@Override
	public Color getListForeground() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_LIST_FOREGROUND);
	}

	@Override
	public Color getLineForeground() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_GRAY);
	}

	@Override
	public Color getMenuBackground() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_WIDGET_BACKGROUND);
	}

	@Override
	public Color getMenuForeground() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_WIDGET_FOREGROUND);
	}

	@Override
	public Color getMenuBackgroundSelected() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_LIST_SELECTION);
	}

	@Override
	public Color getMenuForegroundSelected() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_LIST_SELECTION_TEXT);
	}

	@Override
	public Color getTitleBackground() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_TITLE_BACKGROUND);
	}

	@Override
	public Color getTitleGradient() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
	}

	@Override
	public Color getTitleForeground() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_TITLE_FOREGROUND);
	}

	@Override
	public Color getTitleInactiveForeground() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND);
	}

	@Override
	public Color getTitleInactiveBackground() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND);
	}

	@Override
	public Color getTitleInactiveGradient() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT);
	}

	@Override
	public Color getTooltipForeground() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_INFO_FOREGROUND);
	}

	@Override
	public Color getTooltipBackground() {
		return ColorConstants.SystemColorFactory.getColor(SWT.COLOR_INFO_BACKGROUND);
	}

	@Override
	public Color getListHoverBackgroundColor() {
		return new Color(252, 228, 179);
	}

	@Override
	public Color getListSelectedBackgroundColor() {
		return new Color(207, 227, 250);
	}
}
