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
package org.eclipse.gef.dnd;

import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * A DragSourceListener that maintains and delegates to a set of
 * {@link TransferDragSourceListener}s. Each TransferDragSourceListener can then
 * be implemented as if it were the DragSource's only DragSourceListener.
 * <P>
 * When a native Drag is started, a subset of all
 * <code>TransferDragSourceListeners</code> is generated and stored in a list of
 * <i>active</i> listeners. This subset is calculated by forwarding
 * {@link DragSourceListener#dragStart(DragSourceEvent)} to every listener, and
 * inspecting changes to the {@link DragSourceEvent#doit doit} field. The
 * <code>DragSource</code>'s set of supported Transfer types (
 * {@link DragSource#setTransfer(Transfer[])}) is updated to reflect the
 * Transfer types corresponding to the active listener subset.
 * <P>
 * If and when {@link #dragSetData(DragSourceEvent)} is called, a single
 * <code>TransferDragSourceListener</code> is chosen, and only it is allowed to
 * set the drag data. The chosen listener is the first listener in the subset of
 * active listeners whose Transfer supports (
 * {@link Transfer#isSupportedType(TransferData)}) the dataType on the
 * <code>DragSourceEvent</code>.
 */
public class DelegatingDragAdapter extends org.eclipse.jface.util.DelegatingDragAdapter {

	/**
	 * Adds the given TransferDragSourceListener. The set of Transfer types is
	 * updated to reflect the change.
	 *
	 * @param listener the new listener
	 * @deprecated Use
	 *             {@link #addDragSourceListener(org.eclipse.jface.util.TransferDragSourceListener)}
	 *             instead. This method will be removed after the 2027-03 release.
	 */
	@SuppressWarnings("removal")
	@Deprecated(since = "3.0", forRemoval = true)
	public void addDragSourceListener(TransferDragSourceListener listener) {
		super.addDragSourceListener(listener);
	}

	/**
	 * Combines the <code>Transfer</code>s from every TransferDragSourceListener.
	 *
	 * @return the combined <code>Transfer</code>s
	 * @deprecated call getTransfers() instead. This method will be removed after
	 *             the 2027-03 release.
	 */
	@Deprecated(since = "3.0", forRemoval = true)
	public Transfer[] getTransferTypes() {
		return super.getTransfers();
	}

	/**
	 * Adds the given TransferDragSourceListener. The set of Transfer types is
	 * updated to reflect the change.
	 *
	 * @param listener the listener being removed
	 * @deprecated Use
	 *             {@link #removeDragSourceListener(org.eclipse.jface.util.TransferDragSourceListener)}
	 *             instead. This method will be removed after the 2027-03 release.
	 */
	@SuppressWarnings("removal")
	@Deprecated(since = "3.0", forRemoval = true)
	public void removeDragSourceListener(TransferDragSourceListener listener) {
		super.removeDragSourceListener(listener);
	}

}
