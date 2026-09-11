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

package org.eclipse.draw2d;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Pattern;

import org.eclipse.pde.api.tools.annotations.NoExtend;

import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.internal.AWTUtils;
import org.eclipse.draw2d.internal.NativeGraphics;
import org.eclipse.draw2d.internal.SWTUtils;

/**
 * The {@link AWTGraphics} class paints to a {@link Component}.
 *
 * @since 3.24
 * @noextend This class is not intended to be subclassed by clients.
 */
@NoExtend
public class AWTGraphics extends NativeGraphics {

	static interface AWTClipping extends Clipping {
		@Override
		AWTClipping getCopy();

		void setOn(Graphics2D gc);
	}

	static class AWTRectangleClipping extends RectangleClipping implements AWTClipping {
		AWTRectangleClipping(float x, float y, float right, float bottom) {
			super(x, y, right, bottom);
		}

		AWTRectangleClipping(org.eclipse.draw2d.geometry.Rectangle rect) {
			super(rect);
		}

		AWTRectangleClipping(Rectangle rect) {
			super(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
		}

		@Override
		public AWTRectangleClipping getCopy() {
			return new AWTRectangleClipping(left, top, right, bottom);
		}

		@Override
		public void setOn(Graphics2D gc) {
			int xInt = (int) Math.floor(left);
			int yInt = (int) Math.floor(top);
			int widthInt = (int) Math.ceil(right) - xInt;
			int heightInt = (int) Math.ceil(bottom) - yInt;
			gc.setClip(xInt, yInt, widthInt, heightInt);
		}
	}

	private static final int DPI = Toolkit.getDefaultToolkit().getScreenResolution();

	/**
	 * Convenience method to check two {@link LineAttributes} for (deep) equality,
	 * as the SWT class doesn't override {@code equals(Object)}.
	 *
	 * @param first  The first attribute to check.
	 * @param second The second attribute to check.
	 * @return {@code true}, if both attributes are deeply equal.
	 */
	private static boolean equals(LineAttributes first, LineAttributes second) {
		if (first == second) {
			return true;
		}
		if (first == null) {
			return false;
		}
		if (second == null) {
			return false;
		}
		return first.cap == second.cap //
				&& Arrays.equals(first.dash, second.dash) //
				&& first.dashOffset == second.dashOffset //
				&& first.join == second.join //
				&& first.miterLimit == second.miterLimit //
				&& first.style == second.style //
				&& first.width == second.width;
	}

	private final AffineTransform transform = new AffineTransform();

	private Font initialFont;

	protected final Graphics2D gc;

	/**
	 * Constructs a new AWTGraphics object that draws to the Canvas using the given
	 * Graphics2D.
	 *
	 * @param gc the Graphics2D object.
	 */

	public AWTGraphics(Graphics2D gc) {
		this.gc = gc;
		init();
	}

	/**
	 * If the rendering hints or the clip region has changed, these changes will be
	 * pushed to the GC. Rendering hints include anti-alias, xor, join, cap, line
	 * style, fill rule, interpolation, and other settings.
	 */
	protected void checkGC() {
		if (!Objects.equals(currentState.relativeClip, appliedState.relativeClip)) {
			((AWTClipping) currentState.relativeClip).setOn(gc);
			appliedState.relativeClip = currentState.relativeClip.getCopy();
		}
		if (!Objects.equals(currentState.graphicHints, appliedState.graphicHints)) {
			reconcileHints(currentState.graphicHints, appliedState.graphicHints);
			appliedState.graphicHints = currentState.graphicHints;
		}
		if (!equals(currentState.lineAttributes, appliedState.lineAttributes)) {
			gc.setStroke(AWTUtils.getStroke(currentState.lineAttributes));
			appliedState.lineAttributes = SWTGraphics.clone(currentState.lineAttributes);
		}
		if (!Objects.equals(currentState.font, appliedState.font)) {
			gc.setFont(AWTUtils.getFont(currentState.font, getDPI()));
			appliedState.font = currentState.font;
		}
		if (!Objects.equals(currentState.fgColor, appliedState.fgColor)) {
			gc.setColor(AWTUtils.getColor(currentState.fgColor));
			appliedState.fgColor = currentState.fgColor;
		}
		if (!Objects.equals(currentState.bgColor, appliedState.bgColor)) {
			gc.setBackground(AWTUtils.getColor(currentState.bgColor));
			appliedState.bgColor = currentState.bgColor;
		}
	}

	/**
	 * @see Graphics#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (initialFont != null) {
			initialFont.dispose();
		}
	}

	/**
	 * @see Graphics#drawArc(int, int, int, int, int, int)
	 */
	@Override
	public void drawArc(int x, int y, int w, int h, int offset, int length) {
		drawShape(new Arc2D.Double(x, y, w, h, offset, length, Arc2D.OPEN));
	}

	/**
	 * @see Graphics#drawFocus(int, int, int, int)
	 */
	@Override
	public void drawFocus(int x, int y, int w, int h) {
		drawShape(new Rectangle2D.Double(x, y, w, h));
	}

	/**
	 * @see Graphics#drawImage(Image, int, int)
	 */
	@Override
	public void drawImage(Image srcImage, int x, int y) {
		checkGC();
		gc.drawImage(AWTUtils.getImage(srcImage), x, y, null);
	}

	/**
	 * @see Graphics#drawImage(Image, int, int, int, int)
	 */
	@Override
	public void drawImage(Image srcImage, int x, int y, int width, int height) {
		checkGC();
		gc.drawImage(AWTUtils.getImage(srcImage), x, y, width, height, null);
	}

	/**
	 * @see Graphics#drawImage(Image, int, int, int, int, int, int, int, int)
	 */
	@Override
	public void drawImage(Image srcImage, int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		checkGC();
		gc.drawImage(AWTUtils.getImage(srcImage), x2, y2, x2 + w2, y2 + h2, x1, y1, x1 + w1, y1 + h1, null);
	}

	/**
	 * @see Graphics#drawLine(int, int, int, int)
	 */
	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		drawShape(new Line2D.Double(x1, y1, x2, y2));
	}

	/**
	 * @see Graphics#drawOval(int, int, int, int)
	 */
	@Override
	public void drawOval(int x, int y, int w, int h) {
		drawShape(new Ellipse2D.Double(x, y, w, h));
	}

	/**
	 * @see Graphics#drawPath(Path)
	 */
	@Override
	public void drawPath(Path path) {
		drawShape(AWTUtils.getPath(path, getWindingRule()));
	}

	/**
	 * @see Graphics#drawPolygon(PointList)
	 */
	@Override
	public void drawPolygon(PointList points) {
		drawShape(AWTUtils.getPolygon(points));
	}

	/**
	 * @see Graphics#drawPolyline(PointList)
	 */
	@Override
	public void drawPolyline(PointList points) {
		Polygon p = AWTUtils.getPolygon(points);
		checkGC();
		gc.drawPolyline(p.xpoints, p.ypoints, p.npoints);
	}

	/**
	 * @see Graphics#drawRectangle(int, int, int, int)
	 */
	@Override
	public void drawRectangle(int x, int y, int width, int height) {
		drawShape(new Rectangle2D.Double(x, y, width, height));
	}

	/**
	 * @see Graphics#drawRoundRectangle(org.eclipse.draw2d.geometry.Rectangle, int,
	 *      int)
	 */
	@Override
	public void drawRoundRectangle(org.eclipse.draw2d.geometry.Rectangle r, int arcWidth, int arcHeight) {
		drawShape(new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, arcWidth, arcHeight));
	}

	private void drawShape(Shape shape) {
		checkGC();
		gc.draw(shape);
	}

	/**
	 * @see Graphics#drawString(String, int, int)
	 */
	@Override
	public void drawString(String s, int x, int y) {
		drawString(s, x, y, false);
	}

	private void drawString(String s, int x, int y, boolean fill) {
		checkGC();
		Rectangle2D bounds = gc.getFontMetrics().getStringBounds(s, gc);
		if (fill) {
			Color bgColor = gc.getBackground();
			Color fgColor = gc.getColor();
			gc.setColor(bgColor);
			gc.fillRect(x, y, (int) Math.round(bounds.getWidth()), (int) Math.round(bounds.getHeight()));
			gc.setColor(fgColor);
		}
		gc.drawString(s, Math.round((x - bounds.getX())), Math.round(y - bounds.getY()));
	}

	/**
	 * @see Graphics#drawText(String, int, int)
	 */
	@Override
	public void drawText(String s, int x, int y) {
		drawString(s, x, y, false);
	}

	/**
	 * @see Graphics#fillArc(int, int, int, int, int, int)
	 */
	@Override
	public void fillArc(int x, int y, int w, int h, int offset, int length) {
		fillShape(new Arc2D.Double(x, y, w, h, offset, length, Arc2D.PIE));
	}

	/**
	 * @see Graphics#fillGradient(int, int, int, int, boolean)
	 */
	@Override
	public void fillGradient(int x, int y, int w, int h, boolean vertical) {
		checkGC();
		Paint paint = gc.getPaint();
		Rectangle2D r = new Rectangle2D.Double(x, y, w, h);
		gc.setPaint(AWTUtils.getGradientPaint(r, gc.getColor(), gc.getBackground(), vertical));
		gc.fill(r);
		gc.setPaint(paint);
	}

	/**
	 * @see Graphics#fillOval(int, int, int, int)
	 */
	@Override
	public void fillOval(int x, int y, int w, int h) {
		fillShape(new Ellipse2D.Double(x, y, w, h));
	}

	/**
	 * @see Graphics#fillPath(Path)
	 */
	@Override
	public void fillPath(Path path) {
		fillShape(AWTUtils.getPath(path, getWindingRule()));
	}

	/**
	 * @see Graphics#fillPolygon(PointList)
	 */
	@Override
	public void fillPolygon(PointList points) {
		fillShape(AWTUtils.getPolygon(points));
	}

	/**
	 * @see Graphics#fillRectangle(int, int, int, int)
	 */
	@Override
	public void fillRectangle(int x, int y, int width, int height) {
		fillShape(new Rectangle2D.Double(x, y, width, height));
	}

	/**
	 * @see Graphics#fillRoundRectangle(org.eclipse.draw2d.geometry.Rectangle, int,
	 *      int)
	 */
	@Override
	public void fillRoundRectangle(org.eclipse.draw2d.geometry.Rectangle r, int arcWidth, int arcHeight) {
		fillShape(new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, arcWidth, arcHeight));
	}

	private void fillShape(Shape shape) {
		checkGC();
		Color fgColor = gc.getColor();
		gc.setColor(gc.getBackground());
		gc.fill(shape);
		gc.setColor(fgColor);
	}

	/**
	 * @see Graphics#fillString(String, int, int)
	 */
	@Override
	public void fillString(String s, int x, int y) {
		drawString(s, x, y, true);
	}

	/**
	 * @see Graphics#fillText(String, int, int)
	 */
	@Override
	public void fillText(String s, int x, int y) {
		drawString(s, x, y, true);
	}

	/**
	 * If scale value in x and y is the same this is returned. Otherwise it returns
	 * a stable, direction-independent estimate of the current graphics scaling,
	 * computed as the square root of the absolute determinant of the transformation
	 * matrix.
	 *
	 * @see Graphics#getAbsoluteScale()
	 */
	@Override
	public double getAbsoluteScale() {
		if (currentState.sx == currentState.sy) {
			return currentState.sx;
		}
		return Math.sqrt(transform.getDeterminant());
	}

	@Override
	protected void getAffineMatrix(float[] m) {
		m[0] = (float) transform.getScaleX();
		m[1] = (float) transform.getShearY();
		m[2] = (float) transform.getShearX();
		m[3] = (float) transform.getScaleY();
		m[4] = (float) transform.getTranslateX();
		m[5] = (float) transform.getTranslateY();
	}

	/**
	 * The resolution of the underlying device in dots-per-inch. May be overridden
	 * by subclasses.
	 *
	 * @return The resolution in dots-per-inch.
	 */
	@SuppressWarnings("static-method")
	protected int getDPI() {
		return DPI;
	}

	/**
	 * @see Graphics#getFontMetrics()
	 */
	@Override
	public FontMetrics getFontMetrics() {
		checkGC();
		return FigureUtilities.getFontMetrics(getFont());
	}

	/**
	 * Unlike SWT, the winding rule can't be set globally via
	 * {@link GC#setFillRule(int)}. Instead it has to be set individually for each
	 * path via {@link Path2D#setWindingRule(int)}.
	 *
	 * @return The SWT value for the current winding rule.
	 */
	private int getWindingRule() {
		return ((currentState.graphicHints & FILL_RULE_MASK) >> FILL_RULE_SHIFT) - FILL_RULE_WHOLE_NUMBER;
	}

	private void init() {
		currentState.bgColor = SWTUtils.getColor(gc.getBackground());
		appliedState.bgColor = currentState.bgColor;
		currentState.fgColor = SWTUtils.getColor(gc.getColor());
		appliedState.bgColor = currentState.bgColor;

		initialFont = SWTUtils.getFont(gc.getFont(), null);
		currentState.font = initialFont;
		appliedState.font = initialFont;

		currentState.lineAttributes = SWTUtils.getLineAttributes(gc.getStroke());
		appliedState.lineAttributes = SWTGraphics.clone(currentState.lineAttributes);

		RenderingHints hints = gc.getRenderingHints();
		if (hints.containsKey(RenderingHints.KEY_TEXT_ANTIALIASING)) {
			int value = SWTUtils.getTextAntialias(hints.get(RenderingHints.KEY_TEXT_ANTIALIASING));
			currentState.graphicHints |= (value << TEXT_AA_SHIFT) & TEXT_AA_MASK;
		}
		if (hints.containsKey(RenderingHints.KEY_ANTIALIASING)) {
			int value = SWTUtils.getAntialias(hints.get(RenderingHints.KEY_ANTIALIASING));
			currentState.graphicHints |= (value << AA_SHIFT) & AA_MASK;
		}
		if (hints.containsKey(RenderingHints.KEY_INTERPOLATION)) {
			int value = SWTUtils.getInterpolation(hints.get(RenderingHints.KEY_INTERPOLATION));
			currentState.graphicHints |= (value << INTERPOLATION_SHIFT) & INTERPOLATION_MASK;
		}
		appliedState.graphicHints = currentState.graphicHints;

		currentState.alpha = SWTUtils.getAlpha(gc.getComposite());
		currentState.sx = currentState.sy = 1.0f;

		Rectangle clipBounds = gc.getClipBounds();
		if (clipBounds != null) {
			currentState.relativeClip = new AWTRectangleClipping(clipBounds);
		} else {
			currentState.relativeClip = new AWTRectangleClipping(0, 0, 0, 0);
		}
	}

	/**
	 * @see NativeGraphics#performRotate(float)
	 */
	@Override
	protected boolean performRotate(float degrees) {
		// Flush clipping, patter, etc., before applying transform
		checkGC();
		transform.rotate(degrees * Math.PI / 180);
		gc.setTransform(transform);
		return true;
	}

	/**
	 * @see NativeGraphics#performScale(float, float)
	 */
	@Override
	protected boolean performScale(float horizontal, float vertical) {
		// Flush any clipping before scaling
		checkGC();
		transform.scale(horizontal, vertical);
		gc.setTransform(transform);
		return true;
	}

	/**
	 * @see NativeGraphics#performShear(float, float)
	 */
	@Override
	protected boolean performShear(float horz, float vert) {
		// Flush any clipping changes before shearing
		checkGC();
		transform.shear(horz, vert);
		gc.setTransform(transform);
		return false;
	}

	/**
	 * @see NativeGraphics#performTranslate(float, float)
	 */
	@Override
	protected boolean performTranslate(float dx, float dy) {
		checkGC();
		transform.translate(dx, dy);
		gc.setTransform(transform);
		return true;
	}

	/**
	 * @see NativeGraphics#performTranslate(int, int)
	 */
	@Override
	protected boolean performTranslate(int dx, int dy) {
		checkGC();
		transform.translate(dx, dy);
		gc.setTransform(transform);
		return true;
	}

	private void reconcileHints(int hints, int applied) {
		int changes = hints ^ applied;

		if ((changes & XOR_MASK) != 0) {
			if ((hints & XOR_MASK) != 0) {
				gc.setXORMode(Color.WHITE);
			} else {
				// XOR mode can't be cleared by calling setXORMode(null)
				gc.setPaintMode();
			}
		}

		changes &= ~XOR_MASK;
		// Check to see if there is anything remaining
		if (changes != 0) {
			if ((changes & INTERPOLATION_MASK) != 0) {
				int key = ((hints & INTERPOLATION_MASK) >> INTERPOLATION_SHIFT) - INTERPOLATION_WHOLE_NUMBER;
				if (key == SWT.NONE) {
					gc.getRenderingHints().remove(RenderingHints.KEY_INTERPOLATION);
				} else {
					gc.setRenderingHint(RenderingHints.KEY_INTERPOLATION, AWTUtils.getInterpolation(key));
				}
			}

			if ((changes & AA_MASK) != 0) {
				int key = ((hints & AA_MASK) >> AA_SHIFT) - AA_WHOLE_NUMBER;
				gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, AWTUtils.getAntialias(key));
			}

			if ((changes & TEXT_AA_MASK) != 0) {
				int key = ((hints & TEXT_AA_MASK) >> TEXT_AA_SHIFT) - AA_WHOLE_NUMBER;
				gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, AWTUtils.getTextAntialias(key));
			}
		}
	}

	/**
	 * @see NativeGraphics#setAffineMatrix(float[])
	 */
	@Override
	protected void setAffineMatrix(float[] m) {
		currentState.affineMatrix = m;
		if (m != null) {
			transform.setTransform(m[0], m[1], m[2], m[3], m[4], m[5]);
		} else if (transform != null) {
			transform.setToIdentity();
			elementsNeedUpdate = false;
		}
		gc.setTransform(transform);
	}

	/**
	 * @see Graphics#setAlpha(int)
	 */
	@Override
	public void setAlpha(int alpha) {
		if (currentState.alpha != alpha) {
			currentState.alpha = alpha;
			gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255.0f));
		}
	}

	/**
	 * Needs to be partially implemented as it is called by
	 * {@link NativeGraphics#restoreState(State)}.
	 *
	 * @see Graphics#setBackgroundPattern(Pattern)
	 */
	@Override
	public void setBackgroundPattern(Pattern pattern) {
		if (pattern != null) {
			super.setBackgroundPattern(pattern);
		}
		currentState.bgPattern = pattern;
	}

	/**
	 * @see Graphics#setClip(org.eclipse.draw2d.geometry.Rectangle)
	 */
	@Override
	public void setClip(org.eclipse.draw2d.geometry.Rectangle r) {
		currentState.relativeClip = new AWTRectangleClipping(r);
	}

	/**
	 *
	 * Needs to be partially implemented as it is called by
	 * {@link NativeGraphics#restoreState(State)}.
	 *
	 * @see Graphics#setForegroundPattern(Pattern)
	 */
	@Override
	public void setForegroundPattern(Pattern pattern) {
		if (pattern != null) {
			super.setForegroundPattern(pattern);
		}
		currentState.fgPattern = pattern;
	}
}
