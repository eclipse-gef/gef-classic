/*******************************************************************************
 * Copyright 2008, 2026 CHISEL Group, University of Victoria, Victoria,
 *                      BC, Canada and others.
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
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.interfaces.EntityLayout;

/**
 * This snippet shows how to create a custom layout. This layout simply lays the
 * nodes out vertically on the same Y-Axis as they currently have. All the work
 * is done in the applyLayoutInternal Method.
 *
 * @author irbull
 *
 */
public class CustomLayout {
	private static Graph g;

	public static void main(String[] args) {
		Shell shell = new Shell();
		Display d = shell.getDisplay();
		shell.setText(Messages.CustomLayout_Title);
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

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
		g.setLayoutAlgorithm(new AbstractLayoutAlgorithm() {
			@Override
			public void applyLayout(boolean clean) {
				EntityLayout[] entitiesToLayout = context.getEntities();
				int totalSteps = entitiesToLayout.length;
				double distance = context.getBounds().width / totalSteps;
				double xLocation = 0;
				for (EntityLayout layoutEntity : entitiesToLayout) {
					layoutEntity.setLocation(xLocation, layoutEntity.getLocation().y);
					xLocation += distance;
				}
			}
		}, true);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
