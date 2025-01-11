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
package org.eclipse.gef.examples.logicdesigner.figures;

import org.eclipse.gef.examples.logicdesigner.model.Gate;

public class GateFigure extends OutputFigure {

	public GateFigure() {
		FixedConnectionAnchor inputConnectionAnchorA = new FixedConnectionAnchor(this);
		inputConnectionAnchorA.offsetH = 8;
		FixedConnectionAnchor inputConnectionAnchorB = new FixedConnectionAnchor(this);
		inputConnectionAnchorB.offsetH = 20;
		inputConnectionAnchors.add(inputConnectionAnchorA);
		inputConnectionAnchors.add(inputConnectionAnchorB);
		connectionAnchors.put(Gate.TERMINAL_A, inputConnectionAnchorA);
		connectionAnchors.put(Gate.TERMINAL_B, inputConnectionAnchorB);
	}

	@Override
	public String toString() {
		return "GateFigure"; //$NON-NLS-1$
	}

}
