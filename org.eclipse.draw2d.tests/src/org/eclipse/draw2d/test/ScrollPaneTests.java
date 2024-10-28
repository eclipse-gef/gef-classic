/*******************************************************************************
 * Copyright (c) 2024 Patrick Ziegler and others.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.UpdateListener;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ScrollPaneTests {
	private static final int WIDTH = 300;
	private static final int HEIGHT = 500;
	private static final int FILLER = 50;

	private Shell shell;
	private FigureCanvas figureCanvas;

	@BeforeEach
	public void setUp() {
		shell = new Shell();
		// Shell should be bigger to account for decorations
		shell.setSize(WIDTH + FILLER, HEIGHT + FILLER);
		figureCanvas = new FigureCanvas(shell);
		figureCanvas.setBackground(ColorConstants.white);
		figureCanvas.setSize(WIDTH, HEIGHT);
		shell.open();
		shell.layout();
	}

	@AfterEach
	public void tearDown() {
		shell.dispose();
	}

	/**
	 * With overlay-scrolling enabled, the scrollbars don't require any additional
	 * space.
	 */
	@Test
	public void testGtkWithOverlayScrolling() {
		assumeTrue("gtk".equals(SWT.getPlatform())); //$NON-NLS-1$

		figureCanvas.setHorizontalScrollBarVisibility(FigureCanvas.ALWAYS);
		figureCanvas.setVerticalScrollBarVisibility(FigureCanvas.ALWAYS);
		Rectangle bounds = getViewportBounds(SWT.SCROLLBAR_OVERLAY);
		assertEquals(WIDTH, bounds.width(), "Viewport width:"); //$NON-NLS-1$
		assertEquals(HEIGHT, bounds.height(), "Viewport height: "); //$NON-NLS-1$

		figureCanvas.setHorizontalScrollBarVisibility(FigureCanvas.NEVER);
		figureCanvas.setVerticalScrollBarVisibility(FigureCanvas.NEVER);
		bounds = getViewportBounds(SWT.SCROLLBAR_OVERLAY);
		assertEquals(WIDTH, bounds.width(), "Viewport width:"); //$NON-NLS-1$
		assertEquals(HEIGHT, bounds.height(), "Viewport height: "); //$NON-NLS-1$
	}

	/**
	 * With overlay scrolling disabled, the scrollbars require some of the available
	 * client area.
	 */
	@Test
	public void testGtkWithoutOverlayScrolling() {
		assumeTrue("gtk".equals(SWT.getPlatform())); //$NON-NLS-1$

		figureCanvas.setHorizontalScrollBarVisibility(FigureCanvas.ALWAYS);
		figureCanvas.setVerticalScrollBarVisibility(FigureCanvas.ALWAYS);
		Rectangle bounds = getViewportBounds(SWT.NONE);
		assertTrue(WIDTH > bounds.width(), "Expected non-empty scrollbar width"); //$NON-NLS-1$
		assertTrue(HEIGHT > bounds.height(), "Expected non-empty scrollbar height"); //$NON-NLS-1$

		figureCanvas.setHorizontalScrollBarVisibility(FigureCanvas.NEVER);
		figureCanvas.setVerticalScrollBarVisibility(FigureCanvas.NEVER);
		bounds = getViewportBounds(SWT.NONE);
		assertEquals(WIDTH, bounds.width(), "Viewport width:"); //$NON-NLS-1$
		assertEquals(HEIGHT, bounds.height(), "Viewport height: "); //$NON-NLS-1$
	}

	private Rectangle getViewportBounds(int scrollMode) {
		// force recalculation of scrollbars
		figureCanvas.setScrollbarsMode(scrollMode);
		shell.setVisible(false);
		shell.setVisible(true);

		AtomicBoolean layoutViewportCalled = new AtomicBoolean();
		UpdateListener layoutViewportListener = new UpdateListener.Stub() {
			@Override
			public void notifyValidating() {
				layoutViewportCalled.set(true);
			}
		};

		UpdateManager updateManager = figureCanvas.getLightweightSystem().getUpdateManager();
		updateManager.addUpdateListener(layoutViewportListener);
		updateManager.addInvalidFigure(new Figure());
		updateManager.performValidation();

		assertTrue(layoutViewportCalled.get(), "FigureCanvas.layoutViewport() has likely not been called!"); //$NON-NLS-1$
		return figureCanvas.getViewport().getBounds();
	}
}
