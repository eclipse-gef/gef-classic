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

package org.eclipse.gef.internal;

import org.eclipse.ui.IWorkbench;

import org.eclipse.draw2d.BasicColorProvider;
import org.eclipse.draw2d.ColorProvider;

import org.eclipse.gef.GEFColorProvider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class ColorProviderService {
	@Reference
	@SuppressWarnings("static-method")
	public void setWorkbench(IWorkbench workbench) {
		// Overloads the basic color provider with customizable one
		if (ColorProvider.SystemColorFactory.getColorProvider() instanceof BasicColorProvider) {
			ColorProvider.SystemColorFactory.setColorProvider(new GEFColorProvider());
		}
	}
}
