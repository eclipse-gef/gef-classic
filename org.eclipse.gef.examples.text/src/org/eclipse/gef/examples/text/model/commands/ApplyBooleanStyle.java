/*******************************************************************************
 * Copyright (c) 2004, 2026 IBM Corporation and others.
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

package org.eclipse.gef.examples.text.model.commands;

import org.eclipse.gef.examples.text.model.Container;
import org.eclipse.gef.examples.text.model.InlineContainer;
import org.eclipse.gef.examples.text.model.ModelLocation;
import org.eclipse.gef.examples.text.model.Style;
import org.eclipse.gef.examples.text.model.TextRun;

/**
 * Applies a boolean style such as BOLD to a range in the model.
 *
 * @since 3.1
 */
public class ApplyBooleanStyle extends MiniEdit {

	private final TextRun begin;
	private final int beginOffset;
	private Container container;
	private final int endOffset;
	String[] keys;
	private TextRun middle;
	private final Container parent;
	private TextRun right;
	Object[] values;

	public ApplyBooleanStyle(ModelLocation start, ModelLocation end, String keys[], Object values[]) {
		begin = (TextRun) start.model;
		parent = begin.getContainer();
		beginOffset = start.offset;
		endOffset = end.offset;
		this.keys = keys;
		this.values = values;
	}

	@Override
	public void apply() {
		right = begin.subdivideRun(endOffset);
		if (right.getText().length() == 0) {
			right = null;
		}
		middle = begin.subdivideRun(beginOffset);

		container = new InlineContainer(Container.TYPE_INLINE);
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			switch (key) {
			case Style.PROPERTY_BOLD:
				container.getStyle().setBold(((Boolean) values[i]).booleanValue());
				break;
			case Style.PROPERTY_FONT_SIZE:
				container.getStyle().setFontHeight(((Integer) values[i]).intValue());
				break;
			case Style.PROPERTY_ITALIC:
				container.getStyle().setItalic(((Boolean) values[i]).booleanValue());
				break;
			case Style.PROPERTY_UNDERLINE:
				container.getStyle().setUnderline(((Boolean) values[i]).booleanValue());
				break;
			case Style.PROPERTY_FONT:
				container.getStyle().setFontFamily((String) values[i]);
				break;
			default:
				break;
			}
		}
		container.add(middle);
		int index = begin.getContainer().getChildren().indexOf(begin) + 1;

		if (right != null) {
			parent.add(right, index);
		}
		parent.add(container, index);
		if (begin.getText().length() == 0) {
			parent.remove(begin);
		}
	}

	@Override
	public boolean canApply() {
		return true;
	}

	@Override
	public ModelLocation getResultingLocation() {
		return new ModelLocation(middle, middle.size());
	}

	@Override
	public void rollback() {
		int index = parent.getChildren().indexOf(container);
		parent.remove(container);
		if (right != null) {
			parent.remove(right);
		}
		if (begin.getContainer() == null) {
			parent.add(begin, index);
		}
		begin.insertText(middle.getText(), begin.size());
		if (right != null) {
			begin.insertText(right.getText(), begin.size());
		}
	}

}