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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PR_75430 {
	public static void main(String[] args) {
		final Display display = new Display();
		Shell shell = new Shell(display);
		StackLayout layout = new StackLayout();
		shell.setLayout(layout);
		final Canvas parent = new Canvas(shell, SWT.BORDER);
		layout.topControl = parent;
		Composite child = new Composite(parent, SWT.NONE);
		child.setBackground(new Color(255, 0, 0));
		child.setBounds(100, 100, 50, 50);
		shell.setSize(250, 250);
		shell.open();
		display.timerExec(1000, new Runnable() {
			@Override
			public void run() {
				parent.redraw();
				parent.update();
				parent.scroll(0, -20, 0, 0, 250, 250, true);
				parent.update();
				GC gc = new GC(parent);
				gc.setBackground(new Color(200, 230, 240));
				gc.fillRectangle(0, 0, 250, 230);
				gc.drawRectangle(0, 20, 249, 229);
				gc.dispose();
				display.timerExec(1000, this);
			}
		});
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
