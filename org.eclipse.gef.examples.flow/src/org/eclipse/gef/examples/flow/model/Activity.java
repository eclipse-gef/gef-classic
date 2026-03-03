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
package org.eclipse.gef.examples.flow.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class Activity extends FlowElement {

	protected static IPropertyDescriptor[] descriptors;

	public static final String NAME = "name"; //$NON-NLS-1$
	static {
		descriptors = new IPropertyDescriptor[] { new TextPropertyDescriptor(NAME, "Name") }; //$NON-NLS-1$
	}

	static final long serialVersionUID = 1;
	private final List<Transition> inputs = new ArrayList<>();
	private String name = "Activity"; //$NON-NLS-1$
	private final List<Transition> outputs = new ArrayList<>();
	private int sortIndex;

	public Activity() {
	}

	public Activity(String s) {
		setName(s);
	}

	public void addInput(Transition transition) {
		inputs.add(transition);
		fireStructureChange(INPUTS, transition);
	}

	public void addOutput(Transition transtition) {
		outputs.add(transtition);
		fireStructureChange(OUTPUTS, transtition);
	}

	public List<Transition> getIncomingTransitions() {
		return inputs;
	}

	public String getName() {
		return name;
	}

	public List<Transition> getOutgoingTransitions() {
		return outputs;
	}

	/**
	 * Returns useful property descriptors for the use in property sheets. this
	 * supports location and size.
	 *
	 * @return Array of property descriptors.
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	/**
	 * Returns an Object which represents the appropriate value for the property
	 * name supplied.
	 *
	 * @param propName Name of the property for which the the values are needed.
	 * @return Object which is the value of the property.
	 */
	@Override
	public Object getPropertyValue(Object propName) {
		if (NAME.equals(propName)) {
			return getName();
		}
		return super.getPropertyValue(propName);
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void removeInput(Transition transition) {
		inputs.remove(transition);
		fireStructureChange(INPUTS, transition);
	}

	public void removeOutput(Transition transition) {
		outputs.remove(transition);
		fireStructureChange(OUTPUTS, transition);
	}

	public void setName(String s) {
		name = s;
		firePropertyChange(NAME, null, s);
	}

	/**
	 * Sets the value of a given property with the value supplied.
	 *
	 * @param id    Name of the parameter to be changed.
	 * @param value Value to be set to the given parameter.
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (id == NAME) {
			setName((String) value);
		}
	}

	public void setSortIndex(int i) {
		sortIndex = i;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String className = getClass().getName();
		className = className.substring(className.lastIndexOf('.') + 1);
		return className + "(" + name + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
