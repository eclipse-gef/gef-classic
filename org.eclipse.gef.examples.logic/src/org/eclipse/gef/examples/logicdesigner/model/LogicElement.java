/*******************************************************************************
 * Copyright (c) 2000, 2024 IBM Corporation and others.
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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public abstract class LogicElement implements IPropertySource, Cloneable, Serializable {

	public static final String CHILDREN = "Children"; //$NON-NLS-1$
	public static final String INPUTS = "inputs"; //$NON-NLS-1$
	public static final String OUTPUTS = "outputs"; //$NON-NLS-1$

	protected transient PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	static final long serialVersionUID = 1;

	public void addPropertyChangeListener(PropertyChangeListener l) {
		listeners.addPropertyChangeListener(l);
	}

	protected void firePropertyChange(String prop, Object old, Object newValue) {
		listeners.firePropertyChange(prop, old, newValue);
	}

	protected void fireChildAdded(String prop, Object child, Object index) {
		listeners.firePropertyChange(prop, index, child);
	}

	protected void fireChildRemoved(String prop, Object child) {
		listeners.firePropertyChange(prop, child, null);
	}

	protected void fireStructureChange(String prop, Object child) {
		listeners.firePropertyChange(prop, null, child);
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[0];
	}

	@Override
	public Object getPropertyValue(Object propName) {
		return null;
	}

	@SuppressWarnings("static-method")
	final Object getPropertyValue(String propName) {
		return null;
	}

	@Override
	public boolean isPropertySet(Object propName) {
		return isPropertySet((String) propName);
	}

	@SuppressWarnings("static-method")
	final boolean isPropertySet(String propName) {
		return true;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		listeners = new PropertyChangeSupport(this);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listeners.removePropertyChangeListener(l);
	}

	@Override
	public void resetPropertyValue(Object propName) {
	}

	final void resetPropertyValue(String propName) {
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
	}

	final void setPropertyValue(String propName, Object val) {
	}

	public void update() {
	}

}
