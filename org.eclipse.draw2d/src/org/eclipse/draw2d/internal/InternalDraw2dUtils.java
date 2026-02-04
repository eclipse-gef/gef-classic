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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

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
	 * Internal flag for fetching the shell zoom
	 */
	@Deprecated(since = "2026-03", forRemoval = true)
	private static final String DATA_SHELL_ZOOM = "SHELL_ZOOM"; //$NON-NLS-1$

	/**
	 * Data that can be set to scale this widget at 100%.
	 */
	@Deprecated(since = "2026-03", forRemoval = true)
	private static final String DATA_AUTOSCALE_DISABLED = "AUTOSCALE_DISABLED"; //$NON-NLS-1$

	/**
	 * Data that can be set to make a control not propagate autoScale disabling to
	 * children.
	 */
	@Deprecated(since = "2026-03", forRemoval = true)
	private static final String DATA_PROPOGATE_AUTOSCALE_DISABLED = "PROPOGATE_AUTOSCALE_DISABLED"; //$NON-NLS-1$

	private static final boolean enableAutoScale = "win32".equals(SWT.getPlatform()) //$NON-NLS-1$
			&& Boolean.parseBoolean(System.getProperty(DRAW2D_ENABLE_AUTOSCALE, Boolean.TRUE.toString()))
			&& SWT.getVersion() >= 4971; // SWT 2025-12 release or higher

	private static MethodHandle GETSHELLZOOM_HANDLE;
	static {
		try {
			// Introduced with SWT 3.133
			GETSHELLZOOM_HANDLE = MethodHandles.publicLookup().findVirtual(Shell.class, "getZoom", //$NON-NLS-1$
					MethodType.methodType(int.class));
		} catch (IllegalAccessException | NoSuchMethodException e) {
			// ignore
		}
	}

	private static MethodHandle SET_AUTOSCALINGMODE_HANDLE;
	private static Object AUTOSCALING_MODE_DISABLED_INHERITED;
	private static Object AUTOSCALING_MODE_DISABLED;
	static {
		try {
			// Introduced with SWT 3.133
			Class<?> autoscalingModeEnumClass = Class.forName("org.eclipse.swt.graphics.AutoscalingMode"); //$NON-NLS-1$
			if (autoscalingModeEnumClass.isEnum()) {
				AUTOSCALING_MODE_DISABLED_INHERITED = Enum.valueOf((Class<? extends Enum>) autoscalingModeEnumClass,
						"DISABLED_INHERITED");
				assert AUTOSCALING_MODE_DISABLED != null;
				AUTOSCALING_MODE_DISABLED = Enum.valueOf((Class<? extends Enum>) autoscalingModeEnumClass, "DISABLED");
				assert AUTOSCALING_MODE_DISABLED_INHERITED != null;
				MethodType mt = MethodType.methodType(boolean.class, autoscalingModeEnumClass);
				SET_AUTOSCALINGMODE_HANDLE = MethodHandles.publicLookup().findVirtual(Control.class,
						"setAutoscalingMode", //$NON-NLS-1$
						mt);
			}
		} catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
			// ignore
		}
	}

	public static boolean isAutoScaleEnabled() {
		return enableAutoScale;
	}

	public static void configureForAutoscalingMode(Control control, Consumer<Double> zoomConsumer) {
		if (control == null || !isAutoScaleEnabled()) {
			return;
		}

		if (control.isAutoScalable()) {
			// only set the mode, if SWT autoscaling is enabled
			if (SET_AUTOSCALINGMODE_HANDLE != null) {
				try {
					SET_AUTOSCALINGMODE_HANDLE.invoke(control, AUTOSCALING_MODE_DISABLED_INHERITED);
				} catch (Throwable e) {
					throw new SWTException(e.getMessage());
				}
			} else {
				control.setData(InternalDraw2dUtils.DATA_AUTOSCALE_DISABLED, true);
			}
		}
		control.addListener(SWT.ZoomChanged, e -> zoomConsumer.accept(e.detail / 100.0));
		zoomConsumer.accept((double) InternalDraw2dUtils.calculateScale(control));
	}

	public static void setPropagateAutoScaleDisabled(Control control) {
		if (control == null || !isAutoScaleEnabled()) {
			return;
		}
		if (SET_AUTOSCALINGMODE_HANDLE != null) {
			try {
				SET_AUTOSCALINGMODE_HANDLE.invoke(control, AUTOSCALING_MODE_DISABLED);
			} catch (Throwable e) {
				throw new SWTException(e.getMessage());
			}
		} else {
			control.setData(DATA_PROPOGATE_AUTOSCALE_DISABLED, false);
		}
	}

	/**
	 * Returns the zoom of the given control or {@code 1.0}, if the control is
	 * {@code null} or if the zoom can't be determined.
	 *
	 * @return The shell zoom of the given control.
	 */
	public static float calculateScale(Control control) {
		int shellZoom;
		try {
			if (GETSHELLZOOM_HANDLE != null) {
				try {
					shellZoom = (int) GETSHELLZOOM_HANDLE.invoke(control.getShell());
				} catch (Throwable e) {
					throw new SWTException(e.getMessage());
				}
			} else {
				shellZoom = (int) control.getData(InternalDraw2dUtils.DATA_SHELL_ZOOM);
			}
		} catch (NullPointerException e) {
			shellZoom = 100;
		}
		// returning float allows us to round it to int via Math.round(...)
		return shellZoom / 100.0f;
	}
}
