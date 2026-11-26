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

package org.eclipse.draw2d.mcp;

/**
 * Abstract base implementation of a tool response. May be extended.
 */
public abstract class AbstractToolResponse implements IToolResponse {
	/**
	 * @see IToolResponse#getStructuredContent()
	 */
	@Override
	public Object getStructuredContent() {
		return null;
	}

	/**
	 * @see IToolResponse#isError()
	 */
	@Override
	public boolean isError() {
		return false;
	}
}
