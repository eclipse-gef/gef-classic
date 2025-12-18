/*******************************************************************************
 * Copyright 2005, 2025 CHISEL Group, University of Victoria, Victoria,
 *                      BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.zest.layouts;

import java.util.List;

/**
 * The LayoutGraph interface defines the methods used to add nodes and edges
 * (relationships).
 *
 * @author Chris
 * @deprecated No longer used in Zest 2.x. This interface will be removed in a
 *             future release.
 * @noextend This interface is not intended to be extended by clients.
 * @noreference This interface is not intended to be referenced by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
@SuppressWarnings({ "rawtypes", "removal" })
@Deprecated(since = "2.0", forRemoval = true)
public interface LayoutGraph {

	/**
	 * Adds a node to this graph.
	 *
	 * @param node The new node.
	 */
	@Deprecated
	public void addEntity(LayoutEntity node);

	/**
	 * Adds the given relationship.
	 *
	 * @param relationship
	 */
	@Deprecated
	public void addRelationship(LayoutRelationship relationship);

	/**
	 * Returns a list of LayoutEntity objects that represent the objects added to
	 * this graph using addNode.
	 *
	 * @return List A List of LayoutEntity objects.
	 */
	@Deprecated
	public List getEntities();

	/**
	 * Returns a list of LayoutRelationship objects that represent the objects added
	 * to this graph using addRelationship.
	 *
	 * @return List A List of LayoutRelationship objects.
	 */
	@Deprecated
	public List getRelationships();

	/**
	 * Determines if the graph is bidirectional.
	 *
	 * @return boolean If the graph is bidirectional.
	 */
	@Deprecated
	public boolean isBidirectional();

}
