/*******************************************************************************
 * Copyright 2023, 2024 Sebastian Hollersbacher and others.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors: Sebastian Hollersbacher
 ******************************************************************************/
package org.eclipse.zest.examples.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.HideNodeHelper;
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * This snippet creates a very simple graph with 3 nodes
 *
 * The hide-nodes feature for this graph is set to be enabled and a button is
 * created to reveal all nodes
 *
 * @author Sebastian Hollersbacher
 *
 */
public class GraphSnippet14 {
	private static Graph g;

	public static void main(String[] args) {
		Shell shell = new Shell();
		Display d = shell.getDisplay();
		shell.setText(Messages.GraphSnippet14_Title);
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		g = new Graph(shell, SWT.NONE, true); // enable hide nodes

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

		// example: hide-nodes reveal-all button
		if (g.getHideNodesEnabled()) {
			Button revealAllButton = new Button(Messages.GraphSnippet14_RevealAll);
			revealAllButton.setBounds(new Rectangle(new Point(0, 0), revealAllButton.getPreferredSize()));
			revealAllButton.addActionListener(event -> {
				for (GraphNode node : g.getNodes()) {
					HideNodeHelper hideNodeHelper = node.getHideNodeHelper();
					if (hideNodeHelper != null) {
						node.setVisible(true);
						hideNodeHelper.resetCounter();
					}
				}
			});
			g.getRootLayer().add(revealAllButton);
		}

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

}
