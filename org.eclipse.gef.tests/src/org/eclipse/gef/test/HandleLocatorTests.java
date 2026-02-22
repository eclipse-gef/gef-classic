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

package org.eclipse.gef.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.IScalablePane;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.handles.MoveHandleLocator;
import org.eclipse.gef.handles.RelativeHandleLocator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HandleLocatorTests {
	private IScalablePane root;
	private IFigure figure;

	@BeforeEach
	public void setUp() {
		root = new ScalableLayeredPane();
		root.setScale(2.0);

		figure = new Figure();
		figure.setBounds(new Rectangle(10, 15, 100, 105));

		root.add(figure);
	}

	@Test
	public void testRelocateMoveHandleLocatorWithZoom() {
		IFigure fig = new Figure();
		new MoveHandleLocator(figure).relocate(fig);

		Rectangle bounds = fig.getBounds();
		assertEquals(20, bounds.x);
		assertEquals(30, bounds.y);
		assertEquals(200, bounds.width);
		assertEquals(210, bounds.height);
	}

	@Test
	public void testRelocateRelativeHandleLocatorWithZoom() {
		IFigure fig = new Figure();
		new RelativeHandleLocator(figure, PositionConstants.SOUTH_EAST).relocate(fig);

		Rectangle bounds = fig.getBounds();
		assertEquals(220, bounds.x);
		assertEquals(240, bounds.y);
		assertEquals(0, bounds.width);
		assertEquals(0, bounds.height);
	}
}
