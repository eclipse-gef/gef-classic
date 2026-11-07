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

public final class ErrorToolResponse extends AbstractToolResponse {
	private final List<IContent> content;

	public ErrorToolResponse(String errorMessage) {
		content = Collections.singletonList(new TextContent(errorMessage));
	}

	@Override
	public List<IContent> getContent() {
		return content;
	}

	@Override
	public boolean isError() {
		return true;
	}
}
