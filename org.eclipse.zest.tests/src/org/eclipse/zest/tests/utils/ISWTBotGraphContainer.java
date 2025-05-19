/*******************************************************************************
 * Copyright (c) 2025 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/
package org.eclipse.zest.tests.utils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;

/**
 * This interface is used to extract common methods accessible by both the
 * {@link Graph} and its {@link GraphContainer}s.
 */
public interface ISWTBotGraphContainer {
	/**
	 * Utility method that returns the nodes contained by this graph and/or
	 * subgraph. Should never called by clients.
	 *
	 * @return An unmodifiable list of SWT graph nodes.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	List<? extends GraphNode> internalGetNodes();

	/**
	 * Utility method that converts {@link GraphNode}s into {@link SWTBotGraphNode}s
	 * and {@link GraphContainer}s into {@link SWTBotGraphContainer}s and
	 * {@link GraphItem}s into {@link SWTBotGraphItem}s.
	 *
	 * @param item An SWT graph item.
	 * @return The corresponding SWTBot graph item.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	static SWTBotGraphItem internalConvert(GraphItem item) {
		if (item instanceof GraphContainer container) {
			return new SWTBotGraphContainer(container);
		}
		if (item instanceof GraphNode node) {
			return new SWTBotGraphNode(node);
		}
		return new SWTBotGraphItem(item);
	}

	/**
	 * @param i The position of the graph node.
	 * @return The graph node at that position
	 * @throws IndexOutOfBoundsException If the index is invalid.
	 */
	default SWTBotGraphNode getNode(int i) throws IndexOutOfBoundsException {
		return getNodes().get(i);
	}

	/**
	 * Returns the first graph node by comparing the given name against
	 * {@link GraphNode#getText()}.
	 *
	 * @param text The name to compare against-
	 * @return The first node with the given name.
	 * @throws NoSuchElementException, if no matching node is found.
	 */
	default SWTBotGraphNode getNode(String text) throws NoSuchElementException {
		return getNodesStream() //
				.filter(node -> node.getText().equals(text)) //
				.findFirst().orElseThrow();
	}

	/**
	 * @return An unmodifiable list of all nodes contained by this graph.
	 */
	default List<SWTBotGraphNode> getNodes() {
		return getNodesStream().toList();
	}

	private Stream<SWTBotGraphNode> getNodesStream() {
		return internalGetNodes().stream().map(ISWTBotGraphContainer::internalConvert).map(SWTBotGraphNode.class::cast);
	}
}
