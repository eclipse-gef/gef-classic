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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.internal.InternalDraw2dUtils;

public class MonitorAwareZoomManager {
	private final Set<ScalableFigure> scalablePanes;
	private Control control;
	private final Listener zoomChangedListener = event -> {
		refreshZoom();
	};

	public MonitorAwareZoomManager() {
		this.scalablePanes = new HashSet<>();
	}

	public void setControl(Control control) {
		if (this.control != null) {
			this.control.removeListener(SWT.ZoomChanged, zoomChangedListener);
		}
		this.control = control;
		if (control != null) {
			control.addListener(SWT.ZoomChanged, zoomChangedListener);
			refreshZoom();
		}
	}

	public void registerPane(ScalableFigure scalableFigure) {
		IFigure parentFigure = scalableFigure.getParent();
		while (parentFigure != null) {
			if (this.scalablePanes.contains(parentFigure)) {
				// do not register figure if a parent is already registered
				return;
			}
			parentFigure = parentFigure.getParent();
		}
		this.scalablePanes.add(scalableFigure);
		refreshZoom();
	}

	public void unregisterPane(ScalableFigure scalableFigure) {
		this.scalablePanes.remove(scalableFigure);
	}

	private void refreshZoom() {
		final double calculateScale = calculateScale();
		this.scalablePanes.forEach(scalablePane -> {
			scalablePane.setScale(calculateScale);
		});
	}

	private double calculateScale() {
		int zoom;
		if (InternalDraw2dUtils.disableAutoscale) {
			try {
				zoom = (int) control.getData("NATIVE_ZOOM"); //$NON-NLS-1$
			} catch (Exception e) {
				zoom = 100;
			}
		} else {
			zoom = 100;
		}
		return zoom / 100d;
	}
}