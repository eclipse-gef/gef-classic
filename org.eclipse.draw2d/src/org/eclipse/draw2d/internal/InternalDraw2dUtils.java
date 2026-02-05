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
	 * System property that controls the enabled of the Draw2D autoScale
	 * functionality (replacing the native SWT autoScale functionality).
	 * <p>
	 * Currently it only has effects when executed on Windows.
	 * </p>
	 *
	 * <ul>
	 * <li><b>true</b>: autoScale functionality is enabled</li>
	 * <li><b>false</b>: autoScale functionality is disabled<</li>
	 *
	 * </ul>
	 * The current default is "true".
	 */
	private static final String DRAW2D_ENABLE_AUTOSCALE = "draw2d.enableAutoscale"; //$NON-NLS-1$

	/**
	 * System property that controls if ScaledGraphcis are supposed to be used
	 * instead of SWTGraphics by default.
	 * <p>
	 * The current default is "false".
	 */
	private static final String DRAW2D_SCALEDGRAPHICS_BY_DEFAULT = "draw2d.useScaledGraphicsByDefault"; //$NON-NLS-1$

	private static final boolean enableAutoScale = "win32".equals(SWT.getPlatform()) //$NON-NLS-1$
			&& Boolean.parseBoolean(System.getProperty(DRAW2D_ENABLE_AUTOSCALE, Boolean.TRUE.toString()))
			&& SWT.getVersion() >= 4971; // SWT 2025-12 release or higher

	private static final boolean useScaledGraphicsByDefault = Boolean
			.parseBoolean(System.getProperty(DRAW2D_SCALEDGRAPHICS_BY_DEFAULT, Boolean.FALSE.toString()));

	public static boolean isAutoScaleEnabled() {
		return enableAutoScale;
	}

	public static boolean useScaledGraphicsByDefault() {
		return useScaledGraphicsByDefault;
	}

	public static void configureForAutoscalingMode(Control control, Consumer<Double> zoomConsumer) {
		if (control == null || !isAutoScaleEnabled()) {
			return;
		}
		AutoscalingAccess.setAutoscaleDisabled(control);

		control.addListener(SWT.ZoomChanged, e -> zoomConsumer.accept(e.detail / 100.0));
		zoomConsumer.accept((double) InternalDraw2dUtils.calculateScale(control));
	}

	public static void setPropagateAutoScaleDisabled(Control control) {
		if (control == null || !isAutoScaleEnabled()) {
			return;
		}
		AutoscalingAccess.disablePropagateAutoscale(control);
	}

	/**
	 * Returns the zoom of the given control or {@code 1.0}, if the control is
	 * {@code null} or if the zoom can't be determined.
	 *
	 * @return The shell zoom of the given control.
	 */
	public static float calculateScale(Control control) {
		int shellZoom = AutoscalingAccess.getShellZoom(control);
		// returning float allows us to round it to int via Math.round(...)
		return shellZoom / 100.0f;
	}
}
