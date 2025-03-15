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
package org.eclipse.draw2d.examples.printing;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PrintFigureOperation;
import org.eclipse.draw2d.PrintOperation;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;

/**
 * @author danlee
 */
public class PrintExample {

	static FigureCanvas canvas;
	static Figure printFigure;
	static Figure internalPrintFigure;
	static int count = 1;

	public static void main(String[] args) {
		Display d = Display.getDefault();
		Shell shell = new Shell(d);

		shell.setLayout(new GridLayout(1, false));

		Button printButton = new Button(shell, SWT.PUSH);
		printButton.setText("Print it"); //$NON-NLS-1$
		printButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		GridData canvasData = new GridData(GridData.FILL_BOTH);
		canvasData.horizontalSpan = 2;
		canvas = new FigureCanvas(shell);
		canvas.setLayoutData(canvasData);

		LightweightSystem lws = new LightweightSystem(canvas);

		final Figure printFigure = new Figure();
		printFigure.setMinimumSize(new Dimension(5, 5));
		addAllFigures(printFigure);
		lws.setContents(printFigure);

		printButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				printIt(printFigure, shell);
			}
		});
		shell.setSize(300, 500);
		shell.open();

		while (!shell.isDisposed()) {
			if (!d.readAndDispatch()) {
				d.sleep();
			}
		}

	}

	static void addAllFigures(IFigure parent) {
		Font fonts[] = { new Font(null, "Helvetica", 16, SWT.NONE), new Font(null, "Times New Roman", 24, SWT.BOLD), //$NON-NLS-1$ //$NON-NLS-2$
				new Font(null, "Perpetua", 48, SWT.NONE) }; //$NON-NLS-1$

		parent.setLayoutManager(new FlowLayout());
		org.eclipse.draw2d.Button button = new org.eclipse.draw2d.Button("Button"); //$NON-NLS-1$
		RectangleFigure rf = new RectangleFigure();
		rf.setSize(50, 50);
		Ellipse e = new Ellipse();
		e.setSize(50, 50);
		RoundedRectangle rr = new RoundedRectangle();
		rr.setSize(50, 50);

		for (Font font : fonts) {
			org.eclipse.draw2d.Label label = new org.eclipse.draw2d.Label(font.getFontData()[0].getName());
			label.setFont(font);
			label.setBorder(new LineBorder());
			parent.add(label);
		}
		Figure rect1 = new RectangleFigure();
		rect1.setSize(50, 50);
		Figure rect2 = new RectangleFigure();
		rect2.setSize(50, 50);

		PolylineConnection c = new PolylineConnection();
		ChopboxAnchor chop = new ChopboxAnchor(rect1);
		c.setSourceAnchor(chop);
		c.setTargetAnchor(new ChopboxAnchor(rect2));

		Label icon = new Label(new Image(null, PrintExample.class.getResourceAsStream("brazil.ico"))); //$NON-NLS-1$

		parent.add(rf);
		parent.add(e);
		parent.add(rr);
		parent.add(button);
		parent.add(rect1);
		parent.add(rect2);
		parent.add(icon);
		parent.add(c);
	}

	static private void printIt(IFigure fig, Shell shell) {
		PrintDialog dialog = new PrintDialog(shell);
		PrinterData pd = dialog.open();
		if (pd == null) {
			return;
		}
		Printer p = new Printer(pd);
		PrintOperation op = new PrintFigureOperation(p, fig);
		op.setPrintMargin(new Insets(0, 0, 0, 0));
		op.run("Test"); // "Test" is the print job name //$NON-NLS-1$
		p.dispose();
	}

}