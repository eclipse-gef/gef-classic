/*******************************************************************************
 * Copyright (c) 2011, 2025 Google, Inc. and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Google, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.test.utils.TestFigure;
import org.eclipse.draw2d.test.utils.TestLogger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
public class FigurePaintingTest extends BaseTestCase {
	private TestLogger expectedLogger;
	private TestLogger actualLogger;
	private Figure testFigure;

	@BeforeEach
	public void setUp() {
		expectedLogger = new TestLogger();
		actualLogger = new TestLogger();
		testFigure = new TestFigure(actualLogger);
	}

	@Test
	public void testRepaint() {
		testFigure.setBounds(new Rectangle(10, 11, 50, 78));
		actualLogger.clear();
		//
		// check repaint from figure fully
		testFigure.repaint();
		expectedLogger.log("repaint(10, 11, 50, 78)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no repaint from invisible figure
		testFigure.setVisible(false);
		actualLogger.clear();
		//
		testFigure.repaint();
		actualLogger.assertEmpty();
	}

	@Test
	public void testAdd() {
		//
		// check reset state during add child figure with empty bounds
		testFigure.add(new Figure());
		expectedLogger.log("invalidate()");
		expectedLogger.log("repaint(0, 0, 0, 0)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check reset state during add(Figure) child figure with not empty bounds
		Figure testChildFigure = new Figure();
		testChildFigure.setBounds(new Rectangle(1, 2, 3, 4));
		testFigure.add(testChildFigure);
		expectedLogger.log("invalidate()");
		expectedLogger.log("repaint(1, 2, 3, 4)");
		actualLogger.assertEquals(expectedLogger);
	}

	@Test
	public void testRemove() {
		testFigure.setBounds(new Rectangle(10, 11, 50, 78));
		actualLogger.clear();
		//
		Figure testChildFigure = new Figure();
		testChildFigure.setBounds(new Rectangle(21, 17, 25, 24));
		testFigure.add(testChildFigure);
		testFigure.add(new Figure());
		actualLogger.clear();
		//
		// check reset state during remove child figure
		testFigure.remove(testChildFigure);
		expectedLogger.log("repaint(21, 17, 25, 24)");
		expectedLogger.log("invalidate()");
		actualLogger.assertEquals(expectedLogger);
		//
		// check reset state during remove all children figures
		testFigure.removeAll();
		expectedLogger.log("repaint(0, 0, 0, 0)");
		expectedLogger.log("invalidate()");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no reset state during remove if not childrens
		testFigure.removeAll();
		actualLogger.assertEmpty();
	}

	@Test
	public void testBounds() {
		// check reset state during setBounds()
		testFigure.setBounds(new Rectangle(1, 2, 3, 4));
		expectedLogger.log("erase()");
		expectedLogger.log("invalidate()");
		expectedLogger.log("repaint(1, 2, 3, 4)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no reset state during setBounds() if bounds not change
		testFigure.setBounds(new Rectangle(1, 2, 3, 4));
		actualLogger.assertEmpty();
		//
		// check no reset state during setSize(int, int) if bounds not change
		testFigure.setSize(3, 4);
		actualLogger.assertEmpty();
		//
		// check reset state during setSize(int, int)
		testFigure.setSize(1, 5);
		expectedLogger.log("erase()");
		expectedLogger.log("invalidate()");
		expectedLogger.log("repaint(1, 2, 1, 5)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check reset state during setSize(Dimension)
		testFigure.setSize(new Dimension(11, 12));
		expectedLogger.log("erase()");
		expectedLogger.log("invalidate()");
		expectedLogger.log("repaint(1, 2, 11, 12)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no reset state during setSize(Dimension) if bounds not change
		testFigure.setSize(new Dimension(11, 12));
		actualLogger.assertEmpty();
		//
		// check no reset state during setLocation(Point) if bounds not change
		testFigure.setLocation(new Point(1, 2));
		actualLogger.assertEmpty();
		//
		// check reset state during setLocation(Point)
		testFigure.setLocation(new Point(3, 7));
		expectedLogger.log("erase()");
		expectedLogger.log("repaint(3, 7, 11, 12)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check reset state during setLocation(Point)
		testFigure.setLocation(new Point());
		expectedLogger.log("erase()");
		expectedLogger.log("repaint(0, 0, 11, 12)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no reset state during setLocation(Point) if bounds not change
		testFigure.setLocation(new Point());
		actualLogger.assertEmpty();
	}

	@Test
	public void testBorder() {
		// check repaint during setBorder()
		LineBorder border = new LineBorder();
		testFigure.setBorder(border);
		expectedLogger.log("invalidate()");
		expectedLogger.log("repaint(0, 0, 0, 0)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no repaint during setBorder() if border not change
		testFigure.setBorder(border);
		actualLogger.assertEmpty();
		//
		// check repaint during setBorder()
		testFigure.setBorder(new LineBorder(7));
		expectedLogger.log("invalidate()");
		expectedLogger.log("repaint(0, 0, 0, 0)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check repaint during setBorder()
		testFigure.setBorder(null);
		expectedLogger.log("invalidate()");
		expectedLogger.log("repaint(0, 0, 0, 0)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no repaint during setBorder() if border not change
		testFigure.setBorder(null);
		actualLogger.assertEmpty();
	}

	@Test
	public void testBackground() {
		// check repaint during setBackgroundColor()
		testFigure.setBackgroundColor(ColorConstants.red);
		expectedLogger.log("repaint(0, 0, 0, 0)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no repaint during setBackgroundColor() if color not change
		testFigure.setBackgroundColor(ColorConstants.red);
		actualLogger.assertEmpty();
		//
		// check repaint during setBackgroundColor()
		testFigure.setBackgroundColor(ColorConstants.green);
		expectedLogger.log("repaint(0, 0, 0, 0)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check repaint during setBackgroundColor()
		testFigure.setBackgroundColor(null);
		expectedLogger.log("repaint(0, 0, 0, 0)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no repaint during setBackgroundColor() if color not change
		testFigure.setBackgroundColor(null);
		actualLogger.assertEmpty();
	}

	@Test
	public void testForeground() {
		// check repaint during setForegroundColor()
		testFigure.setForegroundColor(ColorConstants.red);
		expectedLogger.log("repaint(0, 0, 0, 0)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no repaint during setForegroundColor() if color not change
		testFigure.setForegroundColor(ColorConstants.red);
		actualLogger.assertEmpty();
		//
		// check repaint during setForegroundColor()
		testFigure.setForegroundColor(ColorConstants.green);
		expectedLogger.log("repaint(0, 0, 0, 0)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check repaint during setForegroundColor()
		testFigure.setForegroundColor(null);
		expectedLogger.log("repaint(0, 0, 0, 0)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no repaint during setForegroundColor() if color not change
		testFigure.setForegroundColor(null);
		actualLogger.assertEmpty();
	}

	@Test
	public void testFont() {
		// check reset state during setFont()
		testFigure.setFont(new Font(null, "Courier New", 12, SWT.BOLD));
		expectedLogger.log("invalidate()");
		expectedLogger.log("repaint(0, 0, 0, 0)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check reset state during setFont()
		testFigure.setFont(Display.getCurrent().getSystemFont());
		expectedLogger.log("invalidate()");
		expectedLogger.log("repaint(0, 0, 0, 0)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no reset state during setFont() if font not change
		testFigure.setFont(Display.getCurrent().getSystemFont());
		actualLogger.assertEmpty();
		//
		// check reset state during setFont()
		testFigure.setFont(null);
		expectedLogger.log("invalidate()");
		expectedLogger.log("repaint(0, 0, 0, 0)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no reset state during setFont() if font not change
		testFigure.setFont(null);
		actualLogger.assertEmpty();
	}

	@Test
	public void testCursor() {
		// check invoke updateCursor() during setCursor()
		testFigure.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HELP));
		expectedLogger.log("updateCursor()");
		actualLogger.assertEquals(expectedLogger);
		//
		// check invoke updateCursor() during setCursor()
		testFigure.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_CROSS));
		expectedLogger.log("updateCursor()");
		actualLogger.assertEquals(expectedLogger);
		//
		// check not invoke updateCursor() during setCursor() if cursor not change
		testFigure.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_CROSS));
		actualLogger.assertEmpty();
		//
		// check invoke updateCursor() during setCursor()
		testFigure.setCursor(null);
		expectedLogger.log("updateCursor()");
		actualLogger.assertEquals(expectedLogger);
		//
		// check not invoke updateCursor() during setCursor() if cursor not change
		testFigure.setCursor(null);
		actualLogger.assertEmpty();
	}

	@Test
	public void testOpaque() {
		// check repaint during setOpaque()
		testFigure.setOpaque(true);
		expectedLogger.log("repaint(0, 0, 0, 0)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no repaint during setOpaque() if opaque not change
		testFigure.setOpaque(true);
		actualLogger.assertEmpty();
		//
		// check repaint during setOpaque()
		testFigure.setOpaque(false);
		expectedLogger.log("repaint(0, 0, 0, 0)");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no repaint during setOpaque() if opaque not change
		testFigure.setOpaque(false);
		actualLogger.assertEmpty();
	}

	@Test
	public void testVisible() {
		// check reset state during setVisible()
		testFigure.setVisible(false);
		expectedLogger.log("erase()");
		expectedLogger.log("invalidate()");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no reset state during setVisible() if visible not change
		testFigure.setVisible(false);
		actualLogger.assertEmpty();
		//
		// check reset state during setVisible()
		testFigure.setVisible(true);
		expectedLogger.log("repaint(0, 0, 0, 0)");
		expectedLogger.log("invalidate()");
		actualLogger.assertEquals(expectedLogger);
		//
		// check no reset state during setVisible() if visible not change
		testFigure.setVisible(true);
		actualLogger.assertEmpty();
	}

	/**
	 * https://github.com/eclipse-gef/gef-classic/issues/721
	 */
	@Test
	public void testConcurrentModificationOnRepair() {
		testFigure = new TestFigure(actualLogger) {
			@Override
			public Rectangle getBounds() {
				getUpdateManager().addDirtyRegion(new Figure(), new Rectangle(25, 25, 25, 25));
				return bounds;
			}
		};
		testFigure.setOpaque(true);
		actualLogger.clear();

		testFigure.getUpdateManager().addDirtyRegion(testFigure, new Rectangle(0, 0, 50, 50));
		expectedLogger.log("repaint(0, 0, 50, 50)"); //$NON-NLS-1$
		testFigure.getUpdateManager().performUpdate();
		expectedLogger.log("repaint(25, 25, 25, 25)"); //$NON-NLS-1$
		actualLogger.assertEquals(expectedLogger);
	}
}