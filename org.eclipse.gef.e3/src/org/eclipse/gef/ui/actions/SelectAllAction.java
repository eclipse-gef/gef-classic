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
package org.eclipse.gef.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.internal.GEFMessages;

/**
 * An action which selects all edit parts in the active workbench part.
 */
public class SelectAllAction extends Action {

	private final IWorkbenchPart part;

	/**
	 * Constructs a <code>SelectAllAction</code> and associates it with the given
	 * part.
	 *
	 * @param part The workbench part associated with this SelectAllAction
	 */
	public SelectAllAction(IWorkbenchPart part) {
		this.part = part;
		setText(GEFMessages.SelectAllAction_Label);
		setToolTipText(GEFMessages.SelectAllAction_Tooltip);
		setId(ActionFactory.SELECT_ALL.getId());
	}

	/**
	 * Selects all edit parts in the active workbench part.
	 */
	@Override
	public void run() {
		GraphicalViewer viewer = part.getAdapter(GraphicalViewer.class);
		if (viewer != null) {
			viewer.setSelection(new StructuredSelection(getSelectableEditParts(viewer)));
		}
	}

	/**
	 * Retrieves edit parts which can be selected
	 *
	 * @param viewer from which the edit parts are to be retrieved
	 * @return list of selectable EditParts
	 * @since 3.5
	 */
	private static List<? extends EditPart> getSelectableEditParts(GraphicalViewer viewer) {
		return viewer.getContents().getChildren().stream().filter(EditPart::isSelectable).toList();
	}

}
