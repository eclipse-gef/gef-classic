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
package org.eclipse.draw2d;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Provides support for a ConnectionAnchor. A ConnectionAnchor is one of the end
 * points of a {@link Connection}. It holds listeners and notifies them if the
 * anchor is moved.
 */
public abstract class ConnectionAnchorBase implements ConnectionAnchor {

	/**
	 * The list of listeners
	 */
	protected List<AnchorListener> listeners = new CopyOnWriteArrayList<>();

	/**
	 * @see org.eclipse.draw2d.ConnectionAnchor#addAnchorListener(AnchorListener)
	 */
	@Override
	public void addAnchorListener(AnchorListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see org.eclipse.draw2d.ConnectionAnchor#removeAnchorListener(AnchorListener)
	 */
	@Override
	public void removeAnchorListener(AnchorListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notifies all the listeners in the list of a change in position of this
	 * anchor. This is called from one of the implementing anchors when its location
	 * is changed.
	 *
	 * @since 2.0
	 */
	protected void fireAnchorMoved() {
		listeners.forEach(listener -> listener.anchorMoved(this));
	}

}
