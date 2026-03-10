/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gef.examples.ui.pde.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class GefExamplesPlugin extends AbstractUIPlugin {

	/** The shared instance. */
	private static GefExamplesPlugin fPlugin;

	/**
	 * Constructor.
	 */
	public GefExamplesPlugin() {
		fPlugin = this;
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return The plugin instance of <code>GefExamplesPlugin</code>
	 */
	public static GefExamplesPlugin getDefault() {
		return fPlugin;
	}

}