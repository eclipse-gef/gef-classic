/*******************************************************************************
 * Copyright (c) 2003, 2025 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.graph;

import java.util.ArrayList;

/**
 * A list containing nodes.
 *
 * @author hudsonr
 * @since 2.1.2
 */
public class NodeList extends ArrayList<Node> {

	/**
	 * Constructs an empty NodeList.
	 */
	public NodeList() {
	}

	/**
	 * Constructs a NodeList with the elements from the specified list.
	 *
	 * @param list the list whose elements are to be added to this list
	 */
	public NodeList(NodeList list) {
		super(list);
	}

	void adjustRank(int delta) {
		if (delta == 0) {
			return;
		}
		for (int i = 0; i < size(); i++) {
			get(i).rank += delta;
		}
	}

	@SuppressWarnings("removal")
	void resetSortValues() {
		for (int i = 0; i < size(); i++) {
			get(i).sortValue = 0.0;
		}
	}

	void resetIndices() {
		for (int i = 0; i < size(); i++) {
			get(i).index = 0;
		}
	}

	void normalizeRanks() {
		int minRank = Integer.MAX_VALUE;
		for (int i = 0; i < size(); i++) {
			minRank = Math.min(minRank, get(i).rank);
		}
		adjustRank(-minRank);
	}

	/**
	 * Returns the Node at the given index.
	 *
	 * @param index the index
	 * @return the node at a given index
	 *
	 * @deprecated use Use {@link #get(int)} instead.
	 */
	@Deprecated(since = "3.14", forRemoval = true)
	public Node getNode(int index) {
		return super.get(index);
	}

	void resetFlags() {
		for (int i = 0; i < size(); i++) {
			get(i).flag = false;
		}
	}

}
