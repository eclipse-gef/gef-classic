/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *	 Patrick Ziegler - initial API and implementation
 *******************************************************************************/

package org.eclipse.zest.dot.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.zest.dot.core.internal.DOTDocumentModel;
import org.eclipse.zest.dot.core.internal.DOTZestGraphModel;

import org.junit.jupiter.api.Test;

public class DOTTreeVisitorTest {

	@Test
	public void testGraphNodeAttributes() {
		DOTZestGraphModel model = getGraphModel("""
				graph {
					A [shape=circle];
					B [label="DisplayName"];
					C [root];
				}"""); //$NON-NLS-1$
		assertEquals("""
				DOTZestGraphModel [
				  nodes = ListN (
				    DOTZestGraphNodeModel [
				      id = "A"
				      attributes = {shape=circle}
				    ],
				    DOTZestGraphNodeModel [
				      id = "B"
				      attributes = {label=DisplayName}
				    ],
				    DOTZestGraphNodeModel [
				      id = "C"
				      attributes = {root=}
				    ]
				  )
				]""", model.toString()); //$NON-NLS-1$
	}

	@Test
	public void testGraphNodeImplicit() {
		DOTZestGraphModel model = getGraphModel("""
				graph {
					A -> B -> C;
				}"""); //$NON-NLS-1$
		assertEquals("""
				DOTZestGraphModel [
				  nodes = ListN (
				    DOTZestGraphNodeModel [
				      id = "A"
				      attributes = {}
				    ],
				    DOTZestGraphNodeModel [
				      id = "B"
				      attributes = {}
				    ],
				    DOTZestGraphNodeModel [
				      id = "C"
				      attributes = {}
				    ]
				  )
				  edges = UnmodifiableRandomAccessList (
				    DOTZestGraphEdgeModel [
				      sourceId = "A"
				      targetId = "B"
				      attributes = {}
				    ],
				    DOTZestGraphEdgeModel [
				      sourceId = "B"
				      targetId = "C"
				      attributes = {}
				    ]
				  )
				]""", model.toString()); //$NON-NLS-1$
	}

	@Test
	public void testGraphEdgeAttributes() {
		DOTZestGraphModel model = getGraphModel("""
				graph {
					A;
					B;
					C;
					A -> B -> C [label="DisplayName"];
					C -> A;
				}"""); //$NON-NLS-1$
		assertEquals("""
				DOTZestGraphModel [
				  nodes = ListN (
				    DOTZestGraphNodeModel [
				      id = "A"
				      attributes = {}
				    ],
				    DOTZestGraphNodeModel [
				      id = "B"
				      attributes = {}
				    ],
				    DOTZestGraphNodeModel [
				      id = "C"
				      attributes = {}
				    ]
				  )
				  edges = UnmodifiableRandomAccessList (
				    DOTZestGraphEdgeModel [
				      sourceId = "A"
				      targetId = "B"
				      attributes = {label=DisplayName}
				    ],
				    DOTZestGraphEdgeModel [
				      sourceId = "B"
				      targetId = "C"
				      attributes = {label=DisplayName}
				    ],
				    DOTZestGraphEdgeModel [
				      sourceId = "C"
				      targetId = "A"
				      attributes = {}
				    ]
				  )
				]""", model.toString()); //$NON-NLS-1$
	}

	@SuppressWarnings("static-method")
	public DOTZestGraphModel getGraphModel(String content) {
		return new DOTDocumentModel(content).getGraph();
	}
}
