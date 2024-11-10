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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import org.eclipse.gef.examples.logicdesigner.LogicMessages;

public abstract class LogicSubpart extends LogicElement {

	private String id;
	private LogicGuide verticalGuide;
	private LogicGuide horizontalGuide;
	protected Map<String, Wire> inputs = new HashMap<>(7);
	protected Point location = new Point(0, 0);
	protected List<Wire> outputs = new ArrayList<>(4);
	static final long serialVersionUID = 1;
	protected Dimension size = new Dimension(-1, -1);

	protected static IPropertyDescriptor[] descriptors = null;
	public static final String ID_SIZE = "size"; //$NON-NLS-1$
	public static final String ID_LOCATION = "location"; //$NON-NLS-1$

	static {
		descriptors = new IPropertyDescriptor[] {
				new PropertyDescriptor(ID_SIZE, LogicMessages.PropertyDescriptor_LogicSubPart_Size),
				new PropertyDescriptor(ID_LOCATION, LogicMessages.PropertyDescriptor_LogicSubPart_Location) };
	}

	protected static Image createImage(Class rsrcClass, String name) {
		return ImageDescriptor.createFromFile(rsrcClass, name).createImage();
	}

	protected LogicSubpart() {
		setID(getNewID());
	}

	public void connectInput(Wire w) {
		inputs.put(w.getTargetTerminal(), w);
		update();
		fireStructureChange(INPUTS, w);
	}

	public void connectOutput(Wire w) {
		outputs.add(w);
		update();
		fireStructureChange(OUTPUTS, w);
	}

	public void disconnectInput(Wire w) {
		inputs.remove(w.getTargetTerminal());
		update();
		fireStructureChange(INPUTS, w);
	}

	public void disconnectOutput(Wire w) {
		outputs.remove(w);
		update();
		fireStructureChange(OUTPUTS, w);
	}

	public List<Wire> getConnections() {
		List<Wire> conns = new ArrayList<>(outputs);
		conns.addAll(inputs.values());
		return conns;
	}

	public LogicGuide getHorizontalGuide() {
		return horizontalGuide;
	}

	public Image getIcon() {
		return getIconImage();
	}

	abstract public Image getIconImage();

	public String getID() {
		return id;
	}

	protected boolean getInput(String terminal) {
		if (inputs.isEmpty()) {
			return false;
		}
		Wire w = inputs.get(terminal);
		return (w == null) ? false : w.getValue();

	}

	public Point getLocation() {
		return location;
	}

	protected abstract String getNewID();

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
		if (ID_SIZE.equals(propName)) {
			return new DimensionPropertySource(getSize());
		}
		if (ID_LOCATION.equals(propName)) {
			return new LocationPropertySource(getLocation());
		}
		return null;
	}

	public Dimension getSize() {
		return size;
	}

	public List<Wire> getSourceConnections() {
		return new ArrayList<>(outputs);
	}

	public List<Wire> getTargetConnections() {
		return new ArrayList<>(inputs.values());
	}

	public LogicGuide getVerticalGuide() {
		return verticalGuide;
	}

	/**
	*
	*/
	@SuppressWarnings("static-method")
	public boolean isPropertySet() {
		return true;
	}

	public void setHorizontalGuide(LogicGuide hGuide) {
		horizontalGuide = hGuide;
		/*
		 * @TODO:Pratik firePropertyChanged?
		 */
	}

	/*
	 * Does nothing for the present, but could be used to reset the properties of
	 * this to whatever values are desired.
	 *
	 * @param id Parameter which is to be reset.
	 *
	 * public void resetPropertyValue(Object id){ if(ID_SIZE.equals(id)){;}
	 * if(ID_LOCATION.equals(id)){;} }
	 */

	public void setID(String s) {
		id = s;
	}

	public void setLocation(Point p) {
		if (location.equals(p)) {
			return;
		}
		location = p;
		firePropertyChange("location", null, p); //$NON-NLS-1$
	}

	protected void setOutput(String terminal, boolean val) {
		outputs.stream().filter(w -> (w.getSourceTerminal().equals(terminal) && this.equals(w.getSource())))
				.forEach(w -> w.setValue(val));
	}

	/**
	 * Sets the value of a given property with the value supplied. Also fires a
	 * property change if necessary.
	 *
	 * @param id    Name of the parameter to be changed.
	 * @param value Value to be set to the given parameter.
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (ID_SIZE.equals(id)) {
			setSize((Dimension) value);
		} else if (ID_LOCATION.equals(id)) {
			setLocation((Point) value);
		}
	}

	public void setSize(Dimension d) {
		if (size.equals(d)) {
			return;
		}
		size = d;
		firePropertyChange("size", null, size); //$NON-NLS-1$
	}

	public void setVerticalGuide(LogicGuide vGuide) {
		verticalGuide = vGuide;
	}

}
