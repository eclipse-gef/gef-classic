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
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.PathData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**
 * Utility class for converting SWT to AWT properties.
 */
public class AWTUtils {
	public static final float[] LINE_DOT = { 10, 10 };
	public static final float[] LINE_DASH = { 30, 10 };
	public static final float[] LINE_DASHDOT = { 30, 10, 10, 10 };
	public static final float[] LINE_DASHDOTDOT = { 30, 10, 10, 10, 10, 10 };
	public static final float[] LINE_SOLID = null;
	private static final int DPI = Toolkit.getDefaultToolkit().getScreenResolution();

	public static Object getAntialias(int key) {
		return switch (key) {
		case SWT.ON -> RenderingHints.VALUE_ANTIALIAS_ON;
		case SWT.OFF -> RenderingHints.VALUE_ANTIALIAS_OFF;
		case SWT.DEFAULT -> RenderingHints.VALUE_ANTIALIAS_DEFAULT;
		default -> throw new IllegalArgumentException("Unsupported antialias key: %d".formatted(key)); //$NON-NLS-1$
		};
	}

	public static Color getColor(org.eclipse.swt.graphics.Color color) {
		if (color == null) {
			return null;
		}
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public static Font getFont(org.eclipse.swt.graphics.Font font, int dpi) {
		if (font == null) {
			return null;
		}
		FontData fd = font.getFontData()[0];
		int height = fd.getHeight();
		if (DPI != dpi) {
			height = height * DPI / dpi;
		}
		return new Font(fd.getName(), fd.getStyle(), height);
	}

	public static Paint getGradientPaint(Rectangle2D r, Color c1, Color c2, boolean vertical) {
		float x1;
		float x2;
		float y1;
		float y2;
		if (vertical) {
			x1 = (float) (r.getX() + r.getWidth() / 2);
			x2 = x1;
			y1 = (float) r.getY();
			y2 = (float) (r.getY() + r.getHeight());
		} else {
			x1 = (float) r.getX();
			x2 = (float) (r.getX() + r.getWidth());
			y1 = (float) (r.getX() + r.getHeight() / 2);
			y2 = y1;
		}
		return new GradientPaint(x1, y1, c1, x2, y2, c2);
	}

	public static Image getImage(org.eclipse.swt.graphics.Image image) {
		if (image == null) {
			return null;
		}
		ImageData imageData = image.getImageData(100);
		Rectangle bounds = image.getBounds();
		BufferedImage bufferedImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_4BYTE_ABGR);

		for (int x = 0; x < bounds.width; ++x) {
			for (int y = 0; y < bounds.height; ++y) {
				final int alpha = imageData.getAlpha(x, y);
				final RGB rgb = imageData.palette.getRGB(imageData.getPixel(x, y));
				bufferedImage.setRGB(x, y, alpha << 24 | rgb.red << 16 | rgb.green << 8 | rgb.blue);
			}
		}

		return bufferedImage;
	}

	public static Object getInterpolation(int key) {
		return switch (key) {
		case SWT.LOW -> RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		case SWT.HIGH -> RenderingHints.VALUE_INTERPOLATION_BICUBIC;
		case SWT.DEFAULT -> RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		default -> throw new IllegalArgumentException("Unsupported interpolation key: %d".formatted(key)); //$NON-NLS-1$
		};
	}

	public static int getLineCap(LineAttributes attr) {
		return switch (attr.cap) {
		case SWT.CAP_FLAT -> BasicStroke.CAP_BUTT;
		case SWT.CAP_ROUND -> BasicStroke.CAP_ROUND;
		case SWT.CAP_SQUARE -> BasicStroke.CAP_SQUARE;
		default -> throw new IllegalArgumentException("Unknown line cap : %d".formatted(attr.cap)); //$NON-NLS-1$
		};
	}

	public static int getLineJoin(LineAttributes attr) {
		return switch (attr.join) {
		case SWT.JOIN_BEVEL -> BasicStroke.JOIN_BEVEL;
		case SWT.JOIN_MITER -> BasicStroke.JOIN_MITER;
		case SWT.JOIN_ROUND -> BasicStroke.JOIN_ROUND;
		default -> throw new IllegalArgumentException("Unknown line join : %d".formatted(attr.join)); //$NON-NLS-1$
		};
	}

	public static Path2D getPath(Path path, int rule) {
		Path2D.Float shape = new Path2D.Float();
		shape.setWindingRule(getWindingRule(rule));

		PathData pathData = path.getPathData();
		int i = 0;

		for (byte type : pathData.types) {
			switch (type) {
			case SWT.PATH_MOVE_TO:
				shape.moveTo(pathData.points[i++], pathData.points[i++]);
				break;

			case SWT.PATH_LINE_TO:
				shape.lineTo(pathData.points[i++], pathData.points[i++]);
				break;

			case SWT.PATH_QUAD_TO:
				shape.quadTo(pathData.points[i++], pathData.points[i++], pathData.points[i++], pathData.points[i++]);
				break;

			case SWT.PATH_CUBIC_TO:
				shape.curveTo(pathData.points[i++], pathData.points[i++], pathData.points[i++], pathData.points[i++],
						pathData.points[i++], pathData.points[i++]);
				break;

			case SWT.PATH_CLOSE:
				shape.closePath();
				break;
			}
		}
		return shape;
	}

	public static Polygon getPolygon(PointList points) {
		Polygon polygon = new Polygon();
		Point p = new Point();
		for (int i = 0; i < points.size(); i++) {
			points.getPoint(p, i);
			polygon.addPoint(p.x, p.y);
		}
		return polygon;
	}

	public static BasicStroke getStroke(LineAttributes attr) {
		return new BasicStroke(attr.width, getLineCap(attr), getLineJoin(attr), attr.miterLimit, getLineDash(attr),
				attr.dashOffset);
	}

	public static float[] getLineDash(LineAttributes attr) {
		return switch (attr.style) {
		case SWT.LINE_DOT -> LINE_DOT;
		case SWT.LINE_DASH -> LINE_DASH;
		case SWT.LINE_DASHDOT -> LINE_DASHDOT;
		case SWT.LINE_DASHDOTDOT -> LINE_DASHDOTDOT;
		case SWT.LINE_SOLID -> LINE_SOLID;
		case SWT.LINE_CUSTOM -> attr.dash;
		default -> throw new IllegalArgumentException("Unknown line style: %d".formatted(attr.style)); //$NON-NLS-1$
		};
	}

	public static Object getTextAntialias(int key) {
		return switch (key) {
		case SWT.ON -> RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
		case SWT.OFF -> RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
		case SWT.DEFAULT -> RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
		default -> throw new IllegalArgumentException("Unsupported text-antialias key: %d".formatted(key)); //$NON-NLS-1$
		};
	}

	public static int getWindingRule(int rule) {
		if (rule == SWT.FILL_EVEN_ODD) {
			return GeneralPath.WIND_EVEN_ODD;
		}
		if (rule == SWT.FILL_WINDING) {
			return GeneralPath.WIND_NON_ZERO;
		}
		throw new IllegalArgumentException("Unknown winding rule: %d".formatted(rule)); //$NON-NLS-1$
	}

	private AWTUtils() {
		throw new IllegalStateException("Utility class must not be instantiated"); //$NON-NLS-1$
	}

}
