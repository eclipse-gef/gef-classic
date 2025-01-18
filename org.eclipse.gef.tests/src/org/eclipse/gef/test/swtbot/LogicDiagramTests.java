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

import org.eclipse.swtbot.eclipse.gef.finder.matchers.IsInstanceOf;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefViewer;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.rulers.RulerProvider;

import org.eclipse.gef.examples.logicdesigner.edit.LogicEditPart;
import org.eclipse.gef.examples.logicdesigner.edit.LogicLabelEditPart;
import org.eclipse.gef.examples.logicdesigner.model.LED;
import org.eclipse.gef.examples.logicdesigner.model.LogicGuide;
import org.eclipse.gef.examples.logicdesigner.model.LogicRuler;
import org.eclipse.gef.examples.logicdesigner.rulers.LogicRulerProvider;

import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
public class LogicDiagramTests extends AbstractSWTBotEditorTests {

	/**
	 * Tests that when the ruler is enabled, moving the ruler also moves all
	 * attached edit parts.
	 */
	@Test
	public void testWithRulers() {
		bot.menu("View").menu("Rulers").click();

		SWTBotGefEditor editor = bot.gefEditor("emptyModel1.logic");

		LogicRulerProvider provider = getVerticalRuler(editor.getSWTBotGefViewer());
		LogicRuler ruler = (LogicRuler) provider.getRuler();
		LogicGuide guide = new LogicGuide();

		// Create a new guide and add it to the vertical ruler
		guide.setPosition(200);
		UIThreadRunnable.syncExec(() -> ruler.addGuide(guide));

		// Add a new edit part to the primary viewer
		editor.activateTool("XOR Gate");
		editor.click(50, 50);

		List<SWTBotGefEditPart> editParts = editor.mainEditPart().children();
		assertEquals(editParts.size(), 1);
		SWTBotGefEditPart editPart = editParts.get(0);

		// Attach the edit part to the horizontal ruler line
		UIThreadRunnable.syncExec(() -> {
			editor.drag(editPart, 50, 200);
			forceUpdate(editor.getSWTBotGefViewer());
		});

		IFigure figure = ((LogicEditPart) editPart.part()).getFigure();
		assertEquals(figure.getLocation(), new Point(50, 200));

		// TODO ptziegler: Create SWTBotGefViewer over RulerViewer and do a simple drag?
		UIThreadRunnable.syncExec(() -> {
			provider.getMoveGuideCommand(guide, -100).execute();
			forceUpdate(editor.getSWTBotGefViewer());
		});

		assertEquals(figure.getLocation(), new Point(50, 100));
	}

	/**
	 * Tests that when the grid has been enabled, moving an edit part always makes
	 * it snap to the grid lines.
	 */
	@Test
	public void testWithGrid() {
		bot.menu("View").menu("Grid").click();

		SWTBotGefEditor editor = bot.gefEditor("emptyModel1.logic");
		editor.activateTool("LED");
		editor.click(3, 3);

		List<SWTBotGefEditPart> editParts = editor.mainEditPart().children();
		assertEquals(editParts.size(), 1);
		SWTBotGefEditPart editPart = editParts.get(0);

		IFigure figure = ((LogicEditPart) editPart.part()).getFigure();
		assertEquals(figure.getLocation(), new Point(3, 1));

		UIThreadRunnable.syncExec(() -> {
			editor.drag(editPart, 17, 17);
			forceUpdate(editor.getSWTBotGefViewer());
		});

		assertEquals(figure.getLocation(), new Point(12, 20), "Part is not on grid line");
	}

	/**
	 * Tests that when "Snap to Geometry" has been enabled, moving an edit part
	 * always makes it snap to one of the sides of a different part.
	 */
	@Test
	public void testWithSnapToGrid() {
		bot.menu("View").menu("Snap to Geometry").click();

		SWTBotGefEditor editor = bot.gefEditor("emptyModel1.logic");
		editor.activateTool("LED");
		editor.click(50, 48);

		editor.activateTool("Label");
		editor.click(200, 200);

		List<SWTBotGefEditPart> editParts = editor.editParts(IsInstanceOf.instanceOf(LogicLabelEditPart.class));
		assertEquals(editParts.size(), 1);
		SWTBotGefEditPart editPart = editParts.get(0);

		IFigure figure = ((LogicEditPart) editParts.get(0).part()).getFigure();
		assertEquals(figure.getLocation(), new Point(200, 200));

		UIThreadRunnable.syncExec(() -> {
			editor.drag(editPart, 200, 45);
			forceUpdate(editor.getSWTBotGefViewer());
		});

		assertEquals(figure.getLocation(), new Point(200, 50), "Part didn't snap to LED");
	}

	/**
	 * Tests whether the LED figure is correctly updated when clicking the
	 * "Increment" or "Decrement" button.
	 */
	@Test
	public void testIncrementDecrementLED() {
		SWTBotGefEditor editor = bot.gefEditor("emptyModel1.logic");
		editor.activateTool("LED");
		editor.click(5, 5);

		List<SWTBotGefEditPart> editParts = editor.mainEditPart().children();
		assertEquals(editParts.size(), 1);

		SWTBotGefEditPart editPart = editParts.get(0);
		editPart.select();

		LED led = (LED) editPart.part().getModel();
		assertEquals(led.getValue(), 0, "Unexpected LED value");
		bot.toolbarButtonWithTooltip("Increment LED").click();
		assertEquals(led.getValue(), 1, "Unexpected LED value");
		bot.toolbarButtonWithTooltip("Decrement LED").click();
		assertEquals(led.getValue(), 0, "Unexpected LED value");
	}

	/**
	 * Tests whether the size of the figure can be adjusted to match the primary
	 * selection (i.e. the last selected item).
	 */
	@Test
	public void testMatchSizeOfPrimarySelection() {
		List<SWTBotGefEditPart> editParts = initMatchTest("HalfAdder", "FullAdder");
		IFigure first = ((LogicEditPart) editParts.get(0).part()).getFigure();
		IFigure second = ((LogicEditPart) editParts.get(1).part()).getFigure();

		assertNotEquals(first.getSize().width, second.getSize().width);
		assertNotEquals(first.getSize().height, second.getSize().height);
		bot.toolbarButtonWithTooltip("Match Size of Selected Objects to the Primary Selection").click();
		assertEquals(first.getSize().width, second.getSize().width);
		assertEquals(first.getSize().height, second.getSize().height);
	}

	/**
	 * Tests whether the width of the figure can be adjusted to match the primary
	 * selection (i.e. the last selected item).
	 */
	@Test
	public void testMatchWidthOfPrimarySelection() {
		List<SWTBotGefEditPart> editParts = initMatchTest("Label", "Or Gate");
		IFigure first = ((LogicEditPart) editParts.get(0).part()).getFigure();
		IFigure second = ((LogicEditPart) editParts.get(1).part()).getFigure();

		assertNotEquals(first.getSize().width, second.getSize().width);
		assertNotEquals(first.getSize().height, second.getSize().height);
		bot.toolbarButtonWithTooltip("Match Width of Selected Objects to the Primary Selection").click();
		assertEquals(first.getSize().width, second.getSize().width);
		assertNotEquals(first.getSize().height, second.getSize().height);
	}

	/**
	 * Tests whether the height of the figure can be adjusted to match the primary
	 * selection (i.e. the last selected item).
	 */
	@Test
	public void testMatchHeightOfPrimarySelection() {
		List<SWTBotGefEditPart> editParts = initMatchTest("Circuit", "V+");
		IFigure first = ((LogicEditPart) editParts.get(0).part()).getFigure();
		IFigure second = ((LogicEditPart) editParts.get(1).part()).getFigure();

		assertNotEquals(first.getSize().width, second.getSize().width);
		assertNotEquals(first.getSize().height, second.getSize().height);
		bot.toolbarButtonWithTooltip("Match Height of Selected Objects to the Primary Selection").click();
		assertNotEquals(first.getSize().width, second.getSize().width);
		assertEquals(first.getSize().height, second.getSize().height);
	}

	/**
	 * Convenience method for all "match X" tests. This method creates and selects
	 * two editor parts.
	 *
	 * @return The edit parts (excluding children) created by this method.
	 */
	private List<SWTBotGefEditPart> initMatchTest(String tool1, String tool2) {
		SWTBotGefEditor editor = bot.gefEditor("emptyModel1.logic");
		editor.activateTool(tool1);
		editor.click(5, 5);

		editor.activateTool(tool2);
		editor.click(205, 5);

		List<SWTBotGefEditPart> editParts = editor.mainEditPart().children();
		assertEquals(editParts.size(), 2);
		editor.select(editParts);
		return editParts;
	}

	/**
	 * Tests whether the figures can be adjusted so that the top sides have the same
	 * Y coordinates.
	 */
	@Test
	public void testAlignTop() {
		List<SWTBotGefEditPart> editParts = initAlignTest("HalfAdder", "FullAdder");
		IFigure first = ((LogicEditPart) editParts.get(0).part()).getFigure();
		IFigure second = ((LogicEditPart) editParts.get(1).part()).getFigure();

		assertNotEquals(first.getBounds().top(), second.getBounds().top());
		bot.toolbarButtonWithTooltip("Align Top").click();
		assertEquals(first.getBounds().top(), second.getBounds().top());
	}

	/**
	 * Tests whether the figures can be adjusted so that both have their center on
	 * the same Y coordinate.
	 */
	@Test
	public void testAlignMiddle() {
		List<SWTBotGefEditPart> editParts = initAlignTest("Label", "Or Gate");
		IFigure first = ((LogicEditPart) editParts.get(0).part()).getFigure();
		IFigure second = ((LogicEditPart) editParts.get(1).part()).getFigure();

		assertNotEquals(first.getBounds().getCenter().y, second.getBounds().getCenter().y);
		bot.toolbarButtonWithTooltip("Align Middle").click();
		assertEquals(first.getBounds().getCenter().y, second.getBounds().getCenter().y, 1.0);
	}

	/**
	 * Tests whether the figures can be adjusted so that the bottom sides have the
	 * same Y coordinates.
	 */
	@Test
	public void testAlignBottom() {
		List<SWTBotGefEditPart> editParts = initAlignTest("Circuit", "V+");
		IFigure first = ((LogicEditPart) editParts.get(0).part()).getFigure();
		IFigure second = ((LogicEditPart) editParts.get(1).part()).getFigure();

		assertNotEquals(first.getBounds().bottom(), second.getBounds().bottom());
		bot.toolbarButtonWithTooltip("Align Bottom").click();
		assertEquals(first.getBounds().bottom(), second.getBounds().bottom());
	}

	/**
	 * Convenience method for all "match X" tests. This method creates and selects
	 * two editor parts.
	 *
	 * @return The edit parts (excluding children) created by this method.
	 */
	private List<SWTBotGefEditPart> initAlignTest(String tool1, String tool2) {
		SWTBotGefEditor editor = bot.gefEditor("emptyModel1.logic");
		editor.activateTool(tool1);
		editor.click(5, 5);

		editor.activateTool(tool2);
		editor.click(205, 205);

		List<SWTBotGefEditPart> editParts = editor.mainEditPart().children();
		assertEquals(editParts.size(), 2);
		editor.select(editParts);
		return editParts;
	}

	/**
	 * Convenience method for getting the vertical ruler that is attached to the
	 * given viewer.
	 */
	protected static LogicRulerProvider getVerticalRuler(SWTBotGefViewer viewer) {
		SWTBotGefEditPart editPart = viewer.rootEditPart();
		EditPart gefEditPart = editPart.part();
		EditPartViewer gefViewer = gefEditPart.getViewer();
		return (LogicRulerProvider) gefViewer.getProperty(RulerProvider.PROPERTY_VERTICAL_RULER);
	}

	@Override
	protected String getWizardId() {
		return "org.eclipse.gef.examples.logic.wizard.new.file";
	}

	@Override
	protected String getFileName() {
		return "emptyModel1.logic";
	}
}
