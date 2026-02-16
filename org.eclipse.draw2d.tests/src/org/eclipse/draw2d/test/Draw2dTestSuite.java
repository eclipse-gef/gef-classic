/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Alexander Shatalin (Borland) - Contribution for Bug 238874
 *******************************************************************************/
package org.eclipse.draw2d.test;

import org.eclipse.draw2d.graph.test.DirectedGraphLayoutTest;
import org.eclipse.draw2d.test.colors.HSLTest;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * The main test suite for Draw2d.
 *
 * @author Eric Bordeau
 */
@Suite
@SelectClasses({
	FanRouterTest.class,
	ShortestPathRoutingTest.class,
	XYLayoutTest.class,
	TextFlowWrapTest.class,
	LocalOptimizerTest.class,
	AdvancedGraphicsTests.class,
	FlowBorderTests.class,
	GraphicsClipping.class,
	PaintDamageEraseTest.class,
	LayeredPaneTest.class,
	ConnectionEndPointMoveTest.class,
	ImageUtilitiesTest.class,
	LookAheadTest.class,
	TextualTests.class,
	PointTests.class,
	DimensionTests.class,
	PointListTests.class,
	PrecisionDimensionTest.class,
	PrecisionPointTest.class,
	PrecisionPointListTest.class,
	PrecisionRectangleTest.class,
	ThumbnailTest.class,
	FigurePaintingTest.class,
	FigureUtilitiesTest.class,
	RectangleTest.class,
	ColorConstantTest.class,
	VectorTest.class,
	StraightTest.class,
	RelativeBendpointTest.class,
	GeometryTest.class,
	ScalablePolygonShapeTest.class,
	LayerTest.class,
	ShapeTest.class,
	InsetsTest.class,
	DirectedGraphLayoutTest.class,
	ScrollPaneTests.class,
	LabelTest.class,
	PrecisionTests.class,
	ScaledGraphicsTest.class,
	HSLTest.class
})
public class Draw2dTestSuite {
}
