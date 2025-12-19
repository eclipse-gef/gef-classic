/*******************************************************************************
 * Copyright 2005-2010, 2025 CHISEL Group, University of Victoria, Victoria,
 *                           BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria - initial API and implementation
 *               Mateusz Matela
 *               Ian Bull
 *******************************************************************************/
package org.eclipse.zest.layouts.algorithms;

import java.lang.reflect.Field;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;

import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;
import org.eclipse.zest.layouts.interfaces.ConnectionLayout;
import org.eclipse.zest.layouts.interfaces.EntityLayout;
import org.eclipse.zest.layouts.interfaces.NodeLayout;
import org.eclipse.zest.layouts.interfaces.SubgraphLayout;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;

public class DirectedGraphLayoutAlgorithm extends AbstractLayoutAlgorithm {

	/**
	 * Collection of Zest 1.x methods. Used for backwards compatibility.
	 *
	 * @since 2.0
	 * @deprecated Use {@link DirectedGraphLayoutAlgorithm} instead. This class will
	 *             be removed in a future release.
	 * @noextend This class is not intended to be subclassed by clients.
	 * @noreference This class is not intended to be referenced by clients.
	 * @noinstantiate This class is not intended to be instantiated by clients.
	 */
	@Deprecated(since = "2.0", forRemoval = true)
	public static class Zest1 extends AbstractLayoutAlgorithm.Zest1 {

		@Deprecated
		public Zest1(int styles) {
			super(styles);
		}

		@Override
		@Deprecated
		protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
				double boundsX, double boundsY, double boundsWidth, double boundsHeight) {
			Map<InternalNode, Node> mapping = new HashMap<>(entitiesToLayout.length);
			DirectedGraph graph = new DirectedGraph();
			for (InternalNode internalNode : entitiesToLayout) {
				Node node = new Node(internalNode);
				node.setSize(new Dimension(10, 10));
				mapping.put(internalNode, node);
				graph.nodes.add(node);
			}
			for (InternalRelationship relationship : relationshipsToConsider) {
				Node source = mapping.get(relationship.getSource());
				Node dest = mapping.get(relationship.getDestination());
				Edge edge = new Edge(relationship, source, dest);
				graph.edges.add(edge);
			}
			DirectedGraphLayout directedGraphLayout = new ExtendedDirectedGraphLayout();
			directedGraphLayout.visit(graph);

			for (Object node2 : graph.nodes) {
				Node node = (Node) node2;
				InternalNode internalNode = (InternalNode) node.data;
				// For horizontal layout transpose the x and y coordinates
				if ((layout_styles & SWT.HORIZONTAL) == SWT.HORIZONTAL) {
					internalNode.setInternalLocation(node.y, node.x);
				} else {
					internalNode.setInternalLocation(node.x, node.y);
				}
			}
			updateLayoutLocations(entitiesToLayout);
		}

		@Override
		@Deprecated
		protected int getCurrentLayoutStep() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		@Deprecated
		protected int getTotalNumberOfLayoutSteps() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		@Deprecated
		protected boolean isValidConfiguration(boolean asynchronous, boolean continuous) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		@Deprecated
		protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout,
				InternalRelationship[] relationshipsToConsider) {
			// TODO Auto-generated method stub

		}

		@Override
		@Deprecated
		protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
				double x, double y, double width, double height) {
			// TODO Auto-generated method stub

		}

		@Override
		@Deprecated
		public void setLayoutArea(double x, double y, double width, double height) {
			// TODO Auto-generated method stub

		}

	}

	static class ExtendedDirectedGraphLayout extends DirectedGraphLayout {

		@Override
		public void visit(DirectedGraph graph) {
			Field field;
			try {
				field = DirectedGraphLayout.class.getDeclaredField("steps"); //$NON-NLS-1$
				field.setAccessible(true);
				Object object = field.get(this);
				Deque<?> steps = (Deque<?>) object;
				steps.remove(10);
				steps.remove(9);
				steps.remove(8);
				steps.remove(2);
				field.setAccessible(false);
				super.visit(graph);
			} catch (SecurityException | ReflectiveOperationException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	private int orientation = SWT.VERTICAL;

	/**
	 * @since 2.0
	 */
	public DirectedGraphLayoutAlgorithm() {
	}

	public DirectedGraphLayoutAlgorithm(int orientation) {
		if (orientation == SWT.VERTICAL) {
			this.orientation = orientation;
		}
	}

	/**
	 * @since 2.0
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * @since 2.0
	 */
	public void setOrientation(int orientation) {
		if (orientation == SWT.HORIZONTAL || orientation == SWT.VERTICAL) {
			this.orientation = orientation;
		}
	}

	@Override
	public void applyLayout(boolean clean) {
		if (!clean) {
			return;
		}
		Map<EntityLayout, Node> mapping = new HashMap<>();
		DirectedGraph graph = new DirectedGraph();
		EntityLayout[] entities = context.getEntities();
		for (EntityLayout element : entities) {
			Node node = new Node(element);
			node.setSize(new Dimension(10, 10));
			mapping.put(element, node);
			graph.nodes.add(node);
		}
		ConnectionLayout[] connections = context.getConnections();
		for (ConnectionLayout connection : connections) {
			Node source = mapping.get(getEntity(connection.getSource()));
			Node dest = mapping.get(getEntity(connection.getTarget()));
			if (source != null && dest != null) {
				Edge edge = new Edge(connection, source, dest);
				graph.edges.add(edge);
			}
		}
		DirectedGraphLayout directedGraphLayout = new ExtendedDirectedGraphLayout();
		directedGraphLayout.visit(graph);

		for (Object node2 : graph.nodes) {
			Node node = (Node) node2;
			EntityLayout entity = (EntityLayout) node.data;
			if (orientation == SWT.VERTICAL) {
				entity.setLocation(node.x, node.y);
			} else {
				entity.setLocation(node.y, node.x);
			}
		}
	}

	private static EntityLayout getEntity(NodeLayout node) {
		if (!node.isPruned()) {
			return node;
		}
		SubgraphLayout subgraph = node.getSubgraph();
		if (subgraph.isGraphEntity()) {
			return subgraph;
		}
		return null;
	}

}
