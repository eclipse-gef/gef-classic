/*******************************************************************************
 * Copyright (c) 2005, 2023 IBM Corporation and others.
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
import org.eclipse.gef.examples.text.model.ModelLocation;
import org.eclipse.gef.examples.text.model.Style;

public class ApplyMultiStyle extends MiniEdit {

	private int oldValue;
	private int newValue = -1;
	private final String styleID;
	private final Style style;

	public ApplyMultiStyle(Container c, String property, Object value) {
		style = c.getStyle();
		styleID = property;
		newValue = ((Integer) value).intValue();
	}

	@Override
	public boolean canApply() {
		return newValue != -1;
	}

	@Override
	public void apply() {
		if (Style.PROPERTY_ALIGNMENT.equals(styleID)) {
			oldValue = style.getAlignment();
			style.setAlignment(newValue);
		} else if (Style.PROPERTY_ORIENTATION.equals(styleID)) {
			oldValue = style.getOrientation();
			style.setOrientation(newValue);
		}
	}

	@Override
	public ModelLocation getResultingLocation() {
		return null;
	}

	@Override
	public void rollback() {
		if (Style.PROPERTY_ALIGNMENT.equals(styleID)) {
			style.setAlignment(oldValue);
		} else if (Style.PROPERTY_ORIENTATION.equals(styleID)) {
			style.setOrientation(oldValue);
		}
	}

}