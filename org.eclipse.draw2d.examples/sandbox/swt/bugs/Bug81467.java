/*******************************************************************************
 * Copyright (c) 2005, 2023 IBM Corporation and others.
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
package swt.bugs;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.PrinterGraphics;
import org.eclipse.draw2d.SWTGraphics;

public class Bug81467 {

	public static void main(String[] args) {
		Font font = new Font(null, "Helvetica", 12, 0); //$NON-NLS-1$
		Printer printer = new Printer();
		printer.startJob("bugzilla 81467"); //$NON-NLS-1$

		GC gc = new GC(printer);
		SWTGraphics graphics = new SWTGraphics(gc);
		PrinterGraphics printGraphics = new PrinterGraphics(graphics, printer);

		printGraphics.scale((double) printer.getDPI().x / Display.getDefault().getDPI().x);
		printGraphics.setFont(font);
		printGraphics.drawString("Hello world", 50, 50); //$NON-NLS-1$
		printGraphics.dispose();

		graphics.dispose();
		gc.dispose();
		printer.endJob();
		printer.dispose();
		font.dispose();
	}

}