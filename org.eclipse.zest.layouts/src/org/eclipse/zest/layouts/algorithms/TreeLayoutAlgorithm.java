/*******************************************************************************
 * Copyright 2005-2010, 2025 CHISEL Group, University of Victoria, Victoria, BC,
 *                           Canada, Johannes Kepler University Linz and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria - initial API and implementation
 *               Alois Zoitl
 *               Mateusz Matela
 *               Ian Bull
 *               Miles Parker - optional node space configuration
 *******************************************************************************/
package org.eclipse.zest.layouts.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutObserver.TreeNode;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;
import org.eclipse.zest.layouts.interfaces.EntityLayout;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

import org.eclipse.draw2d.geometry.Dimension;

/**
 * The TreeLayoutAlgorithm class implements a simple algorithm to arrange graph
 * nodes in a layered tree-like layout.
 *
 * @version 3.0
 * @author Mateusz Matela
 * @author Casey Best and Rob Lintern (version 2.0)
 * @author Jingwei Wu (version 1.0)
 */
public class TreeLayoutAlgorithm extends AbstractLayoutAlgorithm {

	/**
	 * Collection of Zest 1.x methods. Used for backwards compatibility.
	 *
	 * @since 2.0
	 * @deprecated Use {@link TreeLayoutAlgorithm} instead. This class will be
	 *             removed in a future release.
	 * @noextend This class is not intended to be subclassed by clients.
	 * @noreference This class is not intended to be referenced by clients.
	 * @noinstantiate This class is not intended to be instantiated by clients.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "removal" })
	@Deprecated(since = "2.0", forRemoval = true)
	public static class Zest1 extends AbstractLayoutAlgorithm.Zest1 {

		private static final double DEFAULT_WEIGHT = 0;
		private static final boolean DEFAULT_MARKED = false;

		private static final boolean AS_DESTINATION = false;
		private static final boolean AS_SOURCE = true;

		private static final int NUM_DESCENDENTS_INDEX = 0;
		private static final int NUM_LEVELS_INDEX = 1;

		private List<InternalNode> treeRoots;

		private double boundsX;
		private double boundsY;
		private double boundsWidth;
		private double boundsHeight;
		private DisplayIndependentRectangle layoutBounds = null;

		private List<InternalNode>[] parentLists;
		private List<InternalNode>[] childrenLists;
		private double[] weights;
		private boolean[] markedArr;

		/////////////////////////////////////////////////////////////////////////
		///// Constructors /////
		/////////////////////////////////////////////////////////////////////////

		/**
		 * Constructs a new TreeLayoutAlgorithm object.
		 */
		@Deprecated
		public Zest1(int styles) {
			super(styles);
		}

		/**
		 * Tree layout algorithm Constructor with NO Style
		 *
		 */
		@Deprecated
		public Zest1() {
			this(LayoutStyles.NONE);
		}

		/////////////////////////////////////////////////////////////////////////
		///// Public Methods /////
		/////////////////////////////////////////////////////////////////////////

		@Override
		@Deprecated
		public void setLayoutArea(double x, double y, double width, double height) {
			throw new RuntimeException();
		}

		@Override
		@Deprecated
		protected int getCurrentLayoutStep() {
			return 0;
		}

		@Override
		@Deprecated
		protected int getTotalNumberOfLayoutSteps() {
			return 4;
		}

		/**
		 * Executes this TreeLayoutAlgorithm layout algorithm by referencing the data
		 * stored in the repository system. Once done, the result will be saved to the
		 * data repository.
		 *
		 * @param entitiesToLayout        Apply the algorithm to these entities
		 * @param relationshipsToConsider Only consider these relationships when
		 *                                applying the algorithm.
		 * @param x                       The left side of the bounds in which the
		 *                                layout can place the entities.
		 * @param y                       The top side of the bounds in which the layout
		 *                                can place the entities.
		 * @param width                   The width of the bounds in which the layout
		 *                                can place the entities.
		 * @param height                  The height of the bounds in which the layout
		 *                                can place the entities.
		 * @throws RuntimeException Thrown if entitiesToLayout doesn't contain all of
		 *                          the endpoints for each relationship in
		 *                          relationshipsToConsider
		 */
		@Override
		@Deprecated
		protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
				double x, double y, double width, double height) {
			// Filter unwanted entities and relationships
			// super.applyLayout (entitiesToLayout, relationshipsToConsider, boundsX,
			// boundsY, boundsWidth, boundsHeight);

			parentLists = new List[entitiesToLayout.length];
			childrenLists = new List[entitiesToLayout.length];
			weights = new double[entitiesToLayout.length];
			markedArr = new boolean[entitiesToLayout.length];
			for (int i = 0; i < entitiesToLayout.length; i++) {
				parentLists[i] = new ArrayList<>();
				childrenLists[i] = new ArrayList<>();
				weights[i] = DEFAULT_WEIGHT;
				markedArr[i] = DEFAULT_MARKED;
			}

			this.boundsHeight = height;
			this.boundsWidth = width;
			this.boundsX = x;
			this.boundsY = y;
			layoutBounds = new DisplayIndependentRectangle(boundsX, boundsY, boundsWidth, boundsHeight);

		}

		@Override
		@Deprecated
		protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
				double boundsX, double boundsY, double boundsWidth, double boundsHeight) {

			if (entitiesToLayout.length > 0) {
				int totalProgress = 4;
				fireProgressEvent(1, totalProgress);

				treeRoots = new ArrayList<>();
				buildForest(treeRoots, entitiesToLayout, relationshipsToConsider);
				fireProgressEvent(2, totalProgress);
				computePositions(treeRoots, entitiesToLayout);
				fireProgressEvent(3, totalProgress);
				defaultFitWithinBounds(entitiesToLayout, layoutBounds);

			}
		}

		@Override
		@Deprecated
		protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout,
				InternalRelationship[] relationshipsToConsider) {
			updateLayoutLocations(entitiesToLayout);
			fireProgressEvent(4, 4);
		}

		/**
		 * Returns the last found roots
		 */
		@Deprecated
		public List getRoots() {
			return treeRoots;
		}

		/**
		 * Finds all the relationships in which the node <code>obj<code>
		 * plays the specified <code>role</code>.
		 *
		 * @param entity The node that concerns the relations to be found.
		 * @param role   The role played by the <code>obj</code>. Its type must be of
		 *               <code>ACTOR_ROLE</code> or <code>ACTEE_ROLE</code>.
		 * @see SimpleRelationship
		 */
		private static Collection<InternalRelationship> findRelationships(Object entity, boolean objectAsSource,
				InternalRelationship[] relationshipsToConsider) {
			Collection<InternalRelationship> foundRels = new ArrayList<>();
			for (InternalRelationship rel : relationshipsToConsider) {
				if (objectAsSource && rel.getSource().equals(entity)) {
					foundRels.add(rel);
				} else if (!objectAsSource && rel.getDestination().equals(entity)) {
					foundRels.add(rel);
				}
			}
			return foundRels;
		}

		/**
		 * Finds the relation that has the lowest index in the relation repository in
		 * which the node <code>obj<code> plays the specified
		 * <code>role</code>.
		 *
		 * @param obj  The node that concerns the relations to be found.
		 * @param role The role played by the <code>obj</code>. Its type must be of
		 *             <code>ACTOR_ROLE</code> or <code>ACTEE_ROLE</code>.
		 * @see SimpleRelationship
		 * @see SimpleRelationship#ACTOR_ROLE
		 * @see SimpleRelationship#ACTEE_ROLE
		 */
		private static InternalRelationship findRelationship(Object entity, boolean objectAsSource,
				InternalRelationship[] relationshipsToConsider) {
			InternalRelationship relationship = null;
			for (int i = 0; i < relationshipsToConsider.length && relationship == null; i++) {
				InternalRelationship possibleRel = relationshipsToConsider[i];
				if (objectAsSource && possibleRel.getSource().equals(entity)) {
					relationship = possibleRel;
				} else if (!objectAsSource && possibleRel.getDestination().equals(entity)) {
					relationship = possibleRel;
				}
			}
			return relationship;
		}

		/////////////////////////////////////////////////////////////////////////
		///// Private Methods /////
		/////////////////////////////////////////////////////////////////////////

		/**
		 * Builds the tree forest that is used to calculate positions for each node in
		 * this TreeLayoutAlgorithm.
		 */
		private void buildForest(List<InternalNode> roots, InternalNode[] entities, InternalRelationship[] relationships) {
			List<InternalNode> unplacedEntities = new ArrayList<>(Arrays.asList(entities));
			buildForestRecursively(roots, unplacedEntities, entities, relationships);
		}

		/**
		 * Builds the forest recursively. All entities will be placed somewhere in the
		 * forest.
		 */
		private void buildForestRecursively(List<InternalNode> roots, List<InternalNode> unplacedEntities,
				InternalNode[] entities, InternalRelationship[] relationships) {
			if (unplacedEntities.isEmpty()) {
				return; // no more entities to place
			}

			// get the first entity in the list of unplaced entities, find its root, and
			// build this root's tree
			InternalNode layoutEntity = unplacedEntities.get(0);
			InternalNode rootEntity = findRootObjectRecursive(layoutEntity, new HashSet<>(), relationships);
			int rootEntityIndex = indexOfInternalNode(entities, rootEntity);
			buildTreeRecursively(rootEntity, rootEntityIndex, 0, entities, relationships);
			roots.add(rootEntity);

			// now see which nodes are left to be placed in a tree somewhere
			List<InternalNode> unmarkedCopy = new ArrayList<>(unplacedEntities);
			for (InternalNode tmpEntity : unmarkedCopy) {
				int tmpEntityIndex = indexOfInternalNode(entities, tmpEntity);
				boolean isMarked = markedArr[tmpEntityIndex];
				if (isMarked) {
					unplacedEntities.remove(tmpEntity);
				}
			}
			buildForestRecursively(roots, unplacedEntities, entities, relationships);
		}

		/**
		 * Finds the root node that can be treated as the root of a tree. The found root
		 * node should be one of the unmarked nodes.
		 */
		private InternalNode findRootObjectRecursive(InternalNode currentEntity, Set<InternalNode> seenAlready,
				InternalRelationship[] relationshipsToConsider) {
			InternalNode rootEntity = null;
			InternalRelationship rel = findRelationship(currentEntity, AS_DESTINATION, relationshipsToConsider);
			if (rel == null) {
				rootEntity = currentEntity;
			} else {
				InternalNode parentEntity = rel.getSource();
				if (!seenAlready.contains(parentEntity)) {
					seenAlready.add(parentEntity);
					rootEntity = findRootObjectRecursive(parentEntity, seenAlready, relationshipsToConsider);
				} else {
					rootEntity = currentEntity;
				}
			}
			return rootEntity;
		}

		/**
		 * Builds a tree of the passed in entity. The entity will pass a weight value to
		 * all of its children recursively.
		 */
		private void buildTreeRecursively(InternalNode layoutEntity, int i, double weight, InternalNode[] entities,
				final InternalRelationship[] relationships) {
			// No need to do further computation!
			if (layoutEntity == null) {
				return;
			}

			// A marked entity means that it has been added to the
			// forest, and its weight value needs to be modified.
			if (markedArr[i]) {
				modifyWeightRecursively(layoutEntity, i, weight, new HashSet<>(), entities, relationships);
				return; // No need to do further computation.
			}

			// Mark this entity, set its weight value and create a new tree node.
			markedArr[i] = true;
			weights[i] = weight;

			// collect the children of this entity and put them in order
			Collection<InternalRelationship> rels = findRelationships(layoutEntity, AS_SOURCE, relationships);
			List<InternalNode> children = new ArrayList<>();
			for (InternalRelationship layoutRel : rels) {
				InternalNode childEntity = layoutRel.getDestination();
				children.add(childEntity);
			}

			if (comparator != null) {
				Collections.sort(children, comparator);
			} else {
				// sort the children by level, then by number of descendents, then by number of
				// children
				// TODO: SLOW
				Collections.sort(children, (o1, o2) -> {
					InternalNode node1 = o1;
					InternalNode node2 = o2;
					int[] numDescendentsAndLevel1 = new int[2];
					int[] numDescendentsAndLevel2 = new int[2];
					int level1 = numDescendentsAndLevel1[NUM_LEVELS_INDEX];
					int level2 = numDescendentsAndLevel2[NUM_LEVELS_INDEX];
					if (level1 != level2) {
						return level2 - level1;
					}
					// return getNumChildren(node2, relationships) - getNumChildren(node1,
					// relationships);
					getNumDescendentsAndLevel(node1, relationships, numDescendentsAndLevel1);
					getNumDescendentsAndLevel(node2, relationships, numDescendentsAndLevel2);
					int numDescendents1 = numDescendentsAndLevel1[NUM_DESCENDENTS_INDEX];
					int numDescendents2 = numDescendentsAndLevel2[NUM_DESCENDENTS_INDEX];
					if (numDescendents1 == numDescendents2) {
						int numChildren1 = getNumChildren(node1, relationships);
						int numChildren2 = getNumChildren(node1, relationships);
						return numChildren2 - numChildren1;
					}
					return numDescendents2 - numDescendents1;
				});
			}

			// map children to this parent, and vice versa
			for (InternalNode childEntity : children) {
				int childEntityIndex = indexOfInternalNode(entities, childEntity);
				if (!childrenLists[i].contains(childEntity)) {
					childrenLists[i].add(childEntity);
				}
				if (!parentLists[childEntityIndex].contains(layoutEntity)) {
					parentLists[childEntityIndex].add(layoutEntity);
				}
			}

			for (InternalNode childEntity : children) {
				int childEntityIndex = indexOfInternalNode(entities, childEntity);
				buildTreeRecursively(childEntity, childEntityIndex, weight + 1, entities, relationships);
			}
		}

		private static int getNumChildren(InternalNode layoutEntity, InternalRelationship[] relationships) {
			return findRelationships(layoutEntity, AS_SOURCE, relationships).size();
		}

		private void getNumDescendentsAndLevel(InternalNode layoutEntity, InternalRelationship[] relationships,
				int[] numDescendentsAndLevel) {
			getNumDescendentsAndLevelRecursive(layoutEntity, relationships, new HashSet<>(), numDescendentsAndLevel, 0);
		}

		private void getNumDescendentsAndLevelRecursive(InternalNode layoutEntity, InternalRelationship[] relationships,
				Set<InternalNode> seenAlready, int[] numDescendentsAndLevel, int currentLevel) {
			if (seenAlready.contains(layoutEntity)) {
				return;
			}
			seenAlready.add(layoutEntity);
			numDescendentsAndLevel[NUM_LEVELS_INDEX] = Math.max(numDescendentsAndLevel[NUM_LEVELS_INDEX], currentLevel);
			Collection<InternalRelationship> rels = findRelationships(layoutEntity, AS_SOURCE, relationships);
			for (InternalRelationship layoutRel : rels) {
				InternalNode childEntity = layoutRel.getDestination();
				numDescendentsAndLevel[NUM_DESCENDENTS_INDEX]++;
				getNumDescendentsAndLevelRecursive(childEntity, relationships, seenAlready, numDescendentsAndLevel,
						currentLevel + 1);

			}
		}

		/**
		 * Modifies the weight value of the marked node recursively.
		 */
		private void modifyWeightRecursively(InternalNode layoutEntity, int i, double weight,
				Set<InternalNode> descendentsSeenSoFar, InternalNode[] entities, InternalRelationship[] relationships) {
			// No need to do further computation!
			if (layoutEntity == null) {
				return;
			}

			if (descendentsSeenSoFar.contains(layoutEntity)) {
				return; // No need to do further computation.
			}

			descendentsSeenSoFar.add(layoutEntity);
			// No need to do further computation!
			if (weight < weights[i]) {
				return;
			}

			weights[i] = weight;
			Collection<InternalRelationship> rels = findRelationships(layoutEntity, AS_SOURCE, relationships);

			for (InternalRelationship tmpRel : rels) {
				InternalNode tmpEntity = tmpRel.getDestination();
				int tmpEntityIndex = indexOfInternalNode(entities, tmpEntity);
				modifyWeightRecursively(tmpEntity, tmpEntityIndex, weight + 1, descendentsSeenSoFar, entities,
						relationships);
			}
		}

		/**
		 * Gets the maxium weight of a tree in the forest of this TreeLayoutAlgorithm.
		 */
		private double getMaxiumWeightRecursive(InternalNode layoutEntity, int i, Set<InternalNode> seenAlready,
				InternalNode[] entities) {
			double result = 0;
			if (seenAlready.contains(layoutEntity)) {
				return result;
			}
			seenAlready.add(layoutEntity);
			List<InternalNode> children = childrenLists[i];
			if (children.isEmpty()) {
				result = weights[i];
			} else {
				// TODO: SLOW
				for (InternalNode childEntity : children) {
					int childEntityIndex = indexOfInternalNode(entities, childEntity);
					result = Math.max(result,
							getMaxiumWeightRecursive(childEntity, childEntityIndex, seenAlready, entities));
				}
			}
			return result;
		}

		/**
		 * Computes positions for each node in this TreeLayoutAlgorithm by referencing
		 * the forest that holds those nodes.
		 */
		private void computePositions(List<InternalNode> roots, InternalNode[] entities) {
			// No need to do further computation!
			if (roots.isEmpty()) {
				return;
			}

			int totalLeafCount = 0;
			double maxWeight = 0;
			for (InternalNode rootEntity : roots) {
				int rootEntityIndex = indexOfInternalNode(entities, rootEntity);
				totalLeafCount = totalLeafCount + getNumberOfLeaves(rootEntity, rootEntityIndex, entities);
				maxWeight = Math.max(maxWeight,
						getMaxiumWeightRecursive(rootEntity, rootEntityIndex, new HashSet<>(), entities) + 1.0);
			}

			double width = 1.0 / totalLeafCount;
			double height = 1.0 / maxWeight;

			int leafCountSoFar = 0;

			// TODO: SLOW!
			for (InternalNode rootEntity : roots) {
				int rootEntityIndex = indexOfInternalNode(entities, rootEntity);
				computePositionRecursively(rootEntity, rootEntityIndex, leafCountSoFar, width, height, new HashSet<>(),
						entities);
				leafCountSoFar = leafCountSoFar + getNumberOfLeaves(rootEntity, rootEntityIndex, entities);
			}
		}

		/**
		 * Computes positions recursively until the leaf nodes are reached.
		 */
		private void computePositionRecursively(InternalNode layoutEntity, int i, int relativePosition, double width,
				double height, Set<InternalNode> seenAlready, InternalNode[] entities) {
			if (seenAlready.contains(layoutEntity)) {
				return;
			}
			seenAlready.add(layoutEntity);
			double level = getLevel(layoutEntity, i, entities);
			int breadth = getNumberOfLeaves(layoutEntity, i, entities);
			double absHPosition = relativePosition + breadth / 2.0;
			double absVPosition = (level + 0.5);

			double posx = absHPosition * width;
			double posy = absVPosition * height;
			double weight = weights[i];
			posy = posy + height * (weight - level);
			layoutEntity.setInternalLocation(posx, posy);

			int relativeCount = 0;
			// TODO: Slow
			for (InternalNode childEntity : childrenLists[i]) {
				int childEntityIndex = indexOfInternalNode(entities, childEntity);
				computePositionRecursively(childEntity, childEntityIndex, relativePosition + relativeCount, width, height,
						seenAlready, entities);
				relativeCount = relativeCount + getNumberOfLeaves(childEntity, childEntityIndex, entities);
			}
		}

		private int getNumberOfLeaves(InternalNode layoutEntity, int i, InternalNode[] entities) {
			return getNumberOfLeavesRecursive(layoutEntity, i, new HashSet<>(), entities);
		}

		private int getNumberOfLeavesRecursive(InternalNode layoutEntity, int i, Set<InternalNode> seen,
				InternalNode[] entities) {
			int numLeaves = 0;
			List<InternalNode> children = childrenLists[i];
			if (children.isEmpty()) {
				numLeaves = 1;
			} else {
				// TODO: SLOW!
				for (InternalNode childEntity : children) {
					if (!seen.contains(childEntity)) {
						seen.add(childEntity);
						int childEntityIndex = indexOfInternalNode(entities, childEntity);
						numLeaves += getNumberOfLeavesRecursive(childEntity, childEntityIndex, seen, entities);
					} else {
						numLeaves = 1;
					}
				}
			}
			return numLeaves;
		}

		private int getLevel(InternalNode layoutEntity, int i, InternalNode[] entities) {
			return getLevelRecursive(layoutEntity, i, new HashSet<>(), entities);
		}

		private int getLevelRecursive(InternalNode layoutEntity, int i, Set<InternalNode> seen, InternalNode[] entities) {
			if (seen.contains(layoutEntity)) {
				return 0;
			}
			seen.add(layoutEntity);
			int maxParentLevel = 0;
			for (InternalNode parentEntity : parentLists[i]) {
				int parentEntityIndex = indexOfInternalNode(entities, parentEntity);
				int parentLevel = getLevelRecursive(parentEntity, parentEntityIndex, seen, entities) + 1;
				maxParentLevel = Math.max(maxParentLevel, parentLevel);
			}
			return maxParentLevel;
		}

		/**
		 * Note: Use this as little as possible! TODO limit the use of this method
		 *
		 * @param nodes
		 * @param nodeToFind
		 * @return
		 */
		private static int indexOfInternalNode(InternalNode[] nodes, InternalNode nodeToFind) {
			for (int i = 0; i < nodes.length; i++) {
				InternalNode node = nodes[i];
				if (node.equals(nodeToFind)) {
					return i;
				}
			}
			throw new RuntimeException("Couldn't find index of internal node: " + nodeToFind); //$NON-NLS-1$
		}

		@Override
		@Deprecated
		protected boolean isValidConfiguration(boolean asynchronous, boolean continueous) {
			return !continueous;
		}

	}

	/**
	 * Tree direction constant for which root is placed at the top and branches
	 * spread downwards
	 *
	 * @since 2.0
	 */
	public final static int TOP_DOWN = 1;

	/**
	 * Tree direction constant for which root is placed at the bottom and branches
	 * spread upwards
	 *
	 * @since 2.0
	 */
	public final static int BOTTOM_UP = 2;

	/**
	 * Tree direction constant for which root is placed at the left and branches
	 * spread to the right
	 *
	 * @since 2.0
	 */
	public final static int LEFT_RIGHT = 3;

	/**
	 * Tree direction constant for which root is placed at the right and branches
	 * spread to the left
	 *
	 * @since 2.0
	 */
	public final static int RIGHT_LEFT = 4;

	private int direction = TOP_DOWN;

	private boolean resize = false;

	private DisplayIndependentRectangle bounds;

	private double leafSize, layerSize;

	private TreeLayoutObserver treeObserver;

	private Dimension nodeSpace;

	/**
	 * Create a default Tree Layout.
	 */
	public TreeLayoutAlgorithm() {
	}

	/**
	 * Create a Tree Layout with a specified direction.
	 *
	 * @param direction The direction, one of {@link TreeLayoutAlgorithm#BOTTOM_UP},
	 *                  {@link TreeLayoutAlgorithm#LEFT_RIGHT},
	 *                  {@link TreeLayoutAlgorithm#RIGHT_LEFT},
	 *                  {@link TreeLayoutAlgorithm#TOP_DOWN}
	 */
	public TreeLayoutAlgorithm(int direction) {
		this(direction, null);
	}

	/**
	 * Create a Tree Layout with fixed size spacing around nodes. If nodeSpace is
	 * not null, the layout will size the container to the ideal space to just
	 * contain all nodes of fixed size without any overlap. Otherwise, the algorithm
	 * will size for the container's available space.
	 *
	 * @param direction The direction, one of {@link TreeLayoutAlgorithm#BOTTOM_UP},
	 *                  {@link TreeLayoutAlgorithm#LEFT_RIGHT},
	 *                  {@link TreeLayoutAlgorithm#RIGHT_LEFT},
	 *                  {@link TreeLayoutAlgorithm#TOP_DOWN}
	 * @param nodeSpace the size to make each node. May be null.
	 * @since 2.0
	 */
	public TreeLayoutAlgorithm(int direction, Dimension nodeSpace) {
		setDirection(direction);
		this.nodeSpace = nodeSpace;
	}

	/**
	 * @param nodeSpace the nodeSpaceSize to set
	 * @since 2.0
	 */
	public void setNodeSpace(Dimension nodeSpace) {
		this.nodeSpace = nodeSpace;
	}

	/**
	 * @since 2.0
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * @since 2.0
	 */
	public void setDirection(int direction) {
		if ((direction != TOP_DOWN) && (direction != BOTTOM_UP) && (direction != LEFT_RIGHT)
				&& (direction != RIGHT_LEFT)) {
			throw new IllegalArgumentException("Invalid direction: " + direction); //$NON-NLS-1$
		}
		this.direction = direction;
	}

	/**
	 *
	 * @return true if this algorithm is set to resize elements
	 * @since 2.0
	 */
	public boolean isResizing() {
		return resize;
	}

	/**
	 *
	 * @param resizing true if this algorithm should resize elements (default is
	 *                 false)
	 * @since 2.0
	 */
	public void setResizing(boolean resizing) {
		resize = resizing;
	}

	@Override
	public void setLayoutContext(LayoutContext context) {
		if (treeObserver != null) {
			treeObserver.stop();
		}
		super.setLayoutContext(context);
		if (context != null) {
			treeObserver = new TreeLayoutObserver(context, null);
		}
	}

	@Override
	public void applyLayout(boolean clean) {
		if (!clean) {
			return;
		}

		internalApplyLayout();

		EntityLayout[] entities = context.getEntities();

		if (resize) {
			AlgorithmHelper.maximizeSizes(entities);
		}
		scaleEntities(entities);
	}

	private void scaleEntities(EntityLayout[] entities) {
		if (nodeSpace == null) {
			DisplayIndependentRectangle bounds2 = new DisplayIndependentRectangle(bounds);
			int insets = 4;
			bounds2.x += insets;
			bounds2.y += insets;
			bounds2.width -= 2 * insets;
			bounds2.height -= 2 * insets;
			AlgorithmHelper.fitWithinBounds(entities, bounds2, resize);
		}
	}

	void internalApplyLayout() {
		TreeNode superRoot = treeObserver.getSuperRoot();
		bounds = context.getBounds();
		updateLeafAndLayerSizes();
		int leafCountSoFar = 0;
		for (Object element : superRoot.getChildren()) {
			TreeNode rootInfo = (TreeNode) element;
			computePositionRecursively(rootInfo, leafCountSoFar);
			leafCountSoFar = leafCountSoFar + rootInfo.numOfLeaves;
		}
	}

	private void updateLeafAndLayerSizes() {
		if (nodeSpace != null) {
			if (getDirection() == TOP_DOWN || getDirection() == BOTTOM_UP) {
				leafSize = nodeSpace.preciseWidth();
				layerSize = nodeSpace.preciseHeight();
			} else {
				leafSize = nodeSpace.preciseHeight();
				layerSize = nodeSpace.preciseWidth();
			}
		} else {
			TreeNode superRoot = treeObserver.getSuperRoot();
			if (direction == TOP_DOWN || direction == BOTTOM_UP) {
				leafSize = bounds.width / superRoot.numOfLeaves;
				layerSize = bounds.height / superRoot.height;
			} else {
				leafSize = bounds.height / superRoot.numOfLeaves;
				layerSize = bounds.width / superRoot.height;
			}
		}
	}

	/**
	 * Computes positions recursively until the leaf nodes are reached.
	 */
	private void computePositionRecursively(TreeNode entityInfo, int relativePosition) {
		double breadthPosition = relativePosition + entityInfo.numOfLeaves / 2.0;
		double depthPosition = (entityInfo.depth + 0.5);

		switch (direction) {
		case TOP_DOWN:
			entityInfo.getNode().setLocation(breadthPosition * leafSize, depthPosition * layerSize);
			break;
		case BOTTOM_UP:
			entityInfo.getNode().setLocation(breadthPosition * leafSize, bounds.height - depthPosition * layerSize);
			break;
		case LEFT_RIGHT:
			entityInfo.getNode().setLocation(depthPosition * layerSize, breadthPosition * leafSize);
			break;
		case RIGHT_LEFT:
			entityInfo.getNode().setLocation(bounds.width - depthPosition * layerSize, breadthPosition * leafSize);
			break;
		}

		for (Object child : entityInfo.children) {
			TreeNode childInfo = (TreeNode) child;
			computePositionRecursively(childInfo, relativePosition);
			relativePosition += childInfo.numOfLeaves;
		}
	}
}
