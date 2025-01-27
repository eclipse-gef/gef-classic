/*******************************************************************************
 * Copyright (c) 2000, 2022 IBM Corporation and others.
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
package org.eclipse.gef.examples.logicdesigner.dnd;

import org.eclipse.swt.dnd.Transfer;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;

import org.eclipse.gef.examples.logicdesigner.edit.NativeDropRequest;

public class TextTransferDropTargetListener extends AbstractTransferDropTargetListener {

	public TextTransferDropTargetListener(EditPartViewer viewer, Transfer xfer) {
		super(viewer, xfer);
	}

	@Override
	protected Request createTargetRequest() {
		return new NativeDropRequest();
	}

	@Override
	protected NativeDropRequest getTargetRequest() {
		return (NativeDropRequest) super.getTargetRequest();
	}

	@Override
	protected void updateTargetRequest() {
		getTargetRequest().setData(getCurrentEvent().data);
	}

}
