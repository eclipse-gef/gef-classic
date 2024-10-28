/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Alexander Shatalin (Borland) - initial API and implementation
 *    Alexander Nyssen (itemis) - Bugzilla #162082: testLinesIntersect()
 *******************************************************************************/
package org.eclipse.draw2d.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Geometry;
import org.eclipse.draw2d.geometry.PointList;

import org.junit.jupiter.api.Test;

public class GeometryTest {

	/*
	 * For Geometry.polygonContainsPoint tests
	 */
	private static final PointList RHOMB = new PointList(new int[] { 0, 2, 2, 0, 4, 2, 2, 4 });

	private static final PointList CONCAVE_PENTAGON = new PointList(new int[] { 0, 0, 0, 8, 4, 4, 8, 8, 8, 0 });

	private static final PointList CONCAVE_OCTAGON = new PointList(
			new int[] { 0, 0, 0, 4, 2, 4, 2, 2, 4, 2, 4, 4, 6, 4, 6, 0 });

	/*
	 * For Geometry.polylineContainsPoint tests
	 */
	private static final PointList POLYLINE = new PointList(new int[] { 0, 0, 1, 0, 6, 5 });

	private static final int TOLERANCE = 2;

	/*
	 * shifting all PointLists to this value
	 */
	private static final int POINT_LIST_SHIFT = TOLERANCE + 1;

	/*
	 * 8 = Max(x1, y1, x2, y2, ..) for all coordinates specified in the sample
	 * PointLists
	 */
	private static final int IMAGE_SIZE = 8 + POINT_LIST_SHIFT * 2;

	/**
	 * Testing points inside/outside the rhomb located in top half. Excluding points
	 * of RHOMB border - separate test present for it
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testTopRhombHalfPoints() {
		assertFalse(Geometry.polygonContainsPoint(RHOMB, 0, 1), "This point is outside the rhomb"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(RHOMB, 2, 1), "This point is inside the rhomb"); //$NON-NLS-1$
		assertFalse(Geometry.polygonContainsPoint(RHOMB, 4, 1), "This point is outside the rhomb"); //$NON-NLS-1$
	}

	/**
	 * Testing points inside/outside the rhomb located in bottop half. Excluding
	 * points of RHOMB border - separate test present for it
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testBottomRhombHalfPoints() {
		assertFalse(Geometry.polygonContainsPoint(RHOMB, 0, 3), "This point is outside the rhomb"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(RHOMB, 2, 3), "This point is inside the rhomb"); //$NON-NLS-1$
		assertFalse(Geometry.polygonContainsPoint(RHOMB, 4, 3), "This point is outside the rhomb"); //$NON-NLS-1$
	}

	/**
	 * Testing points inside/outside the rhomb located on the equator. Excluding
	 * points of RHOMB border - separate test present for it
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testRhombEquatorPoints() {
		assertFalse(Geometry.polygonContainsPoint(RHOMB, -1, 2), "This point is outside the rhomb"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(RHOMB, 2, 2), "This point is inside the rhomb"); //$NON-NLS-1$
		assertFalse(Geometry.polygonContainsPoint(RHOMB, 5, 2), "This point is outside the rhomb"); //$NON-NLS-1$
	}

	/**
	 * Testing points outside the rhomb located on top horizontal tangent line.
	 * Excluding points of RHOMB border - separate test present for it
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testTopRhombTangentPoints() {
		assertFalse(Geometry.polygonContainsPoint(RHOMB, 0, 0), "This point is outside the rhomb"); //$NON-NLS-1$
		assertFalse(Geometry.polygonContainsPoint(RHOMB, 4, 0), "This point is outside the rhomb"); //$NON-NLS-1$
	}

	/**
	 * Testing points outside the rhomb located on bottom horizontal tangent line.
	 * Excluding points of RHOMB border - separate test present for it
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testBottomRhombTangentPoints() {
		assertFalse(Geometry.polygonContainsPoint(RHOMB, 0, 4), "This point is outside the rhomb"); //$NON-NLS-1$
		assertFalse(Geometry.polygonContainsPoint(RHOMB, 4, 4), "This point is outside the rhomb"); //$NON-NLS-1$
	}

	/**
	 * Testing points of RHOMB border - all vertexes + one point on the edge
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testRhombBorderPoints() {
		assertTrue(Geometry.polygonContainsPoint(RHOMB, 0, 2), "This point is inside the rhomb"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(RHOMB, 0, 2), "This point is inside the rhomb"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(RHOMB, 2, 4), "This point is inside the rhomb"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(RHOMB, 2, 2), "This point is inside the rhomb"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(RHOMB, 1, 1), "This point is inside the rhomb"); //$NON-NLS-1$
	}

	/**
	 * Testing points inside/outside the pentagon located on equator of concave.
	 * Excluding points of CONCAVE_PENTAGON border - separate test present for it
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testConcavePentagonEquatorPoints() {
		assertFalse(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, -1, 6), "This point is outside the pentagon"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, 1, 6), "This point is inside the pentagon"); //$NON-NLS-1$
		assertFalse(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, 4, 6), "This point is outside the pentagon"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, 7, 6), "This point is inside the pentagon"); //$NON-NLS-1$
		assertFalse(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, 9, 6), "This point is outside the pentagon"); //$NON-NLS-1$
	}

	/**
	 * Testing points outside the pentagon located on top concave tangent. Excluding
	 * points of CONCAVE_PENTAGON border - separate test present for it
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testTopConcavePentagonTangentPoints() {
		assertFalse(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, -1, 8), "This point is outside the pentagon"); //$NON-NLS-1$
		assertFalse(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, 4, 8), "This point is outside the pentagon"); //$NON-NLS-1$
		assertFalse(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, 9, 8), "This point is outside the pentagon"); //$NON-NLS-1$
	}

	/**
	 * Testing points inside/outside the pentagon located on bottom concave tangent.
	 * Excluding points of CONCAVE_PENTAGON border - separate test present for it
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testBottomConcavePentagonTangentPoints() {
		assertFalse(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, -1, 4), "This point is outside the pentagon"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, 1, 4), "This point is inside the pentagon"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, 5, 4), "This point is inside the pentagon"); //$NON-NLS-1$
		assertFalse(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, 9, 4), "This point is outside the pentagon"); //$NON-NLS-1$
	}

	/**
	 * Testing points of CONCAVE_PENTAGON border - all vertexes + points on concave
	 * edges
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testConcavePentagonBorderPoints() {
		assertTrue(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, 0, 8), "This point is inside the pentagon"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, 2, 6), "This point is inside the pentagon"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, 4, 4), "This point is inside the pentagon"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, 6, 6), "This point is inside the pentagon"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(CONCAVE_PENTAGON, 8, 8), "This point is inside the pentagon"); //$NON-NLS-1$
	}

	/**
	 * Testing points located of the horizontal line containing one of the "concave"
	 * edges
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testConcaveOctagonBottomTangentPoints() {
		assertFalse(Geometry.polygonContainsPoint(CONCAVE_OCTAGON, -1, 2), "This point is outside the octagon"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(CONCAVE_OCTAGON, 0, 2), "This point is inside the octagon"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(CONCAVE_OCTAGON, 1, 2), "This point is inside the octagon"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(CONCAVE_OCTAGON, 2, 2), "This point is inside the octagon"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(CONCAVE_OCTAGON, 3, 2), "This point is inside the octagon"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(CONCAVE_OCTAGON, 4, 2), "This point is inside the octagon"); //$NON-NLS-1$
		assertTrue(Geometry.polygonContainsPoint(CONCAVE_OCTAGON, 5, 2), "This point is inside the octagon"); //$NON-NLS-1$
		assertFalse(Geometry.polygonContainsPoint(CONCAVE_OCTAGON, 7, 2), "This point is outside the octagon"); //$NON-NLS-1$
	}

	@SuppressWarnings("static-method")
	@Test
	public void testNotPolylinePoints() {
		// Point is outside of (polyline.bounds +- tolerance) rectangle
		assertFalse(Geometry.polylineContainsPoint(POLYLINE, 9, 5, TOLERANCE));
		// Point is inside of (polyline.bounds +- tolerance) rectangle, but
		// quite far from segments
		assertFalse(Geometry.polylineContainsPoint(POLYLINE, 1, 4, TOLERANCE));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testPolylinePoints() {
		// point is close to horizontal segment
		assertTrue(Geometry.polylineContainsPoint(POLYLINE, -1, 0, TOLERANCE));
		// point is close to the slopping segment
		assertTrue(Geometry.polylineContainsPoint(POLYLINE, 2, 3, TOLERANCE));
		// point is on polyline
		assertTrue(Geometry.polylineContainsPoint(POLYLINE, 0, 0, TOLERANCE));
		assertTrue(Geometry.polylineContainsPoint(POLYLINE, 1, 0, TOLERANCE));
		assertTrue(Geometry.polylineContainsPoint(POLYLINE, 2, 1, TOLERANCE));
	}

	/**
	 * Testing
	 * {@link Geometry#linesIntersect(int, int, int, int, int, int, int, int)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testLinesIntersect() {
		// line segments collapsed to single points
		assertTrue(Geometry.linesIntersect(0, 0, 0, 0, 0, 0, 3, 3), "Starting point on segment"); //$NON-NLS-1$
		assertTrue(Geometry.linesIntersect(0, 0, 3, 3, 0, 0, 0, 0), "Starting point on segment"); //$NON-NLS-1$
		assertFalse(Geometry.linesIntersect(-1, -1, -1, -1, 0, 0, 2, 2),
				"Single point next to starting point of segment"); //$NON-NLS-1$
		assertFalse(Geometry.linesIntersect(0, 0, 2, 2, -1, -1, -1, -1),
				"Single point next to starting point of segment"); //$NON-NLS-1$
		assertTrue(Geometry.linesIntersect(0, 0, 0, 0, 3, 3, -3, -3), "Mid point on segment"); //$NON-NLS-1$
		assertTrue(Geometry.linesIntersect(3, 3, -3, -3, 0, 0, 0, 0), "Mid point on segment"); //$NON-NLS-1$
		assertFalse(Geometry.linesIntersect(0, 1, 0, 1, 0, 0, 2, 2), "Single point next to mid point of segment"); //$NON-NLS-1$
		assertFalse(Geometry.linesIntersect(0, 0, 2, 2, 0, 1, 0, 1), "Single point next to mid point of segment"); //$NON-NLS-1$
		assertTrue(Geometry.linesIntersect(3, 3, 3, 3, 0, 0, 3, 3), "Ending point on segment"); //$NON-NLS-1$
		assertTrue(Geometry.linesIntersect(0, 0, 3, 3, 3, 3, 3, 3), "Ending point on segment"); //$NON-NLS-1$
		assertFalse(Geometry.linesIntersect(3, 3, 3, 3, 0, 0, 2, 2), "Single point next to end point of segment"); //$NON-NLS-1$
		assertFalse(Geometry.linesIntersect(0, 0, 2, 2, 3, 3, 3, 3), "Single point next to end point of segment"); //$NON-NLS-1$
		assertTrue(Geometry.linesIntersect(1, 1, 1, 1, 1, 1, 1, 1), "Identical points"); //$NON-NLS-1$
		assertFalse(Geometry.linesIntersect(1, 1, 1, 1, 2, 2, 2, 2), "Distinct points"); //$NON-NLS-1$

		// non-parallel
		assertTrue(Geometry.linesIntersect(0, 0, 5, 5, 0, 5, 5, 0), "Line segments cross at (2.5, 2.5)."); //$NON-NLS-1$
		assertTrue(Geometry.linesIntersect(-2, 1, 1, 1, 0, 0, 0, 3), "Line segments cross at (0, 1)."); //$NON-NLS-1$
		assertTrue(Geometry.linesIntersect(0, 0, 5, 5, 0, 0, 5, 0), "Line segments share starting point"); //$NON-NLS-1$
		assertTrue(Geometry.linesIntersect(0, 0, 5, 5, 0, 5, 5, 5), "Line segments share ending point"); //$NON-NLS-1$
		assertTrue(Geometry.linesIntersect(0, 0, 5, 5, 3, 3, 0, 5),
				"First line segment contains starting point of second one."); //$NON-NLS-1$
		assertFalse(Geometry.linesIntersect(0, 0, 2, 2, 0, 1, 0, 2),
				"Line segments are non-parallel and do not cross and should thus not be regarded as intersecting."); //$NON-NLS-1$

		// parallel
		assertFalse(Geometry.linesIntersect(0, 0, 5, 5, 1, 0, 6, 5),
				"Line segments are parallel but not co-linear and should thus not be regarded as intersecting."); //$NON-NLS-1$
		assertFalse(Geometry.linesIntersect(0, 0, 3, 3, 4, 0, 6, 2),
				"Line segments are parallel but not co-linear and should thus not be regarded as intersecting."); //$NON-NLS-1$

		// co-linear
		assertTrue(Geometry.linesIntersect(0, 0, 5, 5, 3, 3, 6, 6), "Line segments are co-linear, partly-overlapping."); //$NON-NLS-1$
		assertTrue(Geometry.linesIntersect(3, 3, 6, 6, 0, 0, 5, 5), "Line segments are co-linear, partly-overlapping."); //$NON-NLS-1$
		assertTrue(Geometry.linesIntersect(0, 0, 5, 5, 1, 1, 3, 3), "Line segments are co-linear, fully-overlapping."); //$NON-NLS-1$
		assertTrue(Geometry.linesIntersect(1, 1, 5, 5, -1, -1, 6, 6),
				"Line segments are co-linear, fully-overlapping."); //$NON-NLS-1$
		assertTrue(Geometry.linesIntersect(0, 0, 5, 5, 5, 5, 6, 6),
				"Line segments are co-linear, sharing ending/starting point."); //$NON-NLS-1$
		assertTrue(Geometry.linesIntersect(3, 3, 6, 6, 0, 0, 3, 3),
				"Line segments are co-linear, sharing starting/ending point."); //$NON-NLS-1$
		assertFalse(Geometry.linesIntersect(0, 0, 5, 5, 10, 10, 20, 20),
				"Line segments are co-linear but non-overlapping, and should thus not be regarded as intersecting."); //$NON-NLS-1$
		assertFalse(Geometry.linesIntersect(0, 0, 5, 5, -10, -10, -20, -20),
				"Line segments are co-linear but non-overlapping, and should thus not be regarded as intersecting."); //$NON-NLS-1$
	}

	public static void offTestDrawPolygons() {
		checkFilledPolygonPoints(translatePointList(RHOMB));
		checkFilledPolygonPoints(translatePointList(CONCAVE_PENTAGON));
		checkFilledPolygonPoints(translatePointList(CONCAVE_OCTAGON));
	}

	public static void offTestDrawPolylines() {
		checkPolylinePoints(translatePointList(RHOMB));
		checkPolylinePoints(translatePointList(CONCAVE_PENTAGON));
		checkPolylinePoints(translatePointList(CONCAVE_OCTAGON));
		checkPolylinePoints(translatePointList(POLYLINE));
	}

	private static PointList translatePointList(PointList original) {
		PointList translated = original.getCopy();
		translated.performTranslate(POINT_LIST_SHIFT, POINT_LIST_SHIFT);
		return translated;
	}

	private static void checkFilledPolygonPoints(PointList pointlist) {
		Display display = Display.getDefault();
		Image image = new Image(display, IMAGE_SIZE, IMAGE_SIZE);
		GC gc = new GC(image);
		cleanupImage(gc);
		gc.setBackground(ColorConstants.black);
		gc.setForeground(ColorConstants.black);
		gc.fillPolygon(pointlist.toIntArray());
		gc.drawPolygon(pointlist.toIntArray());
		gc.dispose();
		ImageData imageData = image.getImageData();
		for (int x = 0; x < IMAGE_SIZE; x++) {
			for (int y = 0; y < IMAGE_SIZE; y++) {
				boolean isPolygonPoint = imageData.getPixel(x, y) == 0;
				assertTrue(Geometry.polygonContainsPoint(pointlist, x, y) == isPolygonPoint,
						"Point (" + x + "," + y + ") is" + (isPolygonPoint ? " " : " not ") + "a point of polygon"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			}
		}
	}

	private static void checkPolylinePoints(PointList pointlist) {
		Display display = Display.getDefault();
		Image image = new Image(display, IMAGE_SIZE, IMAGE_SIZE);
		GC gc = new GC(image);
		cleanupImage(gc);
		gc.setForeground(ColorConstants.black);
		gc.drawPolyline(pointlist.toIntArray());
		gc.dispose();
		ImageData imageData = image.getImageData();
		for (int x = 0; x < IMAGE_SIZE; x++) {
			for (int y = 0; y < IMAGE_SIZE; y++) {
				if (imageData.getPixel(x, y) == 0) {
					assertTrue(Geometry.polylineContainsPoint(pointlist, x, y, TOLERANCE),
							"Point (" + x + "," + y + ") is a point of polyline"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
		}
	}

	// Filling initial image with white color
	private static void cleanupImage(GC gc) {
		gc.setBackground(ColorConstants.white);
		gc.setForeground(ColorConstants.white);
		gc.fillRectangle(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		gc.drawRectangle(0, 0, IMAGE_SIZE, IMAGE_SIZE);
	}

}
