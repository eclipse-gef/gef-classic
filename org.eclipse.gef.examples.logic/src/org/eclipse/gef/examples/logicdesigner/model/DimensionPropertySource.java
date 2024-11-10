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

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import org.eclipse.draw2d.geometry.Dimension;

import org.eclipse.gef.examples.logicdesigner.LogicMessages;

public class DimensionPropertySource implements IPropertySource {

	public static final String ID_WIDTH = "width"; //$NON-NLS-1$
	public static final String ID_HEIGHT = "height";//$NON-NLS-1$
	protected static IPropertyDescriptor[] descriptors;

	static {
		PropertyDescriptor widthProp = new TextPropertyDescriptor(ID_WIDTH,
				LogicMessages.DimensionPropertySource_Property_Width_Label);
		widthProp.setValidator(LogicNumberCellEditorValidator.instance());
		PropertyDescriptor heightProp = new TextPropertyDescriptor(ID_HEIGHT,
				LogicMessages.DimensionPropertySource_Property_Height_Label);
		heightProp.setValidator(LogicNumberCellEditorValidator.instance());
		descriptors = new IPropertyDescriptor[] { widthProp, heightProp };
	}

	protected Dimension dimension = null;

	public DimensionPropertySource(Dimension dimension) {
		this.dimension = dimension.getCopy();
	}

	@Override
	public Object getEditableValue() {
		return dimension.getCopy();
	}

	@Override
	public Object getPropertyValue(Object propName) {
		return getPropertyValue((String) propName);
	}

	public Object getPropertyValue(String propName) {
		if (ID_HEIGHT.equals(propName)) {
			return Integer.toString(dimension.height);
		}
		if (ID_WIDTH.equals(propName)) {
			return Integer.toString(dimension.width);
		}
		return null;
	}

	@Override
	public void setPropertyValue(Object propName, Object value) {
		setPropertyValue((String) propName, value);
	}

	public void setPropertyValue(String propName, Object value) {
		if (ID_HEIGHT.equals(propName)) {
			dimension.height = Integer.parseInt((String) value);
		}
		if (ID_WIDTH.equals(propName)) {
			dimension.width = Integer.parseInt((String) value);
		}
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public void resetPropertyValue(String propName) {
	}

	@Override
	public void resetPropertyValue(Object propName) {
	}

	@Override
	public boolean isPropertySet(Object propName) {
		return true;
	}

	@SuppressWarnings("static-method")
	public boolean isPropertySet(String propName) {
		return (ID_HEIGHT.equals(propName) || ID_WIDTH.equals(propName));
	}

	@Override
	public String toString() {
		return "(" + dimension.width + "," + dimension.height + ")"; //$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	}

}
