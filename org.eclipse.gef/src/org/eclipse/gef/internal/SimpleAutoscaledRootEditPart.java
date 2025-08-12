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

import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.IScalablePane;
import org.eclipse.draw2d.ScalableLayeredPane;

import org.eclipse.gef.editparts.SimpleRootEditPart;

public final class SimpleAutoscaledRootEditPart extends SimpleRootEditPart {
	private final PropertyChangeListener autoScaleListener = evt -> {
		if (InternalGEFPlugin.MONITOR_SCALE_PROPERTY.equals(evt.getPropertyName()) && evt.getNewValue() != null) {
			double newValue = (double) evt.getNewValue();
			getFigure().setScale(newValue);
		}
	};

	@Override
	public void activate() {
		try {
			double scale = (double) getViewer().getProperty(InternalGEFPlugin.MONITOR_SCALE_PROPERTY);
			getFigure().setScale(scale);
		} catch (NullPointerException | ClassCastException e) {
			// no value available
		}
		super.activate();
	}

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
		getViewer().addPropertyChangeListener(autoScaleListener);
	}

	@Override
	protected void unregister() {
		getViewer().removePropertyChangeListener(autoScaleListener);
		super.unregister();
	}
}