/*******************************************************************************
 * Copyright (c) 2004, 2026 IBM Corporation and others.
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
import org.eclipse.gef.examples.text.edit.TextEditPart;
import org.eclipse.gef.examples.text.model.Container;
import org.eclipse.gef.examples.text.model.TextRun;

/**
 * @since 3.1
 */
public class NestElementCommand extends ExampleTextCommand {

	private final TextRun run;
	private final int index;
	private final int caretOffset;

	public NestElementCommand(TextEditPart part, int caretOffset) {
		super("indent"); //$NON-NLS-1$
		this.caretOffset = caretOffset;
		run = (TextRun) part.getModel();
		index = run.getContainer().getChildren().indexOf(run);
	}

	@Override
	public void execute() {
		Container container = run.getContainer();

		Container newParent = (Container) container.getChildren().get(index - 1);
		container.remove(run);
		run.setType(newParent.getChildType());
		newParent.add(run);
	}

	@Override
	public boolean canExecute() {
		if (index == 0) {
			return false;
		}
		return run.getContainer().getChildren().get(index - 1) instanceof Container;
	}

	@Override
	public SelectionModel getExecuteSelectionModel(GraphicalTextViewer viewer) {
		SelectionRange range = new SelectionRange(lookupModel(viewer, run), caretOffset);
		return new SelectionModel(range, Collections.emptyList());
	}

	@Override
	public SelectionModel getRedoSelectionModel(GraphicalTextViewer viewer) {
		return getExecuteSelectionModel(viewer);
	}

	@Override
	public SelectionModel getUndoSelectionModel(GraphicalTextViewer viewer) {
		return getExecuteSelectionModel(viewer);
	}

}
