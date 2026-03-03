/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
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
package org.eclipse.gef.examples.logicdesigner.figures;

import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;

/**
 * A Figure with a bent corner and an embedded TextFlow within a FlowPage that
 * contains text.
 */
public class LabelFigure extends BentCornerFigure {

	/** The inner TextFlow **/
	private final TextFlow textFlow;

	/**
	 * Creates a new LabelFigure with a default MarginBorder size of
	 * DEFAULT_CORNER_SIZE - 3 and a FlowPage containing a TextFlow with the style
	 * WORD_WRAP_SOFT.
	 */
	public LabelFigure() {
		this(BentCornerFigure.DEFAULT_CORNER_SIZE - 3);
	}

	/**
	 * Creates a new LabelFigure with a MarginBorder that is the given size and a
	 * FlowPage containing a TextFlow with the style WORD_WRAP_SOFT.
	 *
	 * @param borderSize the size of the MarginBorder
	 */
	public LabelFigure(int borderSize) {
		setBorder(new MarginBorder(borderSize));
		FlowPage flowPage = new FlowPage();

		textFlow = new TextFlow();

		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow, ParagraphTextLayout.WORD_WRAP_SOFT));

		flowPage.add(textFlow);

		setLayoutManager(new StackLayout());
		add(flowPage);
	}

	/**
	 * Returns the text inside the TextFlow.
	 *
	 * @return the text flow inside the text.
	 */
	public String getText() {
		return textFlow.getText();
	}

	/**
	 * Sets the text of the TextFlow to the given value.
	 *
	 * @param newText the new text value.
	 */
	public void setText(String newText) {
		textFlow.setText(newText);
	}

}
