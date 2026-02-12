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

import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

import org.apache.batik.ext.awt.geom.Polygon2D;
import org.apache.batik.ext.awt.geom.Polyline2D;

/**
 * Utility class for converting SWT properties to AWT properties.
 */
public class AwtUtils {
	private static final int DPI = Toolkit.getDefaultToolkit().getScreenResolution();

	private AwtUtils() {
		throw new IllegalStateException("Utility class must not be instantiated"); //$NON-NLS-1$
	}

	public static java.awt.Color getColor(Color color) {
		if (color == null) {
			return null;
		}
		return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public static java.awt.Font getFont(Font font, int dpi) {
		if (font == null) {
			return null;
		}
		FontData fd = font.getFontData()[0];
		int height = fd.getHeight();
		if (DPI != dpi) {
			height = height * DPI / dpi;
		}
		return new java.awt.Font(fd.getName(), fd.getStyle(), height);
	}

	public static java.awt.Image getImage(Image image) {
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

	public static Polygon2D getPolygon(PointList points) {
		int npoints = points.size();
		int[] xpoints = new int[npoints];
		int[] ypoints = new int[npoints];
		Point point = Point.SINGLETON;
		for (int i = 0; i < npoints; ++i) {
			points.getPoint(point, i);
			xpoints[i] = point.x;
			ypoints[i] = point.y;
		}
		return new Polygon2D(xpoints, ypoints, npoints);
	}

	public static Polyline2D getPolyline(PointList points) {
		int npoints = points.size();
		int[] xpoints = new int[npoints];
		int[] ypoints = new int[npoints];
		Point point = Point.SINGLETON;
		for (int i = 0; i < npoints; ++i) {
			points.getPoint(point, i);
			xpoints[i] = point.x;
			ypoints[i] = point.y;
		}
		return new Polyline2D(xpoints, ypoints, npoints);
	}

	public static Paint getGradientPaint(Rectangle2D r, java.awt.Color c1, java.awt.Color c2, boolean vertical) {
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
}
