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

import java.util.List;

import org.eclipse.pde.api.tools.annotations.NoExtend;

import org.eclipse.draw2d.IFigure;

/**
 * The layout manager for {@link InlineFlow} figures.
 *
 * <P>
 * WARNING: This class is not intended to be subclassed by clients.
 *
 * @author hudsonr
 * @since 2.1
 */
@NoExtend
public class InlineFlowLayout extends FlowContainerLayout {

	/**
	 * Creates a new InlineFlowLayout with the given FlowFigure.
	 *
	 * @param flow The FlowFigure
	 */
	public InlineFlowLayout(FlowFigure flow) {
		super(flow);
	}

	/**
	 * Adds the given box as a line below the current line.
	 *
	 * @param box the box to add
	 */
	@Override
	public void addLine(CompositeBox box) {
		endLine();
		getContext().addLine(box);
	}

	/**
	 * @see FlowContainerLayout#createNewLine()
	 */
	@Override
	protected void createNewLine() {
		currentLine = new NestedLine(getFlowFigure());
		setupLine(currentLine);
	}

	/**
	 * @see FlowContext#endLine()
	 */
	@Override
	public void endLine() {
		flush();
		getContext().endLine();
	}

	/**
	 * @since 3.15
	 */
	@Override
	protected InlineFlow getFlowFigure() {
		return (InlineFlow) super.getFlowFigure();
	}

	/**
	 * @see FlowContainerLayout#flush()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void flush() {
		if (currentLine != null && currentLine.isOccupied()) {
			// We want to preserve the state when a linebox is being added
			boolean sameLine = getContext().getContinueOnSameLine();
			getContext().addToCurrentLine(currentLine);
			((List<FlowBox>) getFlowFigure().getFragments()).add(currentLine);
			currentLine = null;
			getContext().setContinueOnSameLine(sameLine);
		}
	}

	/**
	 * InlineFlowLayout gets this information from its context.
	 *
	 * @see FlowContext#getContinueOnSameLine()
	 */
	@Override
	public boolean getContinueOnSameLine() {
		return getContext().getContinueOnSameLine();
	}

	/**
	 * @see FlowContext#getWidthLookahead(FlowFigure, int[])
	 */
	@Override
	public void getWidthLookahead(FlowFigure child, int[] result) {
		List<? extends IFigure> children = getFlowFigure().getChildren();
		int index = -1;
		if (child != null) {
			index = children.indexOf(child);
		}

		for (int i = index + 1; i < children.size(); i++) {
			if (((FlowFigure) children.get(i)).addLeadingWordRequirements(result)) {
				return;
			}
		}

		getContext().getWidthLookahead(getFlowFigure(), result);
	}

	/**
	 * @see FlowContainerLayout#isCurrentLineOccupied()
	 */
	@Override
	public boolean isCurrentLineOccupied() {
		return (currentLine != null && !currentLine.getFragments().isEmpty()) || getContext().isCurrentLineOccupied();
	}

	/**
	 * Clears out all fragments prior to the call to layoutChildren().
	 */
	@Override
	public void preLayout() {
		getFlowFigure().getFragments().clear();
	}

	/**
	 * InlineFlow passes this information to its context.
	 *
	 * @see FlowContext#setContinueOnSameLine(boolean)
	 */
	@Override
	public void setContinueOnSameLine(boolean value) {
		getContext().setContinueOnSameLine(value);
	}

	/**
	 * Initializes the given LineBox. Called by createNewLine().
	 *
	 * @param line The LineBox to initialize.
	 */
	protected void setupLine(LineBox line) {
		line.setX(0);
		line.setRecommendedWidth(getContext().getRemainingLineWidth());
	}

}
