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
 * Default implementation of the {@link IGraphDecorator} interface. May be
 * sub-classed.
 *
 * @since 1.19
 */
public class GraphDecorator implements IGraphDecorator {
	@Override
	public void decorateConnection(GraphConnection connection) {
		// Default implementation does nothing
	}

	@Override
	public void decorateNode(GraphNode node) {
		// Default implementation does nothing
	}
}
