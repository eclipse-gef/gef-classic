/*******************************************************************************
 * Copyright 2005, 2025 CHISEL Group, University of Victoria, Victoria, BC,
 *                      Canada, Johannes Kepler University Linz and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria, Alois Zoitl
 *******************************************************************************/
package org.eclipse.zest.layouts.algorithms.internal;

import java.util.Comparator;
import java.util.TreeSet;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;

/**
 * @deprecated No longer used in Zest 2.x. This class will be removed in a
 *             future release in accordance with the two year deprecation
 *             policy.
 * @noextend This class is not intended to be subclassed by clients.
 * @noreference This class is not intended to be referenced by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@Deprecated(since = "2.0", forRemoval = true)
public class DynamicScreen {

	private final TreeSet<InternalNode> xCoords = new TreeSet<>(new XComparator());
	private final TreeSet<InternalNode> yCoords = new TreeSet<>(new YComparator());

	private DisplayIndependentRectangle screenBounds = null;

	@Deprecated
	double minX = 0.0;
	@Deprecated
	double minY = 0.0;
	@Deprecated
	double maxX = 0.0;
	@Deprecated
	double maxY = 0.0;

	@Deprecated
	public void cleanScreen() {
		minX = 0.0;
		minY = 0.0;
		maxX = 0.0;
		maxY = 0.0;
	}

	@Deprecated
	class XComparator implements Comparator<InternalNode> {
		@Override
		@Deprecated
		public int compare(InternalNode n1, InternalNode n2) {
			if (n1.getInternalX() > n2.getInternalX()) {
				return +1;
			}
			if (n1.getInternalX() < n2.getInternalX()) {
				return -1;
			}
			return n1.toString().compareTo(n2.toString());
		}
	}

	@Deprecated
	class YComparator implements Comparator<InternalNode> {
		@Override
		@Deprecated
		public int compare(InternalNode n1, InternalNode n2) {
			if (n1.getInternalY() > n2.getInternalY()) {
				return +1;
			}
			if (n1.getInternalY() < n2.getInternalY()) {
				return -1;
			}
			return n1.toString().compareTo(n2.toString());

		}
	}

	@Deprecated
	public DynamicScreen(int x, int y, int width, int height) {
		this.screenBounds = new DisplayIndependentRectangle(x, y, width, height);
	}

	@Deprecated
	public void removeNode(InternalNode node) {
		xCoords.remove(node);
		yCoords.remove(node);
	}

	@Deprecated
	public void addNode(InternalNode node) {
		xCoords.add(node);
		yCoords.add(node);
	}

	@Deprecated
	public DisplayIndependentPoint getScreenLocation(InternalNode node) {

		DisplayIndependentRectangle layoutBounds = calculateBounds();

		double x = (layoutBounds.width == 0) ? 0 : (node.getInternalX() - layoutBounds.x) / layoutBounds.width;
		double y = (layoutBounds.height == 0) ? 0 : (node.getInternalY() - layoutBounds.y) / layoutBounds.height;

		x = screenBounds.x + x * screenBounds.width;
		y = screenBounds.y + y * screenBounds.height;

		return new DisplayIndependentPoint(x, y);
	}

	@Deprecated
	public DisplayIndependentPoint getVirtualLocation(DisplayIndependentPoint point) {

		DisplayIndependentRectangle layoutBounds = calculateBounds();

		double x = (point.x / screenBounds.width) * layoutBounds.width + layoutBounds.x;
		double y = (point.y / screenBounds.height) * layoutBounds.height + layoutBounds.y;

		return new DisplayIndependentPoint(x, y);
	}

	private DisplayIndependentRectangle calculateBounds() {
		InternalNode n1 = xCoords.first();
		InternalNode n2 = xCoords.last();
		InternalNode n3 = yCoords.first();
		InternalNode n4 = yCoords.last();
		double x = n1.getInternalX();
		double width = n2.getInternalX();
		double y = n3.getInternalY();
		double height = n4.getInternalY();
		return new DisplayIndependentRectangle(x, y, width - x, height - y);
	}

}
