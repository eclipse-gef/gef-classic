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

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import org.eclipse.gef.examples.logicdesigner.LogicMessages;

public class LogicFlowContainer extends LogicDiagram {

	private static class LayoutLabelProvider extends org.eclipse.jface.viewers.LabelProvider {

		@Override
		public String getText(Object element) {
			if (element instanceof Integer integer) {
				if (LAYOUT_SINGLE_ROW.intValue() == integer.intValue()) {
					return LogicMessages.PropertyDescriptor_LogicFlowContainer_SingleColumnLayout;
				}
				if (LAYOUT_MULTI_ROW.intValue() == integer.intValue()) {
					return LogicMessages.PropertyDescriptor_LogicFlowContainer_MultiRowLayout;
				}
			}
			return super.getText(element);
		}
	}

	public static final String ID_LAYOUT = "layout"; //$NON-NLS-1$

	public static final Integer LAYOUT_MULTI_ROW = Integer.valueOf(0);
	public static final Integer LAYOUT_SINGLE_ROW = Integer.valueOf(1);

	static final long serialVersionUID = 1;

	protected Integer layout = LAYOUT_MULTI_ROW;

	public Integer getLayout() {
		if (layout == null) {
			return LAYOUT_MULTI_ROW;
		}
		return layout;
	}

	/**
	 * Returns <code>null</code> for this model. Returns normal descriptors for all
	 * subclasses.
	 *
	 * @return Array of property descriptors.
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (getClass().equals(LogicFlowContainer.class)) {
			ComboBoxPropertyDescriptor cbd = new ComboBoxPropertyDescriptor(ID_LAYOUT,
					LogicMessages.PropertyDescriptor_LogicFlowContainer_Layout,
					new String[] { LogicMessages.PropertyDescriptor_LogicFlowContainer_MultiRowLayout,
							LogicMessages.PropertyDescriptor_LogicFlowContainer_SingleColumnLayout });
			cbd.setLabelProvider(new LayoutLabelProvider());
			return new IPropertyDescriptor[] { cbd, descriptors[0], descriptors[1] };
		}
		return super.getPropertyDescriptors();
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (propName.equals(ID_LAYOUT)) {
			return layout;
		}
		return super.getPropertyValue(propName);
	}

	public void setLayout(Integer newLayout) {
		Integer oldLayout = layout;
		layout = newLayout;
		firePropertyChange(ID_LAYOUT, oldLayout, layout);
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (ID_LAYOUT.equals(id)) {
			setLayout((Integer) value);
		} else {
			super.setPropertyValue(id, value);
		}
	}

	@Override
	public String toString() {
		return LogicMessages.LogicPlugin_Tool_CreationTool_FlowContainer_Label;
	}

}
