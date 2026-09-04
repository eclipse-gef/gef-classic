/*******************************************************************************
 * Copyright (c) 2025, 2026 Yatta and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Yatta - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d.test.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.PathData;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.Assertions;

public class GraphicsRecorder extends GraphicsStub {
	private final Display display = Display.getDefault();
	private final List<String> events = new ArrayList<>();

	public void assertEquals(GraphicsRecorder other) {
		Assertions.assertEquals(events, other.events);
		events.clear();
		other.events.clear();
	}

	public void assertEmpty() {
		Assertions.assertTrue(events.isEmpty());
	}

	public void log(String pattern, Object... args) {
		events.add(MessageFormat.format(pattern, args));
	}

	public void clear() {
		events.clear();
	}

	// #########################################################################

	private int dx;
	private int dy;
	private final Rectangle clipRect = new Rectangle();

	@Override
	public void translate(int dx, int dy) {
		log("translate(dx={0}, dy={1})", dx, dy); //$NON-NLS-1$
		this.dx += dx;
		this.dy += dy;
	}

	@Override
	public void clipRect(Rectangle rect) {
		log("clipRect(x={0}, y={1}, width={2}, height={3})", rect.x + dx, rect.y + dy, rect.width, rect.height); //$NON-NLS-1$
		clipRect.intersect(rect.getTranslated(dx, dy));
	}

	@Override
	public void setClip(Rectangle rect) {
		log("setClip(x={0}, y={1}, width={2}, height={3})", rect.x + dx, rect.y + dy, rect.width, rect.height); //$NON-NLS-1$
		clipRect.setBounds(rect.getTranslated(dx, dy));
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int offset, int length) {
		log("drawArc(x={0}, y={1}, width={2}, height={3}, offset={4}, length={5})", x + dx, y + dy, width, height, //$NON-NLS-1$
				offset, length);
	}

	@Override
	public void drawPoint(int x, int y) {
		log("drawPoint(x={0}, y={1})", x + dx, y + dy); //$NON-NLS-1$
	}

	@Override
	public void drawFocus(int x, int y, int w, int h) {
		log("drawFocus(x={0}, y={1}, w={2}, h={3})", x + dx, y + dy, w, h); //$NON-NLS-1$
	}

	@Override
	public void drawImage(Image srcImage, int x, int y) {
		log("drawImage(x={0}, y={1})", x + dx, y + dy); //$NON-NLS-1$
	}

	@Override
	public void drawImage(Image srcImage, int x, int y, int w, int h) {
		log("drawImage(x={0}, y={1}, w={2}, h={3})", x + dx, y + dy, w, h); //$NON-NLS-1$
	}

	@Override
	public void drawImage(Image srcImage, int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		log("drawImage(x1={0}, y1={1}, w1={2}, h1={3}, x2={4}, y2={5}, w2={6}, h2={7})", x1, y1, w1, h2, x2 + dx, //$NON-NLS-1$
				y2 + dy, w2, h2);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		log("drawLine(x1={0}, y1={1}, x2={2}, y2={3})", x1 + dx, y1 + dy, x2 + dx, y2 + dy); //$NON-NLS-1$
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		log("drawOval(x={0}, y={1}, width={2}, height={3}, vertical={4})", x + dx, y + dy, width, height); //$NON-NLS-1$
	}

	@Override
	public void drawPath(Path path) {
		PathData pathData = path.getPathData();
		log("drawPath(path=[points={0}, types={1})]", Arrays.toString(pathData.points), //$NON-NLS-1$
				Arrays.toString(pathData.types));
	}

	@Override
	public void drawPolygon(int[] points) {
		log("drawPolygon(points={0})", Arrays.toString(points)); //$NON-NLS-1$
	}

	@Override
	public void drawRectangle(int x, int y, int width, int height) {
		log("drawRectangle(x={0}, y={1}, width={2}, height={3})", x + dx, y + dy, width, height); //$NON-NLS-1$
	}

	@Override
	public void drawRoundRectangle(Rectangle r, int arcWidth, int arcHeight) {
		log("drawRoundRectangle(x={0}, y={1}, width={2}, height={3}, arcWidth={4}, arcHeight={5})", r.x + dx, r.y + dy, //$NON-NLS-1$
				r.width, r.height, arcWidth, arcHeight);
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int offset, int length) {
		log("fillArc(x={0}, y={1}, width={2}, height={3}, offset={4}, length={5})", x + dx, y + dy, width, height, //$NON-NLS-1$
				offset, length);
	}

	@Override
	public void fillGradient(int x, int y, int w, int h, boolean vertical) {
		log("fillGradient(x={0}, y={1}, width={2}, height={3}, vertical={4})", x + dx, y + dy, w, h, vertical); //$NON-NLS-1$
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		log("fillOval(x={0}, y={1}, width={2}, height={3}, vertical={4})", x + dx, y + dy, width, height); //$NON-NLS-1$
	}

	@Override
	public void fillPath(Path path) {
		PathData pathData = path.getPathData();
		log("fillPath(path=[points={0}, types={1})]", Arrays.toString(pathData.points), //$NON-NLS-1$
				Arrays.toString(pathData.types));
	}

	@Override
	public void fillPolygon(int[] points) {
		log("fillPolygon(points={0})", Arrays.toString(points)); //$NON-NLS-1$
	}

	@Override
	public void fillRectangle(int x, int y, int width, int height) {
		log("fillRectangle(x={0}, y={1}, width={2}, height={3})", x + dx, y + dy, width, height); //$NON-NLS-1$
	}

	@Override
	public void fillRoundRectangle(Rectangle r, int arcWidth, int arcHeight) {
		log("fillRoundRectangle(x={0}, y={1}, width={2}, height={3}, arcWidth={4}, arcHeight={5})", r.x + dx, r.y + dy, //$NON-NLS-1$
				r.width, r.height, arcWidth, arcHeight);
	}

	@Override
	public Rectangle getClip(Rectangle rect) {
		// getClip does not utilize the fractional values of ScaledGraphics, so we must
		// ignore the translation here
		return rect.setBounds(clipRect);
	}

	// #########################################################################
	private final LineAttributes lineAttributes = new LineAttributes(1.0f);
	private int alpha = 255;
	private Font font = display.getSystemFont();
	private Color bgColor = ColorConstants.white;
	private Color fgColor = ColorConstants.black;

	@Override
	public void setFont(Font font) {
		this.font = font;
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public int getLineWidth() {
		return (int) getLineWidthFloat();
	}

	@Override
	public float getLineWidthFloat() {
		return lineAttributes.width;
	}

	@Override
	public void setLineWidth(int lineWidth) {
		setLineWidthFloat(lineWidth);
	}

	@Override
	public void setLineWidthFloat(float lineWidth) {
		this.lineAttributes.width = lineWidth;
	}

	@Override
	public LineAttributes getLineAttributes() {
		return SWTGraphics.clone(lineAttributes);
	}

	@Override
	public void setBackgroundColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	@Override
	public Color getBackgroundColor() {
		return bgColor;
	}

	@Override
	public void setForegroundColor(Color fgColor) {
		this.fgColor = fgColor;
	}

	@Override
	public Color getForegroundColor() {
		return fgColor;
	}

	@Override
	public int getAlpha() {
		return alpha;
	}

	@Override
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	@Override
	public void dispose() {
		// noop
	}
}
