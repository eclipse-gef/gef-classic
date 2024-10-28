/*******************************************************************************
 * Copyright (c) Patrick Ziegler and others.
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

package org.eclipse.draw2d.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FanRouterTest {
	FanRouter router;
	Connection connection;
	ConnectionAnchor sourceAnchor;
	ConnectionAnchor targetAnchor;

	@BeforeEach
	public void setUp() {
		sourceAnchor = new XYAnchor(new Point(0, 0));
		targetAnchor = new XYAnchor(new Point(0, 50));

		connection = new PolylineConnection();
		connection.setSourceAnchor(sourceAnchor);
		connection.setTargetAnchor(targetAnchor);

		router = new FanRouter();
	}

	/**
	 * When routing the "same" connection multiple times, no additional break point
	 * needs to be created.
	 */
	@Test
	public void testRouteSameConnection() {
		router.route(connection);
		assertEquals(connection.getPoints().size(), 2);

		router.route(connection);
		assertEquals(connection.getPoints().size(), 2);
	}
}
