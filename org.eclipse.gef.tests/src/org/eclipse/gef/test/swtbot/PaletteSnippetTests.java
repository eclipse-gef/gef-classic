/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
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

package org.eclipse.gef.test.swtbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PlatformUI;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Clickable;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.test.swtbot.utils.SWTBotGefPalette;
import org.eclipse.gef.ui.palette.PaletteColorProvider;
import org.eclipse.gef.ui.palette.PaletteViewer;

import org.eclipse.gef.examples.palette.PaletteSnippet3;
import org.eclipse.gef.examples.palette.PaletteSnippet3.ShapesColorProvider;

import org.junit.jupiter.api.Test;

public class PaletteSnippetTests extends AbstractSWTBotTests {

	@Test
	public void testPaletteWithCustomColors() throws CoreException {
		SWTBotGefPalette viewer = getPaletteViewer();

		EditPart drawerEditPart = viewer.getEditPart("Shapes").part(); //$NON-NLS-1$
		Clickable drawerToggle = drawerEditPart.getAdapter(Clickable.class);
		assertNotNull(drawerToggle);

		EditPart toolEntryEditPart = viewer.getEditPart("Ellipse").part(); //$NON-NLS-1$
		Clickable toolEntryToggle = toolEntryEditPart.getAdapter(Clickable.class);
		assertNotNull(toolEntryToggle);

		Border drawerBorder = drawerToggle.getBorder();
		assertNotNull(drawerToggle);
		assertEquals(drawerBorder.getClass().getName(),
				"org.eclipse.gef.examples.palette.PaletteSnippet3$ShapesPaletteEditPartFactory$DrawerBackground"); //$NON-NLS-1$

		Border toolEntryBorder = toolEntryToggle.getBorder();
		assertNotNull(toolEntryBorder);
		assertEquals(toolEntryBorder.getClass().getName(),
				"org.eclipse.gef.examples.palette.PaletteSnippet3$ShapesPaletteEditPartFactory$ToolEntryBackground"); //$NON-NLS-1$

		PaletteColorProvider colorProvider = viewer.getColorProvider();
		assertEquals(colorProvider.getListHoverBackgroundColor(), ShapesColorProvider.COLOR_PALETTE_BACKGROUND);
		assertEquals(colorProvider.getListSelectedBackgroundColor(), ShapesColorProvider.COLOR_ENTRY_SELECTED);
		assertEquals(colorProvider.getListBackground(), ShapesColorProvider.COLOR_PALETTE_BACKGROUND);
	}

	private SWTBotGefPalette getPaletteViewer() throws CoreException {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.gef.examples.view3"); //$NON-NLS-1$
		waitEventLoop(0);

		PaletteSnippet3 viewPart = (PaletteSnippet3) bot.viewById("org.eclipse.gef.examples.view3") //$NON-NLS-1$
				.getReference().getView(false);
		PaletteViewer viewer = viewPart.getViewer();

		return new SWTBotGefPalette(viewer);
	}
}
