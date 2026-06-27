/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
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
package org.eclipse.zest.core.widgets.decorators;

import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;

/**
 *
 * A graph label decorator decorates the graph nodes and connections of a graph.
 * <p>
 * <em>WARNING:</em> This interface is not intended to be implemented by
 * clients. New methods may be added in the future.
 * </p>
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 1.19
 */
public interface IGraphDecorator {
	/**
	 * Decorate a connection.
	 */
	void decorateConnection(GraphConnection connection);

	/**
	 * Decorate a node.
	 */
	void decorateNode(GraphNode node);
}
