/*******************************************************************************
 * Copyright (c) 2003, 2022 IBM Corporation and others.
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
import org.eclipse.gef.examples.logicdesigner.model.LogicGuide;
import org.eclipse.gef.examples.logicdesigner.model.LogicRuler;

/**
 * @author Pratik Shah
 */
public class CreateGuideCommand extends Command {

	private LogicGuide guide;
	private final LogicRuler parent;
	private final int position;

	public CreateGuideCommand(LogicRuler parent, int position) {
		super(LogicMessages.CreateGuideCommand_Label);
		this.parent = parent;
		this.position = position;
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void execute() {
		if (guide == null) {
			guide = new LogicGuide(!parent.isHorizontal());
		}
		guide.setPosition(position);
		parent.addGuide(guide);
	}

	@Override
	public void undo() {
		parent.removeGuide(guide);
	}

}
