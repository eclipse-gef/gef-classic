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
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.ui.PlatformUI;

import org.eclipse.gef.internal.ui.palette.InternalPaletteSettingsDialog;
import org.eclipse.gef.ui.palette.DefaultPaletteViewerPreferences;
import org.eclipse.gef.ui.palette.PaletteViewerPreferences;
import org.eclipse.gef.ui.palette.customize.PaletteSettingsDialog;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PaletteSettingsDialogTests extends AbstractSWTBotTests {
	private SWTBotShell settingsDialog;
	private PaletteViewerPreferences preferences;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		preferences = new DefaultPaletteViewerPreferences();
		settingsDialog = UIThreadRunnable.syncExec(() -> {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			PaletteSettingsDialog settings = new InternalPaletteSettingsDialog(shell, preferences);
			settings.setBlockOnOpen(false);
			settings.open();
			return bot.shell("Palette Settings"); //$NON-NLS-1$
		});
	}

	@Override
	@After
	public void tearDown() throws Exception {
		settingsDialog.close();
		super.tearDown();
	}

	@Test
	public void testOverlayScrollingPreferences() {
		assumeTrue(Platform.OS_LINUX.equals(Platform.getOS()));

		SWTBotCheckBox checkbox = settingsDialog.bot().checkBox("Enable Overlay Scrolling"); //$NON-NLS-1$
		assertEquals(preferences.getScrollbarsMode(), SWT.SCROLLBAR_OVERLAY);
		assertTrue(checkbox.isChecked());

		checkbox.click();
		assertEquals(preferences.getScrollbarsMode(), SWT.NONE);
		assertFalse(checkbox.isChecked());
	}
}
