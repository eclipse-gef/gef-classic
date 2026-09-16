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

package org.eclipse.draw2d.internal;

import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;

/**
 * Utility class for converting AWT to SWT properties.
 */
public class SWTUtils {

	public static int getAlpha(java.awt.Composite composite) {
		if (composite instanceof java.awt.AlphaComposite alphaComposite) {
			return (int) (255 * alphaComposite.getAlpha());
		}
		return 255;
	}

	public static int getAntialias(Object key) {
		if (Objects.equals(RenderingHints.VALUE_ANTIALIAS_ON, key)) {
			return SWT.ON;
		}
		if (Objects.equals(RenderingHints.VALUE_ANTIALIAS_OFF, key)) {
			return SWT.OFF;
		}
		if (Objects.equals(RenderingHints.VALUE_ANTIALIAS_DEFAULT, key)) {
			return SWT.DEFAULT;
		}
		throw new IllegalArgumentException("Unsupported antialias key: " + key); //$NON-NLS-1$
	}

	public static Color getColor(java.awt.Color color) {
		if (color == null) {
			return null;
		}
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public static Font getFont(java.awt.Font f, Display display) {
		return new Font(display, f.getName(), f.getSize(), getFontStyle(f));
	}

	public static int getFontStyle(java.awt.Font f) {
		int style = 0;
		if (f.isBold()) {
			style |= SWT.BOLD;
		}
		if (f.isItalic()) {
			style |= SWT.ITALIC;
		}
		if (f.isPlain()) {
			style |= SWT.NORMAL;
		}
		return style;
	}

	public static int getInterpolation(Object key) {
		if (Objects.equals(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, key)) {
			return SWT.LOW;
		}
		if (Objects.equals(RenderingHints.VALUE_INTERPOLATION_BICUBIC, key)) {
			return SWT.HIGH;
		}
		if (Objects.equals(RenderingHints.VALUE_INTERPOLATION_BILINEAR, key)) {
			return SWT.DEFAULT;
		}
		throw new IllegalArgumentException("Unsupported interpolation key: " + key); //$NON-NLS-1$
	}

	public static LineAttributes getLineAttributes(java.awt.Stroke stroke) {
		LineAttributes attr = new LineAttributes(1.0f);
		if (stroke instanceof java.awt.BasicStroke basicStroke) {
			attr.cap = getLineCap(basicStroke.getEndCap());
			attr.dash = basicStroke.getDashArray();
			attr.dashOffset = basicStroke.getDashPhase();
			attr.join = getLineJoin(basicStroke.getLineJoin());
			attr.miterLimit = basicStroke.getMiterLimit();
			attr.style = getLineStyle(basicStroke.getDashArray());
			attr.width = basicStroke.getLineWidth();
		}
		return attr;
	}

	public static int getLineCap(int cap) {
		return switch (cap) {
		case BasicStroke.CAP_BUTT -> SWT.CAP_FLAT;
		case BasicStroke.CAP_ROUND -> SWT.CAP_ROUND;
		case BasicStroke.CAP_SQUARE -> SWT.CAP_SQUARE;
		default -> throw new IllegalArgumentException("Unknown line cap : %d".formatted(cap)); //$NON-NLS-1$
		};
	}

	public static int getLineJoin(int join) {
		return switch (join) {
		case BasicStroke.JOIN_BEVEL -> SWT.JOIN_BEVEL;
		case BasicStroke.JOIN_MITER -> SWT.JOIN_MITER;
		case BasicStroke.JOIN_ROUND -> SWT.JOIN_ROUND;
		default -> throw new IllegalArgumentException("Unknown line join : %d".formatted(join)); //$NON-NLS-1$
		};
	}

	public static int getLineStyle(float[] dash) {
		if (Arrays.equals(dash, AWTUtils.LINE_SOLID)) {
			return SWT.LINE_SOLID;
		}
		if (Arrays.equals(dash, AWTUtils.LINE_DASH)) {
			return SWT.LINE_DASH;
		}
		if (Arrays.equals(dash, AWTUtils.LINE_DOT)) {
			return SWT.LINE_DOT;
		}
		if (Arrays.equals(dash, AWTUtils.LINE_DASHDOT)) {
			return SWT.LINE_DASHDOT;
		}
		if (Arrays.equals(dash, AWTUtils.LINE_DASHDOTDOT)) {
			return SWT.LINE_DASHDOTDOT;
		}
		return SWT.LINE_CUSTOM;
	}

	public static Path getPath(java.awt.geom.Path2D path, Display display) {
		java.awt.geom.PathIterator iterator = path.getPathIterator(null);

		float[] coords = new float[6];

		Path p = new Path(display);

		while (!iterator.isDone()) {
			iterator.next();

			int segment = iterator.currentSegment(coords);
			switch (iterator.currentSegment(coords)) {
			case java.awt.geom.PathIterator.SEG_CLOSE -> p.close();
			case java.awt.geom.PathIterator.SEG_CUBICTO ->
				p.cubicTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
			case java.awt.geom.PathIterator.SEG_LINETO -> p.lineTo(coords[0], coords[1]);
			case java.awt.geom.PathIterator.SEG_MOVETO -> p.moveTo(coords[0], coords[1]);
			case java.awt.geom.PathIterator.SEG_QUADTO -> p.quadTo(coords[0], coords[1], coords[2], coords[3]);
			default -> throw new IllegalArgumentException("Unknown segment: " + segment); //$NON-NLS-1$
			}
		}

		return p;
	}

	public static int getTextAntialias(Object key) {
		if (Objects.equals(RenderingHints.VALUE_TEXT_ANTIALIAS_ON, key)) {
			return SWT.ON;
		}
		if (Objects.equals(RenderingHints.VALUE_TEXT_ANTIALIAS_OFF, key)) {
			return SWT.OFF;
		}
		if (Objects.equals(RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, key)) {
			return SWT.DEFAULT;
		}
		throw new IllegalArgumentException("Unsupported text-antialias key: " + key); //$NON-NLS-1$
	}

	private SWTUtils() {
		throw new IllegalStateException("Utility class must not be instantiated"); //$NON-NLS-1$
	}
}
