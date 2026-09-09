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

import org.eclipse.gef.examples.text.GraphicalTextViewer;
import org.eclipse.gef.examples.text.SelectionModel;
import org.eclipse.gef.examples.text.model.ModelElement;
import org.eclipse.gef.examples.text.model.ModelLocation;
import org.eclipse.gef.examples.text.model.TextRun;

/**
 * @since 3.1
 */
public class ProcessMacroCommand extends CompoundEditCommand {

	/**
	 * @since 3.1
	 */
	public ProcessMacroCommand(TextRun run, int begin, int end, ModelElement substitution, ModelLocation loc) {
		super("$$conversion"); //$NON-NLS-1$
		RemoveRange removal = new RemoveRange(run, begin, run, end);
		pendEdit(removal);
		SubdivideElement subdivide = new SubdivideElement(run, begin);
		pendEdit(subdivide);
		InsertModelElement insert = new InsertModelElement(run.getContainer(),
				run.getContainer().getChildren().indexOf(run) + 1, substitution, loc);
		pendEdit(insert);
	}

	/**
	 * @see org.eclipse.gef.examples.text.TextCommand#getRedoSelectionModel(org.eclipse.gef.examples.text.GraphicalTextViewer)
	 */
	@Override
	public SelectionModel getRedoSelectionModel(GraphicalTextViewer viewer) {
		return null;
	}

	/**
	 * @see org.eclipse.gef.examples.text.TextCommand#getExecuteSelectionModel(org.eclipse.gef.examples.text.GraphicalTextViewer)
	 */
	@Override
	public SelectionModel getExecuteSelectionModel(GraphicalTextViewer viewer) {
		return super.getExecuteSelectionModel(viewer);
	}

	/**
	 * @see org.eclipse.gef.examples.text.TextCommand#getUndoSelectionModel(org.eclipse.gef.examples.text.GraphicalTextViewer)
	 */
	@Override
	public SelectionModel getUndoSelectionModel(GraphicalTextViewer viewer) {
		return null;
	}

}
