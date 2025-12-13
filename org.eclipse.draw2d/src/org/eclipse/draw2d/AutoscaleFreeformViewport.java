/*******************************************************************************
 * Copyright (c) 2025 Johannes Kepler University Linz and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Alois Zoitl - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d;

/**
 * <b>IMPORTANT:</b> This class is <em>not</em> part of the GEF public API. It
 * is marked public only so that it can be by other GEF plugins and should never
 * be accessed from application code.
 *
 * @noreference This class is not intended to be referenced by clients.
 */
public class AutoscaleFreeformViewport extends FreeformViewport {

	@SuppressWarnings("removal")
	public AutoscaleFreeformViewport(boolean useScaledGraphics) {
		super.setContents(new ScalableFreeformLayeredPane(useScaledGraphics));
	}

	private ScalableFreeformLayeredPane getAutoScaleLayerPane() {
		return (ScalableFreeformLayeredPane) super.getContents();
	}

	@Override
	public IFigure getContents() {
		ScalableFreeformLayeredPane autoScaleLayerPane = getAutoScaleLayerPane();
		return (!autoScaleLayerPane.getChildren().isEmpty()) ? autoScaleLayerPane.getChildren().get(0) : null;
	}

	@Override
	public void setContents(IFigure figure) {
		ScalableFreeformLayeredPane autoScaleLayerPane = getAutoScaleLayerPane();
		if (!autoScaleLayerPane.getChildren().isEmpty()) {
			IFigure oldContent = autoScaleLayerPane.getChildren().get(0);
			autoScaleLayerPane.remove(oldContent);
		}
		autoScaleLayerPane.add(figure);
	}

	public void setScale(double scale) {
		getAutoScaleLayerPane().setScale(scale);
	}

	@Override
	protected FreeformFigure getFreeformFigure() {
		return getAutoScaleLayerPane();
	}
}
