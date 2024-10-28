/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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

package org.eclipse.draw2d.test;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.AbstractFlowBorder;
import org.eclipse.draw2d.text.TextFragmentBox;

/**
 * @since 3.1
 */
public class AbstractTextTest extends BaseTestCase {

	private static final String FRAGMENT_LOCATION_ERROR = "Fragment location error"; //$NON-NLS-1$

	static class TestBorder extends AbstractFlowBorder {
		private final Insets insets;

		TestBorder(Insets insets) {
			this.insets = insets;
		}

		@Override
		public Insets getInsets(IFigure figure) {
			return insets;
		}
	}

	public Rectangle expected = new Rectangle();

	protected void assertFragmentLocation(TextFragmentBox box) {
		assertEquals(box.getX(), expected.x, FRAGMENT_LOCATION_ERROR);
		assertEquals(box.getBaseline() - box.getAscent(), expected.y, FRAGMENT_LOCATION_ERROR);
		assertEquals(box.getBaseline() + box.getDescent(), expected.bottom(), FRAGMENT_LOCATION_ERROR);
		assertEquals(box.getWidth() + box.getX(), expected.right(), FRAGMENT_LOCATION_ERROR);
	}

}
