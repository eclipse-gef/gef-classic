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

	private static final boolean enableAutoScale = "win32".equals(SWT.getPlatform()) //$NON-NLS-1$
			&& Boolean.parseBoolean(System.getProperty(DRAW2D_ENABLE_AUTOSCALE, Boolean.TRUE.toString()));

	public static boolean isAutoScaleEnabled() {
		return enableAutoScale;
	}

	public static void configureForAutoscalingMode(Control control) {
		if (control != null && enableAutoScale) {
			control.setData("AUTOSCALE_DISABLED", true); //$NON-NLS-1$
		}
	}
}
