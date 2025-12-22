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

import org.eclipse.pde.api.tools.annotations.NoExtend;

import org.eclipse.draw2d.IFigure;

/**
 * A layout for FlowFigures with children.
 *
 * <P>
 * WARNING: This class is not intended to be subclassed by clients.
 *
 * @author hudsonr
 * @since 2.1
 */
@NoExtend
public abstract class FlowContainerLayout extends FlowFigureLayout implements FlowContext {

	/**
	 * the current line
	 */
	LineBox currentLine;

	/**
	 * @see org.eclipse.draw2d.text.FlowFigureLayout#FlowFigureLayout(FlowFigure)
	 */
	protected FlowContainerLayout(FlowFigure flowFigure) {
		super(flowFigure);
	}

	/**
	 * Adds the given box the current line and clears the context's state.
	 *
	 * @see org.eclipse.draw2d.text.FlowContext#addToCurrentLine(FlowBox)
	 */
	@Override
	public void addToCurrentLine(FlowBox child) {
		getCurrentLine().add(child);
		setContinueOnSameLine(false);
	}

	/**
	 * Flush anything pending and free all temporary data used during layout.
	 */
	protected void cleanup() {
		currentLine = null;
	}

	/**
	 * Used by getCurrentLine().
	 */
	protected abstract void createNewLine();

	/**
	 * Called after {@link #layoutChildren()} when all children have been laid out.
	 * This method exists to flush the last line.
	 */
	protected abstract void flush();

	/**
	 * FlowBoxes shouldn't be added directly to the current line. Use
	 * {@link #addToCurrentLine(FlowBox)} for that.
	 *
	 * @see org.eclipse.draw2d.text.FlowContext#getCurrentLine()
	 */
	LineBox getCurrentLine() {
		if (currentLine == null) {
			createNewLine();
		}
		return currentLine;
	}

	/**
	 * @see FlowContext#getRemainingLineWidth()
	 */
	@Override
	public int getRemainingLineWidth() {
		return getCurrentLine().getAvailableWidth();
	}

	/**
	 * @see FlowContext#isCurrentLineOccupied()
	 */
	@Override
	public boolean isCurrentLineOccupied() {
		return currentLine != null && currentLine.isOccupied();
	}

	/**
	 * @see FlowFigureLayout#layout()
	 */
	@Override
	protected void layout() {
		preLayout();
		layoutChildren();
		flush();
		cleanup();
	}

	/**
	 * Layout all children.
	 */
	protected void layoutChildren() {
		getFlowFigure().getChildren().forEach(f -> {
			if (forceChildInvalidation(f)) {
				f.invalidate();
			}
			f.validate();
		});
	}

	@SuppressWarnings("static-method")
	boolean forceChildInvalidation(IFigure f) {
		return true;
	}

	/**
	 * Called before layoutChildren() to setup any necessary state.
	 */
	protected abstract void preLayout();

}
