/*******************************************************************************
 * Copyright (c) 2026 Yatta Solution and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.draw2d.internal;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("unchecked")
class AutoscalingAccess {

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

	private static final Set<Control> propagationDisabledControls = new HashSet<>();

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
			@SuppressWarnings("rawtypes")
			Class<? extends Enum> autoscalingModeEnumClass = Class.forName("org.eclipse.swt.graphics.AutoscalingMode") //$NON-NLS-1$
					.asSubclass(Enum.class);
			Objects.requireNonNull(autoscalingModeEnumClass);
			AUTOSCALING_MODE_DISABLED_INHERITED = Enum.valueOf(autoscalingModeEnumClass, "DISABLED_INHERITED"); //$NON-NLS-1$
			Objects.requireNonNull(AUTOSCALING_MODE_DISABLED_INHERITED);
			AUTOSCALING_MODE_DISABLED = Enum.valueOf(autoscalingModeEnumClass, "DISABLED"); //$NON-NLS-1$
			Objects.requireNonNull(AUTOSCALING_MODE_DISABLED);
			MethodType mt = MethodType.methodType(boolean.class, autoscalingModeEnumClass);
			SET_AUTOSCALINGMODE_HANDLE = MethodHandles.publicLookup().findVirtual(Control.class, "setAutoscalingMode", //$NON-NLS-1$
					mt);
		} catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
			// ignore
		}
	}

	static void setAutoscaleDisabled(Control control) {
		if (SET_AUTOSCALINGMODE_HANDLE != null) {
			if (propagationDisabledControls.contains(control)) {
				// When using the autoscale disablement API, disabling propagation has already
				// disabled autoscaling itself. To avoid an unintended enabled of propagation,
				// we must not change the disablement mode here again.
				return;
			}
			try {
				SET_AUTOSCALINGMODE_HANDLE.invoke(control, AUTOSCALING_MODE_DISABLED_INHERITED);
			} catch (Throwable e) {
				throw new SWTException(e.getMessage());
			}
		} else {
			control.setData(DATA_AUTOSCALE_DISABLED, true);
		}
	}

	static void disablePropagateAutoscale(Control control) {
		if (SET_AUTOSCALINGMODE_HANDLE != null) {
			try {
				SET_AUTOSCALINGMODE_HANDLE.invoke(control, AUTOSCALING_MODE_DISABLED);
			} catch (Throwable e) {
				throw new SWTException(e.getMessage());
			}
		} else {
			control.setData(DATA_PROPOGATE_AUTOSCALE_DISABLED, false);
		}
		propagationDisabledControls.add(control);
		control.addDisposeListener(e -> propagationDisabledControls.remove(control));
	}

	static int getShellZoom(Control control) {
		int shellZoom;
		try {
			if (GETSHELLZOOM_HANDLE != null) {
				try {
					shellZoom = (int) GETSHELLZOOM_HANDLE.invoke(control.getShell());
				} catch (Throwable e) {
					throw new SWTException(e.getMessage());
				}
			} else {
				shellZoom = (int) control.getData(DATA_SHELL_ZOOM);
			}
		} catch (NullPointerException e) {
			shellZoom = 100;
		}
		return shellZoom;
	}

}
