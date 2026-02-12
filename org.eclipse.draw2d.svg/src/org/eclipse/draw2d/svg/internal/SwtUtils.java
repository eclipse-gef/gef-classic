/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d.svg.internal;

import org.eclipse.swt.graphics.Color;

/**
 * Utility class for converting AWT properties to SWT properties.
 *
 * @since 3.2
 */
public class SwtUtils {

	private SwtUtils() {
		throw new IllegalStateException("Utility class must not be instantiated"); //$NON-NLS-1$
	}

	public static Color getColor(java.awt.Color color) {
		if (color == null) {
			return null;
		}
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
}
