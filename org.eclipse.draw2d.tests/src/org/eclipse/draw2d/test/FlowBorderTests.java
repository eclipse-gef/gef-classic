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

import org.eclipse.swt.graphics.FontMetrics;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.draw2d.text.TextFragmentBox;

import org.junit.jupiter.api.Test;

/**
 * @since 3.1
 */
public class FlowBorderTests extends SimpleTextTest {

	@Test
	public void testBorderedTextFlow() {
		sentence.setBorder(new TestBorder(new Insets(5)));
		makePageWidth("The quick", 5); //$NON-NLS-1$
		expected.x = 0;
		expected.y = 0;
		expected.width = 5;
		FontMetrics metrics = FigureUtilities.getFontMetrics(font);
		expected.height = metrics.getHeight();
		assertFragmentLocation(getTextFragment(sentence, 0));
		expected.x = 5;
		expected.width = FigureUtilities.getStringExtents("The quick", font).width; //$NON-NLS-1$
		assertFragmentLocation(getTextFragment(sentence, 1));
	}

	private static TextFragmentBox getTextFragment(TextFlow flow, int index) {
		return flow.getFragments().get(index);
	}

}
