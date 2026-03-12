/*******************************************************************************
 * Copyright (c) 2000, 2026 IBM Corporation and others.
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
package org.eclipse.gef.examples.logicdesigner.figures;

import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RoutingAnimator;

import org.eclipse.gef.examples.logicdesigner.model.SimpleOutput;
import org.eclipse.gef.examples.logicdesigner.model.Wire;

public final class FigureFactory {

	private static final int WIRE_FIGURE_WIDTH = 2;

	public static PolylineConnection createNewBendableWire(Wire wire) {
		PolylineConnection conn = new PolylineConnection();
		conn.addRoutingListener(RoutingAnimator.getDefault());
		updateWireLook(wire, conn);
		return conn;
	}

	public static void updateWireLook(Wire wire, PolylineConnection conn) {
		if (wire != null && wire.getValue()) {
			conn.setForegroundColor(LogicEditorColors.INSTANCE.getWireTrue());
			conn.setLineWidth(FigureFactory.WIRE_FIGURE_WIDTH + 1);
			conn.setAlpha(200);
		} else {
			conn.setForegroundColor(LogicEditorColors.INSTANCE.getWireFalse());
			conn.setLineWidth(FigureFactory.WIRE_FIGURE_WIDTH);
			conn.setAlpha(255);
		}
	}

	public static PolylineConnection createNewWire(Wire wire) {

		PolylineConnection conn = createNewBendableWire(wire);
		PolygonDecoration arrow;

		if (wire == null || wire.getSource() instanceof SimpleOutput) {
			arrow = null;
		} else {
			arrow = new PolygonDecoration();
			arrow.setTemplate(PolygonDecoration.INVERTED_TRIANGLE_TIP);
			arrow.setScale(20, 10);
		}
		conn.setSourceDecoration(arrow);

		if (wire == null || wire.getTarget() instanceof SimpleOutput) {
			arrow = null;
		} else {
			arrow = new PolygonDecoration();
			arrow.setTemplate(PolygonDecoration.INVERTED_TRIANGLE_TIP);
			arrow.setScale(20, 10);
		}
		conn.setTargetDecoration(arrow);
		return conn;
	}

}
