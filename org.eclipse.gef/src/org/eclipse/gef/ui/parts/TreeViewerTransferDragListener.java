/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
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
package org.eclipse.gef.ui.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;

import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.AbstractTransferDragSourceListener;

class TreeViewerTransferDragListener extends AbstractTransferDragSourceListener {

	private List modelSelection;

	public TreeViewerTransferDragListener(EditPartViewer viewer) {
		super(viewer, TreeViewerTransfer.getInstance());
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public TreeViewerTransferDragListener(EditPartViewer viewer, Transfer xfer) {
		super(viewer, xfer);
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		event.data = getViewer().getSelectedEditParts();
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		TreeViewerTransfer.getInstance().setViewer(getViewer());
		List selection = getViewer().getSelectedEditParts();
		TreeViewerTransfer.getInstance().setObject(selection);
		saveModelSelection(selection);
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		TreeViewerTransfer.getInstance().setObject(null);
		TreeViewerTransfer.getInstance().setViewer(null);
		if (event.doit) {
			revertModelSelection();
		} else {
			modelSelection = null;
		}
	}

	protected void revertModelSelection() {
		List list = new ArrayList();
		Object editpart;
		for (int i = 0; i < modelSelection.size(); i++) {
			editpart = getViewer().getEditPartForModel(modelSelection.get(i));
			if (editpart != null) {
				list.add(editpart);
			}
		}
		getViewer().setSelection(new StructuredSelection(list));
		modelSelection = null;
	}

	protected void saveModelSelection(List editPartSelection) {
		modelSelection = new ArrayList();
		for (int i = 0; i < editPartSelection.size(); i++) {
			EditPart editpart = (EditPart) editPartSelection.get(i);
			modelSelection.add(editpart.getModel());
		}
	}

}
