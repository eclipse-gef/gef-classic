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
 * An extension to graph label decorators, to supply styles for connections
 * based upon relationships, rather than on connected nodes.
 *
 * @author Del Myers
 * @since 1.19
 */
public abstract class ConnectionStyleDecorator extends GraphLabelDecorator {

	@Override
	public void decorateConnection(GraphConnection connection) {
		Object rel = connection.getData();

		int style = getConnectionStyle(rel);
		if (!ZestStyles.validateConnectionStyle(style)) {
			throw new SWTError(SWT.ERROR_INVALID_ARGUMENT);
		}

		if (style != ZestStyles.NONE) {
			connection.setConnectionStyle(style);
		}

		Optional.ofNullable(getHighlightColor(rel)).ifPresent(connection::setHighlightColor);
		Optional.ofNullable(getColor(rel)).ifPresent(connection::setLineColor);
		Optional.ofNullable(getTooltip(rel)).ifPresent(connection::setTooltip);
		Optional.ofNullable(getLineWidth(rel)).filter(w -> w >= 0).ifPresent(connection::setLineWidth);
		Optional.ofNullable(getRouter(rel)).ifPresent(connection::setRouter);
	}

	/**
	 * Returns the color for the connection. {@code null} for default.
	 *
	 * @param rel the relationship represented by this connection.
	 * @return the color.
	 */
	protected abstract Color getColor(Object rel);

	/**
	 * Returns the style flags for this connection. Valid flags are those that begin
	 * with CONNECTION in {@link ZestStyles}. Check {@link ZestStyles} for legal
	 * combinations.
	 *
	 * @param rel the relationship represented by this connection.
	 * @return the style flags for this connection.
	 * @see ZestStyles
	 */
	protected abstract int getConnectionStyle(Object rel);

	/**
	 * Returns the highlighted color for this connection. {@code null} for default.
	 *
	 * @param rel the relationship represented by this connection.
	 * @return the highlighted color. {@code null} for default.
	 */
	protected abstract Color getHighlightColor(Object rel);

	/**
	 * Returns the line width of the connection. {@code -1} for default.
	 *
	 * @param rel the relationship represented by this connection.
	 * @return the line width for the connection. {@code -1} for default.
	 */
	protected abstract int getLineWidth(Object rel);

	/**
	 * Returns the connection router of the single relation.
	 *
	 * @param rel the relationship represented by this connection.
	 * @return the connection router for {@code rel}. {@code null} for default.
	 */
	protected abstract ConnectionRouter getRouter(Object rel);

	/**
	 * Returns the tool-tip for this node. If {@code null} is returned Zest will
	 * simply use the default tool-tip.
	 *
	 * @param entity
	 */
	protected abstract IFigure getTooltip(Object entity);
}
