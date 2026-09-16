/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
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
package org.eclipse.draw2d.examples.svg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.svg.SVGGraphics;

/**
 * This example shows a side-by-side comparison of the {@link SVGGraphics} and
 * the {@link SWTGraphics} wrt. the different paint methods. These methods are
 * grouped by their type and accessible via different tabs.
 */
public class SVGExample {

	public static void main(String[] args) {
		Shell shell = new Shell();
		shell.setText(Messages.getString("SVGExample.SVG_EXAMPLE")); //$NON-NLS-1$
		shell.setSize(800, 600);

		shell.setLayout(new FillLayout());
		CTabFolder tabFolder = new CTabFolder(shell, SWT.NONE);

		CTabItem tabItem1 = new CTabItem(tabFolder, SWT.NONE);
		tabItem1.setText(Messages.getString("SVGExample.Mandelbrot")); //$NON-NLS-1$
		tabItem1.setControl(new GraphicsMandelbrotComposite(tabFolder, SWT.NONE));

		CTabItem tabItem2 = new CTabItem(tabFolder, SWT.NONE);
		tabItem2.setText(Messages.getString("SVGExample.Shapes")); //$NON-NLS-1$
		tabItem2.setControl(new GraphicsShapesComposite(tabFolder, SWT.NONE));

		CTabItem tabItem3 = new CTabItem(tabFolder, SWT.NONE);
		tabItem3.setText(Messages.getString("SVGExample.Strings")); //$NON-NLS-1$
		tabItem3.setControl(new GraphicsStringsComposite(tabFolder, SWT.NONE));

		CTabItem tabItem4 = new CTabItem(tabFolder, SWT.NONE);
		tabItem4.setText(Messages.getString("SVGExample.Lines")); //$NON-NLS-1$
		tabItem4.setControl(new GraphicsLinesComposite(tabFolder, SWT.NONE));

		CTabItem tabItem5 = new CTabItem(tabFolder, SWT.NONE);
		tabItem5.setText(Messages.getString("SVGExample.Transform")); //$NON-NLS-1$
		tabItem5.setControl(new GraphicsTransformComposite(tabFolder, SWT.NONE));

		CTabItem tabItem6 = new CTabItem(tabFolder, SWT.NONE);
		tabItem6.setText(Messages.getString("SVGExample.Alpha")); //$NON-NLS-1$
		tabItem6.setControl(new GraphicsAlphaComposite(tabFolder, SWT.NONE));

		CTabItem tabItem7 = new CTabItem(tabFolder, SWT.NONE);
		tabItem7.setText(Messages.getString("SVGExample.Path")); //$NON-NLS-1$
		tabItem7.setControl(new GraphicsPathComposite(tabFolder, SWT.NONE));

		CTabItem tabItem8 = new CTabItem(tabFolder, SWT.NONE);
		tabItem8.setText(Messages.getString("SVGExample.Clipping")); //$NON-NLS-1$
		tabItem8.setControl(new GraphicsClippingComposite(tabFolder, SWT.NONE));

		shell.open();

		Display d = shell.getDisplay();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

}
