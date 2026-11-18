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
 *     DBeaver Corporation - preserve lazy bundle activation
 *******************************************************************************/

package org.eclipse.gef.internal;

import org.eclipse.ui.IWorkbench;

import org.eclipse.draw2d.BasicColorProvider;
import org.eclipse.draw2d.ColorProvider;

import org.eclipse.gef.GEFColorProvider;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Installs the theme color provider when a workbench is available. The tracker is
 * opened by the bundle activator so workbench startup does not activate GEF.
 */
public class ColorProviderService extends ServiceTracker<IWorkbench, IWorkbench> {

	public ColorProviderService(BundleContext context) {
		super(context, IWorkbench.class, null);
	}

	@Override
	public IWorkbench addingService(ServiceReference<IWorkbench> reference) {
		IWorkbench workbench = super.addingService(reference);
		// Overloads the basic color provider with customizable one
		if (workbench != null && ColorProvider.SystemColorFactory.getColorProvider() instanceof BasicColorProvider) {
			ColorProvider.SystemColorFactory.setColorProvider(new GEFColorProvider());
		}
		return workbench;
	}
}
