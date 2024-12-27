/*******************************************************************************
 * Copyright (c) 2009-2010, 2024 Mateusz Matela and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.zest.core.widgets;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.zest.layouts.interfaces.ConnectionLayout;
import org.eclipse.zest.layouts.interfaces.ContextListener;
import org.eclipse.zest.layouts.interfaces.ExpandCollapseManager;
import org.eclipse.zest.layouts.interfaces.GraphStructureListener;
import org.eclipse.zest.layouts.interfaces.LayoutContext;
import org.eclipse.zest.layouts.interfaces.NodeLayout;

/**
 * <p>
 * An {@link ExpandCollapseManager} specialized for Directed Acyclic Graphs. It
 * works correctly only when all connections are directed (and of course nodes
 * form an acyclic graph). It's supposed to be used with
 * {@link InternalLayoutContext}.
 * </p>
 * <p>
 * When a node is collapsed, all its outgoing connections are hidden and these
 * successors that have no visible incoming nodes are pruned. When a node is
 * expanded, all its successors are unpruned and connections pointing to them
 * are shown.
 * </p>
 * <p>
 * <b>NOTE:</b> A <code>Graph</code> using this manager should use
 * {@link DefaultSubgraph}, which doesn't show any information about subgraphs
 * in the graph. That's because for this manager it doesn't matter which
 * subgraph a node belongs to (each pruning creates a new subgraph). Also, this
 * manager adds a label to each collapsed node showing number of its successors.
 * </p>
 * One instance of this class can serve only one instance of <code>Graph</code>.
 *
 * @since 1.14
 */
public class DAGExpandCollapseManager implements ExpandCollapseManager {

	private InternalLayoutContext context;

	private final Set<NodeLayout> expandedNodes = new HashSet<>();

	private final Set<NodeLayout> nodesToPrune = new HashSet<>();

	private final Set<NodeLayout> nodesToUnprune = new HashSet<>();

	private final Set<NodeLayout> nodesToUpdate = new HashSet<>();

	private boolean cleanLayoutScheduled = false;

	@Override
	public void initExpansion(final LayoutContext context2) {
		if (!(context2 instanceof InternalLayoutContext)) {
			throw new RuntimeException(
					"This manager works only with org.eclipse.zest.core.widgets.InternalLayoutContext"); //$NON-NLS-1$
		}
		context = (InternalLayoutContext) context2;

		context.addGraphStructureListener(new GraphStructureListener() {
			@Override
			public boolean nodeRemoved(LayoutContext context, NodeLayout node) {
				if (isExpanded(node)) {
					collapse(node);
				}
				flushChanges(false, true);
				return false;
			}

			@Override
			public boolean nodeAdded(LayoutContext context, NodeLayout node) {
				resetState(node);
				flushChanges(false, true);
				return false;
			}

			@Override
			public boolean connectionRemoved(LayoutContext context, ConnectionLayout connection) {
				NodeLayout target = connection.getTarget();
				if (!isExpanded(target) && target.getIncomingConnections().length == 0) {
					expand(target);
				}
				flushChanges(false, true);
				return false;
			}

			@Override
			public boolean connectionAdded(LayoutContext context, ConnectionLayout connection) {
				resetState(connection.getTarget());
				updateNodeLabel(connection.getSource());
				flushChanges(false, true);
				return false;
			}

		});

		context.addContextListener(new ContextListener.Stub() {
			@Override
			public void backgroundEnableChanged(LayoutContext context) {
				flushChanges(false, false);
			}
		});
	}

	@Override
	public boolean canCollapse(LayoutContext context, NodeLayout node) {
		return isExpanded(node) && !node.isPruned() && node.getOutgoingConnections().length > 0;
	}

	@Override
	public boolean canExpand(LayoutContext context, NodeLayout node) {
		return !isExpanded(node) && !node.isPruned() && node.getOutgoingConnections().length > 0;
	}

	private void collapseAllConnections(NodeLayout node) {
		ConnectionLayout[] outgoingConnections = node.getOutgoingConnections();
		for (ConnectionLayout outgoingConnection : outgoingConnections) {
			outgoingConnection.setVisible(false);
		}
		flushChanges(true, true);
	}

	private void expandAllConnections(NodeLayout node) {
		ConnectionLayout[] outgoingConnections = node.getOutgoingConnections();
		for (ConnectionLayout outgoingConnection : outgoingConnections) {
			outgoingConnection.setVisible(true);
		}
		flushChanges(true, true);
	}

	@Override
	public void setExpanded(LayoutContext context, NodeLayout node, boolean expanded) {

		// if (isExpanded(node) == expanded)
		// return;
		if (expanded) {
			if (canExpand(context, node)) {
				expand(node);
			}
			expandAllConnections(node);
		} else {
			if (canCollapse(context, node)) {
				collapse(node);
			}
			collapseAllConnections(node);
		}
		flushChanges(true, true);
	}

	private void expand(NodeLayout node) {
		setExpanded(node, true);
		NodeLayout[] successingNodes = node.getSuccessingNodes();
		for (NodeLayout successingNode : successingNodes) {
			unpruneNode(successingNode);
		}
		updateNodeLabel(node);
	}

	private void collapse(NodeLayout node) {
		if (!isExpanded(node)) {
			return;
		}
		setExpanded(node, false);
		NodeLayout[] successors = node.getSuccessingNodes();
		for (NodeLayout successor : successors) {
			checkPruning(successor);
			if (isPruned(successor)) {
				collapse(successor);
			}
		}
		updateNodeLabel(node);
	}

	private void checkPruning(NodeLayout node) {
		boolean prune = true;
		NodeLayout[] predecessors = node.getPredecessingNodes();
		for (NodeLayout predecessor : predecessors) {
			if (isExpanded(predecessor)) {
				prune = false;
				break;
			}
		}
		if (prune) {
			pruneNode(node);
		} else {
			unpruneNode(node);
		}
	}

	/**
	 * By default nodes at the top (having no predecessors) are expanded. The rest
	 * are collapsed and pruned if they don't have any expanded predecessors
	 *
	 * @param target
	 */
	private void resetState(NodeLayout node) {
		NodeLayout[] predecessors = node.getPredecessingNodes();
		if (predecessors.length == 0) {
			expand(node);
		} else {
			collapse(node);
			checkPruning(node);
		}
	}

	/**
	 * If given node belongs to a layout context using
	 * {@link PrunedSuccessorsSubgraph}, update of the nodes's label is forced.
	 *
	 * @param node node to update
	 */
	private void updateNodeLabel(NodeLayout node) {
		nodesToUpdate.add(node);
	}

	private static void updateNodeLabel2(InternalNodeLayout node) {
		SubgraphFactory subgraphFactory = node.getOwnerLayoutContext().getSubgraphFactory();
		if (subgraphFactory instanceof DefaultSubgraph.PrunedSuccessorsSubgraphFactory) {
			((DefaultSubgraph.PrunedSuccessorsSubgraphFactory) subgraphFactory).updateLabelForNode(node);
		}
	}

	private void pruneNode(NodeLayout node) {
		if (isPruned(node)) {
			return;
		}
		nodesToUnprune.remove(node);
		nodesToPrune.add(node);
	}

	private void unpruneNode(NodeLayout node) {
		if (!isPruned(node)) {
			return;
		}
		nodesToPrune.remove(node);
		nodesToUnprune.add(node);
	}

	private boolean isPruned(NodeLayout node) {
		if (nodesToUnprune.contains(node)) {
			return false;
		}
		if (nodesToPrune.contains(node)) {
			return true;
		}
		return node.isPruned();
	}

	private void flushChanges(boolean force, boolean clean) {
		cleanLayoutScheduled = cleanLayoutScheduled || clean;
		if (!force && !context.isBackgroundLayoutEnabled()) {
			return;
		}

		for (Object element : nodesToUnprune) {
			NodeLayout node = (NodeLayout) element;
			node.prune(null);
		}
		nodesToUnprune.clear();

		if (!nodesToPrune.isEmpty()) {
			context.createSubgraph(nodesToPrune.toArray(new NodeLayout[nodesToPrune.size()]));
			nodesToPrune.clear();
		}

		for (Object element : nodesToUpdate) {
			InternalNodeLayout node = (InternalNodeLayout) element;
			updateNodeLabel2(node);
		}
		nodesToUpdate.clear();

		(context).applyLayout(cleanLayoutScheduled);
		cleanLayoutScheduled = false;
		context.flushChanges(true);
	}

	private boolean isExpanded(NodeLayout node) {
		return expandedNodes.contains(node);
	}

	private void setExpanded(NodeLayout node, boolean expanded) {
		if (expanded) {
			expandedNodes.add(node);
		} else {
			expandedNodes.remove(node);
		}
	}
}
