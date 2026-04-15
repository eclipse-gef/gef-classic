/*******************************************************************************
 * Copyright (c) 2000, 2023 IBM Corporation and others.
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
package org.eclipse.draw2d;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * A collection of color-related constants.
 *
 * @since 3.13
 */
public interface ColorConstants {

	/**
	 * This utility class is used to create a system {@link Color} using any of the
	 * supported {@link SWT} constants. The {@link Color} is created using the
	 * {@link Display} bound to the current thread.
	 *
	 * @since 3.13
	 */
	class SystemColorFactory {
		/**
		 * Returns the matching standard {@link Color} for the given constant, which
		 * should be one of the color constants specified in class {@link SWT}. Any
		 * value other than one of the {@link SWT} color constants which is passed in
		 * will result in the {@link Color} black. This {@link Color} should not be
		 * free'd because it was allocated by the system, not the application. Note:
		 * This method should be invoked from within the UI thread if possible, as it
		 * will attempt to create a new Display instance, if not!
		 *
		 * @since 3.13
		 * @param which the {@link SWT} {@link Color} constant
		 * @return the corresponding {@link Color} or the {@link Color} {@code black}
		 */
		public static Color getColor(final int which) {
			Display display = Display.getCurrent();
			if (display != null) {
				return display.getSystemColor(which);
			}
			display = Display.getDefault();
			return display.syncCall(() -> Display.getCurrent().getSystemColor(which));
		}
	}

	/**
	 * @see SWT#COLOR_WIDGET_HIGHLIGHT_SHADOW
	 */
	Color buttonLightest = ColorProvider.SystemColorFactory.colorProvider.getButtonLightest();
	/**
	 * @see SWT#COLOR_WIDGET_BACKGROUND
	 */
	Color button = ColorProvider.SystemColorFactory.colorProvider.getButton();
	/**
	 * @see SWT#COLOR_WIDGET_NORMAL_SHADOW
	 */
	Color buttonDarker = ColorProvider.SystemColorFactory.colorProvider.getButtonDarker();
	/**
	 * @see SWT#COLOR_WIDGET_DARK_SHADOW
	 */
	Color buttonDarkest = ColorProvider.SystemColorFactory.colorProvider.getButtonDarkest();

	/**
	 * @see SWT#COLOR_LIST_BACKGROUND
	 */
	Color listBackground = ColorProvider.SystemColorFactory.colorProvider.getListBackground();
	/**
	 * @see SWT#COLOR_LIST_FOREGROUND
	 * @since 3.13
	 */
	Color listForeground = ColorProvider.SystemColorFactory.colorProvider.getListForeground();

	/**
	 * @since 3.13
	 */
	Color lineForeground = ColorProvider.SystemColorFactory.colorProvider.getLineForeground();

	/**
	 * @see SWT#COLOR_WIDGET_BACKGROUND
	 */
	Color menuBackground = ColorProvider.SystemColorFactory.colorProvider.getMenuBackground();
	/**
	 * @see SWT#COLOR_WIDGET_FOREGROUND
	 */
	Color menuForeground = ColorProvider.SystemColorFactory.colorProvider.getMenuForeground();
	/**
	 * @see SWT#COLOR_LIST_SELECTION
	 */
	Color menuBackgroundSelected = ColorProvider.SystemColorFactory.colorProvider.getMenuBackgroundSelected();
	/**
	 * @see SWT#COLOR_LIST_SELECTION_TEXT
	 */
	Color menuForegroundSelected = ColorProvider.SystemColorFactory.colorProvider.getMenuForegroundSelected();

	/**
	 * @see SWT#COLOR_TITLE_BACKGROUND
	 */
	Color titleBackground = ColorProvider.SystemColorFactory.colorProvider.getTitleBackground();
	/**
	 * @see SWT#COLOR_TITLE_BACKGROUND_GRADIENT
	 */
	Color titleGradient = ColorProvider.SystemColorFactory.colorProvider.getTitleGradient();
	/**
	 * @see SWT#COLOR_TITLE_FOREGROUND
	 */
	Color titleForeground = ColorProvider.SystemColorFactory.colorProvider.getTitleForeground();
	/**
	 * @see SWT#COLOR_TITLE_INACTIVE_FOREGROUND
	 */
	Color titleInactiveForeground = ColorProvider.SystemColorFactory.colorProvider.getTitleInactiveForeground();
	/**
	 * @see SWT#COLOR_TITLE_INACTIVE_BACKGROUND
	 */
	Color titleInactiveBackground = ColorProvider.SystemColorFactory.colorProvider.getTitleInactiveBackground();
	/**
	 * @see SWT#COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT
	 */
	Color titleInactiveGradient = ColorProvider.SystemColorFactory.colorProvider.getTitleInactiveGradient();

	/**
	 * @see SWT#COLOR_INFO_FOREGROUND
	 */
	Color tooltipForeground = ColorProvider.SystemColorFactory.colorProvider.getTooltipForeground();
	/**
	 * @see SWT#COLOR_INFO_BACKGROUND
	 * @since 3.13
	 */
	Color tooltipBackground = ColorProvider.SystemColorFactory.colorProvider.getTooltipBackground();

	/**
	 * @since 3.13
	 */
	Color listHoverBackgroundColor = ColorProvider.SystemColorFactory.colorProvider.getListHoverBackgroundColor();

	/**
	 * @since 3.13
	 */
	Color listSelectedBackgroundColor = ColorProvider.SystemColorFactory.colorProvider.getListSelectedBackgroundColor();
	/*
	 * Misc. colors
	 */
	/** One of the pre-defined colors */
	Color white = new Color(255, 255, 255);
	/** One of the pre-defined colors */
	Color lightGray = new Color(192, 192, 192);
	/** One of the pre-defined colors */
	Color gray = new Color(128, 128, 128);
	/** One of the pre-defined colors */
	Color darkGray = new Color(64, 64, 64);
	/** One of the pre-defined colors */
	Color black = new Color(0, 0, 0);
	/** One of the pre-defined colors */
	Color red = new Color(255, 0, 0);
	/** One of the pre-defined colors */
	Color orange = new Color(255, 196, 0);
	/** One of the pre-defined colors */
	Color yellow = new Color(255, 255, 0);
	/** One of the pre-defined colors */
	Color green = new Color(0, 255, 0);
	/** One of the pre-defined colors */
	Color lightGreen = new Color(96, 255, 96);
	/** One of the pre-defined colors */
	Color darkGreen = new Color(0, 127, 0);
	/** One of the pre-defined colors */
	Color cyan = new Color(0, 255, 255);
	/** One of the pre-defined colors */
	Color lightBlue = new Color(127, 127, 255);
	/** One of the pre-defined colors */
	Color blue = new Color(0, 0, 255);
	/** One of the pre-defined colors */
	Color darkBlue = new Color(0, 0, 127);

}
