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

package gef.bugs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImagePrintFigureOperation;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * This example shows the quality of an {@link Image} created via the
 * {@link ImagePrintFigureOperation}, as opposed to the {@link SWTGraphics}.
 * Note that the rectangle is blurry, even if at 100% zoom, when translating the
 * graphics object by a fractional value while anti-aliasing is enabled.
 *
 * @see <a href="https://github.com/eclipse-gef/gef-classic/issues/1146">Issue
 *      1146</a>
 */
public class Issue1146 {
	private static float zoom = 1.0f;
	private static Image image1;
	private static Image image2;

	public static void main(String[] args) {
		Shell shell = new Shell();
		shell.setSize(400, 200);
		shell.setLayout(new GridLayout(2, true));
		shell.setText("Issue 1146"); //$NON-NLS-1$

		Slider slider = new Slider(shell, SWT.NONE);
		slider.setValues(100, 100, 800, 50, 50, 100);
		slider.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		IFigure printSource = new Figure() {
			@Override
			public void paintFigure(Graphics g) {
				Rectangle rect = getBounds().getCopy().resize(-1, -1);
				g.translate(0.5f, 0.5f);
				g.setAntialias(SWT.ON);
				g.setBackgroundColor(ColorConstants.lightBlue);
				g.fillRectangle(rect);
				g.setBackgroundColor(ColorConstants.darkBlue);
				g.drawRectangle(rect);
			}
		};
		printSource.setSize(100, 100);

		Label image = new Label(shell, SWT.NONE);
		image.setText("Image:"); //$NON-NLS-1$
		image.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label imagePrinter = new Label(shell, SWT.NONE);
		imagePrinter.setText("ImagePrinter:"); //$NON-NLS-1$
		imagePrinter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		image1 = printImageViaGraphics(shell, printSource);
		image2 = printImageViaPrintOperation(shell, printSource);

		IFigure figure1 = new Figure() {
			@Override
			public void paint(Graphics g) {
				g.setInterpolation(SWT.NONE);
				g.drawImage(image1, 0, 0);
			}
		};
		ScalableLayeredPane layer1 = new ScalableLayeredPane();
		layer1.add(figure1);
		FigureCanvas canvas1 = new FigureCanvas(shell, SWT.NONE);
		canvas1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		canvas1.setContents(layer1);

		IFigure figure2 = new Figure() {
			@Override
			public void paint(Graphics g) {
				g.setInterpolation(SWT.NONE);
				g.drawImage(image2, 0, 0);
			}
		};
		ScalableLayeredPane layer2 = new ScalableLayeredPane();
		layer2.add(figure2);
		FigureCanvas canvas2 = new FigureCanvas(shell, SWT.NONE);
		canvas2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		canvas2.setContents(layer2);

		slider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				zoom = ((Slider) e.widget).getSelection() / 100.0f;
				image1 = printImageViaGraphics(shell, printSource);
				image2 = printImageViaPrintOperation(shell, printSource);
				layer1.setScale(zoom);
				layer2.setScale(zoom);
				canvas1.redraw();
				canvas2.redraw();
			}
		});

		shell.requestLayout();
		shell.open();

		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private static Image printImageViaGraphics(Shell shell, IFigure figure) {
		Dimension size = figure.getSize();
		Image image = new Image(shell.getDisplay(), size.width, size.height);

		GC gc = new GC(image);
		SWTGraphics graphics = new SWTGraphics(gc);
		figure.paint(graphics);
		graphics.dispose();
		gc.dispose();

		return image;
	}

	private static Image printImageViaPrintOperation(Shell shell, IFigure figure) {
		ImagePrintFigureOperation imagePrintOperation = new ImagePrintFigureOperation(shell, figure);
		return imagePrintOperation.run();
	}
}
