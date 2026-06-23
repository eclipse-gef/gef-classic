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

import org.eclipse.swt.graphics.Color;

import org.eclipse.draw2d.BasicColorProvider;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ColorProvider;

/**
 * Default implementation of the {@link ColorProvider} used by the Zest
 * {@link Graph}. This provider is defines the color of e.g. all
 * {@link GraphNode}s. May be extended to provide custom colors.
 * <p>
 * Additional methods may be added in the future.
 * </p>
 *
 * @since 1.18
 * @see Graph#setColorProvider(GraphColorProvider)
 */
public class GraphColorProvider extends BasicColorProvider {
	/**
	 * Provides a background color for the given element.
	 *
	 * @param element the element
	 * @return the background color for the element, or {@code null} to use the
	 *         default background color
	 */
	@SuppressWarnings("static-method")
	public Color getBackgroundColor(Object element) {
		if (element instanceof Graph) {
			return ColorConstants.white;
		}
		if (element instanceof GraphNode) {
			return ColorConstants.lightGray;
		}
		return null;
	}

	/**
	 * Provides a highlight background color for the given element.
	 *
	 * @param element the element
	 * @return the highlight background color for the element, or {@code null} to
	 *         use the default highlight background color
	 */
	@SuppressWarnings("static-method")
	public Color getBackgroundHighlightColor(Object element) {
		if (element instanceof GraphNode) {
			return ColorConstants.menuBackgroundSelected;
		}
		if (element instanceof GraphConnection) {
			return ColorConstants.darkBlue;
		}
		return null;
	}

	/**
	 * Provides a border color for the given element.
	 *
	 * @param element the element
	 * @return the border color for the element, or {@code null} to use the default
	 *         border color
	 */
	@SuppressWarnings("static-method")
	public Color getBorderColor(Object element) {
		if (element instanceof GraphContainer) {
			return ColorConstants.black;
		}
		if (element instanceof GraphNode) {
			return ColorConstants.lightGray;
		}
		return null;
	}

	/**
	 * Provides a border highlight color for the given element.
	 *
	 * @param element the element
	 * @return the border highlight color for the element, or {@code null} to use
	 *         the default border highlight color
	 */
	@SuppressWarnings("static-method")
	public Color getBorderHighlightColor(Object element) {
		if (element instanceof GraphNode) {
			return ColorConstants.blue;
		}
		return null;
	}

	/**
	 * Provides a foreground color for the given element.
	 *
	 * @param element the element
	 * @return the foreground color for the element, or {@code null} to use the
	 *         default foreground color
	 */
	@SuppressWarnings("static-method")
	public Color getForegroundColor(Object element) {
		if (element instanceof GraphContainer || element instanceof GraphNode) {
			return ColorConstants.black;
		}
		if (element instanceof GraphConnection) {
			return ColorConstants.lightGray;
		}
		return null;
	}

	/**
	 * Provides a highlight foreground color for the given element.
	 *
	 * @param element the element
	 * @return the highlight foreground color for the element, or {@code null} to
	 *         use the default highlight foreground color
	 */
	@SuppressWarnings("static-method")
	public Color getForegroundHighlightColor(Object element) {
		if (element instanceof GraphContainer || element instanceof GraphNode) {
			return ColorConstants.menuForegroundSelected;
		}
		return null;
	}
}
