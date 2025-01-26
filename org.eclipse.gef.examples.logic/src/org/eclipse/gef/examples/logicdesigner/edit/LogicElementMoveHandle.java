/*******************************************************************************
 * Copyright (c) 2025 Wolfgang Schedl.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Wolfgang Schedl - initial API and implementation
 *******************************************************************************/

package org.eclipse.gef.examples.logicdesigner.edit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.handles.MoveHandle;
import org.eclipse.gef.handles.MoveHandleLocator;

/**
 * A move handle that provides enhanced visual feedback with a thicker, rounded
 * border in the OS native selection color.
 *
 * Inspired by Eclipse 4DIAC's implementation:
 * https://github.com/eclipse-4diac/4diac-ide/blob/9e3d159e7004be2cb4b7875a605162a847b743ca/plugins/org.eclipse.fordiac.ide.gef/src/org/eclipse/fordiac/ide/gef/policies/ModifiedMoveHandle.java
 */
public class LogicElementMoveHandle extends MoveHandle {
	private static final int BASE_BORDER_WIDTH = 3;
	private static final int CORNER_RADIUS = 4;

	public LogicElementMoveHandle(GraphicalEditPart owner) {
		super(owner, new MoveHandleLocator(owner.getFigure()));
		setBorder(null);
		setCursor(Cursors.SIZEALL);
		initialize();
	}

	/**
	 * Initializes handle by setting the color to the OS native selection color.
	 */
	@Override
	protected void initialize() {
		setForegroundColor(Display.getCurrent().getSystemColor(org.eclipse.swt.SWT.COLOR_LIST_SELECTION));
	}

	/**
	 * Paints selection feedback with a rounded rectangle at the current zoom level.
	 */
	@Override
	protected void paintFigure(Graphics graphics) {
		int origWidth = graphics.getLineWidth();
		int origAntialias = graphics.getAntialias();

		graphics.setAntialias(SWT.ON);

		int scaledWidth = getScaledBorderWidth();
		graphics.setLineWidth(scaledWidth);

		Rectangle bounds = getBounds().getCopy();
		bounds.shrink(scaledWidth / 2, scaledWidth / 2);

		graphics.drawRoundRectangle(bounds, CORNER_RADIUS, CORNER_RADIUS);

		graphics.setLineWidth(origWidth);
		graphics.setAntialias(origAntialias);
	}

	/**
	 * Calculates the border width which is based on the current zoom level.
	 *
	 * @return the border width, minimum of 1 pixel
	 */
	protected int getScaledBorderWidth() {
		ZoomManager zoomManager = (ZoomManager) getOwner().getViewer().getProperty(ZoomManager.class.toString());
		if (zoomManager != null) {
			double zoom = zoomManager.getZoom();
			return (int) Math.max(1, Math.round(BASE_BORDER_WIDTH * zoom));

		}
		return BASE_BORDER_WIDTH;
	}

}