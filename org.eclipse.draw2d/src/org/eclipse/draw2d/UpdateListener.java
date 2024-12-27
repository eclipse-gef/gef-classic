/*******************************************************************************
 * Copyright (c) 2000, 2024 IBM Corporation and others.
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
package org.eclipse.draw2d;

import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;

/**
 * An interface used to notify listeners that the listened to object is
 * updating.
 */
public interface UpdateListener {

	/**
	 * Notifies the listener that the listened to object is painting. The damage
	 * rectangle may be null or empty. This indicates the dirty regions were clipped
	 * or not visible. But for objects such as the
	 * {@link org.eclipse.draw2d.parts.Thumbnail}, notification still needs to
	 * occur. The map of dirty regions is passed to allow the listener to determine
	 * if it needs to update, for instance when a particular figure is painting.
	 *
	 * @param damage       The area being painted
	 * @param dirtyRegions a Map of figures to their dirty regions
	 */
	void notifyPainting(Rectangle damage, Map<IFigure, Rectangle> dirtyRegions);

	/**
	 * Notifies the listener that the listened to object is validating.
	 */
	void notifyValidating();

	/**
	 * An empty implementation of {@link UpdateListener} for convenience.
	 *
	 * @since 3.16
	 */
	static class Stub implements UpdateListener {
		@Override
		public void notifyPainting(Rectangle damage, Map<IFigure, Rectangle> dirtyRegions) {
			// may be overwritten by subclasses
		}

		@Override
		public void notifyValidating() {
			// may be overwritten by subclasses
		}
	}
}
