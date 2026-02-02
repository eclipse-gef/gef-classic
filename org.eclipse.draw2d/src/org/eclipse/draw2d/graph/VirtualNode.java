/*******************************************************************************
 * Copyright (c) 2003, 2026 IBM Corporation and others.
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

import org.eclipse.draw2d.geometry.Insets;

/**
 * @deprecated virtual nodes of an edge should be cast to Node. This class will
 *             be made package-private after the 2028-03 release.
 * @author Randy Hudson
 * @since 2.1.2
 */
@Deprecated(forRemoval = true, since = "2026-03")
public class VirtualNode extends Node {

	/**
	 * The next node.
	 */
	@Deprecated
	public Node next;

	/**
	 * The previous node.
	 */
	@Deprecated
	public Node prev;

	/**
	 * Constructs a virtual node.
	 *
	 * @deprecated This class is for internal use only.
	 * @param e the edge
	 * @param i the row
	 */
	@Deprecated
	public VirtualNode(Edge e, int i) {
		super(e);
		incoming.add(e);
		outgoing.add(e);
		width = e.getWidth();
		height = 0;
		rank = i;
		setPadding(new Insets(0, e.getPadding(), 0, e.getPadding()));
	}

	/**
	 * Constructor.
	 *
	 * @param o      object
	 * @param parent subgraph
	 */
	@Deprecated
	public VirtualNode(Object o, Subgraph parent) {
		super(o, parent);
	}

	/**
	 * Returns the index of {@link #prev}.
	 *
	 * @return median
	 */
	@Deprecated
	public double medianIncoming() {
		return prev.index;
	}

	/**
	 * Returns the index of {@link #next}.
	 *
	 * @return outgoing
	 */
	@Deprecated
	public double medianOutgoing() {
		return next.index;
	}

	/**
	 * For internal use only. Returns the original edge weight multiplied by the
	 * omega value for the this node and the node on the previous rank.
	 *
	 * @return the weighted weight, or omega
	 */
	@Deprecated
	public int omega() {
		Edge e = (Edge) data;
		if (e.source.rank + 1 < rank && rank < e.target.rank) {
			return 8 * e.weight;
		}
		return 2 * e.weight;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	@Deprecated
	public String toString() {
		if (data instanceof Edge edge) {
			return "VN[" + (edge.vNodes.indexOf(this) + 1) //$NON-NLS-1$
					+ "](" + data + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return super.toString();
	}

}
