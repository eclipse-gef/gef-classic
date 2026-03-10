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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

import org.eclipse.draw2d.IFigure;

/**
 * This snippet shows how to use the findFigureAt to get the figure under the
 * mouse
 *
 * @author Ian Bull
 *
 */
public class GraphSnippet7 {
	private static Graph g;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Shell shell = new Shell();
		Display d = shell.getDisplay();
		shell.setText(Messages.GraphSnippet7_Title);
		shell.setLayout(new GridLayout(2, true));
		shell.setSize(400, 400);

		g = new Graph(shell, SWT.NONE);
		g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

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

		Label cursorLocation = new Label(shell, SWT.NONE);
		cursorLocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		cursorLocation.setText(Messages.bind(Messages.GraphSnippet7_CursorLocation, -1, -1));

		Label figureUnderCursor = new Label(shell, SWT.NONE);
		figureUnderCursor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		figureUnderCursor.setText(Messages.bind(Messages.GraphSnippet7_FigureUnderCursor, (Object) null));

		g.addMouseMoveListener(e -> {
			cursorLocation.setText(Messages.bind(Messages.GraphSnippet7_CursorLocation, e.x, e.y));
			// Get the figure at the current mouse location
			IFigure o = g.getFigureAt(e.x, e.y);
			figureUnderCursor.setText(Messages.bind(Messages.GraphSnippet7_FigureUnderCursor, o));
		});

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
