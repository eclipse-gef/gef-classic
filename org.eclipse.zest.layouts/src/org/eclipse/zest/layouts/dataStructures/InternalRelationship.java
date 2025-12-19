/*******************************************************************************
 * Copyright 2005, 2025 CHISEL Group, University of Victoria, Victoria,
 *                      BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.zest.layouts.dataStructures;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.zest.layouts.LayoutBendPoint;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.constraints.BasicEdgeConstraints;
import org.eclipse.zest.layouts.constraints.LayoutConstraint;

/**
 * @author Ian Bull
 * @deprecated No longer used in Zest 2.x. This class will be removed in a
 *             future release.
 * @noextend This class is not intended to be subclassed by clients.
 * @noreference This class is not intended to be referenced by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@SuppressWarnings("rawtypes")
@Deprecated(since = "2.0", forRemoval = true)
public class InternalRelationship implements LayoutRelationship {

	private final LayoutRelationship externalRelationship;
	private final InternalNode source;
	private final InternalNode destination;
	private Object layoutInfo;
	private final List<BendPoint> bendPoints = new LinkedList<>();
	@Deprecated
	BasicEdgeConstraints basicEdgeConstraints = new BasicEdgeConstraints();

	@Deprecated
	public InternalRelationship(LayoutRelationship externalRelationship, InternalNode source,
			InternalNode destination) {
		this.externalRelationship = externalRelationship;
		this.externalRelationship.setLayoutInformation(this);
		this.source = source;
		this.destination = destination;
		this.externalRelationship.populateLayoutConstraint(basicEdgeConstraints);
	}

	@Deprecated
	public LayoutRelationship getLayoutRelationship() {
		return externalRelationship;
	}

	@Deprecated
	public InternalNode getSource() {
		if (this.source == null) {
			throw new RuntimeException("Source is null"); //$NON-NLS-1$
		}
		return this.source;
	}

	@Deprecated
	public InternalNode getDestination() {
		if (this.destination == null) {
			throw new RuntimeException("Dest is null"); //$NON-NLS-1$
		}
		return this.destination;
	}

	@Deprecated
	public double getWeight() {
		return this.basicEdgeConstraints.weight;
	}

	@Deprecated
	public boolean isBidirectional() {
		return this.basicEdgeConstraints.isBiDirectional;
	}

	/**
	 * Ensure this is called in order of source to target node position.
	 *
	 * @param x
	 * @param y
	 */
	@Deprecated
	public void addBendPoint(double x, double y) {
		bendPoints.add(new BendPoint(x, y));
	}

	/**
	 * Ensure this is called in order of source to target node position. Specifies
	 * if the bendpoint is a curve control point
	 *
	 * @param x
	 * @param y
	 * @param isControlPoint
	 */
	@Deprecated
	public void addBendPoint(double x, double y, boolean isControlPoint) {
		bendPoints.add(new BendPoint(x, y, isControlPoint));
	}

	@Deprecated
	public List getBendPoints() {
		return this.bendPoints;
	}

	@Override
	@Deprecated
	public void clearBendPoints() {
		// TODO Auto-generated method stub

	}

	@Override
	@Deprecated
	public LayoutEntity getDestinationInLayout() {
		// TODO Auto-generated method stub
		return destination;
	}

	@Override
	@Deprecated
	public Object getLayoutInformation() {
		// TODO Auto-generated method stub
		return layoutInfo;
	}

	@Override
	@Deprecated
	public LayoutEntity getSourceInLayout() {
		// TODO Auto-generated method stub
		return source;
	}

	@Override
	@Deprecated
	public void populateLayoutConstraint(LayoutConstraint constraint) {
		// TODO Auto-generated method stub

	}

	@Override
	@Deprecated
	public void setBendPoints(LayoutBendPoint[] bendPoints) {
		// TODO Auto-generated method stub

	}

	@Override
	@Deprecated
	public void setLayoutInformation(Object layoutInformation) {
		this.layoutInfo = layoutInformation;

	}

	@Override
	@Deprecated
	public Object getGraphData() {
		return null;
	}

	@Override
	@Deprecated
	public void setGraphData(Object o) {

	}

}
