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

import java.util.Collections;
import java.util.List;

import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;

/**
 * Root object for the Zest data model. This model describes a graph as a list
 * of connections.
 */
public record DOTZestGraphModel(boolean directed, List<DOTZestGraphNodeModel> nodes, List<DOTZestGraphEdgeModel> edges) {
	public static final DOTZestGraphModel EMPTY = new DOTZestGraphModel(false, Collections.emptyList(), Collections.emptyList());

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		if (!nodes.isEmpty()) {
			builder.add("nodes", nodes); //$NON-NLS-1$
		}
		if (!edges.isEmpty()) {
			builder.add("edges", edges); //$NON-NLS-1$
		}
		return builder.toString();
	}
}
