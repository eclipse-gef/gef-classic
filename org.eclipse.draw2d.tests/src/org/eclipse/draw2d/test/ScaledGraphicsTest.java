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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.PathData;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.ScaledGraphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings({ "removal", "deprecation" })
public class ScaledGraphicsTest {

	static Stream<Arguments> singleValueTestCombinations() {
		int[] inputs = { 5, 7, 10, 17, 20 };
		int[] monitorZooms = { 100, 125, 150, 175, 200 };
		int[] diagramZooms = { 100, 150, 200, 250, 300, 400 };

		return Arrays.stream(inputs).boxed()
				.flatMap(source -> Arrays.stream(monitorZooms).boxed().flatMap(monitorZoom -> Arrays
						.stream(diagramZooms).mapToObj(diagramZoom -> Arguments.of(source, monitorZoom, diagramZoom))));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testTranlsationWithMulipleScaledLayers() {
		Display display = Display.getDefault();
		Image image = new Image(display, 100, 100);
		GC gc = new GC(image);
		RecordingSwtGraphics graphics = new RecordingSwtGraphics(gc);
		ScaledGraphics scaledGraphics = new ScaledGraphics(graphics);
		scaledGraphics.scale(1.5);
		scaledGraphics.translate(1f, 1f);
		ScaledGraphics scaledGraphics2 = new ScaledGraphics(scaledGraphics);
		scaledGraphics2.scale(2.5);
		scaledGraphics2.translate(1f, 1f);

		scaledGraphics2.drawRectangle(0, 0, 10, 10);
		assertEquals(5, graphics.translation.x);
		assertEquals(5, graphics.translation.y);

		validateDrawRectangle(graphics, new Rectangle(5, 5, 37, 37));
		scaledGraphics2.dispose();
		scaledGraphics.dispose();
		graphics.dispose();
		gc.dispose();
		image.dispose();
	}

	@Test
	@SuppressWarnings("static-method")
	public void testDrawLineForRegression() {
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.drawLine(new Point(5, 5), new Point(5, 5 + 20)));
		validateDrawLine(swtGraphics, new Point(30, 30));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawLineWithInt(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics.drawLine(source, source, source, source + 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawLineWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(scaledGraphics -> scaledGraphics.drawLine(source, source, source, source + 10));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawLineWithPoint(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(
				scaledGraphics -> scaledGraphics.drawLine(new Point(source, source), new Point(source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawLineWithPointTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				scaledGraphics -> scaledGraphics.drawLine(new Point(source, source), new Point(source, source + 20)));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testDrawOvalForRegression() {
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.drawOval(5, 7, 9, 25));
		validateDrawOval(swtGraphics, new Rectangle(30, 40, 45, 125));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawOvalWithInt(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics.drawOval(source, source, source, source + 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawOvalWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(scaledGraphics -> scaledGraphics.drawOval(source, source, source, source + 10));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawOvalWithRectangle(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation
				.execute(scaledGraphics -> scaledGraphics.drawOval(new Rectangle(source, source, source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawOvalWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				scaledGraphics -> scaledGraphics.drawOval(new Rectangle(source, source, source, source + 20)));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testDrawPointForRegression() {
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.drawPoint(5, 5));
		validateDrawPoint(swtGraphics, new Point(30, 30));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawPoint(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics.drawPoint(source, source + 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawPointTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(scaledGraphics -> scaledGraphics.drawPoint(source, source + 10));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testDrawArcForRegression() {
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.drawArc(5, 7, 9, 25, 12, 18));
		validateDrawArc(swtGraphics, new Rectangle(30, 40, 45, 125));

		swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.drawArc(0, 0, 0, 0, 12, 18));
		validateDrawArc(swtGraphics, new Rectangle(0, 0, 0, 0));

		swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.drawArc(5, 7, 9, 25, 12, 0));
		validateDrawArc(swtGraphics, new Rectangle(0, 0, 0, 0));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawArcWithInt(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation
				.execute(scaledGraphics -> scaledGraphics.drawArc(source, source, source, source + 5, source, source));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawArcWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				scaledGraphics -> scaledGraphics.drawArc(source, source, source, source + 10, source, source));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawArcWithRectangle(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics.drawArc(new Rectangle(source, source, source, source + 15),
				source, source));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawArcWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(scaledGraphics -> scaledGraphics
				.drawArc(new Rectangle(source, source, source, source + 20), source, source));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testDrawFocusForRegression() {
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.drawFocus(5, 7, 9, 25));
		validateDrawFocus(swtGraphics, new Rectangle(30, 40, 45, 125));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawFocusWithInt(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics.drawFocus(source, source, source, source + 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawFocusWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(scaledGraphics -> scaledGraphics.drawFocus(source, source, source, source + 10));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawFocusWithRectangle(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(
				scaledGraphics -> scaledGraphics.drawFocus(new Rectangle(source, source, source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawFocusWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				scaledGraphics -> scaledGraphics.drawFocus(new Rectangle(source, source, source, source + 20)));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testDrawFullImageForRegression() {
		Image image = new Image(Display.getDefault(), 9, 25);
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.drawImage(image, 5, 7));
		validateDrawImage(swtGraphics, new Rectangle(30, 40, 45, 125));
		image.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawFullImageWithInt(int source, int monitorZoom, int diagramZoom) {
		Image image = new Image(Display.getDefault(), source, source + 5);
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics.drawImage(image, source, source + 5));
		image.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawFullImageWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		Image image = new Image(Display.getDefault(), source, source + 10);
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(scaledGraphics -> scaledGraphics.drawImage(image, new Point(source, source + 10)));
		image.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawFullImageWithPoint(int source, int monitorZoom, int diagramZoom) {
		Image image = new Image(Display.getDefault(), source, source + 20);
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics.drawImage(image, new Point(source, source + 20)));
		image.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawFullImageWithPointTranslated(int source, int monitorZoom, int diagramZoom) {
		Image image = new Image(Display.getDefault(), source, source + 15);
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(scaledGraphics -> scaledGraphics.drawImage(image, new Point(source, source + 15)));
		image.dispose();
	}

	@Test
	@SuppressWarnings("static-method")
	public void testDrawImageForRegression() {
		Image image = new Image(Display.getDefault(), 5, 5);
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.drawImage(image, 5, 5, 5, 5, 5, 7, 9, 25));
		validateDrawImage(swtGraphics, new Rectangle(30, 40, 45, 125));
		image.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawImageWithInt(int source, int monitorZoom, int diagramZoom) {
		Image image = new Image(Display.getDefault(), 5, 5);
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(
				scaledGraphics -> scaledGraphics.drawImage(image, 5, 5, 5, 5, source, source, source, source + 5));
		image.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawImageWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		Image image = new Image(Display.getDefault(), 5, 5);
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				scaledGraphics -> scaledGraphics.drawImage(image, 5, 5, 5, 5, source, source, source, source + 10));
		image.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawImageWithRectangle(int source, int monitorZoom, int diagramZoom) {
		Image image = new Image(Display.getDefault(), 5, 5);
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics.drawImage(image, new Rectangle(5, 5, 5, 5),
				new Rectangle(source, source, source, source + 15)));
		image.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawImageWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		Image image = new Image(Display.getDefault(), 5, 5);
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(scaledGraphics -> scaledGraphics.drawImage(image, new Rectangle(5, 5, 5, 5),
				new Rectangle(source, source, source, source + 20)));
		image.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawPath(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);

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
		validation.execute(scaledGraphics -> scaledGraphics.drawPath(path));
		path.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawPolygon(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		int[] points = new int[8];
		points[0] = source * 1 + 1;
		points[1] = source * 2 + 2;
		points[2] = source * 3 + 3;
		points[3] = source * 4 + 4;
		points[4] = source * 5 + 5;
		points[5] = source * 6 + 6;
		points[6] = source * 7 + 7;
		points[7] = source * 8 + 8;
		validation.execute(scaledGraphics -> scaledGraphics.drawPolygon(points));
		validation.execute(scaledGraphics -> scaledGraphics.drawPolygon(new PointList(points)));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testDrawRectangleForRegression() {
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.drawRectangle(5, 7, 9, 25));
		validateDrawRectangle(swtGraphics, new Rectangle(30, 40, 45, 125));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawRectangleWithInt(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics.drawRectangle(source, source, source, source + 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawRectangleWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation
				.executeTranslated(scaledGraphics -> scaledGraphics.drawRectangle(source, source, source, source + 10));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawRectangleWithRectangle(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(
				scaledGraphics -> scaledGraphics.drawRectangle(new Rectangle(source, source, source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawRectangleWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				scaledGraphics -> scaledGraphics.drawRectangle(new Rectangle(source, source, source, source + 20)));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testDrawRoundRectangleForRegression() {
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.drawRoundRectangle(new Rectangle(5, 7, 9, 25), 5, 5));
		validateDrawRoundRectangle(swtGraphics, new Rectangle(30, 40, 45, 125), new Point(25, 25));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawRoundRectangle(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics
				.drawRoundRectangle(new Rectangle(source, source, source, source + 15), 5, 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawRoundRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(scaledGraphics -> scaledGraphics
				.drawRoundRectangle(new Rectangle(source, source, source, source + 20), 5, 5));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testFillOvalForRegression() {
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.fillOval(5, 7, 9, 25));
		validateFillOval(swtGraphics, new Rectangle(30, 40, 41, 121));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillOvalWithInt(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics.fillOval(source, source, source, source + 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillOvalWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(scaledGraphics -> scaledGraphics.fillOval(source, source, source, source + 10));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillOvalWithRectangle(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation
				.execute(scaledGraphics -> scaledGraphics.fillOval(new Rectangle(source, source, source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillOvalWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				scaledGraphics -> scaledGraphics.fillOval(new Rectangle(source, source, source, source + 20)));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testFillArcForRegression() {
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.fillArc(5, 7, 9, 25, 12, 18));
		validateFillArc(swtGraphics, new Rectangle(30, 40, 41, 121));

		swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.fillArc(0, 0, 0, 0, 12, 18));
		validateFillArc(swtGraphics, new Rectangle(0, 0, 0, 0));

		swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.fillArc(5, 7, 9, 25, 12, 0));
		validateFillArc(swtGraphics, new Rectangle(0, 0, 0, 0));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillArcWithInt(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation
				.execute(scaledGraphics -> scaledGraphics.fillArc(source, source, source, source + 5, source, source));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillArcWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				scaledGraphics -> scaledGraphics.fillArc(source, source, source, source + 10, source, source));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillArcWithRectangle(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics.fillArc(new Rectangle(source, source, source, source + 15),
				source, source));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillArcWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(scaledGraphics -> scaledGraphics
				.fillArc(new Rectangle(source, source, source, source + 20), source, source));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testFillGradientForRegression() {
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.fillGradient(5, 7, 9, 25, true));
		validateFillGradient(swtGraphics, new Rectangle(30, 40, 41, 121));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillGradientWithInt(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics.fillGradient(source, source, source, source + 5, true));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillGradientWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				scaledGraphics -> scaledGraphics.fillGradient(source, source, source, source + 10, true));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillGradientWithRectangle(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics
				.fillGradient(new Rectangle(source, source, source, source + 15), true));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillGradientWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(scaledGraphics -> scaledGraphics
				.fillGradient(new Rectangle(source, source, source, source + 20), true));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillPath(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);

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
		validation.execute(scaledGraphics -> scaledGraphics.fillPath(path));
		path.dispose();
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillPolygon(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		int[] points = new int[8];
		points[0] = source * 1 + 1;
		points[1] = source * 2 + 2;
		points[2] = source * 3 + 3;
		points[3] = source * 4 + 4;
		points[4] = source * 5 + 5;
		points[5] = source * 6 + 6;
		points[6] = source * 7 + 7;
		points[7] = source * 8 + 8;
		validation.execute(scaledGraphics -> scaledGraphics.fillPolygon(points));
		validation.execute(scaledGraphics -> scaledGraphics.fillPolygon(new PointList(points)));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testFillRectangleForRegression() {
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.fillRectangle(5, 7, 9, 25));
		validateFillRectangle(swtGraphics, new Rectangle(30, 40, 41, 121));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillRectangleWithInt(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics.fillRectangle(source, source, source, source + 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillRectangleWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation
				.executeTranslated(scaledGraphics -> scaledGraphics.fillRectangle(source, source, source, source + 10));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillRectangleWithRectangle(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(
				scaledGraphics -> scaledGraphics.fillRectangle(new Rectangle(source, source, source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillRectangleWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				scaledGraphics -> scaledGraphics.fillRectangle(new Rectangle(source, source, source, source + 20)));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testFillRoundRectangleForRegression() {
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.fillRoundRectangle(new Rectangle(5, 7, 9, 25), 5, 5));
		validateFillRoundRectangle(swtGraphics, new Rectangle(30, 40, 41, 121), new Point(25, 25));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillRoundRectangle(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics
				.fillRoundRectangle(new Rectangle(source, source, source, source + 15), 5, 5));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testFillRoundRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(scaledGraphics -> scaledGraphics
				.fillRoundRectangle(new Rectangle(source, source, source, source + 20), 5, 5));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testClipRectForRegression() {
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.clipRect(new Rectangle(5, 7, 9, 25)));
		validateClipRect(swtGraphics, new Rectangle(30, 40, 45, 125));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testClipRectWithRectangle(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation
				.execute(scaledGraphics -> scaledGraphics.clipRect(new Rectangle(source, source, source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testClipRectWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				scaledGraphics -> scaledGraphics.clipRect(new Rectangle(source, source, source, source + 20)));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testGetClipRectForRegression() {
		Rectangle clipRect = new Rectangle();
		executeTranslatedWithOneLayer(200, 250, scaledGraphics -> scaledGraphics.getClip(clipRect),
				graphics -> graphics.clipRect = new Rectangle(25, 35, 45, 125));
		validateRect(clipRect, new Rectangle(5, 7, 9, 25));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testGetClipRectWithRectangle(int source, int monitorZoom, int diagramZoom) {
		Rectangle clipRectOne = new Rectangle();
		executeWithOneLayer(monitorZoom, diagramZoom, scaledGraphics -> scaledGraphics.getClip(clipRectOne),
				graphics -> graphics.clipRect = new Rectangle(source * 10, source * 10, source * 10, source * 10 + 15));

		Rectangle clipRectTwo = new Rectangle();
		executeWithTwoLayers(monitorZoom, diagramZoom, scaledGraphics -> scaledGraphics.getClip(clipRectTwo),
				graphics -> graphics.clipRect = new Rectangle(source * 10, source * 10, source * 10, source * 10 + 15));
		validateRect(clipRectTwo, clipRectOne);
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testGetClipRectWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		Rectangle clipRectOne = new Rectangle();
		executeTranslatedWithOneLayer(monitorZoom, diagramZoom, scaledGraphics -> scaledGraphics.getClip(clipRectOne),
				graphics -> graphics.clipRect = new Rectangle(source * 10, source * 10, source * 10, source * 10 + 15));

		Rectangle clipRectTwo = new Rectangle();
		executeTranslatedWithTwoLayers(monitorZoom, diagramZoom, scaledGraphics -> scaledGraphics.getClip(clipRectTwo),
				graphics -> graphics.clipRect = new Rectangle(source * 10, source * 10, source * 10, source * 10 + 15));
		validateRect(clipRectTwo, clipRectOne);
	}

	@Test
	@SuppressWarnings("static-method")
	public void testSetClipRectForRegression() {
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.setClip(new Rectangle(5, 7, 9, 25)));
		validateSetClipRect(swtGraphics, new Rectangle(30, 40, 45, 125));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testSetClipRectWithRectangle(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation
				.execute(scaledGraphics -> scaledGraphics.setClip(new Rectangle(source, source, source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("singleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testSetClipRectWithRectangleTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				scaledGraphics -> scaledGraphics.setClip(new Rectangle(source, source, source, source + 20)));
	}

	private static class ScaledGraphicsValidation {

		private final int monitorZoom;
		private final int diagramZoom;

		public ScaledGraphicsValidation(int monitorZoom, int diagramZoom) {
			this.monitorZoom = monitorZoom;
			this.diagramZoom = diagramZoom;
		}

		public void execute(Consumer<ScaledGraphics> graphicsCall) {
			RecordingSwtGraphics graphics1 = executeWithOneLayer(monitorZoom, diagramZoom, graphicsCall);
			RecordingSwtGraphics graphics2 = executeWithTwoLayers(monitorZoom, diagramZoom, graphicsCall);

			validate(graphics1, graphics2);
		}

		public void executeTranslated(Consumer<ScaledGraphics> graphicsCall) {
			RecordingSwtGraphics graphics1 = executeTranslatedWithOneLayer(monitorZoom, diagramZoom, graphicsCall);
			RecordingSwtGraphics graphics2 = executeTranslatedWithTwoLayers(monitorZoom, diagramZoom, graphicsCall);

			validate(graphics1, graphics2);
		}

		private static void validate(RecordingSwtGraphics graphics1, RecordingSwtGraphics graphics2) {
			validateClipRect(graphics2, graphics1.clipRect);
			validateSetClipRect(graphics2, graphics1.setClipRect);
			validateDrawLine(graphics2, graphics1.drawLine);
			validateDrawOval(graphics2, graphics1.drawOval);
			validateDrawArc(graphics2, graphics1.drawArc);
			validateDrawFocus(graphics2, graphics1.drawFocus);
			validateDrawImage(graphics2, graphics1.drawImage);
			validateDrawPath(graphics2, graphics1.drawPathData);
			validateDrawPoint(graphics2, graphics1.drawPoint);
			validateDrawPolygon(graphics2, graphics1.drawPolygon);
			validateDrawRectangle(graphics2, graphics1.drawRectangle);
			validateDrawRoundRectangle(graphics2, graphics1.drawRoundRectangle, graphics1.drawRoundRectangleArc);
			validateFillArc(graphics2, graphics1.fillArc);
			validateFillGradient(graphics2, graphics1.fillGradient);
			validateFillOval(graphics2, graphics1.fillOval);
			validateFillPath(graphics2, graphics1.fillPathData);
			validateFillPolygon(graphics2, graphics1.fillPolygon);
			validateFillRectangle(graphics2, graphics1.fillRectangle);
			validateFillRoundRectangle(graphics2, graphics1.fillRoundRectangle, graphics1.fillRoundRectangleArc);
		}
	}

	private static void validateRect(Rectangle actual, Rectangle expected) {
		assertEquals(expected.x, actual.x, String.format("Scaled value for x must match value %s", expected.x)); //$NON-NLS-1$
		assertEquals(expected.y, actual.y, String.format("Scaled value for y must match value %s", expected.y)); //$NON-NLS-1$
		assertEquals(expected.width, actual.width,
				String.format("Scaled value for width must match value %s", expected.width)); //$NON-NLS-1$
		assertEquals(expected.height, actual.height,
				String.format("Scaled value for height must match value %s", expected.height)); //$NON-NLS-1$
	}

	private static void validatePoint(Point actual, Point expected) {
		assertEquals(expected.x, actual.x, String.format("Scaled value for x1 must match value %s", expected.x)); //$NON-NLS-1$
		assertEquals(expected.y, actual.y, String.format("Scaled value for y1 must match value %s", expected.y)); //$NON-NLS-1$
	}

	private static void validateClipRect(RecordingSwtGraphics graphics, Rectangle expected) {
		validateRect(graphics.clipRect, expected);
	}

	private static void validateSetClipRect(RecordingSwtGraphics graphics, Rectangle expected) {
		validateRect(graphics.setClipRect, expected);
	}

	private static void validateDrawImage(RecordingSwtGraphics graphics, Rectangle expected) {
		validateRect(graphics.drawImage, expected);
	}

	private static void validateDrawLine(RecordingSwtGraphics graphics, Point expected) {
		validatePoint(graphics.drawLine, expected);
	}

	private static void validateDrawOval(RecordingSwtGraphics graphics, Rectangle expected) {
		validateRect(graphics.drawOval, expected);
	}

	private static void validateDrawArc(RecordingSwtGraphics graphics, Rectangle expected) {
		validateRect(graphics.drawArc, expected);
	}

	private static void validateDrawFocus(RecordingSwtGraphics graphics, Rectangle expected) {
		validateRect(graphics.drawFocus, expected);
	}

	private static void validateDrawPath(RecordingSwtGraphics graphics, PathData pathData) {
		assertArrayEquals(pathData.points, graphics.drawPathData.points, 0.005f,
				String.format("drawPath: Scaled value for path data must match value %s", pathData.points)); //$NON-NLS-1$
	}

	private static void validateDrawPoint(RecordingSwtGraphics graphics, Point expected) {
		validatePoint(graphics.drawPoint, expected);
	}

	private static void validateDrawPolygon(RecordingSwtGraphics graphics, int[] polygon) {
		assertArrayEquals(polygon, graphics.drawPolygon,
				String.format("drawFocus: Scaled value for polygon must match value %s", polygon)); //$NON-NLS-1$
	}

	private static void validateDrawRectangle(RecordingSwtGraphics graphics, Rectangle expected) {
		validateRect(graphics.drawRectangle, expected);
	}

	private static void validateDrawRoundRectangle(RecordingSwtGraphics graphics, Rectangle expected,
			Point expectedArc) {
		validateRect(graphics.drawRoundRectangle, expected);
		assertEquals(expectedArc.x, graphics.drawRoundRectangleArc.x,
				String.format("drawRoundRectangle: Scaled value for arc width must match value %s", expectedArc.x)); //$NON-NLS-1$
		assertEquals(expectedArc.y, graphics.drawRoundRectangleArc.y,
				String.format("drawRoundRectangle: Scaled value for arc height must match value %s", expectedArc.y)); //$NON-NLS-1$
	}

	private static void validateFillArc(RecordingSwtGraphics graphics, Rectangle expected) {
		validateRect(graphics.fillArc, expected);
	}

	private static void validateFillGradient(RecordingSwtGraphics graphics, Rectangle expected) {
		validateRect(graphics.fillGradient, expected);
	}

	private static void validateFillOval(RecordingSwtGraphics graphics, Rectangle expected) {
		validateRect(graphics.fillOval, expected);
	}

	private static void validateFillPath(RecordingSwtGraphics graphics, PathData pathData) {
		// check fillPath
		assertArrayEquals(pathData.points, graphics.fillPathData.points, 0.005f,
				String.format("fillPath: Scaled value for path data must match value %s", pathData.points)); //$NON-NLS-1$
	}

	private static void validateFillPolygon(RecordingSwtGraphics graphics, int[] polygon) {
		// check fillPolygon
		assertArrayEquals(polygon, graphics.fillPolygon,
				String.format("fillFocus: Scaled value for polygon must match value %s", polygon)); //$NON-NLS-1$
	}

	private static void validateFillRectangle(RecordingSwtGraphics graphics, Rectangle expected) {
		validateRect(graphics.fillRectangle, expected);
	}

	private static void validateFillRoundRectangle(RecordingSwtGraphics graphics, Rectangle expected,
			Point expectedArc) {
		validateRect(graphics.fillRoundRectangle, expected);
		assertEquals(expectedArc.x, graphics.fillRoundRectangleArc.x,
				String.format("fillRoundRectangle: Scaled value for arc width must match value %s", expectedArc.x)); //$NON-NLS-1$
		assertEquals(expectedArc.y, graphics.fillRoundRectangleArc.y,
				String.format("fillRoundRectangle: Scaled value for arc height must match value %s", expectedArc.y)); //$NON-NLS-1$
	}

	private static RecordingSwtGraphics executeWithOneLayer(int monitorZoom, int diagramZoom,
			Consumer<ScaledGraphics> graphicsCall) {
		return executeWithOneLayer(monitorZoom, diagramZoom, graphicsCall, graphics -> {
		});
	}

	private static RecordingSwtGraphics executeWithOneLayer(int monitorZoom, int diagramZoom,
			Consumer<ScaledGraphics> graphicsCall, Consumer<RecordingSwtGraphics> initializeGraphics) {
		Display display = Display.getDefault();
		Image image = new Image(display, 100, 100);
		GC gc = new GC(image);
		RecordingSwtGraphics graphics = new RecordingSwtGraphics(gc);
		initializeGraphics.accept(graphics);
		ScaledGraphics scaledGraphics = new ScaledGraphics(graphics);
		scaledGraphics.scale(monitorZoom / 100d * diagramZoom / 100d);
		graphicsCall.accept(scaledGraphics);
		scaledGraphics.dispose();
		graphics.dispose();
		gc.dispose();
		image.dispose();
		return graphics;
	}

	private static RecordingSwtGraphics executeTranslatedWithOneLayer(int monitorZoom, int diagramZoom,
			Consumer<ScaledGraphics> graphicsCall) {
		return executeTranslatedWithOneLayer(monitorZoom, diagramZoom, graphicsCall, graphics -> {
		});
	}

	private static RecordingSwtGraphics executeTranslatedWithOneLayer(int monitorZoom, int diagramZoom,
			Consumer<ScaledGraphics> graphicsCall, Consumer<RecordingSwtGraphics> initializeGraphics) {
		Display display = Display.getDefault();
		Image image = new Image(display, 100, 100);
		GC gc = new GC(image);
		RecordingSwtGraphics graphics = new RecordingSwtGraphics(gc);
		initializeGraphics.accept(graphics);
		ScaledGraphics scaledGraphics = new ScaledGraphics(graphics);
		scaledGraphics.scale(monitorZoom / 100d * diagramZoom / 100d);
		scaledGraphics.translate(1f, 1f);
		graphicsCall.accept(scaledGraphics);
		scaledGraphics.dispose();
		graphics.dispose();
		gc.dispose();
		image.dispose();
		return graphics;
	}

	private static RecordingSwtGraphics executeWithTwoLayers(int monitorZoom, int diagramZoom,
			Consumer<ScaledGraphics> graphicsCall) {
		return executeWithTwoLayers(monitorZoom, diagramZoom, graphicsCall, graphics -> {
		});
	}

	private static RecordingSwtGraphics executeWithTwoLayers(int monitorZoom, int diagramZoom,
			Consumer<ScaledGraphics> graphicsCall, Consumer<RecordingSwtGraphics> initializeGraphics) {
		Display display = Display.getDefault();
		Image image = new Image(display, 100, 100);
		GC gc = new GC(image);
		RecordingSwtGraphics graphics = new RecordingSwtGraphics(gc);
		initializeGraphics.accept(graphics);
		ScaledGraphics scaledGraphics = new ScaledGraphics(graphics);
		scaledGraphics.scale(monitorZoom / 100d);
		ScaledGraphics scaledGraphics2 = new ScaledGraphics(scaledGraphics);
		scaledGraphics2.scale(diagramZoom / 100d);
		graphicsCall.accept(scaledGraphics2);
		scaledGraphics2.dispose();
		scaledGraphics.dispose();
		graphics.dispose();
		gc.dispose();
		image.dispose();
		return graphics;
	}

	private static RecordingSwtGraphics executeTranslatedWithTwoLayers(int monitorZoom, int diagramZoom,
			Consumer<ScaledGraphics> graphicsCall) {
		return executeTranslatedWithTwoLayers(monitorZoom, diagramZoom, graphicsCall, graphics -> {
		});
	}

	private static RecordingSwtGraphics executeTranslatedWithTwoLayers(int monitorZoom, int diagramZoom,
			Consumer<ScaledGraphics> graphicsCall, Consumer<RecordingSwtGraphics> initializeGraphics) {
		Display display = Display.getDefault();
		Image image = new Image(display, 100, 100);
		GC gc = new GC(image);
		RecordingSwtGraphics graphics = new RecordingSwtGraphics(gc);
		initializeGraphics.accept(graphics);
		ScaledGraphics scaledGraphics = new ScaledGraphics(graphics);
		scaledGraphics.scale(monitorZoom / 100d);
		ScaledGraphics scaledGraphics2 = new ScaledGraphics(scaledGraphics);
		scaledGraphics2.scale(diagramZoom / 100d);
		scaledGraphics2.translate(1f, 1f);
		graphicsCall.accept(scaledGraphics2);
		scaledGraphics2.dispose();
		scaledGraphics.dispose();
		graphics.dispose();
		gc.dispose();
		image.dispose();
		return graphics;
	}

	private static class RecordingSwtGraphics extends SWTGraphics {

		Point translation = new Point();
		Rectangle clipRect = new Rectangle();
		Rectangle setClipRect = new Rectangle();
		Rectangle drawArc = new Rectangle();
		Rectangle drawFocus = new Rectangle();
		Rectangle drawImage = new Rectangle();
		Point drawLine = new Point();
		Rectangle drawOval = new Rectangle();
		Point drawPoint = new Point();
		Rectangle drawRectangle = new Rectangle();
		Rectangle drawRoundRectangle = new Rectangle();
		Point drawRoundRectangleArc = new Point();
		PathData drawPathData = new PathData();
		int[] drawPolygon = {};
		Rectangle fillArc = new Rectangle();
		Rectangle fillGradient = new Rectangle();
		Rectangle fillOval = new Rectangle();
		Rectangle fillRectangle = new Rectangle();
		Rectangle fillRoundRectangle = new Rectangle();
		Point fillRoundRectangleArc = new Point();
		PathData fillPathData = new PathData();
		int[] fillPolygon = {};

		public RecordingSwtGraphics(GC gc) {
			super(gc);
			drawPathData.points = new float[0];
			fillPathData.points = new float[0];
		}

		@Override
		public void translate(int dx, int dy) {
			translation.setX(translation.x + dx);
			translation.setY(translation.y + dy);
		}

		@Override
		public Rectangle getClip(Rectangle rect) {
			// getClip does not utilize the fractional values of ScaledGraphics, so we must
			// ignore the translation here
			rect.setX(clipRect.x);
			rect.setY(clipRect.y);
			rect.setWidth(clipRect.width);
			rect.setHeight(clipRect.height);
			return rect;
		}

		@Override
		public void clipRect(Rectangle rect) {
			clipRect.setX(rect.x + translation.x);
			clipRect.setY(rect.y + translation.y);
			clipRect.setWidth(rect.width);
			clipRect.setHeight(rect.height);
		}

		@Override
		public void setClip(Rectangle rect) {
			setClipRect.setX(rect.x + translation.x);
			setClipRect.setY(rect.y + translation.y);
			setClipRect.setWidth(rect.width);
			setClipRect.setHeight(rect.height);
		}

		@Override
		public void drawArc(int x, int y, int width, int height, int offset, int length) {
			drawArc.setX(x + translation.x);
			drawArc.setY(y + translation.y);
			drawArc.setWidth(width);
			drawArc.setHeight(height);
		}

		@Override
		public void drawPoint(int x, int y) {
			drawPoint.setX(x + translation.x);
			drawPoint.setY(y + translation.y);
		}

		@Override
		public void drawFocus(int x, int y, int w, int h) {
			drawFocus.setX(x + translation.x);
			drawFocus.setY(y + translation.y);
			drawFocus.setWidth(w);
			drawFocus.setHeight(h);
		}

		@Override
		public void drawImage(Image srcImage, int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
			drawImage.setX(x2 + translation.x);
			drawImage.setY(y2 + translation.y);
			drawImage.setWidth(w2);
			drawImage.setHeight(h2);
		}

		@Override
		public void drawLine(int x1, int y1, int x2, int y2) {
			drawLine.setX(x1 + translation.x);
			drawLine.setY(y1 + translation.y);
		}

		@Override
		public void drawOval(int x, int y, int width, int height) {
			drawOval.setX(x + translation.x);
			drawOval.setY(y + translation.y);
			drawOval.setWidth(width);
			drawOval.setHeight(height);
		}

		@Override
		public void drawPath(Path path) {
			drawPathData = path.getPathData();
		}

		@Override
		public void drawPolygon(int[] points) {
			drawPolygon = points;
		}

		@Override
		public void drawRectangle(int x, int y, int width, int height) {
			drawRectangle.setX(x + translation.x);
			drawRectangle.setY(y + translation.y);
			drawRectangle.setWidth(width);
			drawRectangle.setHeight(height);
		}

		@Override
		public void drawRoundRectangle(Rectangle r, int arcWidth, int arcHeight) {
			drawRoundRectangle.setX(r.x + translation.x);
			drawRoundRectangle.setY(r.y + translation.y);
			drawRoundRectangle.setWidth(r.width);
			drawRoundRectangle.setHeight(r.height);
			drawRoundRectangleArc.setX(arcWidth);
			drawRoundRectangleArc.setY(arcHeight);
		}

		@Override
		public void fillArc(int x, int y, int width, int height, int offset, int length) {
			fillArc.setX(x + translation.x);
			fillArc.setY(y + translation.y);
			fillArc.setWidth(width);
			fillArc.setHeight(height);
		}

		@Override
		public void fillGradient(int x, int y, int w, int h, boolean vertical) {
			fillGradient.setX(x + translation.x);
			fillGradient.setY(y + translation.y);
			fillGradient.setWidth(w);
			fillGradient.setHeight(h);
		}

		@Override
		public void fillOval(int x, int y, int width, int height) {
			fillOval.setX(x + translation.x);
			fillOval.setY(y + translation.y);
			fillOval.setWidth(width);
			fillOval.setHeight(height);
		}

		@Override
		public void fillPath(Path path) {
			fillPathData = path.getPathData();
		}

		@Override
		public void fillPolygon(int[] points) {
			fillPolygon = points;
		}

		@Override
		public void fillRectangle(int x, int y, int width, int height) {
			fillRectangle.setX(x + translation.x);
			fillRectangle.setY(y + translation.y);
			fillRectangle.setWidth(width);
			fillRectangle.setHeight(height);
		}

		@Override
		public void fillRoundRectangle(Rectangle r, int arcWidth, int arcHeight) {
			fillRoundRectangle.setX(r.x + translation.x);
			fillRoundRectangle.setY(r.y + translation.y);
			fillRoundRectangle.setWidth(r.width);
			fillRoundRectangle.setHeight(r.height);
			fillRoundRectangleArc.setX(arcWidth);
			fillRoundRectangleArc.setY(arcHeight);
		}
	}
}
