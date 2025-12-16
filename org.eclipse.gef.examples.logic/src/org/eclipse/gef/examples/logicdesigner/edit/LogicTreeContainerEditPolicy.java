/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
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
package org.eclipse.gef.examples.logicdesigner.edit;

import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.TreeContainerEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import org.eclipse.gef.examples.logicdesigner.model.LogicDiagram;
import org.eclipse.gef.examples.logicdesigner.model.LogicSubpart;
import org.eclipse.gef.examples.logicdesigner.model.commands.CreateCommand;
import org.eclipse.gef.examples.logicdesigner.model.commands.ReorderPartCommand;

public class LogicTreeContainerEditPolicy extends TreeContainerEditPolicy {

	protected Command createCreateCommand(LogicSubpart child, Rectangle r, int index, String label) {
		CreateCommand cmd = new CreateCommand();
		Rectangle rect;
		if (r == null) {
			rect = new Rectangle();
			rect.setSize(new Dimension(-1, -1));
		} else {
			rect = r;
		}
		cmd.setLocation(rect);
		cmd.setParent((LogicDiagram) getHost().getModel());
		cmd.setChild(child);
		cmd.setLabel(label);
		if (index >= 0) {
			cmd.setIndex(index);
		}
		return cmd;
	}

	@Override
	protected Command getAddCommand(ChangeBoundsRequest request) {
		CompoundCommand command = new CompoundCommand();
		command.setDebugLabel("Add in LogicTreeContainerEditPolicy");//$NON-NLS-1$
		int index = findIndexOfTreeItemAt(request.getLocation());

		for (EditPart child : request.getEditParts()) {
			LogicSubpart childModel = (LogicSubpart) child.getModel();
			command.add(createCreateCommand(childModel, new Rectangle(new Point(), childModel.getSize()), index,
					"Reparent LogicSubpart"));//$NON-NLS-1$
		}
		return command;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		LogicSubpart child = (LogicSubpart) request.getNewObject();
		int index = findIndexOfTreeItemAt(request.getLocation());
		return createCreateCommand(child, null, index, "Create LogicSubpart");//$NON-NLS-1$
	}

	@Override
	protected Command getMoveChildrenCommand(ChangeBoundsRequest request) {
		CompoundCommand command = new CompoundCommand();
		List<? extends EditPart> children = getHost().getChildren();
		int newIndex = findIndexOfTreeItemAt(request.getLocation());

		for (EditPart child : request.getEditParts()) {
			int tempIndex = newIndex;
			int oldIndex = children.indexOf(child);
			if (oldIndex == tempIndex || oldIndex + 1 == tempIndex) {
				command.add(UnexecutableCommand.INSTANCE);
				return command;
			}
			if (oldIndex <= tempIndex) {
				tempIndex--;
			}
			command.add(new ReorderPartCommand((LogicSubpart) child.getModel(), (LogicDiagram) getHost().getModel(),
					tempIndex));
		}
		return command;
	}

}
