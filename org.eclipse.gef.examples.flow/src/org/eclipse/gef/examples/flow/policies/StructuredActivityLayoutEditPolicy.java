/*******************************************************************************
 * Copyright (c) 2000, 2023 IBM Corporation and others.
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
package org.eclipse.gef.examples.flow.policies;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import org.eclipse.gef.examples.flow.model.Activity;
import org.eclipse.gef.examples.flow.model.StructuredActivity;
import org.eclipse.gef.examples.flow.model.commands.AddCommand;
import org.eclipse.gef.examples.flow.model.commands.CreateCommand;
import org.eclipse.gef.examples.flow.parts.SimpleActivityPart;

/**
 * @author Daniel Lee
 */
public class StructuredActivityLayoutEditPolicy extends LayoutEditPolicy {

	protected Command createAddCommand(EditPart child) {
		Activity activity = (Activity) child.getModel();
		AddCommand add = new AddCommand();
		add.setParent((StructuredActivity) getHost().getModel());
		add.setChild(activity);
		return add;
	}

	/**
	 * @see org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy#createChildEditPolicy(org.eclipse.gef.EditPart)
	 */
	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		if (child instanceof SimpleActivityPart) {
			return new SimpleActivitySelectionEditPolicy();
		}
		return new NonResizableEditPolicy();
	}

	@Override
	protected Command getAddCommand(Request req) {
		ChangeBoundsRequest request = (ChangeBoundsRequest) req;
		CompoundCommand command = new CompoundCommand();
		request.getEditParts().forEach(child -> command.add(createAddCommand(child)));
		return command.unwrap();
	}

	/**
	 * @see LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		CreateCommand command = new CreateCommand();
		command.setParent((StructuredActivity) getHost().getModel());
		command.setChild((Activity) request.getNewObject());
		return command;
	}

	/**
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getMoveChildrenCommand(org.eclipse.gef.Request)
	 */
	@Override
	protected Command getMoveChildrenCommand(Request request) {
		return null;
	}

}
