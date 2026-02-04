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

package org.eclipse.draw2d.svg;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Image;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.svg.internal.AwtUtils;
import org.eclipse.draw2d.svg.internal.RenderingHintsHolder;
import org.eclipse.draw2d.svg.internal.StrokeHolder;
import org.eclipse.draw2d.svg.internal.SwtUtils;

/**
 * The {@link AWTGraphics} class paints to a {@link Component}.
 */
public class AWTGraphics extends Graphics {
	private static final int DPI = Toolkit.getDefaultToolkit().getScreenResolution();
	/* package */ final Graphics2D gc;
	/* package */ State currentState;
	private final Deque<State> stack;
	private final State appliedState;

	public AWTGraphics(Graphics2D gc) {
		this.gc = gc;
		this.appliedState = new State();
		this.currentState = new State();
		this.stack = new ArrayDeque<>();
		init();
	}

	private void init() {
		currentState.bgColor = SwtUtils.getColor(gc.getBackground());
		currentState.fgColor = SwtUtils.getColor(gc.getColor());
	}

	// #################################################################################################################
	//
	// GC
	//
	// #################################################################################################################

	private void checkGC() {
		if (!Objects.equals(currentState.affineTransform, appliedState.affineTransform)) {
			gc.setTransform(currentState.affineTransform);
			appliedState.affineTransform = (AffineTransform) currentState.affineTransform.clone();
		}
		if (!Objects.equals(currentState.renderingHints, appliedState.renderingHints)) {
			gc.setRenderingHints(currentState.renderingHints.build());
			appliedState.renderingHints = currentState.renderingHints.clone();
		}
	}

	private void checkGCPaint() {
		checkGC();
		if (!Objects.equals(currentState.stroke, appliedState.stroke)) {
			gc.setStroke(currentState.stroke.build());
		}
		if (!Objects.equals(currentState.fgColor, appliedState.fgColor)) {
			gc.setColor(AwtUtils.getColor(currentState.fgColor));
		}
		if (!Objects.equals(currentState.bgColor, appliedState.fgColor)) {
			gc.setBackground(AwtUtils.getColor(currentState.bgColor));
		}
	}

	private void checkGCText() {
		checkGCPaint();
		if (!Objects.equals(currentState.font, appliedState.font)) {
			gc.setFont(AwtUtils.getFont(currentState.font, getDPI()));
			appliedState.font = currentState.font;
		}
	}

	private void checkGCFill() {
		checkGC();
		if (!Objects.equals(currentState.bgColor, appliedState.bgColor)) {
			gc.setColor(AwtUtils.getColor(currentState.bgColor));
			appliedState.bgColor = currentState.bgColor;
		}
	}

	@SuppressWarnings("static-method")
	/* package */ int getDPI() {
		return DPI;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#dispose()
	 */
	@Override
	public void dispose() {
		gc.dispose();
	}

	// #################################################################################################################
	//
	// String
	//
	// #################################################################################################################

	/**
	 * @see org.eclipse.draw2d.Graphics#drawString(java.lang.String, int, int)
	 */
	@Override
	public void drawString(String s, int x, int y) {
		doDrawString(s, x, y, false);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#fillString(java.lang.String, int, int)
	 */
	@Override
	public void fillString(String s, int x, int y) {
		doDrawString(s, x, y, true);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#drawText(java.lang.String, int, int)
	 */
	@Override
	public void drawText(String s, int x, int y) {
		drawString(s, x, y);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#fillText(java.lang.String, int, int)
	 */
	@Override
	public void fillText(String s, int x, int y) {
		fillString(s, x, y);
	}

	private void doDrawString(String s, int x, int y, boolean fill) {
		checkGCText();
		Rectangle2D bounds = gc.getFontMetrics().getStringBounds(s, gc);
		doDrawString(s, x, y, bounds, fill);
	}

	/* package */ void doDrawString(String s, int x, int y, Rectangle2D bounds, boolean fill) {
		if (fill) {
			java.awt.Color bgColor = gc.getBackground();
			java.awt.Color fgColor = gc.getColor();
			gc.setColor(bgColor);
			gc.fillRect(x, y, (int) Math.round(bounds.getWidth()), (int) Math.round(bounds.getHeight()));
			gc.setColor(fgColor);
		}
		gc.drawString(s, Math.round((x - bounds.getX())), Math.round(y - bounds.getY()));
	}

	// #################################################################################################################
	//
	// Shape
	//
	// #################################################################################################################

	/**
	 * @see org.eclipse.draw2d.Graphics#drawRectangle(int, int, int, int)
	 */
	@Override
	public void drawRectangle(int x, int y, int width, int height) {
		checkGCPaint();
		doDrawShape(new Rectangle2D.Double(x, y, width, height));
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#fillRectangle(int, int, int, int)
	 */
	@Override
	public void fillRectangle(int x, int y, int width, int height) {
		checkGCFill();
		doFillShape(new Rectangle2D.Double(x, y, width, height));
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#drawRoundRectangle(org.eclipse.draw2d.geometry.Rectangle,
	 *      int, int)
	 */
	@Override
	public void drawRoundRectangle(org.eclipse.draw2d.geometry.Rectangle r, int arcWidth, int arcHeight) {
		checkGCPaint();
		doDrawShape(new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, arcWidth, arcHeight));
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#fillRoundRectangle(org.eclipse.draw2d.geometry.Rectangle,
	 *      int, int)
	 */
	@Override
	public void fillRoundRectangle(org.eclipse.draw2d.geometry.Rectangle r, int arcWidth, int arcHeight) {
		checkGCFill();
		doFillShape(new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, arcWidth, arcHeight));
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#drawArc(int, int, int, int, int, int)
	 */
	@Override
	public void drawArc(int x, int y, int w, int h, int offset, int length) {
		checkGCPaint();
		doDrawShape(new Arc2D.Double(x, y, w, h, offset, length, Arc2D.OPEN));
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#fillArc(int, int, int, int, int, int)
	 */
	@Override
	public void fillArc(int x, int y, int w, int h, int offset, int length) {
		checkGCFill();
		doFillShape(new Arc2D.Double(x, y, w, h, offset, length, Arc2D.PIE));
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#drawOval(int, int, int, int)
	 */
	@Override
	public void drawOval(int x, int y, int w, int h) {
		checkGCPaint();
		doDrawShape(new Ellipse2D.Double(x, y, w, h));
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#fillOval(int, int, int, int)
	 */
	@Override
	public void fillOval(int x, int y, int w, int h) {
		checkGCFill();
		doFillShape(new Ellipse2D.Double(x, y, w, h));
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#drawLine(int, int, int, int)
	 */
	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		checkGCPaint();
		doDrawShape(new Line2D.Double(x1, y1, x2, y2));
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#drawPolygon(org.eclipse.draw2d.geometry.PointList)
	 */
	@Override
	public void drawPolygon(PointList points) {
		checkGCPaint();
		doDrawShape(AwtUtils.getPolygon(points));
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#fillPolygon(org.eclipse.draw2d.geometry.PointList)
	 */
	@Override
	public void fillPolygon(PointList points) {
		checkGCPaint();
		java.awt.Color bgColor = gc.getBackground();
		java.awt.Color fgColor = gc.getColor();
		gc.setColor(bgColor);
		doFillShape(AwtUtils.getPolygon(points));
		gc.setColor(fgColor);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#drawPolyline(org.eclipse.draw2d.geometry.PointList)
	 */
	@Override
	public void drawPolyline(PointList points) {
		checkGCPaint();
		doDrawShape(AwtUtils.getPolyline(points));
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#fillGradient(int, int, int, int, boolean)
	 */
	@Override
	public void fillGradient(int x, int y, int w, int h, boolean vertical) {
		checkGCPaint();
		Rectangle2D r = new Rectangle2D.Double(x, y, w, h);
		Paint oldPaint = gc.getPaint();
		gc.setPaint(AwtUtils.getGradientPaint(r, gc.getColor(), gc.getBackground(), vertical));
		doFillShape(r);
		gc.setPaint(oldPaint);
	}

	/* package */ void doDrawShape(Shape shape) {
		gc.draw(shape);
	}

	/* package */ void doFillShape(Shape shape) {
		gc.fill(shape);
	}

	// #################################################################################################################
	//
	// State
	//
	// #################################################################################################################

	/**
	 * @see org.eclipse.draw2d.Graphics#setBackgroundColor(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setBackgroundColor(Color rgb) {
		currentState.bgColor = rgb;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#getBackgroundColor()
	 */
	@Override
	public Color getBackgroundColor() {
		return currentState.bgColor;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#setForegroundColor(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setForegroundColor(Color rgb) {
		currentState.fgColor = rgb;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#getForegroundColor()
	 */
	@Override
	public Color getForegroundColor() {
		return currentState.fgColor;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#setFont(org.eclipse.swt.graphics.Font)
	 */
	@Override
	public void setFont(Font f) {
		currentState.font = f;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#getFont()
	 */
	@Override
	public Font getFont() {
		return currentState.font;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#pushState()
	 */
	@Override
	public void pushState() {
		stack.addFirst(currentState);
		currentState = currentState.clone();
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#popState()
	 */
	@Override
	public void popState() {
		stack.removeFirst();
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#restoreState()
	 */
	@Override
	public void restoreState() {
		currentState = stack.peekFirst();
	}

	/**
	 * Contains the entire state of the Graphics.
	 */
	static class State extends LazyState implements Cloneable {
		@Override
		public State clone() {
			try {
				State copy = (State) super.clone();
				copy.stroke = stroke.clone();
				copy.affineTransform = (AffineTransform) affineTransform.clone();
				copy.renderingHints = renderingHints.clone();
				return copy;
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Any state stored in this class is only applied when it is needed by a
	 * specific graphics call.
	 */
	static class LazyState {
		Font font;
		Color bgColor;
		Color fgColor;

		StrokeHolder stroke = new StrokeHolder();
		RenderingHintsHolder renderingHints = new RenderingHintsHolder();

		AffineTransform affineTransform = new AffineTransform();
	}

	// #################################################################################################################
	//
	// Stroke
	//
	// #################################################################################################################

	/**
	 * @see org.eclipse.draw2d.Graphics#setLineWidth(int)
	 */
	@Override
	public final void setLineWidth(int width) {
		setLineWidthFloat(width);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#getLineWidth()
	 */
	@Override
	public final int getLineWidth() {
		return Math.round(getLineWidthFloat());
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#setLineWidthFloat(float)
	 */
	@Override
	public void setLineWidthFloat(float width) {
		currentState.stroke.width = width;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#getLineWidthFloat()
	 */
	@Override
	public float getLineWidthFloat() {
		return currentState.stroke.width;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#setLineMiterLimit(float)
	 */
	@Override
	public void setLineMiterLimit(float miterLimit) {
		currentState.stroke.miterLimit = miterLimit;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#getLineMiterLimit()
	 */
	@Override
	public float getLineMiterLimit() {
		return currentState.stroke.miterLimit;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#setLineCap(int)
	 */
	@Override
	public void setLineCap(int cap) {
		currentState.stroke.cap = cap;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#getLineCap()
	 */
	@Override
	public int getLineCap() {
		return currentState.stroke.cap;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#setLineJoin(int)
	 */
	@Override
	public void setLineJoin(int join) {
		currentState.stroke.join = join;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#getLineJoin()
	 */
	@Override
	public int getLineJoin() {
		return currentState.stroke.join;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#setLineDashOffset(float)
	 */
	@Override
	public void setLineDashOffset(float dashPhase) {
		currentState.stroke.dashPhase = dashPhase;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#setLineDash(float[])
	 */
	@Override
	public void setLineDash(float[] dash) {
		currentState.stroke.dashFloat = dash;
		currentState.stroke.dashInt = null;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#setLineDash(int[])
	 */
	@Override
	public final void setLineDash(int[] dash) {
		currentState.stroke.dashFloat = null;
		currentState.stroke.dashInt = dash;
	}

	// #################################################################################################################
	//
	// Transform
	//
	// #################################################################################################################

	/**
	 * @see org.eclipse.draw2d.Graphics#translate(int, int)
	 */
	@Override
	public final void translate(int dx, int dy) {
		translate((float) dx, (float) dy);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#translate(float, float)
	 */
	@Override
	public void translate(float dx, float dy) {
		currentState.affineTransform.translate(dx, dy);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#scale(double)
	 */
	@Override
	public void scale(double amount) {
		scale((float) amount, (float) amount);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#scale(float, float)
	 */
	@Override
	public void scale(float horizontal, float vertical) {
		currentState.affineTransform.scale(horizontal, vertical);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#rotate(float)
	 */
	@Override
	public void rotate(float degrees) {
		currentState.affineTransform.rotate(degrees * Math.PI / 180);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#shear(float, float)
	 */
	@Override
	public void shear(float horz, float vert) {
		currentState.affineTransform.shear(horz, vert);
	}

	// #################################################################################################################
	//
	// Rendering Hints
	//
	// #################################################################################################################

	/**
	 * @see org.eclipse.draw2d.Graphics#setAdvanced(boolean)
	 */
	@Override
	public final void setAdvanced(boolean advanced) {
		// not required for AWT
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#getAdvanced()
	 */
	@Override
	public final boolean getAdvanced() {
		return false;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#setAntialias(int)
	 */
	@Override
	public void setAntialias(int value) {
		currentState.renderingHints.antialias = value;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#getAntialias()
	 */
	@Override
	public int getAntialias() {
		return currentState.renderingHints.antialias;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#setTextAntialias(int)
	 */
	@Override
	public void setTextAntialias(int value) {
		currentState.renderingHints.textAntialias = value;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#getTextAntialias()
	 */
	@Override
	public int getTextAntialias() {
		return currentState.renderingHints.textAntialias;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#setInterpolation(int)
	 */
	@Override
	public void setInterpolation(int value) {
		currentState.renderingHints.interpolation = value;
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#getInterpolation()
	 */
	@Override
	public int getInterpolation() {
		return currentState.renderingHints.interpolation;
	}

	// #################################################################################################################
	//
	// Image
	//
	// #################################################################################################################

	/**
	 * @see org.eclipse.draw2d.Graphics#drawImage(org.eclipse.swt.graphics.Image,
	 *      int, int)
	 */
	@Override
	public void drawImage(Image srcImage, int x, int y) {
		gc.drawImage(AwtUtils.getImage(srcImage), x, y, null);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#drawImage(org.eclipse.swt.graphics.Image,
	 *      int, int, int, int)
	 */
	@Override
	public void drawImage(Image srcImage, int x, int y, int width, int height) {
		gc.drawImage(AwtUtils.getImage(srcImage), x, y, width, height, null);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#drawImage(org.eclipse.swt.graphics.Image,
	 *      int, int, int, int, int, int, int, int)
	 */
	@Override
	public void drawImage(Image srcImage, int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		gc.drawImage(AwtUtils.getImage(srcImage), x2, y2, x2 + w2, y2 + h2, x1, y1, x1 + w1, y1 + h1, null);
	}

	// #################################################################################################################
	//
	// ...
	//
	// #################################################################################################################

	@Override
	public void setLineStyle(int style) {
		throw new NotImplementedException();
	}

	@Override
	public int getLineStyle() {
		throw new NotImplementedException();
	}

	@Override
	public void drawFocus(int x, int y, int w, int h) {
		throw new NotImplementedException();
	}

	@Override
	public void clipRect(org.eclipse.draw2d.geometry.Rectangle r) {
		throw new NotImplementedException();
	}

	@Override
	public void setClip(org.eclipse.draw2d.geometry.Rectangle r) {
		throw new NotImplementedException();
	}

	@Override
	public org.eclipse.draw2d.geometry.Rectangle getClip(org.eclipse.draw2d.geometry.Rectangle rect) {
		throw new NotImplementedException();
	}

	@Override
	public FontMetrics getFontMetrics() {
		throw new NotImplementedException();
	}

	@Override
	public boolean getXORMode() {
		throw new NotImplementedException();
	}

	@Override
	public void setXORMode(boolean b) {
		throw new NotImplementedException();
	}

	private class NotImplementedException extends RuntimeException {
		public NotImplementedException() {
			super("The class: %s has not implemented this new graphics function" //$NON-NLS-1$
					.formatted(AWTGraphics.this.getClass()));
		}
	}
}
