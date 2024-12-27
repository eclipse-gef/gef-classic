/*******************************************************************************
 * Copyright (c) 2000, 2023 IBM Corporation and others.
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
package org.eclipse.draw2d;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;

/**
 * Routes {@link Connection}s through a <code>List</code> of {@link Bendpoint
 * Bendpoints}.
 */
public class BendpointConnectionRouter extends AbstractRouter {

	private final Map<Connection, List<Bendpoint>> constraints = new HashMap<>(11);

	private static final PrecisionPoint A_POINT = new PrecisionPoint();

	/**
	 * Gets the constraint for the given {@link Connection}.
	 *
	 * @param connection The connection whose constraint we are retrieving
	 * @return The constraint
	 * @since 3.15
	 */
	@Override
	public List<Bendpoint> getConstraint(Connection connection) {
		return constraints.get(connection);
	}

	/**
	 * Removes the given connection from the map of constraints.
	 *
	 * @param connection The connection to remove
	 */
	@Override
	public void remove(Connection connection) {
		constraints.remove(connection);
	}

	/**
	 * Routes the {@link Connection}. Expects the constraint to be a List of
	 * {@link org.eclipse.draw2d.Bendpoint Bendpoints}.
	 *
	 * @param conn The connection to route
	 */
	@Override
	public void route(Connection conn) {
		PointList points = conn.getPoints();
		points.removeAllPoints();

		List<Bendpoint> bendpoints = getConstraint(conn);
		if (bendpoints == null) {
			bendpoints = Collections.emptyList();
		}

		Point ref1;
		Point ref2;

		if (bendpoints.isEmpty()) {
			ref1 = conn.getTargetAnchor().getReferencePoint();
			ref2 = conn.getSourceAnchor().getReferencePoint();
		} else {
			ref1 = new Point(bendpoints.get(0).getLocation());
			conn.translateToAbsolute(ref1);
			ref2 = new Point(bendpoints.get(bendpoints.size() - 1).getLocation());
			conn.translateToAbsolute(ref2);
		}

		A_POINT.setLocation(conn.getSourceAnchor().getLocation(ref1));
		conn.translateToRelative(A_POINT);
		points.addPoint(A_POINT);

		bendpoints.forEach(bp -> points.addPoint(bp.getLocation()));

		A_POINT.setLocation(conn.getTargetAnchor().getLocation(ref2));
		conn.translateToRelative(A_POINT);
		points.addPoint(A_POINT);
		conn.setPoints(points);
	}

	/**
	 * Sets the constraint for the given {@link Connection}.
	 *
	 * @param connection The connection whose constraint we are setting
	 * @param constraint The constraint, which as to be a List&lt;Bendpoint&gt;
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setConstraint(Connection connection, Object constraint) {
		constraints.put(connection, (List<Bendpoint>) constraint);
	}

}
