/*******************************************************************************
 * Copyright (c) 2025 Patrick Ziegler and others.
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
package org.eclipse.gef.examples.palette;

import java.beans.PropertyChangeListener;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.draw2d.FigureCanvas;

import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.palette.DefaultPaletteViewerPreferences;
import org.eclipse.gef.ui.palette.PaletteContextMenuProvider;
import org.eclipse.gef.ui.palette.PaletteMessages;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerPreferences;
import org.eclipse.gef.ui.palette.SettingsAction;
import org.eclipse.gef.ui.palette.customize.PaletteSettingsDialog;
import org.eclipse.gef.ui.palette.editparts.PaletteEditPart;

import org.eclipse.gef.examples.Messages;

/**
 * This snippet shows how to use separate fonts for the {@link PaletteDrawer}
 * and {@link ToolEntry} edit parts, rather than using a single font for both.
 * <p>
 * Normally, the font of the palette figures is updated using the
 * {@link PaletteViewerPreferences#PREFERENCE_FONT} preference. This field has
 * been replaced in favor of two new preferences called
 * {@link #PREFERENCE_ENTRY_FONT} and {@link #PREFERENCE_ENTRY_FONT}. Those
 * preferences are handled by a new {@link PropertyChangeListener}, which
 * updates the font assigned to the corresponding figures whenever those entries
 * have been modified.
 * </p>
 */
public class PaletteSnippet1 extends ViewPart {
	private static final String PREFERENCE_DRAWER_FONT = "drawer-font"; //$NON-NLS-1$
	private static final String PREFERENCE_ENTRY_FONT = "entry-font"; //$NON-NLS-1$
	private static final Set<String> FONT_PROPERTIES = Set.of(PREFERENCE_DRAWER_FONT, PREFERENCE_ENTRY_FONT);

	private final SnippetPreferences preferences;
	private final PaletteRoot paletteRoot;
	private PaletteViewer paletteViewer;
	private Font drawerFont;
	private Font entryFont;

	public PaletteSnippet1() {
		PaletteDrawer paletteDrawer = new PaletteDrawer(Messages.PaletteSnippet1_System);
		paletteDrawer.add(new SelectionToolEntry());
		paletteRoot = new PaletteRoot();
		paletteRoot.add(paletteDrawer);

		preferences = new SnippetPreferences();
		preferences.addPropertyChangeListener(event -> {
			if (FONT_PROPERTIES.contains(event.getPropertyName())) {
				updateFonts(event.getPropertyName());
				paletteViewer.getRootEditPart().refresh();
			}
		});
	}

	@Override
	public void createPartControl(Composite parent) {
		drawerFont = new Font(parent.getDisplay(), preferences.getFontData(PREFERENCE_DRAWER_FONT));
		entryFont = new Font(parent.getDisplay(), preferences.getFontData(PREFERENCE_ENTRY_FONT));

		paletteViewer = new PaletteViewer();
		paletteViewer.createControl(parent);
		paletteViewer.setPaletteRoot(paletteRoot);
		paletteViewer.setPaletteViewerPreferences(preferences);
		paletteViewer.setContextMenu(new SnippetContextMenu(paletteViewer));

		updateFonts((String) null);
	}

	@Override
	public void setFocus() {
		if (paletteViewer != null) {
			paletteViewer.getControl().setFocus();
		}
	}

	@Override
	public void dispose() {
		if (drawerFont != null) {
			drawerFont.dispose();
		}

		if (entryFont != null) {
			entryFont.dispose();
		}

		super.dispose();
	}

	/**
	 * Refreshes the font associated with the given property and replaces any old
	 * references in the palette viewer. <i>Important</i> This method will update
	 * all figures and edit parts to consider the new font when e.g. calculating
	 * their size.
	 */
	private void updateFonts(String propertyName) {
		Display display = paletteViewer.getControl().getDisplay();

		if (PREFERENCE_DRAWER_FONT.equals(propertyName)) {
			if (drawerFont != null) {
				drawerFont.dispose();
			}
			drawerFont = new Font(display, preferences.getFontData(PREFERENCE_DRAWER_FONT));
		} else if (PREFERENCE_ENTRY_FONT.equals(propertyName)) {
			if (entryFont != null) {
				entryFont.dispose();
			}
			entryFont = new Font(display, preferences.getFontData(PREFERENCE_ENTRY_FONT));
		}

		updateFonts((PaletteEditPart) paletteViewer.getEditPartForModel(paletteRoot));

		FigureCanvas figureCanvas = (FigureCanvas) paletteViewer.getControl();
		figureCanvas.getViewport().invalidateTree();
		figureCanvas.getViewport().revalidate();
		figureCanvas.redraw();

		paletteViewer.getRootEditPart().refresh();
	}

	/**
	 * Recursively updates the font for all {@link ToolEntry} and
	 * {@link PaletteDrawer} figures. <i>Important</i> It is essential that each
	 * figure has a local font set, instead of inheriting the font of its parent, in
	 * order to e.g. avoid a tool entry using the font of its containing drawer.
	 */
	private void updateFonts(PaletteEditPart editPart) {
		if (editPart.getModel() instanceof ToolEntry) {
			editPart.getFigure().setFont(entryFont);
		} else if (editPart.getModel() instanceof PaletteDrawer) {
			editPart.getFigure().setFont(drawerFont);
		}

		for (PaletteEditPart childEditPart : editPart.getChildren()) {
			updateFonts(childEditPart);
		}
	}

	/**
	 * The palette viewer preferences extended with two additional properties
	 * regarding the font of the drawer and tool entries.
	 */
	private class SnippetPreferences extends DefaultPaletteViewerPreferences {
		private static final String DEFAULT_FONT = "Default"; //$NON-NLS-1$

		public SnippetPreferences() {
			super(new PreferenceStore());
			getPreferenceStore().setDefault(PREFERENCE_DRAWER_FONT, DEFAULT_FONT);
			getPreferenceStore().setDefault(PREFERENCE_ENTRY_FONT, DEFAULT_FONT);
		}

		public void setFontData(String property, FontData fontData) {
			String value = fontData.toString();
			if (fontData.equals(JFaceResources.getDialogFont().getFontData()[0])) {
				value = DEFAULT_FONT;
			}
			getPreferenceStore().setValue(property, value);
		}

		public FontData getFontData(String property) {
			String value = getPreferenceStore().getString(property);
			if (value.equals(DEFAULT_FONT)) {
				return JFaceResources.getDialogFont().getFontData()[0];
			}
			return new FontData(value);
		}

		@Override
		protected void handlePreferenceStorePropertyChanged(String property) {
			if (FONT_PROPERTIES.contains(property)) {
				firePropertyChanged(property, getFontData(property));
			} else {
				super.handlePreferenceStorePropertyChanged(property);
			}
		}
	}

	/**
	 * Minimal context menu for the palette viewer that only contains our custom
	 * "Settings" dialog as menu item.
	 */
	private static class SnippetContextMenu extends PaletteContextMenuProvider {
		public SnippetContextMenu(PaletteViewer palette) {
			super(palette);
		}

		@Override
		public void buildContextMenu(IMenuManager menu) {
			GEFActionConstants.addStandardActionGroups(menu);
			menu.appendToGroup(GEFActionConstants.GROUP_REST, new SnippetSettingsAction(getPaletteViewer()));
		}
	}

	/**
	 * Custom implementation of the "Settings" action in the context menu that
	 * simply replaces the default "Settings" dialog with our own implementation.
	 */
	private static class SnippetSettingsAction extends SettingsAction {
		private final Shell shell;
		private final SnippetPreferences prefs;

		public SnippetSettingsAction(PaletteViewer palette) {
			super(palette);
			shell = palette.getControl().getShell();
			prefs = (SnippetPreferences) palette.getPaletteViewerPreferences();
		}

		@Override
		public void run() {
			Dialog settings = new SnippetSettingsDialog(shell, prefs);
			settings.open();
		}
	}

	/**
	 * Custom implementation of the palette settings where the font entry for both
	 * categories and tool entries is split into two separate fields.
	 */
	private static class SnippetSettingsDialog extends PaletteSettingsDialog {
		private static final String CACHE_DRAWER_FONT = "drawer-font"; //$NON-NLS-1$
		private static final String CACHE_ENTRY_FONT = "entry-font"; //$NON-NLS-1$
		protected static final int DRAWER_FONT_CHANGE_ID = CLIENT_ID + 1;
		protected static final int DRAWER_DEFAULT_FONT_ID = CLIENT_ID + 2;
		protected static final int ENTRY_FONT_CHANGE_ID = CLIENT_ID + 3;
		protected static final int ENTRY_DEFAULT_FONT_ID = CLIENT_ID + 4;
		protected SnippetPreferences prefs;
		protected Label drawerFontName;
		protected Label entryFontName;

		public SnippetSettingsDialog(Shell parentShell, SnippetPreferences prefs) {
			super(parentShell, prefs);
			this.prefs = prefs;
		}

		@Override
		protected void buttonPressed(int buttonId) {
			switch (buttonId) {
			case DRAWER_FONT_CHANGE_ID -> handleChangeFontPressed(PREFERENCE_DRAWER_FONT);
			case DRAWER_DEFAULT_FONT_ID -> handleDefaultFontRequested(PREFERENCE_DRAWER_FONT);
			case ENTRY_FONT_CHANGE_ID -> handleChangeFontPressed(PREFERENCE_ENTRY_FONT);
			case ENTRY_DEFAULT_FONT_ID -> handleDefaultFontRequested(PREFERENCE_ENTRY_FONT);
			default -> super.buttonPressed(buttonId);
			}
		}

		@Override
		protected void restoreSettings() {
			super.restoreSettings();
			prefs.setFontData(PREFERENCE_DRAWER_FONT, (FontData) settings.get(CACHE_DRAWER_FONT));
			prefs.setFontData(PREFERENCE_ENTRY_FONT, (FontData) settings.get(CACHE_ENTRY_FONT));
		}

		@Override
		protected void cacheSettings() {
			super.cacheSettings();
			settings.put(CACHE_DRAWER_FONT, prefs.getFontData(PREFERENCE_DRAWER_FONT));
			settings.put(PREFERENCE_ENTRY_FONT, prefs.getFontData(PREFERENCE_ENTRY_FONT));
		}

		@Override
		protected Control createFontSettings(Composite parent) {
			Composite container = new Composite(parent, SWT.NONE);
			container.setLayout(new GridLayout(2, false));

			drawerFontName = new Label(container, SWT.LEFT | SWT.WRAP);
			drawerFontName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
			createButton(container, DRAWER_FONT_CHANGE_ID, PaletteMessages.SETTINGS_FONT_CHANGE, SWT.PUSH, null);
			createButton(container, DRAWER_DEFAULT_FONT_ID, PaletteMessages.SETTINGS_DEFAULT_FONT, SWT.PUSH, null);

			entryFontName = new Label(container, SWT.LEFT | SWT.WRAP);
			entryFontName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
			createButton(container, ENTRY_FONT_CHANGE_ID, PaletteMessages.SETTINGS_FONT_CHANGE, SWT.PUSH, null);
			createButton(container, ENTRY_DEFAULT_FONT_ID, PaletteMessages.SETTINGS_DEFAULT_FONT, SWT.PUSH, null);

			updateFontName();
			return container;
		}

		@Override
		protected void updateFontName() {
			drawerFontName
					.setText(Messages.bind(Messages.PaletteSnippet1_Drawer_Font, getFontName(PREFERENCE_DRAWER_FONT)));
			entryFontName
					.setText(Messages.bind(Messages.PaletteSnippet1_Entry_Font, getFontName(PREFERENCE_ENTRY_FONT)));

		}

		protected void handleChangeFontPressed(String property) {
			FontDialog dialog = new FontDialog(getShell());
			FontData data = prefs.getFontData(property);
			dialog.setFontList(new FontData[] { data });
			data = dialog.open();
			if (data != null) {
				prefs.setFontData(property, data);
			}
			updateFontName();
		}

		protected void handleDefaultFontRequested(String property) {
			prefs.setFontData(property, JFaceResources.getDialogFont().getFontData()[0]);
			updateFontName();
		}

		protected String getFontName(String property) {
			if (prefs.getFontData(property).equals((JFaceResources.getDialogFont().getFontData()[0]))) {
				return PaletteMessages.SETTINGS_WORKBENCH_FONT_LABEL;
			}
			return StringConverter.asString(prefs.getFontData(property));
		}
	}
}
