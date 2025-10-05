/*******************************************************************************
 * Copyright (c) 2025 Yatta and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Yatta - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.internal;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

public class InternalDraw2dUtils {
	/**
	 * System property that controls the disablement of any autoScale functionality.
	 * Currently it only has effects when executed on Windows.
	 *
	 * <ul>
	 * <li><b>false</b>: autoScale functionality is enabled</li>
	 * <li><b>true</b>: autoScale functionality is disabled<</li>
	 *
	 * </ul>
	 * The current default is "false".
	 */
	private static final String DRAW2D_DISABLE_AUTOSCALE = "draw2d.disableAutoscale"; //$NON-NLS-1$

	/**
	 * Internal flag for fetching the shell zoom
	 */
	private static final String DATA_SHELL_ZOOM = "SHELL_ZOOM"; //$NON-NLS-1$

	/**
	 * Data that can be set to scale this widget at 100%.
	 */
	private static final String DATA_AUTOSCALE_DISABLED = "AUTOSCALE_DISABLED"; //$NON-NLS-1$

	public static boolean disableAutoscale;

	static {
		disableAutoscale = "win32".equals(SWT.getPlatform()) //$NON-NLS-1$
				&& Boolean.parseBoolean(System.getProperty(DRAW2D_DISABLE_AUTOSCALE));
	}

	public static void configureForAutoscalingMode(Control control, Consumer<Double> zoomConsumer) {
		if (control == null || !disableAutoscale) {
			return;
		}
		control.setData(InternalDraw2dUtils.DATA_AUTOSCALE_DISABLED, true);
		control.addListener(SWT.ZoomChanged, e -> zoomConsumer.accept(e.detail / 100.0));
		zoomConsumer.accept(InternalDraw2dUtils.calculateScale(control));
	}

	private static double calculateScale(Control control) {
		int shellZoom;
		try {
			shellZoom = (int) control.getData(InternalDraw2dUtils.DATA_SHELL_ZOOM);
		} catch (ClassCastException | NullPointerException e) {
			shellZoom = 100;
		}
		return shellZoom / 100.0;
	}
}
