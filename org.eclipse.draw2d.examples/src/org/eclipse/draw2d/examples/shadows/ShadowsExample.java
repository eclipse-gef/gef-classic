/*******************************************************************************
 * Copyright (c) 2026 Johannes Kepler University Linz
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alois Zoitl - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d.examples.shadows;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.examples.AbstractExample;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.shadows.EllipseDropShadowBorder;
import org.eclipse.draw2d.shadows.RectangleDropShadowBorder;

public class ShadowsExample extends AbstractExample {

	public static void main(String[] args) {
		new ShadowsExample().run();
	}

	@Override
	protected IFigure createContents() {
		IFigure panel = new Figure();

		panel.add(createPage());
		panel.add(createRectangleFigure(480, 40, 50, 30, ColorConstants.green));
		panel.add(createRectangleFigure(480, 230, 50, 30, ColorConstants.black));

		return panel;
	}

	@Override
	protected void hookShell(Shell shell) {
		getFigureCanvas().setSize(600, 500);
	}

	private static IFigure createCircleFigure(int x, int y, int r, Color color) {
		IFigure circle = new Figure() {
			@Override
			protected void paintFigure(org.eclipse.draw2d.Graphics graphics) {
				// super paint figure first to draw the border
				super.paintFigure(graphics);
				graphics.fillOval(getBounds());
			}
		};

		circle.setBounds(new Rectangle(x - r, y - r, 2 * r, 2 * r));
		circle.setBackgroundColor(color);
		circle.setBorder(new EllipseDropShadowBorder());
		return circle;
	}

	private static IFigure createPage() {
		IFigure page = createRectangleFigure(10, 10, 350, 420, ColorConstants.white);
		// give page a larger softer schadow
		RectangleDropShadowBorder pageBorder = new RectangleDropShadowBorder();
		pageBorder.setDropShadowSize(12);
		pageBorder.setHaloSize(6);
		pageBorder.setSoftness(4.5);
		page.setBorder(pageBorder);

		page.add(createCircleFigure(40, 40, 20, ColorConstants.darkBlue));
		page.add(createRoundedRectangleFigure(150, 45, 120, 100, 10, ColorConstants.button));
		page.add(createRectangleFigure(200, 200, 120, 75, ColorConstants.cyan));
		page.add(createRectangleFigure(75, 300, 200, 100, ColorConstants.lightGreen));

		return page;
	}

	private static IFigure createRectangleFigure(int x, int y, int w, int h, Color color) {
		IFigure circle = new Figure() {
			@Override
			protected void paintFigure(org.eclipse.draw2d.Graphics graphics) {
				// super paint figure first to draw the border
				super.paintFigure(graphics);
				graphics.fillRectangle(getBounds());
			}
		};

		circle.setBounds(new Rectangle(x, y, w, h));
		circle.setBackgroundColor(color);
		circle.setBorder(new RectangleDropShadowBorder());
		return circle;
	}

	private static IFigure createRoundedRectangleFigure(int x, int y, int w, int h, int cornerRadius, Color color) {
		IFigure circle = new Figure() {
			@Override
			protected void paintFigure(org.eclipse.draw2d.Graphics graphics) {
				// super paint figure first to draw the border
				super.paintFigure(graphics);
				graphics.fillRoundRectangle(getBounds(), cornerRadius, cornerRadius);
			}
		};

		circle.setBounds(new Rectangle(x, y, w, h));
		circle.setBackgroundColor(color);
		circle.setBorder(new RectangleDropShadowBorder(cornerRadius));
		return circle;
	}

}
