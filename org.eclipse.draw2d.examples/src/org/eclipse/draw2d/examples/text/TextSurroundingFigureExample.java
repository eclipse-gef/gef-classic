/*******************************************************************************
 * Copyright (c) 2005, 2023 IBM Corporation and others.
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
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.Triangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.text.FlowAdapter;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.TextFlow;

public class TextSurroundingFigureExample {

	public static void main(String[] args) {
		Display d = Display.getDefault();
		Shell shell = new Shell(d);
		StackLayout layout = new StackLayout();
		shell.setLayout(layout);

		FigureCanvas canvas = new FigureCanvas(shell);
		layout.topControl = canvas;
		canvas.getViewport().setContentsTracksWidth(true);

		FlowPage page = new FlowPage();
		populatePage(page);
		canvas.setContents(page);

		shell.setSize(300, 300);
		shell.open();
		while (!shell.isDisposed()) {
			if (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

	private static void populatePage(FlowPage page) {
		page.setOrientation(SWT.LEFT_TO_RIGHT);

		TextFlow textFlow1 = new TextFlow();
		textFlow1.setText(
				"This is the text on the left side, and \u0634\u0637\u0635.  There has to be a lot of text here to see the first proxy wrap.  So, here we go."); //$NON-NLS-1$
		page.add(textFlow1);

		FlowAdapter proxy1 = new FlowAdapter();
		proxy1.setBorder(new LineBorder(ColorConstants.orange, 3));
		proxy1.setLayoutManager(new ToolbarLayout());
		RoundedRectangle rect = new RoundedRectangle();
		rect.setCornerDimensions(new Dimension(25, 25));
		rect.setBackgroundColor(ColorConstants.green);
		rect.setForegroundColor(ColorConstants.black);
		rect.setPreferredSize(100, 40);
		proxy1.add(rect);
		Shape shape = new Triangle();
		shape.setBackgroundColor(ColorConstants.red);
		shape.setForegroundColor(ColorConstants.black);
		shape.setPreferredSize(80, 80);
		proxy1.add(shape);
		shape = new Ellipse();
		shape.setBackgroundColor(ColorConstants.blue);
		shape.setForegroundColor(ColorConstants.black);
		shape.setPreferredSize(70, 70);
		proxy1.add(shape);
		page.add(proxy1);

		TextFlow textFlow2 = new TextFlow();
		textFlow2.setText("This is the right side text."); //$NON-NLS-1$
		page.add(textFlow2);

		FlowAdapter proxy2 = new FlowAdapter();
		proxy2.setBorder(proxy1.getBorder());
		proxy2.setLayoutManager(new org.eclipse.draw2d.StackLayout());
		TextFlow flow = new TextFlow();
		flow.setText("Text inside a proxy figure with BiDi: \u0634\u0637\u0635 \u0639\u0633\u0640 \u0632\u0638\u0635."); //$NON-NLS-1$
		FlowPage innerPage = new FlowPage();
		innerPage.setOrientation(SWT.RIGHT_TO_LEFT);
		innerPage.add(flow);
		proxy2.add(innerPage);
		page.add(proxy2);
	}

}