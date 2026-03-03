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

import org.eclipse.gef.examples.text.GraphicalTextViewer;
import org.eclipse.gef.examples.text.SelectionRange;
import org.eclipse.gef.examples.text.edit.TextEditPart;
import org.eclipse.gef.examples.text.model.Container;
import org.eclipse.gef.examples.text.model.TextRun;

/**
 * Re-parents an element to its grandparent. Removes the parent if it becomes
 * empty
 *
 * @since 3.1
 */
public class PromoteElementCommand extends ExampleTextCommand {

	private final TextRun run;
	private final Container oldParent;
	int index;
	private final int caretOffset;

	public PromoteElementCommand(TextEditPart part, int caretOffset) {
		super("decrease indentation"); //$NON-NLS-1$
		this.caretOffset = caretOffset;
		run = (TextRun) part.getModel();
		oldParent = run.getContainer();
	}

	@Override
	public void execute() {
		index = oldParent.getChildren().indexOf(run);
		Container newParent = oldParent.getContainer();
		int where = newParent.getChildren().indexOf(oldParent) + 1;
		oldParent.remove(run);
		run.setType(newParent.getChildType());
		newParent.add(run, where);
		if (oldParent.getChildren().isEmpty()) {
			oldParent.getContainer().remove(oldParent);
		}
	}

	@Override
	public boolean canExecute() {
		// Is there a container into which the run can be promoted.
		return oldParent.getContainer() != null;
	}

	@Override
	public SelectionRange getExecuteSelectionRange(GraphicalTextViewer viewer) {
		return new SelectionRange(lookupModel(viewer, run), caretOffset);
	}

	@Override
	public SelectionRange getRedoSelectionRange(GraphicalTextViewer viewer) {
		return getExecuteSelectionRange(viewer);
	}

	@Override
	public SelectionRange getUndoSelectionRange(GraphicalTextViewer viewer) {
		return getExecuteSelectionRange(viewer);
	}

}
