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
package org.eclipse.gef.tools;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Event;

import org.eclipse.draw2d.geometry.Point;

import org.eclipse.gef.AccessibleAnchorProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreationFactory;

/**
 * The default creation tool for connections. With this tool, the user must
 * click and release the left mouse button on the source edit part and then
 * click and release the left mouse button on the target edit part. By default,
 * this tool will remain active after connections are created. The user must
 * select a different tool to deactivate this tool.
 */
public class ConnectionCreationTool extends AbstractConnectionCreationTool {

	/**
	 * Default Constructor.
	 */
	public ConnectionCreationTool() {
		setUnloadWhenFinished(false);
	}

	/**
	 * Constructs a new ConnectionCreationTool with the given factory.
	 *
	 * @param factory the creation factory
	 */
	public ConnectionCreationTool(CreationFactory factory) {
		setFactory(factory);
		setUnloadWhenFinished(false);
	}

	boolean acceptConnectionFinish(KeyEvent event) {
		return isInState(STATE_ACCESSIBLE_DRAG_IN_PROGRESS) && event.character == 13;
	}

	boolean acceptConnectionStart(KeyEvent event) {
		return isInState(STATE_INITIAL) && event.character == 13;
	}

	/**
	 * If the connections is already started, the second button down will call
	 * {@link AbstractConnectionCreationTool#handleCreateConnection()}. Otherwise,
	 * it attempts to start the connection.
	 *
	 * @param button the button that was pressed
	 * @return <code>true</code> if the button down was processed
	 */
	@Override
	protected boolean handleButtonDown(int button) {
		if (button == 1 && stateTransition(STATE_CONNECTION_STARTED, STATE_TERMINAL)) {
			return handleCreateConnection();
		}

		super.handleButtonDown(button);
		if (isInState(STATE_CONNECTION_STARTED)) {
			// Fake a drag to cause feedback to be displayed immediately on
			// mouse down.
			handleDrag();
		}
		return true;
	}

	/**
	 * Cleans up feedback and resets the tool when focus is lost.
	 *
	 * @return <code>true</code> if this focus lost event was processed
	 */
	@Override
	protected boolean handleFocusLost() {
		if (isInState(STATE_CONNECTION_STARTED | STATE_ACCESSIBLE_DRAG_IN_PROGRESS)) {
			eraseSourceFeedback();
			eraseTargetFeedback();
			setState(STATE_INVALID);
			handleFinished();
		}
		return super.handleFocusLost();
	}

	/**
	 * Processes the arrow keys (to move the cursor to nearby anchor locations) and
	 * the enter key (to start or complete a connections).
	 *
	 * @param event the key event
	 * @return <code>true</code> if this key down event was processed
	 */
	@Override
	protected boolean handleKeyDown(KeyEvent event) {
		if (acceptArrowKey(event)) {
			int direction = getDirection(event);

			boolean consumed = false;
			if (direction != 0 && event.stateMask == 0) {
				consumed = navigateNextAnchor(direction);
			}
			if (!consumed) {
				event.stateMask |= SWT.CONTROL;
				event.stateMask &= ~SWT.SHIFT;
				if (getCurrentViewer().getKeyHandler().keyPressed(event)) {
					navigateNextAnchor(0);
					updateTargetRequest();
					updateTargetUnderMouse();
					Command command = getCommand();
					if (command != null) {
						setCurrentCommand(command);
					}
					return true;
				}
			}
		}

		if (event.character == '/' || event.character == '\\') {
			event.stateMask |= SWT.CONTROL;
			if (getCurrentViewer().getKeyHandler().keyPressed(event)) {
				navigateNextAnchor(0);
				return true;
			}
		}

		if (acceptConnectionStart(event)) {
			performConnectionStart();
			return true;
		}

		if (acceptConnectionFinish(event)) {
			performConnectionFinished();
			return true;
		}

		return super.handleKeyDown(event);
	}

	/**
	 * Scrolling can happen either in the {@link AbstractTool#STATE_INITIAL initial}
	 * state or once the source of the connection has been
	 * {@link AbstractConnectionCreationTool#STATE_CONNECTION_STARTED identified}.
	 *
	 * @see org.eclipse.gef.Tool#mouseWheelScrolled(org.eclipse.swt.widgets.Event,
	 *      org.eclipse.gef.EditPartViewer)
	 */
	@Override
	public void mouseWheelScrolled(Event event, EditPartViewer viewer) {
		if (isInState(STATE_INITIAL | STATE_CONNECTION_STARTED)) {
			performViewerMouseWheel(event, viewer);
		}
	}

	boolean navigateNextAnchor(int direction) {
		EditPart focus = getCurrentViewer().getFocusEditPart();
		AccessibleAnchorProvider provider;
		provider = focus.getAdapter(AccessibleAnchorProvider.class);
		if (provider == null) {
			return false;
		}

		final List<Point> list = (isInState(STATE_ACCESSIBLE_DRAG_IN_PROGRESS)) ? provider.getTargetAnchorLocations()
				: provider.getSourceAnchorLocations();

		Point start = getLocation();
		int distance = Integer.MAX_VALUE;
		Point next = null;
		for (Point p : list) {
			if (p.equals(start) || (direction != 0 && (start.getPosition(p) != direction))) {
				continue;
			}
			int d = p.getDistanceOrthogonal(start);
			if (d < distance) {
				distance = d;
				next = p;
			}
		}

		if (next != null) {
			placeMouseInViewer(next);
			return true;
		}
		return false;
	}

	private void performConnectionStart() {
		Command command = getCommand();
		if (command != null && command.canExecute()) {
			updateTargetUnderMouse();
			setConnectionSource(getTargetEditPart());
			getTargetRequest().setSourceEditPart(getTargetEditPart());
			setState(STATE_ACCESSIBLE_DRAG_IN_PROGRESS);
			placeMouseInViewer(getLocation().getTranslated(6, 6));
		}
	}

	private void performConnectionFinished() {
		Command command = getCommand();
		if (command != null && command.canExecute()) {
			setState(STATE_INITIAL);
			placeMouseInViewer(getLocation().getTranslated(6, 6));
			eraseSourceFeedback();
			eraseTargetFeedback();
			setCurrentCommand(command);
			executeCurrentCommand();
		}
	}

}
