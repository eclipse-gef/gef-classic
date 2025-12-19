/*******************************************************************************
 * Copyright 2005, 2025 CHISEL Group, University of Victoria, Victoria, BC,
 *                      Canada, Johannes Kepler University Linz and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria, Alois Zoitl
 *******************************************************************************/

package org.eclipse.zest.layouts.algorithms.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;

/**
 * Checks for cycles in the given graph.
 *
 * @author Casey Best
 * @deprecated No longer used in Zest 2.x. This class will be removed in a
 *             future release in accordance with the two year deprecation
 *             policy.
 * @noextend This class is not intended to be subclassed by clients.
 * @noreference This class is not intended to be referenced by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@Deprecated(since = "2.0", forRemoval = true)
public class CycleChecker {
	/**
	 * Tests if there is a directed cirlce in the graph formed by the given entities
	 * and relationships.
	 *
	 * @param entities      The entities in the graph to check
	 * @param relationships The relationships in the graph to check
	 * @param cycle         Populated with the cycle encountered, if there is one.
	 * @throws RuntimeException Thrown if entities doesn't contain all of the
	 *                          endpoints for each relationship in relationships
	 * @return <code>true</code> if there is a directed circle. Otherwise,
	 *         <code>false</code>.
	 */
	@Deprecated
	public static boolean hasDirectedCircles(LayoutEntity[] entities, LayoutRelationship[] relationships,
			List<LayoutEntity> cycle) {
		if (!AbstractLayoutAlgorithm.Zest1.verifyInput(entities, relationships)) {
			throw new RuntimeException("The endpoints of the relationships aren't contained in the entities list."); //$NON-NLS-1$
		}

		Map<LayoutEntity, List<LayoutRelationship>> endPoints = new HashMap<>();

		// Initialize the relation(transitive) vector.
		for (LayoutRelationship rel : relationships) {
			// Add the relationship to the source endpoint
			LayoutEntity subject = rel.getSourceInLayout();
			List<LayoutRelationship> rels = endPoints.computeIfAbsent(subject, sub -> new ArrayList<>());
			if (!rels.contains(rel)) {
				rels.add(rel);
			}
		}
		return hasCycle(new ArrayList<>(Arrays.asList(entities)), endPoints, cycle);
	}

	/**
	 * Check passed in nodes for a cycle
	 */
	private static boolean hasCycle(List<LayoutEntity> nodesToCheck,
			Map<LayoutEntity, List<LayoutRelationship>> endPoints, List<LayoutEntity> cycle) {
		while (!nodesToCheck.isEmpty()) {
			LayoutEntity checkNode = nodesToCheck.get(0);
			List<LayoutEntity> checkedNodes = new ArrayList<>();
			if (hasCycle(checkNode, new ArrayList<>(), null, endPoints, checkedNodes, cycle)) {
				return true;
			}
			nodesToCheck.removeAll(checkedNodes);
		}
		return false;
	}

	/**
	 * Checks all the nodes attached to the nodeToCheck node for a cycle. All nodes
	 * checked are placed in nodePathSoFar.
	 *
	 * @returns true if there is a cycle
	 */
	private static boolean hasCycle(LayoutEntity nodeToCheck, List<LayoutEntity> nodePathSoFar,
			LayoutRelationship cameFrom, Map<LayoutEntity, List<LayoutRelationship>> endPoints,
			List<LayoutEntity> nodesChecked, List<LayoutEntity> cycle) {
		if (nodePathSoFar.contains(nodeToCheck)) {
			cycle.addAll(nodePathSoFar);
			cycle.add(nodeToCheck);
			return true;
		}
		nodePathSoFar.add(nodeToCheck);
		nodesChecked.add(nodeToCheck);

		List<LayoutRelationship> relations = endPoints.get(nodeToCheck);
		if (relations != null) {
			for (LayoutRelationship rel : relations) {
				if (cameFrom == null || !rel.equals(cameFrom)) {
					LayoutEntity currentNode = rel.getDestinationInLayout();
					if (hasCycle(currentNode, nodePathSoFar, rel, endPoints, nodesChecked, cycle)) {
						return true;
					}
				}
			}
		}
		nodePathSoFar.remove(nodeToCheck);
		return false;
	}

}
