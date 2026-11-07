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

/**
 * This interface describes the response sent by a {@link ITool} back to the
 * language model. Clients should extend {@link AbstractToolResponse}. New
 * methods may be added in the future.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IToolResponse {
	List<IContent> getContent();

	boolean isError();
}
