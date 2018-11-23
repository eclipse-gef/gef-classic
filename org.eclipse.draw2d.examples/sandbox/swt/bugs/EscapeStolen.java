/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * The control should be given a chance to process the ESC key.
 */

package swt.bugs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.FigureCanvas;

public class EscapeStolen {

static class SubCanvas extends Canvas {
	SubCanvas(Composite parent) {
		super(parent, SWT.H_SCROLL);
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				System.out.println(e);
			}
		});
	}
}

public static void main(String[] args) {
	Display display = new Display();
	Shell shell = new Shell();

	Shell dialog = new Shell(shell);
	dialog.setLayout(new GridLayout());
	final FigureCanvas canvas = new FigureCanvas(dialog);
	canvas.setContents(new Button("draw2d"));

	canvas.addFocusListener(new FocusListener() {

		public void focusGained(FocusEvent e) {
			canvas.setBackground(new Color(null, 30, 30, 255));
		}

		public void focusLost(FocusEvent e) {
			canvas.setBackground(null);
		}
	});

	shell.open();
	dialog.open();
	canvas.forceFocus();

	while (!shell.isDisposed())
		if (!display.readAndDispatch())
			display.sleep();
}

}