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
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.zest.examples.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;

/**
 * This snippet creates a graph with 80*3 nodes (240 nodes). Only the icons are
 * shown for the nodes, but if you mouse over the node you get the entire text.
 *
 * @author Ian Bull
 *
 */
public class GraphSnippet6 {
	private static Graph g;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Shell shell = new Shell();
		Display d = shell.getDisplay();
		shell.setText(Messages.GraphSnippet6_Title);
		Image image1 = Display.getDefault().getSystemImage(SWT.ICON_INFORMATION);
		Image image2 = Display.getDefault().getSystemImage(SWT.ICON_WARNING);
		Image image3 = Display.getDefault().getSystemImage(SWT.ICON_ERROR);
		shell.setLayout(new FillLayout());
		shell.setSize(800, 800);

		g = new Graph(shell, SWT.NONE);
		g.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		for (int i = 0; i < 80; i++) {
			GraphNode n1 = new GraphNode(g, SWT.NONE);
			n1.setNodeStyle(ZestStyles.NODES_HIDE_TEXT | ZestStyles.NODES_FISHEYE);
			n1.setText(Messages.Information);
			n1.setImage(image1);
			GraphNode n2 = new GraphNode(g, SWT.NONE);
			n2.setNodeStyle(ZestStyles.NODES_HIDE_TEXT | ZestStyles.NODES_FISHEYE);
			n2.setText(Messages.Warning);
			n2.setImage(image2);
			GraphNode n3 = new GraphNode(g, SWT.NONE);
			n3.setNodeStyle(ZestStyles.NODES_HIDE_TEXT | ZestStyles.NODES_FISHEYE);
			n3.setText(Messages.Error);
			n3.setImage(image3);
			new GraphConnection(g, SWT.NONE, n1, n2);
			new GraphConnection(g, SWT.NONE, n2, n3);
			new GraphConnection(g, SWT.NONE, n3, n3);
		}
		g.setLayoutAlgorithm(new GridLayoutAlgorithm(), true);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}

	}

}
