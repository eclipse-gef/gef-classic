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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.ImageObserver;

import org.eclipse.swt.graphics.Path;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.internal.AWTUtils;
import org.eclipse.draw2d.internal.SWTUtils;

/**
 * Wrapper for the Swing {@link Graphics2D} class which delegates all relevant
 * method calls to our Draw2D {@link Graphics} instance.
 */
public class Graphics2DToGraphicsAdapter extends Graphics2DStub {
	private static org.eclipse.draw2d.geometry.PointList asDraw2DPointList(Polygon polygon) {
		org.eclipse.draw2d.geometry.PointList pointList = new org.eclipse.draw2d.geometry.PointList();
		for (int i = 0; i < polygon.npoints; ++i) {
			pointList.addPoint(polygon.xpoints[i], polygon.ypoints[i]);
		}
		return pointList;
	}

	private static org.eclipse.draw2d.geometry.Rectangle asDraw2DRectangle(RectangularShape shape) {
		Rectangle bounds = shape.getBounds();
		return new org.eclipse.draw2d.geometry.Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	private final Graphics g;
	private final RenderingHints renderingHints = new RenderingHints(null);
	private AffineTransform transform;
	private Paint paint;

	public Graphics2DToGraphicsAdapter(Graphics g) {
		this.g = g;
	}

	@Override
	public void dispose() {
		// noop
	}

	@Override
	public void draw(Shape s) {
		switch (s) {
		case Arc2D arc -> drawArc(arc);
		case Line2D line -> drawLine(line);
		case Ellipse2D ellipse -> drawOval(ellipse);
		case Rectangle2D rectangle -> drawRectangle(rectangle);
		case RoundRectangle2D roundRectangle -> drawRoundRectangle(roundRectangle);
		case Polygon polygon -> drawPolygon2(polygon);
		case Path2D path -> drawPath(path);
		default -> throw new UnsupportedOperationException(s.getClass().toString());
		}
	}

	private void drawArc(Arc2D arc) {
		g.drawArc(asDraw2DRectangle(arc), (int) arc.getAngleStart(), (int) arc.getAngleExtent());
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		g.drawImage(null, x, y);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			ImageObserver observer) {
		g.drawImage(null, dx1, dy1, sx1, sy1, dx2, dy2, sx2, sy2);
		return true;
	}

	private void drawLine(Line2D line) {
		g.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
	}

	private void drawOval(Ellipse2D ellipse) {
		g.drawOval(asDraw2DRectangle(ellipse));
	}

	private void drawPath(Path2D path) {
		Path p = SWTUtils.getPath(path, null);
		g.drawPath(p);
		p.dispose();
	}

	private void drawPolygon2(Polygon polygon) {
		g.drawPolygon(asDraw2DPointList(polygon).toIntArray());
	}

	private void drawRectangle(Rectangle2D rect) {
		g.drawRectangle(asDraw2DRectangle(rect));
	}

	private void drawRoundRectangle(RoundRectangle2D rect) {
		g.drawRoundRectangle(asDraw2DRectangle(rect), (int) rect.getArcWidth(), (int) rect.getArcHeight());
	}

	@Override
	public void fill(Shape s) {
		switch (s) {
		case Arc2D arc -> fillArc(arc);
		case Ellipse2D ellipse -> fillOval(ellipse);
		case Rectangle2D rectangle -> fillRectangle(rectangle);
		case RoundRectangle2D roundRectangle -> fillRoundRectangle(roundRectangle);
		case Polygon polygon -> fillPolygon2(polygon);
		case Path2D path -> fillPath(path);
		default -> throw new UnsupportedOperationException(s.getClass().toString());
		}
	}

	private void fillArc(Arc2D arc) {
		g.fillArc(asDraw2DRectangle(arc), (int) arc.getAngleStart(), (int) arc.getAngleExtent());
	}

	private void fillOval(Ellipse2D ellipse) {
		g.fillOval(asDraw2DRectangle(ellipse));
	}

	private void fillPath(Path2D path) {
		Path p = SWTUtils.getPath(path, null);
		g.fillPath(p);
		p.dispose();
	}

	private void fillPolygon2(Polygon polygon) {
		g.fillPolygon(asDraw2DPointList(polygon).toIntArray());
	}

	private void fillRectangle(Rectangle2D rect) {
		g.fillRectangle(asDraw2DRectangle(rect));
	}

	private void fillRoundRectangle(RoundRectangle2D rect) {
		g.fillRoundRectangle(asDraw2DRectangle(rect), (int) rect.getArcWidth(), (int) rect.getArcHeight());
	}

	@Override
	public Color getBackground() {
		return AWTUtils.getColor(g.getBackgroundColor());
	}

	@Override
	public Rectangle getClipBounds() {
		org.eclipse.draw2d.geometry.Rectangle clipRect = g.getClip(new org.eclipse.draw2d.geometry.Rectangle());
		return new Rectangle(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
	}

	@Override
	public Color getColor() {
		return AWTUtils.getColor(g.getForegroundColor());
	}

	@Override
	public Composite getComposite() {
		int alpha = g.getAlpha();
		if (alpha == 255) {
			return null;
		}
		return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255.0f);
	}

	@Override
	public Font getFont() {
		return AWTUtils.getFont(g.getFont(), Toolkit.getDefaultToolkit().getScreenResolution());
	}

	@Override
	public Paint getPaint() {
		return paint;
	}

	@Override
	public RenderingHints getRenderingHints() {
		return renderingHints;
	}

	@Override
	public Stroke getStroke() {
		return AWTUtils.getStroke(g.getLineAttributes());
	}

	@Override
	public AffineTransform getTransform() {
		return transform;
	}

	@Override
	public void setBackground(Color c) {
		g.setBackgroundColor(SWTUtils.getColor(c));
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		g.setClip(new org.eclipse.draw2d.geometry.Rectangle(x, y, width, height));
	}

	@Override
	public void setColor(Color c) {
		g.setForegroundColor(SWTUtils.getColor(c));
	}

	@Override
	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	@Override
	public void setTransform(AffineTransform tx) {
		this.transform = tx;
	}
}
