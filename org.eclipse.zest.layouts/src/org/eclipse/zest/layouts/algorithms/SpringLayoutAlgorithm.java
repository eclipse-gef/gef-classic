/*******************************************************************************
 * Copyright 2005.2010, 2025 CHISEL Group, University of Victoria, Victoria, BC,
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
 *******************************************************************************/
package org.eclipse.zest.layouts.algorithms;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;
import org.eclipse.zest.layouts.interfaces.ConnectionLayout;
import org.eclipse.zest.layouts.interfaces.EntityLayout;
import org.eclipse.zest.layouts.interfaces.LayoutContext;
import org.eclipse.zest.layouts.interfaces.LayoutListener;
import org.eclipse.zest.layouts.interfaces.NodeLayout;
import org.eclipse.zest.layouts.interfaces.SubgraphLayout;

/**
 * The SpringLayoutAlgorithm has its own data repository and relation
 * repository. A user can populate the repository, specify the layout
 * conditions, do the computation and query the computed results.
 *
 * @version 2.0
 * @author Ian Bull
 * @author Casey Best (version 1.0 by Jingwei Wu/Rob Lintern)
 */
public class SpringLayoutAlgorithm extends AbstractLayoutAlgorithm {

	/**
	 * Collection of Zest 1.x methods. Used for backwards compatibility.
	 *
	 * @since 2.0
	 * @deprecated Use {@link SpringLayoutAlgorithm} instead. This class will be
	 *             removed in a future release.
	 * @noextend This class is not intended to be subclassed by clients.
	 * @noreference This class is not intended to be referenced by clients.
	 * @noinstantiate This class is not intended to be instantiated by clients.
	 */
	@Deprecated(since = "2.0", forRemoval = true)
	public static class Zest1 extends ContinuousLayoutAlgorithm {

		private static final boolean DEFAULT_ANCHOR = false;

		/**
		 * The default value for the spring layout number of interations.
		 */
		@Deprecated
		public static final int DEFAULT_SPRING_ITERATIONS = 1000;

		/**
		 * the default value for the time algorithm runs.
		 */
		@Deprecated
		public static final long MAX_SPRING_TIME = 10000;

		/**
		 * The default value for positioning nodes randomly.
		 */
		@Deprecated
		public static final boolean DEFAULT_SPRING_RANDOM = true;

		/**
		 * The default value for ignoring unconnected nodes.
		 */
		@Deprecated
		public static final boolean DEFAULT_SPRING_IGNORE_UNCON = true;

		/**
		 * The default value for separating connected components.
		 */
		@Deprecated
		public static final boolean DEFAULT_SPRING_SEPARATE_COMPONENTS = true;

		/**
		 * The default value for the spring layout move-control.
		 */
		@Deprecated
		public static final double DEFAULT_SPRING_MOVE = 1.0f;

		/**
		 * The default value for the spring layout strain-control.
		 */
		@Deprecated
		public static final double DEFAULT_SPRING_STRAIN = 1.0f;

		/**
		 * The default value for the spring layout length-control.
		 */
		@Deprecated
		public static final double DEFAULT_SPRING_LENGTH = 1.0f;

		/**
		 * The default value for the spring layout gravitation-control.
		 */
		@Deprecated
		public static final double DEFAULT_SPRING_GRAVITATION = 1.0f;

		/**
		 * The variable can be customized to set the number of iterations used.
		 */
		private static int sprIterations = DEFAULT_SPRING_ITERATIONS;

		/**
		 * This variable can be customized to set the max number of MS the algorithm
		 * should run
		 */
		private static long maxTimeMS = MAX_SPRING_TIME;

		/**
		 * The variable can be customized to set whether or not the spring layout nodes
		 * are positioned randomly before beginning iterations.
		 */
		private static boolean sprRandom = DEFAULT_SPRING_RANDOM;

		/**
		 * Minimum distance considered between nodes
		 */
		@Deprecated
		protected static final double MIN_DISTANCE = 0.001d;

		/**
		 * An arbitrarily small value in mathematics.
		 */
		@Deprecated
		protected static final double EPSILON = 0.001d;

		/**
		 * The variable can be customerized to set the spring layout move-control.
		 */
		private static double sprMove = DEFAULT_SPRING_MOVE;

		/**
		 * The variable can be customized to set the spring layout strain-control.
		 */
		private static double sprStrain = DEFAULT_SPRING_STRAIN;

		/**
		 * The variable can be customized to set the spring layout length-control.
		 */
		private static double sprLength = DEFAULT_SPRING_LENGTH;

		/**
		 * The variable can be customized to set the spring layout gravitation-control.
		 */
		private static double sprGravitation = DEFAULT_SPRING_GRAVITATION;

		/**
		 * The largest movement of all vertices that has occured in the most recent
		 * iteration.
		 */
		private double largestMovement = 0;

		/**
		 * Maps a src and dest object to the number of relations between them. Key is
		 * src.toString() + dest.toString(), value is an Integer
		 */
		private Map<String, Integer> srcDestToNumRelsMap;

		/**
		 * Maps a src and dest object to the average weight of the relations between
		 * them. Key is src.toString() + dest.toString(), value is a Double
		 */
		private Map<String, Double> srcDestToRelsAvgWeightMap;

		/**
		 * Maps a relationship type to a weight. Key is a string, value is a Double
		 */
		private static Map<String, Double> relTypeToWeightMap = new HashMap<>();

		private int iteration;

		private int[][] srcDestToNumRels;

		private double[][] srcDestToRelsAvgWeight;

		private double[] tempLocationsX;

		private double[] tempLocationsY;

		private double[] forcesX;

		private double[] forcesY;

		private boolean[] anchors;

		private DisplayIndependentRectangle bounds = null;

		@Deprecated
		Date date = null;

		/**
		 * Constructor.
		 */
		@Deprecated
		public Zest1(int styles) {
			super(styles);
			srcDestToNumRelsMap = new HashMap<>();
			srcDestToRelsAvgWeightMap = new HashMap<>();
			date = new Date();
		}

		/**
		 * Creates a sprint layout algoirthm with no style
		 *
		 */
		@Deprecated
		public Zest1() {
			this(LayoutStyles.NONE);
		}

		@Override
		@Deprecated
		public void setLayoutArea(double x, double y, double width, double height) {
			bounds = new DisplayIndependentRectangle(x, y, width, height);
		}

		/**
		 * Sets the spring layout move-control.
		 *
		 * @param move The move-control value.
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		public void setSpringMove(double move) {
			sprMove = move;
		}

		/**
		 * Returns the move-control value of this SpringLayoutAlgorithm in double
		 * presion.
		 *
		 * @return The move-control value.
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		public double getSpringMove() {
			return sprMove;
		}

		/**
		 * Sets the spring layout strain-control.
		 *
		 * @param strain The strain-control value.
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		public void setSpringStrain(double strain) {
			sprStrain = strain;
		}

		/**
		 * Returns the strain-control value of this SpringLayoutAlgorithm in double
		 * presion.
		 *
		 * @return The strain-control value.
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		public double getSpringStrain() {
			return sprStrain;
		}

		/**
		 * Sets the spring layout length-control.
		 *
		 * @param length The length-control value.
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		public void setSpringLength(double length) {
			sprLength = length;
		}

		/**
		 * Gets the max time this algorithm will run for
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		public long getSpringTimeout() {
			return maxTimeMS;
		}

		/**
		 * Sets the spring timeout
		 *
		 * @param timeout
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		public void setSpringTimeout(long timeout) {
			maxTimeMS = timeout;
		}

		/**
		 * Returns the length-control value of this SpringLayoutAlgorithm in double
		 * presion.
		 *
		 * @return The length-control value.
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		public double getSpringLength() {
			return sprLength;
		}

		/**
		 * Sets the spring layout gravitation-control.
		 *
		 * @param gravitation The gravitation-control value.
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		public void setSpringGravitation(double gravitation) {
			sprGravitation = gravitation;
		}

		/**
		 * Returns the gravitation-control value of this SpringLayoutAlgorithm in double
		 * presion.
		 *
		 * @return The gravitation-control value.
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		public double getSpringGravitation() {
			return sprGravitation;
		}

		/**
		 * Sets the number of iterations to be used.
		 *
		 * @param iterations The number of iterations.
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		public void setIterations(int iterations) {
			sprIterations = iterations;
		}

		/**
		 * Returns the number of iterations to be used.
		 *
		 * @return The number of iterations.
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		public int getIterations() {
			return sprIterations;
		}

		/**
		 * Sets whether or not this SpringLayoutAlgorithm will layout the nodes randomly
		 * before beginning iterations.
		 *
		 * @param random The random placement value.
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		public void setRandom(boolean random) {
			sprRandom = random;
		}

		/**
		 * Returns whether or not this SpringLayoutAlgorithm will layout the nodes
		 * randomly before beginning iterations.
		 */
		@Deprecated
		@SuppressWarnings("static-method")
		public boolean getRandom() {
			return sprRandom;
		}

		@Deprecated
		@SuppressWarnings("static-method")
		public void setWeight(String relType, double weight) {
			relTypeToWeightMap.put(relType, Double.valueOf(weight));
		}

		@Deprecated
		@SuppressWarnings("static-method")
		public double getWeight(String relType) {
			Double weight = relTypeToWeightMap.get(relType);
			return (weight == null) ? 1 : weight.doubleValue();
		}

		/**
		 * Sets the default conditions.
		 */
		@Deprecated
		public void setDefaultConditions() {
			// sprMove = DEFAULT_SPRING_MOVE;
			// sprStrain = DEFAULT_SPRING_STRAIN;
			// sprLength = DEFAULT_SPRING_LENGTH;
			// sprGravitation = DEFAULT_SPRING_GRAVITATION;
			// sprIterations = DEFAULT_SPRING_ITERATIONS;
		}

		/**
		 * Clean up after done
		 *
		 * @param entitiesToLayout
		 */
		private void reset(InternalNode[] entitiesToLayout) {
			tempLocationsX = null;
			tempLocationsY = null;
			forcesX = null;
			forcesY = null;
			anchors = null;
			setDefaultConditions();
			srcDestToNumRelsMap = new HashMap<>();
			srcDestToRelsAvgWeightMap = new HashMap<>();
			relTypeToWeightMap = new HashMap<>();
		}

		private long startTime = 0;

		@Override
		@Deprecated
		protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
				double x, double y, double width, double height) {
			// TODO: Filter out any non-wanted entities and relationships
			// super.applyLayout(entitiesToLayout, relationshipsToConsider, x, y,
			// width, height);
			// InternalNode[] a_entitiesToLayout = (InternalNode[])
			// entitiesToLayout.toArray(new InternalNode[entitiesToLayout.size()]);
			bounds = new DisplayIndependentRectangle(x, y, width, height);
			tempLocationsX = new double[entitiesToLayout.length];
			tempLocationsY = new double[entitiesToLayout.length];
			forcesX = new double[entitiesToLayout.length];
			forcesY = new double[entitiesToLayout.length];
			anchors = new boolean[entitiesToLayout.length];

			for (int i = 0; i < entitiesToLayout.length; i++) {
				anchors[i] = DEFAULT_ANCHOR;
			}
			for (InternalRelationship layoutRelationship : relationshipsToConsider) {
				addRelation(layoutRelationship);
			}

			// do the calculations
			preCompute(entitiesToLayout);
			startTime = date.getTime();
		}

		@Override
		@Deprecated
		protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout,
				InternalRelationship[] relationshipsToConsider) {
			reset(entitiesToLayout);
		}

		/**
		 * Adds a simple relation between two nodes to the relation repository.
		 *
		 * @param layoutRelationship The simple relation to be added
		 * @throws java.lang.NullPointerExcetption If <code>sr</code> is null
		 * @see SimpleRelation
		 */
		private void addRelation(InternalRelationship layoutRelationship) {
			if (layoutRelationship == null) {
				throw new IllegalArgumentException("The arguments can not be null!"); //$NON-NLS-1$
			}
			double weight = layoutRelationship.getWeight();
			weight = (weight <= 0 ? 0.1 : weight);
			String key1 = layoutRelationship.getSource().toString() + layoutRelationship.getDestination().toString();
			String key2 = layoutRelationship.getDestination().toString() + layoutRelationship.getSource().toString();
			String[] keys = { key1, key2 };
			for (String key : keys) {
				Integer count = srcDestToNumRelsMap.get(key);
				Double avgWeight = srcDestToRelsAvgWeightMap.get(key);
				if (count == null) {
					count = Integer.valueOf(1);
					avgWeight = Double.valueOf(weight);
				} else {
					int newCount = count.intValue() + 1;
					double newAverage = (avgWeight.doubleValue() * count.doubleValue() + weight) / newCount;
					avgWeight = Double.valueOf(newAverage);
					count = Integer.valueOf(newCount);
				}
				srcDestToNumRelsMap.put(key, count);
				srcDestToRelsAvgWeightMap.put(key, avgWeight);
			}
		}

		private void preCompute(InternalNode[] entitiesToLayout) {
			// count number of relationships between all nodes and the average
			// weight between them
			srcDestToNumRels = new int[entitiesToLayout.length][entitiesToLayout.length];
			srcDestToRelsAvgWeight = new double[entitiesToLayout.length][entitiesToLayout.length];

			for (int i = 0; i < entitiesToLayout.length - 1; i++) {
				InternalNode layoutEntity1 = entitiesToLayout[i];
				for (int j = i + 1; j < entitiesToLayout.length; j++) {
					InternalNode layoutEntity2 = entitiesToLayout[j];
					srcDestToNumRels[i][j] = numRelations(layoutEntity1, layoutEntity2);
					srcDestToNumRels[i][j] += numRelations(layoutEntity2, layoutEntity1);
					srcDestToRelsAvgWeight[i][j] = avgWeight(layoutEntity1, layoutEntity2);
				}
			}

			if (sprRandom) {
				placeRandomly(entitiesToLayout); // put vertices in random places
			} else {
				convertToUnitCoordinates(entitiesToLayout);
			}

			iteration = 1;
			largestMovement = Double.MAX_VALUE;
		}

		// TODO: This is a complete Clone! (and not in a good way)
		@Deprecated
		protected DisplayIndependentRectangle getLayoutBoundsTemp(InternalNode[] entitiesToLayout,
				boolean includeNodeSize) {
			double rightSide = Double.MIN_VALUE;
			double bottomSide = Double.MIN_VALUE;
			double leftSide = Double.MAX_VALUE;
			double topSide = Double.MAX_VALUE;
			for (int i = 0; i < entitiesToLayout.length; i++) {
				double x = tempLocationsX[i];
				double y = tempLocationsY[i];

				leftSide = Math.min(x, leftSide);
				topSide = Math.min(y, topSide);
				rightSide = Math.max(x, rightSide);
				bottomSide = Math.max(y, bottomSide);

			}
			return new DisplayIndependentRectangle(leftSide, topSide, rightSide - leftSide, bottomSide - topSide);
		}

		@Deprecated
		protected void convertNodePositionsBack(int i, InternalNode entityToConvert, double px, double py,
				double screenWidth, double screenHeight, DisplayIndependentRectangle layoutBounds) {

			// If the node selected is outside the screen, map it to the boarder
			if (px > screenWidth) {
				px = screenWidth;
			}
			if (py > screenHeight) {
				py = screenHeight;
			}

			if (px < 0) {
				px = 1;
			}
			if (py < 0) {
				py = 1;
			}

			double x = (px / screenWidth) * layoutBounds.width + layoutBounds.x;
			double y = (py / screenHeight) * layoutBounds.height + layoutBounds.y;

			tempLocationsX[i] = x;
			tempLocationsY[i] = y;
			// setTempLocation(entityToConvert, new DisplayIndependentPoint(x, y));

			if (entityToConvert.getInternalX() < 0) {
				// System.out.println("We have nodes less than 0 here!");
			}

		}

		private void checkPreferredLocation(InternalNode[] entitiesToLayout, DisplayIndependentRectangle realBounds) {
			// use 10% for the border - 5% on each side
			double borderWidth = Math.min(realBounds.width, realBounds.height) / 10.0;
			DisplayIndependentRectangle screenBounds = new DisplayIndependentRectangle(realBounds.x + borderWidth / 2.0,
					realBounds.y + borderWidth / 2.0, realBounds.width - borderWidth, realBounds.height - borderWidth);

			DisplayIndependentRectangle layoutBounds = getLayoutBoundsTemp(entitiesToLayout, false);
			for (int i = 0; i < entitiesToLayout.length; i++) {
				InternalNode layoutEntity = entitiesToLayout[i];
				if (layoutEntity.hasPreferredLocation()) {
					convertNodePositionsBack(i, layoutEntity, layoutEntity.getPreferredX(), layoutEntity.getPreferredY(),
							screenBounds.width, screenBounds.height, layoutBounds);
				}
			}
		}

		/**
		 * Scales the current iteration counter based on how long the algorithm has been
		 * running for. You can set the MaxTime in maxTimeMS!
		 */
		private void setSprIterationsBasedOnTime() {
			if (maxTimeMS <= 0) {
				return;
			}

			long currentTime = date.getTime();
			double fractionComplete = (double) (currentTime - startTime) / ((double) maxTimeMS);
			int currentIteration = (int) (fractionComplete * sprIterations);
			if (currentIteration > iteration) {
				iteration = currentIteration;
			}

		}

		@Override
		@Deprecated
		protected boolean performAnotherNonContinuousIteration() {
			setSprIterationsBasedOnTime();
			return iteration <= sprIterations && largestMovement >= sprMove;
		}

		@Override
		@Deprecated
		protected int getCurrentLayoutStep() {
			return iteration;
		}

		@Override
		@Deprecated
		protected int getTotalNumberOfLayoutSteps() {
			return sprIterations;
		}

		@Override
		@Deprecated
		protected void computeOneIteration(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
				double x, double y, double width, double height) {
			if (bounds == null) {
				bounds = new DisplayIndependentRectangle(x, y, width, height);
			}
			checkPreferredLocation(entitiesToLayout, bounds);
			computeForces(entitiesToLayout);
			largestMovement = Double.MAX_VALUE;
			computePositions(entitiesToLayout);

			for (int i = 0; i < entitiesToLayout.length; i++) {
				InternalNode layoutEntity = entitiesToLayout[i];
				layoutEntity.setInternalLocation(tempLocationsX[i], tempLocationsY[i]);
			}

			defaultFitWithinBounds(entitiesToLayout, bounds);

			iteration++;
		}

		/**
		 * Puts vertices in random places, all between (0,0) and (1,1).
		 */
		@Deprecated
		public void placeRandomly(InternalNode[] entitiesToLayout) {
			// If only one node in the data repository, put it in the middle
			if (entitiesToLayout.length == 1) {
				// If only one node in the data repository, put it in the middle
				tempLocationsX[0] = 0.5;
				tempLocationsY[0] = 0.5;
			} else {
				for (int i = 0; i < entitiesToLayout.length; i++) {
					if (i == 0) {
						tempLocationsX[i] = 0.0;
						tempLocationsY[i] = 0.0;
					} else if (i == 1) {
						tempLocationsX[i] = 1.0;
						tempLocationsY[i] = 1.0;
					} else {
						tempLocationsX[i] = Math.random();
						tempLocationsY[i] = Math.random();
					}
				}
			}
		}

		// /////////////////////////////////////////////////////////////////
		// /// Protected Methods /////
		// /////////////////////////////////////////////////////////////////

		/**
		 * Computes the force for each node in this SpringLayoutAlgorithm. The computed
		 * force will be stored in the data repository
		 */
		@Deprecated
		protected void computeForces(InternalNode[] entitiesToLayout) {

			// initialize all forces to zero
			for (int i = 0; i < entitiesToLayout.length; i++) {
				forcesX[i] = 0.0;
				forcesY[i] = 0.0;
			}

			// TODO: Again really really slow!

			for (int i = 0; i < entitiesToLayout.length - 1; i++) {
				InternalNode sourceEntity = entitiesToLayout[i];

				double srcLocationX = tempLocationsX[i];
				double srcLocationY = tempLocationsY[i];
				double fx = forcesX[i]; // force in x direction
				double fy = forcesY[i]; // force in y direction

				for (int j = i + 1; j < entitiesToLayout.length; j++) {
					InternalNode destinationEntity = entitiesToLayout[j];

					if (!destinationEntity.equals(sourceEntity)) {
						double destLocationX = tempLocationsX[j];
						double destLocationY = tempLocationsY[j];
						double dx = srcLocationX - destLocationX;
						double dy = srcLocationY - destLocationY;
						double distance = Math.sqrt(dx * dx + dy * dy);
						double distance_sq = distance * distance;
						// make sure distance and distance squared not too small
						distance = Math.max(MIN_DISTANCE, distance);

						// If there are relationships between srcObj and destObj
						// then decrease force on srcObj (a pull) in direction of destObj
						// If no relation between srcObj and destObj then increase
						// force on srcObj (a push) from direction of destObj.
						int numRels = srcDestToNumRels[i][j];
						double avgWeight = srcDestToRelsAvgWeight[i][j];
						if (numRels > 0) {
							// nodes are pulled towards each other
							double f = sprStrain * Math.log(distance / sprLength) * numRels * avgWeight;

							fx = fx - (f * dx / distance);
							fy = fy - (f * dy / distance);

						} else {
							// nodes are repelled from each other
							// double f = Math.min(100, sprGravitation / (distance*distance));
							double f = sprGravitation / (distance_sq);
							fx = fx + (f * dx / distance);
							fy = fy + (f * dy / distance);
						}

						// According to Newton, "for every action, there is an equal
						// and opposite reaction."
						// so give the dest an opposite force
						forcesX[j] = forcesX[j] - fx;
						forcesY[j] = forcesY[j] - fy;
					}
				}

				/*
				 * //make sure forces aren't too big if (fx > 0 ) fx = Math.min(fx, 10*sprMove);
				 * else fx = Math.max(fx, -10*sprMove); if (fy > 0) fy = Math.min(fy,
				 * 10*sprMove); else fy = Math.max(fy, -10*sprMove);
				 */
				forcesX[i] = fx;
				forcesY[i] = fy;
				// Remove the src object from the list of destinations since
				// we've already calculated the force from it on all other
				// objects.
				// dests.remove(srcObj);

			}
		}

		/**
		 * Computes the position for each node in this SpringLayoutAlgorithm. The
		 * computed position will be stored in the data repository. position = position
		 * + sprMove * force
		 */
		@Deprecated
		protected void computePositions(InternalNode[] entitiesToLayout) {
			for (int i = 0; i < entitiesToLayout.length; i++) {
				if (!anchors[i] || entitiesToLayout[i].hasPreferredLocation()) {
					double oldX = tempLocationsX[i];
					double oldY = tempLocationsY[i];
					double deltaX = sprMove * forcesX[i];
					double deltaY = sprMove * forcesY[i];

					// constrain movement, so that nodes don't shoot way off to the edge
					double maxMovement = 0.2d * sprMove;
					if (deltaX >= 0) {
						deltaX = Math.min(deltaX, maxMovement);
					} else {
						deltaX = Math.max(deltaX, -maxMovement);
					}
					if (deltaY >= 0) {
						deltaY = Math.min(deltaY, maxMovement);
					} else {
						deltaY = Math.max(deltaY, -maxMovement);
					}

					largestMovement = Math.max(largestMovement, Math.abs(deltaX));
					largestMovement = Math.max(largestMovement, Math.abs(deltaY));

					double newX = oldX + deltaX;
					double newY = oldY + deltaY;
					tempLocationsX[i] = newX;
					tempLocationsY[i] = newY;
				}

			}

		}

		/**
		 * Converts the position for each node in this SpringLayoutAlgorithm to unit
		 * coordinates in double precision. The computed positions will be still stored
		 * in the data repository.
		 */
		@Deprecated
		protected void convertToUnitCoordinates(InternalNode[] entitiesToLayout) {
			double minX = Double.MAX_VALUE;
			double maxX = Double.MIN_VALUE;
			double minY = Double.MAX_VALUE;
			double maxY = Double.MIN_VALUE;
			for (InternalNode layoutEntity : entitiesToLayout) {
				minX = Math.min(minX, layoutEntity.getInternalX());
				minY = Math.min(minY, layoutEntity.getInternalY());
				maxX = Math.max(maxX, layoutEntity.getInternalX());
				maxY = Math.max(maxY, layoutEntity.getInternalY());
			}

			double spanX = maxX - minX;
			double spanY = maxY - minY;
			double maxSpan = Math.max(spanX, spanY);

			if (maxSpan > EPSILON) {
				for (int i = 0; i < entitiesToLayout.length; i++) {
					InternalNode layoutEntity = entitiesToLayout[i];
					double x = (layoutEntity.getInternalX() - minX) / spanX;
					double y = (layoutEntity.getInternalY() - minY) / spanY;
					tempLocationsX[i] = x;
					tempLocationsY[i] = y;
				}
			} else {
				placeRandomly(entitiesToLayout);
			}
		}

		/**
		 * Examines the number of specified relation between the <code>src</code> and
		 * the <code>dest</code> that exist in this SpringLayoutAlgorithm's relation
		 * repository.
		 *
		 * @param src  The source part of the relaton to be examined.
		 * @param dest The destination part of the relation to be examined.
		 * @return The number of relations between src and dest.
		 */
		private int numRelations(Object src, Object dest) {
			String key = src.toString() + dest.toString();
			Integer count = srcDestToNumRelsMap.get(key);
			return (count == null) ? 0 : count.intValue();
		}

		/**
		 * Returns the average weight between a src and dest object.
		 *
		 * @param src
		 * @param dest
		 * @return The average weight between the given src and dest nodes
		 */
		private double avgWeight(Object src, Object dest) {
			String key = src.toString() + dest.toString();
			Double avgWeight = srcDestToRelsAvgWeightMap.get(key);
			return (avgWeight == null) ? 1 : avgWeight.doubleValue();
		}

		@Override
		@Deprecated
		protected boolean isValidConfiguration(boolean asynchronous, boolean continueous) {
			return asynchronous || !continueous;
		}

	}

	/**
	 * The default value for the spring layout number of interations.
	 */
	public static final int DEFAULT_SPRING_ITERATIONS = 1000;

	/**
	 * the default value for the time algorithm runs.
	 */
	public static final long MAX_SPRING_TIME = 10000;

	/**
	 * The default value for positioning nodes randomly.
	 */
	public static final boolean DEFAULT_SPRING_RANDOM = true;

	/**
	 * The default value for the spring layout move-control.
	 */
	public static final double DEFAULT_SPRING_MOVE = 1.0f;

	/**
	 * The default value for the spring layout strain-control.
	 */
	public static final double DEFAULT_SPRING_STRAIN = 1.0f;

	/**
	 * The default value for the spring layout length-control.
	 */
	public static final double DEFAULT_SPRING_LENGTH = 3.0f;

	/**
	 * The default value for the spring layout gravitation-control.
	 */
	public static final double DEFAULT_SPRING_GRAVITATION = 2.0f;

	/**
	 * Minimum distance considered between nodes
	 */
	protected static final double MIN_DISTANCE = 1.0d;

	/**
	 * An arbitrarily small value in mathematics.
	 */
	protected static final double EPSILON = 0.001d;

	/**
	 * The variable can be customized to set the number of iterations used.
	 */
	private int sprIterations = DEFAULT_SPRING_ITERATIONS;

	/**
	 * This variable can be customized to set the max number of MS the algorithm
	 * should run
	 */
	private long maxTimeMS = MAX_SPRING_TIME;

	/**
	 * The variable can be customized to set whether or not the spring layout nodes
	 * are positioned randomly before beginning iterations.
	 */
	private boolean sprRandom = DEFAULT_SPRING_RANDOM;

	/**
	 * The variable can be customized to set the spring layout move-control.
	 */
	private double sprMove = DEFAULT_SPRING_MOVE;

	/**
	 * The variable can be customized to set the spring layout strain-control.
	 */
	private double sprStrain = DEFAULT_SPRING_STRAIN;

	/**
	 * The variable can be customized to set the spring layout length-control.
	 */
	private double sprLength = DEFAULT_SPRING_LENGTH;

	/**
	 * The variable can be customized to set the spring layout gravitation-control.
	 */
	private double sprGravitation = DEFAULT_SPRING_GRAVITATION;

	/**
	 * Variable indicating whether the algorithm should resize elements.
	 */
	private boolean resize = false;

	private int iteration;

	private double[][] srcDestToSumOfWeights;

	private EntityLayout[] entities;

	private double[] forcesX, forcesY;

	private double[] locationsX, locationsY;

	private double[] sizeW, sizeH;

	private DisplayIndependentRectangle bounds;

	// private double boundsScale = 0.2;
	private double boundsScaleX = 0.2;
	private double boundsScaleY = 0.2;

	/**
	 * @since 2.0
	 */
	public boolean fitWithinBounds = true;

	class SpringLayoutListener implements LayoutListener {

		@Override
		public boolean nodeMoved(LayoutContext context, NodeLayout node) {
			// TODO Auto-generated method stub
			for (int i = 0; i < entities.length; i++) {
				if (entities[i] == node) {
					locationsX[i] = entities[i].getLocation().x;
					locationsY[i] = entities[i].getLocation().y;

				}

			}
			return false;
		}

		@Override
		public boolean nodeResized(LayoutContext context, NodeLayout node) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean subgraphMoved(LayoutContext context, SubgraphLayout subgraph) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean subgraphResized(LayoutContext context, SubgraphLayout subgraph) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	/**
	 * @deprecated Since Zest 2.0, use {@link #SpringLayoutAlgorithm()}.
	 */
	@Deprecated
	public SpringLayoutAlgorithm(int style) {
		this();
		setResizing(style != LayoutStyles.NO_LAYOUT_NODE_RESIZING);
	}

	public SpringLayoutAlgorithm() {
	}

	@Override
	public void applyLayout(boolean clean) {
		initLayout();
		if (!clean) {
			return;
		}
		while (performAnotherNonContinuousIteration()) {
			computeOneIteration();
		}
		saveLocations();
		if (resize) {
			AlgorithmHelper.maximizeSizes(entities);
		}

		if (fitWithinBounds) {
			DisplayIndependentRectangle bounds2 = new DisplayIndependentRectangle(bounds);
			int insets = 4;
			bounds2.x += insets;
			bounds2.y += insets;
			bounds2.width -= 2 * insets;
			bounds2.height -= 2 * insets;
			AlgorithmHelper.fitWithinBounds(entities, bounds2, resize);
		}

	}

	@Override
	public void setLayoutContext(LayoutContext context) {
		super.setLayoutContext(context);
		this.context.addLayoutListener(new SpringLayoutListener());
		initLayout();
	}

	/**
	 * @since 2.0
	 */
	public void performNIteration(int n) {
		if (iteration == 0) {
			entities = context.getEntities();
			loadLocations();
			initLayout();
		}
		bounds = context.getBounds();
		for (int i = 0; i < n; i++) {
			computeOneIteration();
			saveLocations();
		}
		context.flushChanges(false);
	}

	/**
	 * @since 2.0
	 */
	public void performOneIteration() {
		if (iteration == 0) {
			entities = context.getEntities();
			loadLocations();
			initLayout();
		}
		bounds = context.getBounds();
		computeOneIteration();
		saveLocations();
		context.flushChanges(false);
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

	/**
	 * Sets the spring layout move-control.
	 *
	 * @param move The move-control value.
	 */
	public void setSpringMove(double move) {
		sprMove = move;
	}

	/**
	 * Returns the move-control value of this SpringLayoutAlgorithm in double
	 * presion.
	 *
	 * @return The move-control value.
	 */
	public double getSpringMove() {
		return sprMove;
	}

	/**
	 * Sets the spring layout strain-control.
	 *
	 * @param strain The strain-control value.
	 */
	public void setSpringStrain(double strain) {
		sprStrain = strain;
	}

	/**
	 * Returns the strain-control value of this SpringLayoutAlgorithm in double
	 * presion.
	 *
	 * @return The strain-control value.
	 */
	public double getSpringStrain() {
		return sprStrain;
	}

	/**
	 * Sets the spring layout length-control.
	 *
	 * @param length The length-control value.
	 */
	public void setSpringLength(double length) {
		sprLength = length;
	}

	/**
	 * Gets the max time this algorithm will run for
	 */
	public long getSpringTimeout() {
		return maxTimeMS;
	}

	/**
	 * Sets the spring timeout
	 *
	 * @param timeout
	 */
	public void setSpringTimeout(long timeout) {
		maxTimeMS = timeout;
	}

	/**
	 * Returns the length-control value of this SpringLayoutAlgorithm in double
	 * presion.
	 *
	 * @return The length-control value.
	 */
	public double getSpringLength() {
		return sprLength;
	}

	/**
	 * Sets the spring layout gravitation-control.
	 *
	 * @param gravitation The gravitation-control value.
	 */
	public void setSpringGravitation(double gravitation) {
		sprGravitation = gravitation;
	}

	/**
	 * Returns the gravitation-control value of this SpringLayoutAlgorithm in double
	 * presion.
	 *
	 * @return The gravitation-control value.
	 */
	public double getSpringGravitation() {
		return sprGravitation;
	}

	/**
	 * Sets the number of iterations to be used.
	 *
	 * @param iterations The number of iterations.
	 */
	public void setIterations(int iterations) {
		sprIterations = iterations;
	}

	/**
	 * Returns the number of iterations to be used.
	 *
	 * @return The number of iterations.
	 */
	public int getIterations() {
		return sprIterations;
	}

	/**
	 * Sets whether or not this SpringLayoutAlgorithm will layout the nodes randomly
	 * before beginning iterations.
	 *
	 * @param random The random placement value.
	 */
	public void setRandom(boolean random) {
		sprRandom = random;
	}

	/**
	 * Returns whether or not this SpringLayoutAlgorithm will layout the nodes
	 * randomly before beginning iterations.
	 */
	public boolean getRandom() {
		return sprRandom;
	}

	private long startTime = 0;

	private void initLayout() {
		entities = context.getEntities();
		bounds = context.getBounds();
		loadLocations();

		srcDestToSumOfWeights = new double[entities.length][entities.length];
		Map<EntityLayout, Integer> entityToPosition = new HashMap<>();
		for (int i = 0; i < entities.length; i++) {
			entityToPosition.put(entities[i], Integer.valueOf(i));
		}

		ConnectionLayout[] connections = context.getConnections();
		for (ConnectionLayout connection : connections) {
			Integer source = entityToPosition.get(getEntity(connection.getSource()));
			Integer target = entityToPosition.get(getEntity(connection.getTarget()));
			if (source == null || target == null) {
				continue;
			}
			double weight = connection.getWeight();
			weight = (weight <= 0 ? 0.1 : weight);
			srcDestToSumOfWeights[source.intValue()][target.intValue()] += weight;
			srcDestToSumOfWeights[target.intValue()][source.intValue()] += weight;
		}

		if (sprRandom) {
			placeRandomly(); // put vertices in random places
		}

		iteration = 1;

		startTime = System.currentTimeMillis();
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

	private void loadLocations() {
		if (locationsX == null || locationsX.length != entities.length) {
			int length = entities.length;
			locationsX = new double[length];
			locationsY = new double[length];
			sizeW = new double[length];
			sizeH = new double[length];
			forcesX = new double[length];
			forcesY = new double[length];
		}
		for (int i = 0; i < entities.length; i++) {
			DisplayIndependentPoint location = entities[i].getLocation();
			locationsX[i] = location.x;
			locationsY[i] = location.y;
			DisplayIndependentDimension size = entities[i].getSize();
			sizeW[i] = size.width;
			sizeH[i] = size.height;
		}
	}

	private void saveLocations() {
		if (entities == null) {
			return;
		}
		for (int i = 0; i < entities.length; i++) {
			entities[i].setLocation(locationsX[i], locationsY[i]);
		}
	}

	/**
	 * Scales the current iteration counter based on how long the algorithm has been
	 * running for. You can set the MaxTime in maxTimeMS!
	 */
	private void setSprIterationsBasedOnTime() {
		if (maxTimeMS <= 0) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		double fractionComplete = (double) (currentTime - startTime) / ((double) maxTimeMS);
		int currentIteration = (int) (fractionComplete * sprIterations);
		if (currentIteration > iteration) {
			iteration = currentIteration;
		}

	}

	protected boolean performAnotherNonContinuousIteration() {
		setSprIterationsBasedOnTime();
		return (iteration <= sprIterations);
	}

	protected int getCurrentLayoutStep() {
		return iteration;
	}

	protected int getTotalNumberOfLayoutSteps() {
		return sprIterations;
	}

	/**
	 * @since 2.0
	 */
	protected void computeOneIteration() {
		computeForces();
		computePositions();
		DisplayIndependentRectangle currentBounds = getLayoutBounds();
		improveBoundScaleX(currentBounds);
		improveBoundScaleY(currentBounds);
		moveToCenter(currentBounds);
		iteration++;
	}

	/**
	 * Puts vertices in random places, all between (0,0) and (1,1).
	 *
	 * @since 2.0
	 */
	public void placeRandomly() {
		if (locationsX.length == 0) {
			return;
		}
		// If only one node in the data repository, put it in the middle
		if (locationsX.length == 1) {
			// If only one node in the data repository, put it in the middle
			locationsX[0] = bounds.x + 0.5 * bounds.width;
			locationsY[0] = bounds.y + 0.5 * bounds.height;
		} else {
			locationsX[0] = bounds.x;
			locationsY[0] = bounds.y;
			locationsX[1] = bounds.x + bounds.width;
			locationsY[1] = bounds.y + bounds.height;
			for (int i = 2; i < locationsX.length; i++) {
				locationsX[i] = bounds.x + Math.random() * bounds.width;
				locationsY[i] = bounds.y + Math.random() * bounds.height;
			}
		}
	}

	// /////////////////////////////////////////////////////////////////
	// /// Protected Methods /////
	// /////////////////////////////////////////////////////////////////

	/**
	 * Computes the force for each node in this SpringLayoutAlgorithm. The computed
	 * force will be stored in the data repository
	 *
	 * @since 2.0
	 */
	protected void computeForces() {

		double forcesX[][] = new double[2][this.forcesX.length];
		double forcesY[][] = new double[2][this.forcesX.length];
		double locationsX[] = new double[this.forcesX.length];
		double locationsY[] = new double[this.forcesX.length];

		// // initialize all forces to zero
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < this.forcesX.length; i++) {
				forcesX[j][i] = 0;
				forcesY[j][i] = 0;
				locationsX[i] = this.locationsX[i];
				locationsY[i] = this.locationsY[i];
			}
		}
		// TODO: Again really really slow!

		for (int k = 0; k < 2; k++) {
			for (int i = 0; i < this.locationsX.length; i++) {

				for (int j = i + 1; j < locationsX.length; j++) {
					double dx = (locationsX[i] - locationsX[j]) / bounds.width / boundsScaleX;
					double dy = (locationsY[i] - locationsY[j]) / bounds.height / boundsScaleY;
					double distance_sq = dx * dx + dy * dy;
					// make sure distance and distance squared not too small
					distance_sq = Math.max(MIN_DISTANCE * MIN_DISTANCE, distance_sq);
					double distance = Math.sqrt(distance_sq);

					// If there are relationships between srcObj and destObj
					// then decrease force on srcObj (a pull) in direction of
					// destObj
					// If no relation between srcObj and destObj then increase
					// force on srcObj (a push) from direction of destObj.
					double sumOfWeights = srcDestToSumOfWeights[i][j];

					double f;
					if (sumOfWeights > 0) {
						// nodes are pulled towards each other
						f = -sprStrain * Math.log(distance / sprLength) * sumOfWeights;
					} else {
						// nodes are repelled from each other
						f = sprGravitation / (distance_sq);
					}
					double dfx = f * dx / distance;
					double dfy = f * dy / distance;

					forcesX[k][i] += dfx;
					forcesY[k][i] += dfy;

					forcesX[k][j] -= dfx;
					forcesY[k][j] -= dfy;
				}
			}

			for (int i = 0; i < entities.length; i++) {
				if (entities[i].isMovable()) {
					double deltaX = sprMove * forcesX[k][i];
					double deltaY = sprMove * forcesY[k][i];

					// constrain movement, so that nodes don't shoot way off to
					// the
					// edge
					double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
					double maxMovement = 0.2d * sprMove;
					if (dist > maxMovement) {
						deltaX *= maxMovement / dist;
						deltaY *= maxMovement / dist;
					}

					locationsX[i] += deltaX * bounds.width * boundsScaleX;
					locationsY[i] += deltaY * bounds.height * boundsScaleY;
				}
			}

		}
		// // initialize all forces to zero
		for (int i = 0; i < this.entities.length; i++) {
			if (forcesX[0][i] * forcesX[1][i] < 0) {
				this.forcesX[i] = 0;
				// } else if ( this.locationsX[i] < 0 ) {
				// this.forcesX[i] = forcesX[1][i] / 10;
				// } else if ( this.locationsX[i] > boundsScale * bounds.width)
				// {
				// this.forcesX[i] = forcesX[1][i] / 10;
			} else {
				this.forcesX[i] = forcesX[1][i];
			}

			if (forcesY[0][i] * forcesY[1][i] < 0) {
				this.forcesY[i] = 0;
				// } else if ( this.locationsY[i] < 0 ) {
				// this.forcesY[i] = forcesY[1][i] / 10;
				// } else if ( this.locationsY[i] > boundsScale * bounds.height)
				// {
				// this.forcesY[i] = forcesY[1][i] / 10;
			} else {
				this.forcesY[i] = forcesY[1][i];
			}

		}

	}

	/**
	 * Computes the position for each node in this SpringLayoutAlgorithm. The
	 * computed position will be stored in the data repository. position = position
	 * + sprMove * force
	 *
	 * @since 2.0
	 */
	protected void computePositions() {
		for (int i = 0; i < entities.length; i++) {
			if (entities[i].isMovable()) {
				double deltaX = sprMove * forcesX[i];
				double deltaY = sprMove * forcesY[i];

				// constrain movement, so that nodes don't shoot way off to the
				// edge
				double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
				double maxMovement = 0.2d * sprMove;
				if (dist > maxMovement) {
					deltaX *= maxMovement / dist;
					deltaY *= maxMovement / dist;
				}

				locationsX[i] += deltaX * bounds.width * boundsScaleX;
				locationsY[i] += deltaY * bounds.height * boundsScaleY;
			}
		}
	}

	private DisplayIndependentRectangle getLayoutBounds() {
		double minX, maxX, minY, maxY;
		minX = minY = Double.POSITIVE_INFINITY;
		maxX = maxY = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < locationsX.length; i++) {
			maxX = Math.max(maxX, locationsX[i] + sizeW[i] / 2);
			minX = Math.min(minX, locationsX[i] - sizeW[i] / 2);
			maxY = Math.max(maxY, locationsY[i] + sizeH[i] / 2);
			minY = Math.min(minY, locationsY[i] - sizeH[i] / 2);
		}
		return new DisplayIndependentRectangle(minX, minY, maxX - minX, maxY - minY);
	}

	private void improveBoundScaleX(DisplayIndependentRectangle currentBounds) {
		double boundaryProportionX = currentBounds.width / bounds.width;
		// double boundaryProportion = Math.max(currentBounds.width /
		// bounds.width, currentBounds.height / bounds.height);

		// if (boundaryProportionX < 0.1)
		// boundsScaleX *= 2;
		// else if (boundaryProportionX < 0.5)
		// boundsScaleX *= 1.4;
		// else if (boundaryProportionX < 0.8)
		// boundsScaleX *= 1.1;
		if (boundaryProportionX < 0.9) {
			boundsScaleX *= 1.01;

			//
			// else if (boundaryProportionX > 1.8) {
			// if (boundsScaleX < 0.01)
			// return;
			// boundsScaleX /= 1.05;
		} else if (boundaryProportionX > 1) {
			if (boundsScaleX < 0.01) {
				return;
			}
			boundsScaleX /= 1.01;
		}
	}

	private void improveBoundScaleY(DisplayIndependentRectangle currentBounds) {
		double boundaryProportionY = currentBounds.height / bounds.height;
		// double boundaryProportion = Math.max(currentBounds.width /
		// bounds.width, currentBounds.height / bounds.height);

		// if (boundaryProportionY < 0.1)
		// boundsScaleY *= 2;
		// else if (boundaryProportionY < 0.5)
		// boundsScaleY *= 1.4;
		// else if (boundaryProportionY < 0.8)
		// boundsScaleY *= 1.1;
		if (boundaryProportionY < 0.9) {
			boundsScaleY *= 1.01;

			// else if (boundaryProportionY > 1.8) {
			// if (boundsScaleY < 0.01)
			// return;
			// boundsScaleY /= 1.05;
		} else if (boundaryProportionY > 1) {
			if (boundsScaleY < 0.01) {
				return;
			}
			boundsScaleY /= 1.01;
		}
	}

	// private void improveBoundsScale(DisplayIndependentRectangle
	// currentBounds) {
	// double boundaryProportionX = currentBounds.width / bounds.width;
	// double boundaryProportionY = currentBounds.height / bounds.height;
	// // double boundaryProportion = Math.max(currentBounds.width /
	// // bounds.width, currentBounds.height / bounds.height);
	//
	// if (boundaryProportion < 0.1)
	// boundsScale *= 2;
	// else if (boundaryProportion < 0.5)
	// boundsScale *= 1.4;
	// else if (boundaryProportion < 0.8)
	// boundsScale *= 1.1;
	// else if (boundaryProportion < 0.99)
	// boundsScale *= 1.05;
	//
	// else if (boundaryProportion > 1.8) {
	// if (boundsScale < 0.01)
	// return;
	// boundsScale /= 1.05;
	// }
	// else if (boundaryProportion > 1) {
	// if (boundsScale < 0.01)
	// return;
	// boundsScale /= 1.01;
	// }
	//
	// }

	private void moveToCenter(DisplayIndependentRectangle currentBounds) {
		double moveX = (currentBounds.x + currentBounds.width / 2) - (bounds.x + bounds.width / 2);
		double moveY = (currentBounds.y + currentBounds.height / 2) - (bounds.y + bounds.height / 2);
		for (int i = 0; i < locationsX.length; i++) {
			locationsX[i] -= moveX;
			locationsY[i] -= moveY;
		}
	}
}
