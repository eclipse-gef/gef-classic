/*******************************************************************************
 * Copyright 2012, 2024 Fabian Steeg and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Fabian Steeg
 ******************************************************************************/
package org.eclipse.zest.examples.layouts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.algorithms.SugiyamaLayoutAlgorithm;

/**
 * Sample usage for the {@link SugiyamaLayoutAlgorithm}.
 *
 * @author Fabian Steeg
 */
public class SugiyamaLayoutExample {

	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		Graph g = new Graph(shell, SWT.NONE);
		g.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);

		GraphNode coal = new GraphNode(g, SWT.NONE);
		coal.setText("Coal");
		GraphNode ore = new GraphNode(g, SWT.NONE);
		ore.setText("Ore");
		GraphNode stone = new GraphNode(g, SWT.NONE);
		stone.setText("Stone");
		GraphNode metal = new GraphNode(g, SWT.NONE);
		metal.setText("Metal");
		GraphNode concrete = new GraphNode(g, SWT.NONE);
		concrete.setText("Concrete");
		GraphNode machine = new GraphNode(g, SWT.NONE);
		machine.setText("Machine");
		GraphNode building = new GraphNode(g, SWT.NONE);
		building.setText("Building");

		new GraphConnection(g, SWT.NONE, coal, metal);
		new GraphConnection(g, SWT.NONE, coal, concrete);
		new GraphConnection(g, SWT.NONE, metal, machine);
		new GraphConnection(g, SWT.NONE, metal, building);
		new GraphConnection(g, SWT.NONE, concrete, building);
		new GraphConnection(g, SWT.NONE, ore, metal);
		new GraphConnection(g, SWT.NONE, stone, concrete);

		g.setLayoutAlgorithm(new SugiyamaLayoutAlgorithm(), true);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}

	}
}
