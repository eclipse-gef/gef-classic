/*******************************************************************************
 * Copyright (c) 2000, 2026 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.PathData;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A Graphics object able to scale all operations based on the current scale
 * factor.
 *
 * @deprecated all scaling needs for Draw2d can be handled by SWT natively and
 *             there are several issues in the implementation of ScaledGraphics.
 *             Therefore, use {@link SWTGraphics} instead. This class and all
 *             its creator functions will be deleted after the 2028-03 release.
 */
@Deprecated(forRemoval = true, since = "2026-03")
public class ScaledGraphics extends Graphics {

	private static class FontHeightCache {
		Font font;
		int height;
	}

	@Deprecated
	static record FontKey(Font font, int height) {

	}

	/**
	 * The internal state of the scaled graphics.
	 */
	@Deprecated
	protected static class State {
		private double appliedX;
		private double appliedY;
		private Font font;
		private float lineWidth;
		private double zoom;

		/**
		 * Constructs a new, uninitialized State object.
		 */
		@Deprecated
		protected State() {
		}

		/**
		 * Constructs a new State object and initializes the properties based on the
		 * given values.
		 *
		 * @param zoom      the zoom factor
		 * @param x         the x offset
		 * @param y         the y offset
		 * @param font      the font
		 * @param lineWidth the line width
		 */
		@Deprecated
		protected State(double zoom, double x, double y, Font font, int lineWidth) {
			this(zoom, x, y, font, (float) lineWidth);
		}

		/**
		 * Constructs a new State object and initializes the properties based on the
		 * given values.
		 *
		 * @param zoom      the zoom factor
		 * @param x         the x offset
		 * @param y         the y offset
		 * @param font      the font
		 * @param lineWidth the line width
		 *
		 * @since 3.5
		 */
		@Deprecated
		protected State(double zoom, double x, double y, Font font, float lineWidth) {
			this.zoom = zoom;
			this.appliedX = x;
			this.appliedY = y;
			this.font = font;
			this.lineWidth = lineWidth;
		}

		/**
		 * Sets all the properties of the state object.
		 *
		 * @param zoom      the zoom factor
		 * @param x         the x offset
		 * @param y         the y offset
		 * @param font      the font
		 * @param lineWidth the line width
		 */
		@Deprecated
		protected void setValues(double zoom, double x, double y, Font font, int lineWidth) {
			setValues(zoom, x, y, font, (float) lineWidth);
		}

		/**
		 * Sets all the properties of the state object.
		 *
		 * @param zoom      the zoom factor
		 * @param x         the x offset
		 * @param y         the y offset
		 * @param font      the font
		 * @param lineWidth the line width
		 *
		 * @since 3.5
		 */
		@Deprecated
		protected void setValues(double zoom, double x, double y, Font font, float lineWidth) {
			this.zoom = zoom;
			this.appliedX = x;
			this.appliedY = y;
			this.font = font;
			this.lineWidth = lineWidth;
		}
	}

	private static int[][] intArrayCache = new int[8][];
	private final Rectangle tempRECT = new Rectangle();

	static {
		for (int i = 0; i < intArrayCache.length; i++) {
			intArrayCache[i] = new int[i + 1];
		}
	}

	private boolean allowText = true;
	private final Map<FontKey, Font> fontCache = new HashMap<>();
	private final Map<Font, FontData> fontDataCache = new HashMap<>();
	private double fractionalX;
	private double fractionalY;
	private final Graphics graphics;
	private final FontHeightCache localCache = new FontHeightCache();
	private Font localFont;
	private float localLineWidth;
	private final List<State> stack = new ArrayList<>();
	private int stackPointer = 0;
	private final FontHeightCache targetCache = new FontHeightCache();

	@Deprecated
	double zoom = 1.0;

	/**
	 * Constructs a new ScaledGraphics based on the given Graphics object.
	 *
	 * @param g the base graphics object
	 */
	@Deprecated
	public ScaledGraphics(Graphics g) {
		if (g instanceof ScaledGraphics scaledGraphics) {
			graphics = scaledGraphics.graphics;
			zoom = scaledGraphics.zoom;
			fractionalX = scaledGraphics.fractionalX;
			fractionalY = scaledGraphics.fractionalY;
		} else {
			graphics = g;
		}
		localFont = g.getFont();
		localLineWidth = g.getLineWidthFloat();
	}

	/** @see Graphics#clipRect(Rectangle) */
	@Deprecated
	@Override
	public void clipRect(Rectangle r) {
		graphics.clipRect(zoomClipRect(r));
	}

	@Deprecated
	@SuppressWarnings("static-method")
	Font createFont(FontData data) {
		return new Font(Display.getCurrent(), data);
	}

	/**
	 * Scales given path by zoom factor
	 *
	 * @param path Path to be scaled
	 * @return Scaled path
	 */
	private Path createScaledPath(Path path) {
		PathData p = path.getPathData();
		for (int i = 0; i < p.points.length; i += 2) {
			p.points[i] = (float) (p.points[i] * zoom + fractionalX);
			p.points[i + 1] = (float) (p.points[i + 1] * zoom + fractionalY);
		}
		Path scaledPath = new Path(path.getDevice());
		int index = 0;
		for (byte type : p.types) {
			switch (type) {
			case SWT.PATH_MOVE_TO:
				scaledPath.moveTo(p.points[index], p.points[index + 1]);
				index += 2;
				break;
			case SWT.PATH_LINE_TO:
				scaledPath.lineTo(p.points[index], p.points[index + 1]);
				index += 2;
				break;
			case SWT.PATH_CUBIC_TO:
				scaledPath.cubicTo(p.points[index], p.points[index + 1], p.points[index + 2], p.points[index + 3],
						p.points[index + 4], p.points[index + 5]);
				index += 6;
				break;
			case SWT.PATH_QUAD_TO:
				scaledPath.quadTo(p.points[index], p.points[index + 1], p.points[index + 2], p.points[index + 3]);
				index += 4;
				break;
			case SWT.PATH_CLOSE:
				scaledPath.close();
				break;
			}
		}
		return scaledPath;
	}

	/** @see Graphics#dispose() */
	@Deprecated
	@Override
	public void dispose() {
		// Remove all states from the stack
		while (stackPointer > 0) {
			popState();
		}

		for (Font font : fontCache.values()) {
			font.dispose();
		}

	}

	/** @see Graphics#drawArc(int, int, int, int, int, int) */
	@Deprecated
	@Override
	public void drawArc(int x, int y, int w, int h, int offset, int sweep) {
		Rectangle z = zoomRect(x, y, w, h);
		if (z.isEmpty() || sweep == 0) {
			return;
		}
		graphics.drawArc(z, offset, sweep);
	}

	/** @see Graphics#drawFocus(int, int, int, int) */
	@Deprecated
	@Override
	public void drawFocus(int x, int y, int w, int h) {
		graphics.drawFocus(zoomRect(x, y, w, h));
	}

	/** @see Graphics#drawImage(Image, int, int) */
	@Deprecated
	@Override
	public void drawImage(Image srcImage, int x, int y) {
		org.eclipse.swt.graphics.Rectangle size = srcImage.getBounds();
		graphics.drawImage(srcImage, 0, 0, size.width, size.height, (int) (Math.floor((x * zoom + fractionalX))),
				(int) (Math.floor((y * zoom + fractionalY))), (int) (Math.floor((size.width * zoom + fractionalX))),
				(int) (Math.floor((size.height * zoom + fractionalY))));
	}

	/** @see Graphics#drawImage(Image, int, int, int, int) */
	@Deprecated
	@Override
	public void drawImage(Image srcImage, int destX, int destY, int destWidth, int destHeight) {
		Rectangle t = zoomRect(destX, destY, destWidth, destHeight);
		graphics.drawImage(srcImage, t.x, t.y, t.width, t.height);
	}

	/** @see Graphics#drawImage(Image, int, int, int, int, int, int, int, int) */
	@Deprecated
	@Override
	public void drawImage(Image srcImage, int sx, int sy, int sw, int sh, int tx, int ty, int tw, int th) {
		// "t" == target rectangle, "s" = source

		Rectangle t = zoomRect(tx, ty, tw, th);
		if (!t.isEmpty()) {
			graphics.drawImage(srcImage, sx, sy, sw, sh, t.x, t.y, t.width, t.height);
		}
	}

	/** @see Graphics#drawLine(int, int, int, int) */
	@Deprecated
	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		graphics.drawLine((int) (Math.floor((x1 * zoom + fractionalX))), (int) (Math.floor((y1 * zoom + fractionalY))),
				(int) (Math.floor((x2 * zoom + fractionalX))), (int) (Math.floor((y2 * zoom + fractionalY))));
	}

	/** @see Graphics#drawOval(int, int, int, int) */
	@Deprecated
	@Override
	public void drawOval(int x, int y, int w, int h) {
		graphics.drawOval(zoomRect(x, y, w, h));
	}

	/** @see Graphics#drawPath(Path) */
	@Deprecated
	@Override
	public void drawPath(Path path) {
		Path scaledPath = createScaledPath(path);
		try {
			graphics.drawPath(scaledPath);
		} finally {
			scaledPath.dispose();
		}
	}

	/** @see Graphics#drawPoint(int, int) */
	@Deprecated
	@Override
	public void drawPoint(int x, int y) {
		graphics.drawPoint((int) Math.floor(x * zoom + fractionalX), (int) Math.floor(y * zoom + fractionalY));
	}

	/**
	 * @see Graphics#drawPolygon(int[])
	 */
	@Deprecated
	@Override
	public void drawPolygon(int[] points) {
		graphics.drawPolygon(zoomPointList(points));
	}

	/** @see Graphics#drawPolygon(PointList) */
	@Deprecated
	@Override
	public void drawPolygon(PointList points) {
		graphics.drawPolygon(zoomPointList(points.toIntArray()));
	}

	/**
	 * @see Graphics#drawPolyline(int[])
	 */
	@Deprecated
	@Override
	public void drawPolyline(int[] points) {
		graphics.drawPolyline(zoomPointList(points));
	}

	/** @see Graphics#drawPolyline(PointList) */
	@Deprecated
	@Override
	public void drawPolyline(PointList points) {
		graphics.drawPolyline(zoomPointList(points.toIntArray()));
	}

	/** @see Graphics#drawRectangle(int, int, int, int) */
	@Deprecated
	@Override
	public void drawRectangle(int x, int y, int w, int h) {
		graphics.drawRectangle(zoomRect(x, y, w, h));
	}

	/** @see Graphics#drawRoundRectangle(Rectangle, int, int) */
	@Deprecated
	@Override
	public void drawRoundRectangle(Rectangle r, int arcWidth, int arcHeight) {
		graphics.drawRoundRectangle(zoomRect(r.x, r.y, r.width, r.height), (int) (arcWidth * zoom),
				(int) (arcHeight * zoom));
	}

	/** @see Graphics#drawString(String, int, int) */
	@Deprecated
	@Override
	public void drawString(String s, int x, int y) {
		if (allowText) {
			graphics.drawString(s, zoomTextPoint(x, y));
		}
	}

	/** @see Graphics#drawText(String, int, int) */
	@Deprecated
	@Override
	public void drawText(String s, int x, int y) {
		if (allowText) {
			graphics.drawText(s, zoomTextPoint(x, y));
		}
	}

	/**
	 * @see Graphics#drawText(String, int, int, int)
	 */
	@Deprecated
	@Override
	public void drawText(String s, int x, int y, int style) {
		if (allowText) {
			graphics.drawText(s, zoomTextPoint(x, y), style);
		}
	}

	/**
	 * @see Graphics#drawTextLayout(TextLayout, int, int, int, int, Color, Color)
	 */
	@Deprecated
	@Override
	public void drawTextLayout(TextLayout layout, int x, int y, int selectionStart, int selectionEnd,
			Color selectionForeground, Color selectionBackground) {
		TextLayout scaled = zoomTextLayout(layout);
		if (scaled == null) {
			return;
		}
		try {
			graphics.drawTextLayout(scaled, (int) Math.floor(x * zoom + fractionalX),
					(int) Math.floor(y * zoom + fractionalY), selectionStart, selectionEnd, selectionForeground,
					selectionBackground);
		} finally {
			scaled.dispose();
		}
	}

	/** @see Graphics#fillArc(int, int, int, int, int, int) */
	@Deprecated
	@Override
	public void fillArc(int x, int y, int w, int h, int offset, int sweep) {
		Rectangle z = zoomFillRect(x, y, w, h);
		if (z.isEmpty() || sweep == 0) {
			return;
		}
		graphics.fillArc(z, offset, sweep);
	}

	/** @see Graphics#fillGradient(int, int, int, int, boolean) */
	@Deprecated
	@Override
	public void fillGradient(int x, int y, int w, int h, boolean vertical) {
		graphics.fillGradient(zoomFillRect(x, y, w, h), vertical);
	}

	/** @see Graphics#fillOval(int, int, int, int) */
	@Deprecated
	@Override
	public void fillOval(int x, int y, int w, int h) {
		graphics.fillOval(zoomFillRect(x, y, w, h));
	}

	/** @see Graphics#fillPath(Path) */
	@Deprecated
	@Override
	public void fillPath(Path path) {
		Path scaledPath = createScaledPath(path);
		try {
			graphics.fillPath(scaledPath);
		} finally {
			scaledPath.dispose();
		}
	}

	/**
	 * @see Graphics#fillPolygon(int[])
	 */
	@Deprecated
	@Override
	public void fillPolygon(int[] points) {
		graphics.fillPolygon(zoomPointList(points));
	}

	/** @see Graphics#fillPolygon(PointList) */
	@Deprecated
	@Override
	public void fillPolygon(PointList points) {
		graphics.fillPolygon(zoomPointList(points.toIntArray()));
	}

	/** @see Graphics#fillRectangle(int, int, int, int) */
	@Deprecated
	@Override
	public void fillRectangle(int x, int y, int w, int h) {
		graphics.fillRectangle(zoomFillRect(x, y, w, h));
	}

	/** @see Graphics#fillRoundRectangle(Rectangle, int, int) */
	@Deprecated
	@Override
	public void fillRoundRectangle(Rectangle r, int arcWidth, int arcHeight) {
		graphics.fillRoundRectangle(zoomFillRect(r.x, r.y, r.width, r.height), (int) (arcWidth * zoom),
				(int) (arcHeight * zoom));
	}

	/** @see Graphics#fillString(String, int, int) */
	@Deprecated
	@Override
	public void fillString(String s, int x, int y) {
		if (allowText) {
			graphics.fillString(s, zoomTextPoint(x, y));
		}
	}

	/** @see Graphics#fillText(String, int, int) */
	@Deprecated
	@Override
	public void fillText(String s, int x, int y) {
		if (allowText) {
			graphics.fillText(s, zoomTextPoint(x, y));
		}
	}

	/**
	 * @see Graphics#getAbsoluteScale()
	 */
	@Deprecated
	@Override
	public double getAbsoluteScale() {
		return zoom * graphics.getAbsoluteScale();
	}

	/**
	 * @see Graphics#getAlpha()
	 */
	@Deprecated
	@Override
	public int getAlpha() {
		return graphics.getAlpha();
	}

	/**
	 * @see Graphics#getAntialias()
	 */
	@Deprecated
	@Override
	public int getAntialias() {
		return graphics.getAntialias();
	}

	/** @see Graphics#getBackgroundColor() */
	@Deprecated
	@Override
	public Color getBackgroundColor() {
		return graphics.getBackgroundColor();
	}

	@Deprecated
	Font getCachedFont(FontKey key) {
		Font font = fontCache.get(key);
		if (font != null) {
			return font;
		}
		key = new FontKey(key.font, key.height);
		FontData data = key.font.getFontData()[0];
		data.setHeight(key.height);
		Font zoomedFont = createFont(data);
		fontCache.put(key, zoomedFont);
		return zoomedFont;
	}

	@Deprecated
	FontData getCachedFontData(Font f) {
		return fontDataCache.computeIfAbsent(f, font -> font.getFontData()[0]);
	}

	/** @see Graphics#getClip(Rectangle) */
	@Deprecated
	@Override
	public Rectangle getClip(Rectangle rect) {
		graphics.getClip(rect);
		int x = (int) (rect.x / zoom);
		int y = (int) (rect.y / zoom);
		/*
		 * If the clip rectangle is queried, perform an inverse zoom, and take the
		 * ceiling of the resulting double. This is necessary because forward scaling
		 * essentially performs a floor() function. Without this, figures will think
		 * that they don't need to paint when actually they do.
		 */
		rect.width = (int) Math.ceil(rect.right() / zoom) - x;
		rect.height = (int) Math.ceil(rect.bottom() / zoom) - y;
		rect.x = x;
		rect.y = y;
		return rect;
	}

	/**
	 * @see Graphics#getAdvanced()
	 */
	@Deprecated
	@Override
	public boolean getAdvanced() {
		return graphics.getAdvanced();
	}

	/**
	 * @see Graphics#getFillRule()
	 */
	@Deprecated
	@Override
	public int getFillRule() {
		return graphics.getFillRule();
	}

	/** @see Graphics#getFont() */
	@Deprecated
	@Override
	public Font getFont() {
		return getLocalFont();
	}

	/** @see Graphics#getFontMetrics() */
	@Deprecated
	@Override
	public FontMetrics getFontMetrics() {
		return FigureUtilities.getFontMetrics(localFont);
	}

	/** @see Graphics#getForegroundColor() */
	@Deprecated
	@Override
	public Color getForegroundColor() {
		return graphics.getForegroundColor();
	}

	/**
	 * @see Graphics#getInterpolation()
	 */
	@Deprecated
	@Override
	public int getInterpolation() {
		return graphics.getInterpolation();
	}

	/**
	 * @see Graphics#getLineCap()
	 */
	@Deprecated
	@Override
	public int getLineCap() {
		return graphics.getLineCap();
	}

	/**
	 * @see Graphics#getLineJoin()
	 */
	@Deprecated
	@Override
	public int getLineJoin() {
		return graphics.getLineJoin();
	}

	/** @see Graphics#getLineStyle() */
	@Deprecated
	@Override
	public int getLineStyle() {
		return graphics.getLineStyle();
	}

	/** @see Graphics#getLineMiterLimit() */
	@Deprecated
	@Override
	public float getLineMiterLimit() {
		return graphics.getLineMiterLimit();
	}

	/** @see Graphics#getLineWidth() */
	@Deprecated
	@Override
	public int getLineWidth() {
		return (int) getLineWidthFloat();
	}

	/** @see Graphics#getLineWidthFloat() */
	@Deprecated
	@Override
	public float getLineWidthFloat() {
		return getLocalLineWidth();
	}

	/** @see Graphics#getLineAttributes() */
	@Deprecated
	@Override
	public LineAttributes getLineAttributes() {
		LineAttributes a = graphics.getLineAttributes();
		a.width = getLocalLineWidth();
		return a;
	}

	private Font getLocalFont() {
		return localFont;
	}

	private float getLocalLineWidth() {
		return localLineWidth;
	}

	/**
	 * @see Graphics#getTextAntialias()
	 */
	@Deprecated
	@Override
	public int getTextAntialias() {
		return graphics.getTextAntialias();
	}

	/** @see Graphics#getXORMode() */
	@Deprecated
	@Override
	public boolean getXORMode() {
		return graphics.getXORMode();
	}

	/** @see Graphics#popState() */
	@Deprecated
	@Override
	public void popState() {
		graphics.popState();
		stackPointer--;
		restoreLocalState(stack.get(stackPointer));
	}

	/** @see Graphics#pushState() */
	@Deprecated
	@Override
	public void pushState() {
		State s;
		if (stack.size() > stackPointer) {
			s = stack.get(stackPointer);
			s.setValues(zoom, fractionalX, fractionalY, getLocalFont(), localLineWidth);
		} else {
			stack.add(new State(zoom, fractionalX, fractionalY, getLocalFont(), localLineWidth));
		}
		stackPointer++;

		graphics.pushState();
	}

	private void restoreLocalState(State state) {
		this.fractionalX = state.appliedX;
		this.fractionalY = state.appliedY;
		setScale(state.zoom);
		setLocalFont(state.font);
		setLocalLineWidth(state.lineWidth);
	}

	/** @see Graphics#restoreState() */
	@Deprecated
	@Override
	public void restoreState() {
		graphics.restoreState();
		restoreLocalState(stack.get(stackPointer - 1));
	}

	/** @see Graphics#rotate(float) */
	@Deprecated
	@Override
	public void rotate(float degrees) {
		graphics.rotate(degrees);
	}

	/** @see Graphics#scale(double) */
	@Deprecated
	@Override
	public void scale(double amount) {
		setScale(zoom * amount);
	}

	/** @see Graphics#setAdvanced(boolean) */
	@Deprecated
	@Override
	public void setAdvanced(boolean advanced) {
		graphics.setAdvanced(advanced);
	}

	/**
	 * @see Graphics#setAlpha(int)
	 */
	@Deprecated
	@Override
	public void setAlpha(int alpha) {
		graphics.setAlpha(alpha);
	}

	/**
	 * @see Graphics#setAntialias(int)
	 */
	@Deprecated
	@Override
	public void setAntialias(int value) {
		graphics.setAntialias(value);
	}

	/** @see Graphics#setBackgroundColor(Color) */
	@Deprecated
	@Override
	public void setBackgroundColor(Color rgb) {
		graphics.setBackgroundColor(rgb);
	}

	/** @see Graphics#setClip(Path) */
	@Deprecated
	@Override
	public void setClip(Path path) {
		Path scaledPath = createScaledPath(path);
		try {
			graphics.setClip(scaledPath);
		} finally {
			scaledPath.dispose();
		}
	}

	/** @see Graphics#setBackgroundPattern(Pattern) */
	@Deprecated
	@Override
	public void setBackgroundPattern(Pattern pattern) {
		graphics.setBackgroundPattern(pattern);
	}

	/** @see Graphics#setClip(Rectangle) */
	@Deprecated
	@Override
	public void setClip(Rectangle r) {
		graphics.setClip(zoomClipRect(r));
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#clipPath(org.eclipse.swt.graphics.Path)
	 */
	@Deprecated
	@Override
	public void clipPath(Path path) {
		Path scaledPath = createScaledPath(path);
		try {
			graphics.clipPath(scaledPath);
		} finally {
			scaledPath.dispose();
		}
	}

	/**
	 * @see Graphics#setFillRule(int)
	 */
	@Deprecated
	@Override
	public void setFillRule(int rule) {
		graphics.setFillRule(rule);
	}

	/** @see Graphics#setFont(Font) */
	@Deprecated
	@Override
	public void setFont(Font f) {
		setLocalFont(f);
	}

	/** @see Graphics#setForegroundColor(Color) */
	@Deprecated
	@Override
	public void setForegroundColor(Color rgb) {
		graphics.setForegroundColor(rgb);
	}

	/** @see Graphics#setForegroundPattern(Pattern) */
	@Deprecated
	@Override
	public void setForegroundPattern(Pattern pattern) {
		graphics.setForegroundPattern(pattern);
	}

	/** @see org.eclipse.draw2d.Graphics#setInterpolation(int) */
	@Deprecated
	@Override
	public void setInterpolation(int interpolation) {
		graphics.setInterpolation(interpolation);
	}

	/**
	 * @see Graphics#setLineCap(int)
	 */
	@Deprecated
	@Override
	public void setLineCap(int cap) {
		graphics.setLineCap(cap);
	}

	/**
	 * @see Graphics#setLineDash(int[])
	 */
	@Deprecated
	@Override
	public void setLineDash(int[] dash) {
		graphics.setLineDash(dash);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#setLineDash(float[])
	 */
	@Deprecated
	@Override
	public void setLineDash(float[] dash) {
		graphics.setLineDash(dash);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#setLineDashOffset(float)
	 */
	@Deprecated
	@Override
	public void setLineDashOffset(float value) {
		graphics.setLineDashOffset(value);
	}

	/**
	 * @see Graphics#setLineJoin(int)
	 */
	@Deprecated
	@Override
	public void setLineJoin(int join) {
		graphics.setLineJoin(join);
	}

	/** @see Graphics#setLineStyle(int) */
	@Deprecated
	@Override
	public void setLineStyle(int style) {
		graphics.setLineStyle(style);
	}

	/** @see Graphics#setLineMiterLimit(float) */
	@Deprecated
	@Override
	public void setLineMiterLimit(float value) {
		graphics.setLineMiterLimit(value);
	}

	/** @see Graphics#setLineWidth(int) */
	@Deprecated
	@Override
	public void setLineWidth(int width) {
		setLineWidthFloat(width);
	}

	/** @see Graphics#setLineWidthFloat(float) */
	@Deprecated
	@Override
	public void setLineWidthFloat(float width) {
		setLocalLineWidth(width);
	}

	/** @see Graphics#setLineAttributes(LineAttributes) */
	@Deprecated
	@Override
	public void setLineAttributes(LineAttributes attributes) {
		graphics.setLineAttributes(attributes);
		setLocalLineWidth(attributes.width);
	}

	private void setLocalFont(Font f) {
		localFont = f;
		graphics.setFont(zoomFont(f));
	}

	private void setLocalLineWidth(float width) {
		localLineWidth = width;
		graphics.setLineWidthFloat(zoomLineWidth(width));
	}

	@Deprecated
	void setScale(double value) {
		if (zoom != value) {
			this.zoom = value;
			graphics.setFont(zoomFont(getLocalFont()));
			graphics.setLineWidthFloat(zoomLineWidth(localLineWidth));
		}
	}

	/**
	 * @see Graphics#setTextAntialias(int)
	 */
	@Deprecated
	@Override
	public void setTextAntialias(int value) {
		graphics.setTextAntialias(value);
	}

	/** @see Graphics#setXORMode(boolean) */
	@Deprecated
	@Override
	public void setXORMode(boolean b) {
		graphics.setXORMode(b);
	}

	/** @see Graphics#translate(int, int) */
	@Deprecated
	@Override
	public void translate(int dx, int dy) {
		// fractionalX/Y is the fractional part left over from previous
		// translates that gets lost in the integer approximation.
		double dxFloat = dx * zoom + fractionalX;
		double dyFloat = dy * zoom + fractionalY;
		fractionalX = dxFloat - Math.floor(dxFloat);
		fractionalY = dyFloat - Math.floor(dyFloat);
		graphics.translate((int) Math.floor(dxFloat), (int) Math.floor(dyFloat));
	}

	/** @see Graphics#translate(float, float) */
	@Deprecated
	@Override
	public void translate(float dx, float dy) {
		double dxFloat = dx * zoom + fractionalX;
		double dyFloat = dy * zoom + fractionalY;
		fractionalX = dxFloat - Math.floor(dxFloat);
		fractionalY = dyFloat - Math.floor(dyFloat);
		graphics.translate((int) Math.floor(dxFloat), (int) Math.floor(dyFloat));
	}

	private Rectangle zoomClipRect(Rectangle r) {
		tempRECT.x = (int) (Math.floor(r.x * zoom + fractionalX));
		tempRECT.y = (int) (Math.floor(r.y * zoom + fractionalY));
		tempRECT.width = (int) (Math.ceil(((r.x + r.width) * zoom + fractionalX))) - tempRECT.x;
		tempRECT.height = (int) (Math.ceil(((r.y + r.height) * zoom + fractionalY))) - tempRECT.y;
		return tempRECT;
	}

	private Rectangle zoomFillRect(int x, int y, int w, int h) {
		tempRECT.x = (int) (Math.floor((x * zoom + fractionalX)));
		tempRECT.y = (int) (Math.floor((y * zoom + fractionalY)));
		tempRECT.width = (int) (Math.floor(((x + w - 1) * zoom + fractionalX))) - tempRECT.x + 1;
		tempRECT.height = (int) (Math.floor(((y + h - 1) * zoom + fractionalY))) - tempRECT.y + 1;
		return tempRECT;
	}

	@Deprecated
	Font zoomFont(Font f) {
		if (f == null) {
			Font localFont = getLocalFont();
			f = localFont != null ? localFont : Display.getCurrent().getSystemFont();
		}
		FontData data = getCachedFontData(f);
		int zoomedFontHeight = zoomFontHeight(data.getHeight());
		allowText = zoomedFontHeight > 0;
		return getCachedFont(new FontKey(f, zoomedFontHeight));
	}

	@Deprecated
	int zoomFontHeight(int height) {
		return (int) (zoom * height);
	}

	@Deprecated
	float zoomLineWidth(float w) {
		return (float) (w * zoom);
	}

	private int[] zoomPointList(int[] points) {
		int[] scaled = null;

		// Look in cache for a integer array with the same length as 'points'
		for (int i = 0; i < intArrayCache.length; i++) {
			if (intArrayCache[i].length == points.length) {
				scaled = intArrayCache[i];

				// Move this integer array up one notch in the array
				if (i != 0) {
					int[] temp = intArrayCache[i - 1];
					intArrayCache[i - 1] = scaled;
					intArrayCache[i] = temp;
				}
			}
		}

		// If no match is found, take the one that is last and resize it.
		if (scaled == null) {
			intArrayCache[intArrayCache.length - 1] = new int[points.length];
			scaled = intArrayCache[intArrayCache.length - 1];
		}

		// Scale the points
		for (int i = 0; (i + 1) < points.length; i += 2) {
			scaled[i] = (int) (Math.floor((points[i] * zoom + fractionalX)));
			scaled[i + 1] = (int) (Math.floor((points[i + 1] * zoom + fractionalY)));
		}
		return scaled;
	}

	private Rectangle zoomRect(int x, int y, int w, int h) {
		tempRECT.x = (int) (Math.floor(x * zoom + fractionalX));
		tempRECT.y = (int) (Math.floor(y * zoom + fractionalY));
		tempRECT.width = (int) (Math.floor(((x + w) * zoom + fractionalX))) - tempRECT.x;
		tempRECT.height = (int) (Math.floor(((y + h) * zoom + fractionalY))) - tempRECT.y;
		return tempRECT;
	}

	private TextLayout zoomTextLayout(TextLayout layout) {
		int zoomWidth = -1;

		if (layout.getWidth() != -1) {
			zoomWidth = ((int) (layout.getWidth() * zoom));
		}

		if (zoomWidth < -1 || zoomWidth == 0) {
			return null;
		}

		TextLayout zoomed = new TextLayout(Display.getCurrent());
		zoomed.setText(layout.getText());
		zoomed.setFont(zoomFont(layout.getFont()));
		zoomed.setAlignment(layout.getAlignment());
		zoomed.setAscent(layout.getAscent());
		zoomed.setDescent(layout.getDescent());
		zoomed.setOrientation(layout.getOrientation());
		zoomed.setSegments(layout.getSegments());
		zoomed.setSpacing(layout.getSpacing());
		zoomed.setTabs(layout.getTabs());

		zoomed.setWidth(zoomWidth);

		int length = layout.getText().length();
		// navigate through text
		if (length > 0) {
			int start = 0, offset = 1;
			TextStyle style = null, lastStyle = layout.getStyle(0);
			for (; offset <= length; offset++) {
				if (offset != length && (style = layout.getStyle(offset)) == lastStyle) {
					continue;
				}
				int end = offset - 1;

				if (lastStyle != null) {
					TextStyle zoomedStyle = new TextStyle(zoomFont(lastStyle.font), lastStyle.foreground,
							lastStyle.background);
					zoomedStyle.metrics = lastStyle.metrics;
					zoomedStyle.rise = lastStyle.rise;
					zoomedStyle.strikeout = lastStyle.strikeout;
					zoomedStyle.strikeoutColor = lastStyle.strikeoutColor;
					zoomedStyle.underline = lastStyle.underline;
					zoomedStyle.underlineColor = lastStyle.underlineColor;
					zoomedStyle.underlineStyle = lastStyle.underlineStyle;

					zoomed.setStyle(zoomedStyle, start, end);
				}
				lastStyle = style;
				start = offset;
			}
		}
		return zoomed;
	}

	private Point zoomTextPoint(int x, int y) {
		if (localCache.font != localFont) {
			// Font is different, re-calculate its height
			FontMetrics metric = FigureUtilities.getFontMetrics(localFont);
			localCache.height = metric.getHeight() - metric.getDescent();
			localCache.font = localFont;
		}
		if (targetCache.font != graphics.getFont()) {
			FontMetrics metric = graphics.getFontMetrics();
			targetCache.font = graphics.getFont();
			targetCache.height = metric.getHeight() - metric.getDescent();
		}
		return new Point(((int) (Math.floor((x * zoom) + fractionalX))),
				(int) (Math.floor((y + localCache.height - 1) * zoom - targetCache.height + 1 + fractionalY)));
	}
}
