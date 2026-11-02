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

import java.util.Map;

/**
 * This interface describes the request sent by the language model.
 *
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IToolRequest {
	/**
	 * Arguments to pass to the tool. These must conform to the tool's input schema.
	 *
	 * @return An unmodifiable map of arguments.
	 */
	Map<String, Object> getArguments();
}
