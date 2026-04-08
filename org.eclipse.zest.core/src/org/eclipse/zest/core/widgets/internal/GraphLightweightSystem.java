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

package org.eclipse.zest.core.widgets.internal;

import org.eclipse.swt.graphics.Image;

import org.eclipse.zest.core.widgets.Graph;

import org.eclipse.draw2d.LightweightSystem;

/**
 * Custom lightweight-system used by the Zest {@link Graph} in order to handle
 * the lifecycle of the {@link Image}s created by the {@link CachedLabel}.
 */
public class GraphLightweightSystem extends LightweightSystem {
	private ImageRegistry registry = ImageRegistry.getSharedRegistry();

	/**
	 * Updates the image registry used by this {@link LightweightSystem}. If
	 * {@code null} is passed as an argument, the global image registry is used.
	 *
	 * @param registry The new image registry.
	 */
	public void setImageRegistry(ImageRegistry registry) {
		if (registry == null) {
			this.registry = ImageRegistry.getSharedRegistry();
		} else {
			this.registry = registry;
		}
	}

	/**
	 * @return The image registry used by this {@link LightweightSystem}. Never
	 *         {@code null}.
	 */
	public ImageRegistry internalGetImageRegistry() {
		return registry;
	}
}
