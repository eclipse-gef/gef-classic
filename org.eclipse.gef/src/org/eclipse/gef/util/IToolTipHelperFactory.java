/*******************************************************************************
 * Copyright (c) 2025 Patrick Ziegler and others.
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

package org.eclipse.gef.util;

import org.eclipse.swt.widgets.Control;

import org.eclipse.draw2d.SWTEventDispatcher;
import org.eclipse.draw2d.ToolTipHelper;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.parts.DomainEventDispatcher;

/**
 * OSGi service used within the {@link DomainEventDispatcher} to inject a custom
 * {@link ToolTipHelper}.
 *
 * @since 3.25
 */
public interface IToolTipHelperFactory {
	/**
	 * Creates and returns a new {@link ToolTipHelper} instance. This method may
	 * return {@code null}, if this factory can't create a helper for the given
	 * edit-part viewer.
	 * <p>
	 * - If multiple services are registered, the first non-null
	 * {@link ToolTipHelper} is used.
	 * </p>
	 * <p>
	 * - If no {@link ToolTipHelper} could be created,
	 * {@link SWTEventDispatcher#createToolTipHelper()} is used.
	 * </p>
	 *
	 * @param control The {@code control} associated with the
	 *                {@link DomainEventDispatcher}
	 * @param viewer  The {@code viewer} associated with the
	 *                {@link DomainEventDispatcher}.
	 * @return as described.
	 */
	ToolTipHelper create(Control control, EditPartViewer viewer);
}
