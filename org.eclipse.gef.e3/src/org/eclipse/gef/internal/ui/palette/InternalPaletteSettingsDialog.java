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

package org.eclipse.gef.internal.ui.palette;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;

import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.ui.palette.PaletteViewerPreferences;
import org.eclipse.gef.ui.palette.customize.PaletteSettingsDialog;

/**
 * Internal implementation of the {@link PaletteSettingsDialog} that contains
 * additional fields compared to the public dialog.
 */
public class InternalPaletteSettingsDialog extends PaletteSettingsDialog {
	protected static final String CACHE_SCROLLBARS_MODE = "scrollbars mode"; //$NON-NLS-1$
	protected static final int SCROLLBARS_MODE_ID = IDialogConstants.CLIENT_ID + 15;
	private final PaletteViewerPreferences prefs;

	public InternalPaletteSettingsDialog(Shell parentShell, PaletteViewerPreferences prefs) {
		super(parentShell, prefs);
		this.prefs = prefs;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		if (Platform.getOS().equals(Platform.OS_LINUX)) {
			Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
			separator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
			Control child = createScrollbarsSettings(composite);
			child.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		}

		return composite;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (SCROLLBARS_MODE_ID == buttonId) {
			Button b = getButton(buttonId);
			handleScrollbarsModeChanged(b.getSelection());
		} else {
			super.buttonPressed(buttonId);
		}
	}

	@Override
	protected void cacheSettings() {
		super.cacheSettings();
		settings.put(CACHE_SCROLLBARS_MODE, prefs.getScrollbarsMode());
	}

	@Override
	protected void restoreSettings() {
		super.restoreSettings();
		prefs.setScrollbarsMode((int) settings.get(CACHE_SCROLLBARS_MODE));
	}

	/**
	 * Creates and initializes the part of the dialog that displays the options for
	 * overlay scrolling. This method should only be called on Linux. *
	 *
	 * @param parent the parent composite
	 * @return the newly created control
	 */
	protected Control createScrollbarsSettings(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		Label label = new Label(composite, SWT.NONE);
		label.setText(GEFMessages.Settings_OverlayScrolling_Group);
		Button button = createButton(composite, SCROLLBARS_MODE_ID, GEFMessages.Settings_OverlayScrolling, SWT.CHECK,
				null);
		button.setSelection(prefs.getScrollbarsMode() == SWT.SCROLLBAR_OVERLAY);
		return composite;
	}

	/**
	 * This method is called whenever the "Enable Overlay Scrolling" checkbox is
	 * selected/deselected. The method argument is the new state of the button.
	 *
	 * @param checked If {@code true}, overlay scrolling will be enabled.
	 */
	protected void handleScrollbarsModeChanged(boolean checked) {
		prefs.setScrollbarsMode(checked ? SWT.SCROLLBAR_OVERLAY : SWT.NONE);
	}
}
