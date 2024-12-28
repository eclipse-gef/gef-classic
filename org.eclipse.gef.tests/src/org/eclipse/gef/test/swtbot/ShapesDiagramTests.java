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

package org.eclipse.gef.test.swtbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.test.swtbot.utils.SWTBotGefPalette;
import org.eclipse.gef.ui.palette.PaletteColorProvider;

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

		List<SWTBotGefEditPart> editParts = editor.mainEditPart().children();
		assertEquals(editParts.size(), 1);
		SWTBotGefEditPart editPart = editParts.get(0);

		IFigure figure = ((GraphicalEditPart) editPart.part()).getFigure();
		assertNotEquals(figure.getSize(), new Dimension(200, 200));

		UIThreadRunnable.syncExec(() -> {
			editPart.resize(PositionConstants.SOUTH_EAST, 200, 200);
			forceUpdate(editor.getSWTBotGefViewer());
		});

		assertEquals(figure.getSize(), new Dimension(200, 200));
	}

	@Test
	public void testCustomPalette() {
		bot.menu("Palette").menu("Use Custom Color Palette").click();

		SWTBotGefEditor editor = bot.gefEditor("shapesExample1.shapes");
		editor.activateTool("Ellipse");

		SWTBotGefPalette palette = new SWTBotGefPalette(editor.getSWTBotGefViewer());
		PaletteColorProvider colorProvider = palette.getColorProvider();

		assertEquals(colorProvider.getListHoverBackgroundColor(), ColorConstants.cyan);
		assertEquals(colorProvider.getListSelectedBackgroundColor(), ColorConstants.darkGreen);
		assertEquals(colorProvider.getButton(), ColorConstants.lightGray);
		assertEquals(colorProvider.getButtonDarker(), ColorConstants.gray);
		assertEquals(colorProvider.getButtonDarkest(), ColorConstants.darkGray);
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
