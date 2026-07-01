/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
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
package org.eclipse.gef.ui.palette;

import java.util.List;

import org.eclipse.jface.action.Action;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.customize.PaletteCustomizerDialog;

/**
 * This action launches the PaletteCustomizerDialog for the given palette.
 *
 * @author Pratik Shah
 */
public class CustomizeAction extends Action {

	private final PaletteViewer paletteViewer;

	/**
	 * Constructor
	 *
	 * @param palette the palette which has to be customized when this action is run
	 */
	public CustomizeAction(PaletteViewer palette) {
		setText(PaletteMessages.MENU_OPEN_CUSTOMIZE_DIALOG);
		paletteViewer = palette;
	}

	/**
	 * Opens the Customizer Dialog for the palette
	 *
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
	public void run() {
		PaletteCustomizerDialog dialog = paletteViewer.getCustomizerDialog();
		List<? extends EditPart> list = paletteViewer.getSelectedEditParts();
		if (!list.isEmpty()) {
			PaletteEntry selection = (PaletteEntry) list.get(0).getModel();
			if (!(selection instanceof PaletteRoot)) {
				dialog.setDefaultSelection(selection);
			}
		}
		dialog.open();
	}

}
