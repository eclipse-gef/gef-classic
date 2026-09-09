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

package org.eclipse.gef.examples.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Caret;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;

/**
 * @since 3.1
 */
public class GraphicalTextViewer extends ScrollingGraphicalViewer {

	private SelectionModel selectionModel;

	/**
	 * Returns the viewers selection range by <em>reference</em>. The range should
	 * not be modified directly.
	 *
	 * @since 3.1
	 * @return the current selection by reference
	 * @deprecated in 3.2. @TODO:Pratik remove this method and all references to it.
	 *             Use getSelectionModel() instead.
	 */
	@Deprecated
	public SelectionRange getSelectionRange() {
		if (selectionModel != null) {
			return selectionModel.getSelectionRange();
		}
		return null;
	}

	public void revealCaret() {
		Assert.isNotNull(getControl(), "The control has not been created yet."); //$NON-NLS-1$
		Caret caret = getFigureCanvas().getCaret();
		if (caret == null || !caret.isVisible()) {
			return;
		}
		// @TODO:Pratik you should expose the text location first (it might not
		// be visible)
		Viewport port = getFigureCanvas().getViewport();
		Rectangle view = new Rectangle(port.getViewLocation(), port.getClientArea().getSize());
		Rectangle exposeRegion = new Rectangle(caret.getBounds());
		port.getContents().translateToRelative(exposeRegion);
		if (!view.contains(exposeRegion)) {
			int x = view.x, y = view.y;
			if (exposeRegion.x < view.x) {
				x = exposeRegion.x;
			} else if (exposeRegion.right() > view.right()) {
				x = view.x + exposeRegion.right() - view.right();
			}
			if (exposeRegion.y < view.y) {
				y = exposeRegion.y;
			} else if (exposeRegion.bottom() > view.bottom()) {
				y = view.y + exposeRegion.bottom() - view.bottom();
			}
			getFigureCanvas().scrollTo(x, y);
		}
	}

	/**
	 * Sets the selection range to the given value. Updates any editparts which had
	 * or will have textual selection. Fires selection changed. Place the caret in
	 * the appropriate location.
	 *
	 * @since 3.1
	 * @param newRange the new selection range
	 * @deprecated in 3.2. @TODO:Pratik remove this method and all references to it.
	 *             Use setSelectionModel() instead.
	 */
	@Deprecated
	public void setSelectionRange(SelectionRange newRange) {
		// @TODO:Pratik change all these setSelection() methods so that they
		// don't affect
		// any other selection. So, setting selectionRange to null would not
		// clear the
		// selected editparts.
		SelectionModel newModel = null;
		if (newRange != null) {
			newModel = createSelectionModel(null, newRange,
					selectionModel == null ? Collections.emptyList() : selectionModel.getSelectedEditParts());
		}
		setSelectionModel(newModel);
	}

	public SelectionModel getSelectionModel() {
		return selectionModel;
	}

	public void setSelectionModel(SelectionModel selection) {
		if (selection != null) {
			// setFocus(selection.getFocusPart());
			selection.applySelection(selectionModel);
		} else if (selectionModel != null) {
			setFocus(null);
			selectionModel.deselect();
		}

		selectionModel = selection;

		fireSelectionChanged();
	}

	@Override
	public void appendSelection(EditPart editpart) {
		if (getFocusEditPart() != editpart) {
			setFocus(null);
		}
		if (selectionModel != null) {
			setSelectionModel(selectionModel.getAppendedSelection(editpart));
		} else {
			select(editpart);
		}
	}

	@Override
	public void deselect(EditPart editpart) {
		if (selectionModel != null) {
			setSelectionModel(selectionModel.getExcludedSelection(editpart));
		}
	}

	@Override
	public void deselectAll() {
		setSelectionModel(null);
	}

	@Override
	public void select(EditPart editpart) {
		if (getFocusEditPart() != editpart) {
			setFocus(null);
		}
		ArrayList<EditPart> list = new ArrayList<>();
		list.add(editpart);
		setSelectionModel(createSelectionModel(null, null, list));
	}

	// @TODO:Pratik Hack. This shouldn't be here. CommandStack should be doing
	// this automatically.
	// You can make that change once you remove the GraphicalTextViewer class.
	@Override
	public void setEditDomain(EditDomain domain) {
		super.setEditDomain(domain);
		getEditDomain().getCommandStack().addCommandStackEventListener(event -> {
			if (!(event.getCommand() instanceof TextCommand) || getSelectionRange() == null) {
				return;
			}
			TextCommand command = (TextCommand) event.getCommand();
			if (command != null) {
				if (event.getDetail() == CommandStack.POST_EXECUTE) {
					setSelectionModel(command.getExecuteSelectionModel(GraphicalTextViewer.this));
				} else if (event.getDetail() == CommandStack.POST_REDO) {
					setSelectionModel(command.getRedoSelectionModel(GraphicalTextViewer.this));
				} else if (event.getDetail() == CommandStack.POST_UNDO) {
					setSelectionModel(command.getUndoSelectionModel(GraphicalTextViewer.this));
				}
			}
		});
	}

	@Override
	public void setSelection(ISelection newSelection) {
		if (newSelection != null) {
			setSelectionModel(createSelectionModel(newSelection, null, Collections.emptyList()));
		} else {
			setSelectionModel(null);
		}
	}

	@Override
	public ISelection getSelection() {
		if (selectionModel != null) {
			return selectionModel.getSelection();
		}
		return new StructuredSelection(getContents());
	}

	private static SelectionModel createSelectionModel(ISelection selection, SelectionRange range, List<EditPart> parts) {
		if (selection instanceof IStructuredSelection structuredSelection) {
			return new SelectionModel(structuredSelection);
		}
		return new SelectionModel(range, parts);
	}

	/**
	 * @see org.eclipse.gef.ui.parts.AbstractEditPartViewer#getSelectedEditParts()
	 */
	@Override
	public List<EditPart> getSelectedEditParts() {
		return primGetSelectedEditParts();
	}

	/**
	 * The method returns the same list as getSelectedEditParts(). The list is
	 * unmodifiable.
	 *
	 * @deprecated
	 * @see org.eclipse.gef.ui.parts.AbstractEditPartViewer#primGetSelectedEditParts()
	 */
	@Deprecated
	@Override
	protected List<EditPart> primGetSelectedEditParts() {
		if (selectionModel != null) {
			return selectionModel.getSelectedEditParts();
		}
		return Collections.emptyList();
	}

	public boolean isTextSelected() {
		return selectionModel != null && selectionModel.isTextSelected();
	}

}