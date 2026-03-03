/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC,
 *                      Canada.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.zest.core.widgets.internal;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

public class LoopAnchor extends ChopboxAnchor {
	public LoopAnchor(IFigure owner) {
		super(owner);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.draw2d.ChopboxAnchor#getReferencePoint()
	 */
	@Override
	public Point getReferencePoint() {
		// modification to getReferencePoint. Returns
		// a point on the outside of the owners box, rather than the
		// center. Only usefull for self-loops.
		if (getOwner() == null) {
			return null;
		}
		Point ref = getOwner().getBounds().getCenter();
		ref.y = getOwner().getBounds().y;
		getOwner().translateToAbsolute(ref);
		return ref;
	}
}
