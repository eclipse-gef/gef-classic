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
package org.eclipse.draw2d.examples.zoom;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.geometry.Translatable;

public class ZoomContainer extends Figure {

	{
		setLayoutManager(new StackLayout());
	}

	private float zoom;

	/**
	 * @see org.eclipse.draw2d.Figure#getClientArea()
	 */
	@Override
	public Rectangle getClientArea(Rectangle rect) {
		super.getClientArea(rect);
		rect.width /= zoom;
		rect.height /= zoom;
		return rect;
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		Dimension d = super.getPreferredSize(wHint, hHint);
		int w = getInsets().getWidth();
		int h = getInsets().getHeight();
		return d.getExpanded(-w, -h).scale(zoom).expand(w, h);
	}

	/**
	 * @see org.eclipse.draw2d.Figure#paintClientArea(Graphics)
	 */
	@Override
	protected void paintClientArea(Graphics g) {
		if (getChildren().isEmpty()) {
			return;
		}

		if (!optimizeClip()) {
			g.clipRect(getBounds().getShrinked(getInsets()));
		}
		g.translate(getBounds().x + getInsets().left, getBounds().y + getInsets().top);
		g.scale(zoom);
		g.pushState();
		paintChildren(g);
		g.popState();
		g.restoreState();
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
		revalidate();
		repaint();
	}

	/**
	 * @see org.eclipse.draw2d.Figure#translateToParent(Translatable)
	 */
	@Override
	public void translateToParent(Translatable t) {
		t.performScale(zoom);
		super.translateToParent(t);
	}

	/**
	 * @see org.eclipse.draw2d.Figure#translateFromParent(Translatable)
	 */
	@Override
	public void translateFromParent(Translatable t) {
		super.translateFromParent(t);
		t.performScale(1 / zoom);
	}

	/**
	 * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
	 */
	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}

}
