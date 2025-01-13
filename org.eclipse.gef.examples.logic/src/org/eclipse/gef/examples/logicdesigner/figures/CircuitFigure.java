/*******************************************************************************
 * Copyright (c) 2000, 2022 IBM Corporation and others.
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
package org.eclipse.gef.examples.logicdesigner.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.IScrollableFigure;
import org.eclipse.draw2d.PuristicScrollPane;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.handles.HandleBounds;

import org.eclipse.gef.examples.logicdesigner.model.Circuit;

public class CircuitFigure extends NodeFigure implements HandleBounds, IScrollableFigure {

	private final ScrollPane scrollpane;

	public CircuitFigure() {
		setBorder(new CircuitBorder());
		scrollpane = new PuristicScrollPane();
		IFigure pane = new FreeformLayer();
		pane.setLayoutManager(new FreeformLayout());
		setLayoutManager(new StackLayout());
		add(scrollpane);
		scrollpane.setViewport(new FreeformViewport());
		scrollpane.setContents(pane);

		createConnectionAnchors();
		setBackgroundColor(ColorConstants.listBackground);
		setOpaque(true);
	}

	protected void createConnectionAnchors() {
		FixedConnectionAnchor in, out;
		for (int i = 0; i < 8; i++) {
			in = new FixedConnectionAnchor(this);
			out = new FixedConnectionAnchor(this);
			if (i > 3) {
				in.topDown = false;
				in.offsetV = 5;
				out.topDown = false;
			} else {
				out.offsetV = 5;
			}
			setOutputConnectionAnchor(i, out);
			setInputConnectionAnchor(i, in);
			outputConnectionAnchors.add(out);
			inputConnectionAnchors.add(in);
		}
	}

	public IFigure getContentsPane() {
		return scrollpane.getContents();
	}

	protected FixedConnectionAnchor getInputConnectionAnchor(int i) {
		return (FixedConnectionAnchor) connectionAnchors.get(Circuit.TERMINALS_IN[i]);
	}

	/**
	 * @see org.eclipse.gef.handles.HandleBounds#getHandleBounds()
	 */
	@Override
	public Rectangle getHandleBounds() {
		return getBounds().getShrinked(new Insets(4, 0, 4, 0));
	}

	protected FixedConnectionAnchor getOutputConnectionAnchor(int i) {
		return (FixedConnectionAnchor) connectionAnchors.get(Circuit.TERMINALS_OUT[i]);
	}

	@Override
	public Dimension getPreferredSize(int w, int h) {
		Dimension prefSize = super.getPreferredSize(w, h);
		Dimension defaultSize = new Dimension(200, 200);
		prefSize.union(defaultSize);
		return prefSize;
	}

	protected void layoutConnectionAnchors() {
		int x;
		for (int i = 0; i < 4; i++) {
			x = (2 * i + 1) * getSize().width / 8;
			getOutputConnectionAnchor(i + 4).setOffsetH(x - 1);
			getInputConnectionAnchor(i).setOffsetH(x - 1);
			getInputConnectionAnchor(i + 4).setOffsetH(x);
			getOutputConnectionAnchor(i).setOffsetH(x);
		}
	}

	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
	 */
	@Override
	protected void paintFigure(Graphics graphics) {
		Rectangle rect = getBounds().getCopy();
		rect.shrink(new Insets(4, 0, 4, 0));
		graphics.fillRectangle(rect);
	}

	public void setInputConnectionAnchor(int i, ConnectionAnchor c) {
		connectionAnchors.put(Circuit.TERMINALS_IN[i], c);
	}

	public void setOutputConnectionAnchor(int i, ConnectionAnchor c) {
		connectionAnchors.put(Circuit.TERMINALS_OUT[i], c);
	}

	@Override
	public String toString() {
		return "CircuitBoardFigure"; //$NON-NLS-1$
	}

	@Override
	public void validate() {
		if (isValid()) {
			return;
		}
		layoutConnectionAnchors();
		super.validate();
	}

	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}

	@Override
	public ScrollPane getScrollPane() {
		return scrollpane;
	}

}
