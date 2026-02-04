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

public class SVGExample {

	public static void main(String[] args) throws Exception {
		Shell shell = new Shell();
		shell.setText(Messages.getString("SVGExample.SHELL_TITLE")); //$NON-NLS-1$
		shell.setSize(800, 800);

		shell.setLayout(new FillLayout());
		CTabFolder tabFolder = new CTabFolder(shell, SWT.NONE);

		CTabItem tabItem1 = new CTabItem(tabFolder, SWT.NONE);
		tabItem1.setText(Messages.getString("SVGExample.TAB_FONT_LABEL")); //$NON-NLS-1$
		tabItem1.setControl(new GraphicsFontPage(tabFolder, SWT.NONE));

		CTabItem tabItem2 = new CTabItem(tabFolder, SWT.NONE);
		tabItem2.setText(Messages.getString("SVGExample.TAB_SHAPES_LABEL")); //$NON-NLS-1$
		tabItem2.setControl(new GraphicsShapePage(tabFolder, SWT.NONE));

		CTabItem tabItem3 = new CTabItem(tabFolder, SWT.NONE);
		tabItem3.setText(Messages.getString("SVGExample.TAB_TRANSFORM_LABEL")); //$NON-NLS-1$
		tabItem3.setControl(new GraphicsTransformPage(tabFolder, SWT.NONE));

		shell.open();

		Display d = shell.getDisplay();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

}
