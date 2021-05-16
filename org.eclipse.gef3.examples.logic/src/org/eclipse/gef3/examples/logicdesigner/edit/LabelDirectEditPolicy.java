/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef3.examples.logicdesigner.edit;

import org.eclipse.gef3.commands.Command;
import org.eclipse.gef3.editpolicies.DirectEditPolicy;
import org.eclipse.gef3.requests.DirectEditRequest;

import org.eclipse.gef3.examples.logicdesigner.figures.LabelFigure;
import org.eclipse.gef3.examples.logicdesigner.model.LogicLabel;
import org.eclipse.gef3.examples.logicdesigner.model.commands.LogicLabelCommand;

public class LabelDirectEditPolicy extends DirectEditPolicy {

	/**
	 * @see DirectEditPolicy#getDirectEditCommand(DirectEditRequest)
	 */
	protected Command getDirectEditCommand(DirectEditRequest edit) {
		String labelText = (String) edit.getCellEditor().getValue();
		LogicLabelEditPart label = (LogicLabelEditPart) getHost();
		LogicLabelCommand command = new LogicLabelCommand(
				(LogicLabel) label.getModel(), labelText);
		return command;
	}

	/**
	 * @see DirectEditPolicy#showCurrentEditValue(DirectEditRequest)
	 */
	protected void showCurrentEditValue(DirectEditRequest request) {
		String value = (String) request.getCellEditor().getValue();
		((LabelFigure) getHostFigure()).setText(value);
		// hack to prevent async layout from placing the cell editor twice.
		getHostFigure().getUpdateManager().performUpdate();

	}

}