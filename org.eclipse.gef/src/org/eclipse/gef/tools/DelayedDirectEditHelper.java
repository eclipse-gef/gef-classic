/*******************************************************************************
 * Copyright (c) 2000, 2024 IBM Corporation and others.
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

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;

/**
 * @author hudsonr Created on Mar 6, 2003
 */
class DelayedDirectEditHelper implements Runnable {

	private final EditPartViewer viewer;
	private final EditPart part;
	private final Request req;
	private FocusListener focus;
	private MouseListener mouse;
	private KeyListener key;
	private static DelayedDirectEditHelper activeHelper;

	/**
	 * Constructs a new helper and starts it immediately. If another helper is
	 * active, it is aborted and neither helper will run.
	 *
	 * @param viewer   the viewer on which the direct-edit is supposed to happen
	 * @param request  the request that triggered the direct-edit
	 * @param receiver the EditPart that received the request
	 */
	public DelayedDirectEditHelper(EditPartViewer viewer, Request request, EditPart receiver) {
		this.req = request;
		this.viewer = viewer;
		this.part = receiver;
		if (activeHelper != null) {
			setActiveHelper(null);
		} else {
			hookControl(viewer.getControl());
			setActiveHelper(this);
			Display display = Display.getCurrent();
			display.timerExec(display.getDoubleClickTime(), this);
		}
	}

	/**
	 * The edit is canceled by setting the active helper to <code>null</code>.
	 */
	@SuppressWarnings("static-method")
	void abort() {
		setActiveHelper(null);
	}

	void hookControl(Control control) {
		control.addFocusListener(focus = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				abort();
			}
		});
		control.addKeyListener(key = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				abort();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				abort();
			}
		});

		control.addMouseListener(mouse = new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				abort();
			}

			@Override
			public void mouseDown(MouseEvent e) {
				abort();
			}
		});
	}

	/**
	 * If this helper has not been aborted, the target editpart will be sent the
	 * request.
	 */
	@Override
	public void run() {
		if (activeHelper == this && part.isActive() && viewer.getControl() != null
				&& !viewer.getControl().isDisposed()) {
			part.performRequest(req);
		}
		if (viewer.getControl() != null && !viewer.getControl().isDisposed()) {
			viewer.getControl().removeFocusListener(focus);
			viewer.getControl().removeMouseListener(mouse);
			viewer.getControl().removeKeyListener(key);
		}
		setActiveHelper(null);
	}

	private static synchronized void setActiveHelper(DelayedDirectEditHelper activeHelper) {
		DelayedDirectEditHelper.activeHelper = activeHelper;
	}
}
