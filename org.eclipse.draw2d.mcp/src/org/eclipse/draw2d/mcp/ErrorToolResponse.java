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

import java.util.Collections;
import java.util.List;

/**
 * An {@link ErrorToolResponse} is returned whenever a tool couldn't be
 * successfully executed. For example if the input parameters were invalid or if
 * an internal error occurred.
 */
public final class ErrorToolResponse extends AbstractToolResponse {
	private final List<IContent> content;

	/**
	 * Creates a new error response with the given message.
	 *
	 * @param errorMessage The error message.
	 */
	public ErrorToolResponse(String errorMessage) {
		content = Collections.singletonList(new TextContent(errorMessage));
	}

	/**
	 * @see IToolResponse#getContent()
	 */
	@Override
	public List<IContent> getContent() {
		return content;
	}

	/**
	 * @see AbstractToolResponse#isError()
	 */
	@Override
	public boolean isError() {
		return true;
	}
}
