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

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.InlineFlow;
import org.eclipse.draw2d.text.TextFlow;

import org.junit.jupiter.api.BeforeEach;

/**
 * @since 3.1
 */
public class SimpleTextTest extends AbstractTextTest {

	public static final String PHRASE = "The quick brown fox jumped over the lazy dog. "; //$NON-NLS-1$
	public FlowPage flowpage = new FlowPage();
	public TextFlow sentence = new TextFlow(PHRASE);
	public InlineFlow inline = new InlineFlow();
	public TextFlow child1 = new TextFlow(PHRASE);
	public TextFlow child2 = new TextFlow(PHRASE);
	public Font font = Display.getDefault().getSystemFont();

	@BeforeEach
	public void setUp() throws Exception {
		flowpage.setFont(font);
		flowpage.add(sentence);
		flowpage.add(inline);
		inline.add(child1);
		inline.add(child2);
	}

	protected void makePageWidth(String string, int extra) {
		int width = FigureUtilities.getStringExtents(string, font).width + extra;
		flowpage.setBounds(new Rectangle(0, 0, width, 300));
		flowpage.validate();
	}

}
