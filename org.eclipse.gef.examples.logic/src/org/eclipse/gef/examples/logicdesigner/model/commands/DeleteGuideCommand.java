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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.commands.Command;

import org.eclipse.gef.examples.logicdesigner.LogicMessages;
import org.eclipse.gef.examples.logicdesigner.model.LogicGuide;
import org.eclipse.gef.examples.logicdesigner.model.LogicRuler;
import org.eclipse.gef.examples.logicdesigner.model.LogicSubpart;

/**
 * @author Pratik Shah
 */
public class DeleteGuideCommand extends Command {

	private final LogicRuler parent;
	private final LogicGuide guide;
	private Map<LogicSubpart, Integer> oldParts;

	public DeleteGuideCommand(LogicGuide guide, LogicRuler parent) {
		super(LogicMessages.DeleteGuideCommand_Label);
		this.guide = guide;
		this.parent = parent;
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void execute() {
		oldParts = new HashMap<>(guide.getMap());
		for (LogicSubpart subPart : oldParts.keySet()) {
			guide.detachPart(subPart);
		}
		parent.removeGuide(guide);
	}

	@Override
	public void undo() {
		parent.addGuide(guide);
		for (LogicSubpart subPart : oldParts.keySet()) {
			guide.attachPart(subPart, oldParts.get(subPart).intValue());
		}
	}
}
