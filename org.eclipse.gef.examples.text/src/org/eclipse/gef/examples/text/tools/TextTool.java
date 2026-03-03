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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Caret;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelectionChangedListener;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.UpdateListener;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.CaretInfo;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackEventListener;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.gef.tools.ToolUtilities;

import org.eclipse.gef.examples.text.AppendableCommand;
import org.eclipse.gef.examples.text.GraphicalTextViewer;
import org.eclipse.gef.examples.text.SelectionRange;
import org.eclipse.gef.examples.text.TextCommand;
import org.eclipse.gef.examples.text.TextLocation;
import org.eclipse.gef.examples.text.TextUtilities;
import org.eclipse.gef.examples.text.actions.StyleListener;
import org.eclipse.gef.examples.text.actions.StyleProvider;
import org.eclipse.gef.examples.text.actions.StyleService;
import org.eclipse.gef.examples.text.edit.TextEditPart;
import org.eclipse.gef.examples.text.edit.TextStyleManager;
import org.eclipse.gef.examples.text.requests.CaretRequest;
import org.eclipse.gef.examples.text.requests.SearchResult;
import org.eclipse.gef.examples.text.requests.TextRequest;

/**
 * @since 3.1
 */
public class TextTool extends SelectionTool implements StyleProvider {

	static final boolean IS_CARBON = "carbon".equals(SWT.getPlatform()); //$NON-NLS-1$

	private static final int MODE_BS = 2;
	private static final int MODE_DEL = 3;
	private static final int MODE_TYPE = 1;
	private static final String KEY_OVERWRITE = "gef.texttool.overwrite"; //$NON-NLS-1$
	private final CommandStackEventListener commandListener = event -> {
		if ((event.getDetail() & CommandStack.POST_MASK) != 0) {
			fireStyleChanges();
			discardCaretLocation();
		}
	};
	private StyleListener listener;
	private AppendableCommand pendingCommand;
	private final ISelectionChangedListener selectionListener = event -> {
		fireStyleChanges();
		getCaret().setVisible(getSelectionRange() != null);
		queueCaretRefresh(true);
	};
	private final UpdateListener updateListener = new UpdateListener() {
		@Override
		public void notifyPainting(Rectangle damage, Map<IFigure, Rectangle> dirtyRegions) {
			queueCaretRefresh(false);
		}

		@Override
		public void notifyValidating() {
		}
	};
	private final List<String> styleKeys = new ArrayList<>();
	// @TODO:Pratik StyleService cannot be final
	private final StyleService styleService;
	private final List<Object> styleValues = new ArrayList<>();
	private CaretRefresh caretRefresh;
	private int textInputMode, caretXLoc;
	private boolean isMirrored, xCaptured, overwrite;

	public TextTool() {
		this(null);
	}

	/**
	 * @since 3.1
	 */
	public TextTool(StyleService service) {
		styleService = service;
	}

	/*
	 * uncomment this when this class moves to the same package as AbstractTool Need
	 * to override acceptAbort boolean acceptAbort(KeyEvent e) { return
	 * !isInState(STATE_INITIAL) && e.character == SWT.ESC; }
	 */

	@Override
	public void addStyleListener(StyleListener listener) {
		Assert.isTrue(this.listener == null);
		this.listener = listener;
	}

	@Override
	protected Cursor calculateCursor() {
		EditPart target = getTargetEditPart();
		if (target instanceof TextEditPart textTarget) {
			if (textTarget.acceptsCaret()) {
				return Cursors.IBEAM;
			}
		}
		return super.calculateCursor();
	}

	private void recordCaretLocation() {
		if (!xCaptured) {
			caretXLoc = getCaretBounds().x;
			xCaptured = true;
		}
	}

	private void discardCaretLocation() {
		xCaptured = false;
	}

	/**
	 * @since 3.1
	 * @param action
	 * @param event
	 */
	private void doAction(int action, KeyEvent event) {
		boolean append = false;
		getUpdateManager().performUpdate();
		setTextInputMode(0);
		event.doit = false;

		if (action == ST.PAGE_DOWN || action == ST.SELECT_PAGE_DOWN || action == ST.PAGE_UP
				|| action == ST.SELECT_PAGE_UP || action == ST.SELECT_LINE_DOWN || action == ST.LINE_DOWN
				|| action == ST.SELECT_LINE_UP || action == ST.LINE_UP) {
			recordCaretLocation();
		} else {
			discardCaretLocation();
		}

		switch (action) {
		case ST.SELECT_TEXT_START:
			append = true;
		case ST.TEXT_START:
			doSelect(CaretRequest.DOCUMENT, false, append, null);
			break;
		case ST.SELECT_TEXT_END:
			append = true;
		case ST.TEXT_END:
			doSelect(CaretRequest.DOCUMENT, true, append, null);
			break;
		case ST.SELECT_PAGE_DOWN:
			append = true;
		case ST.PAGE_DOWN:
			doTraversePage(true, append);
			break;
		case ST.SELECT_PAGE_UP:
			append = true;
		case ST.PAGE_UP:
			doTraversePage(false, append);
			break;
		case ST.DELETE_PREVIOUS:
			doBackspace();
			break;
		case ST.DELETE_NEXT:
			doDelete();
			break;
		// HOME
		case ST.SELECT_LINE_START:
			append = true;
		case ST.LINE_START:
			doSelect(CaretRequest.LINE_BOUNDARY, false, append, null);
			break;
		// WORD_PREV
		case ST.SELECT_WORD_NEXT:
			append = true;
		case ST.WORD_NEXT:
			doSelect(CaretRequest.WORD_BOUNDARY, true, append, null);
			break;
		// WORD_NEXT
		case ST.SELECT_WORD_PREVIOUS:
			append = true;
		case ST.WORD_PREVIOUS:
			doSelect(CaretRequest.WORD_BOUNDARY, false, append, null);
			break;
		// END
		case ST.SELECT_LINE_END:
			append = true;
		case ST.LINE_END:
			doSelect(CaretRequest.LINE_BOUNDARY, true, append, null);
			break;
		// LEFT
		case ST.SELECT_COLUMN_PREVIOUS:
			append = true;
		case ST.COLUMN_PREVIOUS:
			doSelect(CaretRequest.COLUMN, false, append, null);
			break;
		// RIGHT
		case ST.SELECT_COLUMN_NEXT:
			append = true;
		case ST.COLUMN_NEXT:
			doSelect(CaretRequest.COLUMN, true, append, null);
			break;
		// UP
		case ST.SELECT_LINE_UP:
			append = true;
		case ST.LINE_UP:
			doSelect(CaretRequest.ROW, false, append, null);
			break;
		// DOWN
		case ST.SELECT_LINE_DOWN:
			append = true;
		case ST.LINE_DOWN:
			doSelect(CaretRequest.ROW, true, append, null);
			break;
		// WINDOW END
		case ST.SELECT_WINDOW_END:
			append = true;
		case ST.WINDOW_END:
			doSelect(CaretRequest.WINDOW, true, append, null);
			break;
		// WINDOW START
		case ST.SELECT_WINDOW_START:
			append = true;
		case ST.WINDOW_START:
			doSelect(CaretRequest.WINDOW, false, append, null);
			break;
		case ST.TOGGLE_OVERWRITE:
			toggleOverwrite();
			break;
		// TAB
		case SWT.TAB | SWT.SHIFT:
			doUnindent();
			break;
		case SWT.TAB:
			if (!doIndent()) {
				doTyping(event);
			}
			break;
		// ENTER
		case SWT.CR:
			if (!doNewline()) {
				doTyping(event);
			}
			break;
		default:
			event.doit = true;
			break;
		}
	}

	/**
	 * @since 3.1
	 * @param e
	 */
	private boolean doBackspace() {
		setTextInputMode(MODE_BS);
		SelectionRange range = getSelectionRange();
		if (!range.isEmpty()) {
			return handleTextEdit(new TextRequest(TextRequest.REQ_REMOVE_RANGE, range));
		}
		if (handleTextEdit(new TextRequest(TextRequest.REQ_BACKSPACE, range, pendingCommand))) {
			return true;
		}
		doSelect(CaretRequest.COLUMN, false, false, null);
		return false;
	}

	private boolean doDelete() {
		setTextInputMode(MODE_DEL);
		SelectionRange range = getSelectionRange();

		if (!range.isEmpty()) {
			return handleTextEdit(new TextRequest(TextRequest.REQ_REMOVE_RANGE, range));
		}
		if (handleTextEdit(new TextRequest(TextRequest.REQ_DELETE, range, pendingCommand))) {
			return true;
		}
		doSelect(CaretRequest.COLUMN, true, false, null);
		return false;
	}

	/**
	 * @since 3.1
	 */
	private boolean doIndent() {
		setTextInputMode(0);
		SelectionRange range = getSelectionRange();
		if (!range.isEmpty()) {
			return false;
		}
		TextRequest edit = new TextRequest(TextRequest.REQ_INDENT, range);
		return handleTextEdit(edit);
	}

	/**
	 * @since 3.1
	 * @param e
	 */
	private boolean doInsertContent(char c) {
		setTextInputMode(MODE_TYPE);
		TextRequest edit = new TextRequest(overwrite ? TextRequest.REQ_OVERWRITE : TextRequest.REQ_INSERT,
				getSelectionRange(), Character.toString(c), pendingCommand);
		String keys[] = new String[styleKeys.size()];
		styleKeys.toArray(keys);
		edit.setStyles(keys, styleValues.toArray());
		return handleTextEdit(edit);
	}

	/**
	 * @since 3.1
	 * @param e
	 */
	private void doKeyDown(KeyEvent event) {
		int action = 0;
		if (event.keyCode != 0) {
			action = lookupAction(event.keyCode | event.stateMask);
		} else {
			action = lookupAction(event.character | event.stateMask);
			if (action == 0) {
				// see if we have a control character
				if ((event.stateMask & SWT.CTRL) != 0 && (event.character >= 0) && event.character <= 31) {
					// get the character from the CTRL+char sequence, the
					// control
					// key subtracts 64 from the value of the key that it
					// modifies
					int c = event.character + 64;
					action = lookupAction(c | event.stateMask);
				}
			}
		}

		if (action == 0) {
			doTyping(event);
		} else {
			doAction(action, event);
		}
	}

	/**
	 * @since 3.1
	 */
	private boolean doNewline() {
		setTextInputMode(MODE_BS);
		SelectionRange range = getSelectionRange();
		TextRequest edit;
		Assert.isTrue(range.isEmpty());
		edit = new TextRequest(TextRequest.REQ_NEWLINE, range, pendingCommand);
		return handleTextEdit(edit);
	}

	private void doSelect(Object type, boolean isForward, boolean append, Point loc) {
		GraphicalTextViewer viewer = getTextualViewer();
		SearchResult result = new SearchResult();
		CaretRequest search = new CaretRequest();
		search.setType(type);
		search.isForward = isForward;
		search.setLocation(loc);

		SelectionRange range = getSelectionRange();
		if (range == null) {
			if (viewer.getContents() instanceof TextEditPart tep) {
				if (tep.acceptsCaret()) {
					tep.getTextLocation(search, result);
				}
			}
		} else {
			TextLocation caretLocation = getCaretLocation();
			if (loc == null) {
				search.setLocation(new Point(xCaptured ? caretXLoc : getCaretBounds().x, getCaretInfo().getBaseline()));
			}
			search.where = caretLocation;
			caretLocation.part.getTextLocation(search, result);
			// isForward = range.isForward;
		}

		if (result.location == null) {
			return;
		}
		if (append) {
			TextLocation otherEnd = isForward ? range.begin : range.end;
			if (TextUtilities.isForward(otherEnd, result.location)) {
				range = new SelectionRange(otherEnd, result.location, true, result.trailing);
			} else {
				range = new SelectionRange(result.location, otherEnd, false, result.trailing);
			}
			viewer.setSelectionRange(range);
		} else {
			viewer.setSelectionRange(new SelectionRange(result.location, result.location, isForward, result.trailing));
		}
	}

	private void doTraversePage(boolean isForward, boolean appendSelection) {
		Rectangle caretBounds = getCaretBounds();
		Point loc = caretBounds.getCenter();
		loc.x = caretXLoc;
		int viewerHeight = getTextualViewer().getControl().getBounds().height - caretBounds.height;
		if (isForward) {
			loc.y += viewerHeight;
		} else {
			loc.y -= viewerHeight;
		}
		doSelect(CaretRequest.LOCATION, isForward, appendSelection, loc);
	}

	/**
	 * @since 3.1
	 * @param event
	 */
	private void doTyping(KeyEvent event) {
		boolean ignore = false;

		discardCaretLocation();

		if (IS_CARBON) {
			// Ignore accelerator key combinations (we do not want to
			// insert a character in the text in this instance). Do not
			// ignore COMMAND+ALT combinations since that key sequence
			// produces characters on the mac.
			ignore = (event.stateMask ^ SWT.COMMAND) == 0 || (event.stateMask ^ (SWT.COMMAND | SWT.SHIFT)) == 0;
		} else {
			// Ignore accelerator key combinations (we do not want to
			// insert a character in the text in this instance). Don't
			// ignore CTRL+ALT combinations since that is the Alt Gr
			// key on some keyboards.
			ignore = (event.stateMask ^ SWT.ALT) == 0 || (event.stateMask ^ SWT.CTRL) == 0
					|| (event.stateMask ^ (SWT.ALT | SWT.SHIFT)) == 0
					|| (event.stateMask ^ (SWT.CTRL | SWT.SHIFT)) == 0;
		}
		// -ignore anything below SPACE except for line delimiter keys and tab.
		// -ignore DEL
		if (!ignore && event.character > 31 && event.character != SWT.DEL || event.character == SWT.CR
				|| event.character == SWT.LF || event.character == '\t') {
			doInsertContent(event.character);
			event.doit = false;
		}
	}

	private boolean doUnindent() {
		setTextInputMode(0);
		SelectionRange range = getSelectionRange();
		if (!range.isEmpty()) {
			return false;
		}
		TextRequest edit = new TextRequest(TextRequest.REQ_UNINDENT, range);
		return handleTextEdit(edit);
	}

	private void fireStyleChanges() {
		if (listener != null) {
			listener.styleChanged(null);
		}
	}

	private void flushStyles() {
		styleKeys.clear();
		styleValues.clear();
	}

	private Caret getCaret() {
		Caret caret = null;
		if (getCurrentViewer() != null) {
			Canvas canvas = (Canvas) getCurrentViewer().getControl();
			caret = canvas.getCaret();
			if (caret == null) {
				caret = new Caret(canvas, 0);
			}
		}
		return caret;
	}

	public Rectangle getCaretBounds() {
		return new Rectangle(getCaret().getBounds());
	}

	public CaretInfo getCaretInfo() {
		TextLocation location = getCaretLocation();
		return location.part.getCaretPlacement(location.offset, getSelectionRange().trailing);
	}

	public TextLocation getCaretLocation() {
		if (getSelectionRange().isForward) {
			return getSelectionRange().end;
		}
		return getSelectionRange().begin;
	}

	public TextEditPart getCaretOwner() {
		if (getSelectionRange() != null) {
			return getCaretLocation().part;
		}
		return null;
	}

	private SelectionRange getSelectionRange() {
		if (getCurrentViewer() instanceof GraphicalTextViewer) {
			return getTextualViewer().getSelectionRange();
		}
		return null;
	}

	private UpdateManager getUpdateManager() {
		EditPartViewer viewer = getCurrentViewer();
		if (viewer != null) {
			EditPart root = viewer.getRootEditPart();
			if (root instanceof GraphicalEditPart gep) {
				return gep.getFigure().getUpdateManager();
			}
		}
		return null;
	}

	private Object getSelectionStyle(String styleID, boolean isState) {
		TextRequest req = new TextRequest(TextRequest.REQ_STYLE, getSelectionRange());
		req.setStyles(new String[] { styleID }, new Object[] { null });
		EditPart target = getTextTarget(req);
		if (target == null) {
			return StyleService.UNDEFINED;
		}
		TextStyleManager manager = target.getAdapter(TextStyleManager.class);
		if (isState) {
			return manager.getStyleState(styleID, getSelectionRange());
		}
		return manager.getStyleValue(styleID, getSelectionRange());
	}

	@Override
	public Object getStyle(String styleID) {
		for (int i = 0; i < styleKeys.size(); i++) {
			if (styleID.equals(styleKeys.get(i))) {
				return styleValues.get(i);
			}
		}
		return getSelectionStyle(styleID, false);
	}

	@Override
	public Object getStyleState(String styleID) {
		return getSelectionStyle(styleID, true);
	}

	private TextEditPart getTextTarget(Request request) {
		SelectionRange range = getSelectionRange();
		if (range == null) {
			return null;
		}
		EditPart target;
		EditPart candidate = ToolUtilities.findCommonAncestor(range.begin.part, range.end.part);

		do {
			target = candidate.getTargetEditPart(request);
			candidate = candidate.getParent();
		} while (target == null && candidate != null);
		return (TextEditPart) target;
	}

	GraphicalTextViewer getTextualViewer() {
		return (GraphicalTextViewer) getCurrentViewer();
	}

	@Override
	protected boolean handleButtonDown(int button) {
		discardCaretLocation();
		return super.handleButtonDown(button);
	}

	@Override
	protected boolean handleCommandStackChanged() {
		setTextInputMode(0);
		discardCaretLocation();
		return super.handleCommandStackChanged();
	}

	@Override
	protected boolean handleFocusGained() {
		if (getSelectionRange() == null) {
			doSelect(CaretRequest.DOCUMENT, false, false, null);
		}
		return super.handleFocusGained();
	}

	@Override
	protected boolean handleKeyDown(KeyEvent e) {
		if (isInState(STATE_INITIAL) && getTextualViewer().isTextSelected()) {
			doKeyDown(e);
		}

		if (e.doit) {
			return super.handleKeyDown(e);
		}
		return true;
	}

	@Override
	protected void handleKeyTraversed(TraverseEvent event) {
		if ((event.detail == SWT.TRAVERSE_TAB_PREVIOUS || event.detail == SWT.TRAVERSE_TAB_NEXT)
				&& (event.stateMask & SWT.CTRL) == 0) {
			event.doit = false;
		}
	}

	@Override
	protected boolean handleMove() {
		super.handleMove();
		refreshCursor();
		return true;
	}

	private boolean handleTextEdit(TextRequest edit) {
		EditPart target = getTextTarget(edit);

		Command insert = null;
		if (target != null) {
			insert = target.getCommand(edit);
		}

		if (insert == null) {
			return false;
		}

		if (pendingCommand == null || insert != pendingCommand) {
			if (!insert.canExecute()) {
				return false;
			}
			executeCommand(insert);
			if (insert instanceof AppendableCommand) {
				pendingCommand = (AppendableCommand) insert;
			} else {
				pendingCommand = null;
			}
		} else {
			if (!pendingCommand.canExecutePending()) {
				return false;
			}
			pendingCommand.executePending();
			GraphicalTextViewer viewer = getTextualViewer();
			viewer.setSelectionRange(((TextCommand) pendingCommand).getExecuteSelectionRange(viewer));
		}

		return true;
	}

	/**
	 * @since 3.1
	 * @param i
	 * @return
	 */
	private int lookupAction(int i) {
		switch (i) {
		// Left and Right
		case SWT.ARROW_LEFT:
			return isMirrored ? ST.COLUMN_NEXT : ST.COLUMN_PREVIOUS;
		case SWT.ARROW_RIGHT:
			return isMirrored ? ST.COLUMN_PREVIOUS : ST.COLUMN_NEXT;
		case SWT.ARROW_RIGHT | SWT.SHIFT:
			return isMirrored ? ST.SELECT_COLUMN_PREVIOUS : ST.SELECT_COLUMN_NEXT;
		case SWT.ARROW_LEFT | SWT.SHIFT:
			return isMirrored ? ST.SELECT_COLUMN_NEXT : ST.SELECT_COLUMN_PREVIOUS;
		case SWT.ARROW_RIGHT | SWT.CONTROL:
			return isMirrored ? ST.WORD_PREVIOUS : ST.WORD_NEXT;
		case SWT.ARROW_RIGHT | SWT.CONTROL | SWT.SHIFT:
			return isMirrored ? ST.SELECT_WORD_PREVIOUS : ST.SELECT_WORD_NEXT;
		case SWT.ARROW_LEFT | SWT.CONTROL:
			return isMirrored ? ST.WORD_NEXT : ST.WORD_PREVIOUS;
		case SWT.ARROW_LEFT | SWT.CONTROL | SWT.SHIFT:
			return isMirrored ? ST.SELECT_WORD_NEXT : ST.SELECT_WORD_PREVIOUS;

		case ST.LINE_END:
		case ST.TOGGLE_OVERWRITE:
		case ST.SELECT_LINE_END:
		case ST.LINE_START:
		case ST.SELECT_LINE_START:
		case ST.PAGE_UP:
		case ST.PAGE_DOWN:
		case ST.SELECT_PAGE_UP:
		case ST.SELECT_PAGE_DOWN:
		case ST.LINE_UP:
		case ST.LINE_DOWN:
		case ST.SELECT_LINE_UP:
		case ST.SELECT_LINE_DOWN:
		case ST.TEXT_END:
		case ST.SELECT_TEXT_END:
		case ST.TEXT_START:
		case ST.SELECT_TEXT_START:
		case ST.DELETE_PREVIOUS:
		case ST.DELETE_NEXT:
		case ST.WINDOW_START:
		case ST.WINDOW_END:
		case ST.SELECT_WINDOW_START:
		case ST.SELECT_WINDOW_END:
		case SWT.TAB | SWT.SHIFT:
		case SWT.TAB:
			return i;

		case SWT.LF:
		case SWT.CR:
			return SWT.CR;
		default:
			break;
		}
		return 0;
	}

	void queueCaretRefresh(boolean revealAfterwards) {
		if (!getCaret().isVisible()) {
			return;
		}
		if (caretRefresh == null) {
			caretRefresh = new CaretRefresh(revealAfterwards);
			getUpdateManager().runWithUpdate(caretRefresh);
		} else {
			caretRefresh.enableReveal(revealAfterwards);
		}
	}

	@Override
	public void removeStyleListener(StyleListener listener) {
		Assert.isTrue(this.listener == listener);
		this.listener = null;
	}

	@Override
	public void setDragTracker(DragTracker newDragTracker) {
		if (getDragTracker() == newDragTracker) {
			return;
		}
		setTextInputMode(0);
		super.setDragTracker(newDragTracker);
	}

	@Override
	public void setStyle(String styleID, Object newValue) {
		// Check for cancellations: lookup old style and remove any pending ones
		Object oldValue = getSelectionStyle(styleID, false);
		if (newValue.equals(oldValue)) {
			int prev = styleKeys.indexOf(styleID);
			if (prev != -1) {
				styleKeys.remove(prev);
				styleValues.remove(prev);
				return;
			}
		}

		// Try to apply immediately, pend otherwise.
		TextRequest req = new TextRequest(TextRequest.REQ_STYLE, getSelectionRange());
		// $TODO should this be all pending styles or just the recently set?
		req.setStyles(new String[] { styleID }, new Object[] { newValue });
		EditPart target = getTextTarget(req);
		Command c = target.getCommand(req);
		if (c == null) {
			int prev = styleKeys.indexOf(styleID);
			if (prev != -1) {
				styleKeys.remove(prev);
				styleValues.remove(prev);
			}
			styleKeys.add(0, styleID);
			styleValues.add(0, newValue);
		} else if (c.canExecute()) {
			// $TODO cleanup any pending styles?
			executeCommand(c);
		}
	}

	@Override
	public void setViewer(EditPartViewer viewer) {
		EditPartViewer currentViewer = getCurrentViewer();
		if (viewer == currentViewer || viewer == null) {
			return;
		}

		if (currentViewer != null) {
			if (caretRefresh != null) {
				getUpdateManager().performUpdate();
			}
			currentViewer.getEditDomain().getCommandStack().removeCommandStackEventListener(commandListener);
			currentViewer.removeSelectionChangedListener(selectionListener);
			UpdateManager manager = getUpdateManager();
			if (manager != null) {
				manager.removeUpdateListener(updateListener);
			}
			currentViewer.setProperty(KEY_OVERWRITE, overwrite ? Boolean.TRUE : Boolean.FALSE);
			if (styleService != null) {
				styleService.setStyleProvider(null);
			}
			setTextInputMode(0);
			setTargetRequest(null);
		}
		super.setViewer(viewer);
		if (viewer != null) {
			isMirrored = (viewer.getControl().getStyle() & SWT.MIRRORED) != 0;
			viewer.getEditDomain().getCommandStack().addCommandStackEventListener(commandListener);
			viewer.addSelectionChangedListener(selectionListener);
			UpdateManager manager = getUpdateManager();
			if (manager != null) {
				manager.addUpdateListener(updateListener);
			}
			Boolean bool = (Boolean) viewer.getProperty(KEY_OVERWRITE);
			overwrite = bool != null && bool.booleanValue();
			if (styleService != null) {
				styleService.setStyleProvider(this);
			}
		}
	}

	/**
	 * @since 3.1
	 * @param mode the new input mode
	 */
	private void setTextInputMode(int mode) {
		if (textInputMode != mode) {
			pendingCommand = null;
		}
		if (textInputMode != MODE_TYPE) {
			flushStyles();
		}
		textInputMode = mode;
	}

	private void toggleOverwrite() {
		overwrite = !overwrite;
		queueCaretRefresh(false);
	}

	class CaretRefresh implements Runnable {
		private boolean reveal;

		public CaretRefresh(boolean reveal) {
			enableReveal(reveal);
		}

		@Override
		public void run() {
			refreshCaret();
			caretRefresh = null;
			if (reveal) {
				getTextualViewer().revealCaret();
			}
		}

		public void refreshCaret() {
			if (getCaretOwner() == null) {
				return;
			}
			CaretInfo info = getCaretInfo();
			getCaret().setBounds(info.getX(), info.getY(), overwrite ? info.getHeight() / 2 : 1, info.getHeight());
		}

		public void enableReveal(boolean newVal) {
			reveal |= newVal;
		}
	}

}