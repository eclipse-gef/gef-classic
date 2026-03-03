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

import org.eclipse.gef.examples.logicdesigner.LogicMessages;
import org.eclipse.gef.examples.logicdesigner.model.LogicDiagram;
import org.eclipse.gef.examples.logicdesigner.model.LogicSubpart;

public class ReorderPartCommand extends Command {

	private int oldIndex;
	private final int newIndex;
	private final LogicSubpart child;
	private final LogicDiagram parent;

	public ReorderPartCommand(LogicSubpart child, LogicDiagram parent, int newIndex) {
		super(LogicMessages.ReorderPartCommand_Label);
		this.child = child;
		this.parent = parent;
		this.newIndex = newIndex;
	}

	@Override
	public void execute() {
		oldIndex = parent.getChildren().indexOf(child);
		parent.removeChild(child);
		parent.addChild(child, newIndex);
	}

	@Override
	public void undo() {
		parent.removeChild(child);
		parent.addChild(child, oldIndex);
	}

}
