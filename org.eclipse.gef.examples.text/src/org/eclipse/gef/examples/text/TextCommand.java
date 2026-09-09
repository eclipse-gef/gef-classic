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

package org.eclipse.gef.examples.text;

/**
 * @since 3.1
 */
public interface TextCommand {

	/**
	 * Returns the viewer's selection model for the state after redo.
	 *
	 * @since 3.1
	 * @param viewer the viewer
	 * @return the model
	 */
	SelectionModel getRedoSelectionModel(GraphicalTextViewer viewer);

	/**
	 * Returns the viewer's selection model for the state after execution.
	 *
	 * @param viewer the viewer
	 * @return the model
	 */
	SelectionModel getExecuteSelectionModel(GraphicalTextViewer viewer);

	/**
	 * Returns the viewer's selection model for the state after undo.
	 *
	 * @since 3.1
	 * @param viewer the viewer
	 * @return the model
	 */
	SelectionModel getUndoSelectionModel(GraphicalTextViewer viewer);

}
