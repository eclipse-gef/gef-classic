/*******************************************************************************
 * Copyright (c) 2009, 2026 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.zest.core.viewers;

import org.eclipse.zest.core.viewers.decorators.GraphLabelDecorator;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;

/**
 * Provides a mechanism to style nodes and edges when they are created.
 *
 * After each node or edge is created, the self styling method will be called
 * with both the element and the widget.
 *
 * @deprecated Use {@link GraphLabelDecorator} instead. This interface will be
 *             removed after the 2028-09 release.
 */
@Deprecated(since = "1.19", forRemoval = true)
public interface ISelfStyleProvider {

	/**
	 * Styles a connection
	 */
	@Deprecated(since = "1.19", forRemoval = true)
	public void selfStyleConnection(Object element, GraphConnection connection);

	/**
	 * Styles a node
	 */
	@Deprecated(since = "1.19", forRemoval = true)
	public void selfStyleNode(Object element, GraphNode node);

}
