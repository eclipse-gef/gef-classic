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

import java.util.Objects;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.eclipse.zest.core.widgets.GraphItem;

/**
 * Represents a Zest {@link GraphItem}.
 */
public class SWTBotGraphItem extends AbstractSWTBot<GraphItem> {
	/**
	 * Constructs an instance of this object with the given browser
	 *
	 * @param item the widget.
	 * @throws WidgetNotFoundException if the widget is {@code null} or widget has
	 *                                 been disposed.
	 */
	public SWTBotGraphItem(GraphItem item) throws WidgetNotFoundException {
		super(item);
	}

	/**
	 * May be overridden by subclasses to cast to a more specific sub-type.
	 *
	 * @return The widget contained by this class.
	 */
	public GraphItem getWidget() {
		return widget;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getWidget());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SWTBotGraphItem other) {
			return Objects.equals(getWidget(), other.getWidget());
		}
		return false;
	}
}
