/*******************************************************************************
 * Copyright (c) 2024, 2025 Patrick Ziegler and others.
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

package org.eclipse.zest.tests.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.internal.ContainerFigure;
import org.eclipse.zest.examples.uml.UMLClassFigure;
import org.eclipse.zest.examples.uml.UMLExample;
import org.eclipse.zest.tests.utils.SWTBotGraphContainer;
import org.eclipse.zest.tests.utils.SWTBotGraphNode;
import org.eclipse.zest.tests.utils.Snippet;

import org.junit.jupiter.api.Test;

/**
 * This class instantiates the UML-based Zest examples and tests the correctness
 * of the functionality they are supposed to show.
 */
@SuppressWarnings("nls")
public class GraphUMLTests extends AbstractGraphTest {

	@Override
	protected boolean hasGraph(Lookup lookup, Snippet snippet) throws ReflectiveOperationException {
		try {
			lookup.findStaticVarHandle(snippet.type(), snippet.field(), Graph.class);
			return true;
		} catch (NoSuchFieldException ignore) {
			return false;
		}
	}

	@Override
	protected Graph getGraph(Lookup lookup, Snippet snippet) throws ReflectiveOperationException {
		VarHandle varHandle = lookup.findStaticVarHandle(snippet.type(), snippet.field(), Graph.class); // $NON-NLS-1$
		return (Graph) varHandle.get();
	}

	/**
	 * Tests whether the UML nodes are created properly and added to the correct
	 * container.
	 */
	@Test
	@Snippet(type = UMLExample.class)
	public void testUMLExample() {
		SWTBotGraphNode node1 = graphRobot.getNode(0);
		SWTBotGraphNode node2 = graphRobot.getNode(1);
		SWTBotGraphNode node3 = graphRobot.getNode(2);
		assertEquals(graphRobot.getNodes().size(), 3);

		assertEquals(node1.getText(), "A UML Container");
		assertEquals(node2.getText(), "");
		assertEquals(node3.getText(), "");

		assertInstanceOf(node1.getNodeFigure(), ContainerFigure.class);
		assertInstanceOf(node2.getNodeFigure(), UMLClassFigure.class);
		assertInstanceOf(node3.getNodeFigure(), UMLClassFigure.class);

		SWTBotGraphContainer graphContainer = (SWTBotGraphContainer) graphRobot.getNode(0);
		SWTBotGraphNode node4 = graphContainer.getNode(0);
		assertEquals(graphContainer.getNodes().size(), 1);

		assertEquals(node4.getText(), "");
		assertInstanceOf(node4.getNodeFigure(), UMLClassFigure.class);
	}
}
