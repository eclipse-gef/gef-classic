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
package org.eclipse.zest.core.viewers;

import org.eclipse.swt.graphics.Color;

import org.eclipse.ui.services.IDisposable;

import org.eclipse.draw2d.IFigure;

/**
 * An extension to Label providers for graphs. Gets specific details about the
 * style of an entity before it is created. This style provider offers:
 *
 * -Background and forground colours -Hilighted and unhighlighted colours
 * (colours defined by selections). -Border color. -Highlighted and
 * unhighlighted colours for borders. -Border width -Font for text inside the
 * entity.
 *
 * Any method may return null if the Zest defaults are preferred.
 *
 * NOTE: It is up to the implementors of this interface to dispose of any Colors
 * or Fonts that are created by this class. The dispose() method will be called
 * at the end of the entity's life-cycle so that this class may dispose of its
 * resources.
 *
 * @author Del Myers
 * @see org.eclipse.jface.viewers.IColorProvider
 * @tag bug(151327-Styles) : created to solve this bug
 */
public interface IEntityStyleProvider extends IDisposable {

	/**
	 * Returns the forground colour of this entity. May return null for defaults.
	 * Any resources created by this class must be disposed by this class.
	 *
	 * @param entity the entity to be styled.
	 * @return the forground colour of this entity.
	 * @see #dispose()
	 */
	public Color getNodeHighlightColor(Object entity);

	/**
	 * Returns the background colour for this entity. May return null for defaults.
	 * Any resources created by this class must be disposed by this class.
	 *
	 * @param entity the entity to be styled.
	 * @return the background colour for this entity.
	 * @see #dispose()
	 */
	public Color getBorderColor(Object entity);

	/**
	 * Returns the border highlight colour for this entity. May return null for
	 * defaults. Any resources created by this class must be disposed by this class.
	 *
	 * @param entity the entity to be styled.
	 * @return the border highlight colour for this entity.
	 * @see #dispose()
	 */
	public Color getBorderHighlightColor(Object entity);

	/**
	 * Returns the border width for this entity. May return -1 for defaults.
	 *
	 * @param entity the entity to be styled.
	 * @return the border width, or -1 for defaults.
	 */
	public int getBorderWidth(Object entity);

	/**
	 * Returns the colour that this node should be coloured. This will be ignored if
	 * getNodeColour returns null. Any resources created by this class must be
	 * diposed by this class.
	 *
	 * @param entity The entity to be styled
	 * @return The colour for the node
	 * @see #dispose()
	 */
	public Color getBackgroundColour(Object entity);

	public Color getForegroundColour(Object entity);

	/**
	 * Returns the tooltop for this node. If null is returned Zest will simply use
	 * the default tooltip.
	 *
	 * @param entity
	 */
	public IFigure getTooltip(Object entity);

	public boolean fisheyeNode(Object entity);

}
