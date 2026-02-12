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

import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.swt.SWT;

/**
 * Convenience class for keeping track of the AWT rendering hints.
 */
public class RenderingHintsHolder implements Cloneable {
	public int antialias;
	public int textAntialias;
	public int interpolation;

	@Override
	public int hashCode() {
		return Objects.hash(antialias, textAntialias, interpolation);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RenderingHintsHolder other)) {
			return false;
		}
		return antialias == other.antialias //
				&& textAntialias == other.textAntialias //
				&& interpolation == other.interpolation;
	}

	@Override
	public RenderingHintsHolder clone() {
		try {
			return (RenderingHintsHolder) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public RenderingHints build() {
		RenderingHints renderingHints = new RenderingHints(new HashMap<>());
		renderingHints.put(RenderingHints.KEY_ANTIALIASING, getAntialias(antialias));
		renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, getTextAntialias(textAntialias));
		renderingHints.put(RenderingHints.KEY_INTERPOLATION, getInterpolation(interpolation));
		return renderingHints;
	}

	// #################################################################################################################
	//
	// Anti-Alias
	//
	// #################################################################################################################

	private static Map<Object, Integer> AWT_2_SWT_ANTIALIAS = new HashMap<>();
	static {
		AWT_2_SWT_ANTIALIAS.put(RenderingHints.VALUE_ANTIALIAS_ON, SWT.ON);
		AWT_2_SWT_ANTIALIAS.put(RenderingHints.VALUE_ANTIALIAS_OFF, SWT.OFF);
		AWT_2_SWT_ANTIALIAS.put(RenderingHints.VALUE_ANTIALIAS_DEFAULT, SWT.DEFAULT);
	}

	public static int getAntialias(Object key) {
		if (AWT_2_SWT_ANTIALIAS.containsKey(key)) {
			return AWT_2_SWT_ANTIALIAS.get(key);
		}
		throw new IllegalArgumentException("Unsupported antialias key: " + key); //$NON-NLS-1$
	}

	// #################################################################################################################
	//
	// Text Anti-Alias
	//
	// #################################################################################################################

	private static Map<Object, Integer> AWT_2_SWT_TEXT_ANTIALIAS = new HashMap<>();
	static {
		AWT_2_SWT_TEXT_ANTIALIAS.put(RenderingHints.VALUE_TEXT_ANTIALIAS_ON, SWT.ON);
		AWT_2_SWT_TEXT_ANTIALIAS.put(RenderingHints.VALUE_TEXT_ANTIALIAS_OFF, SWT.OFF);
		AWT_2_SWT_TEXT_ANTIALIAS.put(RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, SWT.DEFAULT);
	}

	public static int getTextAntialias(Object key) {
		if (AWT_2_SWT_TEXT_ANTIALIAS.containsKey(key)) {
			return AWT_2_SWT_TEXT_ANTIALIAS.get(key);
		}
		throw new IllegalArgumentException("Unsupported text-antialias key: " + key); //$NON-NLS-1$
	}

	// #################################################################################################################
	//
	// Interpolation
	//
	// #################################################################################################################

	private static Map<Object, Integer> AWT_2_SWT_INTERPOLATION = new HashMap<>();
	static {
		// AWT_2_SWT_INTERPOLATION.put(..., SWT.NONE);
		AWT_2_SWT_INTERPOLATION.put(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, SWT.LOW);
		AWT_2_SWT_INTERPOLATION.put(RenderingHints.VALUE_INTERPOLATION_BICUBIC, SWT.HIGH);
		AWT_2_SWT_INTERPOLATION.put(RenderingHints.VALUE_INTERPOLATION_BILINEAR, SWT.DEFAULT);
	}

	public static int getInterpolation(Object key) {
		if (AWT_2_SWT_INTERPOLATION.containsKey(key)) {
			return AWT_2_SWT_INTERPOLATION.get(key);
		}
		throw new IllegalArgumentException("Unsupported interpolation key: " + key); //$NON-NLS-1$
	}
}
