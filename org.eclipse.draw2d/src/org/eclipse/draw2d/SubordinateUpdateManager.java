/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
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

import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @deprecated this class is not used
 */
@Deprecated
public abstract class SubordinateUpdateManager extends UpdateManager {

	/**
	 * A root figure.
	 */
	protected IFigure root;

	/**
	 * A graphics source
	 */
	protected GraphicsSource graphicsSource;

	/**
	 * @see UpdateManager#addDirtyRegion(IFigure, int, int, int, int)
	 */
	@Override
	public void addDirtyRegion(IFigure f, int x, int y, int w, int h) {
		if (getSuperior() == null) {
			return;
		}
		getSuperior().addDirtyRegion(f, x, y, w, h);
	}

	/**
	 * @see UpdateManager#addInvalidFigure(IFigure)
	 */
	@Override
	public void addInvalidFigure(IFigure f) {
		UpdateManager um = getSuperior();
		if (um == null) {
			return;
		}
		um.addInvalidFigure(f);
	}

	/**
	 * Returns the host figure.
	 *
	 * @return the host figure
	 */
	protected abstract IFigure getHost();

	/**
	 * Returns the superior update manager.
	 *
	 * @return the superior
	 */
	protected UpdateManager getSuperior() {
		if (getHost().getParent() == null) {
			return null;
		}
		return getHost().getParent().getUpdateManager();
	}

	/**
	 * @see UpdateManager#performUpdate()
	 */
	@Override
	public void performUpdate() {
		UpdateManager um = getSuperior();
		if (um == null) {
			return;
		}
		um.performUpdate();
	}

	/**
	 * @see UpdateManager#performUpdate(Rectangle)
	 */
	@Override
	public void performUpdate(Rectangle rect) {
		UpdateManager um = getSuperior();
		if (um == null) {
			return;
		}
		um.performUpdate(rect);
	}

	/**
	 * @see UpdateManager#setRoot(IFigure)
	 */
	@Override
	public void setRoot(IFigure f) {
		root = f;
	}

	/**
	 * @see UpdateManager#setGraphicsSource(GraphicsSource)
	 */
	@Override
	public void setGraphicsSource(GraphicsSource gs) {
		graphicsSource = gs;
	}
}
