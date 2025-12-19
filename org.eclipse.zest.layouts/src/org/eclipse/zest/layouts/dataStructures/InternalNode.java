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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.constraints.BasicEntityConstraint;
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
public class InternalNode implements Comparable, LayoutEntity {

	private LayoutEntity entity = null;
	private final Map<Object, Object> attributeMap = new HashMap<>();
	@Deprecated
	BasicEntityConstraint basicEntityConstraint = new BasicEntityConstraint();

	@Deprecated
	public InternalNode(LayoutEntity entity) {
		this.entity = entity;
		this.entity.setLayoutInformation(this);
		this.layoutWidth = entity.getWidthInLayout();
		this.layoutHeight = entity.getHeightInLayout();
		entity.populateLayoutConstraint(basicEntityConstraint);
	}

	@Deprecated
	public LayoutEntity getLayoutEntity() {
		return this.entity;
	}

	@Deprecated
	public double getPreferredX() {
		return basicEntityConstraint.preferredX;

	}

	@Deprecated
	public double getPreferredY() {
		return basicEntityConstraint.preferredY;
	}

	@Deprecated
	public boolean hasPreferredLocation() {
		return basicEntityConstraint.hasPreferredLocation;
	}

	@Deprecated
	double dx, dy;

	@Deprecated
	public void setDx(double x) {
		this.dx = x;
	}

	@Deprecated
	public void setDy(double y) {
		this.dy = y;
	}

	@Deprecated
	public double getDx() {
		return this.dx;
	}

	@Deprecated
	public double getDy() {
		return this.dy;
	}

	@Deprecated
	public double getCurrentX() {
		return entity.getXInLayout();
	}

	@Deprecated
	public double getCurrentY() {
		return entity.getYInLayout();
	}

	@Deprecated
	public void setLocation(double x, double y) {
		entity.setLocationInLayout(x, y);
	}

	@Deprecated
	public void setSize(double width, double height) {
		entity.setSizeInLayout(width, height);
	}

	@Deprecated
	double normalizedX = 0.0;
	@Deprecated
	double normalizedY = 0.0;
	@Deprecated
	double normalizedWidth = 0.0;
	@Deprecated
	double normalizedHeight = 0.0;

	@Deprecated
	public void setInternalLocation(double x, double y) {
		// entity.setLocationInLayout(x,y);

		normalizedX = x;
		normalizedY = y;

	}

	@Deprecated
	public DisplayIndependentPoint getInternalLocation() {
		return new DisplayIndependentPoint(getInternalX(), getInternalY());
	}

	@Deprecated
	public void setInternalSize(double width, double height) {
		normalizedWidth = width;
		normalizedHeight = height;
	}

	@Deprecated
	public double getInternalX() {
		// return entity.getXInLayout();
		return normalizedX;
	}

	@Deprecated
	public double getInternalY() {
		// return entity.getYInLayout();
		return normalizedY;
	}

	@Deprecated
	public double getInternalWidth() {
		return normalizedWidth;
	}

	@Deprecated
	public double getInternalHeight() {
		return normalizedHeight;
	}

	/**
	 * An algorithm may require a place to store information. Use this structure for
	 * that purpose.
	 */
	@Deprecated
	public void setAttributeInLayout(Object attribute, Object value) {
		attributeMap.put(attribute, value);
	}

	/**
	 * An algorithm may require a place to store information. Use this structure for
	 * that purpose.
	 */
	@Deprecated
	public Object getAttributeInLayout(Object attribute) {
		return attributeMap.get(attribute);
	}

	// TODO: Fix all these preferred stuff!!!!! NOW!

	@Deprecated
	@SuppressWarnings("static-method")
	public boolean hasPreferredWidth() {
		return false;
		// return enity.getAttributeInLayout(LayoutEntity.ATTR_PREFERRED_WIDTH) != null;
	}

	@Deprecated
	@SuppressWarnings("static-method")
	public double getPreferredWidth() {
		return 0.0;
//	    if (hasPreferredWidth()) {
//	        return ((Double)entity.getAttributeInLayout(LayoutEntity.ATTR_PREFERRED_WIDTH)).doubleValue();
//	    } else {
//	        return 10.0;
//	    }
	}

	@Deprecated
	@SuppressWarnings("static-method")
	public boolean hasPreferredHeight() {
		return false;
		// return entity.getAttributeInLayout(LayoutEntity.ATTR_PREFERRED_HEIGHT) !=
		// null;
	}

	@Deprecated
	@SuppressWarnings("static-method")
	public double getPreferredHeight() {
		return 0.0;
//	    if (hasPreferredHeight()) {
//	        return ((Double)entity.getAttributeInLayout(LayoutEntity.ATTR_PREFERRED_HEIGHT)).doubleValue();
//	    } else {
//	        return 10.0;
//	    }
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	@Deprecated
	public int compareTo(Object arg0) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	@Deprecated
	public String toString() {
		return (entity != null ? entity.toString() : ""); //$NON-NLS-1$
	}

	@Deprecated
	double layoutHeight;
	@Deprecated
	double layoutWidth;
	@Deprecated
	double layoutX;
	@Deprecated
	double layoutY;
	@Deprecated
	Object layoutInfo;

	@Override
	@Deprecated
	public double getHeightInLayout() {
		// TODO Auto-generated method stub
		return layoutHeight;
	}

	@Override
	@Deprecated
	public Object getLayoutInformation() {
		// TODO Auto-generated method stub
		return this.layoutInfo;
	}

	@Override
	@Deprecated
	public double getWidthInLayout() {
		// TODO Auto-generated method stub
		return layoutWidth;
	}

	@Override
	@Deprecated
	public double getXInLayout() {
		// TODO Auto-generated method stub
		return layoutX;
	}

	@Override
	@Deprecated
	public double getYInLayout() {
		// TODO Auto-generated method stub
		return layoutY;
	}

	@Override
	@Deprecated
	public void populateLayoutConstraint(LayoutConstraint constraint) {
		// TODO Auto-generated method stub

	}

	@Override
	@Deprecated
	public void setLayoutInformation(Object internalEntity) {
		this.layoutInfo = internalEntity;

	}

	@Override
	@Deprecated
	public void setLocationInLayout(double x, double y) {
		// TODO Auto-generated method stub
		this.layoutX = x;
		this.layoutY = y;

	}

	@Override
	@Deprecated
	public void setSizeInLayout(double width, double height) {
		this.layoutWidth = width;
		this.layoutHeight = height;
	}

	@Override
	@Deprecated
	public Object getGraphData() {
		return null;
	}

	@Override
	@Deprecated
	public void setGraphData(Object o) {
		// TODO Auto-generated method stub

	}

}
