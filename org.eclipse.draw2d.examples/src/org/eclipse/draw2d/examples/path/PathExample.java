/*******************************************************************************
 * Copyright (c) 2008, 2025 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stephane Lizeray slizeray@ilog.fr - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.examples.path;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ButtonBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.ScrollBar;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Point;

public class PathExample {

	public static void main(String[] args) {
		Display d = new Display();
		final Shell shell = new Shell(d, SWT.SHELL_TRIM | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE);
		shell.setSize(800, 800);
		shell.setLayout(new org.eclipse.swt.layout.GridLayout(1, false));

		Canvas canvas = new Canvas(shell, SWT.NONE);
		canvas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		LightweightSystem lws = new LightweightSystem(canvas);
		final Figure fig = new Figure();

		fig.setLayoutManager(new ToolbarLayout());

		final ScrollBar bar = new ScrollBar();
		final Label l = new Label("<Zoom>"); //$NON-NLS-1$

		l.setBorder(new SchemeBorder(ButtonBorder.SCHEMES.BUTTON_SCROLLBAR));
		bar.setThumb(l);
		bar.setHorizontal(true);
		bar.setMaximum(200);
		bar.setMinimum(0);
		bar.setExtent(25);
		fig.add(bar);

		final ZoomFigure zoomFigure = new ZoomFigure();
		zoomFigure.setPreferredSize(500, 800);

		fig.add(zoomFigure);

		zoomFigure.setLayoutManager(new BorderLayout());
		zoomFigure.setScale(1);

		PathFigure polyline = new PathFigure();
		int w = 70;
		int k = 50;
		polyline.addPoint(new Point(0 + k, 0 + k));
		polyline.addPoint(new Point(w + k, 0 + k));
		polyline.addPoint(new Point(w + k, w + k));
		polyline.addPoint(new Point(0 + k, w + k));
		polyline.setLineWidth(3);
		polyline.setBackgroundColor(ColorConstants.red);
		polyline.setClosed(true);

		Point c = polyline.getBounds().getCenter();
		polyline.setRotation(c.x(), c.y(), 45);
		zoomFigure.add(polyline, BorderLayout.CENTER);

		bar.addPropertyChangeListener("value", evt -> { //$NON-NLS-1$
			float z = (bar.getValue() + 50) * 0.02f;
			zoomFigure.setScale(z);
		});

		lws.setContents(fig);
		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}

	}
}
