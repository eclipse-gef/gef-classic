/*******************************************************************************
 * Copyright (c) 2025 Patrick Ziegler and others.
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

package org.eclipse.draw2d.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MonitorAwareLightweightSystem;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HiDPITest extends BaseTestCase {
	private Shell shell;
	private FigureCanvas canvas;

	private Figure parent;
	private IFigure figure1;
	private IFigure figure2;

	@BeforeEach
	public void setUp() {
		shell = new Shell(SWT.NO_TRIM);
		shell.setSize(400, 400);
		shell.setLayout(new FillLayout());

		figure1 = new Figure();
		figure1.setBounds(Rectangle.SINGLETON.setBounds(50, 50, 100, 100));
		figure2 = new Figure();
		figure2.setBounds(Rectangle.SINGLETON.setBounds(200, 200, 50, 50));

		parent = new Figure();
		parent.setLayoutManager(new XYLayout());
		parent.add(figure1);
		parent.add(figure2);

		canvas = new FigureCanvas(shell, new MonitorAwareLightweightSystem());
		canvas.setContents(parent);
	}

	@AfterEach
	public void tearDown() {
		shell.dispose();
	}

	@Test
	public void test_FigureCanvas_setViewport1() {
		canvas.setViewport(new Viewport());
		// Only set the ScalableLayeredPane when there is something to set
		assertNull(canvas.getContents());
	}

	@Test
	public void test_FigureCanvas_setViewport2() {
		IFigure figure = new Figure();
		canvas.setViewport(new Viewport());
		canvas.setContents(figure);
		// Inject the ScalableLayeredPane when a new Viewport is set
		IFigure contents = canvas.getContents();
		assertTrue(contents instanceof ScalableLayeredPane);
		assertEquals(contents.getChildren().size(), 1);
		assertEquals(contents.getChildren().get(0), figure);
	}

	@Test
	public void test_FigureCanvas_setContents() {
		Figure figure = new Figure();
		canvas.setContents(figure);
		// Update contents of ScalableLayeredPane together with Viewport
		IFigure contents = canvas.getContents();
		assertTrue(contents instanceof ScalableLayeredPane);
		assertEquals(contents.getChildren().size(), 1);
		assertEquals(contents.getChildren().get(0), figure);
	}

	@Test
	public void test_Viewport_getContents() {
		IFigure contents = canvas.getContents();
		assertTrue(contents instanceof ScalableLayeredPane);
		assertEquals(contents.getChildren().size(), 1);
		assertEquals(contents.getChildren().get(0), parent);
	}

	@Test
	public void test_Viewport_getContents_Scale() {
		ScalableLayeredPane contents = (ScalableLayeredPane) canvas.getContents();
		assertEquals(contents.getScale(), 1.0);
		canvas.notifyListeners(SWT.ZoomChanged, createZoomEvent(200));
		assertEquals(contents.getScale(), 2.0);
	}

	@Test
	public void test_Contents_NoZoom() {
		Rectangle bounds1 = figure1.getBounds().getCopy();
		Rectangle bounds2 = figure2.getBounds().getCopy();

		assertEquals(50, 50, 100, 100, bounds1);
		assertEquals(200, 200, 50, 50, bounds2);

		figure1.translateToAbsolute(bounds1);
		figure2.translateToAbsolute(bounds2);

		assertEquals(50, 50, 100, 100, bounds1);
		assertEquals(200, 200, 50, 50, bounds2);
	}

	@Test
	public void test_Contents_200Zoom() {
		canvas.notifyListeners(SWT.ZoomChanged, createZoomEvent(200));

		Rectangle bounds1 = figure1.getBounds().getCopy();
		Rectangle bounds2 = figure2.getBounds().getCopy();

		assertEquals(50, 50, 100, 100, bounds1);
		assertEquals(200, 200, 50, 50, bounds2);

		figure1.translateToAbsolute(bounds1);
		figure2.translateToAbsolute(bounds2);

		assertEquals(100, 100, 200, 200, bounds1);
		assertEquals(400, 400, 100, 100, bounds2);
	}

	private static Event createZoomEvent(int zoom) {
		Event event = new Event();
		event.type = SWT.ZoomChanged;
		event.detail = zoom;
		return event;
	}
}
