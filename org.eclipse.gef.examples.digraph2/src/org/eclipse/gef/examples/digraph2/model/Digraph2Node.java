/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.gef.examples.digraph2.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.examples.digraph1.model.Digraph1Node;

/**
 * The node model object which describes a node in the directed graph.
 *
 * @author Anthony Hunter
 */
public class Digraph2Node extends Digraph1Node {

	/**
	 * The list of edges that have this node as the source.
	 */
	private final List<Digraph2Edge> sourceEdges;

	/**
	 * The list of edges that have this node as the target.
	 */
	private final List<Digraph2Edge> targetEdges;

	/**
	 * Constructor for a Digraph2Node.
	 *
	 * @param aNumber the node number.
	 */
	public Digraph2Node(int aNumber) {
		super(aNumber);
		this.sourceEdges = new ArrayList<Digraph2Edge>();
		this.targetEdges = new ArrayList<Digraph2Edge>();
	}

	/**
	 * Add the edge to the list of source edges.
	 *
	 * @param sourceEdge the edge to add.
	 */
	public void addSourceEdge(Digraph2Edge sourceEdge) {
		this.sourceEdges.add(sourceEdge);
	}

	/**
	 * Add the edge to the list of target edges.
	 *
	 * @param targetEdge the edge to add.
	 */
	public void addTargetEdge(Digraph2Edge targetEdge) {
		this.targetEdges.add(targetEdge);
	}

	/**
	 * Get the list of edges that have this node as the source.
	 *
	 * @return the list of edges that have this node as the source.
	 */
	public List<Digraph2Edge> getSourceEdges() {
		return this.sourceEdges;
	}

	/**
	 * Get the list of edges that have this node as the target.
	 *
	 * @return the list of edges that have this node as the target.
	 */
	public List<Digraph2Edge> getTargetEdges() {
		return this.targetEdges;
	}

	/**
	 * Remove the edge to the list of source edges.
	 *
	 * @param sourceEdge the edge to remove.
	 */
	public void removeSourceEdge(Digraph2Edge sourceEdge) {
		this.sourceEdges.remove(sourceEdge);
	}

	/**
	 * Remove the edge to the list of target edges.
	 *
	 * @param targetEdge the edge to remove.
	 */
	public void removeTargetEdge(Digraph2Edge targetEdge) {
		this.targetEdges.remove(targetEdge);
	}
}