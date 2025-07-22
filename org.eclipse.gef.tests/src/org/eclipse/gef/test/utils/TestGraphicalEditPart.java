/*******************************************************************************
 * Copyright (c) 2006, 2025 IBM Corporation and others.
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
package org.eclipse.gef.test.utils;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class TestGraphicalEditPart extends AbstractGraphicalEditPart {

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	public void addChild(EditPart ep) {
		addChild(ep, 0);
	}

	@Override
	protected void register() {
		// do nothing
	}

	@Override
	protected IFigure createFigure() {
		return new Figure();
	}

	@Override
	protected void createEditPolicies() {
		// do nothing
	}
}