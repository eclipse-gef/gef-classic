/*******************************************************************************
 * Copyright (c) 2005, 2025 IBM Corporation and others.
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

package org.eclipse.draw2d;

import org.eclipse.swt.widgets.Control;

import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A graphics source that posts a paint request to the control rather than
 * constructing GC on it directly. This allows the OS's native painting
 * mechanism to be used directly, including any double-buffering that the OS may
 * provide for free.
 *
 * @since 3.2
 */
public final class NativeGraphicsSource implements GraphicsSource {

	private final Control canvas;

	/**
	 * Constructs a new graphics source on the given control.
	 *
	 * @param canvas the control
	 * @since 3.2
	 */
	public NativeGraphicsSource(Control canvas) {
		this.canvas = canvas;
	}

	/**
	 * Always returns <code>null</code>, because
	 *
	 * @see GraphicsSource#getGraphics(Rectangle)
	 */
	@Override
	public Graphics getGraphics(Rectangle r) {
		canvas.redraw(r.x, r.y, r.width, r.height, false);

		// This is needed to avoid SWT/GEF bug 137786
		// (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=137786) where drag over
		// feedback (ie the drag feedback provided by the drag source) and drag under
		// feedback (ie the feedback provided by the drop target) interfere with each
		// other, causing flickering and more importantly ugly graphical artifacts.

		// canvas.update();

		// canvas.update() paints too much and only works on Windows. Use
		// readAndDispatch() to only paint the redraw() event.

		// Running readAndDispatch() might fire a mouseReleased() event while processing
		// a mousePressed() in e.g. the palette scrollbar. See:
		// https://github.com/eclipse-gef/gef-classic/issues/733
		if (Animation.isAnimating()) {
			canvas.getDisplay().readAndDispatch();
		}
		return null;
	}

	/**
	 * Does nothing.
	 *
	 * @see GraphicsSource#flushGraphics(Rectangle)
	 */
	@Override
	public void flushGraphics(Rectangle region) {
	}

}
