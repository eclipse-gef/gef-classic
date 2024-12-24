/*******************************************************************************
 * Copyright (c) 2000, 2024 IBM Corporation and others.
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
package org.eclipse.gef.internal.ui.palette.editparts;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;

import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.ui.palette.PaletteViewerPreferences;
import org.eclipse.gef.ui.palette.editparts.PaletteEditPart;

public class GroupEditPart extends PaletteEditPart {

	/** Scrollpane border constant for icon and column layout mode **/
	private static final Border SCROLL_PANE_BORDER = new MarginBorder(2, 2, 2, 2);

	/** Scrollpane border constant for list and details layout mode **/
	private static final Border SCROLL_PANE_LIST_BORDER = new MarginBorder(2, 0, 2, 0);

	private int cachedLayout = -1;

	public GroupEditPart(PaletteContainer group) {
		super(group);
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	public IFigure createFigure() {
		Figure figure = new Figure();
		figure.setOpaque(true);
		figure.setBackgroundColor(getColorProvider().getListBackground());
		return figure;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		int layout = getLayoutSetting();
		if (cachedLayout == layout) {
			return;
		}
		cachedLayout = layout;
		LayoutManager manager;
		if (layout == PaletteViewerPreferences.LAYOUT_COLUMNS) {
			manager = new ColumnsLayout();
			getContentPane().setBorder(SCROLL_PANE_BORDER);
		} else if (layout == PaletteViewerPreferences.LAYOUT_ICONS) {
			PaletteContainerFlowLayout flow = new PaletteContainerFlowLayout();
			flow.setMajorSpacing(0);
			flow.setMinorSpacing(0);
			manager = flow;
			getContentPane().setBorder(SCROLL_PANE_BORDER);
		} else {
			manager = new ToolbarLayout();
			getContentPane().setBorder(SCROLL_PANE_LIST_BORDER);
		}
		getContentPane().setLayoutManager(manager);
	}

}
