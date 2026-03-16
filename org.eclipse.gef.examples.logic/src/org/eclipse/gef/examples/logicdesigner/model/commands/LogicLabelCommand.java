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

import org.eclipse.gef.commands.Command;

import org.eclipse.gef.examples.logicdesigner.model.LogicLabel;

public class LogicLabelCommand extends Command {

	private String newName, oldName;
	private final LogicLabel label;

	public LogicLabelCommand(LogicLabel l, String s) {
		label = l;
		if (s != null) {
			newName = s;
		} else {
			newName = ""; //$NON-NLS-1$
		}
	}

	@Override
	public void execute() {
		oldName = label.getLabelContents();
		label.setLabelContents(newName);
	}

	@Override
	public void undo() {
		label.setLabelContents(oldName);
	}

}
