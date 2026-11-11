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

import java.util.List;

import org.eclipse.pde.api.tools.annotations.NoImplement;

/**
 * This interface describes the response sent by a {@link ITool} back to the
 * language model. Clients should extend {@link AbstractToolResponse}. New
 * methods may be added in the future.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 */
@NoImplement
public interface IToolResponse {
	/**
	 * A list of content items representing the tool's output. Each item can be
	 * text, an image, or an embedded resource.
	 *
	 * @return An unmodifiable list of content items.
	 */
	List<IContent> getContent();

	/**
	 * An optional JSON object that represents the structured result of the tool
	 * call.
	 *
	 * @return A structured JSON-like response.
	 */
	Object getStructuredContent();

	/**
	 * If {@code true}, indicates that the tool execution failed and the content
	 * contains error information. If false or absent, indicates successful
	 * execution.
	 *
	 * @return {@code true}, if the tool execution was successful.
	 */
	boolean isError();
}
