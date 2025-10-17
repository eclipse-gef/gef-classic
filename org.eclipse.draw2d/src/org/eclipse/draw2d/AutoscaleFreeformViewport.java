/*******************************************************************************
 * Copyright (c) 2025 Johannes Kepler University Linz.
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
 * @since 3.21
 */
public class AutoscaleFreeformViewport extends FreeformViewport implements ScalableFigure {

	public AutoscaleFreeformViewport() {
		super.setContents(new ScalableFreeformLayeredPane(false));
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
	public double getScale() {
		return getAutoScaleLayerPane().getScale();
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

	@Override
	public void setScale(double scale) {
		getAutoScaleLayerPane().setScale(scale);
	}

}
