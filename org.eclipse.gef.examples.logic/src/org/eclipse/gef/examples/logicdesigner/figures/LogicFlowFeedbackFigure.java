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

import org.eclipse.draw2d.RectangleFigure;

public class LogicFlowFeedbackFigure extends RectangleFigure {

	public LogicFlowFeedbackFigure() {
		this.setFill(false);
		setBorder(new LogicFlowFeedbackBorder());
	}

}
