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
package org.eclipse.gef;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.properties.IPropertySheetEntry;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.internal.InternalGEFPlugin;

/**
 * @deprecated The GEF plugin class must not be referenced by clients.
 */
@Deprecated
public final class GEFPlugin extends AbstractUIPlugin {

	private static GEFPlugin singleton;

	/**
	 * This method will be deleted.
	 *
	 * @deprecated use org.eclipse.gef.ui.properties.UndoablePropertySheetEntry
	 * @param stack a command stack
	 * @return the implementation for the entry
	 */
	@Deprecated
	public static IPropertySheetEntry createUndoablePropertySheetEntry(CommandStack stack) {
		return new org.eclipse.gef.ui.properties.UndoablePropertySheetEntry(stack);
	}

	/**
	 * Gets the plugin singleton.
	 *
	 * @return the default GEFPlugin singleton
	 */
	public static GEFPlugin getDefault() {
		if (singleton == null) {
			singleton = new GEFPlugin();
		}
		return singleton;
	}

	GEFPlugin() {
		try {
			start(InternalGEFPlugin.getContext());
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

}
