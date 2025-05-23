/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.ui.palette;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;

import org.eclipse.gef.internal.InternalImages;

/**
 * This action allows to switch between the various supported layout modes for
 * the given palette.
 *
 * @author Pratik Shah
 */
public class LayoutAction extends Action implements IMenuCreator {

	private final PaletteViewerPreferences prefs;
	private final List<LayoutChangeAction> actions;

	/**
	 * Constructor
	 *
	 * @param prefs PaletteViewerPreferences object where the settings can be saved
	 */
	public LayoutAction(PaletteViewerPreferences prefs) {
		this(prefs, false);
	}

	/**
	 * Constructor
	 *
	 * @param hasIcon True if this action should associate an icon with itself
	 * @param prefs   PaletteViewerPreferences object where the settings can be
	 *                saved
	 */
	public LayoutAction(PaletteViewerPreferences prefs, boolean hasIcon) {
		super(PaletteMessages.LAYOUT_MENU_LABEL);
		this.prefs = prefs;
		actions = createActions();
		setMenuCreator(this);

		if (hasIcon) {
			setImageDescriptor(InternalImages.createDescriptor("icons/palette_layout.svg")); //$NON-NLS-1$
		}

		setToolTipText(PaletteMessages.LAYOUT_MENU_LABEL);
	}

	/**
	 * Helper method that wraps the given action in an ActionContributionItem and
	 * then adds it to the given menu.
	 *
	 * @param parent The menu to which the given action is to be added
	 * @param action The action that is to be added to the given menu
	 */
	@SuppressWarnings("static-method")
	protected void addActionToMenu(Menu parent, IAction action) {
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(parent, -1);
	}

	/**
	 * @return A list of actions that can switch to one of the supported layout
	 *         modes
	 */
	protected List<LayoutChangeAction> createActions() {
		List<LayoutChangeAction> list = new ArrayList<>();
		int[] modes = prefs.getSupportedLayoutModes();

		LayoutChangeAction action;
		for (int mode : modes) {
			switch (mode) {
			case PaletteViewerPreferences.LAYOUT_COLUMNS:
				action = new LayoutChangeAction(PaletteViewerPreferences.LAYOUT_COLUMNS);
				action.setText(PaletteMessages.SETTINGS_COLUMNS_VIEW_LABEL);
				list.add(action);
				break;
			case PaletteViewerPreferences.LAYOUT_LIST:
				action = new LayoutChangeAction(PaletteViewerPreferences.LAYOUT_LIST);
				action.setText(PaletteMessages.SETTINGS_LIST_VIEW_LABEL);
				list.add(action);
				break;
			case PaletteViewerPreferences.LAYOUT_ICONS:
				action = new LayoutChangeAction(PaletteViewerPreferences.LAYOUT_ICONS);
				action.setText(PaletteMessages.SETTINGS_ICONS_VIEW_LABEL_CAPS);
				list.add(action);
				break;
			case PaletteViewerPreferences.LAYOUT_DETAILS:
				action = new LayoutChangeAction(PaletteViewerPreferences.LAYOUT_DETAILS);
				action.setText(PaletteMessages.SETTINGS_DETAILS_VIEW_LABEL);
				list.add(action);
				break;
			default:
				break;
			}
		}
		return list;
	}

	/**
	 * Empty method
	 *
	 * @see org.eclipse.jface.action.IMenuCreator#dispose()
	 */
	@Override
	public void dispose() {
		// nothing to do
	}

	private Menu fillMenu(Menu menu) {
		for (LayoutChangeAction action : actions) {
			action.setChecked(prefs.getLayoutSetting() == action.getLayoutSetting());
			addActionToMenu(menu, action);
		}

		setEnabled(!actions.isEmpty());

		return menu;
	}

	/**
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(Control)
	 */
	@Override
	public Menu getMenu(Control parent) {
		return fillMenu(new Menu(parent));
	}

	/**
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(Menu)
	 */
	@Override
	public Menu getMenu(Menu parent) {
		return fillMenu(new Menu(parent));
	}

	private class LayoutChangeAction extends Action {
		private final int value;

		public LayoutChangeAction(int layoutSetting) {
			value = layoutSetting;
		}

		public int getLayoutSetting() {
			return value;
		}

		@Override
		public void run() {
			prefs.setLayoutSetting(value);
		}
	}

}
