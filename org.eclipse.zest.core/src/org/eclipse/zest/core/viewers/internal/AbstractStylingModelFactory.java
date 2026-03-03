/*******************************************************************************
 * Copyright 2005, 2024, CHISEL Group, University of Victoria, Victoria,
 *                       BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 ******************************************************************************/
package org.eclipse.zest.core.viewers.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.zest.core.viewers.AbstractStructuredGraphViewer;
import org.eclipse.zest.core.viewers.IFigureProvider;
import org.eclipse.zest.core.viewers.INestedContentProvider;
import org.eclipse.zest.core.widgets.ConstraintAdapter;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.IContainer;

import org.eclipse.draw2d.IFigure;

/**
 * Base class that can be used for model factories. Offers facilities to style
 * the items that have been created by the factory.
 *
 * @author Del Myers
 */
// @tag zest.bug.160367-Refreshing.fix : update the factory to use the
// IStylingGraphModelFactory
@SuppressWarnings("removal")
public abstract class AbstractStylingModelFactory implements IStylingGraphModelFactory {
	private final AbstractStructuredGraphViewer viewer;
	private int connectionStyle;
	private int nodeStyle;
	private List<ConstraintAdapter> constraintAdapters = new ArrayList<>();

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public AbstractStylingModelFactory(AbstractStructuredGraphViewer viewer) {
		this.viewer = viewer;
		this.connectionStyle = SWT.NONE;
		this.nodeStyle = SWT.NONE;
		if (viewer != null) {
			this.constraintAdapters = (List<ConstraintAdapter>) viewer.getConstraintAdapters();
		}
	}

	public void styleConnection(GraphConnection conn) {
		// recount the source and target connections on the node.
		// this isn't a great way to do it, because it results in
		// an n^2 algorithm. But, if anyone can figure out a better way
		// go ahead and try it.
		GraphNode source = conn.getSource();
		GraphNode dest = conn.getDestination();
		List<GraphConnection> rightList = getConnectionList(source, dest);

		List<GraphConnection> leftList = null;

		if (dest != source) {
			leftList = getConnectionList(dest, source);
		}

		int size = (leftList != null) ? leftList.size() + rightList.size() : rightList.size();
		// adjust the arcs going from source to destination
		adjustCurves(rightList, size);
		// adjust the arcs going from destination to source
		if (leftList != null) {
			adjustCurves(leftList, size);
		}
	}

	/**
	 * Takes a list of IGraphModelConnections and adjusts the curve depths and the
	 * bezier curves based on the number of curves in the list.
	 *
	 * @param connections
	 * @param size        + * total number of arcs - may be bigger then
	 *                    connections.size
	 */
	@SuppressWarnings("static-method")
	protected void adjustCurves(List<GraphConnection> connections, int size) {
		/*
		 * The connections should be curved if source and dest are equal, or there are
		 * multiple arcs between two nodes
		 */
		for (int i = 0; i < connections.size(); i++) {
			GraphConnection conn = connections.get(i);
			int radius = 20;
			if (conn.getSource() == conn.getDestination()) {
				radius = 40;
			} else if (size < 2) {
				radius = 0;
			}
			conn.setCurveDepth((i + 1) * radius);
		}
	}

	/**
	 * @param source
	 * @param dest
	 * @return
	 */
	private static List<GraphConnection> getConnectionList(GraphNode source, GraphNode dest) {
		List<GraphConnection> list = new LinkedList<>();
		for (GraphConnection c : source.getSourceConnections()) {
			if (c.getDestination() == dest) {
				list.add(c);
			}
		}
		return list;
	}

	public void styleItem(GraphItem item) {
		GraphItemStyler.styleItem(item, getLabelProvider());
		if (item instanceof GraphConnection) {
			styleConnection((GraphConnection) item);
		}
	}

	@Override
	public StructuredViewer getViewer() {
		return viewer;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.zest.core.internal.graphmodel.IStylingGraphModelFactory#
	 * getLabelProvider()
	 */
	@Override
	public IBaseLabelProvider getLabelProvider() {
		return viewer.getLabelProvider();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.zest.core.internal.graphmodel.IStylingGraphModelFactory#
	 * getContentProvider()
	 */
	@Override
	public IStructuredContentProvider getContentProvider() {
		return (IStructuredContentProvider) viewer.getContentProvider();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.zest.core.internal.graphmodel.IStylingGraphModelFactory#
	 * createConnection(org.eclipse.zest.core.internal.graphmodel.GraphModel,
	 * java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public GraphConnection createConnection(Graph graph, Object element, Object source, Object dest) {
		if (source == null || dest == null) {
			return null;
		}
		GraphConnection oldConnection = viewer.getGraphModelConnection(element);
		GraphNode sn = viewer.getGraphModelNode(source);
		GraphNode dn = viewer.getGraphModelNode(dest);
		if (oldConnection != null) {
			if ((sn == oldConnection.getSource()) && (dn == oldConnection.getDestination())) {
				styleItem(oldConnection);
				return oldConnection;
			}
			viewer.removeGraphModelConnection(oldConnection);
		}
		IFigureProvider figureProvider = null;
		if (getLabelProvider() instanceof IFigureProvider) {
			figureProvider = (IFigureProvider) getLabelProvider();
		}
		if (sn == null) {
			IFigure figure = null;
			if (figureProvider != null) {
				figure = figureProvider.getFigure(source);
			}
			if (figure != null) {
				sn = createNode(graph, source, figure);
			} else {
				sn = createNode(graph, source);
			}
		}
		if (dn == null) {
			IFigure figure = null;
			if (figureProvider != null) {
				figure = figureProvider.getFigure(dest);
			}
			if (figure != null) {
				dn = createNode(graph, dest, figure);
			} else {
				dn = createNode(graph, dest);
			}
		}
		GraphConnection c = viewer.addGraphModelConnection(element, sn, dn);
		styleItem(c);
		return c;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.zest.core.internal.graphmodel.IStylingGraphModelFactory#
	 * createNode(org.eclipse.zest.core.internal.graphmodel.GraphModel,
	 * java.lang.Object)
	 */
	public GraphNode createNode(Graph graph, Object element, IFigure figure) {
		GraphNode node = null;
		if (getContentProvider() instanceof INestedContentProvider) {
			boolean isContainer = ((INestedContentProvider) getContentProvider()).hasChildren(element);
			if (isContainer) {
				node = viewer.addGraphModelContainer(element);
				styleItem(node);
				Object[] childNodes = ((INestedContentProvider) getContentProvider()).getChildren(element);
				childNodes = filter(getViewer().getInput(), childNodes);
				if (childNodes == null) {
					return node;
				}
				for (Object childNode2 : childNodes) {
					GraphNode childNode = viewer.addGraphModelNode((IContainer) node, childNode2);
					styleItem(childNode);
				}
				((IContainer) node).applyLayout();
				return node;
			}
		}
		node = viewer.addGraphModelNode(element, figure);
		styleItem(node);
		return node;
	}

	@Override
	public GraphNode createNode(Graph graph, Object element) {
		IFigure nodeFigure = null;
		if (getLabelProvider() instanceof IFigureProvider) {
			nodeFigure = ((IFigureProvider) getLabelProvider()).getFigure(element);
		}
		return this.createNode(graph, element, nodeFigure);
	}

	@Override
	public void setConnectionStyle(int style) {
		this.connectionStyle = style;
	}

	/**
	 * @return the connectionStyle
	 */
	@Override
	public int getConnectionStyle() {
		return connectionStyle;
	}

	@Override
	public void setNodeStyle(int style) {
		this.nodeStyle = style;
	}

	public List<? extends ConstraintAdapter> getConstraintAdapters() {
		return this.constraintAdapters;
	}

	/**
	 * @return the nodeStyle
	 */
	@Override
	public int getNodeStyle() {
		return nodeStyle;
	}

	/**
	 * Default implementation simply restyles the item, regardless of the
	 * properties.
	 */
	@Override
	public void update(GraphItem item) {
		styleItem(item);
	}

	/**
	 * Default implementation simply restyles the items, regardless of the
	 * properties.
	 */
	@Override
	public void update(GraphItem[] items) {
		for (GraphItem item : items) {
			styleItem(item);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.zest.core.internal.graphmodel.IStylingGraphModelFactory#
	 * refreshGraph(org.eclipse.zest.core.internal.graphmodel.GraphModel)
	 */
	@Override
	public void refreshGraph(Graph graph) {
		// with this kind of graph, it is just as easy and cost-effective to
		// rebuild the whole thing.

		Map<Object, GraphNode> oldMap = viewer.getNodesMap();
		Map<Object, GraphNode> nodesMap = new HashMap<>();
		// have to copy the Map data accross so that it doesn't get overwritten
		for (Object key : oldMap.keySet()) {
			nodesMap.put(key, oldMap.get(key));
		}
		clearGraph(graph);
		doBuildGraph(graph);
		// update the positions on the new nodes to match the old ones.
		GraphNode[] nodes = getNodesArray(graph);
		// save a little time, go with the smallest list as the primary list
		if (nodes.length < nodesMap.size()) {
			for (GraphNode node : nodes) {
				GraphNode oldNode = nodesMap.get(node.getData());
				if (oldNode != null) {
					node.setLocation(oldNode.getLocation().x, oldNode.getLocation().y);
					if (oldNode.isSizeFixed()) {
						node.setSize(oldNode.getSize().width, oldNode.getSize().height);
					}
				}
			}
		} else {
			for (Object key : nodesMap.keySet()) {
				GraphNode node = viewer.getGraphModelNode(key);
				if (node != null) {
					GraphNode oldNode = nodesMap.get(key);
					node.setLocation(oldNode.getLocation().x, oldNode.getLocation().y);
					if (oldNode.isSizeFixed()) {
						node.setSize(oldNode.getSize().width, oldNode.getSize().height);
					}
				}
			}
		}
	}

	/**
	 * Convenience method for clearing all the elements in the graph.
	 *
	 * @param graph
	 */
	public void clearGraph(Graph graph) {
		graph.setSelection(null);
		Object[] nodeElements = viewer.getNodeElements();
		for (Object nodeElement : nodeElements) {
			viewer.removeGraphModelNode(nodeElement);
		}
		Object[] connectionElements = viewer.getConnectionElements();
		for (Object connectionElement : connectionElements) {
			viewer.removeGraphModelConnection(connectionElement);
		}
	}

	/**
	 * Builds the graph model from the viewer's content provider. There is no
	 * guarantee that the model will be cleared before this method is called.
	 *
	 * @param model
	 */
	protected void doBuildGraph(Graph model) {
		clearGraph(model);
		model.setConnectionStyle(getConnectionStyle());
		model.setNodeStyle(getNodeStyle());
		model.setConstraintAdapters(constraintAdapters);
	}

	/**
	 * Determines if this element should be filtered or not.
	 *
	 * @param parent
	 * @param element
	 */
	protected boolean filterElement(Object parent, Object element) {
		ViewerFilter[] filters = getViewer().getFilters();
		for (ViewerFilter filter : filters) {
			boolean selected = filter.select(viewer, parent, element);
			if (!selected) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.zest.core.internal.graphmodel.IStylingGraphModelFactory#
	 * isFiltered(java.lang.Object)
	 */
	protected Object[] filter(Object parent, Object[] elements) {
		Object[] result = elements;
		ViewerFilter[] filters = getViewer().getFilters();
		for (ViewerFilter filter : filters) {
			result = filter.filter(viewer, parent, result);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.zest.core.internal.graphmodel.IStylingGraphModelFactory#refresh
	 * (org.eclipse.zest.core.internal.graphmodel.GraphModel, java.lang.Object)
	 */
	@Override
	public void refresh(Graph graph, Object element) {
		refresh(graph, element, false);
	}

	/**
	 * Converts the list of GraphNode objects into an array and return it.
	 *
	 * @return GraphModelNode[]
	 */
	protected static GraphNode[] getNodesArray(Graph graph) {
		return graph.getNodes().toArray(new GraphNode[0]);
	}

	/**
	 * Converts the list of GraphConnections objects into an array and return it.
	 *
	 * @param graph
	 */
	protected static GraphConnection[] getConnectionArray(Graph graph) {
		return graph.getConnections().toArray(new GraphConnection[0]);
	}
}
