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

package org.eclipse.gef.examples.shapes.handler;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipse.ui.IEditorPart;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.palette.PaletteViewer;

import org.eclipse.gef.examples.shapes.ShapesEditor;
import org.eclipse.gef.examples.shapes.palette.ShapesColorPalette;

/**
 * This handler manages the {@code Use Custom Palette} menu item which switches
 * between the default and the custom palette theme.
 */
public class ColorPaletteHandler {
	@Execute
	@SuppressWarnings("static-method")
	public void execute(@Active MPart activePart, MMenuItem menuItem) {
		IEditorPart editorPart = activePart.getContext().get(IEditorPart.class);
		GraphicalViewer graphicalViewer = editorPart.getAdapter(GraphicalViewer.class);
		PaletteViewer paletteViewer = graphicalViewer.getEditDomain().getPaletteViewer();
		if (menuItem.isSelected()) {
			paletteViewer.setColorPalette(new ShapesColorPalette());
		} else {
			paletteViewer.setColorPalette(null);
		}
		paletteViewer.getControl().redraw();
	}

	@CanExecute
	@SuppressWarnings("static-method")
	public boolean canExecute(@Active MPart activePart) {
		return activePart.getContext().get(IEditorPart.class) instanceof ShapesEditor;
	}
}
