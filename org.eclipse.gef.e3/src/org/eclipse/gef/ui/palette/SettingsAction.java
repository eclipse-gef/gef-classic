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
package org.eclipse.gef.ui.palette;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;

import org.eclipse.gef.internal.ui.palette.InternalPaletteSettingsDialog;

/**
 * An action to launch the Settings dialog for the given palette.
 *
 * @author Pratik Shah
 */
public class SettingsAction extends Action {

	private final PaletteViewer paletteViewer;

	/**
	 * Constructor
	 *
	 * @param palette The Palette which has to be customized when this action is run
	 */
	public SettingsAction(PaletteViewer palette) {
		setText(PaletteMessages.MENU_OPEN_SETTINGS_DIALOG);
		paletteViewer = palette;
	}

	/**
	 * Opens the Settings dialog
	 *
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		Dialog settings = new InternalPaletteSettingsDialog(paletteViewer.getControl().getShell(),
				paletteViewer.getPaletteViewerPreferences());
		settings.open();
	}

}
