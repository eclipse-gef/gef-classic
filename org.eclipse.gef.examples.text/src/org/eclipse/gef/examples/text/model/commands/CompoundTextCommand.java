/*******************************************************************************
 * Copyright (c) 2005, 2026 IBM Corporation and others.
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
package org.eclipse.gef.examples.text.model.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;

import org.eclipse.gef.examples.text.AppendableCommand;
import org.eclipse.gef.examples.text.GraphicalTextViewer;
import org.eclipse.gef.examples.text.SelectionModel;
import org.eclipse.gef.examples.text.TextCommand;

public class CompoundTextCommand extends Command implements TextCommand, AppendableCommand {

	private final List<Command> pending = new ArrayList<>();
	private final List<Command> applied = new ArrayList<>();

	public CompoundTextCommand() {
	}

	public CompoundTextCommand(String label) {
		super(label);
	}

	public void add(TextCommand command) {
		if (command instanceof Command cmd) {
			pending.add(cmd);
		}
	}

	@Override
	public boolean canExecute() {
		return canExecutePending();
	}

	@Override
	public boolean canExecutePending() {
		if (pending.isEmpty()) {
			return false;
		}
		for (Command cmd : pending) {
			if ((cmd == null) || !cmd.canExecute()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void dispose() {
		for (Command element : applied) {
			element.dispose();
		}
		flushPending();
	}

	@Override
	public void execute() {
		executePending();
	}

	@Override
	public void executePending() {
		for (Command cmd : pending) {
			cmd.execute();
			applied.add(cmd);
		}
		flushPending();
	}

	@Override
	public void flushPending() {
		pending.clear();
	}

	@Override
	public SelectionModel getExecuteSelectionModel(GraphicalTextViewer viewer) {
		if (applied.isEmpty()) {
			return null;
		}
		return getAppliedTextCommand(applied.size() - 1).getExecuteSelectionModel(viewer);
	}

	private TextCommand getAppliedTextCommand(int i) {
		return (TextCommand) applied.get(i);
	}

	@Override
	public SelectionModel getRedoSelectionModel(GraphicalTextViewer viewer) {
		if (applied.isEmpty()) {
			return null;
		}
		return getAppliedTextCommand(applied.size() - 1).getExecuteSelectionModel(viewer);
	}

	@Override
	public SelectionModel getUndoSelectionModel(GraphicalTextViewer viewer) {
		if (applied.isEmpty()) {
			return null;
		}
		return getAppliedTextCommand(0).getUndoSelectionModel(viewer);
	}

	@Override
	public void redo() {
		for (Command element : applied) {
			element.redo();
		}
	}

	@Override
	public void undo() {
		for (int i = applied.size() - 1; i >= 0; i--) {
			applied.get(i).undo();
		}
	}

}