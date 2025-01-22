/*******************************************************************************
 * Copyright 2005, 2025 CHISEL Group, University of Victoria, Victoria, BC, Canada.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *     Mateusz Matela <mateusz.matela@gmail.com> - [tree] Add Space Tree support to Graph widget - https://bugs.eclipse.org/bugs/show_bug.cgi?id=277534
 *******************************************************************************/
package org.eclipse.zest.examples.layouts;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.zest.core.widgets.DefaultSubgraph;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.algorithms.SpaceTreeLayoutAlgorithm;

/**
 * This example shows how SpringLayoutAlgorithm reacts to graph structure
 * related events, automatically rebuilding trees every time a new connection is
 * added.
 */
public class SpaceTreeBuilding {
	private static GraphNode parentNode = null;
	private static GraphNode childNode = null;

	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("GraphSnippet1");
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		final Graph g = new Graph(shell, SWT.NONE);

		hookMenu(g);

		SpaceTreeLayoutAlgorithm spaceTreeLayoutAlgorithm = new SpaceTreeLayoutAlgorithm();
		g.setLayoutAlgorithm(spaceTreeLayoutAlgorithm, false);
		g.setExpandCollapseManager(spaceTreeLayoutAlgorithm.getExpandCollapseManager());

		g.setSubgraphFactory(new DefaultSubgraph.LabelSubgraphFactory());

		for (int i = 0; i < 20; i++) {
			GraphNode graphNode = new GraphNode(g, SWT.NONE);
			graphNode.setText("" + i);
		}

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

	private static void tryToAddConnection(Graph g) {
		if (parentNode != null && childNode != null) {
			new GraphConnection(g, SWT.NONE, parentNode, childNode);
			parentNode = childNode = null;
		}
	}

	private static void hookMenu(final Graph g) {
		MenuManager menuMgr = new MenuManager("#PopupMenu");

		Action parentAction = new Action() {
			@Override
			public void run() {
				List<? extends GraphItem> selection = g.getSelection();
				if (!selection.isEmpty()) {
					GraphNode selected = (GraphNode) selection.get(0);
					parentNode = selected;
					tryToAddConnection(g);
				}
			}
		};
		parentAction.setText("use as parent");
		menuMgr.add(parentAction);

		Action childAction = new Action() {
			@Override
			public void run() {
				List<? extends GraphItem> selection = g.getSelection();
				if (!selection.isEmpty()) {
					GraphNode selected = (GraphNode) selection.get(0);
					childNode = selected;
					tryToAddConnection(g);
				}
			}
		};
		childAction.setText("use as child");
		menuMgr.add(childAction);

		Action expandAction = new Action() {
			@Override
			public void run() {
				List<? extends GraphItem> selection = g.getSelection();
				if (!selection.isEmpty()) {
					GraphNode selected = (GraphNode) selection.get(0);
					g.setExpanded(selected, true);
				}
			}
		};
		expandAction.setText("expand");
		menuMgr.add(expandAction);

		Action collapseAction = new Action() {
			@Override
			public void run() {
				List<? extends GraphItem> selection = g.getSelection();
				if (!selection.isEmpty()) {
					GraphNode selected = (GraphNode) selection.get(0);
					g.setExpanded(selected, false);
				}
			}
		};
		collapseAction.setText("collapse");
		menuMgr.add(collapseAction);

		g.setMenu(menuMgr.createContextMenu(g));
	}
}
