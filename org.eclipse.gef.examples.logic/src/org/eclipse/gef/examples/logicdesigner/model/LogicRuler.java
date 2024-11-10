/*******************************************************************************
 * Copyright (c) 2003, 2024 IBM Corporation and others.
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
package org.eclipse.gef.examples.logicdesigner.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.rulers.RulerProvider;

/**
 * @author Pratik Shah
 */
public class LogicRuler implements Serializable {

	public static final String PROPERTY_CHILDREN = "children changed"; //$NON-NLS-1$
	public static final String PROPERTY_UNIT = "units changed"; //$NON-NLS-1$

	static final long serialVersionUID = 1;

	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private int unit;
	private final boolean horizontal;
	private final List<LogicGuide> guides = new ArrayList<>();

	public LogicRuler(boolean isHorizontal) {
		this(isHorizontal, RulerProvider.UNIT_PIXELS);
	}

	public LogicRuler(boolean isHorizontal, int unit) {
		horizontal = isHorizontal;
		setUnit(unit);
	}

	public void addGuide(LogicGuide guide) {
		if (!guides.contains(guide)) {
			guide.setHorizontal(!isHorizontal());
			guides.add(guide);
			listeners.firePropertyChange(PROPERTY_CHILDREN, null, guide);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	// the returned list should not be modified
	public List<LogicGuide> getGuides() {
		return guides;
	}

	public int getUnit() {
		return unit;
	}

	@SuppressWarnings("static-method")
	public boolean isHidden() {
		return false;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public void removeGuide(LogicGuide guide) {
		if (guides.remove(guide)) {
			listeners.firePropertyChange(PROPERTY_CHILDREN, null, guide);
		}
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	public void setHidden(boolean isHidden) {
	}

	public void setUnit(int newUnit) {
		if (unit != newUnit) {
			int oldUnit = unit;
			unit = newUnit;
			listeners.firePropertyChange(PROPERTY_UNIT, oldUnit, newUnit);
		}
	}

}
