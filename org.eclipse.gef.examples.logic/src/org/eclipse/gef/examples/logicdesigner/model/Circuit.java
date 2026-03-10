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
package org.eclipse.gef.examples.logicdesigner.model;

import org.eclipse.gef.examples.logicdesigner.LogicMessages;

public class Circuit extends LogicDiagram {

	static final long serialVersionUID = 1;

	private static int count;
	public static final String TERMINALS_OUT[] = { "1", "2", "3", "4", //$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
			"5", "6", "7", "8" };//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$

	public static final String TERMINALS_IN[] = { "A", "B", "C", "D", //$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
			"E", "F", "G", "H" };//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$

	@Override
	public String getNewID() {
		return Integer.toString(count++);
	}

	@Override
	public String toString() {
		return LogicMessages.Circuit_LabelText + " #" + getID(); //$NON-NLS-1$
	}

	@Override
	public void update() {
		for (int i = 0; i < 8; i++) {
			setOutput(TERMINALS_OUT[i], getInput(TERMINALS_IN[i]));
		}
	}

}
