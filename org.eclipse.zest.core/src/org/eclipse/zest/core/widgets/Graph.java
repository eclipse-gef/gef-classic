/*******************************************************************************
 * Copyright 2005, 2025, CHISEL Group, University of Victoria, Victoria,
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import org.eclipse.zest.core.viewers.internal.ZoomManager;
import org.eclipse.zest.core.widgets.gestures.RotateGestureListener;
import org.eclipse.zest.core.widgets.gestures.ZoomGestureListener;
import org.eclipse.zest.core.widgets.internal.ContainerFigure;
import org.eclipse.zest.core.widgets.internal.ZestRootLayer;
import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.constraints.LayoutConstraint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.interfaces.ExpandCollapseManager;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

import org.eclipse.draw2d.Animation;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutAnimator;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.SWTEventDispatcher;
import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.internal.FileImageDataProvider;
import org.eclipse.draw2d.internal.InternalDraw2dUtils;

/**
 * Holds the nodes and connections for the graph.
 *
 * @author Chris Callendar
 *
 * @author Ian Bull
 *
 * @author Sebastian Hollersbacher
 * @since 1.0
 */
public class Graph extends FigureCanvas implements IContainer2 {

	// CLASS CONSTANTS
	public static final int ANIMATION_TIME = 500;
	public static final int FISHEYE_ANIMATION_TIME = 100;
	private static final Image BACK_ARROW = FileImageDataProvider.createImage(Graph.class, "/icons/back_arrow.svg"); //$NON-NLS-1$

	// @tag CGraph.Colors : These are the colour constants for the graph, they
	// are disposed on clean-up
	public Color LIGHT_BLUE = null;
	public Color LIGHT_BLUE_CYAN = null;
	public Color GREY_BLUE = null;
	public Color DARK_BLUE = null;
	public Color LIGHT_YELLOW = null;

	public Color HIGHLIGHT_COLOR = ColorConstants.yellow;
	public Color HIGHLIGHT_ADJACENT_COLOR = ColorConstants.orange;
	public Color DEFAULT_NODE_COLOR = LIGHT_BLUE;

	/**
	 * These are all the children of this graph. These lists contains all nodes and
	 * connections that have added themselves to this graph.
	 */
	private final List<GraphNode> nodes;
	protected List<GraphConnection> connections;
	private List<GraphItem> selectedItems = null;
	Set<IFigure> subgraphFigures;
	private HideNodeHelper hoverNode = null;
	IFigure fisheyedFigure = null;
	private final List<FisheyeListener> fisheyeListeners = new CopyOnWriteArrayList<>();
	private List<SelectionListener> selectionListeners = null;

	/** This maps all visible nodes to their model element. */
	private HashMap<IFigure, GraphItem> figure2ItemMap = null;

	/** Maps user nodes to internal nodes */
	private int connectionStyle;
	private int nodeStyle;
	@SuppressWarnings("removal")
	private List<ConstraintAdapter> constraintAdapters;
	private ScalableFreeformLayeredPane fishEyeLayer = null;
	private InternalLayoutContext layoutContext = null;
	private volatile boolean shouldSheduleLayout;
	private volatile Runnable scheduledLayoutRunnable = null;
	private volatile boolean scheduledLayoutClean = false;
	private Dimension preferredSize = null;
	int style = 0;

	private ScalableFreeformLayeredPane rootlayer;
	private ZestRootLayer zestRootLayer;

	private boolean enableHideNodes;
	private ConnectionRouter defaultConnectionRouter;
	private ZoomManager zoomManager = null;

	private final ZoomGestureListener zoomListener;
	private final RotateGestureListener rotateListener;

	/**
	 * Constructor for a Graph. This widget represents the root of the graph, and
	 * can contain graph items such as graph nodes and graph connections.
	 *
	 * @param parent
	 * @param style  The SWT style used for the underlying {@link FigureCanvas}. See
	 *               {@link FigureCanvas#ACCEPTED_STYLES} for a list of all
	 *               supported styles. Use {@link #setGraphStyle(int)} to set the
	 *               Zest-specific styles.
	 */
	public Graph(Composite parent, int style) {
		this(parent, style, false);
	}

	/**
	 * Constructor for a Graph. This widget represents the root of the graph, and
	 * can contain graph items such as graph nodes and graph connections.
	 *
	 * @param parent
	 * @param style           The SWT style used for the underlying
	 *                        {@link FigureCanvas}. See
	 *                        {@link FigureCanvas#ACCEPTED_STYLES} for a list of all
	 *                        supported styles. Use {@link #setGraphStyle(int)} to
	 *                        set the Zest-specific styles.
	 * @param enableHideNodes
	 * @since 1.8
	 */
	public Graph(Composite parent, int style, boolean enableHideNodes) {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		this.setBackground(ColorConstants.white);

		LIGHT_BLUE = new Color(Display.getDefault(), 216, 228, 248);
		LIGHT_BLUE_CYAN = new Color(Display.getDefault(), 213, 243, 255);
		GREY_BLUE = new Color(Display.getDefault(), 139, 150, 171);
		DARK_BLUE = new Color(Display.getDefault(), 1, 70, 122);
		LIGHT_YELLOW = new Color(Display.getDefault(), 255, 255, 206);

		this.setViewport(new FreeformViewport());

		this.getVerticalBar().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Graph.this.redraw();
			}

		});
		this.getHorizontalBar().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Graph.this.redraw();
			}
		});

		// @tag CGraph.workaround : this allows me to handle mouse events
		// outside of the canvas
		this.getLightweightSystem().setEventDispatcher(new SWTEventDispatcher() {
			@Override
			public void dispatchMouseMoved(org.eclipse.swt.events.MouseEvent me) {
				super.dispatchMouseMoved(me);

				// If the current event is null, return
				if (getCurrentEvent() == null) {
					return;
				}

				if (getMouseTarget() == null) {
					setMouseTarget(getRoot());
				}
				if ((me.stateMask & SWT.BUTTON_MASK) != 0) {
					// Sometimes getCurrentEvent() returns null
					getMouseTarget().handleMouseDragged(getCurrentEvent());
				} else {
					getMouseTarget().handleMouseMoved(getCurrentEvent());
				}
			}
		});

		this.setContents(createLayers());
		GraphDragSupport dragSupport = createGraphDragSupport();
		this.getLightweightSystem().getRootFigure().addMouseListener(dragSupport);
		this.getLightweightSystem().getRootFigure().addMouseMotionListener(dragSupport);

		this.nodes = new ArrayList<>();
		this.preferredSize = new Dimension(-1, -1);
		this.connectionStyle = ZestStyles.NONE;
		this.nodeStyle = ZestStyles.NONE;
		this.connections = new ArrayList<>();
		this.constraintAdapters = new ArrayList<>();
		this.selectedItems = new ArrayList<>();
		this.selectionListeners = new CopyOnWriteArrayList<>();
		this.figure2ItemMap = new HashMap<>();
		this.enableHideNodes = enableHideNodes;
		this.subgraphFigures = new HashSet<>();
		this.zoomListener = new ZoomGestureListener();
		this.rotateListener = new RotateGestureListener();

		if (InternalDraw2dUtils.disableAutoscale) {
			InternalDraw2dUtils.configureForAutoscalingMode(this);
			addListener(SWT.ZoomChanged, event -> rootlayer.setScale(event.detail / 100.0));
			rootlayer.setScale(InternalDraw2dUtils.calculateScale(this));
		}

		this.addPaintListener(event -> {
			if (shouldSheduleLayout) {
				applyLayoutInternal(true);
				shouldSheduleLayout = false;
			}

			/*
			 * Iterator iterator = getNodes().iterator(); while (iterator.hasNext()) {
			 * GraphNode node = (GraphNode) iterator.next(); node.paint(); }
			 */
		});

		this.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				if (preferredSize.width == -1 || preferredSize.height == -1) {
					internalGetLayoutContext().fireBoundsChangedEvent();
				}
			}
		});
		this.addGestureListener(zoomListener);
		this.addGestureListener(rotateListener);
		this.addDisposeListener(event -> release());
	}

	/**
	 * This adds a listener to the set of listeners that will be called when a
	 * selection event occurs.
	 *
	 * @param selectionListener
	 */
	public void addSelectionListener(SelectionListener selectionListener) {
		if (!selectionListeners.contains(selectionListener)) {
			selectionListeners.add(selectionListener);
		}
	}

	public void removeSelectionListener(SelectionListener selectionListener) {
		if (selectionListeners.contains(selectionListener)) {
			selectionListeners.remove(selectionListener);
		}
	}

	/**
	 * Gets a list of the GraphModelNode children objects under the root node in
	 * this diagram. If the root node is null then all the top level nodes are
	 * returned.
	 *
	 * @return List of GraphModelNode objects
	 */

	@Override
	public List<? extends GraphNode> getNodes() {
		return nodes;
	}

	/**
	 * Adds a new constraint adapter to the list of constraint adapters
	 *
	 * @param constraintAdapter
	 * @deprecated No longer used in Zest 2.x. This class will be removed in a
	 *             future release in accordance with the two year deprecation
	 *             policy.
	 */
	@Deprecated(since = "2.0", forRemoval = true)
	public void addConstraintAdapter(ConstraintAdapter constraintAdapter) {
		this.constraintAdapters.add(constraintAdapter);
	}

	/**
	 * Sets the constraint adapters on this model
	 *
	 * @param constraintAdapters
	 * @deprecated No longer used in Zest 2.x. This class will be removed in a
	 *             future release in accordance with the two year deprecation
	 *             policy.
	 */
	@Deprecated(since = "2.0", forRemoval = true)
	public void setConstraintAdapters(List<ConstraintAdapter> constraintAdapters) {
		this.constraintAdapters = constraintAdapters;
	}

	/**
	 * Gets the root layer for this graph
	 */
	public ScalableFigure getRootLayer() {
		return rootlayer;
	}

	/**
	 * Sets the default connection style.
	 *
	 * @param connectionStyle style the connection style to set
	 * @see ZestStyles
	 */
	public void setConnectionStyle(int connectionStyle) {
		this.connectionStyle = connectionStyle;
	}

	/**
	 * Gets the default connection style.
	 *
	 * @return the connection style
	 * @see ZestStyles
	 */
	public int getConnectionStyle() {
		return connectionStyle;
	}

	/**
	 * Sets the default node style.
	 *
	 * @param nodeStyle the node style to set
	 * @see ZestStyles
	 */
	public void setNodeStyle(int nodeStyle) {
		this.nodeStyle = nodeStyle;
	}

	/**
	 * Gets the default node style.
	 *
	 * @return the node style
	 * @see ZestStyles
	 */
	public int getNodeStyle() {
		return nodeStyle;
	}

	/**
	 * Sets the default graph style.
	 *
	 * @param style the graph style to set.
	 * @since 1.15
	 * @see ZestStyles
	 */
	public final void setGraphStyle(int style) {
		// Remove old Gesture listeners (if not already disabled)
		if (!ZestStyles.checkStyle(this.style, ZestStyles.GESTURES_DISABLED)) {
			removeGestureListener(zoomListener);
			removeGestureListener(rotateListener);
		}
		this.style = style;
		// Add new Gesture listeners (if not disabled)
		if (!ZestStyles.checkStyle(this.style, ZestStyles.GESTURES_DISABLED)) {
			addGestureListener(zoomListener);
			addGestureListener(rotateListener);
		}
	}

	/**
	 * Gets the default graph style.
	 *
	 * @return the default graph style
	 * @since 1.15
	 */
	public final int getGraphStyle() {
		return style;
	}

	/**
	 * Gets the list of GraphModelConnection objects.
	 *
	 * @return list of GraphModelConnection objects
	 */
	@Override
	public List<? extends GraphConnection> getConnections() {
		return this.connections;
	}

	/**
	 * Changes the selection to the list of items
	 *
	 * @param items
	 */
	public void setSelection(GraphItem[] items) {
		clearSelection();
		if (items != null) {
			for (GraphItem item : items) {
				if (item != null) {
					select(item);
				}
			}
		}
	}

	public void selectAll() {
		setSelection(nodes.toArray(new GraphItem[] {}));
	}

	/**
	 * Gets the list of currently selected graph items
	 *
	 * @return Currently selected graph items
	 */
	public List<? extends GraphItem> getSelection() {
		return selectedItems;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.swt.widgets.Widget#toString()
	 */
	@Override
	public String toString() {
		return "GraphModel {%d nodes, %d connections}".formatted(nodes.size(), connections.size()); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.mylar.zest.core.internal.graphmodel.IGraphItem#getGraphModel
	 * ()
	 *
	 * @deprecated Use {@link #getGraph()} instead.
	 */
	@Deprecated(since = "1.12", forRemoval = true)
	public Graph getGraphModel() {
		return this;
	}

	/**
	 * Dispose of the nodes and edges when the graph is disposed.
	 */
	@Override
	public void dispose() {
		release();
		super.dispose();
	}

	/**
	 * Runs the layout on this graph. If the view is not visible layout will be
	 * deferred until after the view is available.
	 */
	@Override
	public void applyLayout() {
		scheduleLayoutOnReveal(true);
	}

	/**
	 * Apply this graphs's layout cleanly and display all changes.
	 *
	 * @since 1.14
	 */
	@SuppressWarnings("removal")
	public void applyLayoutNow() {
		if (getLayoutAlgorithm() instanceof LayoutAlgorithm.Zest1) {
			applyLayoutInternal(true);
		} else {
			internalGetLayoutContext().applyLayout(true);
			layoutContext.flushChanges(false);
		}
	}

	/**
	 * Enables or disables dynamic layout (that is layout algorithm performing
	 * layout in background or when certain events occur). Dynamic layout should be
	 * disabled before doing a long series of changes in the graph to make sure that
	 * layout algorithm won't interfere with these changes.
	 *
	 * Enabling dynamic layout causes the layout algorithm to be applied even if
	 * it's not actually a dynamic algorithm.
	 *
	 * @param enabled
	 *
	 * @since 1.14
	 */
	public void setDynamicLayout(boolean enabled) {
		if (getLayoutContext().isBackgroundLayoutEnabled() != enabled) {
			layoutContext.setBackgroundLayoutEnabled(enabled);
			if (enabled) {
				scheduleLayoutOnReveal(false);
			}
		}
	}

	/**
	 *
	 * @return true if dynamic layout is enabled (see
	 *         {@link #setDynamicLayout(boolean)})
	 * @since 1.14
	 */
	public boolean isDynamicLayoutEnabled() {
		return getLayoutContext().isBackgroundLayoutEnabled();
	}

	/**
	 * Sets the preferred size of the layout area. Size of ( -1, -1) uses the
	 * current canvas size.
	 *
	 * @param width
	 * @param height
	 */
	public void setPreferredSize(int width, int height) {
		this.preferredSize = new Dimension(width, height);
		internalGetLayoutContext().fireBoundsChangedEvent();
	}

	/**
	 * @return the preferred size of the layout area.
	 * @since 1.14
	 */
	public Dimension getPreferredSize() {
		if (preferredSize.width < 0 || preferredSize.height < 0) {
			org.eclipse.swt.graphics.Point size = getSize();
			double scale = getZoomManager().getZoom();
			return new Dimension((int) (size.x / scale + 0.5), (int) (size.y / scale + 0.5));
		}
		return preferredSize;
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

	InternalLayoutContext internalGetLayoutContext() {
		return (InternalLayoutContext) getLayoutContext();
	}

	/**
	 * @param algorithm
	 */
	@Override
	public void setLayoutAlgorithm(LayoutAlgorithm algorithm, boolean applyLayout) {
		internalGetLayoutContext().setLayoutAlgorithm(algorithm);
		if (applyLayout) {
			applyLayout();
		}
	}

	/**
	 * @since 1.14
	 */
	public void setSubgraphFactory(SubgraphFactory factory) {
		internalGetLayoutContext().setSubgraphFactory(factory);
	}

	/**
	 * @since 1.14
	 */
	public SubgraphFactory getSubgraphFactory() {
		return internalGetLayoutContext().getSubgraphFactory();
	}

	/**
	 * @since 1.14
	 */
	public void setExpandCollapseManager(ExpandCollapseManager expandCollapseManager) {
		getLayoutContext().setExpandCollapseManager(expandCollapseManager);
	}

	/**
	 * @since 1.14
	 */
	public ExpandCollapseManager getExpandCollapseManager() {
		return getLayoutContext().getExpandCollapseManager();
	}

	/**
	 * Adds a filter used for hiding elements from layout algorithm.
	 *
	 * NOTE: If a node or subgraph if filtered out, all connections adjacent to it
	 * should also be filtered out. Otherwise layout algorithm may behave in an
	 * unexpected way.
	 *
	 * @param filter filter to add
	 * @since 1.14
	 */
	public void addLayoutFilter(LayoutFilter filter) {
		internalGetLayoutContext().addFilter(filter);
	}

	public LayoutAlgorithm getLayoutAlgorithm() {
		return internalGetLayoutContext().getLayoutAlgorithm();
	}

	/**
	 * Removes given layout filter. If it had not been added to this graph, this
	 * method does nothing.
	 *
	 * @param filter filter to remove
	 * @since 1.14
	 */
	public void removeLayoutFilter(LayoutFilter filter) {
		internalGetLayoutContext().removeFilter(filter);
	}

	/**
	 * Finds a figure at the location X, Y in the graph
	 *
	 * This point should be translated to relative before calling findFigureAt
	 */
	public IFigure getFigureAt(int x, int y) {
		return this.getContents().findFigureAt(x, y, new TreeSearch() {

			@Override
			public boolean accept(IFigure figure) {
				return true;
			}

			@Override
			public boolean prune(IFigure figure) {
				IFigure parent = figure.getParent();
				// @tag TODO Zest : change these to from getParent to
				// their actual layer names

				if (parent == fishEyeLayer) {
					// If it node is on the fish eye layer, don't worry
					// about it.
					return true;
				}
				if (parent instanceof ContainerFigure && figure instanceof PolylineConnection) {
					return false;
				}
				if (parent == zestRootLayer || parent == zestRootLayer.getParent()
						|| parent == zestRootLayer.getParent().getParent()) {
					return false;
				}
				GraphItem item = figure2ItemMap.get(figure);
				return !((item != null && item.getItemType() == GraphItem.CONTAINER) || figure instanceof FreeformLayer
						|| parent instanceof FreeformLayer || figure instanceof ScrollPane
						|| parent instanceof ScrollPane || parent instanceof ScalableFreeformLayeredPane
						|| figure instanceof ScalableFreeformLayeredPane || figure instanceof FreeformViewport
						|| parent instanceof FreeformViewport);
			}

		});

	}

	/**
	 * @since 1.8
	 */
	public boolean getHideNodesEnabled() {
		return enableHideNodes;
	}

	/**
	 * Creator method for DragSupport
	 *
	 * @return class that implemented GraphDragSupport
	 * @since 1.9
	 */
	protected GraphDragSupport createGraphDragSupport() {
		return new DragSupport();
	}

	// /////////////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS. These are NON API
	// /////////////////////////////////////////////////////////////////////////////////
	private class DragSupport implements GraphDragSupport {
		Point lastLocation = null;
		IFigure draggedSubgraphFigure = null;
		/** locations of dragged items relative to cursor position */
		List<Point> relativeLocations = new ArrayList<>();
		GraphItem fisheyedItem = null;
		boolean isDragging = false;

		@Override
		public void mouseDragged(org.eclipse.draw2d.MouseEvent me) {
			if (!isDragging) {
				return;
			}
			if (selectedItems.isEmpty()) {
				IFigure figureUnderMouse = getFigureAt(lastLocation.x, lastLocation.y);
				if (subgraphFigures.contains(figureUnderMouse)) {
					draggedSubgraphFigure = figureUnderMouse;
				}
			}

			Point mousePoint = new Point(me.x, me.y);
			if (!selectedItems.isEmpty() || draggedSubgraphFigure != null) {

				if (relativeLocations.isEmpty()) {
					for (GraphItem item : selectedItems) {
						if ((item.getItemType() == GraphItem.NODE) || (item.getItemType() == GraphItem.CONTAINER)) {
							relativeLocations.add(getRelativeLocation(item.getFigure()));
						}
					}
					if (draggedSubgraphFigure != null) {
						relativeLocations.add(getRelativeLocation(draggedSubgraphFigure));
					}
				}

				Iterator<Point> locationsIterator = relativeLocations.iterator();
				for (GraphItem item : selectedItems) {
					if ((item.getItemType() == GraphItem.NODE) || (item.getItemType() == GraphItem.CONTAINER)) {
						Point pointCopy = mousePoint.getCopy();
						Point relativeLocation = locationsIterator.next();

						item.getFigure().getParent().translateToRelative(pointCopy);
						item.getFigure().getParent().translateFromParent(pointCopy);

						((GraphNode) item).setLocation(relativeLocation.x + pointCopy.x,
								relativeLocation.y + pointCopy.y);
					} else {
						// There is no movement for connection
					}
				}
				if (draggedSubgraphFigure != null) {
					Point pointCopy = mousePoint.getCopy();
					draggedSubgraphFigure.getParent().translateToRelative(pointCopy);
					draggedSubgraphFigure.getParent().translateFromParent(pointCopy);
					Point relativeLocation = locationsIterator.next();
					pointCopy.x += relativeLocation.x;
					pointCopy.y += relativeLocation.y;

					draggedSubgraphFigure.setLocation(pointCopy);
				}
			}
		}

		private Point getRelativeLocation(IFigure figure) {
			Point location = figure.getBounds().getTopLeft();
			Point mousePointCopy = lastLocation.getCopy();
			figure.getParent().translateToRelative(mousePointCopy);
			figure.getParent().translateFromParent(mousePointCopy);
			location.x -= mousePointCopy.x;
			location.y -= mousePointCopy.y;
			return location;
		}

		@Override
		public void mouseEntered(org.eclipse.draw2d.MouseEvent me) {

		}

		@Override
		public void mouseExited(org.eclipse.draw2d.MouseEvent me) {

		}

		@Override
		public void mouseHover(org.eclipse.draw2d.MouseEvent me) {

		}

		/**
		 * This tracks whenever a mouse moves. The only thing we care about is
		 * fisheye(ing) nodes. This means whenever the mouse moves we check if we need
		 * to fisheye on a node or not.
		 */
		@Override
		public void mouseMoved(org.eclipse.draw2d.MouseEvent me) {
			Point mousePoint = new Point(me.x, me.y);
			getRootLayer().translateToRelative(mousePoint);
			IFigure figureUnderMouse = getFigureAt(mousePoint.x, mousePoint.y);

			if (figureUnderMouse != null) {
				// There is a figure under this mouse
				GraphItem itemUnderMouse = figure2ItemMap.get(figureUnderMouse);
				if (itemUnderMouse instanceof GraphNode node) {
					hoverNode = node.getHideNodeHelper();
					if (hoverNode != null) {
						hoverNode.setHideButtonVisible(true);
						hoverNode.setRevealButtonVisible(true);
					}
				} else {
					if (hoverNode != null) {
						hoverNode.setHideButtonVisible(false);
						hoverNode.setRevealButtonVisible(false);
						hoverNode = null;
					}
				}

				if (itemUnderMouse == fisheyedItem) {
					return;
				}
				if (fisheyedItem != null) {
					((GraphNode) fisheyedItem).fishEye(false, true);
					fisheyedItem = null;
				}
				if (itemUnderMouse != null && itemUnderMouse.getItemType() == GraphItem.NODE) {
					fisheyedItem = itemUnderMouse;
					IFigure fisheyedFigure = ((GraphNode) itemUnderMouse).fishEye(true, true);
					if (fisheyedFigure == null) {
						// If there is no fisheye figure (this means that the
						// node does not support a fish eye)
						// then remove the fisheyed item
						fisheyedItem = null;
					}
				} else if (fisheyedItem != null) {
					((GraphNode) fisheyedItem).fishEye(false, true);
					fisheyedItem = null;
					fisheyedFigure = null;
				}
			} else {
				if (hoverNode != null) {
					hoverNode.setHideButtonVisible(false);
					hoverNode.setRevealButtonVisible(false);
					hoverNode = null;
				}
				if (fisheyedItem != null) {
					((GraphNode) fisheyedItem).fishEye(false, true);
					fisheyedItem = null;
					fisheyedFigure = null;
				}
			}
		}

		@Override
		public void mouseDoubleClicked(org.eclipse.draw2d.MouseEvent me) {
			Point mousePoint = new Point(me.x, me.y);
			getRootLayer().translateToRelative(mousePoint);
			IFigure figureUnderMouse = getFigureAt(mousePoint.x, mousePoint.y);

			if (figureUnderMouse == null) {
				return;
			}

			GraphItem itemUnderMouse = figure2ItemMap.get(figureUnderMouse);
			if (itemUnderMouse instanceof GraphContainer container) {
				// GraphContainer under this mouse

				Display display = Display.getCurrent();
				Shell shell = display.getActiveShell();
				shell.setLayout(new FillLayout());

				String oldShellLabel = display.getActiveShell().getText();
				StringBuilder labelBuilder = new StringBuilder();
				IContainer currentContainer = container;
				while (currentContainer instanceof GraphContainer current) {
					labelBuilder.insert(0, current.getText());
					labelBuilder.insert(0, '/');
					currentContainer = current.getParent();
				}
				labelBuilder.insert(0, oldShellLabel);
				shell.setText(labelBuilder.toString());

				Graph g = new Graph(shell, SWT.NONE);
				container.getGraph().setParent(new Shell()); // remove old graph from shell
				shell.layout();

				for (GraphNode node : container.getNodes()) {
					container.graph.removeNode(node); // remove nodes from old graph
					g.addNode(node); // add node to new graph
					g.registerItem(node); // register figure in new graph
					node.parent = g; // change parent and graph of node
					node.graph = g;
					node.setVisible(true); // make sure the nodes are visible
					node.unhighlight();
					HideNodeHelper hideNodeHelper = node.getHideNodeHelper();
					if (hideNodeHelper != null) {
						hideNodeHelper.resetCounter();
					}

					if (node instanceof GraphContainer containerNode) {
						g.registerChildrenOfContainer(containerNode, true); // recursively add childNodes to graph
					}
				}
				for (GraphNode node : g.getNodes()) {
					for (GraphConnection connection : node.getTargetConnections()) {
						container.graph.removeConnection(connection);
						g.addConnection(connection, true);
						g.registerItem(connection);
					}
					for (GraphConnection connection : node.getSourceConnections()) {
						container.graph.removeConnection(connection);
						g.addConnection(connection, true);
						g.registerItem(connection);
					}
				}
				g.setLayoutAlgorithm(container.getLayoutAlgorithm(), false);

				Button backButton = new Button(BACK_ARROW);
				backButton.setBounds(new Rectangle(new Point(0, 0), backButton.getPreferredSize()));
				backButton.addActionListener(event -> {
					for (GraphNode node : new ArrayList<>(g.getNodes())) {
						g.removeNode(node); // remove nodes from graph
						container.addNode(node); // add nodes to container
						container.graph.registerItem(node); // register figure in old graph
						node.parent = container; // change parent and graph of node
						node.graph = container.getGraph();
						node.unhighlight();

						if (node instanceof GraphContainer containerNode) {
							registerChildrenOfContainer(containerNode, false); // recursively add childNodes to graph
						}
					}
					for (GraphConnection connection : new ArrayList<GraphConnection>(g.getConnections())) {
						g.removeConnection(connection);
						container.graph.addConnection(connection, false);
						container.graph.registerItem(connection);
						connection.registerConnection(connection.getSource(), connection.getDestination());
						connection.setVisible(true);
					}

					container.applyLayout();

					g.setParent(new Shell()); // remove graph from shell
					container.getGraph().setParent(shell);
					shell.layout();
					shell.setText(oldShellLabel);

					g.release();
				});

				g.rootlayer.add(backButton);

				shell.addDisposeListener(e -> {
					g.connections.clear();
					g.nodes.clear();
					g.release();
				});
			}
		}

		@Override
		public void mousePressed(org.eclipse.draw2d.MouseEvent me) {
			isDragging = true;
			Point mousePoint = new Point(me.x, me.y);
			lastLocation = mousePoint.getCopy();

			getRootLayer().translateToRelative(mousePoint);

			if ((me.getState() & SWT.MOD3) != 0) {
				if ((me.getState() & SWT.MOD2) == 0) {
					double scale = getRootLayer().getScale();
					scale *= 1.05;
					getRootLayer().setScale(scale);
					Point newMousePoint = mousePoint.getCopy().scale(1.05);
					Point delta = new Point(newMousePoint.x - mousePoint.x, newMousePoint.y - mousePoint.y);
					Point newViewLocation = getViewport().getViewLocation().getCopy().translate(delta);
					getViewport().setViewLocation(newViewLocation);
					clearSelection();
				} else {
					double scale = getRootLayer().getScale();
					scale /= 1.05;
					getRootLayer().setScale(scale);
					Point newMousePoint = mousePoint.getCopy().scale(1 / 1.05);
					Point delta = new Point(newMousePoint.x - mousePoint.x, newMousePoint.y - mousePoint.y);
					Point newViewLocation = getViewport().getViewLocation().getCopy().translate(delta);
					getViewport().setViewLocation(newViewLocation);
					clearSelection();
				}
			} else {
				boolean hasSelection = !selectedItems.isEmpty();
				IFigure figureUnderMouse = getFigureAt(mousePoint.x, mousePoint.y);
				getRootLayer().translateFromParent(mousePoint);

				if (figureUnderMouse != null) {
					figureUnderMouse.getParent().translateFromParent(mousePoint);
				}
				// If the figure under the mouse is the canvas, and CTRL/CMD is
				// not being held down, then select nothing
				if (figureUnderMouse == null || figureUnderMouse == Graph.this) {
					if ((me.getState() & SWT.MOD1) == 0) {
						clearSelection();
						if (hasSelection) {
							fireWidgetSelectedEvent(null);
							hasSelection = false;
						}
					}
					return;
				}

				GraphItem itemUnderMouse = figure2ItemMap.get(figureUnderMouse);
				if (itemUnderMouse == null) {
					if ((me.getState() & SWT.MOD1) != 0) {
						clearSelection();
						if (hasSelection) {
							fireWidgetSelectedEvent(null);
							hasSelection = false;
						}
					}
					return;
				}
				if (selectedItems.contains(itemUnderMouse)) {
					// We have already selected this node, and CTRL/CMD is being
					// held down, remove this selection
					// @tag Zest.selection : This deselects when you have
					// CTRL/CMD pressed
					if ((me.getState() & SWT.MOD1) != 0) {
						selectedItems.remove(itemUnderMouse);
						(itemUnderMouse).unhighlight();
						fireWidgetSelectedEvent(itemUnderMouse);
					}
					return;
				}

				if ((me.getState() & SWT.MOD1) == 0) {
					clearSelection();
				}

				if (itemUnderMouse.getItemType() == GraphItem.NODE) {
					// @tag Zest.selection : This is where the nodes are
					// selected
					selectedItems.add(itemUnderMouse);
					((GraphNode) itemUnderMouse).highlight();
					fireWidgetSelectedEvent(itemUnderMouse);
				} else if (itemUnderMouse.getItemType() == GraphItem.CONNECTION) {
					selectedItems.add(itemUnderMouse);
					((GraphConnection) itemUnderMouse).highlight();
					fireWidgetSelectedEvent(itemUnderMouse);

				} else if (itemUnderMouse.getItemType() == GraphItem.CONTAINER) {
					selectedItems.add(itemUnderMouse);
					((GraphContainer) itemUnderMouse).highlight();
					fireWidgetSelectedEvent(itemUnderMouse);
				}
			}

		}

		@Override
		public void mouseReleased(org.eclipse.draw2d.MouseEvent me) {
			isDragging = false;
			relativeLocations.clear();
			draggedSubgraphFigure = null;
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.swt.widgets.Widget#notifyListeners(int,
	 * org.eclipse.swt.widgets.Event)
	 */
	@Override
	public void notifyListeners(int eventType, Event event) {
		super.notifyListeners(eventType, event);
		if (eventType == SWT.Selection && event != null) {
			notifySelectionListeners(new SelectionEvent(event));
		}
	}

	private void release() {
		while (!nodes.isEmpty()) {
			GraphNode node = nodes.get(0);
			if (node != null) {
				node.dispose();
			}
		}
		while (!connections.isEmpty()) {
			GraphConnection connection = connections.get(0);
			if (connection != null) {
				connection.dispose();
			}
		}

		if (LIGHT_BLUE != null) {
			LIGHT_BLUE.dispose();
		}
		if (LIGHT_BLUE_CYAN != null) {
			LIGHT_BLUE_CYAN.dispose();
		}
		if (GREY_BLUE != null) {
			GREY_BLUE.dispose();
		}
		if (DARK_BLUE != null) {
			DARK_BLUE.dispose();
		}
		if (LIGHT_YELLOW != null) {
			LIGHT_YELLOW.dispose();
		}
	}

	private void clearSelection() {
		for (GraphItem item : new ArrayList<>(selectedItems)) {
			deselect(item);
		}
	}

	private void fireWidgetSelectedEvent(Item item) {
		Event swtEvent = new Event();
		swtEvent.item = item;
		swtEvent.widget = this;
		notifySelectionListeners(new SelectionEvent(swtEvent));
	}

	private void notifySelectionListeners(SelectionEvent event) {
		for (SelectionListener listener : selectionListeners) {
			(listener).widgetSelected(event);
		}
	}

	private void deselect(GraphItem item) {
		selectedItems.remove(item);
		item.unhighlight();
		setNodeSelected(item, false);
	}

	private void select(GraphItem item) {
		selectedItems.add(item);
		item.highlight();
		setNodeSelected(item, true);
	}

	private static void setNodeSelected(GraphItem item, boolean selected) {
		if (item instanceof GraphNode node) {
			node.setSelected(selected);
		}
	}

	/**
	 * Converts the list of GraphModelConnection objects into an array and returns
	 * it.
	 *
	 * @return GraphModelConnection[]
	 */
	GraphConnection[] getConnectionsArray() {
		GraphConnection[] connsArray = new GraphConnection[connections.size()];
		return connections.toArray(connsArray);
	}

	/**
	 * @deprecated Not used in Zest 2.x. This method will be removed in a future
	 *             release in accordance with the two year deprecation policy.
	 */
	@Deprecated(since = "1.12", forRemoval = true)
	LayoutRelationship[] getConnectionsToLayout(List<GraphNode> nodesToLayout) {
		// @tag zest.bug.156528-Filters.follows : make sure not to layout
		// filtered connections, if the style says so.
		LayoutRelationship[] entities;
		if (ZestStyles.checkStyle(style, ZestStyles.IGNORE_INVISIBLE_LAYOUT)) {
			LinkedList<LayoutRelationship> connectionList = new LinkedList<>();
			for (GraphConnection next : this.getConnections()) {
				if (next.isVisible() && nodesToLayout.contains(next.getSource())
						&& nodesToLayout.contains(next.getDestination())) {
					connectionList.add(next.getLayoutRelationship());
				}
			}
			entities = connectionList.toArray(new LayoutRelationship[] {});
		} else {
			LinkedList<LayoutRelationship> nodeList = new LinkedList<>();
			for (GraphConnection next : this.getConnections()) {
				if (nodesToLayout.contains(next.getSource()) && nodesToLayout.contains(next.getDestination())) {
					nodeList.add(next.getLayoutRelationship());
				}
			}
			entities = nodeList.toArray(new LayoutRelationship[] {});
		}
		return entities;
	}

	/**
	 * @deprecated Not used in Zest 2.x. This method will be removed in a future
	 *             release in accordance with the two year deprecation policy.
	 */
	@Deprecated(since = "1.12", forRemoval = true)
	LayoutEntity[] getNodesToLayout(List<? extends GraphNode> nodes) {
		// @tag zest.bug.156528-Filters.follows : make sure not to layout
		// filtered nodes, if the style says so.
		LayoutEntity[] entities;
		if (ZestStyles.checkStyle(style, ZestStyles.IGNORE_INVISIBLE_LAYOUT)) {
			LinkedList<LayoutEntity> nodeList = new LinkedList<>();
			for (GraphNode next : nodes) {
				if (next.isVisible()) {
					nodeList.add(next.getLayoutEntity());
				}
			}
			entities = nodeList.toArray(new LayoutEntity[] {});
		} else {
			LinkedList<LayoutEntity> nodeList = new LinkedList<>();
			for (GraphNode next : nodes) {
				nodeList.add(next.getLayoutEntity());
			}
			entities = nodeList.toArray(new LayoutEntity[] {});
		}
		return entities;
	}

	/**
	 * Clear the graph of all its content.
	 *
	 * @since 1.14
	 */
	public void clear() {
		for (GraphConnection element : new ArrayList<>(connections)) {
			removeConnection(element);
		}
		for (IFigure element : new HashSet<>(subgraphFigures)) {
			removeSubgraphFigure(element);
		}
		for (GraphNode element : new ArrayList<>(nodes)) {
			removeNode(element);
		}
	}

	void removeConnection(GraphConnection connection) {
		IFigure figure = connection.getConnectionFigure();
		PolylineConnection sourceContainerConnectionFigure = connection.getSourceContainerConnectionFigure();
		PolylineConnection targetContainerConnectionFigure = connection.getTargetContainerConnectionFigure();
		connection.removeFigure();
		this.getConnections().remove(connection);
		this.selectedItems.remove(connection);
		figure2ItemMap.remove(figure);
		if (sourceContainerConnectionFigure != null) {
			figure2ItemMap.remove(sourceContainerConnectionFigure);
		}
		if (targetContainerConnectionFigure != null) {
			figure2ItemMap.remove(targetContainerConnectionFigure);
		}
		internalGetLayoutContext().fireConnectionRemovedEvent(connection.getLayout());
	}

	void removeNode(GraphNode node) {
		IFigure figure = node.getNodeFigure();
		if (figure.getParent() != null) {
			figure.getParent().remove(figure);
		}
		this.getNodes().remove(node);
		if (this.getSelection() != null) {
			this.getSelection().remove(node);
		}
		figure2ItemMap.remove(figure);
		node.getLayout().dispose();
	}

	void addConnection(GraphConnection connection, boolean addToEdgeLayer) {
		connections.add(connection);
		if (addToEdgeLayer) {
			zestRootLayer.addConnection(connection.getFigure());
		}
		internalGetLayoutContext().fireConnectionAddedEvent(connection.getLayout());
	}

	/*
	 * public void redraw() {
	 *
	 * Iterator iterator = this.getConnections().iterator(); while
	 * (iterator.hasNext()) { GraphConnection connection = (GraphConnection)
	 * iterator.next(); IFigure figure = connection.getFigure(); if
	 * (!zestRootLayer.getChildren().contains(figure)) { if (true || false || false)
	 * { zestRootLayer.addConnection(connection.getFigure()); } } } iterator =
	 * this.getNodes().iterator(); while (iterator.hasNext()) { GraphNode graphNode
	 * = (GraphNode) iterator.next(); IFigure figure = graphNode.getFigure(); if
	 * (!zestRootLayer.getChildren().contains(figure)) {
	 * zestRootLayer.addNode(graphNode.getFigure()); } }
	 *
	 * super.redraw();
	 *
	 * }
	 */

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	@Override
	public void addNode(GraphNode node) {
		nodes.add(node);
		zestRootLayer.addNode(node.getNodeFigure());
		internalGetLayoutContext().fireNodeAddedEvent(node.getLayout());
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	@Override
	public void addSubgraphFigure(IFigure figure) {
		zestRootLayer.addSubgraph(figure);
		subgraphFigures.add(figure);
	}

	void removeSubgraphFigure(IFigure figure) {
		subgraphFigures.remove(figure);
		figure.getParent().remove(figure);
	}

	void registerItem(GraphItem item) {
		if (item.getItemType() == GraphItem.NODE) {
			IFigure figure = ((GraphNode) item).getNodeFigure();
			figure2ItemMap.put(figure, item);
		} else if (item.getItemType() == GraphItem.CONNECTION) {
			IFigure figure = item.getFigure();
			figure2ItemMap.put(figure, item);
			if (((GraphConnection) item).getSourceContainerConnectionFigure() != null) {
				figure2ItemMap.put(((GraphConnection) item).getSourceContainerConnectionFigure(), item);
			}
			if (((GraphConnection) item).getTargetContainerConnectionFigure() != null) {
				figure2ItemMap.put(((GraphConnection) item).getTargetContainerConnectionFigure(), item);
			}
		} else if (item.getItemType() == GraphItem.CONTAINER) {
			IFigure figure = ((GraphNode) item).getNodeFigure();
			figure2ItemMap.put(figure, item);
			figure = ((GraphNode) item).getModelFigure();
			figure2ItemMap.put(figure, item);
		} else {
			throw new RuntimeException("Unknown item type: " + item.getItemType()); //$NON-NLS-1$
		}
	}

	private void registerChildrenOfContainer(GraphContainer container, boolean addToMap) {
		for (GraphNode node : container.getNodes()) {
			if (node instanceof GraphContainer childContainer) {
				registerChildrenOfContainer(childContainer, addToMap);
			} else {
				node.graph = this;
				node.unhighlight();
				if (addToMap) {
					IFigure figure = node.getFigure();
					figure2ItemMap.put(figure, node);
				}
			}
		}
	}

	/*
	 *
	 *
	 * /** Changes the figure for a particular node
	 */
	void changeNodeFigure(IFigure oldValue, IFigure newFigure, GraphNode graphItem) {
		if (zestRootLayer.getChildren().contains(oldValue)) {
			zestRootLayer.remove(oldValue);
			figure2ItemMap.remove(oldValue);
		}
		figure2ItemMap.put(newFigure, graphItem);
		zestRootLayer.add(newFigure);
	}

	/**
	 * Invoke all the constraint adapters for this constraints
	 *
	 * @param object
	 * @param constraint
	 * @deprecated No longer used in Zest 2.x. This class will be removed in a
	 *             future release in accordance with the two year deprecation
	 *             policy.
	 */
	@Deprecated(since = "2.0", forRemoval = true)
	void invokeConstraintAdapters(Object object, LayoutConstraint constraint) {
		if (constraintAdapters == null) {
			return;
		}
		for (ConstraintAdapter constraintAdapter : constraintAdapters) {
			constraintAdapter.populateConstraint(object, constraint);
		}
	}

	@SuppressWarnings("removal")
	private void applyLayoutInternal(boolean clean) {
		if (internalGetLayoutContext().getLayoutAlgorithm() == null) {
			return;
		}

		if (this.getNodes().isEmpty()) {
			return;
		}
		scheduledLayoutClean = scheduledLayoutClean || clean;
		synchronized (this) {
			if (scheduledLayoutRunnable == null) {
				Display.getDefault().asyncExec(scheduledLayoutRunnable = () -> {
					if (Graph.this.isDisposed()) {
						return;
					}
					int layoutStyle = 0;

					if ((nodeStyle & ZestStyles.NODES_NO_LAYOUT_RESIZE) > 0) {
						layoutStyle = LayoutStyles.NO_LAYOUT_NODE_RESIZING;
					}

					if ((nodeStyle & ZestStyles.NODES_NO_LAYOUT_ANIMATION) == 0) {
						Animation.markBegin();
					}
					if (getLayoutAlgorithm() instanceof LayoutAlgorithm.Zest1 zest1) {
						try {
							zest1.setStyle(zest1.getStyle() | layoutStyle);

							// calculate the size for the layout algorithm
							Dimension d = this.getViewport().getSize();
							d.width = d.width - 10;
							d.height = d.height - 10;

							if (this.preferredSize.width >= 0) {
								d.width = preferredSize.width;
							}
							if (this.preferredSize.height >= 0) {
								d.height = preferredSize.height;
							}

							if (d.isEmpty()) {
								return;
							}
							LayoutRelationship[] connectionsToLayout = getConnectionsToLayout(nodes);
							LayoutEntity[] nodesToLayout = getNodesToLayout(getNodes());

							zest1.applyLayout(nodesToLayout, connectionsToLayout, 0, 0, d.width, d.height, false,
									false);

						} catch (InvalidLayoutConfiguration e) {
							e.printStackTrace();
						}
					} else {
						internalGetLayoutContext().applyLayout(scheduledLayoutClean);
						layoutContext.flushChanges(false);
					}
					Animation.run(ANIMATION_TIME);
					getLightweightSystem().getUpdateManager().performUpdate();
					synchronized (Graph.this) {
						scheduledLayoutRunnable = null;
						scheduledLayoutClean = false;
					}
				});
			}
		}
	}

	/**
	 * Schedules a layout to be performed after the view is revealed (or
	 * immediately, if the view is already revealed).
	 *
	 * @param clean
	 */
	private void scheduleLayoutOnReveal(final boolean clean) {

		final boolean[] isVisibleSync = new boolean[1];
		Display.getDefault().syncExec(() -> isVisibleSync[0] = isVisible());

		if (isVisibleSync[0]) {
			applyLayoutInternal(clean);
		} else {
			shouldSheduleLayout = true;
		}
	}

	/**
	 * Layer creation
	 *
	 * @return IFigure the rootlayer
	 * @since 1.9
	 */
	protected IFigure createLayers() {
		rootlayer = new ScalableFreeformLayeredPane();
		rootlayer.setLayoutManager(new FreeformLayout());
		zestRootLayer = createZestRootLayer();

		zestRootLayer.setLayoutManager(new FreeformLayout());

		fishEyeLayer = new ScalableFreeformLayeredPane();
		fishEyeLayer.setLayoutManager(new FreeformLayout());

		rootlayer.add(zestRootLayer);
		rootlayer.add(fishEyeLayer);

		zestRootLayer.addLayoutListener(LayoutAnimator.getDefault());
		fishEyeLayer.addLayoutListener(LayoutAnimator.getDefault());

		rootlayer.addCoordinateListener(source -> {
			if (preferredSize.width == -1 && preferredSize.height == -1) {
				internalGetLayoutContext().fireBoundsChangedEvent();
			}
		});

		return rootlayer;
	}

	/**
	 * Creator method for ZestRootLayer
	 *
	 * @return new ZestRootLayer instance
	 * @since 1.9
	 */
	@SuppressWarnings("static-method")
	protected ZestRootLayer createZestRootLayer() {
		return new ZestRootLayer();
	}

	/**
	 * This removes the fisheye from the graph. It uses an animation to make the
	 * fisheye shrink, and then it finally clears the fisheye layer. This assumes
	 * that there is ever only 1 node on the fisheye layer at any time.
	 *
	 * @param fishEyeFigure The fisheye figure
	 * @param regularFigure The regular figure (i.e. the non fisheye version)
	 */
	void removeFishEye(final IFigure fishEyeFigure, final IFigure regularFigure, boolean animate) {

		if (!fishEyeLayer.getChildren().contains(fishEyeFigure)) {
			return;
		}
		if (animate) {
			Animation.markBegin();
		}

		Rectangle bounds = regularFigure.getBounds().getCopy();
		regularFigure.translateToAbsolute(bounds);

		double scale = rootlayer.getScale();
		fishEyeLayer.setScale(1 / scale);
		fishEyeLayer.translateToRelative(bounds);
		fishEyeLayer.translateFromParent(bounds);

		fishEyeLayer.setConstraint(fishEyeFigure, bounds);

		for (FisheyeListener listener : fisheyeListeners) {
			listener.fisheyeRemoved(this, regularFigure, fishEyeFigure);
		}

		if (animate) {
			Animation.run(FISHEYE_ANIMATION_TIME * 2);
		}
		this.getRootLayer().getUpdateManager().performUpdate();
		fishEyeLayer.removeAll();
		this.fisheyedFigure = null;

	}

	/**
	 * Replaces the old fisheye figure with a new one.
	 *
	 * @param oldFigure
	 * @param newFigure
	 */
	boolean replaceFishFigure(IFigure oldFigure, IFigure newFigure) {
		if (this.fishEyeLayer.getChildren().contains(oldFigure)) {
			Rectangle bounds = oldFigure.getBounds();
			newFigure.setBounds(bounds);
			// this.fishEyeLayer.getChildren().remove(oldFigure);
			this.fishEyeLayer.remove(oldFigure);
			this.fishEyeLayer.add(newFigure);
			for (FisheyeListener listener : fisheyeListeners) {
				listener.fisheyeReplaced(this, oldFigure, newFigure);
			}
			// this.fishEyeLayer.getChildren().add(newFigure);
			// this.fishEyeLayer.invalidate();
			// this.fishEyeLayer.repaint();
			this.fisheyedFigure = newFigure;
			return true;
		}
		return false;
	}

	/**
	 * Add a fisheye version of the node. This works by animating the change from
	 * the original node to the fisheyed one, and then placing the fisheye node on
	 * the fisheye layer.
	 *
	 * @param startFigure The original node
	 * @param endFigure   The fisheye figure
	 * @param newBounds   The final size of the fisheyed figure
	 */
	void fishEye(IFigure startFigure, IFigure endFigure, Rectangle newBounds, boolean animate) {

		fishEyeLayer.removeAll();
		fisheyedFigure = null;
		if (animate) {
			Animation.markBegin();
		}

		double scale = rootlayer.getScale();
		fishEyeLayer.setScale(1 / scale);

		fishEyeLayer.translateToRelative(newBounds);
		fishEyeLayer.translateFromParent(newBounds);

		Rectangle bounds = startFigure.getBounds().getCopy();
		startFigure.translateToAbsolute(bounds);
		// startFigure.translateToRelative(bounds);
		fishEyeLayer.translateToRelative(bounds);
		fishEyeLayer.translateFromParent(bounds);

		endFigure.setLocation(bounds.getLocation());
		endFigure.setSize(bounds.getSize());
		fishEyeLayer.add(endFigure);
		fishEyeLayer.setConstraint(endFigure, newBounds);

		for (FisheyeListener listener : fisheyeListeners) {
			listener.fisheyeAdded(this, startFigure, endFigure);
		}

		if (animate) {
			Animation.run(FISHEYE_ANIMATION_TIME);
		}
		this.getRootLayer().getUpdateManager().performUpdate();
	}

	@Override
	public Graph getGraph() {
		// @tag refactor : Is this method really needed
		return this.getGraphModel();
	}

	/**
	 * Adds a listener that will be notified when fisheyed figures change in this
	 * graph.
	 *
	 * @param listener listener to add
	 * @since 1.14
	 */
	public void addFisheyeListener(FisheyeListener listener) {
		fisheyeListeners.add(listener);
	}

	/**
	 * @since 1.14
	 */
	public void removeFisheyeListener(FisheyeListener listener) {
		fisheyeListeners.remove(listener);
	}

	@Override
	public int getItemType() {
		return GraphItem.GRAPH;
	}

	/**
	 * Returns the item for the given figure
	 *
	 * @param figure
	 * @return GraphItem for the given IFigure
	 * @since 1.9
	 */
	protected GraphItem getGraphItem(IFigure figure) {
		return figure2ItemMap.get(figure);
	}

	/**
	 * @since 1.14
	 */
	public void setExpanded(GraphNode node, boolean expanded) {
		layoutContext.setExpanded(node.getLayout(), expanded);
		rootlayer.invalidate();
	}

	/**
	 * @since 1.14
	 */
	public boolean canExpand(GraphNode node) {
		return layoutContext.canExpand(node.getLayout());
	}

	/**
	 * @since 1.14
	 */
	public boolean canCollapse(GraphNode node) {
		return layoutContext.canCollapse(node.getLayout());
	}

	/**
	 * @since 1.12
	 */
	@Override
	public Widget getItem() {
		return this;
	}

	/**
	 * @since 1.12
	 */
	@Override
	public DisplayIndependentRectangle getLayoutBounds() {
		Dimension preferredSize = this.getPreferredSize();
		return new DisplayIndependentRectangle(0, 0, preferredSize.width, preferredSize.height);
	}

	/**
	 * Sets the default connection router for the graph view, but does not apply it
	 * retroactively.
	 *
	 * @param defaultConnectionRouter
	 * @since 1.12
	 */
	void setDefaultConnectionRouter(ConnectionRouter defaultConnectionRouter) {
		this.defaultConnectionRouter = defaultConnectionRouter;
	}

	/**
	 * Returns the default connection router for the graph view.
	 *
	 * @return the default connection router; may be null.
	 * @since 1.12
	 */
	ConnectionRouter getDefaultConnectionRouter() {
		return defaultConnectionRouter;
	}

	/**
	 * Sets the default connection router for all connections that have no
	 * connection routers attached to them.
	 *
	 * @since 1.12
	 */
	void applyConnectionRouter() {
		for (GraphConnection conn : getConnections()) {
			conn.getConnectionFigure().setConnectionRouter(defaultConnectionRouter);
		}
		this.getRootLayer().getUpdateManager().performUpdate();
	}

	/**
	 * Updates the connection router and applies to to all existing connections that
	 * have no connection routers set to them.
	 *
	 * @param connectionRouter
	 * @since 1.14
	 */
	public void setRouter(ConnectionRouter connectionRouter) {
		setDefaultConnectionRouter(connectionRouter);
		applyConnectionRouter();
	}

	/**
	 * Returns the ZoomManager component used for scaling the graph widget.
	 *
	 * @return the ZoomManager component
	 * @since 1.14
	 */
	// @tag zest.bug.156286-Zooming.fix.experimental : expose the zoom manager
	// for new actions.
	public ZoomManager getZoomManager() {
		if (zoomManager == null) {
			zoomManager = new ZoomManager(getRootLayer(), getViewport());
		}
		return zoomManager;
	}
}
