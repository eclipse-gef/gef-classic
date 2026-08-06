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
 ******************************************************************************/
package org.eclipse.zest.core.viewers.decorators;

import org.eclipse.swt.graphics.Color;

import org.eclipse.draw2d.IFigure;

/**
 * An extension to Label providers for graphs. Gets specific details about the
 * style of an entity before it is created. This style provider offers:
 *
 * <ul>
 * <li>Background and foreground colors.</li>
 * <li>Highlighted and un-highlighted colors (colors defined by
 * selections).</li>
 * <li>Border color.</li>
 * <li>Highlighted and un-highlighted colors for borders.</li>
 * <li>Border width.</li>
 * <li>Font for text inside the entity.</li>
 * </ul>
 *
 * Any method may return {@code null} if the Zest defaults are preferred.
 *
 * NOTE: It is up to the implementors of this interface to dispose of any Fonts
 * that are created by this class.
 *
 * @author Del Myers
 * @since 1.19
 */
public interface IEntityStyleDecorator {

	/**
	 * Returns {@code true} if the node should be displayed with a fish-eye effect.
	 *
	 * @param entity The entity to be styled
	 * @return {@code true} if the node should be displayed with a fish-eye effect,
	 *         {@code false} otherwise.
	 */
	boolean fisheyeNode(Object entity);

	/**
	 * Returns the background color that this node should be colored. This will be
	 * ignored if {@link #getBackgroundColor(Object)} returns {@code null}.
	 *
	 * @param entity The entity to be styled
	 * @return The color for the node
	 */
	Color getBackgroundColor(Object entity);

	/**
	 * Returns the background color for this entity. May return {@code null} for
	 * defaults.
	 *
	 * @param entity the entity to be styled.
	 * @return the background color for this entity.
	 */
	Color getBorderColor(Object entity);

	/**
	 * Returns the border highlight color for this entity. May return {@code null}
	 * for defaults.
	 *
	 * @param entity the entity to be styled.
	 * @return the border highlight color for this entity.
	 */
	Color getBorderHighlightColor(Object entity);

	/**
	 * Returns the border width for this entity. May return {@code -1} for defaults.
	 *
	 * @param entity the entity to be styled.
	 * @return the border width, or {@code -1} for defaults.
	 */
	int getBorderWidth(Object entity);

	/**
	 * Returns the foreground color that this node should be colored. This will be
	 * ignored if {@link #getForegroundColor(Object)} returns {@code null}.
	 *
	 * @param entity The entity to be styled
	 * @return The color for the node
	 */
	Color getForegroundColor(Object entity);

	/**
	 * Returns the foreground highlight color of this entity. May return
	 * {@code null} for defaults.
	 *
	 * @param entity the entity to be styled.
	 * @return the foreground color of this entity.
	 */
	Color getNodeForegroundHighlightColor(Object entity);

	/**
	 * Returns the background highlight color of this entity. May return
	 * {@code null} for defaults.
	 *
	 * @param entity the entity to be styled.
	 * @return the background color of this entity.
	 */
	Color getNodeBackgroundHighlightColor(Object entity);

	/**
	 * Returns the tool-tip for this node. If {@code null} is returned Zest will
	 * simply use the default tool-tip.
	 *
	 * @param entity The entity to be styled
	 * @return The tool-tip for the node
	 */
	IFigure getTooltip(Object entity);
}
