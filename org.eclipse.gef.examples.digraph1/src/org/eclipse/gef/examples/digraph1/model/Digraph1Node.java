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

package org.eclipse.gef.examples.digraph1.model;

/**
 * The node model object which describes a node in the directed graph.
 *
 * @author Anthony Hunter
 */
public class Digraph1Node {

	/**
	 * The node knows what node number it is on the graph.
	 */
	private final int number;

	/**
	 * Constructor for a Digraph1Node.
	 *
	 * @param aNumber the node number.
	 */
	public Digraph1Node(int aNumber) {
		this.number = aNumber;
	}

	/**
	 * Get the node number.
	 *
	 * @return the node number.
	 */
	public int getNumber() {
		return this.number;
	}
}