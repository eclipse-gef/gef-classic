/*******************************************************************************
 * Copyright 2005-2010, 2024 CHISEL Group, University of Victoria, Victoria, BC,
 *                           Canada, Johannes Kepler University Linz and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * Contributors: The Chisel Group, University of Victoria - initial API and implementation
 *               Alois Zoitl
 *               Mateusz Matela
 *               Ian Bull
 *******************************************************************************/
package org.eclipse.zest.layouts.algorithms;

import java.util.List;

import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;
import org.eclipse.zest.layouts.interfaces.EntityLayout;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

/**
 * This layout will take the given entities, apply a tree layout to them, and
 * then display the tree in a circular fashion with the roots in the center.
 *
 * @author Casey Best
 * @author Rob Lintern
 */
public class RadialLayoutAlgorithm extends AbstractLayoutAlgorithm {

	/**
	 * Collection of Zest 1.x methods. Used for backwards compatibility.
	 *
	 * @since 2.0
	 * @deprecated Use {@link RadialLayoutAlgorithm} instead. This class will be
	 *             removed in a future release.
	 * @noextend This class is not intended to be subclassed by clients.
	 * @noreference This class is not intended to be referenced by clients.
	 * @noinstantiate This class is not intended to be instantiated by clients.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Deprecated(since = "2.0", forRemoval = true)
	public static class Zest1 extends TreeLayoutAlgorithm.Zest1 {
		private static final double MAX_DEGREES = Math.PI * 2;
		private double startDegree;
		private double endDegree;
		private final TreeLayoutAlgorithm.Zest1 treeLayout;
		private List roots;

		/**
		 * Creates a radial layout with no style.
		 */
		public Zest1() {
			this(LayoutStyles.NONE);
		}

		// TODO: This is a really strange pattern. It extends tree layout and it
		// contains a tree layout ?
		public Zest1(int styles) {
			super(styles);
			treeLayout = new TreeLayoutAlgorithm.Zest1(styles);
			startDegree = 0;
			endDegree = MAX_DEGREES;
		}

		@Override
		public void setLayoutArea(double x, double y, double width, double height) {
			throw new RuntimeException("Operation not implemented"); //$NON-NLS-1$
		}

		@Override
		protected boolean isValidConfiguration(boolean asynchronous, boolean continueous) {
			return !continueous;
		}

		DisplayIndependentRectangle layoutBounds = null;

		@Override
		protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
				double x, double y, double width, double height) {
			layoutBounds = new DisplayIndependentRectangle(x, y, width, height);
			super.preLayoutAlgorithm(entitiesToLayout, relationshipsToConsider, x, y, width, height);
		}

		@Override
		protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout,
				InternalRelationship[] relationshipsToConsider) {
			roots = treeLayout.getRoots();
			computeRadialPositions(entitiesToLayout, layoutBounds);

			defaultFitWithinBounds(entitiesToLayout, layoutBounds);

			super.postLayoutAlgorithm(entitiesToLayout, relationshipsToConsider);

		}

		/**
		 * Set the range the radial layout will use when applyLayout is called. Both
		 * values must be in radians.
		 */
		public void setRangeToLayout(double startDegree, double endDegree) {
			this.startDegree = startDegree;
			this.endDegree = endDegree;
		}

		/**
		 * Take the tree and make it round. This is done by determining the location of
		 * each entity in terms of its percentage in the tree layout. Then apply that
		 * percentage to the radius and distance from the center.
		 */
		protected void computeRadialPositions(InternalNode[] entities, DisplayIndependentRectangle bounds2) {
			DisplayIndependentRectangle bounds = new DisplayIndependentRectangle(getLayoutBounds(entities, true));
			bounds.height = bounds2.height;
			bounds.y = bounds2.y;
			for (InternalNode entity : entities) {
				double percentTheta = (entity.getInternalX() - bounds.x) / bounds.width;
				double distance = (entity.getInternalY() - bounds.y) / bounds.height;
				double theta = startDegree + Math.abs(endDegree - startDegree) * percentTheta;
				double newX = distance * Math.cos(theta);
				double newY = distance * Math.sin(theta);

				entity.setInternalLocation(newX, newY);
			}
		}

		/**
		 * Find the bounds in which the nodes are located. Using the bounds against the
		 * real bounds of the screen, the nodes can proportionally be placed within the
		 * real bounds. The bounds can be determined either including the size of the
		 * nodes or not. If the size is not included, the bounds will only be guaranteed
		 * to include the center of each node.
		 */
		@Override
		protected DisplayIndependentRectangle getLayoutBounds(InternalNode[] entitiesToLayout, boolean includeNodeSize) {
			DisplayIndependentRectangle layoutBounds = super.getLayoutBounds(entitiesToLayout, includeNodeSize);
			DisplayIndependentPoint centerPoint = (roots != null) ? determineCenterPoint(roots)
					: new DisplayIndependentPoint(layoutBounds.x + layoutBounds.width / 2,
							layoutBounds.y + layoutBounds.height / 2);
			// The center entity is determined in applyLayout
			double maxDistanceX = Math.max(Math.abs(layoutBounds.x + layoutBounds.width - centerPoint.x),
					Math.abs(centerPoint.x - layoutBounds.x));
			double maxDistanceY = Math.max(Math.abs(layoutBounds.y + layoutBounds.height - centerPoint.y),
					Math.abs(centerPoint.y - layoutBounds.y));
			return new DisplayIndependentRectangle(centerPoint.x - maxDistanceX, centerPoint.y - maxDistanceY,
					maxDistanceX * 2, maxDistanceY * 2);
		}

		/**
		 * Find the center point between the roots
		 */
		private static DisplayIndependentPoint determineCenterPoint(List<InternalNode> roots) {
			double totalX = 0;
			double totalY = 0;
			for (InternalNode entity : roots) {
				totalX += entity.getInternalX();
				totalY += entity.getInternalY();
			}
			return new DisplayIndependentPoint(totalX / roots.size(), totalY / roots.size());
		}
	}

	private static final double MAX_DEGREES = Math.PI * 2;
	private double startDegree = 0;
	private double endDegree = MAX_DEGREES;

	private boolean resize = false;

	private final TreeLayoutAlgorithm treeLayout = new TreeLayoutAlgorithm();

	/**
	 * @deprecated Since Zest 2.0, use {@link #RadialLayoutAlgorithm()}.
	 */
	@Deprecated
	public RadialLayoutAlgorithm(int style) {
		this();
		setResizing(style != LayoutStyles.NO_LAYOUT_NODE_RESIZING);
	}

	public RadialLayoutAlgorithm() {
	}

	@Override
	public void applyLayout(boolean clean) {
		if (!clean) {
			return;
		}
		treeLayout.internalApplyLayout();
		EntityLayout[] entities = context.getEntities();
		DisplayIndependentRectangle bounds = context.getBounds();
		computeRadialPositions(entities, bounds);
		if (resize) {
			AlgorithmHelper.maximizeSizes(entities);
		}
		int insets = 4;
		bounds.x += insets;
		bounds.y += insets;
		bounds.width -= 2 * insets;
		bounds.height -= 2 * insets;
		AlgorithmHelper.fitWithinBounds(entities, bounds, resize);
	}

	private void computeRadialPositions(EntityLayout[] entities, DisplayIndependentRectangle bounds) {
		DisplayIndependentRectangle layoutBounds = AlgorithmHelper.getLayoutBounds(entities, false);
		layoutBounds.x = bounds.x;
		layoutBounds.width = bounds.width;
		for (EntityLayout element : entities) {
			DisplayIndependentPoint location = element.getLocation();
			double percenttheta = (location.x - layoutBounds.x) / layoutBounds.width;
			double distance = (location.y - layoutBounds.y) / layoutBounds.height;
			double theta = startDegree + Math.abs(endDegree - startDegree) * percenttheta;
			location.x = distance * Math.cos(theta);
			location.y = distance * Math.sin(theta);
			element.setLocation(location.x, location.y);
		}
	}

	@Override
	public void setLayoutContext(LayoutContext context) {
		super.setLayoutContext(context);
		treeLayout.setLayoutContext(context);
	}

	/**
	 * Set the range the radial layout will use when applyLayout is called. Both
	 * values must be in radians.
	 */
	public void setRangeToLayout(double startDegree, double endDegree) {
		this.startDegree = startDegree;
		this.endDegree = endDegree;
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
		treeLayout.setResizing(resize);
	}
}
