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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

public class NestedGraphSnippet2 {
	private static Graph g;

	public static void main(String[] args) {
		// Create the shell
		Shell shell = new Shell();
		Display d = shell.getDisplay();
		shell.setText(Messages.NestedGraphSnippet2_Title);
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		g = new Graph(shell, SWT.NONE);

		/* Machines */
		GraphContainer machine1 = new GraphContainer(g, SWT.NONE);
		machine1.setText(Messages.NestedGraphSnippet2_Container1);
		GraphContainer machine2 = new GraphContainer(g, SWT.NONE);
		machine2.setText(Messages.NestedGraphSnippet2_Container2);
		GraphContainer machine3 = new GraphContainer(g, SWT.NONE);
		machine3.setText(Messages.NestedGraphSnippet2_Container3);

		/* Network */
		GraphConnection networkConnection = new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, machine1, machine2);
		networkConnection.setText(Messages.NestedGraphSnippet2_Connection);
		new GraphConnection(g, SWT.NONE, machine2, machine3);

		/* Containers */
		GraphContainer container1 = new GraphContainer(machine1, SWT.NONE);
		container1.setText(Messages.NestedGraphSnippet2_Container4);
		GraphContainer container2 = new GraphContainer(machine1, SWT.NONE);
		container2.setText(Messages.NestedGraphSnippet2_Container5);

		GraphContainer container3 = new GraphContainer(machine2, SWT.NONE);
		container3.setText(Messages.NestedGraphSnippet2_Container6);
		GraphContainer container4 = new GraphContainer(machine3, SWT.NONE);
		container4.setText(Messages.NestedGraphSnippet2_Container7);

		/* Objects */
		GraphNode object1 = new GraphNode(container1, ZestStyles.NODES_FISHEYE);
		object1.setText(Messages.NestedGraphSnippet2_Node1);
		GraphNode object2 = new GraphNode(container1, ZestStyles.NODES_FISHEYE);
		object2.setText(Messages.NestedGraphSnippet2_Node2);
		GraphNode object3 = new GraphNode(container2, ZestStyles.NODES_FISHEYE);
		object3.setText(Messages.NestedGraphSnippet2_Node3);
		GraphNode object4 = new GraphNode(container3, ZestStyles.NODES_FISHEYE);
		object4.setText(Messages.NestedGraphSnippet2_Node4);
		GraphNode object5 = new GraphNode(container4, ZestStyles.NODES_FISHEYE);
		object5.setText(Messages.NestedGraphSnippet2_Node5);

		/* Connections */
		new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, object1, object2);
		new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, object2, object3);
		new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, object3, object4);
		new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, object4, object5);

		container1.setLayoutAlgorithm(new GridLayoutAlgorithm(), true);
		container2.setLayoutAlgorithm(new GridLayoutAlgorithm(), true);
		container3.setLayoutAlgorithm(new GridLayoutAlgorithm(), true);
		container3.setLayoutAlgorithm(new GridLayoutAlgorithm(), true);
		g.setLayoutAlgorithm(new SpringLayoutAlgorithm(), true);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
