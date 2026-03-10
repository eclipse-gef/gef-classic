/*******************************************************************************
 * Copyright (c) 2004, 2022 IBM Corporation and others.
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

import org.eclipse.gef.examples.logicdesigner.model.LogicGuide;
import org.eclipse.gef.examples.logicdesigner.model.LogicSubpart;

/**
 * @author Pratik Shah
 */
public class ChangeGuideCommand extends Command {

	private final LogicSubpart part;
	private LogicGuide oldGuide, newGuide;
	private int oldAlign, newAlign;
	private final boolean horizontal;

	public ChangeGuideCommand(LogicSubpart part, boolean horizontalGuide) {
		this.part = part;
		horizontal = horizontalGuide;
	}

	protected void changeGuide(LogicGuide oldGuide, LogicGuide newGuide, int newAlignment) {
		if (oldGuide != null && oldGuide != newGuide) {
			oldGuide.detachPart(part);
		}
		// You need to re-attach the part even if the oldGuide and the newGuide
		// are the same
		// because the alignment could have changed
		if (newGuide != null) {
			newGuide.attachPart(part, newAlignment);
		}
	}

	@Override
	public void execute() {
		// Cache the old values
		oldGuide = horizontal ? part.getHorizontalGuide() : part.getVerticalGuide();
		if (oldGuide != null) {
			oldAlign = oldGuide.getAlignment(part);
		}

		redo();
	}

	@Override
	public void redo() {
		changeGuide(oldGuide, newGuide, newAlign);
	}

	public void setNewGuide(LogicGuide guide, int alignment) {
		newGuide = guide;
		newAlign = alignment;
	}

	@Override
	public void undo() {
		changeGuide(newGuide, oldGuide, oldAlign);
	}

}
