/*******************************************************************************
 * Copyright (c) 2011, 2025 Google, Inc. and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Google, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.test.utils;

import org.eclipse.draw2d.DeferredUpdateManager;
import org.eclipse.draw2d.EventDispatcher;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTEventDispatcher;
import org.eclipse.draw2d.UpdateManager;

@SuppressWarnings("nls")
public class TestFigure extends Figure {
	private final UpdateManager updateManager;
	private final EventDispatcher eventDispatcher;
	private final TestLogger logger;

	public TestFigure(TestLogger logger) {
		this.logger = logger;
		this.updateManager = new DeferredUpdateManager() {
			@Override
			public void addDirtyRegion(IFigure figure, int x, int y, int w, int h) {
				logger.log("repaint(" + x + ", " + y + ", " + w + ", " + h + ")");
				super.addDirtyRegion(figure, x, y, w, h);
			}

			@Override
			protected void queueWork() {
				// noop
			}
		};
		this.eventDispatcher = new SWTEventDispatcher() {
			@Override
			public void updateCursor() {
				logger.log("updateCursor()");
			}
		};
	}

	@Override
	public void invalidate() {
		logger.log("invalidate()");
	}

	@Override
	public void erase() {
		logger.log("erase()");
	}

	@Override
	public UpdateManager getUpdateManager() {
		return updateManager;
	}

	@Override
	public EventDispatcher internalGetEventDispatcher() {
		return eventDispatcher;
	}
}
