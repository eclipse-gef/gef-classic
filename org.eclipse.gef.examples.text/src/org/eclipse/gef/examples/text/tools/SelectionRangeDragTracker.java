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

package org.eclipse.gef.examples.text.tools;

import org.eclipse.swt.graphics.Cursor;

import org.eclipse.draw2d.Cursors;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.tools.SimpleDragTracker;
import org.eclipse.gef.tools.ToolUtilities;

import org.eclipse.gef.examples.text.GraphicalTextViewer;
import org.eclipse.gef.examples.text.SelectionRange;
import org.eclipse.gef.examples.text.TextLocation;
import org.eclipse.gef.examples.text.edit.TextEditPart;
import org.eclipse.gef.examples.text.requests.CaretRequest;
import org.eclipse.gef.examples.text.requests.SearchResult;

/**
 * @since 3.1
 */
public class SelectionRangeDragTracker extends SimpleDragTracker {

	private static final int STATE_START = SimpleDragTracker.MAX_STATE << 1;

	private static final int STATE_SWIPE = SimpleDragTracker.MAX_STATE << 2;

	private TextLocation beginDrag;

	private TextLocation endDrag;

	/**
	 * This flag is set to true during a double-click-swipe.
	 */
	private boolean isWordSelection;

	private final TextEditPart textSource;

	/**
	 * @since 3.1
	 */
	public SelectionRangeDragTracker(TextEditPart part) {
		this.textSource = part;
	}

	@Override
	protected Cursor calculateCursor() {
		return Cursors.IBEAM;
	}

	/**
	 *
	 * @since 3.1
	 */
	@SuppressWarnings("deprecation")
	private void doNormalSwipe() {
		SearchResult result = getCurrentTextLocation();
		endDrag = result.location;
		if (endDrag != null) {

			// Previous
			EditPart end = endDrag.part;
			EditPart begin = beginDrag.part;
			boolean inverted = false;
			if (end == begin) {
				inverted = endDrag.offset < beginDrag.offset;
			} else {
				EditPart ancestor = ToolUtilities.findCommonAncestor(end, begin);
				while (end.getParent() != ancestor) {
					end = end.getParent();
				}
				while (begin.getParent() != ancestor) {
					begin = begin.getParent();
				}
				inverted = ancestor.getChildren().indexOf(end) < ancestor.getChildren().indexOf(begin);
			}
			GraphicalTextViewer viewer = (GraphicalTextViewer) getCurrentViewer();
			if (!inverted) {
				viewer.setSelectionRange(new SelectionRange(beginDrag, endDrag, true, result.trailing));
			} else {
				viewer.setSelectionRange(new SelectionRange(endDrag, beginDrag, false, result.trailing));
			}
		}
	}

	/**
	 * Selects the word at the current mouse location
	 *
	 * @since 3.1
	 */
	@SuppressWarnings("deprecation")
	private void doWordSelect() {
		SearchResult result = new SearchResult();
		CaretRequest locationRequest = new CaretRequest();
		locationRequest.setType(CaretRequest.LOCATION);
		locationRequest.setLocation(getLocation());
		locationRequest.isForward = true;
		getSource().getTextLocation(locationRequest, result);
		TextLocation exact = result.location;

		CaretRequest nextWord = new CaretRequest();
		result = new SearchResult();
		nextWord.setType(CaretRequest.WORD_BOUNDARY);
		nextWord.isForward = true;
		nextWord.where = exact;
		getSource().getTextLocation(nextWord, result);
		TextLocation wordEnd = result.location;
		boolean isAfter = result.trailing;
		result = new SearchResult();
		nextWord.where = wordEnd;
		nextWord.isForward = false;
		getSource().getTextLocation(nextWord, result);
		TextLocation wordBegin = result.location;
		if (wordBegin != null && wordEnd != null) {
			((GraphicalTextViewer) getCurrentViewer())
					.setSelectionRange(new SelectionRange(wordBegin, wordEnd, true, isAfter));
		}
	}

	/**
	 * @since 3.1
	 */
	private void doWordSwipe() {
	}

	@Override
	protected String getCommandName() {
		return "Drop Text Request"; //$NON-NLS-1$
	}

	private SearchResult getCurrentTextLocation() {
		SearchResult result = new SearchResult();
		EditPart part = getCurrentViewer().findObjectAt(getLocation());
		if (part instanceof TextEditPart textPart) {
			if (textPart.acceptsCaret()) {
				CaretRequest request = new CaretRequest();
				request.setType(CaretRequest.LOCATION);
				request.setLocation(getLocation());
				textPart.getTextLocation(request, result);
			}
		}
		return result;
	}

	/**
	 *
	 * @since 3.1
	 */
	private TextEditPart getSource() {
		return textSource;
	}

	@Override
	@SuppressWarnings("deprecation")
	protected boolean handleButtonDown(int button) {
		if (button == 1) {
			SearchResult result = getCurrentTextLocation();
			beginDrag = result.location;
			((GraphicalTextViewer) getCurrentViewer())
					.setSelectionRange(new SelectionRange(beginDrag, beginDrag, true, result.trailing));
			return stateTransition(STATE_INITIAL, STATE_START);
		}
		return super.handleButtonDown(button);
	}

	@Override
	protected boolean handleDoubleClick(int button) {
		if (button == 1) {
			doWordSelect();
			isWordSelection = true;
			return true;
		}
		return super.handleDoubleClick(button);
	}

	@Override
	protected boolean handleDragInProgress() {
		// $TODO during a swipe, the viewer should not be firing selection
		// changes the whole time.
		if (isInState(STATE_SWIPE)) {
			if (isWordSelection) {
				doWordSwipe();
			} else {
				doNormalSwipe();
			}
		}
		return super.handleDragInProgress();
	}

	@Override
	protected boolean handleDragStarted() {
		return stateTransition(STATE_START, STATE_SWIPE);
	}

}
