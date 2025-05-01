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

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RoutingListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**
 * Subclass of the default RoutingAnimator to handle the special behavior for
 * the Flow editor. If a new edge is added to the chart, it should not be
 * animated in order to avoid visual artifacts. Instead, the connection is only
 * made visible after the animation has finished.
 */
public final class RoutingAnimator extends org.eclipse.draw2d.RoutingAnimator implements RoutingListener {

	private static final RoutingAnimator INSTANCE = new RoutingAnimator();

	private RoutingAnimator() {
		// Should never be instantiated directly
	}

	public static RoutingAnimator getDefault() {
		return INSTANCE;
	}

	@Override
	protected Object getCurrentState(IFigure figure) {
		Connection connection = (Connection) figure;
		PointList points = connection.getPoints().getCopy();
		// The initial PointList of a PolyLineConnection
		if (points.size() == 2 //
				&& points.getPoint(0).equals(Point.SINGLETON.setLocation(0, 0))
				&& points.getPoint(1).equals(Point.SINGLETON.setLocation(100, 100))) {
			return null;
		}
		return points;
	}

	@Override
	public void tearDown(IFigure figure) {
		figure.revalidate();
		figure.setVisible(true);
	}
}
