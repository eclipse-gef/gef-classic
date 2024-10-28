/*******************************************************************************
 * Copyright (c) 2000, 2023 IBM Corporation and others.
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

import java.util.Map;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.UpdateListener;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PaintDamageEraseTest extends BaseTestCase implements UpdateListener {

	private FigureCanvas fc;
	protected IFigure contents;
	protected Shell shell;
	protected IFigure insideBox;
	protected Rectangle lastDamaged;
	protected Display d;
	protected IFigure container;
	private String errMsg;

	/**
	 * Tests a mixed move (x pos,y neg) with relative bounds.
	 *
	 */
	@Test
	public void testRelativeBoundsMixedMove2() {
		doIndividualSetup(true);

		doTestEraseBoxAfterMove(15, -15, "RelativeMixed2"); //$NON-NLS-1$
	}

	/**
	 * Tests a mixed move (x pos,y neg) with absolute bounds.
	 *
	 */
	@Test
	public void testAbsoluteBoundsMixedMove2() {
		doIndividualSetup(false);

		doTestEraseBoxAfterMove(15, -15, "AbsoluteMixed2"); //$NON-NLS-1$
	}

	/**
	 * Tests a mixed move (x neg,x pos) with relative bounds.
	 *
	 */
	@Test
	public void testRelativeBoundsMixedMove() {
		doIndividualSetup(true);

		doTestEraseBoxAfterMove(-15, 15, "RelativeMixed"); //$NON-NLS-1$
	}

	/**
	 * Tests a mixed move (x neg, y pos) with absolute bounds.
	 *
	 */
	@Test
	public void testAbsoluteBoundsMixedMove() {
		doIndividualSetup(false);

		doTestEraseBoxAfterMove(-15, 15, "AbsoluteMixed"); //$NON-NLS-1$
	}

	/**
	 * Tests a positive move with relative bounds.
	 *
	 */
	@Test
	public void testRelativeBoundsPositiveMove() {
		doIndividualSetup(true);

		doTestEraseBoxAfterMove(15, 15, "RelativePositive"); //$NON-NLS-1$
	}

	/**
	 * Tests a negative move with relative bounds.
	 *
	 */
	@Test
	public void testRelativeBoundsNegativeMove() {
		doIndividualSetup(true);

		doTestEraseBoxAfterMove(-15, -15, "RelativeNegative"); //$NON-NLS-1$
	}

	/**
	 * Tests a positive move with absolute bounds.
	 *
	 */
	@Test
	public void testAbsoluteBoundsPositiveMove() {
		doIndividualSetup(false);

		doTestEraseBoxAfterMove(15, 15, "AbsolutePositive"); //$NON-NLS-1$
	}

	/**
	 * Tests a negative move with absolute bounds.
	 *
	 */
	@Test
	public void testAbsoluteBoundsNegativeMove() {
		doIndividualSetup(false);

		doTestEraseBoxAfterMove(-15, -15, "AbsoluteNegative"); //$NON-NLS-1$
	}

	/**
	 * This method tests to see if the old box has been properly erased by moving
	 * the box by the given offsets and asserting that the damaged region is
	 * correct.
	 *
	 *
	 */
	public void doTestEraseBoxAfterMove(int xOffset, int yOffset, String msg) {
		insideBox.setBounds(insideBox.getBounds().getTranslated(xOffset, yOffset));

		performUpdate();

		assertNotNull(lastDamaged);

		int x, y, h, w;

		if (xOffset >= 0) {
			x = 25;
			w = xOffset + 25;
		} else {
			x = 25 + xOffset;
			w = Math.abs(xOffset) + 25;
		}

		if (yOffset >= 0) {
			y = 25;
			h = yOffset + 25;
		} else {
			y = 25 + yOffset;
			h = Math.abs(yOffset) + 25;
		}

		if (lastDamaged == null || x != lastDamaged.x || y != lastDamaged.y || h != lastDamaged.height
				|| w != lastDamaged.width) {
			errMsg += " " + msg; //$NON-NLS-1$
		}
	}

	protected void doIndividualSetup(boolean useRelativeCoordinates) {
		if (useRelativeCoordinates) {
			container = new FigureWithRelativeCoords();
		} else {
			container = new RectangleFigure();
		}

		container.setBackgroundColor(ColorConstants.red);
		container.setBorder(new MarginBorder(15));

		insideBox = new RectangleFigure();

		container.add(insideBox);

		insideBox.setBackgroundColor(ColorConstants.green);

		if (useRelativeCoordinates) {
			insideBox.setBounds(new Rectangle(10, 10, 25, 25));
		} else {
			insideBox.setBounds(new Rectangle(25, 25, 25, 25));
		}

		fc.setContents(container);

		performUpdate();
		container.getUpdateManager().addUpdateListener(this);
	}

	protected void doIndividualTearDown() {
		container.getUpdateManager().removeUpdateListener(this);
		fc.setContents(new Figure());

		insideBox = null;
		lastDamaged = null;
		container = null;
	}

	@BeforeEach
	public void setUp() throws Exception {
		errMsg = ""; //$NON-NLS-1$
		d = Display.getDefault();
		shell = new Shell(d);

		String appName = getClass().getName();
		appName = appName.substring(appName.lastIndexOf('.') + 1);

		shell.setText(appName);
		shell.setLayout(new FillLayout());

		fc = new FigureCanvas(shell);

		fc.setSize(200, 200);

		shell.open();
	}

	@AfterEach
	public void tearDown() throws Exception {
		doIndividualTearDown();

		shell.dispose();

		if (!errMsg.isEmpty()) {
			throw new AssertionError(errMsg);
		}
	}

	@Override
	public void notifyPainting(Rectangle damage, Map<IFigure, Rectangle> dirtyRegions) {
		lastDamaged = damage;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.draw2d.UpdateListener#notifyValidating()
	 */
	@Override
	public void notifyValidating() {
		// nothing
	}

	private void performUpdate() {
		container.getUpdateManager().performUpdate();
		waitEventLoop(shell, 100);
	}

}

class FigureWithRelativeCoords extends RectangleFigure {
	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}
}
