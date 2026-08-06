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

import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;

/**
 * Default implementation of the {@link IGraphLabelDecorator} interface. May be
 * sub-classed.
 *
 * @since 1.19
 */
public class GraphLabelDecorator extends LabelProvider implements IGraphLabelDecorator {

	@Override
	public Image decorateImage(Image image, Object element) {
		// Default implementation does nothing
		return image;
	}

	@Override
	public String decorateText(String text, Object element) {
		// Default implementation does nothing
		return text;
	}

	@Override
	public void decorateConnection(GraphConnection connection) {
		if (this instanceof IConnectionStyleDecorator decorator) {
			Object rel = connection.getData();

			int style = decorator.getConnectionStyle(rel);
			if (!ZestStyles.validateConnectionStyle(style)) {
				throw new SWTError(SWT.ERROR_INVALID_ARGUMENT);
			}

			if (style != ZestStyles.NONE) {
				connection.setConnectionStyle(style);
			}

			Optional.ofNullable(decorator.getHighlightColor(rel)).ifPresent(connection::setHighlightColor);
			Optional.ofNullable(decorator.getColor(rel)).ifPresent(connection::setLineColor);
			Optional.ofNullable(decorator.getTooltip(rel)).ifPresent(connection::setTooltip);
			Optional.ofNullable(decorator.getLineWidth(rel)).filter(w -> w >= 0).ifPresent(connection::setLineWidth);
			Optional.ofNullable(decorator.getRouter(rel)).ifPresent(connection::setRouter);
		}
		if (this instanceof IEntityConnectionStyleDecorator decorator) {
			Object src = connection.getSource().getData();
			Object dest = connection.getDestination().getData();

			int style = decorator.getConnectionStyle(src, dest);
			if (!ZestStyles.validateConnectionStyle(style)) {
				throw new SWTError(SWT.ERROR_INVALID_ARGUMENT);
			}

			if (style != ZestStyles.NONE) {
				connection.setConnectionStyle(style);
			}

			Optional.ofNullable(decorator.getColor(src, dest)).ifPresent(connection::setLineColor);
			Optional.ofNullable(decorator.getHighlightColor(src, dest)).ifPresent(connection::setHighlightColor);
			Optional.ofNullable(decorator.getTooltip(src, dest)).ifPresent(connection::setTooltip);
			Optional.ofNullable(decorator.getLineWidth(src, dest)).filter(w -> w >= 0).ifPresent(connection::setLineWidth);
			Optional.ofNullable(decorator.getRouter(src, dest)).ifPresent(connection::setRouter);
		}
	}

	@Override
	public void decorateNode(GraphNode node) {
		if (this instanceof IEntityStyleDecorator decorator) {
			Object entity = node.getData();

			if (decorator.fisheyeNode(entity)) {
				node.setNodeStyle(node.getNodeStyle() | ZestStyles.NODES_FISHEYE);
			}

			Optional.ofNullable(decorator.getBorderColor(entity)).ifPresent(node::setBorderColor);
			Optional.ofNullable(decorator.getBorderHighlightColor(entity)).ifPresent(node::setBorderHighlightColor);
			Optional.ofNullable(decorator.getNodeForegroundHighlightColor(entity)).ifPresent(node::setForegroundHighlightColor);
			Optional.ofNullable(decorator.getNodeBackgroundHighlightColor(entity)).ifPresent(node::setBackgroundHighlightColor);
			Optional.ofNullable(decorator.getBackgroundColor(entity)).ifPresent(node::setBackgroundColor);
			Optional.ofNullable(decorator.getForegroundColor(entity)).ifPresent(node::setForegroundColor);
			Optional.ofNullable(decorator.getBorderWidth(entity)).filter(width -> width >= 0).ifPresent(node::setBorderWidth);
			Optional.ofNullable(decorator.getTooltip(entity)).ifPresent(node::setTooltip);
		}
	}
}
