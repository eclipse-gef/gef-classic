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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Image;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PointList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Document;

/**
 * The {@link SVGGraphics} class transforms the contents of a
 * {@link FigureCanvas} into an SVG. Use the {@link #stream(Writer)} method to
 * export the XML structure.
 */
public class SVGGraphics extends Graphics {
	private static final int DPI = Toolkit.getDefaultToolkit().getScreenResolution();
	private final Rectangle documentSize;
	private final SVGGraphics2D gc;
	private final Deque<State> stack;
	private State currentState;

	/**
	 * Constructs a new SWTGraphics that draws to the Canvas using the given GC.
	 */
	public SVGGraphics() {
		gc = new SVGGraphics2D(createDocument());
		stack = new ArrayDeque<>();
		currentState = new State();
		documentSize = new Rectangle();
		init();
	}

	private void init() {
		currentState.setBackgroundColor(awt2swt(gc.getBackground()));
		currentState.setForegroundColor(awt2swt(gc.getColor()));
	}

	private static Color awt2swt(java.awt.Color color) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	// #################################################################################################################
	//
	// GC
	//
	// #################################################################################################################

	private void checkGC() {
		gc.setTransform(currentState.affineTransform);
	}

	private void checkGCPaint() {
		checkGC();
		gc.setColor(currentState.fgColor);
		gc.setBackground(currentState.bgColor);
	}

	private void checkGCText() {
		checkGCPaint();
		gc.setFont(currentState.font);
	}

	private void checkGCFill() {
		checkGC();
		gc.setColor(currentState.bgColor);
	}

	private void updateSize(double x, double y, double width, double height) {
		updateSize(new Rectangle2D.Double(x, y, width, height));
	}

	private void updateSize(Shape shape) {
		Shape transformed = currentState.affineTransform.createTransformedShape(shape);
		documentSize.add(transformed.getBounds2D());
		gc.setSVGCanvasSize(documentSize.getSize());
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

	private void doDrawString(String s, int x, int y, boolean fill) {
		checkGCText();
		Rectangle2D bounds = gc.getFontMetrics().getStringBounds(s, gc);
		if (fill) {
			gc.setColor(currentState.bgColor);
			gc.fillRect(x, y, (int) Math.round(bounds.getWidth()), (int) Math.round(bounds.getHeight()));
			gc.setColor(currentState.fgColor);
		}
		updateSize(x, y, bounds.getWidth(), bounds.getHeight());
		gc.drawString(s, Math.round((x - bounds.getX())), Math.round(y - bounds.getY()));

	}

	/**
	 * @see org.eclipse.draw2d.Graphics#drawText(java.lang.String, int, int)
	 */
	@Override
	public void drawText(String s, int x, int y) {
		throw new NotImplementedException();
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#fillText(java.lang.String, int, int)
	 */
	@Override
	public void fillText(String s, int x, int y) {
		throw new NotImplementedException();
	}

	// #################################################################################################################
	//
	// Rectangle
	//
	// #################################################################################################################

	/**
	 * @see org.eclipse.draw2d.Graphics#drawRectangle(int, int, int, int)
	 */
	@Override
	public void drawRectangle(int x, int y, int width, int height) {
		checkGCPaint();
		Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
		updateSize(rect);
		gc.draw(rect);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#fillRectangle(int, int, int, int)
	 */
	@Override
	public void fillRectangle(int x, int y, int width, int height) {
		checkGCFill();
		Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
		updateSize(rect);
		gc.fill(rect);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#drawRoundRectangle(org.eclipse.draw2d.geometry.Rectangle,
	 *      int, int)
	 */
	@Override
	public void drawRoundRectangle(org.eclipse.draw2d.geometry.Rectangle r, int arcWidth, int arcHeight) {
		checkGCPaint();
		RoundRectangle2D rect = new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, arcWidth, arcHeight);
		updateSize(rect);
		gc.draw(rect);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#fillRoundRectangle(org.eclipse.draw2d.geometry.Rectangle,
	 *      int, int)
	 */
	@Override
	public void fillRoundRectangle(org.eclipse.draw2d.geometry.Rectangle r, int arcWidth, int arcHeight) {
		checkGCFill();
		RoundRectangle2D rect = new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, arcWidth, arcHeight);
		updateSize(rect);
		gc.fill(rect);
	}

	// #################################################################################################################
	//
	// Arc
	//
	// #################################################################################################################

	/**
	 * @see org.eclipse.draw2d.Graphics#drawArc(int, int, int, int, int, int)
	 */
	@Override
	public void drawArc(int x, int y, int w, int h, int offset, int length) {
		checkGCPaint();
		Arc2D arc = new Arc2D.Double(x, y, w, h, offset, length, Arc2D.OPEN);
		updateSize(arc);
		gc.draw(arc);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#fillArc(int, int, int, int, int, int)
	 */
	@Override
	public void fillArc(int x, int y, int w, int h, int offset, int length) {
		checkGCFill();
		Arc2D arc = new Arc2D.Double(x, y, w, h, offset, length, Arc2D.PIE);
		updateSize(arc);
		gc.fill(arc);
	}

	// #################################################################################################################
	//
	// Oval
	//
	// #################################################################################################################

	/**
	 * @see org.eclipse.draw2d.Graphics#drawOval(int, int, int, int)
	 */
	@Override
	public void drawOval(int x, int y, int w, int h) {
		checkGCPaint();
		Ellipse2D oval = new Ellipse2D.Double(x, y, w, h);
		updateSize(oval);
		gc.draw(oval);
	}

	/**
	 * @see org.eclipse.draw2d.Graphics#fillOval(int, int, int, int)
	 */
	@Override
	public void fillOval(int x, int y, int w, int h) {
		checkGCFill();
		Ellipse2D oval = new Ellipse2D.Double(x, y, w, h);
		updateSize(oval);
		gc.fill(oval);
	}

	// #################################################################################################################
	//
	// Line
	//
	// #################################################################################################################

	/**
	 * @see org.eclipse.draw2d.Graphics#drawLine(int, int, int, int)
	 */
	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		checkGCPaint();
		Line2D line = new Line2D.Double(x1, y1, x2, y2);
		updateSize(line);
		gc.draw(line);
	}

	// #################################################################################################################
	//
	// SVG
	//
	// #################################################################################################################

	/**
	 * @return the factory which will produce Elements for the DOM tree this
	 *         Graphics generates.
	 */
	@SuppressWarnings("static-method") // may be overridden by subclasses
	protected Document createDocument() {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			return documentBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param writer used to writer out the SVG content
	 */
	public void stream(Writer writer) throws IOException {
		gc.stream(writer);
	}

	// #################################################################################################################
	//
	// State
	//
	// #################################################################################################################

	@Override
	public void setBackgroundColor(Color rgb) {
		currentState.setBackgroundColor(rgb);
	}

	@Override
	public Color getBackgroundColor() {
		return currentState.swtBgColor;
	}

	@Override
	public void setForegroundColor(Color rgb) {
		currentState.setForegroundColor(rgb);
	}

	@Override
	public Color getForegroundColor() {
		return currentState.swtFgColor;
	}

	@Override
	public void setFont(Font f) {
		currentState.setFont(f);
	}

	@Override
	public Font getFont() {
		return currentState.swtFont;
	}

	@Override
	public void translate(int dx, int dy) {
		if (currentState.affineTransform == null) {
			currentState.affineTransform = new AffineTransform();
		}
		currentState.affineTransform.translate(dx, dy);
	}

	@Override
	public void scale(double amount) {
		scale((float) amount, (float) amount);
	}

	@Override
	public void scale(float horizontal, float vertical) {
		currentState.affineTransform.scale(horizontal, vertical);
	}

	@Override
	public void pushState() {
		stack.addFirst(currentState);
		currentState = currentState.getCopy();
	}

	@Override
	public void popState() {
		stack.removeFirst();
	}

	@Override
	public void restoreState() {
		currentState = stack.peekFirst();
	}

	/**
	 * Contains the entire state of the Graphics.
	 */
	static class State extends LazyState implements Cloneable {
		public State getCopy() {
			State copy = new State();
			copy.bgColor = bgColor;
			copy.fgColor = fgColor;
			copy.font = font;
			copy.swtBgColor = swtBgColor;
			copy.swtFgColor = swtFgColor;
			copy.swtFont = swtFont;
			copy.affineTransform = (AffineTransform) affineTransform.clone();
			return copy;
		}
	}

	/**
	 * Any state stored in this class is only applied when it is needed by a
	 * specific graphics call.
	 */
	static class LazyState {
		java.awt.Font font;
		Font swtFont;

		java.awt.Color bgColor;
		Color swtBgColor;

		java.awt.Color fgColor;
		Color swtFgColor;

		AffineTransform affineTransform = new AffineTransform();

		void setFont(Font swtFont) {
			this.swtFont = swtFont;
			this.font = swt2awt(swtFont);
		}

		void setBackgroundColor(Color swtBgColor) {
			this.swtBgColor = swtBgColor;
			this.bgColor = swt2awt(swtBgColor);
		}

		void setForegroundColor(Color swtFgColor) {
			this.swtFgColor = swtFgColor;
			this.fgColor = swt2awt(swtFgColor);
		}

		private static java.awt.Font swt2awt(Font font) {
			if (font == null) {
				return null;
			}
			FontData fd = font.getFontData()[0];
			int height = fd.getHeight();
			if (DPI != 72) {
				height = height * DPI / 72;
			}
			return new java.awt.Font(fd.getName(), fd.getStyle(), height);
		}

		private static java.awt.Color swt2awt(Color color) {
			if (color == null) {
				return null;
			}
			return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		}
	}

	// #################################################################################################################
	//
	// ...
	//
	// #################################################################################################################

	@Override
	public void clipRect(org.eclipse.draw2d.geometry.Rectangle r) {
		throw new NotImplementedException();
	}

	@Override
	public void drawFocus(int x, int y, int w, int h) {
		throw new NotImplementedException();
	}

	@Override
	public void drawImage(Image srcImage, int x, int y) {
		throw new NotImplementedException();
	}

	@Override
	public void drawImage(Image srcImage, int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		throw new NotImplementedException();
	}

	@Override
	public void drawPolygon(PointList points) {
		throw new NotImplementedException();
	}

	@Override
	public void drawPolyline(PointList points) {
		throw new NotImplementedException();
	}

	@Override
	public void fillGradient(int x, int y, int w, int h, boolean vertical) {
		throw new NotImplementedException();
	}

	@Override
	public void fillPolygon(PointList points) {
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
	public int getLineStyle() {
		throw new NotImplementedException();
	}

	@Override
	public int getLineWidth() {
		throw new NotImplementedException();
	}

	@Override
	public float getLineWidthFloat() {
		throw new NotImplementedException();
	}

	@Override
	public boolean getXORMode() {
		throw new NotImplementedException();
	}

	@Override
	public void setClip(org.eclipse.draw2d.geometry.Rectangle r) {
		throw new NotImplementedException();
	}

	@Override
	public void setLineStyle(int style) {
		throw new NotImplementedException();
	}

	@Override
	public void setLineWidth(int width) {
		throw new NotImplementedException();
	}

	@Override
	public void setLineWidthFloat(float width) {
		throw new NotImplementedException();
	}

	@Override
	public void setLineMiterLimit(float miterLimit) {
		throw new NotImplementedException();
	}

	@Override
	public void setXORMode(boolean b) {
		throw new NotImplementedException();
	}

	private class NotImplementedException extends RuntimeException {
		public NotImplementedException() {
			super("The class: %s has not implemented this new graphics function" //$NON-NLS-1$
					.formatted(SVGGraphics.this.getClass()));
		}
	}
}
