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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShiftAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

public class NestedGraphSnippet {
	private static Graph g;
	private static Image image1;
	private static Image classImage;

	public static void createContainer(Graph g) {
		GraphContainer a = new GraphContainer(g, SWT.NONE);
		a.setText(Messages.NestedGraphSnippet_Container1);
		a.setImage(classImage);
		int r = (int) ((Math.random() * 3) + 1);
		r = 2;
		populateContainer(a, g, r, true);
		for (int i = 0; i < 4; i++) {
			GraphContainer b = new GraphContainer(g, SWT.NONE);
			b.setText(Messages.NestedGraphSnippet_Container2);
			b.setImage(classImage);
			r = (int) ((Math.random() * 3) + 1);
			r = 2;
			populateContainer(b, g, r, false);
			new GraphConnection(g, SWT.NONE, a, b);
			for (int j = 0; j < 4; j++) {
				GraphContainer c = new GraphContainer(g, SWT.NONE);
				c.setText(Messages.NestedGraphSnippet_Container3);
				c.setImage(classImage);
				r = (int) ((Math.random() * 3) + 1);
				r = 2;
				populateContainer(c, g, r, true);
				new GraphConnection(g, SWT.NONE, b, c);
			}
		}
	}

	public static void populateContainer(GraphContainer c, Graph g, int number, boolean radial) {
		GraphNode a = new GraphNode(c, ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT);
		a.setText(Messages.NestedGraphSnippet_Node1);
		a.setImage(classImage);
		for (int i = 0; i < 4; i++) {
			GraphNode b = new GraphNode(c, ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT);
			b.setText(Messages.NestedGraphSnippet_Node2);
			b.setImage(classImage);
			new GraphConnection(g, SWT.NONE, a, b);
			for (int j = 0; j < 4; j++) {
				GraphNode d = new GraphNode(c, ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT);
				d.setText(Messages.NestedGraphSnippet_Node3);
				d.setImage(classImage);
				new GraphConnection(g, SWT.NONE, b, d);
				if (number > 2) {
					for (int k = 0; k < 4; k++) {
						GraphNode e = new GraphNode(c, ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT);
						e.setText(Messages.NestedGraphSnippet_Node4);
						e.setImage(classImage);
						new GraphConnection(g, SWT.NONE, d, e);
						if (number > 3) {
							for (int l = 0; l < 4; l++) {
								GraphNode f = new GraphNode(c, ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT);
								f.setText(Messages.NestedGraphSnippet_Node5);
								f.setImage(classImage);
								new GraphConnection(g, SWT.NONE, e, f);
							}
						}
					}
				}
			}
		}
		if (number == 1) {
			c.setScale(0.75);
		} else if (number == 2) {
			c.setScale(0.50);
		} else {
			c.setScale(0.25);
		}
		if (radial) {
			c.setLayoutAlgorithm(new RadialLayoutAlgorithm(), true);
		} else {
			c.setLayoutAlgorithm(new TreeLayoutAlgorithm(), true);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create the shell
		image1 = new Image(Display.getDefault(), NestedGraphSnippet.class.getResourceAsStream("package_obj.gif")); //$NON-NLS-1$
		classImage = new Image(Display.getDefault(), NestedGraphSnippet.class.getResourceAsStream("class_obj.gif")); //$NON-NLS-1$

		Shell shell = new Shell();
		Display d = shell.getDisplay();
		shell.setText(Messages.NestedGraphSnippet_Title);
		shell.setLayout(new FillLayout());
		shell.setSize(500, 800);

		g = new Graph(shell, SWT.NONE);
		createContainer(g);

		CompositeLayoutAlgorithm compositeLayoutAlgorithm = new CompositeLayoutAlgorithm(
				new LayoutAlgorithm[] { new GridLayoutAlgorithm(), new HorizontalShiftAlgorithm() });
		// g.setLayoutAlgorithm(new
		// GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		g.setLayoutAlgorithm(compositeLayoutAlgorithm, true);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
		image1.dispose();
	}
}
