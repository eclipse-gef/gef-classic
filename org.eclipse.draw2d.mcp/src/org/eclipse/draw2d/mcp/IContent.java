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
 * Base interface for all content values that can be sent back to the language
 * model.
 *
 * @noreference This interface is not intended to be referenced by clients.
 */
public sealed interface IContent permits TextContent {
	/**
	 * Describes the structure of the {@link #data()}. For example a plain string in
	 * a text response or a base 64 encoded data stream in case of an image.
	 *
	 * @return The content type.
	 */
	ContentType type();

	/**
	 *
	 * @return the content value.
	 */
	String data();
}
