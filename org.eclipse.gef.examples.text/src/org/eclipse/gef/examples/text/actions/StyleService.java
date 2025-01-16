/*******************************************************************************
 * Copyright (c) 2004, 2023 IBM Corporation and others.
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

package org.eclipse.gef.examples.text.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @since 3.1
 */
public class StyleService {

	public static final Object STATE_EDITABLE = new Object();
	public static final Object STATE_READ_ONLY = new Object();
	public static final Object UNDEFINED = new Object();

	private List<StyleListener> listeners = new CopyOnWriteArrayList<>();
	private StyleProvider provider;
	private StyleListener providerListener = StyleService.this::propogateChange;

	/**
	 * Constructs a new StyleService object
	 */
	public StyleService() {
		super();
	}

	public void addStyleListener(StyleListener listener) {
		listeners.add(listener);
	}

	public Object getStyle(String styleID) {
		if (provider != null) {
			return provider.getStyle(styleID);
		}
		return UNDEFINED;
	}

	public Object getStyleState(String styleID) {
		if (provider != null) {
			return provider.getStyleState(styleID);
		}
		return UNDEFINED;
	}

	/**
	 * @param styleID
	 */
	protected void propogateChange(String styleID) {
		listeners.forEach(listener -> listener.styleChanged(styleID));
	}

	/**
	 * Removes the first occurrence of the given listener.
	 *
	 * @param listener the style listener
	 */
	public void removeStyleListener(StyleListener listener) {
		listeners.remove(listener);
	}

	public void setStyle(String styleID, Object value) {
		if (provider != null) {
			provider.setStyle(styleID, value);
		}
	}

	public void setStyleProvider(StyleProvider provider) {
		if (this.provider == provider) {
			return;
		}
		if (this.provider != null) {
			this.provider.removeStyleListener(providerListener);
		}
		this.provider = provider;
		if (this.provider != null) {
			this.provider.addStyleListener(providerListener);
		}
		propogateChange(null);
	}

}