/*******************************************************************************
 * Copyright 2005, 2025 CHISEL Group, University of Victoria, Victoria,
 *                      BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.zest.layouts;

import java.util.List;

/**
 * When a layout completes an iteration, it throws this event to allow the
 * application to update. For example, at the end of an iteration is can be
 * assumed the layout has placed each entity into a new location. This event
 * allows the application to update the GUI to represent the new locations
 *
 * @author Casey Best and Rob Lintern
 * @deprecated No longer used in Zest 2.x. This class will be removed in a
 *             future release.
 * @noextend This class is not intended to be subclassed by clients.
 * @noreference This class is not intended to be referenced by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@SuppressWarnings("rawtypes")
@Deprecated(since = "2.0", forRemoval = true)
public class LayoutIterationEvent {
	private List relationshipsToLayout, entitiesToLayout;
	private int iterationCompleted;

	/**
	 * Return the relationships used in this layout.
	 */
	@Deprecated
	public List getRelationshipsToLayout() {
		return relationshipsToLayout;
	}

	/**
	 * Return the entities used in this layout.
	 */
	@Deprecated
	public List getEntitiesToLayout() {
		return entitiesToLayout;
	}

	/**
	 * Return the iteration of the layout algorithm that was just completed.
	 */
	@Deprecated
	public int getIterationCompleted() {
		return iterationCompleted;
	}
}
