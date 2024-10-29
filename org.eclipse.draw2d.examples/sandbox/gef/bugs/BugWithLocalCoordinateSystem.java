/*******************************************************************************
 * Copyright (c) 2008, 2024 Manuel Selva and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Manuel Selva - initial API and implementation
 *******************************************************************************/
package gef.bugs;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Sample class showing a bug when using local coordinate system. Launch the
 * main and click on the blue label => The yellow rectangle is notified with a
 * MousePressed event.
 *
 * @author Manuel Selva
 * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=226103">[1]
 *      here</a>
 * @see <a href="https://github.com/eclipse-gef/gef-classic/issues/495">[2] here</a>
 */
public class BugWithLocalCoordinateSystem {

	private static Shell shell;

	/**
	 * RectangleFigure using local coordinate system.
	 *
	 * @author Manuel Selva
	 */
	private class LocalCoordinateRectangle extends RectangleFigure {

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
		 */
		@Override
		protected boolean useLocalCoordinates() {
			return true;
		}

	}

	/**
	 * Opens a shell showing the bug.
	 */
	public BugWithLocalCoordinateSystem() {

		// Creates display and shell
		shell = new Shell();
		Display display = shell.getDisplay();
		shell.setLayout(new FillLayout());

		// Creates the canvas
		FigureCanvas canvas = new FigureCanvas(shell);
		Panel panel = new Panel();
		panel.setLayoutManager(new XYLayout());

		// Creates the canvas content
		RectangleFigure rect = new LocalCoordinateRectangle();
		rect.setBackgroundColor(ColorConstants.red);
		rect.setLayoutManager(new XYLayout());
		panel.add(rect, new Rectangle(5, 5, 250, 100));

		RectangleFigure child = new RectangleFigure();
		child.setBackgroundColor(ColorConstants.yellow);
		rect.add(child, new Rectangle(10, 10, 100, 30));
		child.setLineWidth(0);
		child.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClicked(MouseEvent me) {
				//
			}

			@Override
			public void mousePressed(MouseEvent me) {
				System.out.println(me);
				IFigure source = (IFigure) me.getSource();
				if (source.getBorder() == null) {
					source.setBorder(new LineBorder(5));
				} else {
					source.setBorder(null);
				}
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				//
			}
		});

		RectangleFigure rect2 = new LocalCoordinateRectangle();
		rect2.setBackgroundColor(ColorConstants.orange);
		rect2.setLayoutManager(new XYLayout());
		panel.add(rect2, new Rectangle(5, 110, 250, 100));

		Label label = new Label("Click Here");
		label.setBackgroundColor(ColorConstants.blue);
		label.setOpaque(true);
		label.setForegroundColor(ColorConstants.white);
		rect2.add(label, new Rectangle(10, 10, 100, 30));

		canvas.setContents(panel);

		// Opens the shell and start UI loop
		shell.pack();
		shell.setText("Local Coordinates");
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Main method.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		new BugWithLocalCoordinateSystem();
	}
}
