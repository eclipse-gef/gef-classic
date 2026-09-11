/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
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

package org.eclipse.draw2d.examples.awt;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.AWTGraphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;

import javax.swing.JPanel;

/**
 * A simple example showing how to use Draw2d with AWT and SWT.
 */
public class AWTExample {

	public static void main(String[] args) {
		Shell shell = new Shell();
		shell.setLayout(new FillLayout());

		IFigure figure = new Label("Hello World"); //$NON-NLS-1$
		figure.setBounds(new Rectangle(0, 50, 100, 50));

		Composite composite1 = new Composite(shell, SWT.NONE);
		composite1.addPaintListener(event -> {
			SWTGraphics graphics = new SWTGraphics(event.gc);
			figure.paint(graphics);
			graphics.dispose();
		});

		Composite composite2 = new Composite(shell, SWT.EMBEDDED);
		Frame frame = SWT_AWT.new_Frame(composite2);
		frame.add(new JPanel() {
			@Override
			public void paint(Graphics g) {
				AWTGraphics graphics = new AWTGraphics((Graphics2D) g);
				figure.paint(graphics);
				g.dispose();
			}
		});

		shell.setText("Draw2d"); //$NON-NLS-1$
		shell.setSize(200, 100);
		shell.open();

		Display d = shell.getDisplay();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}

	}
}
