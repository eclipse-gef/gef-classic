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

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;

import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.editparts.PaletteAnimator;
import org.eclipse.gef.ui.palette.editparts.PaletteEditPart;
import org.eclipse.gef.ui.palette.editparts.PaletteToolbarLayout;

public class SliderPaletteEditPart extends PaletteEditPart {

	public SliderPaletteEditPart(PaletteRoot paletteRoot) {
		super(paletteRoot);
	}

	@Override
	public IFigure createFigure() {
		Figure figure = new Figure();
		figure.setOpaque(true);
		figure.setForegroundColor(getColorProvider().getListForeground());
		figure.setBackgroundColor(getColorProvider().getSliderPaletteEditPartBackground());
		return figure;
	}

	/**
	 * This method overrides super's functionality to do nothing.
	 *
	 * @see PaletteEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#registerVisuals()
	 */
	@Override
	protected void registerVisuals() {
		super.registerVisuals();
		PaletteAnimator controller = new PaletteAnimator(getViewer().getPaletteViewerPreferences());
		getViewer().setPaletteAnimator(controller);
		ToolbarLayout layout = new PaletteToolbarLayout();
		getFigure().setLayoutManager(layout);
		getFigure().addLayoutListener(controller);
	}

}
