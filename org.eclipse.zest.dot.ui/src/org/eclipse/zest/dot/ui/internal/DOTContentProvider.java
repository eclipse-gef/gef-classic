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

package org.eclipse.zest.dot.ui.internal;

import java.util.function.Function;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;
import org.eclipse.zest.dot.core.internal.DOTDocumentModel;
import org.eclipse.zest.dot.core.internal.DOTZestGraphEdgeModel;
import org.eclipse.zest.dot.core.internal.DOTZestGraphModel;
import org.eclipse.zest.dot.core.internal.DOTZestGraphNodeModel;

public class DOTContentProvider implements IGraphContentProvider {
	private DOTZestGraphModel graph = DOTZestGraphModel.EMPTY;

	@Override
	public Object getSource(Object rel) {
		return findNode(rel, DOTZestGraphEdgeModel::sourceId);
	}

	@Override
	public Object getDestination(Object rel) {
		return findNode(rel, DOTZestGraphEdgeModel::targetId);
	}

	private DOTZestGraphNodeModel findNode(Object rel, Function<DOTZestGraphEdgeModel, String> extractor) {
		for (DOTZestGraphEdgeModel edge : graph.edges()) {
			if (edge.equals(rel)) {
				for (DOTZestGraphNodeModel node : graph.nodes()) {
					if (extractor.apply(edge).equals(node.id())) {
						return node;
					}
				}
			}
		}
		return null;
	}

	@Override
	public Object[] getElements(Object input) {
		return graph.edges().toArray();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		final DOTDocumentModel document = (DOTDocumentModel) newInput;
		graph = document != null ? document.getGraph() : DOTZestGraphModel.EMPTY;
	}
}
