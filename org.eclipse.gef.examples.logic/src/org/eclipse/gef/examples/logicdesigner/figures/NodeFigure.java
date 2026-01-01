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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.graphics.GC;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Point;

public class NodeFigure extends Figure {
	protected static final int ALPHA_OPAQUE = 255;
	protected static final int ALPHA_FEEDBACK = 100;

	private int alpha = ALPHA_OPAQUE;

	protected Map<String, ConnectionAnchor> connectionAnchors = new HashMap<>(7);
	protected List<ConnectionAnchor> inputConnectionAnchors = new ArrayList<>(2);
	protected List<ConnectionAnchor> outputConnectionAnchors = new ArrayList<>(2);

	public ConnectionAnchor connectionAnchorAt(Point p) {
		ConnectionAnchor closest = null;
		long min = Long.MAX_VALUE;

		for (ConnectionAnchor c : getSourceConnectionAnchors()) {
			Point p2 = c.getLocation(null);
			long d = p.getDistanceSquared(p2);
			if (d < min) {
				min = d;
				closest = c;
			}
		}
		for (ConnectionAnchor c : getTargetConnectionAnchors()) {
			Point p2 = c.getLocation(null);
			long d = p.getDistanceSquared(p2);
			if (d < min) {
				min = d;
				closest = c;
			}
		}
		return closest;
	}

	public ConnectionAnchor getConnectionAnchor(String terminal) {
		return connectionAnchors.get(terminal);
	}

	public String getConnectionAnchorName(ConnectionAnchor c) {
		for (Entry<String, ConnectionAnchor> entry : connectionAnchors.entrySet()) {
			if (entry.getValue().equals(c)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public ConnectionAnchor getSourceConnectionAnchorAt(Point p) {
		ConnectionAnchor closest = null;
		long min = Long.MAX_VALUE;

		for (ConnectionAnchor c : getSourceConnectionAnchors()) {
			Point p2 = c.getLocation(null);
			long d = p.getDistanceSquared(p2);
			if (d < min) {
				min = d;
				closest = c;
			}
		}
		return closest;
	}

	public List<ConnectionAnchor> getSourceConnectionAnchors() {
		return outputConnectionAnchors;
	}

	public ConnectionAnchor getTargetConnectionAnchorAt(Point p) {
		ConnectionAnchor closest = null;
		long min = Long.MAX_VALUE;

		for (ConnectionAnchor c : getTargetConnectionAnchors()) {
			Point p2 = c.getLocation(null);
			long d = p.getDistanceSquared(p2);
			if (d < min) {
				min = d;
				closest = c;
			}
		}
		return closest;
	}

	public List<ConnectionAnchor> getTargetConnectionAnchors() {
		return inputConnectionAnchors;
	}

	/**
	 * Sets the transparency with which this figure is painted. <i>Note</i> The
	 * transparency is a hint and may be ignored.
	 *
	 * @param alpha A value between 0 and 255.
	 * @throws IllegalArgumentException If the value is outside the valid range.
	 */
	protected final void setAlpha(int alpha) throws IllegalArgumentException {
		if (alpha < 0 || alpha > 255) {
			throw new IllegalArgumentException("Alpha value must be between [0, 255]: %d".formatted(alpha)); //$NON-NLS-1$
		}
		this.alpha = alpha;
	}

	/**
	 * Returns the transparency with which this figure is painted as a value between
	 * {@code 0} (transparent) and {@code 255} (opaque).
	 *
	 * @see GC#getAlpha
	 */
	protected final int getAlpha() {
		return alpha;
	}
}
