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

package org.eclipse.gef.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.Clickable;

import org.eclipse.gef.internal.ui.palette.editparts.PinnablePaletteStackEditPart;
import org.eclipse.gef.internal.ui.palette.editparts.ToolEntryEditPart;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteStack;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PinnablePaletteStackEditPartTests {
	private PinnablePaletteStackEditPart stackEditPart;
	private ToolEntryEditPart toolEntryEditPart1;
	private ToolEntryEditPart toolEntryEditPart2;
	private Clickable arrowFigure;

	private PaletteStack stack;
	private ToolEntry toolEntry1;
	private ToolEntry toolEntry2;

	private PaletteViewer viewer;
	private Shell shell;

	@BeforeEach
	public void setUp() {
		shell = new Shell();

		toolEntry1 = new ToolEntry("Entry 1", null, null, null) { //$NON-NLS-1$
		};
		toolEntry2 = new ToolEntry("Entry 2", null, null, null) { //$NON-NLS-1$
		};

		stack = new PaletteStack("Stack", null, null); //$NON-NLS-1$
		stack.add(toolEntry1);
		stack.add(toolEntry2);
		PaletteDrawer drawer = new PaletteDrawer("Drawer"); //$NON-NLS-1$
		drawer.add(stack);
		PaletteRoot root = new PaletteRoot();
		root.add(drawer);

		viewer = new PaletteViewer();
		viewer.createControl(shell);
		viewer.setPaletteRoot(root);

		toolEntryEditPart1 = (ToolEntryEditPart) viewer.getEditPartForModel(toolEntry1);
		toolEntryEditPart2 = (ToolEntryEditPart) viewer.getEditPartForModel(toolEntry2);
		stackEditPart = (PinnablePaletteStackEditPart) viewer.getEditPartForModel(stack);
		arrowFigure = stackEditPart.getAdapter(Clickable.class);
	}

	@AfterEach
	public void tearDown() {
		shell.dispose();
	}

	/**
	 * Collapsing the palette stack removes its figures from the model and therefore
	 * needs to de-select the associated edit-parts. Otherwise a NPE might be thrown
	 * when e.g. the palette viewer regains focus (when the EventDispatcher is
	 * accessed).
	 *
	 * @see <a href="https://github.com/eclipse-gef/gef-classic/issues/916">Issue
	 *      916</a>
	 */
	@Test
	public void testIssue916() {
		assertSame(toolEntryEditPart1, stackEditPart.getActiveEntry());
		arrowFigure.doClick();
		assertFalse(stackEditPart.isExpanded());
		assertSame(toolEntryEditPart1, stackEditPart.getActiveEntry());
		assertSame(toolEntryEditPart1, viewer.getFocusEditPart());

		viewer.setActiveTool(toolEntry2);
		assertSame(toolEntryEditPart2, stackEditPart.getActiveEntry());
		arrowFigure.doClick();
		assertFalse(stackEditPart.isExpanded());
		assertSame(toolEntryEditPart2, stackEditPart.getActiveEntry());
		assertSame(toolEntryEditPart2, viewer.getFocusEditPart());

		viewer.select(toolEntryEditPart1);
		assertSame(toolEntryEditPart2, stackEditPart.getActiveEntry());
		arrowFigure.doClick();
		assertFalse(stackEditPart.isExpanded());
		assertSame(toolEntryEditPart2, stackEditPart.getActiveEntry());
		assertSame(toolEntryEditPart2, viewer.getFocusEditPart());

		viewer.setActiveTool(toolEntry1);
		assertSame(toolEntryEditPart1, stackEditPart.getActiveEntry());
		viewer.select(toolEntryEditPart2);
		arrowFigure.doClick();
		assertFalse(stackEditPart.isExpanded());
		assertSame(toolEntryEditPart1, stackEditPart.getActiveEntry());
		assertSame(toolEntryEditPart1, viewer.getFocusEditPart());
	}
}
