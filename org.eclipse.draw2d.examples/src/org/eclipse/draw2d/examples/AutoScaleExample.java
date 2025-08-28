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

package org.eclipse.draw2d.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MonitorAwareLightweightSystem;
import org.eclipse.draw2d.geometry.Dimension;

public class AutoScaleExample {
	public static void main(String[] args) {
		System.setProperty("swt.autoScale.updateOnRuntime", Boolean.TRUE.toString()); //$NON-NLS-1$
		System.setProperty("draw2d.autoScale", "200"); //$NON-NLS-1$ //$NON-NLS-2$
		Shell shell = new Shell();
		shell.setSize(400, 400);
		shell.setLayout(new FillLayout());

		FigureCanvas canvas = new FigureCanvas(shell, SWT.NONE, new MonitorAwareLightweightSystem());
		Figure root = new Figure();
		root.setLayoutManager(new ListLayout());
		root.add(createLabel(ColorConstants.red));
		root.add(createLabel(ColorConstants.green));
		root.add(createLabel(ColorConstants.blue));
		root.add(createLabel(ColorConstants.yellow));
		root.add(createLabel(ColorConstants.cyan));
		root.add(createLabel(ColorConstants.white));
		root.add(createLabel(ColorConstants.gray));
		root.add(createLabel(ColorConstants.orange));
		canvas.getViewport().setContentsTracksHeight(true);
		canvas.getViewport().setContentsTracksWidth(true);
		canvas.getViewport().setContents(root);

		shell.open();

		Display display = shell.getDisplay();
		while (!display.isDisposed()) {
			display.readAndDispatch();
		}
	}

	private static IFigure createLabel(Color bg) {
		return new Figure() {
			@Override
			protected void paintFigure(Graphics graphics) {
				super.paintFigure(graphics);
				graphics.setBackgroundColor(bg);
				graphics.fillRectangle(getClientArea());
			}

			@Override
			public Dimension getPreferredSize(int wHint, int hHint) {
				return new Dimension(wHint, 100);
			}
		};
	}

	private static class ListLayout extends FlowLayout {
		public ListLayout() {
			setMajorSpacing(0);
		}
	}
}
