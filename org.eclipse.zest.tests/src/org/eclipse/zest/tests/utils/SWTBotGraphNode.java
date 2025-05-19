/*******************************************************************************
 * Copyright (c) 2025 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/
package org.eclipse.zest.tests.utils;

import org.eclipse.swt.graphics.Image;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.zest.core.widgets.GraphNode;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

/**
 * Represents a Zest {@link GraphNode}.
 */
public class SWTBotGraphNode extends SWTBotGraphItem {
	/**
	 * Constructs an instance of this object with the given browser
	 *
	 * @param node the widget.
	 * @throws WidgetNotFoundException if the widget is {@code null} or widget has
	 *                                 been disposed.
	 */
	public SWTBotGraphNode(GraphNode node) throws WidgetNotFoundException {
		super(node);
	}

	@Override
	public GraphNode getWidget() {
		return (GraphNode) super.getWidget();
	}

	@Override
	public boolean isVisible() {
		// SWTBot only supports isVisible for Controls, not plain Widgets
		return syncExec(getWidget()::isVisible);
	}

	/**
	 * @return The figure of the whole node.
	 */
	public IFigure getNodeFigure() {
		return syncExec(getWidget()::getNodeFigure);
	}

	/**
	 * @return the widget data
	 */
	public Object getData() {
		return syncExec((Result<Object>) getWidget()::getData);
	}

	/**
	 * @return a copy of the node's location.
	 */
	public Point getLocation() {
		return syncExec(getWidget()::getLocation);
	}

	/**
	 * @return a copy of the node's size.
	 */
	public Dimension getSize() {
		return syncExec(getWidget()::getSize);
	}

	/**
	 * @return the node's image
	 */
	public Image getImage() {
		return syncExec(getWidget()::getImage);
	}
}