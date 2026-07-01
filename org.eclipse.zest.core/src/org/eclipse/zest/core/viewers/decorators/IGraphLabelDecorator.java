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
package org.eclipse.zest.core.viewers.decorators;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.decorators.IGraphDecorator;

/**
 *
 * A graph label decorator decorates the graph nodes and connections of a graph.
 * The original label text and image are obtained by some other means, for
 * example by a label provider.
 * <p>
 * <em>WARNING:</em> This interface is not intended to be implemented by
 * clients. Use {@link GraphLabelDecorator} instead. New methods may be added in
 * the future.
 * </p>
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 1.19
 */
public interface IGraphLabelDecorator extends IGraphDecorator, ILabelDecorator {
	@Override
	void decorateConnection(GraphConnection connection);

	@Override
	void decorateNode(GraphNode node);
}
