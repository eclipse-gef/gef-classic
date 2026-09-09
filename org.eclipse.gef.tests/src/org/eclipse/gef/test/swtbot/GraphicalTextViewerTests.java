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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCanvas;

import org.junit.jupiter.api.Test;

public class GraphicalTextViewerTests extends AbstractSWTBotEditorTests {

	@Test
	public void testSelectDeleteRestoreImportDeclaration() {
		SWTBotGefEditor editor = bot.gefEditor("new_file.text"); //$NON-NLS-1$

		SWTBotGefEditPart importPart = editor.getEditPart("org.eclipse.draw2d"); //$NON-NLS-1$
		importPart.doubleClick();

		SWTBotCanvas canvas = editor.bot().canvas();

		canvas.pressShortcut(Keystrokes.DELETE);
		waitEventLoop(0);
		assertNull(editor.getEditPart("org.eclipse.draw2d"), "Import Declaration wasn't deleted"); //$NON-NLS-1$ //$NON-NLS-2$

		bot.menu("Edit").menu("Undo typing").click(); //$NON-NLS-1$ //$NON-NLS-2$
		waitEventLoop(0);
		assertNotNull(editor.getEditPart("org.eclipse.draw2d"), "Import Declaration wasn't restored"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected String getWizardId() {
		return "org.eclipse.gef.examples.text.wizard.TextEditorWizard"; //$NON-NLS-1$
	}

	@Override
	protected String getFileName() {
		return "new_file.text"; //$NON-NLS-1$
	}
}
