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
package org.eclipse.gef.examples.flow.model.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;

import org.eclipse.gef.examples.flow.model.Activity;
import org.eclipse.gef.examples.flow.model.StructuredActivity;
import org.eclipse.gef.examples.flow.model.Transition;

/**
 * Handles the deletion of Activities.
 *
 * @author Daniel Lee
 */
public class DeleteCommand extends Command {

	private Activity child;
	private StructuredActivity parent;
	private int index = -1;
	private final List<Transition> sourceConnections = new ArrayList<>();
	private final List<Transition> targetConnections = new ArrayList<>();

	private void deleteConnections(Activity a) {
		if (a instanceof StructuredActivity structAct) {
			structAct.getChildren().forEach(this::deleteConnections);
		}
		sourceConnections.addAll(a.getIncomingTransitions());
		sourceConnections.forEach(t -> {
			t.source.removeOutput(t);
			a.removeInput(t);
		});

		targetConnections.addAll(a.getOutgoingTransitions());
		targetConnections.forEach(t -> {
			t.target.removeInput(t);
			a.removeOutput(t);
		});
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		primExecute();
	}

	/**
	 * Invokes the execution of this command.
	 */
	protected void primExecute() {
		deleteConnections(child);
		index = parent.getChildren().indexOf(child);
		parent.removeChild(child);
	}

	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		primExecute();
	}

	private void restoreConnections() {
		sourceConnections.forEach(t -> {
			t.target.addInput(t);
			t.source.addOutput(t);
		});
		sourceConnections.clear();
		targetConnections.forEach(t -> {
			t.source.addOutput(t);
			t.target.addInput(t);
		});
		targetConnections.clear();
	}

	/**
	 * Sets the child to the passed Activity
	 *
	 * @param a the child
	 */
	public void setChild(Activity a) {
		child = a;
	}

	/**
	 * Sets the parent to the passed StructuredActivity
	 *
	 * @param sa the parent
	 */
	public void setParent(StructuredActivity sa) {
		parent = sa;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		parent.addChild(child, index);
		restoreConnections();
	}

}
