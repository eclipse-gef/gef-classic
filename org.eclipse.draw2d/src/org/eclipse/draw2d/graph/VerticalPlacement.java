/*******************************************************************************
 * Copyright (c) 2003, 2025 IBM Corporation and others.
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
package org.eclipse.draw2d.graph;

import org.eclipse.draw2d.geometry.Insets;

/**
 * Assigns the Y and Height values to the nodes in the graph. All nodes in the
 * same row are given the same height.
 *
 * @author Randy Hudson
 * @since 2.1.2
 */
class VerticalPlacement extends GraphVisitor {

	@Override
	@SuppressWarnings("removal")
	void visit(DirectedGraph g) {
		Insets pad;
		int currentY = g.getMargin().top;
		int row;
		RankList ranks = g.ranks;
		g.rankLocations = new int[ranks.size() + 1];
		for (row = 0; row < ranks.size(); row++) {
			g.rankLocations[row] = currentY;
			Rank rank = ranks.getRank(row);
			int rowHeight = 0;
			rank.topPadding = rank.bottomPadding = 0;
			for (Node node : rank) {
				pad = g.getPadding(node);
				rowHeight = Math.max(node.height, rowHeight);
				rank.topPadding = Math.max(pad.top, rank.topPadding);
				rank.bottomPadding = Math.max(pad.bottom, rank.bottomPadding);
			}
			currentY += rank.topPadding;
			rank.setDimensions(currentY, rowHeight);
			currentY += rank.height + rank.bottomPadding;
		}
		g.rankLocations[row] = currentY;
		g.size.height = currentY;
	}

}
