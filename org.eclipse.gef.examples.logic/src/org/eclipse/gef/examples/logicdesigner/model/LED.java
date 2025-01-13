/*******************************************************************************
 * Copyright (c) 2000, 2022 IBM Corporation and others.
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

import java.util.Arrays;

import org.eclipse.swt.graphics.Image;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import org.eclipse.draw2d.geometry.Dimension;

import org.eclipse.gef.examples.logicdesigner.LogicMessages;

public class LED extends LogicSubpart {

	static final long serialVersionUID = 1;

	private static final Dimension DEFAULT_SIZE = new Dimension(122, 94);

	private static final Image LED_ICON = createImage(LED.class, "icons/ledicon16.gif"); //$NON-NLS-1$
	private static int count;
	public static final String P_VALUE = "value"; //$NON-NLS-1$
	protected static IPropertyDescriptor[] newDescriptors = null;

	// Inputs
	public static final String TERMINAL_1_IN = "A"; //$NON-NLS-1$
	public static final String TERMINAL_2_IN = "B"; //$NON-NLS-1$
	public static final String TERMINAL_3_IN = "C"; //$NON-NLS-1$
	public static final String TERMINAL_4_IN = "D"; //$NON-NLS-1$
	// Outputs
	public static final String TERMINAL_1_OUT = "1"; //$NON-NLS-1$
	public static final String TERMINAL_2_OUT = "2"; //$NON-NLS-1$
	public static final String TERMINAL_3_OUT = "3"; //$NON-NLS-1$
	public static final String TERMINAL_4_OUT = "4"; //$NON-NLS-1$

	protected static final String[] IN_TERMINALS = { TERMINAL_1_IN, TERMINAL_2_IN, TERMINAL_3_IN, TERMINAL_4_IN };
	protected boolean[] bits = new boolean[4];

	static {
		PropertyDescriptor pValueProp = new TextPropertyDescriptor(P_VALUE, LogicMessages.PropertyDescriptor_LED_Value);
		pValueProp.setValidator(LogicNumberCellEditorValidator.instance());
		if (descriptors != null) {
			newDescriptors = Arrays.copyOf(descriptors, descriptors.length + 1);
			newDescriptors[descriptors.length] = pValueProp;
		} else {
			newDescriptors = new IPropertyDescriptor[] { pValueProp };
		}
	}

	public LED() {
		size.width = DEFAULT_SIZE.width;
		size.height = DEFAULT_SIZE.height;
		location.x = 20;
		location.y = 20;
	}

	@Override
	public Image getIconImage() {
		return LED_ICON;
	}

	@Override
	public String getNewID() {
		return Integer.toString(count++);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return newDescriptors;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (P_VALUE.equals(propName)) {
			return Integer.toString(getValue());
		}
		if (ID_SIZE.equals(propName)) {
			return ("(" + getSize().width + "," + getSize().height + ")");//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
		}
		return super.getPropertyValue(propName);
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (P_VALUE.equals(id)) {
			setValue(0);
		}
		super.resetPropertyValue(id);
	}

	public int getValue() {
		if (!inputs.isEmpty()) {
			boolean[] inputs = new boolean[4];
			inputs[0] = getInput(TERMINAL_1_IN);
			inputs[1] = getInput(TERMINAL_2_IN);
			inputs[2] = getInput(TERMINAL_3_IN);
			inputs[3] = getInput(TERMINAL_4_IN);
			return getValue(inputs);
		}
		return getValue(bits);
	}

	private static int getValue(boolean[] bits) {
		int val = 0;
		if (bits[0]) {
			val += 1;
		}
		if (bits[1]) {
			val += 2;
		}
		if (bits[2]) {
			val += 4;
		}
		if (bits[3]) {
			val += 8;
		}
		return val;
	}

	/**
	 * Nulls out any changes to this's size as it is fixed.
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (P_VALUE.equals(id)) {
			setValue(Integer.parseInt((String) value));
		} else {
			super.setPropertyValue(id, value);
		}
	}

	@Override
	public void setSize(Dimension d) {
		super.setSize(DEFAULT_SIZE);
	}

	public void setValue(int v) {
		if (!inputs.isEmpty()) {
			// ignore if we have inputs
			return;
		}
		int val = v % 16;
		bits = new boolean[4]; // Shorthand to set all bits to false
		if (val >= 8) {
			bits[3] = true;
		}
		if (val % 8 > 3) {
			bits[2] = true;
		}
		if (val % 4 > 1) {
			bits[1] = true;
		}
		bits[0] = val % 2 == 1;
		firePropertyChange(P_VALUE, null, null);
		update();
	}

	@Override
	public String toString() {
		return LogicMessages.LED_LabelText + " #" + getID() //$NON-NLS-1$
				+ " " + LogicMessages.PropertyDescriptor_LED_Value //$NON-NLS-1$
				+ "=" + getValue(); //$NON-NLS-1$
	}

	@Override
	public void update() {
		boolean[] outputBits = new boolean[4];
		if (!inputs.isEmpty()) {
			outputBits[0] = getInput(TERMINAL_1_IN);
			outputBits[1] = getInput(TERMINAL_2_IN);
			outputBits[2] = getInput(TERMINAL_3_IN);
			outputBits[3] = getInput(TERMINAL_4_IN);
		} else {
			outputBits = this.bits;
		}

		firePropertyChange(P_VALUE, null, null);

		setOutput(TERMINAL_1_OUT, outputBits[0]);
		setOutput(TERMINAL_2_OUT, outputBits[1]);
		setOutput(TERMINAL_3_OUT, outputBits[2]);
		setOutput(TERMINAL_4_OUT, outputBits[3]);
	}

}
