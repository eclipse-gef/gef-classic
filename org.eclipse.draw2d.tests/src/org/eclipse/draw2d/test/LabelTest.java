/*******************************************************************************
 * Copyright (c) 2011, 2025 Google, Inc. and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Google, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.test.utils.TestFigure;
import org.eclipse.draw2d.test.utils.TestLogger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author lobas_av
 */
public class LabelTest extends BaseTestCase {

	private static Font testFont;

	@BeforeAll
	public static void setUpAll() {
		testFont = new Font(null, "", 100, SWT.NONE); //$NON-NLS-1$
	}

	@AfterAll
	public static void tearDownAll() {
		testFont.dispose();
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Test
	//
	////////////////////////////////////////////////////////////////////////////
	@Test
	@SuppressWarnings("static-method")
	public void test_text() throws Exception {
		// check text for new empty Label
		assertEquals("", new Label().getText()); //$NON-NLS-1$
		//
		// check text for Label created by constructor Label(String)
		assertEquals("Column: 1", new Label("Column: 1").getText()); //$NON-NLS-1$ //$NON-NLS-2$
		//
		Label label = new Label();
		//
		// check work setText()/getText()
		label.setText("123ZzzzZ"); //$NON-NLS-1$
		assertEquals("123ZzzzZ", label.getText()); //$NON-NLS-1$
		//
		// check work setText()/getText()
		label.setText("Row: 0"); //$NON-NLS-1$
		assertEquals("Row: 0", label.getText()); //$NON-NLS-1$
		//
		// check work setText()/getText()
		label.setText(null);
		assertEquals("", label.getText()); //$NON-NLS-1$
	}

	@Test
	@SuppressWarnings("static-method")
	public void test_resetState() throws Exception {
		TestLogger actualLogger = new TestLogger();
		//
		TestFigure parentFigure = new TestFigure(actualLogger);
		//
		TestLogger expectedLogger = new TestLogger();
		//
		Label label = new Label();
		parentFigure.add(label);
		actualLogger.clear();
		//
		// check no reset state during setText() if text not change
		label.setText(""); //$NON-NLS-1$
		actualLogger.assertEmpty();
		//
		// check no reset state during setText() if text not change
		label.setText(null);
		actualLogger.assertEmpty();
		//
		// check reset state during setText()
		label.setText("123"); //$NON-NLS-1$
		expectedLogger.log("invalidate()"); //$NON-NLS-1$
		expectedLogger.log("repaint(0, 0, 0, 0)"); //$NON-NLS-1$
		actualLogger.assertEquals(expectedLogger);
		//
		// check no reset state during setText() if text not change
		label.setText("123"); //$NON-NLS-1$
		actualLogger.assertEmpty();
		//
		// check reset state during setText()
		label.setText("231"); //$NON-NLS-1$
		expectedLogger.log("invalidate()"); //$NON-NLS-1$
		expectedLogger.log("repaint(0, 0, 0, 0)"); //$NON-NLS-1$
		actualLogger.assertEquals(expectedLogger);
		//
		// check reset state during setText()
		label.setText(null);
		expectedLogger.log("invalidate()"); //$NON-NLS-1$
		expectedLogger.log("repaint(0, 0, 0, 0)"); //$NON-NLS-1$
		actualLogger.assertEquals(expectedLogger);
	}

	@Test
	@SuppressWarnings("static-method")
	public void test_getPreferredSize() throws Exception {
		Label label = new Label();
		assertTextSize(label);
		Dimension size1 = label.getPreferredSize();
		//
		// check calc preferred size if text is changed
		label.setText("1234"); //$NON-NLS-1$
		Dimension size2 = label.getPreferredSize();
		assertTextSize(label);
		assertNotSame(size1, size2);
		assertSame(size2, label.getPreferredSize());
		//
		// check calc preferred size if font is changed
		label.setFont(testFont);
		assertNotSame(size2, label.getPreferredSize());
		assertTextSize(label);
		//
		// check calc preferred size if set border
		label.setBorder(new CompoundBorder(new LineBorder(), new MarginBorder(2)));
		assertTextSize(label);
	}

	private static final void assertTextSize(Label label) throws Exception {
		// create calc GC
		GC gc = new GC(Display.getDefault());
		// set label font
		gc.setFont(label.getFont());
		// calc text size
		org.eclipse.swt.graphics.Point size = gc.textExtent(label.getText());
		// dispose calc GC
		gc.dispose();
		// get label border insets and calc expected preferred size
		Insets insets = label.getInsets();
		Dimension expectedSize = new Dimension(size).expand(insets.getWidth(), insets.getHeight());
		//
		assertEquals(expectedSize, label.getPreferredSize());
	}
}