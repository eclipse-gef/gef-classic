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

package org.eclipse.zest.layouts.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.zest.layouts.Filter;
import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutItem;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.Stoppable;
import org.eclipse.zest.layouts.constraints.BasicEntityConstraint;
import org.eclipse.zest.layouts.dataStructures.BendPoint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;
import org.eclipse.zest.layouts.interfaces.LayoutContext;
import org.eclipse.zest.layouts.progress.ProgressEvent;
import org.eclipse.zest.layouts.progress.ProgressListener;

/**
 * Handles common elements in all layout algorithms [irbull] Refactored into a
 * template pattern. ApplyLayout now delegates the task to ApplyLayoutInternal
 *
 * [irbull] Included asynchronous layouts
 *
 * @version 1.0
 * @author Casey Best
 * @author Ian Bull
 * @author Chris Callendar
 * @author Rob Lintern
 * @author Chris Bennett
 */
public abstract class AbstractLayoutAlgorithm implements LayoutAlgorithm {
	/**
	 * @deprecated No longer used in Zest 2.x. This class will be removed in a
	 *             future release.
	 * @noextend This class is not intended to be subclassed by clients.
	 * @noreference This class is not intended to be referenced by clients.
	 * @since 2.0
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Deprecated(since = "2.0", forRemoval = true)
	public static abstract class Zest1 implements LayoutAlgorithm.Zest1, Stoppable {

		@Deprecated
		public void removeRelationships(Collection<? extends LayoutRelationship> collection) {

		}

		@Deprecated
		public static final int MIN_ENTITY_SIZE = 5;
		private static final int MIN_TIME_DELAY_BETWEEN_PROGRESS_EVENTS = 1;

		private Thread creationThread = null;
		@Deprecated
		protected Comparator comparator;
		@Deprecated
		protected Filter filter;
		private final List<ProgressListener> progressListeners = new CopyOnWriteArrayList<>();
		private Calendar lastProgressEventFired;
		private double widthToHeightRatio;

		@Deprecated
		class InternalComparator implements Comparator<InternalNode> {
			Comparator<LayoutEntity> externalComparator = null;

			public InternalComparator(Comparator<LayoutEntity> externalComparator) {
				this.externalComparator = externalComparator;
			}

			@Override
			public int compare(InternalNode internalNode1, InternalNode internalNode2) {
				return this.externalComparator.compare(internalNode1.getLayoutEntity(), internalNode2.getLayoutEntity());
			}

		}

		/*
		 * Internal Nodes.
		 */
		private InternalNode[] internalNodes;
		private InternalRelationship[] internalRelationships;
		private double internalX;
		private double internalY;
		private double internalWidth;
		private double internalHeight;
		@Deprecated
		protected boolean internalContinuous;
		@Deprecated
		protected boolean internalAsynchronous;

		/*
		 * A queue of entities and relationships to add or remove. Each layout algorithm
		 * should check these and update their internal lists.
		 */

		/** A list of LayoutEntity objects to be removed from the layout. */
		private final List<LayoutEntity> entitiesToRemove = new ArrayList<>();
		/** A list of LayoutRelationship objects to be removed. */
		private final List<LayoutRelationship> relationshipsToRemove = new ArrayList<>();
		/** A list of LayoutEntity objects to be added to the layout. */
		private final List<LayoutEntity> entitiesToAdd = new ArrayList<>();
		/** A list of LayoutRelationship objects to be added. */
		private final List<LayoutRelationship> relationshipsToAdd = new ArrayList<>();

		@Deprecated
		protected boolean layoutStopped = true;

		@Deprecated
		protected int layout_styles = 0;

		// Child classes can set to false to retain node shapes and sizes
		@Deprecated
		protected boolean resizeEntitiesAfterLayout = true;

		/**
		 * Initializes the abstract layout algorithm.
		 *
		 * @see LayoutStyles
		 */
		@Deprecated
		public Zest1(int styles) {
			this.creationThread = Thread.currentThread();
			this.lastProgressEventFired = Calendar.getInstance();
			this.widthToHeightRatio = 1.0;

			this.layout_styles = styles;
		}

		/**
		 * Queues up the given entity (if it isn't in the list) to be added to the
		 * algorithm.
		 *
		 * @param entity
		 */
		@Override
		@Deprecated
		public void addEntity(LayoutEntity entity) {
			if ((entity != null) && !entitiesToAdd.contains(entity)) {
				entitiesToAdd.add(entity);
			}
		}

		/**
		 * Queues up the given relationshp (if it isn't in the list) to be added to the
		 * algorithm.
		 *
		 * @param relationship
		 */
		@Override
		@Deprecated
		public void addRelationship(LayoutRelationship relationship) {
			if ((relationship != null) && !relationshipsToAdd.contains(relationship)) {
				relationshipsToAdd.add(relationship);
			}
		}

		/**
		 * Queues up the given entity to be removed from the algorithm next time it
		 * runs.
		 *
		 * @param entity The entity to remove
		 */
		@Override
		@Deprecated
		public void removeEntity(LayoutEntity entity) {
			if ((entity != null) && !entitiesToRemove.contains(entity)) {
				entitiesToRemove.add(entity);
			}
		}

		/**
		 * Queues up the given relationship to be removed from the algorithm next time
		 * it runs.
		 *
		 * @param relationship The relationship to remove.
		 */
		@Override
		@Deprecated
		public void removeRelationship(LayoutRelationship relationship) {
			if ((relationship != null) && !relationshipsToRemove.contains(relationship)) {
				relationshipsToRemove.add(relationship);
			}
		}

		/**
		 * Queues up all the relationships in the list to be removed.
		 *
		 * @param relationships
		 */
		@Override
		@Deprecated
		public void removeRelationships(List<? extends LayoutRelationship> relationships) {
			// note we don't check if the relationshipsToRemove contains
			// any of the objects in relationships.
			relationshipsToRemove.addAll(relationships);
		}

		/**
		 * Sets the current layout style. This overwrites all other layout styles. Use
		 * getStyle to get the current style.
		 *
		 * @param style
		 */
		@Override
		@Deprecated
		public void setStyle(int style) {
			this.layout_styles = style;
		}

		/**
		 * Gets the current layout style
		 *
		 * @return the layout styles for this layout
		 */
		@Override
		@Deprecated
		public int getStyle() {
			return this.layout_styles;
		}

		@Deprecated
		public abstract void setLayoutArea(double x, double y, double width, double height);

		/**
		 * Determines if the configuration is valid for this layout
		 *
		 * @param asynchronous
		 * @param continuous
		 */
		@Deprecated
		protected abstract boolean isValidConfiguration(boolean asynchronous, boolean continuous);

		/**
		 * Apply the layout to the given entities. The entities will be moved and
		 * resized based on the algorithm.
		 *
		 * @param entitiesToLayout        Apply the algorithm to these entities
		 * @param relationshipsToConsider Only consider these relationships when
		 *                                applying the algorithm.
		 * @param boundsX                 The left side of the bounds in which the
		 *                                layout can place the entities.
		 * @param boundsY                 The top side of the bounds in which the layout
		 *                                can place the entities.
		 * @param boundsWidth             The width of the bounds in which the layout
		 *                                can place the entities.
		 * @param boundsHeight            The height of the bounds in which the layout
		 *                                can place the entities.
		 */
		@Deprecated
		protected abstract void applyLayoutInternal(InternalNode[] entitiesToLayout,
				InternalRelationship[] relationshipsToConsider, double boundsX, double boundsY, double boundsWidth,
				double boundsHeight);

		/**
		 * Updates the given array of entities checking if any need to be removed or
		 * added.
		 *
		 * @param entities the current entities
		 * @return the updated entities array
		 */
		@Deprecated
		protected InternalNode[] updateEntities(InternalNode[] entities) {
			if (!entitiesToRemove.isEmpty() || !entitiesToAdd.isEmpty()) {
				List<InternalNode> internalNodesList = new ArrayList<>(Arrays.asList(entities));

				// remove nodes
				entitiesToRemove.stream().filter(e -> e.getLayoutInformation() != null)
						.forEach(e -> internalNodesList.remove(e.getLayoutInformation()));

				// Also remove from _internalNodes
				List<InternalNode> updatedEntities = new ArrayList<>(
						internalNodes.length - entitiesToRemove.size() + entitiesToAdd.size());
				for (InternalNode node : internalNodes) {
					if (entitiesToRemove.contains(node.getLayoutEntity())) {
						entitiesToRemove.remove(node.getLayoutEntity());
					} else {
						updatedEntities.add(node);
					}
				}
				entitiesToRemove.clear();

				// Add any new nodes
				LayoutEntity[] entitiesArray = new LayoutEntity[entitiesToAdd.size()];
				entitiesArray = entitiesToAdd.toArray(entitiesArray);
				InternalNode[] newNodes = createInternalNodes(entitiesArray);
				for (InternalNode newNode : newNodes) {
					internalNodesList.add(newNode);
					updatedEntities.add(newNode);
				}
				entitiesToAdd.clear();

				entities = new InternalNode[internalNodesList.size()];
				entities = internalNodesList.toArray(entities);

				internalNodes = new InternalNode[updatedEntities.size()];
				internalNodes = updatedEntities.toArray(internalNodes);
			}

			return entities;
		}

		/**
		 * Updates the given array of relationships checking if any need to be removed
		 * or added. Also updates the original array of relationships.
		 *
		 * @param relationships the current relationships
		 * @return the update relationships array
		 */
		@Deprecated
		protected InternalRelationship[] updateRelationships(InternalRelationship[] relationships) {
			if (!relationshipsToRemove.isEmpty() || !relationshipsToAdd.isEmpty()) {
				List<InternalRelationship> internalRelsList = new ArrayList<>(Arrays.asList(relationships));

				// remove relationships
				relationshipsToRemove.stream().filter(r -> r.getLayoutInformation() != null)
						.forEach(r -> internalRelsList.remove(r.getLayoutInformation()));

				// Also remove from _internalRelationships
				List<InternalRelationship> updatedRelationships = new ArrayList<>(
						internalRelationships.length - relationshipsToRemove.size() + relationshipsToAdd.size());
				for (InternalRelationship relation : internalRelationships) {
					if (relationshipsToRemove.contains(relation.getLayoutRelationship())) {
						relationshipsToRemove.remove(relation.getLayoutRelationship());
					} else {
						updatedRelationships.add(relation);
					}
				}
				relationshipsToRemove.clear();

				// add relationships
				if (!relationshipsToAdd.isEmpty()) {
					LayoutRelationship[] relsArray = new LayoutRelationship[relationshipsToAdd.size()];
					relsArray = relationshipsToAdd.toArray(relsArray);
					InternalRelationship[] newRelationships = createInternalRelationships(relsArray);
					for (InternalRelationship newRelationship : newRelationships) {
						internalRelsList.add(newRelationship);
						updatedRelationships.add(newRelationship);
					}
				}
				relationshipsToAdd.clear();

				relationships = new InternalRelationship[internalRelsList.size()];
				relationships = internalRelsList.toArray(relationships);

				internalRelationships = new InternalRelationship[updatedRelationships.size()];
				internalRelationships = updatedRelationships.toArray(internalRelationships);
			}

			return relationships;
		}

		/**
		 * Returns true if the layout algorithm is running
		 *
		 * @return boolean if the layout algorithm is running
		 */
		@Override
		@Deprecated
		public synchronized boolean isRunning() {
			return !layoutStopped;
		}

		/**
		 * Stops the current layout from running. All layout algorithms should
		 * constantly check isLayoutRunning
		 */
		@Override
		@Deprecated
		public synchronized void stop() {
			layoutStopped = true;
			postLayoutAlgorithm(internalNodes, internalRelationships);
			fireProgressEnded(getTotalNumberOfLayoutSteps());
		}

		private void setupLayout(LayoutEntity[] entitiesToLayout, LayoutRelationship[] relationshipsToConsider, double x,
				double y, double width, double height) {
			internalX = x;
			internalY = y;
			internalHeight = height;
			internalWidth = width;
			// Filter all the unwanted entities and relationships
			entitiesToLayout = (LayoutEntity[]) filterUnwantedObjects(entitiesToLayout);
			relationshipsToConsider = (LayoutRelationship[]) filterUnwantedObjects(relationshipsToConsider);

			// Check that the input is valid
			if (!verifyInput(entitiesToLayout, relationshipsToConsider)) {
				layoutStopped = true;
				throw new RuntimeException(
						"The relationships in relationshipsToConsider don't contain the entities in entitiesToLayout"); //$NON-NLS-1$
			}

			// Create the internal nodes and relationship
			internalNodes = createInternalNodes(entitiesToLayout);
			internalRelationships = createInternalRelationships(relationshipsToConsider);
		}

		/**
		 * Code called before the layout algorithm starts
		 */
		@Deprecated
		protected abstract void preLayoutAlgorithm(InternalNode[] entitiesToLayout,
				InternalRelationship[] relationshipsToConsider, double x, double y, double width, double height);

		/**
		 * Code called after the layout algorithm ends
		 */
		@Deprecated
		protected abstract void postLayoutAlgorithm(InternalNode[] entitiesToLayout,
				InternalRelationship[] relationshipsToConsider);

		/**
		 * Gets the total number of steps in this layout
		 */
		@Deprecated
		protected abstract int getTotalNumberOfLayoutSteps();

		/**
		 * Gets the current layout step
		 */
		@Deprecated
		protected abstract int getCurrentLayoutStep();

		/**
		 * This actually applies the layout
		 */
		@Override
		@Deprecated
		public synchronized void applyLayout(final LayoutEntity[] entitiesToLayout,
				final LayoutRelationship[] relationshipsToConsider, final double x, final double y, final double width,
				final double height, boolean asynchronous, boolean continuous) throws InvalidLayoutConfiguration {
			checkThread();
			this.internalAsynchronous = asynchronous;
			this.internalContinuous = continuous;

			if (!isValidConfiguration(asynchronous, continuous)) {
				throw new InvalidLayoutConfiguration();
			}

			clearBendPoints(relationshipsToConsider);

			this.layoutStopped = false;

			// when an algorithm starts, reset the progress event
			lastProgressEventFired = Calendar.getInstance();
			if (asynchronous) {

				Thread thread = new Thread(() -> {
					setupLayout(entitiesToLayout, relationshipsToConsider, x, y, width, height);
					preLayoutAlgorithm(internalNodes, internalRelationships, internalX, internalY, internalWidth,
							internalHeight);
					fireProgressStarted(getTotalNumberOfLayoutSteps());

					applyLayoutInternal(internalNodes, internalRelationships, internalX, internalY, internalWidth,
							internalHeight);
					stop();
				});
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.start();
			} else {

				// If we are running synchronously then we have to stop this at some
				// point? right?
				setupLayout(entitiesToLayout, relationshipsToConsider, x, y, width, height);
				preLayoutAlgorithm(internalNodes, internalRelationships, internalX, internalY, internalWidth,
						internalHeight);
				fireProgressStarted(getTotalNumberOfLayoutSteps());

				applyLayoutInternal(internalNodes, internalRelationships, internalX, internalY, internalWidth,
						internalHeight);
				stop();
			}

		}

		/**
		 * Clear out all old bend points before doing a layout
		 */
		private static void clearBendPoints(LayoutRelationship[] relationships) {
			for (LayoutRelationship rel : relationships) {
				rel.clearBendPoints();
			}
		}

		/**
		 * Update external bend points from the internal bendpoints list. Save the
		 * source and destination points for later use in scaling and translating
		 *
		 * @param relationshipsToConsider
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		protected void updateBendPoints(InternalRelationship[] relationshipsToConsider) {
			for (InternalRelationship relationship : relationshipsToConsider) {
				List bendPoints = relationship.getBendPoints();
				if (!bendPoints.isEmpty()) {
					// We will assume that source/dest coordinates are for center of node
					BendPoint[] externalBendPoints = new BendPoint[bendPoints.size() + 2];
					InternalNode sourceNode = relationship.getSource();
					externalBendPoints[0] = new BendPoint(sourceNode.getInternalX(), sourceNode.getInternalY());
					InternalNode destNode = relationship.getDestination();
					externalBendPoints[externalBendPoints.length - 1] = new BendPoint(destNode.getInternalX(),
							destNode.getInternalY());

					for (int j = 0; j < bendPoints.size(); j++) {
						BendPoint bp = (BendPoint) bendPoints.get(j);
						externalBendPoints[j + 1] = new BendPoint(bp.x, bp.y, bp.getIsControlPoint());
					}
					relationship.getLayoutRelationship().setBendPoints(externalBendPoints);
				}
			}
		}

		/**
		 * Creates a list of InternalNode objects from the list of LayoutEntity objects
		 * the user wants layed out. Sets the internal nodes' positions and sizes from
		 * the external entities.
		 */
		private static InternalNode[] createInternalNodes(LayoutEntity[] nodes) {
			InternalNode[] internalNodes = new InternalNode[nodes.length];
			BasicEntityConstraint basicEntityConstraint = new BasicEntityConstraint();
			for (int i = 0; i < nodes.length; i++) {
				basicEntityConstraint.clear();
				LayoutEntity externalNode = nodes[i];
				InternalNode internalNode = new InternalNode(externalNode);
				externalNode.populateLayoutConstraint(basicEntityConstraint);
				internalNode.setInternalLocation(externalNode.getXInLayout(), externalNode.getYInLayout());
				internalNodes[i] = internalNode;
			} // end of for
			return internalNodes;
		}

		/**
		 * Creates a list of InternalRelationship objects from the given list of
		 * LayoutRelationship objects.
		 *
		 * @param rels
		 * @return List of internal relationships
		 */
		private static InternalRelationship[] createInternalRelationships(LayoutRelationship[] rels) {
			List<LayoutRelationship> listOfInternalRelationships = new ArrayList<>(rels.length);
			for (LayoutRelationship relation : rels) {
				InternalNode src = (InternalNode) relation.getSourceInLayout().getLayoutInformation();
				InternalNode dest = (InternalNode) relation.getDestinationInLayout().getLayoutInformation();
				if ((src == null) || (dest == null)) {
					throw new RuntimeException("Error creating internal relationship, one of the nodes is null: src=" + src //$NON-NLS-1$
							+ ", dest=" + dest); //$NON-NLS-1$
				}
				InternalRelationship internalRelationship = new InternalRelationship(relation, src, dest);
				listOfInternalRelationships.add(internalRelationship);
			}
			return listOfInternalRelationships.toArray(new InternalRelationship[listOfInternalRelationships.size()]);
		}

		/**
		 * Removes any objects that are currently filtered
		 */
		private Object[] filterUnwantedObjects(LayoutItem[] objects) {
			// first remove any entities or relationships that are filtered.
			List<LayoutItem> unfilteredObjsList = new ArrayList<>();
			if (filter != null) {
				for (LayoutItem object : objects) {
					if (!filter.isObjectFiltered(object)) {
						unfilteredObjsList.add(object);
					}
				}
				// @tag bug.156266-ClassCast.fix : use reflection to create the array.
				Object[] unfilteredObjs = (Object[]) java.lang.reflect.Array
						.newInstance(objects.getClass().getComponentType(), unfilteredObjsList.size());
				unfilteredObjsList.toArray(unfilteredObjs);
				return unfilteredObjs;
			}
			return objects;
		}

		/**
		 * Filters the entities and relationships to apply the layout on
		 */
		@Override
		@Deprecated
		public void setFilter(Filter filter) {
			this.filter = filter;
		}

		/**
		 * Determines the order in which the objects should be displayed. Note: Some
		 * algorithms force a specific order.
		 */
		@Override
		@Deprecated
		public void setComparator(Comparator comparator) {
			this.comparator = new InternalComparator(comparator);
		}

		/**
		 * Verifies the endpoints of the relationships are entities in the
		 * entitiesToLayout list. Allows other classes in this package to use this
		 * method to verify the input
		 */
		@Deprecated
		public static boolean verifyInput(LayoutEntity[] entitiesToLayout, LayoutRelationship[] relationshipsToConsider) {
			boolean stillValid = true;
			for (LayoutRelationship relationship : relationshipsToConsider) {
				LayoutEntity source = relationship.getSourceInLayout();
				LayoutEntity destination = relationship.getDestinationInLayout();
				boolean containsSrc = false;
				boolean containsDest = false;
				int j = 0;
				while (j < entitiesToLayout.length && !(containsSrc && containsDest)) {
					if (entitiesToLayout[j].equals(source)) {
						containsSrc = true;
					}
					if (entitiesToLayout[j].equals(destination)) {
						containsDest = true;
					}
					j++;
				}
				stillValid = containsSrc && containsDest;
			}
			return stillValid;
		}

		/**
		 * Gets the location in the layout bounds for this node
		 *
		 * @param x
		 * @param y
		 */
		@Deprecated
		protected DisplayIndependentPoint getLocalLocation(InternalNode[] entitiesToLayout, double x, double y,
				DisplayIndependentRectangle realBounds) {

			double screenWidth = realBounds.width;
			double screenHeight = realBounds.height;
			DisplayIndependentRectangle layoutBounds = getLayoutBounds(entitiesToLayout, false);
			double localX = (x / screenWidth) * layoutBounds.width + layoutBounds.x;
			double localY = (y / screenHeight) * layoutBounds.height + layoutBounds.y;
			return new DisplayIndependentPoint(localX, localY);
		}

		/**
		 * Find an appropriate size for the given nodes, then fit them into the given
		 * bounds. The relative locations of the nodes to each other must be preserved.
		 * Child classes should set flag reresizeEntitiesAfterLayout to false if they
		 * want to preserve node sizes.
		 */
		@Deprecated
		protected void defaultFitWithinBounds(InternalNode[] entitiesToLayout, DisplayIndependentRectangle realBounds) {
			defaultFitWithinBounds(entitiesToLayout, new InternalRelationship[0], realBounds);
		}

		/**
		 * Find an appropriate size for the given nodes, then fit them into the given
		 * bounds. The relative locations of the nodes to each other must be preserved.
		 * Child classes should set flag reresizeEntitiesAfterLayout to false if they
		 * want to preserve node sizes.
		 */
		@Deprecated
		protected void defaultFitWithinBounds(InternalNode[] entitiesToLayout, InternalRelationship[] relationships,
				DisplayIndependentRectangle realBounds) {

			DisplayIndependentRectangle layoutBounds;

			if (resizeEntitiesAfterLayout) {
				layoutBounds = getLayoutBounds(entitiesToLayout, false);

				// Convert node x,y to be in percent rather than absolute coords
				convertPositionsToPercentage(entitiesToLayout, relationships, layoutBounds, false /* do not update size */);

				// Resize and shift nodes
				resizeAndShiftNodes(entitiesToLayout);
			}

			// Recalculate layout, allowing for the node width, which we now know
			layoutBounds = getLayoutBounds(entitiesToLayout, true);

			// adjust node positions again, to the new coordinate system (still as a
			// percentage)
			convertPositionsToPercentage(entitiesToLayout, relationships, layoutBounds, true /* update node size */);

			DisplayIndependentRectangle screenBounds = calcScreenBounds(realBounds, layoutBounds);

			// Now convert to real screen coordinates
			convertPositionsToCoords(entitiesToLayout, relationships, screenBounds);
		}

		/**
		 * Calculate the screen bounds, maintaining the
		 *
		 * @param realBounds
		 * @return
		 */
		private DisplayIndependentRectangle calcScreenBounds(DisplayIndependentRectangle realBounds,
				DisplayIndependentRectangle layoutBounds) {
			if (resizeEntitiesAfterLayout) { // OK to alter aspect ratio
				double borderWidth = Math.min(realBounds.width, realBounds.height) / 10.0; // use 10% for the border - 5% on
																							// each side
				return new DisplayIndependentRectangle(realBounds.x + borderWidth / 2.0, realBounds.y + borderWidth / 2.0,
						realBounds.width - borderWidth, realBounds.height - borderWidth);
			}
			double heightAdjustment = realBounds.height / layoutBounds.height;
			double widthAdjustment = realBounds.width / layoutBounds.width;
			double ratio = Math.min(heightAdjustment, widthAdjustment);
			double adjustedHeight = layoutBounds.height * ratio;
			double adjustedWidth = layoutBounds.width * ratio;
			double adjustedX = realBounds.x + (realBounds.width - adjustedWidth) / 2.0;
			double adjustedY = realBounds.y + (realBounds.height - adjustedHeight) / 2.0;
			double borderWidth = Math.min(adjustedWidth, adjustedHeight) / 10.0; // use 10% for the border - 5% on each
																					// side
			return new DisplayIndependentRectangle(adjustedX + borderWidth / 2.0, adjustedY + borderWidth / 2.0,
					adjustedWidth - borderWidth, adjustedHeight - borderWidth);
		}

		/**
		 * Find and set the node size - shift the nodes to the right and down to make
		 * room for the width and height.
		 *
		 * @param entitiesToLayout
		 * @param relationships
		 */
		private static void resizeAndShiftNodes(InternalNode[] entitiesToLayout) {
			// get maximum node size as percent of screen dimensions
			double nodeSize = getNodeSize(entitiesToLayout);
			double halfNodeSize = nodeSize / 2;

			// Resize and shift nodes
			for (InternalNode node : entitiesToLayout) {
				node.setInternalSize(nodeSize, nodeSize);
				node.setInternalLocation(node.getInternalX() + halfNodeSize, node.getInternalY() + halfNodeSize);
			}
		}

		/**
		 * Convert all node positions into a percentage of the screen. If
		 * includeNodeSize is true then this also updates the node's internal size.
		 *
		 * @param entitiesToLayout
		 */
		private static void convertPositionsToPercentage(InternalNode[] entitiesToLayout,
				InternalRelationship[] relationships, DisplayIndependentRectangle layoutBounds, boolean includeNodeSize) {

			// Adjust node positions and sizes
			for (InternalNode node : entitiesToLayout) {
				DisplayIndependentPoint location = node.getInternalLocation().convertToPercent(layoutBounds);
				node.setInternalLocation(location.x, location.y);
				if (includeNodeSize) { // adjust node sizes
					double width = node.getInternalWidth() / layoutBounds.width;
					double height = node.getInternalHeight() / layoutBounds.height;
					node.setInternalSize(width, height);
				}
			}

			// Adjust bendpoint positions
			for (InternalRelationship rel : relationships) {
				for (Object element : rel.getBendPoints()) {
					BendPoint bp = (BendPoint) element;
					DisplayIndependentPoint toPercent = bp.convertToPercent(layoutBounds);
					bp.setX(toPercent.x);
					bp.setY(toPercent.y);
				}
			}
		}

		/**
		 * Convert the positions from a percentage of bounds area to fixed coordinates.
		 * NOTE: ALL OF THE POSITIONS OF NODES UNTIL NOW WERE FOR THE CENTER OF THE NODE
		 * - Convert it to the left top corner.
		 *
		 * @param entitiesToLayout
		 * @param relationships
		 * @param realBounds
		 */
		private void convertPositionsToCoords(InternalNode[] entitiesToLayout, InternalRelationship[] relationships,
				DisplayIndependentRectangle screenBounds) {

			// Adjust node positions and sizes
			for (InternalNode node : entitiesToLayout) {
				double width = node.getInternalWidth() * screenBounds.width;
				double height = node.getInternalHeight() * screenBounds.height;
				DisplayIndependentPoint location = node.getInternalLocation().convertFromPercent(screenBounds);
				node.setInternalLocation(location.x - width / 2, location.y - height / 2);
				if (resizeEntitiesAfterLayout) {
					adjustNodeSizeAndPos(node, height, width);
				} else {
					node.setInternalSize(width, height);
				}
			}

			// Adjust bendpoint positions and shift based on source node size
			for (InternalRelationship rel : relationships) {
				for (Object element : rel.getBendPoints()) {
					BendPoint bp = (BendPoint) element;
					DisplayIndependentPoint fromPercent = bp.convertFromPercent(screenBounds);
					bp.setX(fromPercent.x);
					bp.setY(fromPercent.y);
				}
			}
		}

		/**
		 * Adjust node size to take advantage of space. Reset position to top left
		 * corner of node.
		 *
		 * @param node
		 * @param height
		 * @param width
		 */
		private void adjustNodeSizeAndPos(InternalNode node, double height, double width) {
			double widthUsingHeight = height * widthToHeightRatio;
			if (widthToHeightRatio <= 1.0 && widthUsingHeight <= width) {
				double widthToUse = height * widthToHeightRatio;
				double leftOut = width - widthToUse;
				node.setInternalSize(Math.max(height * widthToHeightRatio, MIN_ENTITY_SIZE),
						Math.max(height, MIN_ENTITY_SIZE));
				node.setInternalLocation(node.getInternalX() + leftOut / 2, node.getInternalY());

			} else {
				double heightToUse = height / widthToHeightRatio;
				double leftOut = height - heightToUse;

				node.setInternalSize(Math.max(width, MIN_ENTITY_SIZE),
						Math.max(width / widthToHeightRatio, MIN_ENTITY_SIZE));
				node.setInternalLocation(node.getInternalX(), node.getInternalY() + leftOut / 2);
			}

		}

		/**
		 * Returns the maximum possible node size as a percentage of the width or height
		 * in current coordinate system.
		 */
		private static double getNodeSize(InternalNode[] entitiesToLayout) {
			double width;
			double height;
			if (entitiesToLayout.length == 1) {
				width = 0.8;
				height = 0.8;
			} else {
				DisplayIndependentDimension minimumDistance = getMinimumDistance(entitiesToLayout);
				width = 0.8 * minimumDistance.width;
				height = 0.8 * minimumDistance.height;
			}
			return Math.max(width, height);
		}

		/**
		 * Find the bounds in which the nodes are located. Using the bounds against the
		 * real bounds of the screen, the nodes can proportionally be placed within the
		 * real bounds. The bounds can be determined either including the size of the
		 * nodes or not. If the size is not included, the bounds will only be guaranteed
		 * to include the center of each node.
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		protected DisplayIndependentRectangle getLayoutBounds(InternalNode[] entitiesToLayout, boolean includeNodeSize) {
			double rightSide = Double.MIN_VALUE;
			double bottomSide = Double.MIN_VALUE;
			double leftSide = Double.MAX_VALUE;
			double topSide = Double.MAX_VALUE;
			for (InternalNode entity : entitiesToLayout) {
				if (entity.hasPreferredLocation()) {
					continue;
				}

				if (includeNodeSize) {
					leftSide = Math.min(entity.getInternalX() - entity.getInternalWidth() / 2, leftSide);
					topSide = Math.min(entity.getInternalY() - entity.getInternalHeight() / 2, topSide);
					rightSide = Math.max(entity.getInternalX() + entity.getInternalWidth() / 2, rightSide);
					bottomSide = Math.max(entity.getInternalY() + entity.getInternalHeight() / 2, bottomSide);
				} else {
					leftSide = Math.min(entity.getInternalX(), leftSide);
					topSide = Math.min(entity.getInternalY(), topSide);
					rightSide = Math.max(entity.getInternalX(), rightSide);
					bottomSide = Math.max(entity.getInternalY(), bottomSide);
				}
			}
			return new DisplayIndependentRectangle(leftSide, topSide, rightSide - leftSide, bottomSide - topSide);
		}

		/**
		 * minDistance is the closest that any two points are together. These two points
		 * become the center points for the two closest nodes, which we wish to make
		 * them as big as possible without overlapping. This will be the maximum of
		 * minDistanceX and minDistanceY minus a bit, lets say 20%
		 *
		 * We make the recommended node size a square for convenience.
		 *
		 *
		 * _______ | | | | | + | | |\ | |___|_\_|_____ | | \ | | | \ | +-|---+ | | |
		 * |_______|
		 *
		 *
		 *
		 */
		private static DisplayIndependentDimension getMinimumDistance(InternalNode[] entitiesToLayout) {
			DisplayIndependentDimension horAndVertdistance = new DisplayIndependentDimension(Double.MAX_VALUE,
					Double.MAX_VALUE);
			double minDistance = Double.MAX_VALUE; // the minimum distance between all the nodes
			// TODO: Very Slow!
			for (int i = 0; i < entitiesToLayout.length; i++) {
				InternalNode layoutEntity1 = entitiesToLayout[i];
				double x1 = layoutEntity1.getInternalX();
				double y1 = layoutEntity1.getInternalY();
				for (int j = i + 1; j < entitiesToLayout.length; j++) {
					InternalNode layoutEntity2 = entitiesToLayout[j];
					double x2 = layoutEntity2.getInternalX();
					double y2 = layoutEntity2.getInternalY();
					double distanceX = Math.abs(x1 - x2);
					double distanceY = Math.abs(y1 - y2);
					double distance = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

					if (distance < minDistance) {
						minDistance = distance;
						horAndVertdistance.width = distanceX;
						horAndVertdistance.height = distanceY;
					}
				}
			}
			return horAndVertdistance;
		}

		/**
		 * Set the width to height ratio you want the entities to use
		 */
		@Override
		@Deprecated
		public void setEntityAspectRatio(double ratio) {
			widthToHeightRatio = ratio;
		}

		/**
		 * Returns the width to height ratio this layout will use to set the size of the
		 * entities.
		 */
		@Override
		@Deprecated
		public double getEntityAspectRatio() {
			return widthToHeightRatio;
		}

		/**
		 * A layout algorithm could take an uncomfortable amout of time to complete. To
		 * relieve some of the mystery, the layout algorithm will notify each
		 * ProgressListener of its progress.
		 */
		@Override
		@Deprecated
		public void addProgressListener(ProgressListener listener) {
			if (!progressListeners.contains(listener)) {
				progressListeners.add(listener);
			}
		}

		/**
		 * Removes the given progress listener, preventing it from receiving any more
		 * updates.
		 */
		@Override
		@Deprecated
		public void removeProgressListener(ProgressListener listener) {
			if (progressListeners.contains(listener)) {
				progressListeners.remove(listener);
			}
		}

		/**
		 * Updates the layout locations so the external nodes know about the new
		 * locations
		 */
		@Deprecated
		protected void updateLayoutLocations(InternalNode[] nodes) {
			for (InternalNode node : nodes) {
				if (!node.hasPreferredLocation()) {
					node.setLocation(node.getInternalX(), node.getInternalY());

					if ((layout_styles & LayoutStyles.NO_LAYOUT_NODE_RESIZING) != 1) {
						// Only set the size if we are supposed to
						node.setSize(node.getInternalWidth(), node.getInternalHeight());
					}
				}
			}
		}

		@Deprecated
		protected void fireProgressStarted(int totalNumberOfSteps) {
			ProgressEvent event = new ProgressEvent(0, totalNumberOfSteps);
			List.copyOf(progressListeners).forEach(listener -> listener.progressStarted(event));
		}

		@Deprecated
		protected void fireProgressEnded(int totalNumberOfSteps) {
			ProgressEvent event = new ProgressEvent(totalNumberOfSteps, totalNumberOfSteps);
			List.copyOf(progressListeners).forEach(listener -> listener.progressEnded(event));
		}

		/**
		 * @since 1.5
		 */
		@Deprecated
		protected void fireProgressUpdated(int currentStep, int totalNumberOfSteps) {
			ProgressEvent event = new ProgressEvent(currentStep, totalNumberOfSteps);
			List.copyOf(progressListeners).forEach(listener -> listener.progressUpdated(event));
		}

		/**
		 * Fires an event to notify all of the registered ProgressListeners that another
		 * step has been completed in the algorithm.
		 *
		 * @param currentStep        The current step completed.
		 * @param totalNumberOfSteps The total number of steps in the algorithm.
		 */
		@Deprecated
		protected void fireProgressEvent(int currentStep, int totalNumberOfSteps) {
			// Update the layout locations to the external nodes
			Calendar now = Calendar.getInstance();
			now.add(Calendar.MILLISECOND, -MIN_TIME_DELAY_BETWEEN_PROGRESS_EVENTS);

			if (now.after(lastProgressEventFired) || currentStep == totalNumberOfSteps) {
				fireProgressUpdated(currentStep, totalNumberOfSteps);
				lastProgressEventFired = Calendar.getInstance();
			}
		}

		@Deprecated
		protected int getNumberOfProgressListeners() {
			return progressListeners.size();
		}

		private void checkThread() {
			if (this.creationThread != Thread.currentThread()) {
				throw new RuntimeException("Invalid Thread Access."); //$NON-NLS-1$
			}
		}

		/**
		 * @since 2.0
		 */
		@Override
		@Deprecated
		public void setLayoutContext(LayoutContext context) {
			throw new UnsupportedOperationException("Not supported in Zest 1.x"); //$NON-NLS-1$
		}

		/**
		 * @since 2.0
		 */
		@Override
		@Deprecated
		public void applyLayout(boolean clean) {
			throw new UnsupportedOperationException("Not supported in Zest 1.x"); //$NON-NLS-1$
		}

	}
	
	/**
	 * @since 2.0
	 */
	protected LayoutContext context;

	@Override
	public void setLayoutContext(LayoutContext context) {
		this.context = context;
	}
}
