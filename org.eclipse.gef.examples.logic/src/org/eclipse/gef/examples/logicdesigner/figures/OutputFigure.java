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

import org.eclipse.draw2d.StackLayout;

import org.eclipse.gef.examples.logicdesigner.model.SimpleOutput;

public class OutputFigure extends NodeFigure {

	public OutputFigure() {
		FixedConnectionAnchor outputConnectionAnchor = new FixedConnectionAnchor(this);
		outputConnectionAnchor.topDown = false;
		outputConnectionAnchor.offsetH = 14;
		outputConnectionAnchors.add(outputConnectionAnchor);
		connectionAnchors.put(SimpleOutput.TERMINAL_OUT, outputConnectionAnchor);
		setLayoutManager(new StackLayout());
	}

}
