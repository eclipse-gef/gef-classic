/*******************************************************************************
 * Copyright 2005-2010, 2025 CHISEL Group, University of Victoria, Victoria,
 *                           BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria - initial API and implementation
 *               Mateusz Matela
 *               Ian Bull
 *******************************************************************************/
package org.eclipse.zest.layouts.algorithms;

import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

public class CompositeLayoutAlgorithm implements LayoutAlgorithm {

	/**
	 * Collection of Zest 1.x methods. Used for backwards compatibility.
	 *
	 * @since 2.0
	 * @deprecated Use {@link CompositeLayoutAlgorithm} instead. This class will be
	 *             removed in a future release.
	 * @noextend This class is not intended to be subclassed by clients.
	 * @noreference This class is not intended to be referenced by clients.
	 * @noinstantiate This class is not intended to be instantiated by clients.
	 */
	@SuppressWarnings("removal")
	@Deprecated(since = "2.0", forRemoval = true)
	public static class Zest1 extends AbstractLayoutAlgorithm.Zest1 {

		@Deprecated
		LayoutAlgorithm.Zest1[] algorithms = null;

		@Deprecated
		public Zest1(int styles, LayoutAlgorithm.Zest1[] algoirthms) {
			super(styles);
			this.algorithms = algoirthms;
		}

		@Deprecated
		public Zest1(LayoutAlgorithm.Zest1[] algoirthms) {
			this(0, algoirthms);
		}

		@Override
		@Deprecated
		protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
				double boundsX, double boundsY, double boundsWidth, double boundsHeight) {

			for (LayoutAlgorithm.Zest1 algorithm : algorithms) {
				try {
					algorithm.applyLayout(entitiesToLayout, relationshipsToConsider, boundsX, boundsY, boundsWidth,
							boundsHeight, this.internalAsynchronous, this.internalContinuous);
				} catch (InvalidLayoutConfiguration e) {
					e.printStackTrace();
				}
			}
			for (InternalNode element : entitiesToLayout) {
				element.getLayoutEntity().setLocationInLayout(element.getXInLayout(), element.getYInLayout());
			}

			// updateLayoutLocations(entitiesToLayout);
		}

		@Override
		@Deprecated
		protected int getCurrentLayoutStep() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		@Deprecated
		protected int getTotalNumberOfLayoutSteps() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		@Deprecated
		protected boolean isValidConfiguration(boolean asynchronous, boolean continuous) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		@Deprecated
		protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout,
				InternalRelationship[] relationshipsToConsider) {
			// TODO Auto-generated method stub

		}

		@Override
		@Deprecated
		protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
				double x, double y, double width, double height) {
			// TODO Auto-generated method stub

		}

		@Override
		@Deprecated
		public void setLayoutArea(double x, double y, double width, double height) {
			// TODO Auto-generated method stub

		}

	}

	private LayoutAlgorithm[] algorithms = null;

	public CompositeLayoutAlgorithm(LayoutAlgorithm[] algorithms) {
		this.algorithms = algorithms;
	}

	/**
	 * @deprecated Since Zest 2.0, use
	 *             {@link #CompositeLayoutAlgorithm(LayoutAlgorithm[])}
	 */
	@Deprecated
	public CompositeLayoutAlgorithm(int style, LayoutAlgorithm[] layoutAlgorithms) {
		this(layoutAlgorithms);
	}

	@Override
	public void applyLayout(boolean clean) {
		for (LayoutAlgorithm algorithm : algorithms) {
			algorithm.applyLayout(clean);
		}
	}

	@Override
	public void setLayoutContext(LayoutContext context) {
		for (LayoutAlgorithm algorithm : algorithms) {
			algorithm.setLayoutContext(context);
		}
	}
}
