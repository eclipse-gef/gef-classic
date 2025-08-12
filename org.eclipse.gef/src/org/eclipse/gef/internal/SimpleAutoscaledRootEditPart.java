/*******************************************************************************
 * Copyright (c) 2025 Yatta and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Yatta - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.internal;

import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.ScalableLayeredPane;

import org.eclipse.gef.editparts.SimpleRootEditPart;

public final class SimpleAutoscaledRootEditPart extends SimpleRootEditPart {

	@Override
	protected final ScalableFigure createFigure() {
		ScalableFigure scalableFigure = new ScalableLayeredPane();
		this.addEditPartListener(InternalGEFPlugin.createAutoscaleEditPartListener(scalableFigure));
		return scalableFigure;
	}

	@Override
	public ScalableFigure getFigure() {
		return (ScalableFigure) super.getFigure();
	}
}