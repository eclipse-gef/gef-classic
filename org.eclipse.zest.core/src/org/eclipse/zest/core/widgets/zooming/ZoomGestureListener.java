/*******************************************************************************
 * Copyright 2012, Zoltan Ujhelyi. All rights reserved. This program and the 
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Zoltan Ujhelyi
 ******************************************************************************/
package org.eclipse.zest.core.widgets.zooming;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.GestureEvent;
import org.eclipse.swt.events.GestureListener;

/**
 * A simple magnify gesture listener class that calls an associated
 * {@link ZoomManager} class to perform zooming.
 * 
 * @author Zoltan Ujhelyi
 * @since 2.0
 */
public class ZoomGestureListener implements GestureListener {
	private final ZoomManager manager;

	/**
	 * Initializes the gesture listener
	 * 
	 * @param graph
	 *            the graph widget to zoom
	 */
	public ZoomGestureListener(ZoomManager manager) {
		this.manager = manager;
	}

	double zoom = 1.0;

	public void gesture(GestureEvent e) {
		switch (e.detail) {
		case SWT.GESTURE_BEGIN:
			zoom = manager.getZoom();
			break;
		case SWT.GESTURE_END:
			break;
		case SWT.GESTURE_MAGNIFY:
			double newValue = zoom * e.magnification;
			manager.setZoom(newValue);
			break;
		default:
			// Do nothing
		}
	}
}