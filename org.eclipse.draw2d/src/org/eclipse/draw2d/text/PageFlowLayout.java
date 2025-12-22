/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
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
package org.eclipse.draw2d.text;

import org.eclipse.pde.api.tools.annotations.NoExtend;

/**
 * A block layout which requires no FlowContext to perform its layout. This
 * class is used by {@link FlowPage}.
 * <p>
 * WARNING: This class is not intended to be subclassed by clients.
 */
@NoExtend
public class PageFlowLayout extends BlockFlowLayout {

	/**
	 * Creates a new PageFlowLayout with the given FlowPage
	 *
	 * @param page the FlowPage
	 */
	public PageFlowLayout(FlowPage page) {
		super(page);
	}

	/**
	 * @see org.eclipse.draw2d.text.BlockFlowLayout#getContextWidth()
	 */
	@Override
	int getContextWidth() {
		return ((FlowPage) getFlowFigure()).getPageWidth();
	}

}
