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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;

import org.junit.Test;

@SuppressWarnings("nls")
public class FlowDiagramTests extends AbstractSWTBotEditorTests {

	/**
	 * Tests whether new palette items can be added to the viewer.
	 */
	@Test
	public void testAddActivity() {
		SWTBotGefEditor editor = bot.gefEditor("GEF Flow Example");

		editor.activateTool("Parallel Activity");
		assertEquals(editor.getEditPart("Sleep.....").sourceConnections().size(), 1);
		editor.getEditPart("Sleep.....").click();
		assertEquals(editor.getEditPart("Sleep.....").sourceConnections().size(), 2);

		editor.activateTool("Sequential Activity");
		assertEquals(editor.getEditPart("a 12").children().size(), 0);
		editor.getEditPart("a 12").click();
		assertEquals(editor.getEditPart("a 12").children().size(), 1);

		editor.activateTool("Activity");
		assertEquals(editor.getEditPart("a 12").children().size(), 1);
		editor.getEditPart("a 12").click();
		assertEquals(editor.getEditPart("a 12").children().size(), 2);
	}

	/**
	 * Tests whether an activity can be removed from the viewer.
	 */
	@Test
	public void testRemoveActivity() {
		SWTBotGefEditor editor = bot.gefEditor("GEF Flow Example");

		List<SWTBotGefEditPart> children = editor.getEditPart("Wake up").children();
		assertEquals(children.size(), 4);
		assertEquals(children.get(0), editor.getEditPart("Hit snooze button"));

		editor.getEditPart("Hit snooze button").select();
		editor.clickContextMenu("Delete");

		children = editor.getEditPart("Wake up").children();
		assertEquals(children.size(), 3);
		assertEquals(children.get(0), editor.getEditPart("Go back to sleep"));
	}

	/**
	 * Tests whether a new connection can be added between two activities.
	 */
	@Test
	public void testAddConnection() {
		SWTBotGefEditor editor = bot.gefEditor("GEF Flow Example");

		editor.activateTool("Parallel Activity");
		assertEquals(editor.getEditPart("Sleep.....").sourceConnections().size(), 1);
		assertEquals(editor.getEditPart("Wake up").targetConnections().size(), 1);

		editor.activateTool("Connection Creation");
		editor.getEditPart("Sleep.....").click();
		editor.getEditPart("Wake up").click();
		assertEquals(editor.getEditPart("Sleep.....").sourceConnections().size(), 2);
		assertEquals(editor.getEditPart("Wake up").targetConnections().size(), 2);
	}

	/**
	 * Tests whether modifications to an activity can be both un- and re-done.
	 */
	@Test
	public void testUndoRedoCommand() {
		SWTBotGefEditor editor = bot.gefEditor("GEF Flow Example");

		assertNotNull(editor.getEditPart("Sleep....."));
		editor.getEditPart("Sleep.....").select();
		editor.clickContextMenu("Delete");
		assertNull(editor.getEditPart("Sleep....."));

		bot.toolbarButtonWithTooltip("Undo Delete").click();
		assertNotNull(editor.getEditPart("Sleep....."));

		bot.toolbarButtonWithTooltip("Redo Delete").click();
		assertNull(editor.getEditPart("Sleep....."));
	}

	/**
	 * Checks whether one or more elements can be selected with the marquee
	 * selection tool. Note that the current viewer is null at the start of this
	 * test, as it is only set when the cursor is moved inside the editor.
	 *
	 * @see <a href="https://github.com/eclipse-gef/gef-classic/issues/466">here</a>
	 */
	@Test
	public void testMarqueeSelection() {
		SWTBotGefEditor editor = bot.gefEditor("GEF Flow Example");
		editor.activateTool("Marquee");

		UIThreadRunnable.syncExec(() -> editor.drag(0, 0, 500, 500));
		assertFalse("At least one edit part should be selected", editor.selectedEditParts().isEmpty());
	}

	@Override
	protected String getWizardId() {
		return "org.eclipse.gef.examples.flow.wizard.new.file";
	}

	@Override
	protected String getFileName() {
		return "flowDiagram.flow";
	}

}