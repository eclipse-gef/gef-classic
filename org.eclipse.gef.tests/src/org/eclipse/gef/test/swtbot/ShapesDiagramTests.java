/*******************************************************************************
 * Copyright (c) 2024, 2026 Patrick Ziegler and others.
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.test.swtbot.utils.SWTBotGefPalette;
import org.eclipse.gef.ui.palette.PaletteColorProvider;

import org.eclipse.gef.examples.shapes.palette.ShapesColorProvider;

import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
public class ShapesDiagramTests extends AbstractSWTBotEditorTests {

	/**
	 * Tests whether edit parts can be properly resized.
	 */
	@Test
	public void testResizeEditPart() {
		SWTBotGefEditor editor = bot.gefEditor("shapesExample1.shapes");

		editor.activateTool("Rectangle");
		editor.click(5, 5);
		waitEventLoop(0);

		List<SWTBotGefEditPart> editParts = editor.mainEditPart().children();
		assertEquals(editParts.size(), 1);
		SWTBotGefEditPart editPart = editParts.get(0);

		IFigure figure = ((GraphicalEditPart) editPart.part()).getFigure();
		assertNotEquals(figure.getSize(), new Dimension(200, 200));

		editPart.resize(PositionConstants.SOUTH_EAST, 200, 200);
		forceUpdate(editor.getSWTBotGefViewer());

		assertEquals(figure.getSize(), new Dimension(200, 200));
	}

	@Test
	public void testCustomPalette() {
		bot.menu("Palette").menu("Use Custom Color Palette").click();

		SWTBotGefEditor editor = bot.gefEditor("shapesExample1.shapes");
		SWTBotGefPalette palette = new SWTBotGefPalette(editor.getSWTBotGefViewer());

		EditPart drawerEditPart = palette.getEditPart("Shapes").part();
		Clickable drawerToggle = drawerEditPart.getAdapter(Clickable.class);
		assertNotNull(drawerToggle);

		EditPart toolEntryEditPart = palette.getEditPart("Ellipse").part();
		Clickable toolEntryToggle = toolEntryEditPart.getAdapter(Clickable.class);
		assertNotNull(toolEntryToggle);

		Border drawerBorder = drawerToggle.getBorder();
		assertNotNull(drawerToggle);
		assertEquals(drawerBorder.getClass().getName(),
				"org.eclipse.gef.examples.shapes.palette.ShapesPaletteEditPartFactory$DrawerBackground");

		Border toolEntryBorder = toolEntryToggle.getBorder();
		assertNotNull(toolEntryBorder);
		assertEquals(toolEntryBorder.getClass().getName(),
				"org.eclipse.gef.examples.shapes.palette.ShapesPaletteEditPartFactory$ToolEntryBackground");

		PaletteColorProvider colorProvider = palette.getColorProvider();
		assertEquals(colorProvider.getListHoverBackgroundColor(), ShapesColorProvider.COLOR_PALETTE_BACKGROUND);
		assertEquals(colorProvider.getListSelectedBackgroundColor(), ShapesColorProvider.COLOR_ENTRY_SELECTED);
		assertEquals(colorProvider.getListBackground(), ShapesColorProvider.COLOR_PALETTE_BACKGROUND);
	}

	@Override
	protected String getWizardId() {
		return "org.eclipse.gef.examples.shapes.ShapesCreationWizard";
	}

	@Override
	protected String getFileName() {
		return "shapesExample1.shapes";
	}

}
