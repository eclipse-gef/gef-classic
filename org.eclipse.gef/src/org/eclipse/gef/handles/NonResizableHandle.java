/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
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
package org.eclipse.gef.handles;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Locator;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.DragEditPartsTracker;

/**
 * A MoveHandle for a non-resizable EditPart.
 *
 * @deprecated this handle type is no longer used
 */
@Deprecated
public class NonResizableHandle extends MoveHandle {

	/**
	 * The border
	 */
	@Deprecated
	protected CornerTriangleBorder border;

	/**
	 * Creates a NonResizableHandle for the given <code>GraphicalEditPart</code>
	 * using a default {@link Locator}.
	 *
	 * @param owner The GraphicalEditPart to be moved by this handle.
	 */
	@Deprecated
	public NonResizableHandle(GraphicalEditPart owner) {
		this(owner, new MoveHandleLocator(owner.getFigure()));
	}

	/**
	 * Creates a NonResizableHandle for the given <code>GraphicalEditPart</code>
	 * using the given <code>Locator</code>.
	 *
	 * @param owner The GraphicalEditPart to be moved by this handle.
	 * @param loc   The Locator used to place the handle.
	 */
	@Deprecated
	public NonResizableHandle(GraphicalEditPart owner, Locator loc) {
		super(owner, loc);
	}

	/**
	 * Initializes the handle. Sets the {@link org.eclipse.gef.DragTracker} and
	 * DragCursor.
	 */
	@Override
	@Deprecated
	protected void initialize() {
		setOpaque(false);
		border = new CornerTriangleBorder(false);
		setBorder(border);
		setCursor(Cursors.SIZEALL);
		setDragTracker(new DragEditPartsTracker(getOwner()));
	}

	/**
	 * Updates the handle's color by setting the border's primary attribute.
	 */
	@Override
	@Deprecated
	public void validate() {
		border.setPrimary(getOwner().getSelected() == EditPart.SELECTED_PRIMARY);
		super.validate();
	}

}
