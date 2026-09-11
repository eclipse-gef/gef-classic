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

package org.eclipse.draw2d.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.PathData;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.AWTGraphics;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ScaledGraphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.test.utils.Graphics2DToGraphicsAdapter;
import org.eclipse.draw2d.test.utils.GraphicsRecorder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public abstract class GraphicsTest {

	static Stream<Arguments> singleValueTestCombinations() {
		int[] inputs = { 5, 7, 10, 17, 20 };
		int[] monitorZooms = { 100, 125, 150, 175, 200 };
		int[] diagramZooms = { 100, 150, 200, 250, 300, 400 };

		return Arrays.stream(inputs).boxed()
				.flatMap(source -> Arrays.stream(monitorZooms).boxed().flatMap(monitorZoom -> Arrays
						.stream(diagramZooms).mapToObj(diagramZoom -> Arguments.of(source, monitorZoom, diagramZoom))));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawLineWithInt(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.drawLine(source, source, source, source + 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawLineWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.drawLine(source, source, source, source + 10));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawLineWithPoint(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.drawLine(new Point(source, source), new Point(source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawLineWithPointTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				graphics -> graphics.drawLine(new Point(source, source), new Point(source, source + 20)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawOvalWithInt(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.drawOval(source, source, source, source + 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawOvalWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.drawOval(source, source, source, source + 10));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawOvalWithRectangle(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.drawOval(new Rectangle(source, source, source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawOvalWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.drawOval(new Rectangle(source, source, source, source + 20)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawPoint(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.drawPoint(source, source + 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawPointTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.drawPoint(source, source + 10));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawArcWithInt(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.drawArc(source, source, source, source + 5, source, source));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawArcWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.drawArc(source, source, source, source + 10, source, source));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawArcWithRectangle(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(
				graphics -> graphics.drawArc(new Rectangle(source, source, source, source + 15), source, source));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawArcWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				graphics -> graphics.drawArc(new Rectangle(source, source, source, source + 20), source, source));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawFocusWithInt(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.drawFocus(source, source, source, source + 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawFocusWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.drawFocus(source, source, source, source + 10));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawFocusWithRectangle(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.drawFocus(new Rectangle(source, source, source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawFocusWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation
				.executeTranslated(graphics -> graphics.drawFocus(new Rectangle(source, source, source, source + 20)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawFullImageWithInt(int source, int monitorZoom, int diagramZoom) {
		Image image = new Image(Display.getDefault(), source, source + 5);
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.drawImage(image, source, source + 5));
		image.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawFullImageWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		Image image = new Image(Display.getDefault(), source, source + 10);
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.drawImage(image, new Point(source, source + 10)));
		image.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawFullImageWithPoint(int source, int monitorZoom, int diagramZoom) {
		Image image = new Image(Display.getDefault(), source, source + 20);
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.drawImage(image, new Point(source, source + 20)));
		image.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawFullImageWithPointTranslated(int source, int monitorZoom, int diagramZoom) {
		Image image = new Image(Display.getDefault(), source, source + 15);
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.drawImage(image, new Point(source, source + 15)));
		image.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawPath(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);

		PathData data = new PathData();
		float[] points = new float[18];
		byte[] types = new byte[7];
		points[0] = source * 1.1f;
		points[1] = source * 1.1f;
		types[0] = SWT.PATH_MOVE_TO;
		points[2] = source * 2.2f;
		points[3] = source * 2.2f;
		types[1] = SWT.PATH_LINE_TO;
		points[4] = source * 3.3f;
		points[5] = source * 3.3f;
		types[2] = SWT.PATH_MOVE_TO;
		points[6] = source * 4.4f;
		points[7] = source * 4.4f;
		points[8] = source * 4.4f;
		points[9] = source * 4.4f;
		types[3] = SWT.PATH_QUAD_TO;
		points[10] = source * 5.5f;
		points[11] = source * 5.5f;
		types[4] = SWT.PATH_MOVE_TO;
		points[12] = source * 6.6f;
		points[13] = source * 6.6f;
		points[14] = source * 6.6f;
		points[15] = source * 6.6f;
		points[16] = source * 6.6f;
		points[17] = source * 6.6f;
		types[5] = SWT.PATH_CUBIC_TO;
		types[6] = SWT.PATH_CLOSE;
		data.points = points;
		data.types = types;
		Path path = new Path(Display.getDefault(), data);
		validation.execute(graphics -> graphics.drawPath(path));
		path.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawPolygon(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		int[] points = new int[8];
		points[0] = source * 1 + 1;
		points[1] = source * 2 + 2;
		points[2] = source * 3 + 3;
		points[3] = source * 4 + 4;
		points[4] = source * 5 + 5;
		points[5] = source * 6 + 6;
		points[6] = source * 7 + 7;
		points[7] = source * 8 + 8;
		validation.execute(graphics -> graphics.drawPolygon(points));
		validation.execute(graphics -> graphics.drawPolygon(new PointList(points)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawRectangleWithInt(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.drawRectangle(source, source, source, source + 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawRectangleWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.drawRectangle(source, source, source, source + 10));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawRectangleWithRectangle(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.drawRectangle(new Rectangle(source, source, source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawRectangleWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				graphics -> graphics.drawRectangle(new Rectangle(source, source, source, source + 20)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawRoundRectangle(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(
				graphics -> graphics.drawRoundRectangle(new Rectangle(source, source, source, source + 15), 5, 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testDrawRoundRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				graphics -> graphics.drawRoundRectangle(new Rectangle(source, source, source, source + 20), 5, 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillOvalWithInt(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.fillOval(source, source, source, source + 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillOvalWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.fillOval(source, source, source, source + 10));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillOvalWithRectangle(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.fillOval(new Rectangle(source, source, source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillOvalWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.fillOval(new Rectangle(source, source, source, source + 20)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillArcWithInt(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.fillArc(source, source, source, source + 5, source, source));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillArcWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.fillArc(source, source, source, source + 10, source, source));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillArcWithRectangle(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(
				graphics -> graphics.fillArc(new Rectangle(source, source, source, source + 15), source, source));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillArcWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				graphics -> graphics.fillArc(new Rectangle(source, source, source, source + 20), source, source));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillGradientWithInt(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.fillGradient(source, source, source, source + 5, true));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillGradientWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.fillGradient(source, source, source, source + 10, true));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillGradientWithRectangle(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.fillGradient(new Rectangle(source, source, source, source + 15), true));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillGradientWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				graphics -> graphics.fillGradient(new Rectangle(source, source, source, source + 20), true));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillPath(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);

		PathData data = new PathData();
		float[] points = new float[18];
		byte[] types = new byte[7];
		points[0] = source * 1.1f;
		points[1] = source * 1.1f;
		types[0] = SWT.PATH_MOVE_TO;
		points[2] = source * 2.2f;
		points[3] = source * 2.2f;
		types[1] = SWT.PATH_LINE_TO;
		points[4] = source * 3.3f;
		points[5] = source * 3.3f;
		types[2] = SWT.PATH_MOVE_TO;
		points[6] = source * 4.4f;
		points[7] = source * 4.4f;
		points[8] = source * 4.4f;
		points[9] = source * 4.4f;
		types[3] = SWT.PATH_QUAD_TO;
		points[10] = source * 5.5f;
		points[11] = source * 5.5f;
		types[4] = SWT.PATH_MOVE_TO;
		points[12] = source * 6.6f;
		points[13] = source * 6.6f;
		points[14] = source * 6.6f;
		points[15] = source * 6.6f;
		points[16] = source * 6.6f;
		points[17] = source * 6.6f;
		types[5] = SWT.PATH_CUBIC_TO;
		types[6] = SWT.PATH_CLOSE;
		data.points = points;
		data.types = types;
		Path path = new Path(Display.getDefault(), data);
		validation.execute(graphics -> graphics.fillPath(path));
		path.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillPolygon(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		int[] points = new int[8];
		points[0] = source * 1 + 1;
		points[1] = source * 2 + 2;
		points[2] = source * 3 + 3;
		points[3] = source * 4 + 4;
		points[4] = source * 5 + 5;
		points[5] = source * 6 + 6;
		points[6] = source * 7 + 7;
		points[7] = source * 8 + 8;
		validation.execute(graphics -> graphics.fillPolygon(points));
		validation.execute(graphics -> graphics.fillPolygon(new PointList(points)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillRectangleWithInt(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.fillRectangle(source, source, source, source + 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillRectangleWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.fillRectangle(source, source, source, source + 10));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillRectangleWithRectangle(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.fillRectangle(new Rectangle(source, source, source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillRectangleWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				graphics -> graphics.fillRectangle(new Rectangle(source, source, source, source + 20)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillRoundRectangle(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(
				graphics -> graphics.fillRoundRectangle(new Rectangle(source, source, source, source + 15), 5, 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testFillRoundRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				graphics -> graphics.fillRoundRectangle(new Rectangle(source, source, source, source + 20), 5, 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testClipRectWithRectangle(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.clipRect(new Rectangle(source, source, source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testClipRectWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.clipRect(new Rectangle(source, source, source, source + 20)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testSetClipRectWithRectangle(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(graphics -> graphics.setClip(new Rectangle(source, source, source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	public void testSetClipRectWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(graphics -> graphics.setClip(new Rectangle(source, source, source, source + 20)));
	}

	private class GraphicsValidation {

		private final int monitorZoom;
		private final int diagramZoom;

		public GraphicsValidation(int monitorZoom, int diagramZoom) {
			this.monitorZoom = monitorZoom;
			this.diagramZoom = diagramZoom;
		}

		public void execute(Consumer<Graphics> graphicsCall) {
			GraphicsRecorder graphics1 = executeWithOneLayer(monitorZoom, diagramZoom, graphicsCall);
			GraphicsRecorder graphics2 = executeWithTwoLayers(monitorZoom, diagramZoom, graphicsCall);

			graphics2.assertEquals(graphics1);
		}

		public void executeTranslated(Consumer<Graphics> graphicsCall) {
			GraphicsRecorder graphics1 = executeTranslatedWithOneLayer(monitorZoom, diagramZoom, graphicsCall);
			GraphicsRecorder graphics2 = executeTranslatedWithTwoLayers(monitorZoom, diagramZoom, graphicsCall);

			graphics2.assertEquals(graphics1);
		}
	}

	private static void validateRect(Rectangle actual, Rectangle expected) {
		assertEquals(expected.x, actual.x, String.format("Actual value for x must match value %s", expected.x)); //$NON-NLS-1$
		assertEquals(expected.y, actual.y, String.format("Actual value for y must match value %s", expected.y)); //$NON-NLS-1$
		assertEquals(expected.width, actual.width,
				String.format("Actual value for width must match value %s", expected.width)); //$NON-NLS-1$
		assertEquals(expected.height, actual.height,
				String.format("Actual value for height must match value %s", expected.height)); //$NON-NLS-1$
	}

	protected GraphicsRecorder executeWithOneLayer(int monitorZoom, int diagramZoom, Consumer<Graphics> graphicsCall) {
		return executeWithOneLayer(monitorZoom, diagramZoom, graphicsCall, graphics -> {
		});
	}

	protected GraphicsRecorder executeWithOneLayer(int monitorZoom, int diagramZoom, Consumer<Graphics> graphicsCall,
			Consumer<GraphicsRecorder> initializeGraphics) {
		GraphicsRecorder recorder = new GraphicsRecorder();
		initializeGraphics.accept(recorder);
		Graphics graphics = createGraphics(recorder);
		graphics.scale(monitorZoom / 100d * diagramZoom / 100d);
		graphicsCall.accept(graphics);
		graphics.dispose();
		recorder.dispose();
		return recorder;
	}

	protected GraphicsRecorder executeTranslatedWithOneLayer(int monitorZoom, int diagramZoom,
			Consumer<Graphics> graphicsCall) {
		return executeTranslatedWithOneLayer(monitorZoom, diagramZoom, graphicsCall, graphics -> {
		});
	}

	protected GraphicsRecorder executeTranslatedWithOneLayer(int monitorZoom, int diagramZoom,
			Consumer<Graphics> graphicsCall, Consumer<GraphicsRecorder> initializeGraphics) {
		GraphicsRecorder recorder = new GraphicsRecorder();
		initializeGraphics.accept(recorder);
		Graphics graphics = createGraphics(recorder);
		graphics.scale(monitorZoom / 100d * diagramZoom / 100d);
		graphics.translate(1f, 1f);
		recorder.clear();
		graphicsCall.accept(graphics);
		graphics.dispose();
		recorder.dispose();
		return recorder;
	}

	protected GraphicsRecorder executeWithTwoLayers(int monitorZoom, int diagramZoom, Consumer<Graphics> graphicsCall) {
		return executeWithTwoLayers(monitorZoom, diagramZoom, graphicsCall, graphics -> {
		});
	}

	protected GraphicsRecorder executeWithTwoLayers(int monitorZoom, int diagramZoom, Consumer<Graphics> graphicsCall,
			Consumer<GraphicsRecorder> initializeGraphics) {
		GraphicsRecorder recorder = new GraphicsRecorder();
		initializeGraphics.accept(recorder);
		Graphics graphics = createGraphics(recorder);
		graphics.scale(monitorZoom / 100d);
		Graphics graphics2 = createGraphics(graphics);
		graphics2.scale(diagramZoom / 100d);
		graphicsCall.accept(graphics2);
		graphics2.dispose();
		graphics.dispose();
		recorder.dispose();
		return recorder;
	}

	protected GraphicsRecorder executeTranslatedWithTwoLayers(int monitorZoom, int diagramZoom,
			Consumer<Graphics> graphicsCall) {
		return executeTranslatedWithTwoLayers(monitorZoom, diagramZoom, graphicsCall, graphics -> {
		});
	}

	protected GraphicsRecorder executeTranslatedWithTwoLayers(int monitorZoom, int diagramZoom,
			Consumer<Graphics> graphicsCall, Consumer<GraphicsRecorder> initializeGraphics) {
		GraphicsRecorder recorder = new GraphicsRecorder();
		initializeGraphics.accept(recorder);
		Graphics graphics = createGraphics(recorder);
		graphics.scale(monitorZoom / 100d);
		Graphics graphics2 = createGraphics(graphics);
		graphics2.scale(diagramZoom / 100d);
		graphics2.translate(1f, 1f);
		recorder.clear();
		graphicsCall.accept(graphics2);
		graphics2.dispose();
		graphics.dispose();
		recorder.dispose();
		return recorder;
	}

	/**
	 * Creates and returns a new instance of the Graphics object to be tested.
	 */
	protected abstract Graphics createGraphics(Graphics recorder);

	public static class ScaledGraphicsTest extends GraphicsTest {
		@Override
		@SuppressWarnings("removal")
		protected Graphics createGraphics(Graphics recorder) {
			return new ScaledGraphics(recorder);
		}

		@Test
		public void testClipRectForRegression() {
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.clipRect(new Rectangle(5, 7, 9, 25)));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.clipRect(new Rectangle(30, 40, 45, 125));
			expectedRecorder.assertEquals(actualRecorder);
		}

		@Test
		public void testDrawArcForRegression() {
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.drawArc(5, 7, 9, 25, 12, 18));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.drawArc(new Rectangle(30, 40, 45, 125), 12, 18);
			expectedRecorder.assertEquals(actualRecorder);

			actualRecorder = executeTranslatedWithOneLayer(200, 250, graphics -> graphics.drawArc(0, 0, 0, 0, 12, 18));
			actualRecorder.assertEmpty();

			actualRecorder = executeTranslatedWithOneLayer(200, 250, graphics -> graphics.drawArc(5, 7, 9, 25, 12, 0));
			actualRecorder.assertEmpty();
		}

		@Test
		public void testDrawFocusForRegression() {
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.drawFocus(5, 7, 9, 25));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.drawFocus(30, 40, 45, 125);
			expectedRecorder.assertEquals(actualRecorder);
		}

		@Test
		public void testDrawFullImageForRegression() {
			Image image = new Image(Display.getDefault(), 9, 25);
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.drawImage(image, 5, 7));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.drawImage(image, 0, 0, 9, 25, 30, 40, 45, 125);
			expectedRecorder.assertEquals(actualRecorder);
			image.dispose();
		}

		@Test
		public void testDrawImageForRegression() {
			Image image = new Image(Display.getDefault(), 5, 5);
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.drawImage(image, 5, 5, 5, 5, 5, 7, 9, 25));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.drawImage(image, 5, 5, 5, 5, 30, 40, 45, 125);
			expectedRecorder.assertEquals(actualRecorder);
			image.dispose();
		}

		@ParameterizedTest
		@MethodSource("singleValueTestCombinations")
		public void testDrawImageWithInt(int source, int monitorZoom, int diagramZoom) {
			Image image = new Image(Display.getDefault(), 5, 5);
			GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
			validation.execute(graphics -> graphics.drawImage(image, 5, 5, 5, 5, source, source, source, source + 5));
			image.dispose();
		}

		@ParameterizedTest
		@MethodSource("singleValueTestCombinations")
		public void testDrawImageWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
			Image image = new Image(Display.getDefault(), 5, 5);
			GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
			validation.executeTranslated(
					graphics -> graphics.drawImage(image, 5, 5, 5, 5, source, source, source, source + 10));
			image.dispose();
		}

		@ParameterizedTest
		@MethodSource("singleValueTestCombinations")
		public void testDrawImageWithRectangle(int source, int monitorZoom, int diagramZoom) {
			Image image = new Image(Display.getDefault(), 5, 5);
			GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
			validation.execute(graphics -> graphics.drawImage(image, new Rectangle(5, 5, 5, 5),
					new Rectangle(source, source, source, source + 15)));
			image.dispose();
		}

		@ParameterizedTest
		@MethodSource("singleValueTestCombinations")
		public void testDrawImageWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
			Image image = new Image(Display.getDefault(), 5, 5);
			GraphicsValidation validation = new GraphicsValidation(monitorZoom, diagramZoom);
			validation.executeTranslated(graphics -> graphics.drawImage(image, new Rectangle(5, 5, 5, 5),
					new Rectangle(source, source, source, source + 20)));
			image.dispose();
		}

		@Test
		public void testDrawLineForRegression() {
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.drawLine(new Point(5, 5), new Point(5, 5 + 20)));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.drawLine(30, 30, 30, 130);
			expectedRecorder.assertEquals(actualRecorder);
		}

		@Test
		public void testDrawOvalForRegression() {
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.drawOval(5, 7, 9, 25));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.drawOval(30, 40, 45, 125);
			expectedRecorder.assertEquals(actualRecorder);
		}

		@Test
		public void testDrawPointForRegression() {
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.drawPoint(5, 5));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.drawPoint(30, 30);
			expectedRecorder.assertEquals(actualRecorder);
		}

		@Test
		public void testDrawRectangleForRegression() {
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.drawRectangle(5, 7, 9, 25));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.drawRectangle(30, 40, 45, 125);
			expectedRecorder.assertEquals(actualRecorder);
		}

		@Test
		public void testDrawRoundRectangleForRegression() {
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.drawRoundRectangle(new Rectangle(5, 7, 9, 25), 5, 5));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.drawRoundRectangle(new Rectangle(30, 40, 45, 125), 25, 25);
			expectedRecorder.assertEquals(actualRecorder);
		}

		@Test
		public void testFillArcForRegression() {
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.fillArc(5, 7, 9, 25, 12, 18));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.fillArc(30, 40, 41, 121, 12, 18);
			expectedRecorder.assertEquals(actualRecorder);

			actualRecorder = executeTranslatedWithOneLayer(200, 250, graphics -> graphics.fillArc(0, 0, 0, 0, 12, 18));
			actualRecorder.assertEmpty();

			actualRecorder = executeTranslatedWithOneLayer(200, 250, graphics -> graphics.fillArc(5, 7, 9, 25, 12, 0));
			actualRecorder.assertEmpty();
		}

		@Test
		public void testFillGradientForRegression() {
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.fillGradient(5, 7, 9, 25, true));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.fillGradient(30, 40, 41, 121, true);
			expectedRecorder.assertEquals(actualRecorder);
		}

		@Test
		public void testFillOvalForRegression() {
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.fillOval(5, 7, 9, 25));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.fillOval(30, 40, 41, 121);
			expectedRecorder.assertEquals(actualRecorder);
		}

		@Test
		public void testFillRectangleForRegression() {
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.fillRectangle(5, 7, 9, 25));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.fillRectangle(30, 40, 41, 121);
			expectedRecorder.assertEquals(actualRecorder);
		}

		@Test
		public void testFillRoundRectangleForRegression() {
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.fillRoundRectangle(new Rectangle(5, 7, 9, 25), 5, 5));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.fillRoundRectangle(new Rectangle(30, 40, 41, 121), 25, 25);
			expectedRecorder.assertEquals(actualRecorder);
		}

		@Test
		public void testGetClipRectForRegression() {
			Rectangle clipRect = new Rectangle();
			executeTranslatedWithOneLayer(200, 250, graphics -> graphics.getClip(clipRect),
					graphics -> graphics.setClip(new Rectangle(25, 35, 45, 125)));
			validateRect(clipRect, new Rectangle(5, 7, 9, 25));
		}

		@ParameterizedTest
		@MethodSource("singleValueTestCombinations")
		public void testGetClipRectWithRectangle(int source, int monitorZoom, int diagramZoom) {
			Rectangle clipRectOne = new Rectangle();
			executeWithOneLayer(monitorZoom, diagramZoom, graphics -> graphics.getClip(clipRectOne),
					graphics -> graphics
							.setClip(new Rectangle(source * 10, source * 10, source * 10, source * 10 + 15)));

			Rectangle clipRectTwo = new Rectangle();
			executeWithTwoLayers(monitorZoom, diagramZoom, graphics -> graphics.getClip(clipRectTwo),
					graphics -> graphics
							.setClip(new Rectangle(source * 10, source * 10, source * 10, source * 10 + 15)));
			validateRect(clipRectTwo, clipRectOne);
		}

		@ParameterizedTest
		@MethodSource("singleValueTestCombinations")
		public void testGetClipRectWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
			Rectangle clipRectOne = new Rectangle();
			executeTranslatedWithOneLayer(monitorZoom, diagramZoom, graphics -> graphics.getClip(clipRectOne),
					graphics -> graphics
							.setClip(new Rectangle(source * 10, source * 10, source * 10, source * 10 + 15)));

			Rectangle clipRectTwo = new Rectangle();
			executeTranslatedWithTwoLayers(monitorZoom, diagramZoom, graphics -> graphics.getClip(clipRectTwo),
					graphics -> graphics
							.setClip(new Rectangle(source * 10, source * 10, source * 10, source * 10 + 15)));
			validateRect(clipRectTwo, clipRectOne);
		}

		@Test
		public void testSetClipRectForRegression() {
			GraphicsRecorder actualRecorder = executeTranslatedWithOneLayer(200, 250,
					graphics -> graphics.setClip(new Rectangle(5, 7, 9, 25)));
			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.setClip(new Rectangle(30, 40, 45, 125));
			expectedRecorder.assertEquals(actualRecorder);
		}

		@Test
		public void testTranslationWithMulipleScaledLayers() {
			GraphicsRecorder actualRecorder = new GraphicsRecorder();
			Graphics graphics = createGraphics(actualRecorder);
			graphics.scale(1.5);
			graphics.translate(1f, 1f);
			Graphics graphics2 = createGraphics(graphics);
			graphics2.scale(2.5);
			graphics2.translate(1f, 1f);
			graphics2.drawRectangle(0, 0, 10, 10);

			GraphicsRecorder expectedRecorder = new GraphicsRecorder();
			expectedRecorder.translate(1, 1);
			expectedRecorder.translate(4, 4);
			expectedRecorder.drawRectangle(0, 0, 37, 37);
			expectedRecorder.assertEquals(actualRecorder);
			graphics2.dispose();
			graphics.dispose();
			actualRecorder.dispose();
		}
	}

	public static class AWTGraphicsTest extends GraphicsTest {
		@Override
		protected Graphics createGraphics(Graphics recorder) {
			return new AWTGraphics(new Graphics2DToGraphicsAdapter(recorder));
		}
	}
}
