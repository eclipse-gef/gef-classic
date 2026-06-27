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
package org.eclipse.zest.core.viewers.decorators;

import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.graphics.Color;

import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.ZestStyles;

import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;

/**
 * An extension for graph label decorators which allows users to set styles for
 * connections that are based on entity end points.
 *
 * @author Del Myers
 * @since 1.19
 */
public abstract class EntityConnectionStyleDecorator extends GraphLabelDecorator {

	@Override
	public void decorateConnection(GraphConnection connection) {
		Object src = connection.getSource().getData();
		Object dest = connection.getDestination().getData();

		int style = getConnectionStyle(src, dest);
		if (!ZestStyles.validateConnectionStyle(style)) {
			throw new SWTError(SWT.ERROR_INVALID_ARGUMENT);
		}

		if (style != ZestStyles.NONE) {
			connection.setConnectionStyle(style);
		}

		Optional.ofNullable(getColor(src, dest)).ifPresent(connection::setLineColor);
		Optional.ofNullable(getHighlightColor(src, dest)).ifPresent(connection::setHighlightColor);
		Optional.ofNullable(getTooltip(src, dest)).ifPresent(connection::setTooltip);
		Optional.ofNullable(getLineWidth(src, dest)).filter(w -> w >= 0).ifPresent(connection::setLineWidth);
		Optional.ofNullable(getRouter(src, dest)).ifPresent(connection::setRouter);
	}

	/**
	 * Returns the color for the connection. {@code null} for default.
	 *
	 * @param src  the source entity.
	 * @param dest the destination entity.
	 * @return the color.
	 */
	protected abstract Color getColor(Object src, Object dest);

	/**
	 * Returns the style flags for this connection. Valid flags are those that begin
	 * with CONNECTION in {@link ZestStyles}. Check {@link ZestStyles} for legal
	 * combinations.
	 *
	 * @param src  the source entity.
	 * @param dest the destination entity.
	 * @return the style flags for this connection.
	 * @see ZestStyles
	 */
	protected abstract int getConnectionStyle(Object src, Object dest);

	/**
	 * Returns the highlighted color for this connection. {@code null} for default.
	 *
	 * @param src  the source entity.
	 * @param dest the destination entity.
	 * @return the highlighted color. {@code null} for default.
	 */
	protected abstract Color getHighlightColor(Object src, Object dest);

	/**
	 * Returns the line width of the connection. {@code -1} for default.
	 *
	 * @param src  the source entity.
	 * @param dest the destination entity.
	 * @return the line width for the connection. {@code -1} for default.
	 */
	protected abstract int getLineWidth(Object src, Object dest);

	/**
	 * Returns the connection router of the single relation.
	 *
	 * @param src  the source entity.
	 * @param dest the destination entity.
	 * @return the router for the connection. {@code null} for default.
	 */
	protected abstract ConnectionRouter getRouter(Object src, Object dest);

	/**
	 * Returns the tool-tip for the connection.
	 *
	 * @param src  the source entity.
	 * @param dest the destination entity.
	 * @return the tool-tip for the connection. {@code null} for default.
	 */
	protected abstract IFigure getTooltip(Object src, Object dest);
}
