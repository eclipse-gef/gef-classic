/*******************************************************************************
 * Copyright (c) 2005, 2026 IBM Corporation and others.
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.gef.EditPart;

import org.eclipse.gef.examples.text.edit.TextEditPart;

/**
 * SelectionModel is immutable.
 *
 * @author Pratik Shah
 * @since 3.2
 */
public class SelectionModel {

	private final SelectionRange selectionRange;
	private final List<EditPart> constantSelection;

	@SuppressWarnings("unchecked")
	public SelectionModel(IStructuredSelection selection) {
		this(null, selection.toList());
	}

	/**
	 * Constructs a selection model combining an optional text selection range and
	 * an (optional) ordered list of EditParts to be selected. Passing an empty list
	 * leaves the current selection unchanged.
	 *
	 * @param range         the {@link SelectionRange} describing text selection
	 *                      (caret and range). May be {@code null} when there is no
	 *                      text selection.
	 * @param selectedParts a list of {@link EditPart}s to be selected; the last
	 *                      element in the list will be treated as the focus/primary
	 *                      selection. The list must not be {@code null}. If no edit
	 *                      parts should be selected supply an empty list (e.g.
	 *                      {@code Collections.emptyList()}).
	 */
	public SelectionModel(SelectionRange range, List<EditPart> selectedParts) {
		selectionRange = range;
		constantSelection = Collections.unmodifiableList(selectedParts);
	}

	protected void applySelectedParts() {
		if (!constantSelection.isEmpty()) {
			Iterator<EditPart> itr = constantSelection.iterator();
			while (true) {
				EditPart part = itr.next();
				if (!itr.hasNext()) {
					part.setSelected(EditPart.SELECTED_PRIMARY);
					break;
				}
				part.setSelected(EditPart.SELECTED);
			}
		}
	}

	protected void applySelectionRange() {
		SelectionRange range = getSelectionRange();
		if (range != null) {
			List<EditPart> currentSelection = range.getSelectedParts();
			for (EditPart element : currentSelection) {
				TextEditPart textpart = (TextEditPart) element;
				textpart.setSelection(0, textpart.getLength());
			}

			if (range.begin.part == range.end.part) {
				range.begin.part.setSelection(range.begin.offset, range.end.offset);
			} else {
				range.begin.part.setSelection(range.begin.offset, range.begin.part.getLength());
				range.end.part.setSelection(0, range.end.offset);
			}
		}
	}

	public void deselect() {
		deselectSelectedParts();
		deselectSelectionRange();
	}

	protected void deselectSelectedParts() {
		constantSelection.forEach(ep -> ep.setSelected(EditPart.SELECTED_NONE));
	}

	protected void deselectSelectionRange() {
		SelectionRange range = getSelectionRange();
		if (range != null) {
			range.getSelectedParts().forEach(ep -> ((TextEditPart) ep).setSelection(-1, -1));
		}
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = obj == this;
		if (!result && obj instanceof SelectionModel other) {
			SelectionRange otherRange = other.getSelectionRange();
			result = constantSelection.equals(other.getSelectedEditParts())
					&& (selectionRange == otherRange || (selectionRange != null && selectionRange.equals(otherRange)));
		}
		return result;
	}

	public SelectionModel getAppendedSelection(EditPart newPart) {
		ArrayList<EditPart> list = new ArrayList<>(constantSelection);
		list.remove(newPart);
		list.add(newPart);
		return new SelectionModel(selectionRange, list);
	}

	public SelectionModel getExcludedSelection(EditPart exclude) {
		ArrayList<EditPart> list = new ArrayList<>(constantSelection);
		list.remove(exclude);
		return new SelectionModel(selectionRange, list);
	}

	public EditPart getFocusPart() {
		if (constantSelection.isEmpty()) {
			return null;
		}
		return constantSelection.get(constantSelection.size() - 1);
	}

	public List<EditPart> getSelectedEditParts() {
		return constantSelection;
	}

	public ISelection getSelection() {
		return new StructuredSelection(constantSelection);
	}

	public SelectionRange getSelectionRange() {
		return selectionRange;
	}

	public void applySelection(SelectionModel old) {
		if (old == null) {
			applySelectedParts();
			applySelectionRange();
			return;
		}

		// Convert to HashSet to optimize performance.
		if (!old.getSelectedEditParts().isEmpty()) {
			Collection<EditPart> editparts = new HashSet<>(constantSelection);
			old.getSelectedEditParts().stream().filter(part -> !editparts.contains(part))
					.forEach(part -> part.setSelected(EditPart.SELECTED_NONE));
		}
		applySelectedParts();

		old.deselectSelectionRange();
		applySelectionRange();
	}

	public boolean isTextSelected() {
		return selectionRange != null;
	}

}