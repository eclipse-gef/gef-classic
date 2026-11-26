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

package org.eclipse.draw2d.mcp.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import org.eclipse.draw2d.mcp.ITool;
import org.eclipse.draw2d.mcp.IToolProvider;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ToolExtensionRegistry {
	private static final Bundle BUNDLE = FrameworkUtil.getBundle(ToolExtensionRegistry.class);
	private static final ILog LOGGER = Platform.getLog(BUNDLE);

	private static final String NAMESPACE = BUNDLE.getSymbolicName();
	private static final String ID = "tools"; //$NON-NLS-1$

	public static List<ITool> getTools() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(NAMESPACE, ID);

		List<ITool> tools = new ArrayList<>();
		for (IConfigurationElement element : extensionPoint.getConfigurationElements()) {
			try {
				IToolProvider provider = (IToolProvider) element.createExecutableExtension("class"); //$NON-NLS-1$
				tools.addAll(provider.getTools());
			} catch (CoreException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return Collections.unmodifiableList(tools);
	}
}
