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

import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.internal.InternalDraw2dUtils;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.editparts.ZoomManager;

public class MonitorAwareZoomManager {
	private final Set<ScalableFigure> scalablePanes;
	private final EditPartViewer viewer;
	private Control control;
	private final Listener zoomChangedListener = event -> refreshZoom(false);

	public enum ScaleMode {
		AUTO_SCALE, NATIVE_SCALE;
	}

	public MonitorAwareZoomManager(EditPartViewer viewer) {
		if (viewer == null) {
			throw new IllegalArgumentException("EditPartViewer must not be null"); //$NON-NLS-1$
		}
		this.scalablePanes = new HashSet<>();
		this.viewer = viewer;
		updateZoomManager(this.viewer.getProperty(ZoomManager.class.toString()), true);
		this.viewer.addPropertyChangeListener(evt -> {
			if (ZoomManager.class.toString().equals(evt.getPropertyName())) {
				updateZoomManager(evt.getNewValue(), true);
			}
		});
	}

	public void setControl(Control control) {
		if (this.control != null) {
			this.control.removeListener(SWT.ZoomChanged, zoomChangedListener);
		}
		this.control = control;
		if (control != null) {
			control.addListener(SWT.ZoomChanged, zoomChangedListener);
			refreshZoom(true);
		}
	}

	public void registerPane(ScalableFigure scalableFigure) {
		this.scalablePanes.add(scalableFigure);
		refreshZoom(true);
	}

	private void refreshZoom(boolean asyncUpdate) {
		this.scalablePanes.forEach(scalablePane -> scalablePane.setScale(calculateScale()));
		updateZoomManager(this.viewer.getProperty(ZoomManager.class.toString()), asyncUpdate);
	}

	private void updateZoomManager(Object value, boolean asyncUpdate) {
		if (value instanceof ZoomManager zoomManager) {
			zoomManager.setMonitorMultiplier(calculateScale(), asyncUpdate);
		}
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