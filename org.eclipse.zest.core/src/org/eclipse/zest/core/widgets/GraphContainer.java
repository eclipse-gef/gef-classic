/*******************************************************************************
 * Copyright 2005, 2026, CHISEL Group, University of Victoria, Victoria,
 *                       BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 *               Sebastian Hollersbacher
 *               Mateusz Matela
 ******************************************************************************/
package org.eclipse.zest.core.widgets;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;

import org.eclipse.zest.core.widgets.internal.AspectRatioFreeformLayer;
import org.eclipse.zest.core.widgets.internal.ContainerFigure;
import org.eclipse.zest.core.widgets.internal.ExpandGraphLabel;
import org.eclipse.zest.core.widgets.internal.ZestRootLayer;
import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

import org.eclipse.draw2d.Animation;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutAnimator;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A Container than can be added to a Graph. Nodes can be added to this
 * container. The container supports collapsing and expanding and has the same
 * properties as the nodes. Containers cannot have custom figures.
 *
 * @author Ian Bull
 *
 * @author Sebastian Hollersbacher
 */
@SuppressWarnings("removal")
public class GraphContainer extends GraphNode implements IContainer2 {

	private static final double SCALED_WIDTH = 300;
	private static final double SCALED_HEIGHT = 200;
	private static final int CONTAINER_HEIGHT = 200;
	private static final int MIN_WIDTH = 250;
	private static final int ANIMATION_TIME = 100;
	private static final int SUBLAYER_OFFSET = 2;

	private static SelectionListener selectionListener;

	private ExpandGraphLabel expandGraphLabel;

	private List<GraphNode> childNodes = null;
	private int childAreaHeight = CONTAINER_HEIGHT;

	// TODO Zest 2.x - Make private
	public ZestRootLayer zestLayer;
	private ScrollPane scrollPane;
	private LayoutAlgorithm layoutAlgorithm;
	private boolean isExpanded = false;
	private AspectRatioFreeformLayer scalledLayer;
	private InternalLayoutContext layoutContext;

	/**
	 * Creates a new GraphContainer. A GraphContainer may contain nodes, and has
	 * many of the same properties as a graph node.
	 *
	 * @param graph The graph that the container is being added to
	 * @param style
	 */
	public GraphContainer(IContainer graph, int style) {
		this(graph, style, ""); //$NON-NLS-1$

	}

	/**
	 * @deprecated Since Zest 2.0, use {@link #GraphContainer(Graph, int)},
	 *             {@link #setText(String)}
	 */
	@Deprecated(since = "1.12", forRemoval = true)
	public GraphContainer(IContainer graph, int style, String text) {
		this(graph, style, text, null);

	}

	/**
	 * @deprecated Since Zest 2.0, use {@link #GraphContainer(Graph, int)},
	 *             {@link #setText(String)}, and {@link #setImage(Image)}
	 */
	@Deprecated(since = "1.12", forRemoval = true)
	public GraphContainer(IContainer graph, int style, String text, Image image) {
		super(graph, style, text, image);
		initModel(graph, text, image);
		close(false);
		childNodes = new ArrayList<>();
		registerToParent(graph);
	}

	/**
	 * Custom figures cannot be set on a GraphContainer.
	 */
	@SuppressWarnings("static-method")
	public void setCustomFigure(IFigure nodeFigure) {
		throw new RuntimeException("Operation not supported:  Containers cannot have custom figures"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.mylar.zest.core.widgets.GraphItem#getItemType()
	 *
	 * /** Gets the figure for this container.
	 */
	@Override
	public IFigure getNodeFigure() {
		return this.nodeFigure;
	}

	/**
	 * Close this node.
	 *
	 * @param animate
	 */
	public void close(boolean animate) {
		if (animate) {
			Animation.markBegin();
		}
		isExpanded = false;

		expandGraphLabel.setExpandedState(ExpandGraphLabel.CLOSED);
		Rectangle newBounds = scrollPane.getBounds().getCopy();
		newBounds.height = 0;

		scrollPane.setSize(scrollPane.getSize().width, 0);
		updateFigureForModel(this.zestLayer);
		scrollPane.setVisible(false);

		for (IFigure child : zestLayer.getChildren()) {
			GraphItem item = getGraph().getGraphItem(child);
			item.setVisible(false);
			if (item instanceof GraphNode gn) { // refresh nodes in container if closed
				HideNodeHelper hideNodeHelper = gn.getHideNodeHelper();
				if (hideNodeHelper != null) {
					hideNodeHelper.resetCounter();
				}
			}
		}
		Rectangle containerBounds = new Rectangle(this.getLocation(),
				new Dimension(this.getSize().width, CONTAINER_HEIGHT + this.expandGraphLabel.getSize().height));
		moveNodesUp(containerBounds, this);
		if (animate) {
			Animation.run(ANIMATION_TIME);
		}
		// this.nodeFigure.getUpdateManager().performUpdate();
		updateFigureForModel(getModelFigure());
	}

	private static void addNodeToOrderedList(List<GraphNode> orderedNodeList, GraphNode node) {
		int counter = 0;
		for (GraphNode nextOrderedNode : orderedNodeList) {
			// Look through the list of nodes below and find the right spot for
			// this
			if (nextOrderedNode.getLocation().y + nextOrderedNode.getBounds().height > node.getLocation().y
					+ node.getBounds().height) {
				break;
			}
			counter++;
		}
		// Place this in the right location
		orderedNodeList.add(counter, node);
	}

	/**
	 * Gets all the nodes below the yValue. The nodes are returned in order.
	 *
	 * @param nodes
	 * @param yValue
	 * @return
	 */
	private static List<GraphNode> getOrderedNodesBelowY(List<? extends GraphNode> nodes, int yValue, GraphNode yValueNode) {
		LinkedList<GraphNode> orderedNode = new LinkedList<>();
		for (GraphNode node : nodes) {
			if (node == yValueNode) {
				continue;
			}
			if (node.getLocation().y + node.getBounds().height > yValue) {
				// This node is below the container
				addNodeToOrderedList(orderedNode, node);
			}
		}
		// Convert this to an arrayList for faster access
		return new ArrayList<>(orderedNode);
	}

	/**
	 * Checks if the node intersects the stripe between left and right
	 *
	 * @param left
	 * @param right
	 * @param node
	 * @return
	 */
	private static boolean nodeInStripe(int left, int right, GraphNode node) {
		return (node.getBounds().x < right && node.getBounds().x + node.getBounds().width > left);
	}

	void pack(Graph g) {
		GraphNode highestNode = getHighestNode(g);
		moveNodesUp(highestNode.getBounds(), highestNode);
	}

	/**
	 *
	 * @param g
	 * @return
	 */
	static GraphNode getHighestNode(Graph g) {
		GraphNode lowest /* highest on the screen */ = null;

		for (GraphNode node : g.getNodes()) {
			if (lowest == null || lowest.getBounds().y > node.getBounds().y) {
				lowest = node;
			}
		}
		return lowest;

	}

	/**
	 * Move the nodes below this node up
	 *
	 * @param containerBounds
	 * @param graphContainer
	 */
	private void moveNodesUp(Rectangle containerBounds, GraphNode graphContainer) {

		// Get all nodes below this container, in order
		List<GraphNode> orderedNodesBelowY = getOrderedNodesBelowY(parent.getNodes(), containerBounds.y, graphContainer);
		int leftSide = containerBounds.x;
		int rightSide = containerBounds.x + containerBounds.width;
		List<GraphNode> nodesToConsider = new LinkedList<>(orderedNodesBelowY);
		addNodeToOrderedList(orderedNodesBelowY, graphContainer);

		while (!nodesToConsider.isEmpty()) {
			GraphNode node = nodesToConsider.get(0);
			if (nodeInStripe(leftSide, rightSide, node)) {
				leftSide = Math.min(leftSide, node.getBounds().x);
				rightSide = Math.max(rightSide, node.getBounds().x + node.getBounds().width);
				// If this node is in the stripe, move it up
				// the previous node
				GraphNode previousNode = null;
				int i = 0;
				for (; i < orderedNodesBelowY.size(); i++) {
					if (orderedNodesBelowY.get(i) == node) {
						break;
					}
				}
				int j = i - 1;
				while (j >= 0) {
					GraphNode pastNode = orderedNodesBelowY.get(j);
					// if (nodeInStripe(leftSide, rightSide, pastNode)) {
					if (nodeInStripe(node.getBounds().x, node.getBounds().x + node.getBounds().width, pastNode)) {
						previousNode = pastNode;
						break;
					}
					j--;
				}
				if (previousNode == null) {
					previousNode = graphContainer;
				}
				int previousLocation = previousNode.getBounds().y + previousNode.getBounds().height + 2;

				orderedNodesBelowY.remove(i);
				node.setLocation(node.getLocation().x, previousLocation);
				addNodeToOrderedList(orderedNodesBelowY, node);

			}
			nodesToConsider.remove(node);
		}
	}

	/**
	 * Open the container. This opens the graph container to show the nodes within
	 * and update the twistie
	 */
	public void open(boolean animate) {
		if (animate) {
			Animation.markBegin();
		}
		isExpanded = true;

		expandGraphLabel.setExpandedState(ExpandGraphLabel.OPEN);

		scrollPane.setSize(computeChildArea());
		scrollPane.setVisible(true);
		// setSize(expandGraphLabel.getSize().width,
		// expandGraphLabel.getSize().height + expandedHeight -
		// SUBLAYER_OFFSET);

		for (IFigure child : this.zestLayer.getChildren()) {
			GraphItem item = getGraph().getGraphItem(child);
			item.setVisible(true);
		}

		updateFigureForModel(getModelFigure());

		Rectangle containerBounds = new Rectangle(this.getLocation(),
				new Dimension(this.getSize().width, CONTAINER_HEIGHT + this.expandGraphLabel.getSize().height));
		// moveIntersectedNodes(containerBounds, this);
		moveNodesDown(containerBounds, this);
		moveNodesUp(containerBounds, this);
		// pack(graph);
		if (animate) {
			Animation.run(ANIMATION_TIME);
		}
		this.getFigure().getUpdateManager().performValidation();
		// this.nodeFigure.getUpdateManager().performUpdate();

	}

	/**
	 *
	 * @param containerBounds
	 * @param graphContainer
	 */
	private void moveNodesDown(Rectangle containerBounds, GraphContainer graphContainer) {

		// Find all nodes below here
		List<GraphNode> nodesBelowHere = getOrderedNodesBelowY(parent.getNodes(), containerBounds.y, graphContainer);
		List<GraphNode> nodesToMove = new LinkedList<>();
		int left = containerBounds.x;
		int right = containerBounds.x + containerBounds.width;
		for (GraphNode node : nodesBelowHere) {
			if (nodeInStripe(left, right, node)) {
				nodesToMove.add(node);
				left = Math.min(left, node.getBounds().x);
				right = Math.max(right, node.getBounds().x + node.getBounds().width);
			}
		}
		List<GraphNode> intersectingNodes = intersectingNodes(containerBounds, nodesToMove, graphContainer);
		int delta = getMaxMovement(containerBounds, intersectingNodes);
		if (delta > 0) {
			shiftNodesDown(nodesToMove, delta);
		}

	}

	// /**
	// * Gets a list of nodes below the given node
	// * @param node
	// * @return
	// */
	// private List getNodesBelow(int y, List nodes) {
	// Iterator allNodes = nodes.iterator();
	// LinkedList result = new LinkedList();
	// while (allNodes.hasNext()) {
	// GraphNode nextNode = (GraphNode) allNodes.next();
	// int top = nextNode.getLocation().y;
	// if (top > y) {
	// result.add(nextNode);
	// }
	// }
	// return result;
	// }

	/**
	 * Checks all the nodes in the list of nodesToCheck to see if they intersect
	 * with the bounds set
	 *
	 * @param node
	 * @param nodesToCheck
	 * @return
	 */
	private static List<GraphNode> intersectingNodes(Rectangle bounds, List<GraphNode> nodesToCheck, GraphNode node) {
		List<GraphNode> result = new LinkedList<>();
		for (GraphNode nodeToCheck : nodesToCheck) {
			if (node == nodeToCheck) {
				continue;
			}
			if (bounds.intersects(nodeToCheck.getBounds())) {
				result.add(nodeToCheck);
			}
		}
		return result;
	}

	/**
	 * Gets the max distance the intersecting nodes need to be shifted to make room
	 * for the expanding node
	 *
	 * @param bounds
	 * @param nodesToMove
	 * @return
	 */
	private static int getMaxMovement(Rectangle bounds, List<GraphNode> nodesToMove) {
		int maxMovement = 0;
		for (GraphNode node : nodesToMove) {
			int yValue = node.getLocation().y;
			int distanceFromBottom = (bounds.y + bounds.height) - yValue;
			maxMovement = Math.max(maxMovement, distanceFromBottom);
		}
		return maxMovement + 3;
	}

	/**
	 * Shifts a collection of nodes down.
	 *
	 * @param nodesToShift
	 * @param amount
	 */
	private static void shiftNodesDown(List<GraphNode> nodesToShift, int amount) {
		for (GraphNode node : nodesToShift) {
			node.setLocation(node.getLocation().x, node.getLocation().y + amount);
		}
	}

	// /**
	// * This finds the highest Y Value of a set of nodes.
	// * @param nodes
	// * @return
	// */
	// private int findSmallestYValue(List nodes) {
	// Iterator iterator = nodes.iterator();
	// int lowestNode /*highest on the screen*/= Integer.MAX_VALUE - 100; //
	// Subtract 100 so we don't overflow
	// while (iterator.hasNext()) {
	// GraphNode node = (GraphNode) iterator.next();
	// int y = node.getLocation().y;
	// lowestNode = Math.min(lowestNode, y);
	// }
	// return lowestNode;
	// }

	// /**
	// * Clears the nodes that the container intersects as it expands
	// * @param containerBounds
	// * @param graphContainer
	// */
	// private void moveIntersectedNodes(Rectangle containerBounds, GraphNode
	// graphContainer) {
	//
	// List nodesBelowHere = getNodesBelow(this.getLocation().y,
	// graphContainer.getGraphModel().getNodes());
	// List intersectingNodes = intersectingNodes(containerBounds,
	// nodesBelowHere, graphContainer);
	// int delta = getMaxMovement(containerBounds, intersectingNodes);
	// shiftNodesDown(intersectingNodes, delta);
	//
	// int lowestNode /*highest on the screen*/=
	// findSmallestYValue(intersectingNodes);
	// nodesBelowHere = getNodesBelow(lowestNode, nodesBelowHere);
	//
	// while (nodesBelowHere.size() > 0) {
	// Iterator intersectingNodeIterator = intersectingNodes.iterator();
	// List nodesMovedInLastIteration = new LinkedList();
	// while (intersectingNodeIterator.hasNext()) {
	// GraphNode node = (GraphNode) intersectingNodeIterator.next();
	// intersectingNodes = intersectingNodes(node.getBounds(), nodesBelowHere,
	// node);
	// delta = getMaxMovement(node.getBounds(), intersectingNodes);
	// if (delta > 0) {
	// shiftNodesDown(intersectingNodes, delta);
	// nodesMovedInLastIteration.addAll(intersectingNodes);
	// }
	// }
	// lowestNode /*highest on the screen*/=
	// findSmallestYValue(nodesMovedInLastIteration);
	// nodesBelowHere = getNodesBelow(lowestNode, nodesBelowHere);
	// intersectingNodes = nodesMovedInLastIteration;
	// }
	// }

	/**
	 * Gets the graph that this container has been added to.
	 */
	@Override
	public Graph getGraph() {
		return this.graph.getGraph();
	}

	@Override
	public Widget getItem() {
		return this;
	}

	@Override
	public int getItemType() {
		return CONTAINER;
	}

	/**
	 *
	 */
	@Override
	public void setLayoutAlgorithm(LayoutAlgorithm algorithm, boolean applyLayout) {
		this.layoutAlgorithm = algorithm;
		if (!(layoutAlgorithm instanceof LayoutAlgorithm.Zest1)) {
			this.layoutAlgorithm.setLayoutContext(getLayoutContext());
		}
		if (applyLayout) {
			applyLayout();
		}

	}

	/**
	 * @since 1.10
	 */
	public LayoutAlgorithm getLayoutAlgorithm() {
		return this.layoutAlgorithm;
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	@Override
	public final LayoutContext getLayoutContext() {
		if (layoutContext == null) {
			layoutContext = new InternalLayoutContext(this);
		}
		return layoutContext;
	}

	/**
	 * @since 1.12
	 */
	@Override
	public DisplayIndependentRectangle getLayoutBounds() {
		double width = GraphContainer.SCALED_WIDTH - 10;
		double height = GraphContainer.SCALED_HEIGHT - 10;
		return new DisplayIndependentRectangle(25, 25, width - 50, height - 50);
	}

	@Override
	public void applyLayout() {
		if ((this.getNodes().isEmpty())) {
			return;
		}

		int layoutStyle = 0;

		if (checkStyle(ZestStyles.NODES_NO_LAYOUT_RESIZE)) {
			layoutStyle = LayoutStyles.NO_LAYOUT_NODE_RESIZING;
		}

		if (layoutAlgorithm == null) {
			layoutAlgorithm = new TreeLayoutAlgorithm.Zest1(layoutStyle);
		}

		try {
			Animation.markBegin();
			if (layoutAlgorithm instanceof LayoutAlgorithm.Zest1 zest1) {
				zest1.setStyle(zest1.getStyle() | layoutStyle);

				// calculate the size for the layout algorithm
				// Dimension d = this.scalledLayer.getSize();
				Dimension d = new Dimension();
				d.width = (int) SCALED_WIDTH;
				d.height = (int) SCALED_HEIGHT;

				d.width = d.width - 10;
				d.height = d.height - 10;
				// if (d.height <= 0) {
				// d.height = (CONTAINER_HEIGHT);
				// }
				// d.scale(1 / this.scalledLayer.getScale());

				if (d.isEmpty()) {
					return;
				}
				LayoutRelationship[] connectionsToLayout = getGraph().getConnectionsToLayout(getNodes());
				LayoutEntity[] nodesToLayout = getGraph().getNodesToLayout(getNodes());

				zest1.applyLayout(nodesToLayout, connectionsToLayout, 25, 25, d.width - 50, d.height - 50, false,
						false);
			} else {
				layoutAlgorithm.applyLayout(true);
				layoutContext.flushChanges(false);
			}
			Animation.run(ANIMATION_TIME);
			getFigure().getUpdateManager().performUpdate();

		} catch (InvalidLayoutConfiguration e) {
			e.printStackTrace();
		}

	}

	/**
	 * Get the scale for this container. This is the scale applied to the children
	 * contained within
	 */
	public double getScale() {
		return this.scalledLayer.getScale();
	}

	/**
	 * Set the scale for this container. This is the scale applied to the children
	 * contained within.
	 *
	 * @param scale
	 */
	public void setScale(double scale) {
		this.scalledLayer.setScale(scale);
	}

	@Override
	protected Rectangle getHideContainerBounds() {
		Point loc = getLocation();
		ContainerDimension containerDimension = computeContainerSize();
		Dimension size = new Dimension(containerDimension.width + 2 * HideNodeHelper.MARGIN,
				containerDimension.labelHeight + 2 * HideNodeHelper.MARGIN);
		return new Rectangle(loc, size);
	}

	/***************************************************************************
	 * NON API MEMBERS
	 **************************************************************************/
	@Override
	protected void initFigure() {
		setModelFigure(createContainerFigure());
		if (graph.getHideNodesEnabled()) {
			nodeFigure = new ContainerFigure();
			nodeFigure.add(getModelFigure());
			setHideNodeHelper(new HideNodeHelper(this));
		} else {
			nodeFigure = getModelFigure();
		}
	}

	/**
	 * This is a small class to help represent the size of the container. It should
	 * only be used in the computeContainerSize method.
	 */
	class ContainerDimension {
		int width;
		int labelHeight;
		int expandedHeight;
	}

	/**
	 * Computes size of the scroll pane that the child nodes will be placed in.
	 *
	 * @return
	 */
	private Dimension computeChildArea() {
		ContainerDimension containerDimension = computeContainerSize();
		Dimension dimension = new Dimension();
		dimension.width = containerDimension.width;
		dimension.height = containerDimension.expandedHeight - containerDimension.labelHeight + SUBLAYER_OFFSET;
		return dimension;
	}

	/**
	 * Computes the desired size of the container. This method uses the minimum
	 * size, label size and setSize to compute the size.
	 *
	 * @return
	 */
	private ContainerDimension computeContainerSize() {
		ContainerDimension dimension = new ContainerDimension();
		int labelHeight = expandGraphLabel.getPreferredSize().height;
		int labelWidth = expandGraphLabel.getPreferredSize().width;
		if (labelWidth < MIN_WIDTH) {
			labelWidth = MIN_WIDTH;
			expandGraphLabel.setPreferredSize(labelWidth, labelHeight);
		}
		if (labelHeight < 30) {
			labelHeight = 30;
		}

		dimension.labelHeight = labelHeight;
		dimension.width = labelWidth;
		dimension.width = Math.max(dimension.width, this.size.width);
		dimension.expandedHeight = dimension.labelHeight + childAreaHeight - SUBLAYER_OFFSET;
		dimension.expandedHeight = Math.max(dimension.expandedHeight, this.size.height);

		return dimension;
	}

	/*
	 * private double computeChildScale() { Dimension childArea =
	 * computeChildArea(); double widthScale = childArea.width / scaledWidth; double
	 * heightScale = childArea.height / scaledHeight; return Math.min(widthScale,
	 * heightScale); }
	 */
	private double computeHeightScale() {
		Dimension childArea = computeChildArea();
		return childArea.height / SCALED_HEIGHT;
	}

	private double computeWidthScale() {
		Dimension childArea = computeChildArea();
		return childArea.width / SCALED_WIDTH;
	}

	private IFigure createContainerFigure() {
		GraphContainer node = this;
		IFigure containerFigure = new ContainerFigure();
		containerFigure.setOpaque(true);

		containerFigure.addLayoutListener(LayoutAnimator.getDefault());

		containerFigure.setLayoutManager(new FreeformLayout());
		expandGraphLabel = new ExpandGraphLabel(this, node.getText(), node.getImage(), false);
		expandGraphLabel.setText(getText());
		expandGraphLabel.setImage(getImage());
		ContainerDimension containerDimension = computeContainerSize();

		scrollPane = new ScrollPane();
		scrollPane.addLayoutListener(LayoutAnimator.getDefault());

		Viewport viewport = new FreeformViewport();
		/*
		 * This is the code that helps remove the scroll bars moving when the nodes are
		 * dragged.
		 *
		 * viewport.setHorizontalRangeModel(new DefaultRangeModel() { public void
		 * setAll(int min, int ext, int max) { System.out.println("Max: " + max +
		 * " : current Max:  " + getMaximum()); if (max < getMaximum()) { max =
		 * getMaximum(); } super.setAll(min, ext, max); }
		 *
		 * public void setMaximum(int maximum) { // TODO Auto-generated method stub
		 * System.out.println("Max: " + maximum + " : current Max:  " + getMaximum());
		 * if (maximum < getMaximum()) { return; } super.setMaximum(maximum); } });
		 */

		scrollPane.setViewport(viewport);
		viewport.addLayoutListener(LayoutAnimator.getDefault());
		scrollPane.setScrollBarVisibility(ScrollPane.AUTOMATIC);

		// scalledLayer = new ScalableFreeformLayeredPane();
		scalledLayer = new AspectRatioFreeformLayer("debug label"); //$NON-NLS-1$
		scalledLayer.addLayoutListener(LayoutAnimator.getDefault());
		// scalledLayer.setScale(computeChildScale());
		scalledLayer.setScale(computeWidthScale(), computeHeightScale());
		// container = new FreeformLayer();
		// edgeLayer = new FreeformLayer();
		zestLayer = new ZestRootLayer();
		zestLayer.addLayoutListener(LayoutAnimator.getDefault());
		// container.addLayoutListener(LayoutAnimator.getDefault());
		// edgeLayer.addLayoutListener(LayoutAnimator.getDefault());
		// scalledLayer.add(edgeLayer);
		// scalledLayer.add(container);
		scalledLayer.add(zestLayer);

		// container.setLayoutManager(new FreeformLayout());
		zestLayer.setLayoutManager(new FreeformLayout());
		scrollPane.setSize(computeChildArea());
		scrollPane.setLocation(new Point(0, containerDimension.labelHeight - SUBLAYER_OFFSET));
		scrollPane.setForegroundColor(getGraph().getColorProvider().getForegroundColor(this));

		expandGraphLabel.setBackgroundColor(getBackgroundColor());
		expandGraphLabel.setForegroundColor(getForegroundColor());
		expandGraphLabel.setLocation(new Point(0, 0));
		expandGraphLabel.setBorder(new LineBorder(getGraph().getColorProvider().getBorderColor(this)));

		containerFigure.add(scrollPane);
		containerFigure.add(expandGraphLabel);

		scrollPane.getViewport().setContents(scalledLayer);
		scrollPane.setBorder(new LineBorder(getGraph().getColorProvider().getBorderColor(this)));

		return containerFigure;
	}

	private static void registerToParent(IContainer parent) {
		if (parent.getItemType() == GRAPH) {
			createSelectionListener();
			parent.getGraph().addSelectionListener(selectionListener);
		}
	}

	private static void createSelectionListener() {
		if (selectionListener == null) {
			selectionListener = new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (e.item instanceof GraphContainer) {
						// set focus to expand label so that pressing space
						// opens/closes
						// the last selected container
						((GraphContainer) e.item).expandGraphLabel.setFocus();
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// ignore
				}
			};

		}
	}

	@Override
	protected void updateFigureForModel(IFigure currentFigure) {

		if (expandGraphLabel == null) {
			initFigure();
		}
		expandGraphLabel.setTextT(getText());
		expandGraphLabel.setImage(getImage());
		expandGraphLabel.setFont(getFont());

		if (highlighted == HIGHLIGHT_ON) {
			expandGraphLabel.setForegroundColor(getForegroundHighlightColor());
			expandGraphLabel.setBackgroundColor(getBackgroundHighlightColor());
		}
		// @tag ADJACENT : Removed highlight adjacent
		/*
		 * else if (highlighted == HIGHLIGHT_ADJACENT) {
		 * expandGraphLabel.setForegroundColor(getForegroundColor());
		 * expandGraphLabel.setBackgroundColor(getHighlightAdjacentColor()); }
		 */
		else {
			expandGraphLabel.setForegroundColor(getForegroundColor());
			expandGraphLabel.setBackgroundColor(getBackgroundColor());
		}

		ContainerDimension containerDimension = computeContainerSize();

		expandGraphLabel.setSize(containerDimension.width, containerDimension.labelHeight);
		if (isExpanded) {
			// setSize(expandGraphLabel.getSize().width,
			// expandGraphLabel.getSize().height + expandedHeight -
			// SUBLAYER_OFFSET);
			setSize(containerDimension.width, containerDimension.expandedHeight);
		} else {
			setSize(containerDimension.width, containerDimension.labelHeight);
		}
		scrollPane.setLocation(new Point(expandGraphLabel.getLocation().x,
				expandGraphLabel.getLocation().y + containerDimension.labelHeight - SUBLAYER_OFFSET));
		// scrollPane.setLocation(new Point(0, labelHeight - SUBLAYER_OFFSET));
		// Rectangle bounds = expandGraphLabel.getBounds().getCopy();
		// Rectangle newBounds = new Rectangle(new Point(bounds.x, bounds.y +
		// labelHeight - SUBLAYER_OFFSET), scrollPane.getSize());
		// figure.setConstraint(scrollPane, newBounds);
		/*
		 * size.width = labelWidth; if (scrollPane.getSize().height > 0) { size.height =
		 * labelHeight + scrollPane.getSize().height - SUBLAYER_OFFSET; } else {
		 * size.height = labelHeight; } refreshLocation();
		 * figure.getUpdateManager().performValidation();
		 */

	}

	@Override
	protected void refreshLocation() {
		if (nodeFigure == null || nodeFigure.getParent() == null) {
			return; // node figure has not been created yet
		}
		Point loc = getLocation();

		ContainerDimension containerDimension = computeContainerSize();
		Dimension size = new Dimension();

		expandGraphLabel.setSize(containerDimension.width, containerDimension.labelHeight);
		this.childAreaHeight = computeChildArea().height;
		if (isExpanded) {
			size.width = containerDimension.width;
			size.height = containerDimension.expandedHeight;
		} else {
			size.width = containerDimension.width;
			size.height = containerDimension.labelHeight;
		}
		Rectangle bounds = new Rectangle(loc, size);
		nodeFigure.getParent().setConstraint(nodeFigure, bounds);
		scrollPane.setLocation(new Point(expandGraphLabel.getLocation().x,
				expandGraphLabel.getLocation().y + containerDimension.labelHeight - SUBLAYER_OFFSET));
		scrollPane.setSize(computeChildArea());
		scalledLayer.setScale(computeWidthScale(), computeHeightScale());
		if (getHideNodeHelper() != null) {
			bounds.width += 2 * HideNodeHelper.MARGIN;
			bounds.height += 2 * HideNodeHelper.MARGIN;
			getHideNodeHelper().updateNodeBounds(bounds);
		}
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	@Override
	public void addSubgraphFigure(IFigure figure) {
		zestLayer.addSubgraph(figure);
		graph.subgraphFigures.add(figure);
	}

	void addConnectionFigure(PolylineConnection connection) {
		getModelFigure().add(connection);
		// zestLayer.addConnection(connection);
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	@Override
	public void addNode(GraphNode node) {
		zestLayer.addNode(node.getNodeFigure());
		if (!childNodes.contains(node)) {
			childNodes.add(node);
		}
		// container.add(node.getNodeFigure());
		// graph.registerItem(node);
		node.setVisible(isExpanded);
	}

	void addNode(GraphContainer container) {
		// Containers cannot be added to other containers (yet)
	}

	@Override
	public List<GraphNode> getNodes() {
		return this.childNodes;
	}

	/**
	 * @since 1.12
	 */
	@Override
	public List<GraphConnection> getConnections() {
		return filterConnections(getGraph().getConnections());

	}

	@Override
	void paint() {
		for (GraphNode node : getNodes()) {
			node.paint();
		}
	}

	private List<GraphConnection> filterConnections(List<? extends GraphConnection> connections) {
		List<GraphConnection> result = new ArrayList<>();
		for (GraphConnection connection : connections) {
			if (connection.getSource().getParent() == this && connection.getDestination().getParent() == this) {
				result.add(connection);
			}
		}
		return result;
	}
}
