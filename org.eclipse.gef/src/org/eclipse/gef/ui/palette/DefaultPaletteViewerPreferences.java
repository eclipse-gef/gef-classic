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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import org.eclipse.gef.internal.InternalGEFPlugin;

/**
 * This is the default implementation for PaletteViewerPreferences. It uses a
 * single IPreferenceStore to load and save the palette viewer settings.
 * <p>
 * It is recommended that the default constructor be used (which will use the
 * preference store in the GEF plugin) as that will cause the preferences to be
 * shared across different types of editors. If the client does not wish to
 * share one of the existing preferences for their editor (say the auto-collapse
 * setting), they will have to sub-class this class and override the necessary
 * methods (in this case, {@link #getAutoCollapseSetting()} and
 * {@link #setAutoCollapseSetting(int)}) and save that preference in some other
 * preference store. Sub-classes can add newer preferences to the store by using
 * {@link #getPreferenceStore()}.
 * </p>
 *
 * @author Pratik Shah
 */
public class DefaultPaletteViewerPreferences implements PaletteViewerPreferences {

	private static final String DEFAULT_FONT = "Default"; //$NON-NLS-1$

	private final PreferenceStoreListener listener;
	private final IPropertyChangeListener fontListener;
	private FontData fontData;
	private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private final IPreferenceStore store;
	private int[] supportedModes = { LAYOUT_COLUMNS, LAYOUT_LIST, LAYOUT_ICONS, LAYOUT_DETAILS };

	/**
	 * Default Constructor
	 * <p>
	 * Uses the GEF Plugin's IPreferenceStore to store the preferences.
	 * </p>
	 */
	public DefaultPaletteViewerPreferences() {
		this(InternalGEFPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * Constructor
	 *
	 * @param store The IPreferenceStore where the settings are to be saved.
	 */
	public DefaultPaletteViewerPreferences(final IPreferenceStore store) {
		this.store = store;
		store.setDefault(PREFERENCE_DETAILS_ICON_SIZE, false);
		store.setDefault(PREFERENCE_COLUMNS_ICON_SIZE, true);
		store.setDefault(PREFERENCE_ICONS_ICON_SIZE, true);
		store.setDefault(PREFERENCE_LIST_ICON_SIZE, false);
		store.setDefault(PREFERENCE_LAYOUT, LAYOUT_LIST);
		store.setDefault(PREFERENCE_AUTO_COLLAPSE, COLLAPSE_AS_NEEDED);
		store.setDefault(PREFERENCE_FONT, DEFAULT_FONT);
		store.setDefault(PREFERENCE_SCROLLBARS_MODE, SWT.SCROLLBAR_OVERLAY);

		listener = new PreferenceStoreListener();
		store.addPropertyChangeListener(listener);

		fontListener = event -> {
			if (JFaceResources.DIALOG_FONT.equals(event.getProperty())
					&& getPreferenceStore().getString(PREFERENCE_FONT).equals(DEFAULT_FONT)) {
				setFontData(JFaceResources.getDialogFont().getFontData()[0]);
				handlePreferenceStorePropertyChanged(PREFERENCE_FONT);
			}
		};
		JFaceResources.getFontRegistry().addListener(fontListener);
	}

	/**
	 * NOTE: The <code>oldValue</code> field of the <code>PropertyChangeEvent</code>
	 * used to notify listeners will always be <code>null</code>.
	 *
	 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#addPropertyChangeListener(PropertyChangeListener)
	 */
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	/**
	 * This is a convenience method that converts the given layout mode to the
	 * matching preference name.
	 *
	 * <UL>
	 * <LI>int &#8596; String</LI>
	 * <LI>LAYOUT_LIST &#8596; PREFERENCE_LIST_ICON_SIZE</LI>
	 * <LI>LAYOUT_COLUMNS &#8596; PREFERENCE_COLUMNS_ICON_SIZE</LI>
	 * <LI>LAYOUT_ICONS &#8596; PREFERENCE_ICONS_ICON_SIZE</LI>
	 * <LI>LAYOUT_DETAILS &#8596; PREFERENCE_DETAILS_ICON_SIZE</LI>
	 * </UL>
	 *
	 * @param layout LAYOUT_LIST, LAYOUT_DETAILS, LAYOUT_COLUMNS, or LAYOUT_ICONS
	 * @return The corresponding preference String
	 */
	public static String convertLayoutToPreferenceName(int layout) {
		String key = ""; //$NON-NLS-1$
		switch (layout) {
		case LAYOUT_COLUMNS:
			key = PREFERENCE_COLUMNS_ICON_SIZE;
			break;
		case LAYOUT_LIST:
			key = PREFERENCE_LIST_ICON_SIZE;
			break;
		case LAYOUT_ICONS:
			key = PREFERENCE_ICONS_ICON_SIZE;
			break;
		case LAYOUT_DETAILS:
			key = PREFERENCE_DETAILS_ICON_SIZE;
			break;
		}
		return key;
	}

	/**
	 * This convenience method converts the given preference to the matching layout
	 * mode.
	 *
	 * <UL>
	 * <LI>int &#8596; String</LI>
	 * <LI>LAYOUT_LIST &#8596; PREFERENCE_LIST_ICON_SIZE</LI>
	 * <LI>LAYOUT_COLUMNS &#8596; PREFERENCE_COLUMNS_ICON_SIZE</LI>
	 * <LI>LAYOUT_ICONS &#8596; PREFERENCE_ICONS_ICON_SIZE</LI>
	 * <LI>LAYOUT_DETAILS &#8596; PREFERENCE_DETAILS_ICON_SIZE</LI>
	 * </UL>
	 *
	 * @param preference PREFERENCE_DETAILS_ICON_SIZE, PREFERENCE_COLUMNS_ICON_SIZE,
	 *                   PREFERENCE_ICONS_ICON_SIZE or PREFERENCE_LIST_ICON_SIZE
	 * @return The corresponding layout code
	 */
	public static int convertPreferenceNameToLayout(String preference) {
		return switch (preference) {
		case PREFERENCE_DETAILS_ICON_SIZE -> LAYOUT_DETAILS;
		case PREFERENCE_COLUMNS_ICON_SIZE -> LAYOUT_COLUMNS;
		case PREFERENCE_ICONS_ICON_SIZE -> LAYOUT_ICONS;
		case PREFERENCE_LIST_ICON_SIZE -> LAYOUT_LIST;
		default -> -1;
		};
	}

	/**
	 * The oldValue of the PropertyChangeEvent that is fired will always be
	 * <code>null</code>.
	 *
	 * @param property The programmatic name of the property that was changed
	 * @param newVal   The new value of the property
	 * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String,
	 *      java.lang.Object, java.lang.Object)
	 */
	protected void firePropertyChanged(String property, Object newVal) {
		listeners.firePropertyChange(property, null, newVal);
	}

	/**
	 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#getAutoCollapseSetting()
	 */
	@Override
	public int getAutoCollapseSetting() {
		return getPreferenceStore().getInt(PREFERENCE_AUTO_COLLAPSE);
	}

	/**
	 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#getFontData()
	 */
	@Override
	public FontData getFontData() {
		if (fontData == null) {
			String value = getPreferenceStore().getString(PREFERENCE_FONT);
			if (value.equals(DEFAULT_FONT)) {
				fontData = JFaceResources.getDialogFont().getFontData()[0];
			} else {
				fontData = new FontData(value);
			}
		}
		return fontData;
	}

	/**
	 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#getLayoutSetting()
	 */
	@Override
	public int getLayoutSetting() {
		return getPreferenceStore().getInt(PREFERENCE_LAYOUT);
	}

	/**
	 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#getSupportedLayoutModes()
	 */
	@Override
	public int[] getSupportedLayoutModes() {
		return supportedModes;
	}

	/**
	 * This method is invoked when the preference store fires a property change.
	 *
	 * @param property The property String used for the change fired by the
	 *                 preference store
	 */
	protected void handlePreferenceStorePropertyChanged(String property) {
		switch (property) {
		case PREFERENCE_LAYOUT:
			firePropertyChanged(property, Integer.valueOf(getLayoutSetting()));
			break;
		case PREFERENCE_AUTO_COLLAPSE:
			firePropertyChanged(property, Integer.valueOf(getAutoCollapseSetting()));
			break;
		case PREFERENCE_FONT:
			firePropertyChanged(property, getFontData());
			break;
		case PREFERENCE_SCROLLBARS_MODE:
			firePropertyChanged(property, getScrollbarsMode());
			break;
		default:
			firePropertyChanged(property, Boolean.valueOf(useLargeIcons(convertPreferenceNameToLayout(property))));
			break;
		}
	}

	/**
	 * @return The IPreferenceStore used by this class to store the preferences.
	 */
	protected IPreferenceStore getPreferenceStore() {
		return store;
	}

	/**
	 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#isSupportedLayoutMode(int)
	 */
	@Override
	public boolean isSupportedLayoutMode(int layout) {
		for (int supportedMode : supportedModes) {
			if (supportedMode == layout) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#removePropertyChangeListener(PropertyChangeListener)
	 */
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	/**
	 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#setAutoCollapseSetting(int)
	 */
	@Override
	public void setAutoCollapseSetting(int newVal) {
		getPreferenceStore().setValue(PREFERENCE_AUTO_COLLAPSE, newVal);
	}

	/**
	 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#setFontData(FontData)
	 */
	@Override
	public void setFontData(FontData data) {
		fontData = data;
		String value = data.toString();
		if (fontData.equals(JFaceResources.getDialogFont().getFontData()[0])) {
			value = DEFAULT_FONT;
		}
		getPreferenceStore().setValue(PREFERENCE_FONT, value);
	}

	/**
	 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#setLayoutSetting(int)
	 */
	@Override
	public void setLayoutSetting(int newVal) {
		getPreferenceStore().setValue(PREFERENCE_LAYOUT, newVal);
	}

	/**
	 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#setCurrentUseLargeIcons(boolean)
	 */
	@Override
	public void setCurrentUseLargeIcons(boolean newVal) {
		setUseLargeIcons(getLayoutSetting(), newVal);
	}

	/**
	 * NOTE: Restricting the layout modes here does not in any way restrict those
	 * values from being stored in the preference store. Instead, it is the
	 * responsibility of all clients manipulating the layout settings to check to
	 * see if a particular layout mode is supported before manipulating it, or
	 * allowing the end user to manipulate it.
	 *
	 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#setSupportedLayoutModes(int[])
	 */
	@Override
	public void setSupportedLayoutModes(int[] modes) {
		supportedModes = modes;
		if (!isSupportedLayoutMode(getPreferenceStore().getDefaultInt(PREFERENCE_LAYOUT))) {
			getPreferenceStore().setDefault(PREFERENCE_LAYOUT, supportedModes[0]);
		}
		if (!isSupportedLayoutMode(getPreferenceStore().getInt(PREFERENCE_LAYOUT))) {
			setLayoutSetting(supportedModes[0]);
		}
	}

	/**
	 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#setUseLargeIcons(int,
	 *      boolean)
	 */
	@Override
	public void setUseLargeIcons(int layout, boolean newVal) {
		getPreferenceStore().setValue(convertLayoutToPreferenceName(layout), newVal);
	}

	/**
	 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#useLargeIcons(int)
	 */
	@Override
	public boolean useLargeIcons(int layout) {
		return getPreferenceStore().getBoolean(convertLayoutToPreferenceName(layout));
	}

	/**
	 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#useLargeIcons()
	 */
	@Override
	public boolean useLargeIcons() {
		return useLargeIcons(getLayoutSetting());
	}

	private class PreferenceStoreListener implements IPropertyChangeListener {
		/**
		 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			handlePreferenceStorePropertyChanged(evt.getProperty());
		}
	}

	@Override
	public void setScrollbarsMode(int mode) {
		if (mode != SWT.SCROLLBAR_OVERLAY && mode != SWT.NONE) {
			throw new IllegalArgumentException(
					"Scrollbar mode must be either SWT.SCROLLBAR_OVERLAY or SWT.NONE, but %d was given!" //$NON-NLS-1$
							.formatted(mode));
		}
		getPreferenceStore().setValue(PREFERENCE_SCROLLBARS_MODE, mode);
	}

	@Override
	public int getScrollbarsMode() {
		return getPreferenceStore().getInt(PREFERENCE_SCROLLBARS_MODE);
	}
}
