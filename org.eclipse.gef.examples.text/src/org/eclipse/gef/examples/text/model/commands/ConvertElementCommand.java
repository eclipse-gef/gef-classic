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
import org.eclipse.gef.examples.text.model.Container;
import org.eclipse.gef.examples.text.model.ModelElement;
import org.eclipse.gef.examples.text.model.ModelLocation;
import org.eclipse.gef.examples.text.model.TextRun;

/**
 * @since 3.1
 */
public class ConvertElementCommand extends ExampleTextCommand {

	private final TextRun text;
	private final char[] removed;
	private final int offset;
	private final ModelElement converted;
	private final ModelLocation caret;

	/**
	 * @since 3.1
	 */
	public ConvertElementCommand(TextRun text, int begin, int end, ModelElement converted, ModelLocation caret) {
		super("bogus"); //$NON-NLS-1$
		this.text = text;
		this.offset = begin;
		this.converted = converted;
		this.caret = caret;
		removed = text.getText().substring(begin, end).toCharArray();
	}

	@Override
	public void execute() {
		text.removeRange(offset, removed.length);
		Container container = text.getContainer();
		container.remove(text);
		container.add(converted);
	}

	@Override
	public SelectionModel getRedoSelectionModel(GraphicalTextViewer viewer) {
		return null;
	}

	@Override
	public SelectionModel getExecuteSelectionModel(GraphicalTextViewer viewer) {
		SelectionRange range = new SelectionRange(lookupModel(viewer, caret.model), caret.offset);
		return new SelectionModel(range, Collections.emptyList());
	}

	@Override
	public SelectionModel getUndoSelectionModel(GraphicalTextViewer viewer) {
		return null;
	}

}
