/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
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
package org.eclipse.draw2d.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;

import org.eclipse.pde.api.tools.annotations.NoExtend;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A <code>FlowFigure</code> represented by multiple <code>LineBox</code>
 * fragments. An <code>InlineFlow</code>'s parent must be either a
 * {@link BlockFlow} or another InlineFlow.
 *
 * <P>
 * An InlineFlow may contain other InlineFlow figures.
 *
 * <P>
 * WARNING: This class is not intended to be subclassed by clients.
 *
 * @author Randy Hudson
 * @since 2.0
 */
@NoExtend
public class InlineFlow extends FlowFigure {

	List<FlowBox> fragments = new ArrayList<>(1);

	/**
	 * Iterates over the children to find the width before a line-break is
	 * encountered.
	 *
	 * @see org.eclipse.draw2d.text.FlowFigure#addLeadingWordRequirements(int[])
	 */
	@Override
	public boolean addLeadingWordRequirements(int[] width) {
		for (IFigure fig : getChildren()) {
			if (((FlowFigure) fig).addLeadingWordRequirements(width)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Extended to return false if the point is not also contained by at least one
	 * fragment.
	 *
	 * @return <code>true</code> if a fragment contains the given point
	 * @param x the relative x coordinate
	 * @param y the relative y coordinate
	 */
	@Override
	public boolean containsPoint(int x, int y) {
		if (super.containsPoint(x, y)) {
			for (FlowBox frag : getFragments()) {
				if (frag.containsPoint(x, y)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * @see FlowFigure#createDefaultFlowLayout()
	 */
	@Override
	protected FlowFigureLayout createDefaultFlowLayout() {
		return new InlineFlowLayout(this);
	}

	/**
	 * Returns the <code>FlowBox</code> fragments contained in this InlineFlow. The
	 * returned list should not be modified.
	 *
	 * @return The fragments
	 */
	public List<? extends FlowBox> getFragments() {
		return fragments;
	}

	/**
	 * Overridden to paint a {@link FlowBorder} if present, and draw selection. The
	 * border is painted first, followed by selection which is generally done in
	 * XOR, which still allows the border to be seen.
	 *
	 * @param graphics the graphics
	 */
	@Override
	protected void paintBorder(Graphics graphics) {
		if (getBorder() != null) {
			FlowBorder fb = (FlowBorder) getBorder();
			List<? extends FlowBox> frags = getFragments();
			Rectangle where = new Rectangle();
			int sides;
			for (int i = 0; i < frags.size(); i++) {
				FlowBox box = frags.get(i);

				where.x = box.getX();
				where.width = box.getWidth();
				where.y = -box.getAscentWithBorder();
				where.height = box.getDescentWithBorder() - where.y;
				where.y += box.getBaseline();
				sides = 0;
				if (i == 0) {
					sides = SWT.LEAD;
				}
				if (i == frags.size() - 1) {
					sides |= SWT.TRAIL;
				}
				fb.paint(this, graphics, where, sides);
			}
			graphics.restoreState();
		}
		if (selectionStart != -1) {
			paintSelection(graphics);
		}
	}

	/**
	 * Renders the XOR selection rectangles to the graphics.
	 *
	 * @param graphics the graphics to paint on
	 * @since 3.1
	 */
	protected void paintSelection(Graphics graphics) {
		graphics.restoreState();
		graphics.setXORMode(true);
		graphics.setBackgroundColor(ColorConstants.white);
		for (FlowBox box : getFragments()) {
			int top = box.getLineRoot().getVisibleTop();
			int bottom = box.getLineRoot().getVisibleBottom();
			graphics.fillRectangle(box.getX(), top, box.getWidth(), bottom - top);
		}
	}

	/**
	 * @see FlowFigure#postValidate()
	 */
	@Override
	public void postValidate() {
		int left = Integer.MAX_VALUE;
		int top = left;
		int right = Integer.MIN_VALUE;
		int bottom = right;

		for (FlowBox box : getFragments()) {
			left = Math.min(left, box.getX());
			right = Math.max(right, box.getX() + box.getWidth());
			top = Math.min(top, box.getLineRoot().getVisibleTop());
			bottom = Math.max(bottom, box.getLineRoot().getVisibleBottom());
		}

		setBounds(new Rectangle(left, top, right - left, bottom - top));
		repaint();
		getChildren().forEach(child -> ((FlowFigure) child).postValidate());
	}

	/**
	 * Overridden to assert that only {@link FlowBorder} is used. <code>null</code>
	 * is still a valid value as well.
	 *
	 * @param border <code>null</code> or a FlowBorder
	 */
	@Override
	public void setBorder(Border border) {
		if ((border != null) && !(border instanceof FlowBorder)) {
			throw new RuntimeException("Border must be an instance of FlowBorder"); //$NON-NLS-1$
		}
		super.setBorder(border);
	}

}
