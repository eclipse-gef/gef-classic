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

import java.util.Arrays;

import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;
import org.eclipse.zest.layouts.interfaces.EntityLayout;

/**
 * @version 2.0
 * @author Ian Bull
 * @author Casey Best and Rob Lintern
 */
public class GridLayoutAlgorithm extends AbstractLayoutAlgorithm {

	/**
	 * Collection of Zest 1.x methods. Used for backwards compatibility.
	 *
	 * @since 2.0
	 * @deprecated Use {@link GridLayoutAlgorithm} instead. This class will be
	 *             removed in a future release.
	 * @noextend This class is not intended to be subclassed by clients.
	 * @noreference This class is not intended to be referenced by clients.
	 * @noinstantiate This class is not intended to be instantiated by clients.
	 */
	@SuppressWarnings("unchecked")
	@Deprecated(since = "2.0", forRemoval = true)
	public static class Zest1 extends AbstractLayoutAlgorithm.Zest1 {

		private static final double PADDING_PERCENTAGE = 0.95;

		@Deprecated
		protected int rowPadding = 0;

		@Override
		@Deprecated
		public void setLayoutArea(double x, double y, double width, double height) {
			throw new RuntimeException("Operation not implemented"); //$NON-NLS-1$
		}

		@Deprecated
		int rows, cols, numChildren;
		@Deprecated
		double colWidth, rowHeight, offsetX, offsetY;
		@Deprecated
		int totalProgress;
		@Deprecated
		double h, w;

		/**
		 * Initializes the grid layout.
		 *
		 * @param styles
		 * @see LayoutStyles
		 */
		@Deprecated
		public Zest1(int styles) {
			super(styles);
		}

		/**
		 * Inititalizes the grid layout with no style.
		 */
		@Deprecated
		public Zest1() {
			this(LayoutStyles.NONE);
		}

		@Override
		@Deprecated
		protected int getCurrentLayoutStep() {
			// TODO: This isn't right
			return 0;
		}

		@Override
		@Deprecated
		protected int getTotalNumberOfLayoutSteps() {
			return totalProgress;
		}

		/**
		 *
		 */
		@Override
		@Deprecated
		protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
				double x, double y, double width, double height) {

			// TODO: Filter unwanted entities and relationships
			// super.applyLayout (entitiesToLayout, relationshipsToConsider, boundsX,
			// boundsY, boundsWidth, boundsHeight);
			// now begin
			numChildren = entitiesToLayout.length;
			if (numChildren < 1) {
				return;
			}

			int[] colsAndRows = calculateNumberOfRowsAndCols(numChildren, x, y, width, height);
			cols = colsAndRows[0];
			rows = colsAndRows[1];

			totalProgress = rows + 2;
			fireProgressEvent(1, totalProgress);

			// sort the entities
			if (comparator != null) {
				Arrays.sort(entitiesToLayout, comparator);
			} else {
				Arrays.sort(entitiesToLayout);
			}
			fireProgressEvent(2, totalProgress);

			// Calculate row height and column width
			colWidth = width / cols;
			rowHeight = height / rows;

			// Calculate amount to scale children
			double[] nodeSize = calculateNodeSize(colWidth, rowHeight);
			w = nodeSize[0];
			h = nodeSize[1];
			offsetX = (colWidth - w) / 2.0; // half of the space between columns
			offsetY = (rowHeight - h) / 2.0; // half of the space between rows
		}

		/**
		 * Use this algorithm to layout the given entities, using the given
		 * relationships and bounds. The entities will be placed in the same order as
		 * they are passed in, unless a comparator is supplied.
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
		 * @throws RuntimeException Thrown if entitiesToLayout doesn't contain all of
		 *                          the endpoints for each relationship in
		 *                          relationshipsToConsider
		 */
		@Override
		@Deprecated
		protected synchronized void applyLayoutInternal(InternalNode[] entitiesToLayout,
				InternalRelationship[] relationshipsToConsider, double boundsX, double boundsY, double boundsWidth,
				double boundsHeight) {

			int index = 0;
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					if ((i * cols + j) < numChildren) {
						// find new position for child
						double xmove = boundsX + j * colWidth + offsetX;
						double ymove = boundsY + i * rowHeight + offsetY;
						InternalNode sn = entitiesToLayout[index++];
						sn.setInternalLocation(xmove, ymove);
						sn.setInternalSize(Math.max(w, MIN_ENTITY_SIZE), Math.max(h, MIN_ENTITY_SIZE));
					}
				}
				fireProgressEvent(2 + i, totalProgress);
			}
			updateLayoutLocations(entitiesToLayout);
			fireProgressEvent(totalProgress, totalProgress);
		}

		@Override
		@Deprecated
		protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout,
				InternalRelationship[] relationshipsToConsider) {

		}

		/**
		 * Calculates and returns an array containing the number of columns, followed by
		 * the number of rows
		 */
		@Deprecated
		protected int[] calculateNumberOfRowsAndCols(int numChildren, double boundX, double boundY, double boundWidth,
				double boundHeight) {
			if (getEntityAspectRatio() == 1.0) {
				return calculateNumberOfRowsAndCols_square(numChildren, boundX, boundY, boundWidth, boundHeight);
			} else {
				return calculateNumberOfRowsAndCols_rectangular(numChildren);
			}
		}

		@Deprecated
		@SuppressWarnings("static-method")
		protected int[] calculateNumberOfRowsAndCols_square(int numChildren, double boundX, double boundY,
				double boundWidth, double boundHeight) {
			int rows = Math.max(1, (int) Math.sqrt(numChildren * boundHeight / boundWidth));
			int cols = Math.max(1, (int) Math.sqrt(numChildren * boundWidth / boundHeight));

			// if space is taller than wide, adjust rows first
			if (boundWidth <= boundHeight) {
				// decrease number of rows and columns until just enough or not enough
				while (rows * cols > numChildren) {
					if (rows > 1) {
						rows--;
					}
					if (rows * cols > numChildren) {
						if (cols > 1) {
							cols--;
						}
					}
				}
				// increase number of rows and columns until just enough
				while (rows * cols < numChildren) {
					rows++;
					if (rows * cols < numChildren) {
						cols++;
					}
				}
			} else {
				// decrease number of rows and columns until just enough or not enough
				while (rows * cols > numChildren) {
					if (cols > 1) {
						cols--;
					}
					if (rows * cols > numChildren) {
						if (rows > 1) {
							rows--;
						}
					}
				}
				// increase number of rows and columns until just enough
				while (rows * cols < numChildren) {
					cols++;
					if (rows * cols < numChildren) {
						rows++;
					}
				}
			}
			int[] result = { cols, rows };
			return result;
		}

		@Deprecated
		@SuppressWarnings("static-method")
		protected int[] calculateNumberOfRowsAndCols_rectangular(int numChildren) {
			int rows = Math.max(1, (int) Math.ceil(Math.sqrt(numChildren)));
			int cols = Math.max(1, (int) Math.ceil(Math.sqrt(numChildren)));
			int[] result = { cols, rows };
			return result;
		}

		@Deprecated
		protected double[] calculateNodeSize(double colWidth, double rowHeight) {
			double childW = Math.max(MIN_ENTITY_SIZE, PADDING_PERCENTAGE * colWidth);
			double childH = Math.max(MIN_ENTITY_SIZE, PADDING_PERCENTAGE * (rowHeight - rowPadding));
			double whRatio = colWidth / rowHeight;
			if (whRatio < getEntityAspectRatio()) {
				childH = childW / getEntityAspectRatio();
			} else {
				childW = childH * getEntityAspectRatio();
			}
			double[] result = { childW, childH };
			return result;
		}

		/**
		 * Increases the padding between rows in the grid
		 *
		 * @param rowPadding Value will not be set if less than 0.
		 */
		@Deprecated
		public void setRowPadding(int rowPadding) {
			if (rowPadding < 0) {
				return;
			}
			this.rowPadding = rowPadding;
		}

		@Override
		@Deprecated
		protected boolean isValidConfiguration(boolean asynchronous, boolean continueous) {
			if (asynchronous && continueous) {
				return false;
			} else if (asynchronous && !continueous) {
				return true;
			} else if (!asynchronous && continueous) {
				return false;
			} else if (!asynchronous && !continueous) {
				return true;
			}

			return false;
		}

	}

	private static final double PADDING_PERCENTAGE = 0.95;
	private static final int MIN_ENTITY_SIZE = 5;

	/**
	 * @since 2.0
	 */
	protected double aspectRatio = 1.0;
	protected int rowPadding = 0;
	private boolean resize = false;
	/**
	 * @since 2.0
	 */
	protected int rows, cols, numChildren;
	/**
	 * @since 2.0
	 */
	protected double colWidth, rowHeight, offsetX, offsetY;
	/**
	 * @since 2.0
	 */
	protected double childrenHeight, childrenWidth;

	/**
	 * @deprecated Since Zest 2.0, use {@link #GridLayoutAlgorithm()}.
	 */
	@Deprecated
	public GridLayoutAlgorithm(int style) {
		this();
		setResizing(style != LayoutStyles.NO_LAYOUT_NODE_RESIZING);
	}

	public GridLayoutAlgorithm() {
	}

	@Override
	public void applyLayout(boolean clean) {
		if (!clean) {
			return;
		}
		DisplayIndependentRectangle bounds = context.getBounds();
		calculateGrid(bounds);
		applyLayoutInternal(context.getEntities(), bounds);
	}

	/**
	 * Calculates all the dimensions of grid that layout entities will be fit in.
	 * The following fields are set by this method: {@link #numChildren},
	 * {@link #rows}, {@link #cols}, {@link #colWidth}, {@link #rowHeight},
	 * {@link #offsetX}, {@link #offsetY}
	 *
	 * @param bounds
	 * @since 2.0
	 */
	protected void calculateGrid(DisplayIndependentRectangle bounds) {
		numChildren = context.getNodes().length;
		int[] result = calculateNumberOfRowsAndCols(numChildren, bounds.x, bounds.y, bounds.width, bounds.height);
		cols = result[0];
		rows = result[1];

		colWidth = bounds.width / cols;
		rowHeight = bounds.height / rows;

		double[] nodeSize = calculateNodeSize(colWidth, rowHeight);
		childrenWidth = nodeSize[0];
		childrenHeight = nodeSize[1];
		offsetX = (colWidth - childrenWidth) / 2.0; // half of the space between
													// columns
		offsetY = (rowHeight - childrenHeight) / 2.0; // half of the space
														// between rows
	}

	/**
	 * Use this algorithm to layout the given entities and bounds. The entities will
	 * be placed in the same order as they are passed in, unless a comparator is
	 * supplied.
	 *
	 * @param entitiesToLayout apply the algorithm to these entities
	 * @param bounds           the bounds in which the layout can place the
	 *                         entities.
	 * @since 2.0
	 */
	protected synchronized void applyLayoutInternal(EntityLayout[] entitiesToLayout,
			DisplayIndependentRectangle bounds) {

		int index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if ((i * cols + j) < numChildren) {
					EntityLayout node = entitiesToLayout[index];
					index++;
					if (resize && node.isResizable()) {
						node.setSize(Math.max(childrenWidth, MIN_ENTITY_SIZE),
								Math.max(childrenHeight, MIN_ENTITY_SIZE));
					}
					double xmove = bounds.x + j * colWidth + offsetX;
					double ymove = bounds.y + i * rowHeight + offsetY;
					if (node.isMovable()) {
						node.setLocation(xmove, ymove);
					}
				}
			}
		}
	}

	/**
	 * Calculates and returns an array containing the number of columns, followed by
	 * the number of rows
	 */
	protected int[] calculateNumberOfRowsAndCols(int numChildren, double boundX, double boundY, double boundWidth,
			double boundHeight) {
		if (aspectRatio == 1.0) {
			return calculateNumberOfRowsAndCols_square(numChildren, boundX, boundY, boundWidth, boundHeight);
		}
		return calculateNumberOfRowsAndCols_rectangular(numChildren);
	}

	@SuppressWarnings("static-method")
	protected int[] calculateNumberOfRowsAndCols_square(int numChildren, double boundX, double boundY,
			double boundWidth, double boundHeight) {
		int rows = Math.max(1, (int) Math.sqrt(numChildren * boundHeight / boundWidth));
		int cols = Math.max(1, (int) Math.sqrt(numChildren * boundWidth / boundHeight));

		// if space is taller than wide, adjust rows first
		if (boundWidth <= boundHeight) {
			// decrease number of rows and columns until just enough or not
			// enough
			while (rows * cols > numChildren) {
				if (rows > 1) {
					rows--;
				}
				if (rows * cols > numChildren) {
					if (cols > 1) {
						cols--;
					}
				}
			}
			// increase number of rows and columns until just enough
			while (rows * cols < numChildren) {
				rows++;
				if (rows * cols < numChildren) {
					cols++;
				}
			}
		} else {
			// decrease number of rows and columns until just enough or not
			// enough
			while (rows * cols > numChildren) {
				if (cols > 1) {
					cols--;
				}
				if (rows * cols > numChildren) {
					if (rows > 1) {
						rows--;
					}
				}
			}
			// increase number of rows and columns until just enough
			while (rows * cols < numChildren) {
				cols++;
				if (rows * cols < numChildren) {
					rows++;
				}
			}
		}
		return new int[] { cols, rows };
	}

	@SuppressWarnings("static-method")
	protected int[] calculateNumberOfRowsAndCols_rectangular(int numChildren) {
		int rows = Math.max(1, (int) Math.ceil(Math.sqrt(numChildren)));
		int cols = Math.max(1, (int) Math.ceil(Math.sqrt(numChildren)));
		return new int[] { cols, rows };
	}

	protected double[] calculateNodeSize(double colWidth, double rowHeight) {
		double childW = Math.max(MIN_ENTITY_SIZE, PADDING_PERCENTAGE * colWidth);
		double childH = Math.max(MIN_ENTITY_SIZE, PADDING_PERCENTAGE * (rowHeight - rowPadding));
		double whRatio = colWidth / rowHeight;
		if (whRatio < aspectRatio) {
			childH = childW / aspectRatio;
		} else {
			childW = childH * aspectRatio;
		}
		return new double[] { childW, childH };
	}

	/**
	 * Sets the padding between rows in the grid
	 *
	 * @param rowPadding padding - should be greater than or equal to 0
	 */
	public void setRowPadding(int rowPadding) {
		if (rowPadding >= 0) {
			this.rowPadding = rowPadding;
		}
	}

	/**
	 * Sets the preferred aspect ratio for layout entities. The default aspect ratio
	 * is 1.
	 *
	 * @param aspectRatio aspect ratio - should be greater than 0
	 * @since 2.0
	 */
	public void setAspectRatio(double aspectRatio) {
		if (aspectRatio > 0) {
			this.aspectRatio = aspectRatio;
		}
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

}
