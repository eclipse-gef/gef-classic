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

package org.eclipse.draw2d.test.utils;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * Dummy implementation of the Swing Graphics2D class where every method throws
 * an {@link UnsupportedOperationException}.
 */
public class Graphics2DStub extends Graphics2D {

	@Override
	public void draw(Shape s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawString(String str, int x, int y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawString(String str, float x, float y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void fill(Shape s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		throw new UnsupportedOperationException();
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setComposite(Composite comp) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPaint(Paint paint) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStroke(Stroke s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		throw new UnsupportedOperationException();
	}

	@Override
	public RenderingHints getRenderingHints() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void translate(int x, int y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void translate(double tx, double ty) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rotate(double theta) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rotate(double theta, double x, double y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void scale(double sx, double sy) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void shear(double shx, double shy) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void transform(AffineTransform Tx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTransform(AffineTransform Tx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AffineTransform getTransform() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Paint getPaint() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Composite getComposite() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBackground(Color color) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Color getBackground() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Stroke getStroke() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clip(Shape s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Graphics create() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Color getColor() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setColor(Color c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPaintMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setXORMode(Color c1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Font getFont() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFont(Font font) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Rectangle getClipBounds() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Shape getClip() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setClip(Shape clip) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispose() {
		throw new UnsupportedOperationException();
	}
}
