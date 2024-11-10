/*******************************************************************************
 * Copyright (c) 2000, 2024 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.editparts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleControlEvent;

import org.eclipse.core.runtime.IAdaptable;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.AccessibleAnchorProvider;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.AccessibleHandleProvider;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeListener;
import org.eclipse.gef.Request;

/**
 * Default implementation for {@link org.eclipse.gef.GraphicalEditPart}.
 * <P>
 * This is an implementation class, and the documentation here is targeted at
 * subclassing this class. Callers of public API should refer to the interface's
 * documentation.
 */
public abstract class AbstractGraphicalEditPart extends AbstractEditPart implements GraphicalEditPart {

	/**
	 * The Figure
	 */
	protected IFigure figure;

	/**
	 * List of <i>source</i> ConnectionEditParts
	 */
	protected List<ConnectionEditPart> sourceConnections;

	/**
	 * List of <i>source</i> ConnectionEditParts
	 */
	protected List<ConnectionEditPart> targetConnections;

	/**
	 * A default implementation of {@link AccessibleEditPart}. Subclasses can extend
	 * this implementation to get base accessibility for free.
	 *
	 * @since 2.0
	 */
	protected abstract class AccessibleGraphicalEditPart extends AccessibleEditPart {
		/**
		 * @see AccessibleEditPart#getChildCount(AccessibleControlEvent)
		 */
		@Override
		public void getChildCount(AccessibleControlEvent e) {
			e.detail = AbstractGraphicalEditPart.this.getChildren().size();
		}

		/**
		 * @see AccessibleEditPart#getChildren(AccessibleControlEvent)
		 */
		@Override
		public void getChildren(AccessibleControlEvent e) {
			ArrayList<Integer> children = new ArrayList<>(AbstractGraphicalEditPart.this.getChildren().size());
			for (EditPart part : AbstractGraphicalEditPart.this.getChildren()) {
				AccessibleEditPart access = part.getAdapter(AccessibleEditPart.class);
				if (access == null) {
					return; // fail if any children aren't accessible.
				}
				children.add(Integer.valueOf(access.getAccessibleID()));
			}
			e.children = children.toArray();
		}

		/**
		 * @see AccessibleEditPart#getLocation(AccessibleControlEvent)
		 */
		@Override
		public void getLocation(AccessibleControlEvent e) {
			Rectangle bounds = getFigure().getBounds().getCopy();
			getFigure().translateToAbsolute(bounds);
			org.eclipse.swt.graphics.Point p = new org.eclipse.swt.graphics.Point(0, 0);
			p = getViewer().getControl().toDisplay(p);
			e.x = bounds.x + p.x;
			e.y = bounds.y + p.y;
			e.width = bounds.width;
			e.height = bounds.height;
		}

		/**
		 * @see AccessibleEditPart#getState(AccessibleControlEvent)
		 */
		@Override
		public void getState(AccessibleControlEvent e) {
			e.detail = ACC.STATE_SELECTABLE | ACC.STATE_FOCUSABLE;
			if (getSelected() != EditPart.SELECTED_NONE) {
				e.detail |= ACC.STATE_SELECTED;
			}
			if (getViewer().getFocusEditPart() == AbstractGraphicalEditPart.this) {
				e.detail |= ACC.STATE_FOCUSED;
			}
		}

		/**
		 * @see AccessibleEditPart#getRole(AccessibleControlEvent)
		 */
		@Override
		public void getRole(AccessibleControlEvent e) {
			e.detail = ACC.ROLE_LABEL;
		}
	}

	/**
	 * The default implementation of {@link AccessibleAnchorProvider} returned in
	 * {@link #getAdapter(Class)}. This implementation creates an accessible
	 * location located along the right edge of the EditPart's Figure.
	 *
	 * @since 2.0
	 */
	protected class DefaultAccessibleAnchorProvider implements AccessibleAnchorProvider {
		private List<Point> getDefaultLocations() {
			List<Point> list = new ArrayList<>();
			Rectangle r = getFigure().getBounds();
			Point p = r.getTopRight().translate(-1, r.height / 3);
			getFigure().translateToAbsolute(p);
			list.add(p);
			return list;
		}

		/**
		 * @see AccessibleAnchorProvider#getSourceAnchorLocations()
		 */
		@Override
		public List<Point> getSourceAnchorLocations() {
			return getDefaultLocations();
		}

		/**
		 * @see AccessibleAnchorProvider#getTargetAnchorLocations()
		 */
		@Override
		public List<Point> getTargetAnchorLocations() {
			return getDefaultLocations();
		}
	}

	static class MergedAccessibleHandles implements AccessibleHandleProvider {
		List<Point> locations = new ArrayList<>();

		MergedAccessibleHandles(Iterable<EditPolicy> iterable) {
			for (EditPolicy policy : iterable) {
				if (policy instanceof IAdaptable adaptable) {
					AccessibleHandleProvider adapter = adaptable.getAdapter(AccessibleHandleProvider.class);
					if (adapter != null) {
						locations.addAll(adapter.getAccessibleHandleLocations());
					}
				}
			}
		}

		@Override
		public List<Point> getAccessibleHandleLocations() {
			return locations;
		}
	}

	/**
	 * Extends {@link AbstractEditPart#activate()} to also activate all
	 * <i>source</i> ConnectionEditParts.
	 *
	 * @see org.eclipse.gef.EditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		getSourceConnections().forEach(ConnectionEditPart::activate);
	}

	/**
	 * Adds the child's Figure to the {@link #getContentPane() contentPane}.
	 *
	 * @see org.eclipse.gef.editparts.AbstractEditPart#addChildVisual(EditPart, int)
	 */
	@Override
	protected void addChildVisual(EditPart childEditPart, int index) {
		IFigure child = ((GraphicalEditPart) childEditPart).getFigure();
		getContentPane().add(child, index);
	}

	/**
	 * @see org.eclipse.gef.GraphicalEditPart#addNodeListener(org.eclipse.gef.NodeListener)
	 */
	@Override
	public void addNodeListener(NodeListener listener) {
		eventListeners.addListener(NodeListener.class, listener);
	}

	/**
	 * @see org.eclipse.gef.EditPart#addNotify()
	 */
	@Override
	public void addNotify() {
		super.addNotify();
		getSourceConnections().forEach(conn -> conn.setSource(this));
		getTargetConnections().forEach(conn -> conn.setTarget(this));
	}

	/**
	 * Adds a <i>source</i> ConnectionEditPart at the specified index. This method
	 * is called from {@link #refreshSourceConnections()}. There should be no reason
	 * to call or override this method. Source connection are created as a result of
	 * overriding {@link #getModelSourceConnections()}.
	 * <P>
	 * {@link #primAddSourceConnection(ConnectionEditPart, int)} is called to
	 * perform the actual update of the {@link #sourceConnections}
	 * <code>List</code>. The connection will have its source set to
	 * <code>this</code>.
	 * <P>
	 * If active, this EditPart will activate the ConnectionEditPart.
	 * <P>
	 * Finally, all {@link NodeListener}s are notified of the new connection.
	 *
	 * @param connection Connection being added
	 * @param index      Index where it is being added
	 */
	protected void addSourceConnection(ConnectionEditPart connection, int index) {
		primAddSourceConnection(connection, index);

		GraphicalEditPart source = (GraphicalEditPart) connection.getSource();
		if (source != null) {
			source.getSourceConnections().remove(connection);
		}

		connection.setSource(this);
		if (isActive()) {
			connection.activate();
		}
		fireSourceConnectionAdded(connection, index);
	}

	/**
	 * Adds a <i>target</i> ConnectionEditPart at the specified index. This method
	 * is called from {@link #refreshTargetConnections()}. There should be no reason
	 * to call or override this method. Target connection are created as a result of
	 * overriding {@link #getModelTargetConnections()}.
	 * <P>
	 * {@link #primAddTargetConnection(ConnectionEditPart, int)} is called to
	 * perform the actual update of the {@link #targetConnections}
	 * <code>List</code>. The connection will have its target set to
	 * <code>this</code>.
	 * <P>
	 * Finally, all {@link NodeListener}s are notified of the new connection.
	 *
	 * @param connection Connection being added
	 * @param index      Index where it is being added
	 */
	protected void addTargetConnection(ConnectionEditPart connection, int index) {
		primAddTargetConnection(connection, index);

		GraphicalEditPart target = (GraphicalEditPart) connection.getTarget();
		if (target != null) {
			target.getTargetConnections().remove(connection);
		}

		connection.setTarget(this);
		fireTargetConnectionAdded(connection, index);
	}

	/**
	 * Creates a {@link ConnectionEditPart} for the given model. Similar to
	 * {@link AbstractEditPart#createChild(Object)}. This method is called
	 * indirectly during {@link #refreshSourceConnections()}, and
	 * {@link #refreshTargetConnections()}.
	 * <P>
	 * The default implementation goes to the EditPartViewer's
	 * {@link EditPartFactory} to create the connection. This method should not be
	 * overridden unless factories are not being used.
	 *
	 * @param model the connection model object
	 * @return the new ConnectionEditPart
	 */
	protected ConnectionEditPart createConnection(Object model) {
		return (ConnectionEditPart) getViewer().getEditPartFactory().createEditPart(this, model);
	}

	/**
	 * Creates the <code>Figure</code> to be used as this part's <i>visuals</i>.
	 * This is called from {@link #getFigure()} if the figure has not been created.
	 *
	 * @return a Figure
	 */
	protected abstract IFigure createFigure();

	/**
	 * Searches for an existing <code>ConnectionEditPart</code> in the Viewer's
	 * {@link EditPartViewer#getEditPartRegistry() EditPart registry} and returns it
	 * if one is found. Otherwise, {@link #createConnection(Object)} is called to
	 * create a new ConnectionEditPart. Override this method only if you need to
	 * find an existing connection some other way.
	 *
	 * @param model the Connection's model
	 * @return the ConnectionEditPart
	 */
	protected ConnectionEditPart createOrFindConnection(Object model) {
		if (getViewer().getEditPartForModel(model) instanceof ConnectionEditPart conx) {
			return conx;
		}
		return createConnection(model);
	}

	/**
	 * Extends {@link AbstractEditPart#deactivate()} to also deactivate the source
	 * ConnectionEditParts. Subclasses should <em>extend</em> this method to remove
	 * any listeners added in {@link #activate}.
	 *
	 * @see org.eclipse.gef.EditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		getSourceConnections().forEach(ConnectionEditPart::deactivate);
		super.deactivate();
	}

	/**
	 * Notifies listeners that a source connection has been removed. Called from
	 * {@link #removeSourceConnection(ConnectionEditPart)}. There is no reason for
	 * subclasses to call or override this method.
	 *
	 * @param connection <code>ConnectionEditPart</code> being added as child.
	 * @param index      Position child is being added into.
	 */
	protected void fireRemovingSourceConnection(ConnectionEditPart connection, int index) {
		if (eventListeners == null) {
			return;
		}
		eventListeners.getListenersIterable(NodeListener.class)
				.forEach(lst -> lst.removingSourceConnection(connection, index));
	}

	/**
	 * Notifies listeners that a target connection has been removed. Called from
	 * {@link #removeTargetConnection(ConnectionEditPart)}. There is no reason for
	 * subclasses to call or override this method.
	 *
	 * @param connection <code>ConnectionEditPart</code> being added as child.
	 * @param index      Position child is being added into.
	 */
	protected void fireRemovingTargetConnection(ConnectionEditPart connection, int index) {
		if (eventListeners == null) {
			return;
		}
		eventListeners.getListenersIterable(NodeListener.class)
				.forEach(lst -> lst.removingTargetConnection(connection, index));
	}

	/**
	 * Notifies listeners that a source connection has been added. Called from
	 * {@link #addSourceConnection(ConnectionEditPart, int)}. There is no reason for
	 * subclasses to call or override this method.
	 *
	 * @param connection <code>ConnectionEditPart</code> being added as child.
	 * @param index      Position child is being added into.
	 */
	protected void fireSourceConnectionAdded(ConnectionEditPart connection, int index) {
		if (eventListeners == null) {
			return;
		}
		eventListeners.getListenersIterable(NodeListener.class)
				.forEach(lst -> lst.sourceConnectionAdded(connection, index));
	}

	/**
	 * Notifies listeners that a target connection has been added. Called from
	 * {@link #addTargetConnection(ConnectionEditPart, int)}. There is no reason for
	 * subclasses to call or override this method.
	 *
	 * @param connection <code>ConnectionEditPart</code> being added as child.
	 * @param index      Position child is being added into.
	 */
	protected void fireTargetConnectionAdded(ConnectionEditPart connection, int index) {
		if (eventListeners == null) {
			return;
		}
		eventListeners.getListenersIterable(NodeListener.class)
				.forEach(lst -> lst.targetConnectionAdded(connection, index));
	}

	/**
	 * Extends {@link AbstractEditPart#getAdapter(Class)} to handle additional
	 * adapter types. Currently, these types include
	 * {@link AccessibleHandleProvider} and {@link AccessibleAnchorProvider}.
	 * Subclasses should <em>extend</em> this method to support additional adapter
	 * types, or to replace the default provided adapters.
	 *
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(Class)
	 */
	@Override
	public <T> T getAdapter(final Class<T> key) {
		if (key == AccessibleHandleProvider.class) {
			return key.cast(new MergedAccessibleHandles(getEditPolicyIterable()));
		}

		if (key == AccessibleAnchorProvider.class) {
			return key.cast(new DefaultAccessibleAnchorProvider());
		}

		return super.getAdapter(key);
	}

	@SuppressWarnings("unchecked") // the children of a GraphicalEditPart have to be GraphicalEditParts
	@Override
	public List<? extends GraphicalEditPart> getChildren() {
		return (List<? extends GraphicalEditPart>) super.getChildren();
	}

	/**
	 * Implemented to delegate to {@link #getFigure()} by default. Subclasses may
	 * overwrite in case the {@link IFigure} returned by {@link #getFigure()} is a
	 * composite figure and child figures should be added to one of its children
	 * instead of the figure itself.
	 *
	 * @see GraphicalEditPart#getContentPane()
	 */
	@Override
	public IFigure getContentPane() {
		return getFigure();
	}

	/**
	 * Overridden to return a default <code>DragTracker</code> for
	 * GraphicalEditParts.
	 *
	 * @see org.eclipse.gef.EditPart#getDragTracker(Request)
	 */
	@Override
	public DragTracker getDragTracker(Request request) {
		return new org.eclipse.gef.tools.DragEditPartsTracker(this);
	}

	/**
	 * The default implementation calls {@link #createFigure()} if the figure is
	 * currently <code>null</code>.
	 *
	 * @see org.eclipse.gef.GraphicalEditPart#getFigure()
	 */
	@Override
	public IFigure getFigure() {
		if (figure == null) {
			setFigure(createFigure());
		}
		return figure;
	}

	/**
	 * A convenience method for obtaining the specified layer from the
	 * <code>LayerManager</code>.
	 *
	 * @param layer ID of the Layer
	 * @return The requested layer or <code>null</code> if it doesn't exist
	 */
	protected IFigure getLayer(Object layer) {
		return LayerManager.Helper.find(this).getLayer(layer);
	}

	/**
	 * Returns the <code>List</code> of the connection model objects for which this
	 * EditPart's model is the <b>source</b>. {@link #refreshSourceConnections()}
	 * calls this method. For each connection model object,
	 * {@link #createConnection(Object)} will be called automatically to obtain a
	 * corresponding {@link ConnectionEditPart}.
	 * <P>
	 * Callers must not modify the returned List.
	 *
	 * @return the List of model source connections
	 */
	@SuppressWarnings("static-method")
	protected List getModelSourceConnections() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * Returns the <code>List</code> of the connection model objects for which this
	 * EditPart's model is the <b>target</b>. {@link #refreshTargetConnections()}
	 * calls this method. For each connection model object,
	 * {@link #createConnection(Object)} will be called automatically to obtain a
	 * corresponding {@link ConnectionEditPart}.
	 * <P>
	 * Callers must not modify the returned List.
	 *
	 * @return the List of model target connections
	 */
	@SuppressWarnings("static-method")
	protected List getModelTargetConnections() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * @see org.eclipse.gef.GraphicalEditPart#getSourceConnections()
	 */
	@Override
	public List<? extends ConnectionEditPart> getSourceConnections() {
		if (sourceConnections == null) {
			return Collections.emptyList();
		}
		return sourceConnections;
	}

	/**
	 * @see org.eclipse.gef.GraphicalEditPart#getTargetConnections()
	 */
	@Override
	public List<? extends ConnectionEditPart> getTargetConnections() {
		if (targetConnections == null) {
			return Collections.emptyList();
		}
		return targetConnections;
	}

	/**
	 * A GraphicalEditPart is considered selectable, if it is active and its figure
	 * is showing.
	 *
	 * @see org.eclipse.gef.editparts.AbstractEditPart#isSelectable()
	 */
	@Override
	public boolean isSelectable() {
		return super.isSelectable() && getFigure() != null && getFigure().isShowing();
	}

	/**
	 * Adds the specified source <code>ConnectionEditPart</code> at an index. This
	 * method is used to update the {@link #sourceConnections} List. This method is
	 * called from {@link #addSourceConnection(ConnectionEditPart, int)}. Subclasses
	 * should not call or override this method.
	 *
	 * @param connection the ConnectionEditPart
	 * @param index      the index of the add
	 */
	protected void primAddSourceConnection(ConnectionEditPart connection, int index) {
		if (sourceConnections == null) {
			sourceConnections = new ArrayList<>();
		}
		sourceConnections.add(index, connection);
	}

	/**
	 * Adds the specified target <code>ConnectionEditPart</code> at an index. This
	 * method is used to update the {@link #targetConnections} List. This method is
	 * called from {@link #addTargetConnection(ConnectionEditPart, int)}. Subclasses
	 * should not call or override this method.
	 *
	 * @param connection the ConnectionEditPart
	 * @param index      the index of the add
	 */
	protected void primAddTargetConnection(ConnectionEditPart connection, int index) {
		if (targetConnections == null) {
			targetConnections = new ArrayList<>();
		}
		targetConnections.add(index, connection);
	}

	/**
	 * Removes the specified source <code>ConnectionEditPart</code> from the
	 * {@link #sourceConnections} List. This method is called from
	 * {@link #removeSourceConnection(ConnectionEditPart)}. Subclasses should not
	 * call or override this method.
	 *
	 * @param connection Connection to remove.
	 */
	protected void primRemoveSourceConnection(ConnectionEditPart connection) {
		sourceConnections.remove(connection);
	}

	/**
	 * Removes the specified target <code>ConnectionEditPart</code> from the
	 * {@link #targetConnections} List. This method is called from
	 * {@link #removeTargetConnection(ConnectionEditPart)}. Subclasses should not
	 * call or override this method.
	 *
	 * @param connection Connection to remove.
	 */
	protected void primRemoveTargetConnection(ConnectionEditPart connection) {
		targetConnections.remove(connection);
	}

	/**
	 * Extends {@link AbstractEditPart#refresh()} to refresh two additional
	 * structural features: <i>source</i> and <i>target</i> connections. Subclasses
	 * should probably override {@link AbstractEditPart#refreshVisuals()} instead of
	 * this method.
	 *
	 * @see org.eclipse.gef.EditPart#refresh()
	 */
	@Override
	public void refresh() {
		super.refresh();
		refreshSourceConnections();
		refreshTargetConnections();
	}

	/**
	 * Updates the set of <i>source</i> ConnectionEditParts so that it is in sync
	 * with the model source connections. This method is called from
	 * {@link #refresh()}, and may also be called in response to notification from
	 * the model.
	 * <P>
	 * The update is performed by comparing the existing source ConnectionEditParts
	 * with the set of model source connections returned from
	 * {@link #getModelSourceConnections()}. EditParts whose model no longer exists
	 * are {@link #removeSourceConnection(ConnectionEditPart) removed}. New models
	 * have their ConnectionEditParts {@link #createConnection(Object) created}.
	 * Subclasses should override <code>getModelSourceChildren()</code>.
	 * <P>
	 * This method should <em>not</em> be overridden.
	 */
	protected void refreshSourceConnections() {
		int i;

		List<? extends ConnectionEditPart> sourceConns = getSourceConnections();
		Map<Object, ConnectionEditPart> modelToEditPart = sourceConns.stream()
				.collect(Collectors.toMap(ConnectionEditPart::getModel, cEP -> cEP));

		List<? extends Object> modelObjects = getModelSourceConnections();
		if (modelObjects == null) {
			modelObjects = Collections.emptyList();
		}
		for (i = 0; i < modelObjects.size(); i++) {
			Object model = modelObjects.get(i);

			if (i < sourceConns.size() && sourceConns.get(i).getModel() == model) {
				continue;
			}

			final ConnectionEditPart editPart = modelToEditPart.get(model);
			if (editPart != null) {
				reorderSourceConnection(editPart, i);
			} else {
				addSourceConnection(createOrFindConnection(model), i);
			}
		}

		// Remove the remaining EditParts
		int size = sourceConns.size();
		if (i < size) {
			List<ConnectionEditPart> trash = new ArrayList<>(size - i);
			for (; i < size; i++) {
				trash.add(sourceConns.get(i));
			}
			trash.forEach(this::removeSourceConnection);
		}
	}

	/**
	 * Updates the set of <i>target</i> ConnectionEditParts so that it is in sync
	 * with the model target connections. This method is called from
	 * {@link #refresh()}, and may also be called in response to notification from
	 * the model.
	 * <P>
	 * The update is performed by comparing the existing source ConnectionEditParts
	 * with the set of model source connections returned from
	 * {@link #getModelTargetConnections()}. EditParts whose model no longer exists
	 * are {@link #removeTargetConnection(ConnectionEditPart) removed}. New models
	 * have their ConnectionEditParts {@link #createConnection(Object) created}.
	 * Subclasses should override <code>getModelTargetChildren()</code>.
	 * <P>
	 * This method should <em>not</em> be overridden.
	 */
	protected void refreshTargetConnections() {
		int i;

		List<? extends ConnectionEditPart> targetConns = getTargetConnections();
		Map<Object, ConnectionEditPart> modelToEditPart = targetConns.stream()
				.collect(Collectors.toMap(ConnectionEditPart::getModel, cEP -> cEP));

		List<? extends Object> modelObjects = getModelTargetConnections();
		if (modelObjects == null) {
			modelObjects = Collections.emptyList();
		}
		for (i = 0; i < modelObjects.size(); i++) {
			Object model = modelObjects.get(i);

			if (i < targetConns.size() && targetConns.get(i).getModel() == model) {
				continue;
			}

			final ConnectionEditPart editPart = modelToEditPart.get(model);
			if (editPart != null) {
				reorderTargetConnection(editPart, i);
			} else {
				addTargetConnection(createOrFindConnection(model), i);
			}
		}

		// Remove the remaining EditParts
		int size = targetConns.size();
		if (i < size) {
			List<ConnectionEditPart> trash = new ArrayList<>(size - i);
			for (; i < size; i++) {
				trash.add(targetConns.get(i));
			}
			trash.forEach(this::removeTargetConnection);
		}
	}

	/**
	 * Registers the EditPart's Figure in the Viewer. This is what makes it possible
	 * for the Viewer to map a mouse location to an EditPart.
	 *
	 * @see org.eclipse.gef.editparts.AbstractEditPart#registerVisuals()
	 */
	@Override
	protected void registerVisuals() {
		getViewer().getVisualPartMap().put(getFigure(), this);
	}

	/**
	 * Remove the child's Figure from the {@link #getContentPane() contentPane}.
	 *
	 * @see AbstractEditPart#removeChildVisual(EditPart)
	 */
	@Override
	protected void removeChildVisual(EditPart childEditPart) {
		IFigure child = ((GraphicalEditPart) childEditPart).getFigure();
		getContentPane().remove(child);
	}

	/**
	 * @see org.eclipse.gef.GraphicalEditPart#removeNodeListener(org.eclipse.gef.NodeListener)
	 */
	@Override
	public void removeNodeListener(NodeListener listener) {
		eventListeners.removeListener(NodeListener.class, listener);
	}

	/**
	 * Extends {@link AbstractEditPart#removeNotify()} to cleanup
	 * <code>ConnectionEditParts</code>.
	 *
	 * @see EditPart#removeNotify()
	 */
	@Override
	public void removeNotify() {
		getSourceConnections().stream().filter(conn -> conn.getSource() == this).forEach(conn -> conn.setSource(null));
		getTargetConnections().stream().filter(conn -> conn.getTarget() == this).forEach(conn -> conn.setTarget(null));
		super.removeNotify();
	}

	/**
	 * Removes the given connection for which this EditPart is the <B>source</b>.
	 * <BR>
	 * Fires notification. <BR>
	 * Inverse of {@link #addSourceConnection(ConnectionEditPart, int)}
	 *
	 * @param connection Connection being removed
	 */
	protected void removeSourceConnection(ConnectionEditPart connection) {
		fireRemovingSourceConnection(connection, getSourceConnections().indexOf(connection));
		if (connection.getSource() == this) {
			connection.deactivate();
			connection.setSource(null);
		}
		primRemoveSourceConnection(connection);
	}

	/**
	 * Removes the given connection for which this EditPart is the <B>target</b>.
	 * <BR>
	 * Fires notification. <BR>
	 * Inverse of {@link #addTargetConnection(ConnectionEditPart, int)}
	 *
	 * @param connection Connection being removed
	 */
	protected void removeTargetConnection(ConnectionEditPart connection) {
		fireRemovingTargetConnection(connection, getTargetConnections().indexOf(connection));
		if (connection.getTarget() == this) {
			connection.setTarget(null);
		}
		primRemoveTargetConnection(connection);
	}

	/**
	 * This method is extended to preserve a LayoutManager constraint if one exists.
	 *
	 * @see org.eclipse.gef.editparts.AbstractEditPart#reorderChild(EditPart, int)
	 */
	@Override
	protected void reorderChild(EditPart child, int index) {
		// Save the constraint of the child so that it does not
		// get lost during the remove and re-add.
		IFigure childFigure = ((GraphicalEditPart) child).getFigure();
		LayoutManager layout = getContentPane().getLayoutManager();
		Object constraint = null;
		if (layout != null) {
			constraint = layout.getConstraint(childFigure);
		}

		super.reorderChild(child, index);
		setLayoutConstraint(child, childFigure, constraint);
	}

	/**
	 * Moves a source <code>ConnectionEditPart</code> into a lower index than it
	 * currently occupies. This method is called from
	 * {@link #refreshSourceConnections()}.
	 *
	 * @param connection the ConnectionEditPart
	 * @param index      the new index
	 */
	protected void reorderSourceConnection(ConnectionEditPart connection, int index) {
		primRemoveSourceConnection(connection);
		primAddSourceConnection(connection, index);
	}

	/**
	 * Moves a target <code>ConnectionEditPart</code> into a lower index than it
	 * currently occupies. This method is called from
	 * {@link #refreshTargetConnections()}.
	 *
	 * @param connection the ConnectionEditPart
	 * @param index      the new index
	 */
	protected void reorderTargetConnection(ConnectionEditPart connection, int index) {
		primRemoveTargetConnection(connection);
		primAddTargetConnection(connection, index);
	}

	/**
	 * Sets the Figure
	 *
	 * @param figure the Figure
	 */
	protected void setFigure(IFigure figure) {
		this.figure = figure;
	}

	/**
	 * @see GraphicalEditPart#setLayoutConstraint(EditPart, IFigure, Object)
	 */
	@Override
	public void setLayoutConstraint(EditPart child, IFigure childFigure, Object constraint) {
		childFigure.getParent().setConstraint(childFigure, constraint);
	}

	/**
	 * Implemented to remove the Figure from the Viewer's registry.
	 *
	 * @see AbstractEditPart#unregisterVisuals()
	 */
	@Override
	protected void unregisterVisuals() {
		getViewer().getVisualPartMap().remove(getFigure());
	}

}
