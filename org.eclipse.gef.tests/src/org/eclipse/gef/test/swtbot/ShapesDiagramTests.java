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

import java.util.List;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;

import org.eclipse.gef.GraphicalEditPart;

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

	@Override
	protected String getWizardId() {
		return "org.eclipse.gef.examples.shapes.ShapesCreationWizard";
	}

	@Override
	protected String getFileName() {
		return "shapesExample1.shapes";
	}

}
