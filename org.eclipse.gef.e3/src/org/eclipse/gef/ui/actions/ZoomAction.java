/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.draw2d.zoom.ZoomListener;

import org.eclipse.gef.Disposable;
import org.eclipse.gef.editparts.ZoomManager;

/**
 * Base zoom action. Sub-classes can perform zoom in or zoom out.
 *
 * @author hudsonr
 * @see org.eclipse.gef.ui.actions.ZoomInAction
 * @see org.eclipse.gef.ui.actions.ZoomOutAction
 */
abstract class ZoomAction extends Action implements ZoomListener, Disposable {

	/**
	 * The ZoomManager used to zoom in or out
	 */
	protected ZoomManager zoomManager;

	/**
	 * Constructor
	 *
	 * @param text        the action's text, or <code>null</code> if there is no
	 *                    text
	 * @param image       the action's image, or <code>null</code> if there is no
	 *                    image
	 * @param zoomManager the ZoomManager used to zoom in or out
	 */
	public ZoomAction(String text, ImageDescriptor image, ZoomManager zoomManager) {
		super(text, image);
		this.zoomManager = zoomManager;
		zoomManager.addZoomListener(this);
	}

	/**
	 * @see org.eclipse.gef.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		zoomManager.removeZoomListener(this);
	}

}
