/*******************************************************************************
 * Copyright 2005, 2026, CHISEL Group, University of Victoria, Victoria, BC,
 *                       Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.zest.core.viewers;

import org.eclipse.zest.core.viewers.decorators.EntityConnectionStyleDecorator;

import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;

/**
 * Extend the IEntityConnectionStyleProvider interface to provide additional
 * methods introduced by Zest 2.x.
 *
 * WARNING: THIS API IS UNDER CONSTRUCTION AND SHOULD NOT BE USED
 *
 * @author Del Myers
 * @since 1.12
 * @noreference This interface is not intended to be referenced by clients.
 * @deprecated Use {@link EntityConnectionStyleDecorator} instead. This
 *             interface will be removed after the 2028-09 release.
 */
// TODO Zest 2.x - Integrate into IEntityConnectionStyleProvider
@SuppressWarnings("removal")
@Deprecated(since = "1.19", forRemoval = true)
public interface IEntityConnectionStyleProvider2 extends IEntityConnectionStyleProvider {
	/**
	 * Returns the tooltip for the connection.
	 *
	 * @param src  the source entity.
	 * @param dest the destination entity.
	 * @return the tooltip for the connection. Null for default.
	 * @since 1.12
	 */
	@Deprecated(since = "1.19", forRemoval = true)
	IFigure getTooltip(Object src, Object dest);

	/**
	 * Returns the connection router of the single relation.
	 *
	 * @param src  the source entity.
	 * @param dest the destination entity.
	 * @return the router for the connection. Null for default.
	 * @since 1.12
	 */
	@Deprecated(since = "1.19", forRemoval = true)
	ConnectionRouter getRouter(Object src, Object dest);
}
