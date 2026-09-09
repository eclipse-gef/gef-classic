/*******************************************************************************
 * Copyright (c) 2026 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.zest.dot.ui.internal;

import org.eclipse.zest.core.viewers.decorators.GraphLabelDecorator;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.dot.core.internal.DOTZestGraphEdgeModel;
import org.eclipse.zest.dot.core.internal.DOTZestGraphNodeModel;

public class DOTLabelDecorator extends GraphLabelDecorator {

	@Override
	public void decorateConnection(GraphConnection connection) {
		if (connection.getData() instanceof DOTZestGraphEdgeModel model) {
			connection.setText(model.attributes().getOrDefault(DOTAttribute.LABEL.getKey(), "")); //$NON-NLS-1$
		}
	}

	@Override
	public void decorateNode(GraphNode node) {
		if (node.getData() instanceof DOTZestGraphNodeModel model) {
			node.setText(model.attributes().getOrDefault(DOTAttribute.LABEL.getKey(), model.id()));
		}
	}
}
