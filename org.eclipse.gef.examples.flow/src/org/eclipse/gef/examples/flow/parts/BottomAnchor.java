/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
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
package org.eclipse.gef.examples.flow.parts;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

class BottomAnchor extends AbstractConnectionAnchor {

	private final int offset;

	BottomAnchor(IFigure source, int offset) {
		super(source);
		this.offset = offset;
	}

	@Override
	public Point getLocation(Point reference) {
		Rectangle r = getOwner().getBounds().getCopy();
		getOwner().translateToAbsolute(r);
		int off = offset;
		if (off == -1) {
			off = r.width / 2;
		}
		if (r.contains(reference) || r.bottom() > reference.y) {
			return r.getTopLeft().translate(off, 0);
		} else {
			return r.getBottomLeft().translate(off, -1);
		}
	}

}
