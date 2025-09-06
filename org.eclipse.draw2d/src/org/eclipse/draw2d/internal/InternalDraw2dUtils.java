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

	public static boolean disableAutoscale;

	static {
		disableAutoscale = "win32".equals(SWT.getPlatform()) //$NON-NLS-1$
				&& Boolean.parseBoolean(System.getProperty(DRAW2D_DISABLE_AUTOSCALE));
	}
}
