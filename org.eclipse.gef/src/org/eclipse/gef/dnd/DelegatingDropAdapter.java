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

import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;

/**
 * A <code>DropTargetListener</code> that manages and delegates to a set of
 * {@link TransferDropTargetListener}s. Each
 * <code>TransferDropTargetListener</code> can then be implemented as if it were
 * the DropTarget's only DropTargetListener.
 * <P>
 * On each DropTargetEvent, a <i>current</i> listener is obtained from the set
 * of all TransferDropTargetListers. The current listener is the first listener
 * to return <code>true</code> for
 * {@link TransferDropTargetListener#isEnabled(DropTargetEvent)}. The current
 * listener is forwarded all <code>DropTargetEvents</code> until some other
 * listener becomes the current listener, or the Drop terminates.
 * <P>
 * As listeners are added and removed, the combined set of Transfers is updated
 * to contain the <code>Tranfer</code> from each listener.
 * {@link #getTransferTypes()} provides the merged transfers. This set of
 * Transfers should be set on the SWT {@link org.eclipse.swt.dnd.DropTarget}.
 */
public class DelegatingDropAdapter extends org.eclipse.jface.util.DelegatingDropAdapter {

	/**
	 * Adds the given TransferDropTargetListener.
	 *
	 * @param listener the listener
	 * @deprecated Use
	 *             {@link #addDropTargetListener(org.eclipse.jface.util.TransferDropTargetListener)}
	 *             instead. This method will be removed after the 2027-03 release.
	 */
	@SuppressWarnings("removal")
	@Deprecated(since = "3.0", forRemoval = true)
	public void addDropTargetListener(TransferDropTargetListener listener) {
		super.addDropTargetListener(listener);
	}

	/**
	 * Adds the Transfer from each listener to an array and returns that array.
	 *
	 * @return the merged Transfers from all listeners
	 * @deprecated use getTransfers() instead
	 */
	@Deprecated(since = "3.0", forRemoval = true)
	public Transfer[] getTransferTypes() {
		return super.getTransfers();
	}

	/**
	 * Removes the given <code>TransferDropTargetListener</code>.
	 *
	 * @param listener the listener
	 * @deprecated Use
	 *             {@link #removeDropTargetListener(org.eclipse.jface.util.TransferDropTargetListener)}
	 *             instead. This method will be removed after the 2027-03 release.
	 */
	@SuppressWarnings("removal")
	@Deprecated(since = "3.21", forRemoval = true)
	public void removeDropTargetListener(TransferDropTargetListener listener) {
		super.removeDropTargetListener(listener);
	}

}
