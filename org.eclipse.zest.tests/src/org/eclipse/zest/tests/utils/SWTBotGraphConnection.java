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

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.eclipse.zest.core.widgets.GraphConnection;

import org.eclipse.draw2d.Connection;

/**
 * Represents a Zest {@link GraphConnection}.
 */
public class SWTBotGraphConnection extends AbstractSWTBot<GraphConnection> {

	/**
	 * Constructs an instance of this object with the given browser
	 *
	 * @param connection the widget.
	 * @throws WidgetNotFoundException if the widget is {@code null} or widget has
	 *                                 been disposed.
	 */
	public SWTBotGraphConnection(GraphConnection connection) {
		super(connection);
	}

	/**
	 * @return The source node for this relationship
	 */
	public SWTBotGraphNode getSource() {
		return (SWTBotGraphNode) ISWTBotGraphContainer.internalConvert(syncExec(widget::getSource));
	}

	/**
	 * @return The target node for this relationship
	 */
	public SWTBotGraphNode getDestination() {
		return (SWTBotGraphNode) ISWTBotGraphContainer.internalConvert(syncExec(widget::getDestination));
	}

	/**
	 * @return The figure of the whole connection.
	 */
	public Connection getConnectionFigure() {
		return syncExec(widget::getConnectionFigure);
	}

	/**
	 * @param value the widget data
	 */
	public void setData(Object value) {
		syncExec(() -> widget.setData(value));
	}
}
