/*******************************************************************************
 * Copyright (c) 2025 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/

package org.eclipse.gef.examples.flow.parts;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.Animation;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Subclass of the default LayoutAnimator to handle the special behavior for the
 * Flow editor. If a new node is added to the chart, the animation should start
 * from its destination, rather than the top left corner of the editor. In order
 * to still give the appearance of an animation, the height remains zero and
 * slowly grows to its final value.
 */
public final class LayoutAnimator extends org.eclipse.draw2d.LayoutAnimator implements LayoutListener {
	private static LayoutAnimator INSTANCE = new LayoutAnimator();

	private LayoutAnimator() {
		// Should never be instantiated directly
	}

	public static LayoutAnimator getDefault() {
		return INSTANCE;
	}

	@Override
	protected Map<IFigure, Rectangle> getCurrentState(IFigure container) {
		Map<IFigure, Rectangle> locations = new HashMap<>();
		container.getChildren().forEach(child -> locations.put(child, child.getBounds().getCopy()));

		@SuppressWarnings("unchecked")
		Map<IFigure, Rectangle> initialState = (Map<IFigure, Rectangle>) Animation.getInitialState(this, container);
		if (initialState != null) {
			initialState.forEach((figure, initialRectangle) -> {
				Rectangle rectangle = locations.get(figure);
				if (initialRectangle.isEmpty()) {
					initialRectangle.x = rectangle.x;
					initialRectangle.y = rectangle.y;
					initialRectangle.width = rectangle.width;
				}
			});
		}

		return locations;
	}
}
