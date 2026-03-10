/*******************************************************************************
 * Copyright (c) 2005, 2023 IBM Corporation and others.
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
package org.eclipse.draw2d.examples.tree;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.OrderedLayout;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author hudsonr Created on Apr 21, 2003
 */
public class TreeBranch extends Figure {

	class AnimatingLayer extends Layer {

		/**
		 * @see org.eclipse.draw2d.Figure#setBounds(org.eclipse.draw2d.geometry.Rectangle)
		 */
		@Override
		public void setBounds(Rectangle rect) {

			int x = bounds.x;
			int y = bounds.y;

			boolean resize = (rect.width != bounds.width) || (rect.height != bounds.height);
			boolean translate = (rect.x != x) || (rect.y != y);

			if (isVisible() && (resize || translate)) {
				erase();
			}
			if (translate) {
				int dx = rect.x - x;
				int dy = rect.y - y;
				primTranslate(dx, dy);
			}
			bounds.width = rect.width;
			bounds.height = rect.height;
			// if (resize) Layouts dont depend on size.
			// invalidate();
			if (resize || translate) {
				fireMoved();
				repaint();
			}
		}

		@SuppressWarnings("unchecked") // we only have treebranches in our layer
		@Override
		public List<TreeBranch> getChildren() {
			return (List<TreeBranch>) super.getChildren();
		}

	}

	public static final int STYLE_HANGING = 1;
	public static final int STYLE_NORMAL = 2;

	int aligment = PositionConstants.CENTER;

	/*
	 * A layer is being used simply because it is the only "transparent" figure in
	 * draw2d. See the implementation of Layer.containsPoint(...) for what is meant
	 * by "transparent". If a layer is not used, then overlapping branches will
	 * cause hit-test problems.
	 */
	private final AnimatingLayer contents = new AnimatingLayer();
	boolean expanded = true;

	private final IFigure node;
	private int style;

	public TreeBranch(IFigure title) {
		this(title, STYLE_NORMAL);
	}

	public TreeBranch(IFigure title, int style) {
		setStyle(style);
		if (title.getBorder() == null) {
			title.setBorder(new LineBorder(ColorConstants.gray, 2));
		}
		this.node = title;
		add(contents);
		add(title);
	}

	/**
	 * recursively set all nodes and sub-treebranch nodes to the same location. This
	 * gives the appearance of all nodes coming from the same place.
	 *
	 * @param bounds where to set
	 */
	public void animationReset(Rectangle bounds) {
		contents.setBounds(bounds);

		// Make the center of this node match the center of the given bounds
		Rectangle r = node.getBounds();
		int dx = (bounds.x + (bounds.width / 2)) - r.x - (r.width / 2);
		int dy = (bounds.y + (bounds.height / 2)) - r.y - (r.height / 2);
		node.translate(dx, dy);
		revalidate(); // Otherwise, this branch will not layout

		// Pass the location to all children
		contents.getChildren().forEach(child -> {
			child.setBounds(bounds);
			child.animationReset(bounds);
		});
	}

	public void collapse() {
		if (!expanded) {
			return;
		}

		IFigure root = this;
		Viewport port = null;
		while (root.getParent() != null) {
			if (root instanceof Viewport vp) {
				port = vp;
			}
			root = root.getParent();
		}
		Point viewportStart = port.getViewLocation();
		Point nodeStart = node.getBounds().getLocation();
		setExpanded(false);
		root.validate();

		setExpanded(true);
		animationReset(getNodeBounds());
		Animation.mark(getNode());
		Animation.captureLayout(getRoot());
		Animation.swap();
		Animation.trackLocation = nodeStart;

		root.validate();
		port.setViewLocation(viewportStart);
		while (Animation.step()) {
			getUpdateManager().performUpdate();
		}
		Animation.end();
		setExpanded(false);
	}

	/**
	 * @see org.eclipse.draw2d.Figure#containsPoint(int, int)
	 */
	@Override
	public boolean containsPoint(int x, int y) {
		return node.containsPoint(x, y) || contents.containsPoint(x, y);
	}

	public void expand() {
		if (expanded) {
			return;
		}
		setExpanded(true);
		animationReset(getNodeBounds());

		Animation.mark(getNode());
		Animation.captureLayout(getRoot());

		while (Animation.step()) {
			getUpdateManager().performUpdate();
		}
		Animation.end();
	}

	public int getAlignment() {
		return aligment;
	}

	protected AbstractBranchLayout getBranchLayout() {
		return (AbstractBranchLayout) getLayoutManager();
	}

	public void addBranch(TreeBranch branch) {
		contents.add(branch);
	}

	public IFigure getContentsPane() {
		return contents;
	}

	public List<TreeBranch> getSubtrees() {
		return contents.getChildren();
	}

	public int[] getContourLeft() {
		return getBranchLayout().getContourLeft();
	}

	public int[] getContourRight() {
		return getBranchLayout().getContourRight();
	}

	public int getDepth() {
		return getBranchLayout().getDepth();
	}

	/**
	 * @see org.eclipse.draw2d.Figure#getMinimumSize(int, int)
	 */
	@Override
	public Dimension getMinimumSize(int wHint, int hHint) {
		if (!Animation.PLAYBACK) {
			validate();
		}
		return super.getMinimumSize(wHint, hHint);
	}

	public IFigure getNode() {
		return node;
	}

	public Rectangle getNodeBounds() {
		return node.getBounds();
	}

	public int[] getPreferredRowHeights() {
		return getBranchLayout().getPreferredRowHeights();
	}

	/**
	 * @see org.eclipse.draw2d.Figure#getPreferredSize(int, int)
	 */
	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		if (!Animation.PLAYBACK) {
			validate();
		}
		return super.getPreferredSize(wHint, hHint);
	}

	public TreeRoot getRoot() {
		return ((TreeBranch) getParent().getParent()).getRoot();
	}

	public int getStyle() {
		return style;
	}

	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		if (isExpanded()) {
			getBranchLayout().paintLines(graphics);
			// if (getDepth() == 2)
			// graphics.drawRectangle(getBounds().getResized(-1, -1));
		}
	}

	public void setAlignment(int value) {
		aligment = value;
		revalidate();
	}

	/**
	 * @param b
	 */
	public void setExpanded(boolean b) {
		if (expanded == b) {
			return;
		}
		expanded = b;
		contents.setVisible(b);
		revalidate();
	}

	public void setRowHeights(int[] heights, int offset) {
		getBranchLayout().setRowHeights(heights, offset);
	}

	public void setStyle(int style) {
		if (this.style == style) {
			return;
		}
		this.style = style;
		switch (style) {
		case STYLE_HANGING -> setLayoutManager(new HangingLayout(this));
		default -> {
			setLayoutManager(new NormalLayout(this));
			contents.setLayoutManager(new TreeLayout());
		}
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString(0);
	}

	protected String toString(int level) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < level; i++) {
			builder.append("  "); //$NON-NLS-1$
		}
		builder.append(getChildren().get(1));
		builder.append("\n"); //$NON-NLS-1$
		final int childrenlevel = level + 1;
		contents.getChildren().forEach(child -> builder.append(child.toString(childrenlevel)));
		return builder.toString();
	}

	/**
	 * @see org.eclipse.draw2d.Figure#validate()
	 */
	@Override
	public void validate() {
		if (isValid()) {
			return;
		}
		if (style == STYLE_HANGING) {
			ToolbarLayout layout = new ToolbarLayout(!getRoot().isHorizontal()) {
				@Override
				public void layout(IFigure parent) {
					Animation.recordInitialState(parent);
					if (Animation.playbackState(parent)) {
						return;
					}

					super.layout(parent);
				}

			};
			layout.setMinorAlignment(OrderedLayout.ALIGN_TOPLEFT);
			layout.setStretchMinorAxis(false);
			contents.setLayoutManager(layout);
		}
		repaint();
		super.validate();
	}

}
