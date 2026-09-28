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

package org.eclipse.draw2d.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.internal.DrawableFigureUtilities;
import org.eclipse.draw2d.internal.InternalDraw2dUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DrawableFigureUtilitiesTest {
	private DrawableFigureUtilities figureUtilities;
	private Shell shell;
	private Font f;

	@BeforeEach
	public void setUp() {
		shell = new Shell();
		f = new Font(shell.getDisplay(), "Arial", 30, SWT.NORMAL); //$NON-NLS-1$
		figureUtilities = new DrawableFigureUtilities(shell);
	}

	@AfterEach
	public void tearDown() {
		shell.dispose();
		f.dispose();
	}

	/**
	 * Make sure the applied font is updated correctly after a zoom-changed event.
	 * Otherwise the local GC might return the font metrics for a different font.
	 *
	 * @see <a href=
	 *      "https://github.com/eclipse-gef/gef-classic/issues/1194">here</a>
	 */
	@Test
	public void testFontMetricsAfterZoomChange() {
		int descent = figureUtilities.getFontMetrics(f).getDescent();
		int currentZoom = (int) (InternalDraw2dUtils.calculateScale(shell) * 100);
		shell.notifyListeners(SWT.ZoomChanged, createZoomChangedEvent(currentZoom));
		assertEquals(descent, figureUtilities.getFontMetrics(f).getDescent());
	}

	private Event createZoomChangedEvent(int zoom) {
		Event event = new Event();
		event.type = SWT.ZoomChanged;
		event.widget = shell;
		event.detail = zoom;
		return event;
	}
}
