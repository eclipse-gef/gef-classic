/*******************************************************************************
 * Copyright 2005-2007, 2024, CHISEL Group, University of Victoria, Victoria,
 *                            BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 ******************************************************************************/
package org.eclipse.zest.examples.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

import org.eclipse.draw2d.SWTGraphics;

/**
 * This snippet creates a very simpl graph where Rock is connected to Paper
 * which is connected to scissors which is connected to rock.
 *
 * The nodes a layed out using a SpringLayout Algorithm, and they can be moved
 * around.
 *
 *
 * @author Ian Bull
 *
 */
public class PaintSnippet {
	private static Graph g;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create the shell
		final Shell shell = new Shell();
		final Display d = shell.getDisplay();
		shell.setText(Messages.PaintSnippet_Title);
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		Button b = new Button(shell, SWT.PUSH);
		b.setText(Messages.PaintSnippet_TakeScreenshot);

		g = new Graph(shell, SWT.NONE);

		GraphNode n = new GraphNode(g, SWT.NONE);
		n.setText(Messages.Paper);
		GraphNode n2 = new GraphNode(g, SWT.NONE);
		n2.setText(Messages.Rock);
		GraphNode n3 = new GraphNode(g, SWT.NONE);
		n3.setText(Messages.Scissors);
		new GraphConnection(g, SWT.NONE, n, n2);
		new GraphConnection(g, SWT.NONE, n2, n3);
		new GraphConnection(g, SWT.NONE, n3, n);
		g.setLayoutAlgorithm(new SpringLayoutAlgorithm(), true);

		b.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				Point size = new Point(g.getContents().getSize().width, g.getContents().getSize().height);
				final Image image = new Image(null, size.x, size.y);
				GC gc = new GC(image);
				SWTGraphics swtGraphics = new SWTGraphics(gc);
				g.getContents().paint(swtGraphics);
				gc.copyArea(image, 0, 0);
				gc.dispose();

				Shell popup = new Shell(shell);
				popup.setText(Messages.PaintSnippet_Popup_Title);
				popup.addListener(SWT.Close, e1 -> image.dispose());

				Canvas canvas = new Canvas(popup, SWT.NONE);
				canvas.setBounds(10, 10, size.x + 10, size.y + 10);
				canvas.addPaintListener(e1 -> e1.gc.drawImage(image, 0, 0));
				popup.pack();
				popup.open();

			}

		});

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
