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

package org.eclipse.draw2d.examples.printing;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.ImagePrintFigureOperation;
import org.eclipse.draw2d.ScalableLightweightSystem;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;

/**
 * This example shows how to use the {@link ImagePrintFigureOperation}. The
 * image automatically adjust
 */
public class ImagePrintExample {

	public static void main(String[] args) {
		Shell shell = new Shell();
		shell.setSize(400, 200);
		shell.setLayout(new GridLayout(2, true));

		Label original = new Label(shell, SWT.NONE);
		original.setText("Original:"); //$NON-NLS-1$
		original.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label image = new Label(shell, SWT.NONE);
		image.setText("Image:"); //$NON-NLS-1$
		image.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		TextFlow textFlow = new TextFlow("Hello World"); //$NON-NLS-1$
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow, ParagraphTextLayout.WORD_WRAP_SOFT));

		FlowPage flowPage = new FlowPage();
		flowPage.add(textFlow);

		FigureCanvas canvas = new FigureCanvas(shell, new ScalableLightweightSystem());
		canvas.setContents(flowPage);
		canvas.getLightweightSystem().getUpdateManager().performUpdate();
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		ImagePrintFigureOperation imagePrinter = new ImagePrintFigureOperation(shell.getDisplay(), textFlow);
		Image printResult = imagePrinter.run();

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.addPaintListener(event -> event.gc.drawImage(printResult, 0, 0));

		shell.requestLayout();
		shell.open();

		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		printResult.dispose();
	}

}
