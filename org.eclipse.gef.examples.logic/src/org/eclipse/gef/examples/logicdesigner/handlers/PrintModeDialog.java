/*******************************************************************************
 * Copyright (c) 2003, 2025 IBM Corporation and others.
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
package org.eclipse.gef.examples.logicdesigner.handlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.dialogs.Dialog;

import org.eclipse.draw2d.PrintFigureOperation;

import org.eclipse.gef.examples.logicdesigner.LogicMessages;

/**
 * @author Eric Bordeau
 */
public class PrintModeDialog extends Dialog {

	private Button tile;
	private Button fitPage;
	private Button fitWidth;
	private Button fitHeight;

	public PrintModeDialog(Shell shell) {
		super(shell);
	}

	@Override
	protected void cancelPressed() {
		setReturnCode(-1);
		close();
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText(LogicMessages.PrintDialog_Title);
		super.configureShell(newShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		tile = new Button(composite, SWT.RADIO);
		tile.setText(LogicMessages.PrintDialog_Tile);
		tile.setSelection(true);

		fitPage = new Button(composite, SWT.RADIO);
		fitPage.setText(LogicMessages.PrintDialog_Page);

		fitWidth = new Button(composite, SWT.RADIO);
		fitWidth.setText(LogicMessages.PrintDialog_Width);

		fitHeight = new Button(composite, SWT.RADIO);
		fitHeight.setText(LogicMessages.PrintDialog_Height);

		return composite;
	}

	@Override
	protected void okPressed() {
		int returnCode = -1;
		if (tile.getSelection()) {
			returnCode = PrintFigureOperation.TILE;
		} else if (fitPage.getSelection()) {
			returnCode = PrintFigureOperation.FIT_PAGE;
		} else if (fitHeight.getSelection()) {
			returnCode = PrintFigureOperation.FIT_HEIGHT;
		} else if (fitWidth.getSelection()) {
			returnCode = PrintFigureOperation.FIT_WIDTH;
		}
		setReturnCode(returnCode);
		close();
	}
}
