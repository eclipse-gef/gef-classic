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

import org.eclipse.swt.graphics.Image;

import org.eclipse.draw2d.geometry.Dimension;

import org.eclipse.gef.examples.logicdesigner.LogicMessages;

public class LogicLabel extends LogicSubpart {

	private static final int DEFAULT_WIDTH = 100;

	static final long serialVersionUID = 1;

	private String text = LogicMessages.LogicPlugin_Tool_CreationTool_LogicLabel;

	private static final Image LOGIC_LABEL_ICON = createImage(LED.class, "icons/label16.gif"); //$NON-NLS-1$

	private static int count;

	public LogicLabel() {
		size.width = DEFAULT_WIDTH;
	}

	public String getLabelContents() {
		return text;
	}

	@Override
	public Image getIconImage() {
		return LOGIC_LABEL_ICON;
	}

	@Override
	protected String getNewID() {
		return Integer.toString(count++);
	}

	@Override
	public Dimension getSize() {
		return new Dimension(size.width, -1);
	}

	@Override
	public void setSize(Dimension d) {
		d.height = -1;
		super.setSize(d);
	}

	public void setLabelContents(String s) {
		text = s;
		firePropertyChange("labelContents", null, text); // $NON-NLS-2$//$NON-NLS-1$
	}

	@Override
	public String toString() {
		return LogicMessages.LogicPlugin_Tool_CreationTool_LogicLabel + " #" + getID() + " " //$NON-NLS-1$ //$NON-NLS-2$
				+ LogicMessages.PropertyDescriptor_Label_Text + "=" + getLabelContents(); //$NON-NLS-1$
	}

}
