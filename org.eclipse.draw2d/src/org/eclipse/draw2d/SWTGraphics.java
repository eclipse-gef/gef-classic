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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.PathData;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

import org.eclipse.pde.api.tools.annotations.NoExtend;

import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.internal.NativeGraphics;

/**
 * A concrete implementation of <code>Graphics</code> using an SWT
 * <code>GC</code>. There are 2 states contained in this graphics class -- the
 * applied state which is the actual state of the GC and the current state which
 * is the current state of this graphics object. Certain properties can be
 * changed multiple times and the GC won't be updated until it's actually used.
 * <P>
 * WARNING: This class is not intended to be subclassed.
 */
@NoExtend
public class SWTGraphics extends NativeGraphics {

	/**
	 * An internal type used to represent and update the GC's clipping.
	 *
	 * @since 3.1
	 */
	interface SWTClipping extends Clipping {
		void setOn(GC gc, int translateX, int translateY);
	}

	static class SWTRectangleClipping extends RectangleClipping implements SWTClipping {

		SWTRectangleClipping(float left, float top, float right, float bottom) {
			super(left, top, right, bottom);
		}

		SWTRectangleClipping(org.eclipse.swt.graphics.Rectangle rect) {
			super(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
		}

		SWTRectangleClipping(Rectangle rect) {
			super(rect);
		}

		@Override
		public Clipping getCopy() {
			return new SWTRectangleClipping(left, top, right, bottom);
		}

		@Override
		public void setOn(GC gc, int translateX, int translateY) {
			int xInt = (int) Math.floor(left);
			int yInt = (int) Math.floor(top);
			gc.setClipping(xInt + translateX, yInt + translateY, (int) Math.ceil(right) - xInt,
					(int) Math.ceil(bottom) - yInt);
		}
	}

	private static final MethodHandle DRAW_IMAGE_HANDLE = getDrawImageHandle();

	private static MethodHandle getDrawImageHandle() {
		try {
			// Introduced with SWT 3.132
			MethodType mt = MethodType.methodType(void.class, Image.class, int.class, int.class, int.class, int.class);
			return MethodHandles.publicLookup().findVirtual(GC.class, "drawImage", mt); //$NON-NLS-1$
		} catch (IllegalAccessException | NoSuchMethodException e) {
			// ignore
		}
		return null;
	}

	private final GC gc;

	private Transform transform;
	private int translateX = 0;
	private int translateY = 0;

	/**
	 * Constructs a new SWTGraphics that draws to the Canvas using the given GC.
	 *
	 * @param gc the GC
	 */
	public SWTGraphics(GC gc) {
		this.gc = gc;
		init();
	}

	/**
	 * If the background color has changed, this change will be pushed to the GC.
	 * Also calls {@link #checkGC()}.
	 */
	protected final void checkFill() {
		if (!currentState.bgColor.equals(appliedState.bgColor) && currentState.bgPattern == null) {
			appliedState.bgColor = currentState.bgColor;
			gc.setBackground(appliedState.bgColor);
		}
		checkGC();
	}

	/**
	 * If the rendering hints or the clip region has changed, these changes will be
	 * pushed to the GC. Rendering hints include anti-alias, xor, join, cap, line
	 * style, fill rule, interpolation, and other settings.
	 */
	protected final void checkGC() {
		if (appliedState.relativeClip != currentState.relativeClip) {
			appliedState.relativeClip = currentState.relativeClip;
			((SWTClipping) currentState.relativeClip).setOn(gc, translateX, translateY);
		}

		if (appliedState.graphicHints != currentState.graphicHints) {
			reconcileHints(gc, appliedState.graphicHints, currentState.graphicHints);
			appliedState.graphicHints = currentState.graphicHints;
		}
	}

	/**
	 * If the line width, line style, foreground or background colors have changed,
	 * these changes will be pushed to the GC. Also calls {@link #checkGC()}.
	 */
	protected final void checkPaint() {
		checkGC();
		if (!currentState.fgColor.equals(appliedState.fgColor) && currentState.fgPattern == null) {
			appliedState.fgColor = currentState.fgColor;
			gc.setForeground(appliedState.fgColor);
		}

		LineAttributes lineAttributes = currentState.lineAttributes;
		if (!appliedState.lineAttributes.equals(lineAttributes)) {
			if (getAdvanced()) {
				gc.setLineAttributes(clone(lineAttributes)); // Clone lineAttributes because on Windows hi-dpi the line
																// width may be increased
			} else {
				gc.setLineWidth((int) lineAttributes.width);
				gc.setLineCap(lineAttributes.cap);
				gc.setLineJoin(lineAttributes.join);
				gc.setLineStyle(lineAttributes.style);
				if (lineAttributes.dash != null) {
					gc.setLineDash(convertFloatArrayToInt(lineAttributes.dash));
				}
			}
			appliedState.lineAttributes = clone(lineAttributes);
		}

		if (!currentState.bgColor.equals(appliedState.bgColor) && currentState.bgPattern == null) {
			appliedState.bgColor = currentState.bgColor;
			gc.setBackground(appliedState.bgColor);
		}
	}

	/**
	 * If the font has changed, this change will be pushed to the GC. Also calls
	 * {@link #checkPaint()} and {@link #checkFill()}.
	 */
	protected final void checkText() {
		checkPaint();
		if (!appliedState.font.equals(currentState.font)) {
			appliedState.font = currentState.font;
			gc.setFont(appliedState.font);
		}
	}

	/**
	 * @see Graphics#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (transform != null) {
			transform.dispose();
		}
	}

	/**
	 * @see Graphics#drawArc(int, int, int, int, int, int)
	 */
	@Override
	public void drawArc(int x, int y, int width, int height, int offset, int length) {
		checkPaint();
		gc.drawArc(x + translateX, y + translateY, width, height, offset, length);
	}

	/**
	 * @see Graphics#drawFocus(int, int, int, int)
	 */
	@Override
	public void drawFocus(int x, int y, int w, int h) {
		checkPaint();
		gc.drawFocus(x + translateX, y + translateY, w + 1, h + 1);
	}

	/**
	 * @see Graphics#drawImage(Image, int, int)
	 */
	@Override
	public void drawImage(Image srcImage, int x, int y) {
		checkGC();
		gc.drawImage(srcImage, x + translateX, y + translateY);
	}

	@Override
	public void drawImage(Image image, int destX, int destY, int destWidth, int destHeight) {
		if (DRAW_IMAGE_HANDLE != null) {
			try {
				checkGC();
				DRAW_IMAGE_HANDLE.invoke(gc, image, destX + translateX, destY + translateY, destWidth, destHeight);
				return;
			} catch (Throwable e) {
				throw new SWTException(e.getMessage());
			}
		}
		// fallback for older SWT versions
		super.drawImage(image, destX, destY, destWidth, destHeight);
	}

	/**
	 * @see Graphics#drawImage(Image, int, int, int, int, int, int, int, int)
	 */
	@Override
	public void drawImage(Image srcImage, int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		checkGC();
		gc.drawImage(srcImage, x1, y1, w1, h1, x2 + translateX, y2 + translateY, w2, h2);
	}

	/**
	 * @see Graphics#drawLine(int, int, int, int)
	 */
	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		checkPaint();
		gc.drawLine(x1 + translateX, y1 + translateY, x2 + translateX, y2 + translateY);
	}

	/**
	 * @see Graphics#drawOval(int, int, int, int)
	 */
	@Override
	public void drawOval(int x, int y, int width, int height) {
		checkPaint();
		gc.drawOval(x + translateX, y + translateY, width, height);
	}

	/**
	 * This method requires advanced graphics support. A check should be made to
	 * ensure advanced graphics is supported in the user's environment before
	 * calling this method. See {@link GC#getAdvanced()}.
	 *
	 * @see Graphics#drawPath(Path)
	 */
	@Override
	public void drawPath(Path path) {
		checkPaint();
		initTransform(false);
		gc.drawPath(path);
	}

	/**
	 * @see Graphics#drawPoint(int, int)
	 */
	@Override
	public void drawPoint(int x, int y) {
		checkPaint();
		gc.drawPoint(x + translateX, y + translateY);
	}

	/**
	 * @see Graphics#drawPolygon(int[])
	 */
	@Override
	public void drawPolygon(int[] points) {
		checkPaint();
		try {
			translatePointArray(points, translateX, translateY);
			gc.drawPolygon(points);
		} finally {
			translatePointArray(points, -translateX, -translateY);
		}
	}

	/**
	 * @see Graphics#drawPolygon(PointList)
	 */
	@Override
	public void drawPolygon(PointList points) {
		drawPolygon(points.toIntArray());
	}

	/**
	 * @see Graphics#drawPolyline(int[])
	 */
	@Override
	public void drawPolyline(int[] points) {
		checkPaint();
		try {
			translatePointArray(points, translateX, translateY);
			gc.drawPolyline(points);
		} finally {
			translatePointArray(points, -translateX, -translateY);
		}
	}

	/**
	 * @see Graphics#drawPolyline(PointList)
	 */
	@Override
	public void drawPolyline(PointList points) {
		drawPolyline(points.toIntArray());
	}

	/**
	 * @see Graphics#drawRectangle(int, int, int, int)
	 */
	@Override
	public void drawRectangle(int x, int y, int width, int height) {
		checkPaint();
		gc.drawRectangle(x + translateX, y + translateY, width, height);
	}

	/**
	 * @see Graphics#drawRoundRectangle(Rectangle, int, int)
	 */
	@Override
	public void drawRoundRectangle(Rectangle r, int arcWidth, int arcHeight) {
		checkPaint();
		gc.drawRoundRectangle(r.x + translateX, r.y + translateY, r.width, r.height, arcWidth, arcHeight);
	}

	/**
	 * @see Graphics#drawString(String, int, int)
	 */
	@Override
	public void drawString(String s, int x, int y) {
		checkText();
		gc.drawString(s, x + translateX, y + translateY, true);
	}

	/**
	 * @see Graphics#drawText(String, int, int)
	 */
	@Override
	public void drawText(String s, int x, int y) {
		checkText();
		gc.drawText(s, x + translateX, y + translateY, true);
	}

	/**
	 * @see Graphics#drawTextLayout(TextLayout, int, int, int, int, Color, Color)
	 */
	@Override
	public void drawTextLayout(TextLayout layout, int x, int y, int selectionStart, int selectionEnd,
			Color selectionForeground, Color selectionBackground) {
		// $TODO probably just call checkPaint since Font and BG color don't
		// apply
		checkText();
		layout.draw(gc, x + translateX, y + translateY, selectionStart, selectionEnd, selectionForeground,
				selectionBackground);
	}

	/**
	 * @see Graphics#fillArc(int, int, int, int, int, int)
	 */
	@Override
	public void fillArc(int x, int y, int width, int height, int offset, int length) {
		checkFill();
		gc.fillArc(x + translateX, y + translateY, width, height, offset, length);
	}

	/**
	 * @see Graphics#fillGradient(int, int, int, int, boolean)
	 */
	@Override
	public void fillGradient(int x, int y, int w, int h, boolean vertical) {
		checkPaint();
		gc.fillGradientRectangle(x + translateX, y + translateY, w, h, vertical);
	}

	/**
	 * @see Graphics#fillOval(int, int, int, int)
	 */
	@Override
	public void fillOval(int x, int y, int width, int height) {
		checkFill();
		gc.fillOval(x + translateX, y + translateY, width, height);
	}

	/**
	 * This method requires advanced graphics support. A check should be made to
	 * ensure advanced graphics is supported in the user's environment before
	 * calling this method. See {@link GC#getAdvanced()}.
	 *
	 * @see Graphics#fillPath(Path)
	 */
	@Override
	public void fillPath(Path path) {
		checkFill();
		initTransform(false);
		gc.fillPath(path);
	}

	/**
	 * @see Graphics#fillPolygon(int[])
	 */
	@Override
	public void fillPolygon(int[] points) {
		checkFill();
		try {
			translatePointArray(points, translateX, translateY);
			gc.fillPolygon(points);
		} finally {
			translatePointArray(points, -translateX, -translateY);
		}
	}

	/**
	 * @see Graphics#fillPolygon(PointList)
	 */
	@Override
	public void fillPolygon(PointList points) {
		fillPolygon(points.toIntArray());
	}

	/**
	 * @see Graphics#fillRectangle(int, int, int, int)
	 */
	@Override
	public void fillRectangle(int x, int y, int width, int height) {
		checkFill();
		gc.fillRectangle(x + translateX, y + translateY, width, height);
	}

	/**
	 * @see Graphics#fillRoundRectangle(Rectangle, int, int)
	 */
	@Override
	public void fillRoundRectangle(Rectangle r, int arcWidth, int arcHeight) {
		checkFill();
		gc.fillRoundRectangle(r.x + translateX, r.y + translateY, r.width, r.height, arcWidth, arcHeight);
	}

	/**
	 * @see Graphics#fillString(String, int, int)
	 */
	@Override
	public void fillString(String s, int x, int y) {
		checkText();
		gc.drawString(s, x + translateX, y + translateY, false);
	}

	/**
	 * @see Graphics#fillText(String, int, int)
	 */
	@Override
	public void fillText(String s, int x, int y) {
		checkText();
		gc.drawText(s, x + translateX, y + translateY, false);
	}

	private final float[] transformElements = new float[6];

	/**
	 * If scale value in x and y is the same this is returned. Otherwise it returns
	 * a stable, direction-independent estimate of the current graphics scaling,
	 * computed as the square root of the absolute determinant of the transformation
	 * matrix.
	 *
	 * @see org.eclipse.draw2d.Graphics#getAbsoluteScale()
	 */
	@Override
	public double getAbsoluteScale() {
		if (currentState.sx == currentState.sy) {
			return currentState.sx;
		}

		if (transform == null) {
			return super.getAbsoluteScale();
		}
		transform.getElements(transformElements);

		return Math.sqrt(
				Math.abs(transformElements[0] * transformElements[3] - transformElements[1] * transformElements[2]));
	}

	/**
	 * @see NativeGraphics#getAffineMatrix(float[])
	 * @since 3.24
	 */
	@Override
	protected void getAffineMatrix(float[] m) {
		if (transform != null) {
			transform.getElements(m);
		}
	}

	/**
	 * @see Graphics#getFontMetrics()
	 */
	@Override
	public FontMetrics getFontMetrics() {
		checkText();
		return gc.getFontMetrics();
	}

	/**
	 * @since 3.5
	 */
	public void getLineAttributes(LineAttributes lineAttributes) {
		SWTGraphics.copyLineAttributes(lineAttributes, currentState.lineAttributes);
	}

	/**
	 * @since 3.5
	 */
	public float[] getLineDash() {
		return currentState.lineAttributes.dash.clone();
	}

	/**
	 * @since 3.5
	 */
	public float getLineDashOffset() {
		return currentState.lineAttributes.dashOffset;
	}

	/**
	 * Called by constructor, initializes all State information for currentState
	 */
	protected void init() {
		currentState.bgColor = appliedState.bgColor = gc.getBackground();
		currentState.fgColor = appliedState.fgColor = gc.getForeground();
		currentState.font = appliedState.font = gc.getFont();
		currentState.lineAttributes = gc.getLineAttributes();
		appliedState.lineAttributes = clone(currentState.lineAttributes);
		currentState.graphicHints |= gc.getLineStyle();
		currentState.graphicHints |= gc.getAdvanced() ? ADVANCED_GRAPHICS_MASK : 0;
		currentState.graphicHints |= gc.getXORMode() ? XOR_MASK : 0;

		appliedState.graphicHints = currentState.graphicHints;

		currentState.relativeClip = new SWTRectangleClipping(gc.getClipping());
		currentState.alpha = gc.getAlpha();
		currentState.sx = currentState.sy = 1.0f;
	}

	private void initTransform(boolean force) {
		if (!force && translateX == 0 && translateY == 0) {
			return;
		}

		if (transform == null) {
			transform = new Transform(Display.getCurrent());
			elementsNeedUpdate = true;
			transform.translate(translateX, translateY);
			translateX = 0;
			translateY = 0;
			gc.setTransform(transform);
			currentState.graphicHints |= ADVANCED_GRAPHICS_MASK;
		}
	}

	/**
	 * @see NativeGraphics#performRotate(float)
	 * @since 3.24
	 */
	@Override
	protected boolean performRotate(float degrees) {
		// Flush clipping, patter, etc., before applying transform
		checkGC();
		initTransform(true);
		transform.rotate(degrees);
		gc.setTransform(transform);
		return true;
	}

	/**
	 * @see NativeGraphics#performScale(float, float)
	 * @since 3.24
	 */
	@Override
	protected boolean performScale(float horizontal, float vertical) {
		// Flush any clipping before scaling
		checkGC();

		initTransform(true);
		transform.scale(horizontal, vertical);
		gc.setTransform(transform);
		return true;
	}

	/**
	 * @see NativeGraphics#performShear(float, float)
	 * @since 3.24
	 */
	@Override
	protected boolean performShear(float horz, float vert) {
		// Flush any clipping changes before shearing
		checkGC();
		initTransform(true);
		float[] matrix = new float[6];
		transform.getElements(matrix);
		transform.setElements(matrix[0] + matrix[2] * vert, matrix[1] + matrix[3] * vert, matrix[0] * horz + matrix[2],
				matrix[1] * horz + matrix[3], matrix[4], matrix[5]);

		gc.setTransform(transform);
		return true;
	}

	/**
	 * @see NativeGraphics#performTranslate(float, float)
	 * @since 3.24
	 */
	@Override
	protected boolean performTranslate(float dx, float dy) {
		initTransform(true);
		checkGC();
		transform.translate(dx, dy);
		gc.setTransform(transform);
		return true;
	}

	/**
	 * @see NativeGraphics#performTranslate(int, int)
	 * @since 3.24
	 */
	@Override
	protected boolean performTranslate(int dx, int dy) {
		if (transform != null) {
			// Flush clipping, pattern, etc. before applying transform
			checkGC();
			transform.translate(dx, dy);
			gc.setTransform(transform);
			return true;
		}
		translateX += dx;
		translateY += dy;
		return false;
	}

	private static void reconcileHints(GC gc, int applied, int hints) {
		int changes = hints ^ applied;

		if ((changes & XOR_MASK) != 0) {
			gc.setXORMode((hints & XOR_MASK) != 0);
		}

		// Check to see if there is anything remaining
		changes &= ~XOR_MASK;
		if (changes != 0) {
			if ((changes & INTERPOLATION_MASK) != 0) {
				gc.setInterpolation(((hints & INTERPOLATION_MASK) >> INTERPOLATION_SHIFT) - INTERPOLATION_WHOLE_NUMBER);
			}

			if ((changes & FILL_RULE_MASK) != 0) {
				gc.setFillRule(((hints & FILL_RULE_MASK) >> FILL_RULE_SHIFT) - FILL_RULE_WHOLE_NUMBER);
			}

			if ((changes & AA_MASK) != 0) {
				gc.setAntialias(((hints & AA_MASK) >> AA_SHIFT) - AA_WHOLE_NUMBER);
			}

			if ((changes & TEXT_AA_MASK) != 0) {
				gc.setTextAntialias(((hints & TEXT_AA_MASK) >> TEXT_AA_SHIFT) - AA_WHOLE_NUMBER);
			}

			// If advanced was flagged, but none of the conditions which trigger
			// advanced
			// actually got applied, force advanced graphics on.
			if ((changes & ADVANCED_GRAPHICS_MASK) != 0) {
				if ((hints & ADVANCED_GRAPHICS_MASK) != 0 && !gc.getAdvanced()) {
					gc.setAdvanced(true);
				}
			}
		}
	}

	@Override
	public void pushState() {
		// Only update current state when push is valid
		if (currentState.relativeClip != null) {
			currentState.dx = translateX;
			currentState.dy = translateY;
		}
		super.pushState();
	}

	/**
	 * @see Graphics#restoreState()
	 * @since 3.24
	 */
	@Override
	protected void restoreState(State s) {
		super.restoreState(s);

		// If the GC is currently advanced, but it was not when pushed, revert
		if (gc.getAdvanced() && (s.graphicHints & ADVANCED_GRAPHICS_MASK) == 0) {
			// Set applied clip to null to force a re-setting of the clipping.
			appliedState.relativeClip = null;
			gc.setAdvanced(false);
			appliedState.graphicHints &= ~ADVANCED_HINTS_MASK;
			appliedState.graphicHints |= ADVANCED_HINTS_DEFAULTS;
		}

		translateX = s.dx;
		translateY = s.dy;
	}

	/**
	 * @since 3.24
	 */
	@Override
	protected void setAffineMatrix(float[] m) {
		currentState.affineMatrix = m;
		if (m != null) {
			transform.setElements(m[0], m[1], m[2], m[3], m[4], m[5]);
		} else if (transform != null) {
			transform.dispose();
			transform = null;
			elementsNeedUpdate = false;
		}
		gc.setTransform(transform);
	}

	/**
	 * This method requires advanced graphics support. A check should be made to
	 * ensure advanced graphics is supported in the user's environment before
	 * calling this method. See {@link GC#getAdvanced()}.
	 *
	 * @see Graphics#setAlpha(int)
	 */
	@Override
	public void setAlpha(int alpha) {
		currentState.graphicHints |= ADVANCED_GRAPHICS_MASK;
		if (currentState.alpha != alpha) {
			currentState.alpha = alpha;
			gc.setAlpha(currentState.alpha);
		}
	}

	/**
	 * @see Graphics#setBackgroundPattern(Pattern)
	 */
	@Override
	public void setBackgroundPattern(Pattern pattern) {
		currentState.graphicHints |= ADVANCED_GRAPHICS_MASK;
		if (currentState.bgPattern == pattern) {
			return;
		}
		currentState.bgPattern = pattern;

		if (pattern != null) {
			initTransform(true);
		}
		gc.setBackgroundPattern(pattern);
	}

	/**
	 * This method requires advanced graphics support. A check should be made to
	 * ensure advanced graphics is supported in the user's environment before
	 * calling this method. See {@link GC#getAdvanced()}.
	 *
	 * @see Graphics#setClip(Path)
	 */
	@Override
	public void setClip(Path path) {
		initTransform(false);
		if (((appliedState.graphicHints ^ currentState.graphicHints) & FILL_RULE_MASK) != 0) {
			// If there is a pending change to the fill rule, apply it first.
			gc.setFillRule(((currentState.graphicHints & FILL_RULE_MASK) >> FILL_RULE_SHIFT) - FILL_RULE_WHOLE_NUMBER);
			// As long as the FILL_RULE is stored in a single bit, just toggling
			// it works.
			appliedState.graphicHints ^= FILL_RULE_MASK;
		}
		gc.setClipping(path);
		appliedState.relativeClip = currentState.relativeClip = null;
	}

	/**
	 * Simple implementation of clipping a Path within the context of current
	 * clipping rectangle for now (not region)
	 * <ul>
	 * <li>Note that this method wipes out the clipping rectangle area, hence if
	 * clients need to reset it call {@link #restoreState()}</li>
	 * </ul>
	 *
	 * @see org.eclipse.draw2d.Graphics#clipPath(org.eclipse.swt.graphics.Path)
	 */
	@Override
	public void clipPath(Path path) {
		initTransform(false);
		if (((appliedState.graphicHints ^ currentState.graphicHints) & FILL_RULE_MASK) != 0) {
			// If there is a pending change to the fill rule, apply it first.
			gc.setFillRule(((currentState.graphicHints & FILL_RULE_MASK) >> FILL_RULE_SHIFT) - FILL_RULE_WHOLE_NUMBER);
			// As long as the FILL_RULE is stored in a single bit, just toggling
			// it works.
			appliedState.graphicHints ^= FILL_RULE_MASK;
		}
		Rectangle clipping = currentState.relativeClip != null ? getClip(new Rectangle()) : new Rectangle();
		if (!clipping.isEmpty()) {
			Path flatPath = new Path(path.getDevice(), path, 0.01f);
			PathData pathData = flatPath.getPathData();
			flatPath.dispose();
			Region region = new Region(path.getDevice());
			loadPath(region, pathData.points, pathData.types);
			region.intersect(
					new org.eclipse.swt.graphics.Rectangle(clipping.x, clipping.y, clipping.width, clipping.height));
			gc.setClipping(region);
			appliedState.relativeClip = currentState.relativeClip = null;
			region.dispose();
		}
	}

	/**
	 * @see Graphics#setClip(Rectangle)
	 */
	@Override
	public void setClip(Rectangle rect) {
		currentState.relativeClip = new SWTRectangleClipping(rect);
	}

	/**
	 * @see Graphics#setForegroundPattern(Pattern)
	 */
	@Override
	public void setForegroundPattern(Pattern pattern) {
		currentState.graphicHints |= ADVANCED_GRAPHICS_MASK;
		if (currentState.fgPattern == pattern) {
			return;
		}
		currentState.fgPattern = pattern;

		if (pattern != null) {
			initTransform(true);
		}
		gc.setForegroundPattern(pattern);
	}

	private static void translatePointArray(int[] points, int translateX, int translateY) {
		if (translateX == 0 && translateY == 0) {
			return;
		}
		for (int i = 0; (i + 1) < points.length; i += 2) {
			points[i] += translateX;
			points[i + 1] += translateY;
		}
	}

	/**
	 * Countermeasure against LineAttributes class not having its own clone()
	 * method.
	 *
	 * @since 3.6
	 */
	public static LineAttributes clone(LineAttributes src) {
		float[] dashClone = null;
		if (src.dash != null) {
			dashClone = new float[src.dash.length];
			System.arraycopy(src.dash, 0, dashClone, 0, dashClone.length);
		}
		return new LineAttributes(src.width, src.cap, src.join, src.style, dashClone, src.dashOffset, src.miterLimit);
	}

	/**
	 * Countermeasure against LineAttributes class not having a copy by value
	 * function.
	 *
	 * @since 3.6
	 */
	public static void copyLineAttributes(LineAttributes dest, LineAttributes src) {
		if (dest != src) {
			dest.cap = src.cap;
			dest.join = src.join;
			dest.miterLimit = src.miterLimit;
			dest.style = src.style;
			dest.width = src.width;
			dest.dashOffset = src.dashOffset;

			if (src.dash == null) {
				dest.dash = null;
			} else {
				if ((dest.dash == null) || (dest.dash.length != src.dash.length)) {
					dest.dash = new float[src.dash.length];
				}
				System.arraycopy(src.dash, 0, dest.dash, 0, src.dash.length);
			}
		}
	}

	/**
	 * Utility method for use with countermeasure against passing line attributes to
	 * SWT forcing advanced graphics.
	 *
	 * @return
	 * @since 3.2
	 */
	private static int[] convertFloatArrayToInt(float[] fArray) {
		int[] iArray = null;
		if (fArray != null) {
			int arrayLen = fArray.length;
			iArray = new int[arrayLen];
			for (int i = 0; i < arrayLen; i++) {
				iArray[i] = (int) fArray[i];
			}
		}
		return iArray;
	}

	static void loadPath(Region region, float[] points, byte[] types) {
		int start = 0, end = 0;
		for (byte type : types) {
			switch (type) {
			case SWT.PATH_MOVE_TO: {
				if (start != end) {
					int n = 0;
					int[] temp = new int[end - start];
					for (int k = start; k < end; k++) {
						temp[n] = Math.round(points[k]);
						n++;
					}
					region.add(temp);
				}
				start = end;
				end += 2;
				break;
			}
			case SWT.PATH_LINE_TO: {
				end += 2;
				break;
			}
			case SWT.PATH_CLOSE: {
				if (start != end) {
					int n = 0;
					int[] temp = new int[end - start];
					for (int k = start; k < end; k++) {
						temp[n] = Math.round(points[k]);
						n++;
					}
					region.add(temp);
				}
				start = end;
				break;
			}
			}
		}
		if (start != end) {
			int n = 0;
			int[] temp = new int[end - start];
			for (int k = start; k < end; k++) {
				temp[n] = Math.round(points[k]);
				n++;
			}
			region.add(temp);
		}
	}
}