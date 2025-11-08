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

import org.eclipse.core.runtime.Assert;

import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.internal.InternalDraw2dUtils;

import org.eclipse.gef.editparts.SimpleRootEditPart;

/**
 * <b>Important:</b> This edit part should only be used when the Draw2D-based
 * auto-scaling is enabled. Otherwise scaling might be applied twice; Once by
 * Draw2D and once by SWT.
 */
public final class SimpleAutoscaledRootEditPart extends SimpleRootEditPart {
	public SimpleAutoscaledRootEditPart() {
		Assert.isTrue(InternalDraw2dUtils.isAutoScaleEnabled());
	}

	@Override
	protected final ScalableFigure createFigure() {
		ScalableFigure scalableFigure = new ScalableLayeredPane();
		this.addEditPartListener(InternalGEFPlugin.createAutoscaleEditPartListener(scalableFigure::setScale));
		return scalableFigure;
	}

	@Override
	public ScalableFigure getFigure() {
		return (ScalableFigure) super.getFigure();
	}
}