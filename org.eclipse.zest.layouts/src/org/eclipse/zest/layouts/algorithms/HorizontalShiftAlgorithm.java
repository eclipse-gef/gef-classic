/*******************************************************************************
 * Copyright (c) 2005-2010, 2026 The Chisel Group and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group - initial API and implementation
 *               Mateusz Matela
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.zest.layouts.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.interfaces.EntityLayout;

/**
 * This layout shifts overlapping nodes to the right.
 *
 * @author Ian Bull
 * @since 2.0
 */
public class HorizontalShiftAlgorithm extends AbstractLayoutAlgorithm {

	private static final double DELTA = 10;

	private static final double VSPACING = 16;

	@Override
	public void applyLayout(boolean clean) {
		if (!clean) {
			return;
		}
		List<List<EntityLayout>> rowsList = new ArrayList<>();
		EntityLayout[] entities = context.getEntities();

		for (EntityLayout element : entities) {
			addToRowList(element, rowsList);
		}

		Collections.sort(rowsList, (a0, a1) -> {
			EntityLayout entity0 = a0.get(0);
			EntityLayout entity1 = a1.get(0);
			return (int) (entity0.getLocation().y - entity1.getLocation().y);
		});

		Comparator<EntityLayout> entityComparator = (o1, o2) -> (int) (o1.getLocation().y - o2.getLocation().y);
		DisplayIndependentRectangle bounds = context.getBounds();
		double heightSoFar = 0;

		for (List<EntityLayout> currentRow : rowsList) {
			Collections.sort(currentRow, entityComparator);

			int i = 0;
			double width = bounds.width / 2 - currentRow.size() * 75;

			heightSoFar += currentRow.get(0).getSize().height + VSPACING;
			for (EntityLayout entity : currentRow) {
				DisplayIndependentDimension size = entity.getSize();
				i++;
				entity.setLocation(width + 10 * i + size.width / 2, heightSoFar + size.height / 2);
				width += size.width;
			}
		}
	}

	private static void addToRowList(EntityLayout entity, List<List<EntityLayout>> rowsList) {
		double layoutY = entity.getLocation().y;

		for (List<EntityLayout> currentRow : rowsList) {
			EntityLayout currentRowEntity = currentRow.get(0);
			double currentRowY = currentRowEntity.getLocation().y;
			if (layoutY >= currentRowY - DELTA && layoutY <= currentRowY + DELTA) {
				currentRow.add(entity);
				return;
			}
		}
		List<EntityLayout> newRow = new ArrayList<>();
		newRow.add(entity);
		rowsList.add(newRow);
	}
}
