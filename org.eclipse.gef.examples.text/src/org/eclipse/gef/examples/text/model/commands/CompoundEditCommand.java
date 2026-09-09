/*******************************************************************************
 * Copyright (c) 2004, 2023 IBM Corporation and others.
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
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import org.eclipse.gef.examples.text.AppendableCommand;
import org.eclipse.gef.examples.text.GraphicalTextViewer;
import org.eclipse.gef.examples.text.SelectionModel;
import org.eclipse.gef.examples.text.SelectionRange;
import org.eclipse.gef.examples.text.edit.TextEditPart;
import org.eclipse.gef.examples.text.model.ModelLocation;

/**
 * @since 3.1
 */
public class CompoundEditCommand extends ExampleTextCommand implements AppendableCommand {

	private ModelLocation beginLocation;

	private final List<MiniEdit> edits = new ArrayList<>();

	private ModelLocation endLocation;

	private List<MiniEdit> pending;

	/**
	 * @since 3.1
	 * @param label
	 */
	public CompoundEditCommand(String label) {
		super(label);
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		executePending();
	}

	@Override
	public boolean canExecute() {
		return canExecutePending();
	}

	@Override
	public boolean canExecutePending() {
		if (pending == null || pending.isEmpty()) {
			return false;
		}

		return pending.stream().noneMatch(edit -> (edit == null || !edit.canApply()));
	}

	@Override
	public void executePending() {
		Assert.isNotNull(pending);
		pending.forEach(MiniEdit::apply);
		edits.addAll(pending);
		pending = null;
	}

	@Override
	public void flushPending() {
		pending = null;
	}

	@Override
	public SelectionModel getExecuteSelectionModel(GraphicalTextViewer viewer) {
		ModelLocation loc = edits.get(edits.size() - 1).getResultingLocation();
		if (loc == null) {
			return getUndoSelectionModel(viewer);
		}
		SelectionRange range = new SelectionRange(lookupModel(viewer, loc.model), loc.offset);
		return new SelectionModel(range, Collections.emptyList());
	}

	@Override
	public SelectionModel getRedoSelectionModel(GraphicalTextViewer viewer) {
		return getExecuteSelectionModel(viewer);
	}

	@Override
	public SelectionModel getUndoSelectionModel(GraphicalTextViewer viewer) {
		TextEditPart begin = lookupModel(viewer, beginLocation.model);
		if (endLocation == null) {
			SelectionRange range = new SelectionRange(begin, beginLocation.offset);
			return new SelectionModel(range, Collections.emptyList());
		}
		TextEditPart end = lookupModel(viewer, endLocation.model);
		SelectionRange range = new SelectionRange(begin, beginLocation.offset, end, endLocation.offset);
		return new SelectionModel(range, Collections.emptyList());
	}

	public void pendEdit(MiniEdit edit) {
		if (pending == null) {
			pending = new ArrayList<>(2);
		}
		pending.add(edit);
	}

	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		edits.forEach(MiniEdit::reapply);
	}

	public void setEndLocation(ModelLocation endLocation) {
		this.endLocation = endLocation;
	}

	public void setBeginLocation(ModelLocation startLocation) {
		this.beginLocation = startLocation;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		for (int i = edits.size() - 1; i >= 0; i--) {
			MiniEdit edit = edits.get(i);
			edit.rollback();
		}
	}

}
