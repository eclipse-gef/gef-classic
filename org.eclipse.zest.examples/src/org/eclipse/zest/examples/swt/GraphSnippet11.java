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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

import org.eclipse.draw2d.ColorConstants;

/**
 *
 * This snippet shows how to create a curved connection using Zest.
 *
 * @author Ian Bull
 *
 */
public class GraphSnippet11 {
	private static Graph g;

	public static void createConnection(Graph g, GraphNode n1, GraphNode n2, Color color, int curve) {
		GraphConnection connection = new GraphConnection(g, SWT.NONE, n1, n2);
		connection.setLineColor(color);
		connection.setCurveDepth(curve);
		connection.setLineWidth(1);
	}

	public static void main(String[] args) {
		Shell shell = new Shell();
		Display d = shell.getDisplay();
		shell.setText(Messages.GraphSnippet11_Title);
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		g = new Graph(shell, SWT.NONE);
		GraphNode n = new GraphNode(g, SWT.NONE);
		n.setText(Messages.GraphSnippet11_Node1);
		GraphNode n2 = new GraphNode(g, SWT.NONE);
		n2.setText(Messages.GraphSnippet11_Node2);
		createConnection(g, n, n2, ColorConstants.darkGreen, 20);
		createConnection(g, n, n2, ColorConstants.darkGreen, -20);
		createConnection(g, n, n2, ColorConstants.darkBlue, 40);
		createConnection(g, n, n2, ColorConstants.darkBlue, -40);
		createConnection(g, n, n2, ColorConstants.darkGray, 60);
		createConnection(g, n, n2, ColorConstants.darkGray, -60);
		createConnection(g, n, n2, ColorConstants.black, 0);
		g.setLayoutAlgorithm(new SpringLayoutAlgorithm(), true);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

}
