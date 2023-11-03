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
package org.eclipse.gef.examples.logicdesigner.model.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.examples.logicdesigner.LogicMessages;
import org.eclipse.gef.examples.logicdesigner.model.LogicSubpart;

public class SetConstraintCommand extends org.eclipse.gef.commands.Command {

	private Point newPos;
	private Dimension newSize;
	private Point oldPos;
	private Dimension oldSize;
	private LogicSubpart part;

	@Override
	public void execute() {
		oldSize = part.getSize();
		oldPos = part.getLocation();
		redo();
	}

	@Override
	public String getLabel() {
		if (oldSize.equals(newSize))
			return LogicMessages.SetLocationCommand_Label_Location;
		return LogicMessages.SetLocationCommand_Label_Resize;
	}

	@Override
	public void redo() {
		part.setSize(newSize);
		part.setLocation(newPos);
	}

	public void setLocation(Rectangle r) {
		setLocation(r.getLocation());
		setSize(r.getSize());
	}

	public void setLocation(Point p) {
		newPos = p;
	}

	public void setPart(LogicSubpart part) {
		this.part = part;
	}

	public void setSize(Dimension p) {
		newSize = p;
	}

	@Override
	public void undo() {
		part.setSize(oldSize);
		part.setLocation(oldPos);
	}

}
