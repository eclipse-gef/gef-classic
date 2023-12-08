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
package org.eclipse.gef.examples.text.actions;

public class MultiStyleAction extends BooleanStyleAction {

	private Object value;

	public MultiStyleAction(StyleService service, String styleID, String property, Object value) {
		super(service, styleID, property);
		this.value = value;
	}

	@Override
	public void run() {
		service.setStyle(property, isChecked() ? value : Integer.valueOf(0));
	}

	@Override
	public void refresh() {
		setChecked(value.equals(service.getStyle(property)));
		setEnabled(service.getStyleState(property).equals(StyleService.STATE_EDITABLE));
	}

}