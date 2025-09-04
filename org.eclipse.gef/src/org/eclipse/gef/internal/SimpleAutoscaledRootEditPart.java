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

import org.eclipse.draw2d.IScalablePane;
import org.eclipse.draw2d.ScalableLayeredPane;

import org.eclipse.gef.editparts.SimpleRootEditPart;

public final class SimpleAutoscaledRootEditPart extends SimpleRootEditPart {
	@Override
	protected final IScalablePane createFigure() {
		return new ScalableLayeredPane();
	}

	@Override
	public IScalablePane getFigure() {
		return (IScalablePane) super.getFigure();
	}

	@Override
	protected void register() {
		super.register();
		MonitorAwareZoomManager monitorAwareZoomManager = (MonitorAwareZoomManager) getViewer()
				.getProperty(MonitorAwareZoomManager.class.toString());
		if (monitorAwareZoomManager != null) {
			monitorAwareZoomManager.registerPane(getFigure());
		}
	}
}