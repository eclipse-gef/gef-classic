/*******************************************************************************
 * Copyright (c) 2025 Patrick Ziegler and others.
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

package org.eclipse.draw2d;

import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;

/**
 * Subclass of the {@link LightweightSystem} to handle native HiDPI scaling.
 *
 * <p>
 * Primary purpose of this class is to take over the scaling that would normally
 * be done by SWT. When this class is used, all figures are painted at 100% zoom
 * and then up-scaled to match the display zoom, in an attempt to reduce the
 * number of visual artifacts that would otherwise be introduced as a result of
 * rounding errors that result from fractional scaling.
 * </p>
 *
 * <p>
 * <i>Important:</i> This class modifies the viewport that is set as its
 * contents. Calling {@link Viewport#getContents()} returns a
 * {@link ScalableLayeredPane} with the figure that is set via
 * {@link Viewport#setContents(IFigure)} as its only child.
 *
 * </p>
 *
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as part
 * of a work in progress. There is no guarantee that this API will work nor that
 * it will remain the same. Please do not use this API without consulting with
 * the GEF-classic team.
 * </p>
 *
 * @since 3.21
 */
public class MonitorAwareLightweightSystem extends LightweightSystem {

	/**
	 * Data set by SWT that describes the native device zoom used for this widget.
	 */
	private static final String DATA_NATIVE_ZOOM = "NATIVE_ZOOM"; //$NON-NLS-1$

	/**
	 * Data that can be set to scale this widget at 100%.
	 */
	private static final String DATA_AUTOSCALE_DISABLED = "AUTOSCALE_DISABLED"; //$NON-NLS-1$

	/**
	 * Environment variable that when enabled, takes over the native HiDPI scaling
	 * that would otherwise be done by SWT. The value needs to be a positive
	 * integer.
	 */
	private static final String PROP_DRAW2D_AUTO_SCALE = "draw2d.autoScale"; //$NON-NLS-1$

	/**
	 * Set via the {@link #PROP_DRAW2D_AUTO_SCALE} property. If not {@code null},
	 * the auto-scaling done by SWT is disabled and instead, the lightweight-system
	 * is scaled by this factor.
	 */
	private static final Double DRAW2D_AUTO_SCALE;

	static {
		String autoScale = System.getProperty(PROP_DRAW2D_AUTO_SCALE);
		if (autoScale != null) {
			int zoom = parseIntUnchecked(autoScale);
			DRAW2D_AUTO_SCALE = zoom / 100.0;
		} else {
			DRAW2D_AUTO_SCALE = null;
		}
	}

	/**
	 * Convenience method to convert the integer defined by
	 * {@link #PROP_DRAW2D_AUTO_SCALE} without throwing a checked exception.
	 *
	 * @param property The property to convert.
	 * @return The integer described by the given string.
	 */
	private static int parseIntUnchecked(String property) {
		try {
			return Integer.parseInt(property);
		} catch (NumberFormatException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the native device zoom for this widget.
	 *
	 * @return 1.0 at 100%, 1.25 at 125% etc.
	 */
	private static double getNativeZoom(Control c) {
		Object displayZoom = c.getData(DATA_NATIVE_ZOOM);
		if (displayZoom == null) {
			return 1.0;
		}
		return Double.valueOf(displayZoom.toString()) / 100.0;
	}

	/**
	 * The scalable pane that is injected between the root figure and the contents
	 * of this viewport.
	 */
	private ScalableLayeredPane scalablePane;

	/**
	 * This property-change listener is hooked to the viewport of this
	 * lightweight-system and will automatically inject the scalable pane when
	 * {@link Viewport#setContents(IFigure)} is called.
	 */
	private final PropertyChangeListener viewportListener;

	public MonitorAwareLightweightSystem() {
		scalablePane = new ScalableLayeredPane(false);
		scalablePane.setLayoutManager(new StackLayout());
		viewportListener = event -> {
			if (Viewport.PROPERTY_CONTENTS.equals(event.getPropertyName())) {
				Viewport viewport = (Viewport) event.getSource();
				// ignore event fired when calling viewport.setContents(scalablePane)
				if (scalablePane == viewport.getContents()) {
					return;
				}

				injectScalablePane(viewport);
			}
		};
	}

	/**
	 * Updates the scale factor of the scalable pane to the given value. This value
	 * is ignored if {@link #PROP_DRAW2D_AUTO_SCALE} has been set.
	 *
	 * @param nativeZoom The new scale factor.
	 */
	private void setScale(double nativeZoom) {
		if (DRAW2D_AUTO_SCALE != null) {
			scalablePane.setScale(DRAW2D_AUTO_SCALE);
		} else {
			scalablePane.setScale(nativeZoom);
		}
	}

	@Override
	public void setControl(Canvas c) {
		if (c == null) {
			return;
		}

		c.setData(DATA_AUTOSCALE_DISABLED, true);
		c.addListener(SWT.ZoomChanged, e -> setScale(e.detail / 100.0));
		setScale(getNativeZoom(c));

		super.setControl(c);
	}

	@Override
	public void setContents(IFigure figure) {
		if (contents instanceof Viewport vp) {
			unhookViewport(vp);
		}
		super.setContents(figure);
		if (figure instanceof Viewport vp) {
			hookViewport(vp);
		}
	}

	/**
	 * Remove the property-change listener from the given viewport. Called from
	 * {@link #setContents(IFigure)}.
	 *
	 * @param viewport The old viewport of this lightweight-system.
	 */
	private void unhookViewport(Viewport viewport) {
		viewport.removePropertyChangeListener(viewportListener);
	}

	/**
	 * Add the property-change listener to the given viewport and inject the
	 * scalable pane. Called from {@link #setContents(IFigure)}.
	 *
	 * @param viewport The new viewport of this lightweight-system.
	 */
	private void hookViewport(Viewport viewport) {
		injectScalablePane(viewport);
		viewport.addPropertyChangeListener(viewportListener);
	}

	/**
	 * Injects a scalable pane between the given viewport and its contents. The old
	 * contents is moved and becomes a child of the scalable pane.
	 *
	 * @param viewport The viewport of this lightweight-system.
	 */
	private void injectScalablePane(Viewport viewport) {
		IFigure contents = viewport.getContents();

		scalablePane.removeAll();

		if (contents != null) {
			viewport.setContents(scalablePane);
			scalablePane.add(contents);
		}
	}
}
