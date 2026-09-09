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

import java.util.Collections;

import org.eclipse.gef.examples.text.GraphicalTextViewer;
import org.eclipse.gef.examples.text.SelectionModel;
import org.eclipse.gef.examples.text.SelectionRange;
import org.eclipse.gef.examples.text.TextLocation;
import org.eclipse.gef.examples.text.model.ModelLocation;

/**
 * @since 3.1
 */
public class SingleEditCommand extends ExampleTextCommand {

	private final MiniEdit edit;
	private final ModelLocation end;
	private final ModelLocation start;

	/**
	 * @since 3.1
	 */
	public SingleEditCommand(MiniEdit edit, ModelLocation start, ModelLocation end) {
		super(""); //$NON-NLS-1$
		this.edit = edit;
		this.start = start;
		this.end = end;
	}

	@Override
	public void execute() {
		edit.apply();
	}

	@Override
	public SelectionModel getExecuteSelectionModel(GraphicalTextViewer viewer) {
		ModelLocation loc = edit.getResultingLocation();
		if (loc != null) {
			SelectionRange range = new SelectionRange(lookupModel(viewer, loc.model), loc.offset);
			return new SelectionModel(range, Collections.emptyList());
		}
		return getUndoSelectionModel(viewer);
	}

	@Override
	public SelectionModel getRedoSelectionModel(GraphicalTextViewer viewer) {
		return getExecuteSelectionModel(viewer);
	}

	@Override
	public SelectionModel getUndoSelectionModel(GraphicalTextViewer viewer) {
		TextLocation startLoc = new TextLocation(lookupModel(viewer, start.model), start.offset);
		TextLocation endLoc = new TextLocation(lookupModel(viewer, end.model), end.offset);
		SelectionRange range = new SelectionRange(startLoc, endLoc);
		return new SelectionModel(range, Collections.emptyList());
	}

	@Override
	public void redo() {
		edit.reapply();
	}

	@Override
	public void undo() {
		edit.rollback();
	}

}