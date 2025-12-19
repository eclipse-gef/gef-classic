/*******************************************************************************
 * Copyright 2005, 2025 CHISEL Group, University of Victoria, Victoria,
 *                      BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.zest.layouts.algorithms;

import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

/**
 * A simple algorithm to arrange graph nodes in a layered horizontal tree-like
 * layout.
 *
 * @see TreeLayoutAlgorithm
 *
 * @version 1.0
 * @author Rob Lintern
 * @deprecated No longer used in Zest 2.x. This class will be removed in a
 *             future release. Use {@link TreeLayoutAlgorithm} with
 *             {@link TreeLayoutAlgorithm#LEFT_RIGHT} instead.
 * @noextend This class is not intended to be subclassed by clients.
 * @noreference This class is not intended to be referenced by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@Deprecated(since = "2.0", forRemoval = true)
public class HorizontalTreeLayoutAlgorithm extends TreeLayoutAlgorithm.Zest1 {

	/**
	 * Creates a horizontal tree layout with no style
	 */
	@Deprecated
	public HorizontalTreeLayoutAlgorithm() {
		this(LayoutStyles.NONE);
	}

	/**
	 *
	 */
	@Deprecated
	public HorizontalTreeLayoutAlgorithm(int styles) {
		super(styles);
	}

	@Override
	@Deprecated
	protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
			double x, double y, double width, double height) {
		// NOTE: width and height are swtiched here when calling super method
		super.preLayoutAlgorithm(entitiesToLayout, relationshipsToConsider, x, y, height, width);
	}

	@Override
	@Deprecated
	protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout,
			InternalRelationship[] relationshipsToConsider) {
		// swap x->y and width->height
		for (InternalNode entity : entitiesToLayout) {
			entity.setInternalLocation(entity.getInternalY(), entity.getInternalX());
			entity.setInternalSize(entity.getInternalWidth(), entity.getInternalHeight());
		}
		super.postLayoutAlgorithm(entitiesToLayout, relationshipsToConsider);
	}

	@Override
	@Deprecated
	protected boolean isValidConfiguration(boolean asynchronous, boolean continueous) {
		if (asynchronous && continueous) {
			return false;
		} else if (asynchronous && !continueous) {
			return true;
		} else if (!asynchronous && continueous) {
			return false;
		} else if (!asynchronous && !continueous) {
			return true;
		}

		return false;
	}

}
