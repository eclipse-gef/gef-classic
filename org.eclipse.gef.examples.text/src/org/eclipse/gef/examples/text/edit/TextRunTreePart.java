/*******************************************************************************
 * Copyright (c) 2004, 2023 IBM Corporation and others.
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

package org.eclipse.gef.examples.text.edit;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.examples.text.model.TextRun;

/**
 * @since 3.1
 */
public class TextRunTreePart extends ExampleTreePart {

	public TextRunTreePart(Object model) {
		setModel(model);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("text")) { //$NON-NLS-1$
			refreshVisuals();
		}
	}

	@Override
	protected void refreshVisuals() {
		TextRun run = (TextRun) getModel();
		String s = run.getText();
		if (s.length() > 50) {
			s = s.substring(0, 50) + "..."; //$NON-NLS-1$
		}
		setWidgetText(s);
	}

}
