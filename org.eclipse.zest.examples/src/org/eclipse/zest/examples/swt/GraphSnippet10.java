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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

/**
 *
 * This snippet shows how to create a curved connection using Zest. Each time
 * the Button is clicked, the curve changes.
 *
 * @author Ian Bull
 *
 */
public class GraphSnippet10 {
	private static Graph g;

	public static void main(String[] args) {
		Shell shell = new Shell();
		Display d = shell.getDisplay();
		shell.setText(Messages.GraphSnippet10_Title);
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		g = new Graph(shell, SWT.NONE);

		GraphNode n = new GraphNode(g, SWT.NONE);
		n.setText(Messages.Paper);
		n.setBorderColor(org.eclipse.draw2d.ColorConstants.yellow);
		n.setBorderWidth(3);
		GraphNode n2 = new GraphNode(g, SWT.NONE);
		n2.setText(Messages.Rock);
		GraphNode n3 = new GraphNode(g, SWT.NONE);
		n3.setText(Messages.Scissors);
		final GraphConnection connection = new GraphConnection(g, SWT.NONE, n, n2);
		connection.setLineWidth(3);
		new GraphConnection(g, SWT.NONE, n2, n3);
		new GraphConnection(g, SWT.NONE, n3, n);
		g.setLayoutAlgorithm(new SpringLayoutAlgorithm(), true);

		Button button = new Button(shell, SWT.PUSH);
		button.setText(Messages.GraphSnippet10_ChangeCurve);
		button.addSelectionListener(new SelectionAdapter() {
			int count = 0;

			@Override
			public void widgetSelected(SelectionEvent e) {
				count = ++count % 16;
				if (count > 8) {
					connection.setCurveDepth((-16 + count) * 10);
				} else {
					connection.setCurveDepth(count * 10);
				}
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
