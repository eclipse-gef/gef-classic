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

import org.eclipse.swt.graphics.Color;

import org.eclipse.ui.services.IDisposable;
import org.eclipse.zest.core.viewers.decorators.ConnectionStyleDecorator;

import org.eclipse.draw2d.IFigure;

/**
 * An extension to label providers, to supply styles for connections based upon
 * relationships, rather than on connected nodes.
 *
 * @author Del Myers
 * @see IGraphContentProvider
 * @see IEntityStyleProvider
 * @deprecated Use {@link ConnectionStyleDecorator} instead. This interface will
 *             be removed after the 2028-09 release.
 */
//@tag bug(151327-Styles) : created to solve this bug
@Deprecated(since = "1.19", forRemoval = true)
public interface IConnectionStyleProvider extends IDisposable {
	/**
	 * Returns the style flags for this connection. Valid flags are those that begin
	 * with CONNECTION in @see org.eclipse.zest.core.ZestStyles. Check ZestStyles
	 * for legal combinations.
	 *
	 * @param rel the relationship represented by this connection.
	 * @return the style flags for this connection.
	 * @see org.eclipse.zest.core.widgets.ZestStyles
	 */
	@Deprecated(since = "1.19", forRemoval = true)
	public int getConnectionStyle(Object rel);

	/**
	 * Returns the color for the connection. Null for default. Any resources created
	 * by this class must be disposed by this class.
	 *
	 * @param rel the relationship represented by this connection.
	 * @return the color.
	 * @see #dispose()
	 */
	@Deprecated(since = "1.19", forRemoval = true)
	public Color getColor(Object rel);

	/**
	 * Returns the highlighted color for this connection. Null for default. Any
	 * resources created by this class must be disposed by this class.
	 *
	 * @param rel the relationship represented by this connection.
	 * @return the highlighted color. Null for default.
	 * @see #dispose()
	 */
	@Deprecated(since = "1.19", forRemoval = true)
	public Color getHighlightColor(Object rel);

	/**
	 * Returns the line width of the connection. -1 for default.
	 *
	 * @param rel the relationship represented by this connection.
	 * @return the line width for the connection. -1 for default.
	 */
	@Deprecated(since = "1.19", forRemoval = true)
	public int getLineWidth(Object rel);

	/**
	 * Returns the tooltop for this node. If null is returned Zest will simply use
	 * the default tooltip.
	 *
	 * @param entity
	 */
	@Deprecated(since = "1.19", forRemoval = true)
	public IFigure getTooltip(Object entity);
}
