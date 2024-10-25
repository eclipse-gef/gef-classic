/*******************************************************************************
 * Copyright (c) 2024 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/

package org.eclipse.gef.examples.shapes.handler;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.ui.IEditorPart;

import org.eclipse.gef.examples.shapes.ShapesEditor;

/**
 * This imperative expression is invoked by the {@code fragment.e4xmi} to check
 * whether the {@code Palette} menu item is visible.
 */
public class ColorPaletteExpression {
	@Evaluate
	@SuppressWarnings("static-method")
	public boolean test(@Optional @Active MPart activePart) {
		if (activePart != null) {
			return activePart.getContext().get(IEditorPart.class) instanceof ShapesEditor;
		}
		return false;
	}
}
