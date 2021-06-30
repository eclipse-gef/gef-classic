/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef3.internal.ui.rulers;

import java.util.List;

import org.eclipse.draw2dl.AbstractLayout;
import org.eclipse.draw2dl.IFigure;
import org.eclipse.draw2dl.XYLayout;
import org.eclipse.draw2dl.LayoutManager;
import org.eclipse.draw2dl.geometry.Dimension;
import org.eclipse.draw2dl.geometry.Rectangle;

/**
 * A custom layout manager for rulers. It is not meant to be used externally or
 * with any figure other than a
 * {@link org.eclipse.gef3.internal.ui.rulers.RulerFigure ruler}.
 * 
 * @author Pratik Shah
 * @since 3.0
 */
public class RulerLayout extends XYLayout {

	/**
	 * @see AbstractLayout#calculatePreferredSize(IFigure,
	 *      int, int)
	 */
	protected Dimension calculatePreferredSize(IFigure container, int wHint,
			int hHint) {
		return new Dimension(1, 1);
	}

	/**
	 * @see AbstractLayout#getConstraint(IFigure)
	 */
	public Object getConstraint(IFigure child) {
		return constraints.get(child);
	}

	/**
	 * @see LayoutManager#layout(IFigure)
	 */
	public void layout(IFigure container) {
		List<IFigure> children = container.getChildren();
		Rectangle rulerSize = container.getClientArea();
		for (IFigure child : children) {
			Dimension childSize = child.getPreferredSize();
			int position = (Integer) getConstraint(child);
			if (((RulerFigure) container).isHorizontal()) {
				childSize.height = rulerSize.height - 1;
				Rectangle.SINGLETON.setLocation(position
					- (childSize.width / 2), rulerSize.y);
			} else {
				childSize.width = rulerSize.width - 1;
				Rectangle.SINGLETON.setLocation(rulerSize.x, position
					- (childSize.height / 2));
			}
			Rectangle.SINGLETON.setSize(childSize);
			child.setBounds(Rectangle.SINGLETON);
		}
	}

}
