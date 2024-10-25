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

package org.eclipse.gef.test.swtbot.utils;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefViewer;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;

import org.eclipse.gef.ui.palette.ColorPalette;
import org.eclipse.gef.ui.palette.PaletteViewer;

/**
 * Convenience class to create an SWTBot instance over the palette viewer.
 */
public class SWTBotGefPalette extends SWTBotGefViewer {

	public SWTBotGefPalette(SWTBotGefViewer gefViewer) throws WidgetNotFoundException {
		super(getPaletteViewer(gefViewer));
	}

	private static PaletteViewer getPaletteViewer(SWTBotGefViewer gefViewer) {
		SWTBotGefEditPart gefEditPart = gefViewer.rootEditPart();
		return gefEditPart.part().getViewer().getEditDomain().getPaletteViewer();
	}

	public PaletteViewer getPaletteViewer() {
		return (PaletteViewer) graphicalViewer;
	}

	public ColorPalette getColorPalette() {
		return getPaletteViewer().getColorPalette();
	}
}
