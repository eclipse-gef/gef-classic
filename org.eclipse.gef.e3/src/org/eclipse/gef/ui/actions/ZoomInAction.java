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

import org.eclipse.draw2d.zoom.ZoomListener;

import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.internal.InternalImages;

/**
 * @author danlee
 */
public class ZoomInAction extends ZoomAction {

	/**
	 * Constructor for ZoomInAction.
	 *
	 * @param zoomManager the zoom manager
	 */
	public ZoomInAction(ZoomManager zoomManager) {
		super(GEFMessages.ZoomIn_Label, InternalImages.DESC_ZOOM_IN, zoomManager);
		setToolTipText(GEFMessages.ZoomIn_Tooltip);
		setId(GEFActionConstants.ZOOM_IN);
		setActionDefinitionId(GEFActionConstants.ZOOM_IN);
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
	public void run() {
		zoomManager.zoomIn();
	}

	/**
	 * @see ZoomListener#zoomChanged(double)
	 */
	@Override
	public void zoomChanged(double zoom) {
		setEnabled(zoomManager.canZoomIn());
	}

}
