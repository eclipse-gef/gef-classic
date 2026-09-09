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

package org.eclipse.zest.dot.core.internal;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;

/**
 * This model describes a connection between two nodes in the Zest graph.
 */
public record DOTZestGraphEdgeModel(String sourceId, String targetId, Map<String, String> attributes) {

	public DOTZestGraphEdgeModel(String sourceId, String targetId) {
		this(sourceId, targetId, new LinkedHashMap<>());
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.add("sourceId", sourceId); //$NON-NLS-1$
		builder.add("targetId", targetId); //$NON-NLS-1$
		builder.add("attributes", attributes); //$NON-NLS-1$
		return builder.toString();
	}
}
