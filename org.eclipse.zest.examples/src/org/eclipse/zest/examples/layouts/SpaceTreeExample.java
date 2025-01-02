package org.eclipse.zest.examples.layouts;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.zest.core.widgets.DefaultSubgraph;
import org.eclipse.zest.core.widgets.DefaultSubgraph.TriangleSubgraphFactory;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.algorithms.SpaceTreeLayoutAlgorithm;

import org.eclipse.draw2d.ColorConstants;

public class SpaceTreeExample {

	static Graph g;

	static GraphNode source;

	static GraphNode target;

	static boolean changesSeries = false;

	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("GraphSnippet1");
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		g = new Graph(shell, SWT.NONE);
		g.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		g.setNodeStyle(ZestStyles.NODES_FISHEYE);

		TriangleSubgraphFactory factory = new DefaultSubgraph.TriangleSubgraphFactory();
		factory.setColor(ColorConstants.green);

		g.setSubgraphFactory(factory);
		SpaceTreeLayoutAlgorithm spaceTreeLayoutAlgorithm = new SpaceTreeLayoutAlgorithm();
		g.setExpandCollapseManager(spaceTreeLayoutAlgorithm.getExpandCollapseManager());

		g.setLayoutAlgorithm(spaceTreeLayoutAlgorithm, false);

		// g.setExpandCollapseManager(new DAGExpandCollapseManager());
		// g.setLayoutAlgorithm(new TreeLayoutAlgorithm(), true);
		createTree(g, "!", 5, 5);

		hookMenu(g);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

	private static GraphNode createTree(Graph g, String rootTitle, int depth, int breadth) {
		GraphNode root = new GraphNode(g, SWT.NONE);
		root.setText(rootTitle);
		if (depth > 0) {
			for (int i = 0; i < breadth; i++) {
				GraphNode child = createTree(g, rootTitle + i, depth - 1 - i, breadth - i);
				new GraphConnection(g, SWT.NONE, root, child);
			}
		}
		return root;
	}

	private static void hookMenu(final Graph g) {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(SpaceTreeExample::fillContextMenu);
		g.setMenu(menuMgr.createContextMenu(g));
	}

	private static void fillContextMenu(IMenuManager menuMgr) {
		List<? extends GraphItem> selection = g.getSelection();
		if (selection.size() == 1) {
			if (selection.get(0) instanceof GraphNode) {
				final GraphNode node = (GraphNode) selection.get(0);
				if (g.canExpand(node)) {
					Action expandAction = new Action() {
						@Override
						public void run() {
							g.setExpanded(node, true);
						}
					};
					expandAction.setText("expand");
					menuMgr.add(expandAction);
				}
				if (g.canCollapse(node)) {
					Action collapseAction = new Action() {
						@Override
						public void run() {
							g.setExpanded(node, false);
						}
					};
					collapseAction.setText("collapse");
					menuMgr.add(collapseAction);
				}
				Action disposeAction = new Action() {
					@Override
					public void run() {
						node.dispose();
					}
				};
				disposeAction.setText("dispose");
				menuMgr.add(disposeAction);

				Action asSourceAction = new Action() {
					@Override
					public void run() {
						source = node;
						addConnection();
					}
				};
				asSourceAction.setText("use as source");
				menuMgr.add(asSourceAction);

				Action asTargetAction = new Action() {
					@Override
					public void run() {
						target = node;
						addConnection();
					}
				};
				asTargetAction.setText("use as target");
				menuMgr.add(asTargetAction);
			}
			if (selection.get(0) instanceof GraphConnection) {
				final GraphConnection connection = (GraphConnection) selection.get(0);
				Action removeAction = new Action() {
					@Override
					public void run() {
						connection.dispose();
					}
				};
				removeAction.setText("remove");
				menuMgr.add(removeAction);
			}
		}
		if (selection.size() == 0) {
			Action addNode = new Action() {
				@Override
				public void run() {
					GraphNode n = new GraphNode(g, SWT.NONE);
					n.setText("new!");
				}
			};
			addNode.setText("add node");
			menuMgr.add(addNode);

			if (!changesSeries) {
				Action startChangesSeries = new Action() {
					@Override
					public void run() {
						g.setDynamicLayout(false);
						changesSeries = true;
					}
				};
				startChangesSeries.setText("start changes");
				menuMgr.add(startChangesSeries);
			} else {
				Action endChangesSeries = new Action() {
					@Override
					public void run() {
						g.setDynamicLayout(true);
						changesSeries = false;
					}
				};
				endChangesSeries.setText("end changes");
				menuMgr.add(endChangesSeries);
			}
		}
	}

	private static void addConnection() {
		if (source != null && target != null) {
			new GraphConnection(g, SWT.NONE, source, target);
			source = target = null;
		}
	}
}
