/*******************************************************************************
 * Copyright 2005-2007, 2025, CHISEL Group, University of Victoria, Victoria,
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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.examples.Messages;

/**
 * This snippet demonstrates a self loop with a label.
 *
 * @author Ian Bull
 *
 */
public class GraphSnippet9 {
	private static Graph graph;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Shell shell = new Shell();
		Display display = shell.getDisplay();
		shell.setText(Messages.GraphSnippet9_Title);
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		graph = new Graph(shell, SWT.NONE);

		GraphNode a = new GraphNode(graph, SWT.NONE);
		a.setText(Messages.Root);
		GraphConnection connection = new GraphConnection(graph, SWT.NONE, a, a);
		connection.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		connection.setText(Messages.GraphSnippet9_Connection);
		a.setLocation(100, 100);

		shell.open();
		while (!shell.isDisposed()) {
			while (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
