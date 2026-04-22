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

package org.eclipse.zest.core.widgets;

import org.eclipse.zest.core.widgets.internal.GraphLabel;

import org.eclipse.draw2d.IFigure;

/**
 * @since 1.18
 */
public class GraphFigureFactory implements IFigureFactory {
	@Override
	public IFigure createFigure(Object model) {
		if (model instanceof GraphNode node) {
			return new GraphLabel(node.getText(), node.getImage(), node.cacheLabel());
		}
		return null;
	}

}
