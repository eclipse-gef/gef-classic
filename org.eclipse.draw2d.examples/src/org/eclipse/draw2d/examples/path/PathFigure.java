/*******************************************************************************
 * Copyright (c) 2008, 2025 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stephane Lizeray slizeray@ilog.fr - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.examples.path;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

public class PathFigure extends Polyline {

	{
		setFill(true);
	}

	private boolean isClosed;

	private float degrees;

	private float cx, cy;

	@Override
	protected void outlineShape(Graphics g) {
		drawShape(g, false);

	}

	@Override
	protected void fillShape(Graphics g) {
		drawShape(g, true);

	}

	private void drawShape(Graphics g, boolean fill) {
		Path path = createPath();
		g.pushState();
		g.setAntialias(SWT.ON);
		if (fill) {
			g.fillPath(path);
		} else {
			g.drawPath(path);
		}
		g.rotate(degrees);
		g.setForegroundColor(ColorConstants.black);

		double angle = degreesToRadians(degrees);
		double cos = Math.cos(angle), sin = Math.sin(angle);

		TextLayout textLayout = new TextLayout(Display.getDefault());
		textLayout.setFont(g.getFont());
		textLayout.setText("zoom"); //$NON-NLS-1$
		TextStyle textStyle = new TextStyle();
		textStyle.underline = true;
		textStyle.underlineColor = ColorConstants.blue;
		textLayout.setStyle(textStyle, 1, 2);
		Point p = getRotatedPoint(getBounds().x, getBounds().y - 20, cos, sin);
		g.drawTextLayout(textLayout, p.x, p.y, 0, 1, ColorConstants.white, ColorConstants.yellow);

		g.popState();
		path.dispose();

	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setRotation(float cx, float cy, float degrees) {
		this.degrees = degrees;
		this.cx = cx;
		this.cy = cy;
		bounds = null;
		repaint();
	}

	public float getRotationAngle() {
		return degrees;

	}

	public Point getRotationCenter() {
		return new PrecisionPoint(cx, cy);
	}

	private Path createPath() {
		PointList points = getPoints();

		Path path = new Path(Display.getCurrent());

		double angle = degreesToRadians(degrees);
		double cos = Math.cos(angle), sin = Math.sin(angle);

		PrecisionPoint p1 = getRotatedPoint(points.getFirstPoint().x(), points.getFirstPoint().y(), cos, sin);
		path.moveTo((float) p1.preciseX(), (float) p1.preciseY());
		for (int i = 1; i < points.size(); i++) {
			PrecisionPoint p = getRotatedPoint(points.getPoint(i).x(), points.getPoint(i).y(), cos, sin);
			path.lineTo((float) p.preciseX(), (float) p.preciseY());
		}
		if (isClosed()) {
			path.close();
		}

		return path;

	}

	public PrecisionPoint getRotatedPoint(float x, float y, double cos, double sin) {

		PrecisionPoint p = new PrecisionPoint(x, y);
		if (degrees == 0) {
			return p;
		}

		p.setPreciseX(p.preciseX() - cx);
		p.setPreciseY(p.preciseY() - cy);

		float x1 = (float) ((p.preciseX() * cos) - (p.preciseY() * sin));
		float y1 = (float) ((p.preciseY() * cos) + (p.preciseX() * sin));

		p.setPreciseX(x1 + cx);
		p.setPreciseY(y1 + cy);
		return p;
	}

	@Override
	public Rectangle getBounds() {
		if (bounds == null) {
			bounds = new Rectangle();
			if (getPoints().size() > 0) {
				PointList points = getPoints();
				double angle = degreesToRadians(degrees);
				double cos = Math.cos(angle), sin = Math.sin(angle);
				PrecisionPoint p1 = getRotatedPoint(points.getFirstPoint().x(), points.getFirstPoint().y(), cos, sin);
				bounds.setLocation(p1);
				for (int i = 1; i < points.size(); i++) {
					PrecisionPoint p = getRotatedPoint(points.getPoint(i).x(), points.getPoint(i).y(), cos, sin);
					bounds.union(p);
				}
			}
		}
		return bounds.getExpanded(getLineWidth() / 2, getLineWidth() / 2);
	}

	static private double degreesToRadians(double angle) {
		return (angle * Math.PI) / 180;
	}

}
