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

import java.util.List;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphNode;

/**
 * Represents a Zest {@link GraphContainer}.
 */
public class SWTBotGraphContainer extends SWTBotGraphNode implements ISWTBotGraphContainer {
	/**
	 * Constructs an instance of this object with the given browser
	 *
	 * @param container the widget.
	 * @throws WidgetNotFoundException if the widget is {@code null} or widget has
	 *                                 been disposed.
	 */
	public SWTBotGraphContainer(GraphContainer container) {
		super(container);
	}

	@Override
	public GraphContainer getWidget() {
		return (GraphContainer) super.getWidget();
	}

	@Override
	public List<? extends GraphNode> internalGetNodes() {
		return syncExec(getWidget()::getNodes);
	}

	/**
	 * Open the container.
	 *
	 * @param animate {@code true}, if opening should be animated.
	 */
	public void open(boolean animate) {
		syncExec(() -> getWidget().open(animate));
	}
}
