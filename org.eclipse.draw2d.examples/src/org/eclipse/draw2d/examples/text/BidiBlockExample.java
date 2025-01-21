/*******************************************************************************
 * Copyright (c) 2004, 2023 IBM Corporation and others.
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
package org.eclipse.draw2d.examples.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.text.BlockFlow;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.TextFlow;

public class BidiBlockExample {

	public static void main(String[] args) {
		new BidiBlockExample().run();
	}

	private FigureCanvas fc;
	protected Shell shell;

	// The backwards figure canvas for bidi
	protected FigureCanvas cf;
	protected String s = "\u0634\u0635\u062c\u062d \u0630\u0628\u063a and some english text."; //$NON-NLS-1$

	protected IFigure createContents() {
		FlowPage page = new FlowPage();

		BlockFlow para = new BlockFlow();
		page.add(para);
		para.setOrientation(SWT.LEFT_TO_RIGHT);
		para.setHorizontalAligment(PositionConstants.LEFT);
		para.add(new TextFlow(s));

		para = new BlockFlow();
		page.add(para);
		para.setOrientation(SWT.LEFT_TO_RIGHT);
		para.setHorizontalAligment(PositionConstants.RIGHT);
		para.add(new TextFlow(s));

		para = new BlockFlow();
		page.add(para);
		para.setOrientation(SWT.LEFT_TO_RIGHT);
		para.setHorizontalAligment(PositionConstants.ALWAYS_LEFT);
		para.add(new TextFlow(s));

		para = new BlockFlow();
		page.add(para);
		para.setOrientation(SWT.LEFT_TO_RIGHT);
		para.setHorizontalAligment(PositionConstants.ALWAYS_RIGHT);
		para.add(new TextFlow(s));

		para = new BlockFlow();
		page.add(para);
		para.setOrientation(SWT.LEFT_TO_RIGHT);
		para.add(new TextFlow(s));

		para = new BlockFlow();
		page.add(para);
		para.setOrientation(SWT.RIGHT_TO_LEFT);
		para.setHorizontalAligment(PositionConstants.LEFT);
		para.add(new TextFlow(s));

		para = new BlockFlow();
		page.add(para);
		para.setOrientation(SWT.RIGHT_TO_LEFT);
		para.setHorizontalAligment(PositionConstants.RIGHT);
		para.add(new TextFlow(s));

		para = new BlockFlow();
		page.add(para);
		para.setOrientation(SWT.RIGHT_TO_LEFT);
		para.setHorizontalAligment(PositionConstants.ALWAYS_LEFT);
		para.add(new TextFlow(s));

		para = new BlockFlow();
		page.add(para);
		para.setOrientation(SWT.RIGHT_TO_LEFT);
		para.setHorizontalAligment(PositionConstants.ALWAYS_RIGHT);
		para.add(new TextFlow(s));

		para = new BlockFlow();
		page.add(para);
		para.setOrientation(SWT.RIGHT_TO_LEFT);
		para.add(new TextFlow(s));

		return page;
	}

	protected void run() {
		Display d = Display.getDefault();
		shell = new Shell(d);
		String appName = getClass().getName();
		appName = appName.substring(appName.lastIndexOf('.') + 1);
		shell.setText(appName);
		shell.setLayout(new GridLayout(2, true));

		fc = new FigureCanvas(shell);
		fc.setContents(createContents());
		cf = new FigureCanvas(shell, SWT.RIGHT_TO_LEFT);
		cf.setContents(createContents());

		fc.getViewport().setContentsTracksWidth(true);
		cf.getViewport().setContentsTracksWidth(true);

		fc.setLayoutData(new GridData(GridData.FILL_BOTH));
		cf.setLayoutData(new GridData(GridData.FILL_BOTH));

		cf.getViewport().setVerticalRangeModel(fc.getViewport().getVerticalRangeModel());

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

}