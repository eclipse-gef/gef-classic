/*******************************************************************************
 * Copyright (c) 2025 IBM Corporation and others.
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

package org.eclipse.draw2d.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.ScaledGraphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ScaledGraphicsTest {

	static Stream<Arguments> drawSingleValueTestCombinations() {
		int[] inputs = { 5, 7, 10, 17, 20 };
		int[] monitorZooms = { 100, 125, 150, 175, 200 };
		int[] diagramZooms = { 100, 150, 200, 250, 300, 400 };

		return Arrays.stream(inputs).boxed()
				.flatMap(source -> Arrays.stream(monitorZooms).boxed().flatMap(monitorZoom -> Arrays
						.stream(diagramZooms).mapToObj(diagramZoom -> Arguments.of(source, monitorZoom, diagramZoom))));
	}

	@Test
	@SuppressWarnings("static-method")
	public void testDrawLineForRegression() {
		RecordingSwtGraphics swtGraphics = executeTranslatedWithOneLayer(200, 250,
				scaledGraphics -> scaledGraphics.drawLine(new Point(5, 5), new Point(5, 5 + 20)));
		validateDrawLine(swtGraphics, new Point(30, 30));
	}

	@ParameterizedTest
	@MethodSource("drawSingleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawLineWithInt(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(scaledGraphics -> scaledGraphics.drawLine(source, source, source, source + 5));
	}

	@ParameterizedTest
	@MethodSource("drawSingleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawLineWithIntTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(scaledGraphics -> scaledGraphics.drawLine(source, source, source, source + 10));
	}

	@ParameterizedTest
	@MethodSource("drawSingleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawLineWithPoint(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.execute(
				scaledGraphics -> scaledGraphics.drawLine(new Point(source, source), new Point(source, source + 15)));
	}

	@ParameterizedTest
	@MethodSource("drawSingleValueTestCombinations")
	@SuppressWarnings("static-method")
	public void testDrawLineWithPointTranslated(int source, int monitorZoom, int diagramZoom) {
		ScaledGraphicsValidation validation = new ScaledGraphicsValidation(monitorZoom, diagramZoom);
		validation.executeTranslated(
				scaledGraphics -> scaledGraphics.drawLine(new Point(source, source), new Point(source, source + 20)));
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
			validateDrawLine(graphics2, graphics1.drawLine1);
		}
	}

	private static void validateDrawLine(RecordingSwtGraphics graphics, Point expected) {
		// check drawLine
		assertEquals(expected.x, graphics.drawLine1.x,
				String.format("drawLine: Scaled value for x1 must match %s", expected.x)); //$NON-NLS-1$
		assertEquals(expected.y, graphics.drawLine1.y,
				String.format("drawLine: Scaled value for y1 must match scaled value %s", expected.y)); //$NON-NLS-1$
	}

	private static RecordingSwtGraphics executeWithOneLayer(int monitorZoom, int diagramZoom,
			Consumer<ScaledGraphics> graphicsCall) {
		Display display = Display.getDefault();
		Image image = new Image(display, 100, 100);
		GC gc = new GC(image);
		RecordingSwtGraphics graphics = new RecordingSwtGraphics(gc);
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
		Display display = Display.getDefault();
		Image image = new Image(display, 100, 100);
		GC gc = new GC(image);
		RecordingSwtGraphics graphics = new RecordingSwtGraphics(gc);
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
		Display display = Display.getDefault();
		Image image = new Image(display, 100, 100);
		GC gc = new GC(image);
		RecordingSwtGraphics graphics = new RecordingSwtGraphics(gc);
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
		Display display = Display.getDefault();
		Image image = new Image(display, 100, 100);
		GC gc = new GC(image);
		RecordingSwtGraphics graphics = new RecordingSwtGraphics(gc);
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
		Point drawLine1 = new Point();
		Rectangle drawOval = new Rectangle();

		public RecordingSwtGraphics(GC gc) {
			super(gc);
		}

		@Override
		public void translate(int dx, int dy) {
			translation.setX(dx);
			translation.setY(dy);
		}

		@Override
		public void drawLine(int x1, int y1, int x2, int y2) {
			drawLine1.setX(x1 + translation.x);
			drawLine1.setY(y1 + translation.y);
		}

		@Override
		public void drawOval(int x, int y, int width, int height) {
			drawOval.setX(x + translation.x);
			drawOval.setY(y + translation.y);
			drawOval.setWidth(width);
			drawOval.setHeight(height);
		}
	}
}
