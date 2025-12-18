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
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.examples.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.Triangle;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class ShapeStylesExample {

	static StyleAxis[] styleAxes = { new StyleAxis("Line Width", //$NON-NLS-1$
			new StyleItem[] { new StyleItem(0.0f), new StyleItem(0.25f), new StyleItem(0.5f), new StyleItem(1.0f),
					new StyleItem(2.5f), new StyleItem(5.0f), new StyleItem(10.0f), }) {
		@Override
		public void applyTo(Shape shape, int i) {
			shape.setLineWidthFloat(elements[i].getValue());
		}
	},

			new StyleAxis("Line Style", new StyleItem[] { new StyleItem("SWT.LINE_SOLID", SWT.LINE_SOLID), //$NON-NLS-1$ //$NON-NLS-2$
					new StyleItem("SWT.LINE_DASH", SWT.LINE_DASH), new StyleItem("SWT.LINE_DASHDOT", SWT.LINE_DASHDOT), //$NON-NLS-1$ //$NON-NLS-2$
					new StyleItem("SWT.LINE_DASHDOTDOT", SWT.LINE_DASHDOTDOT), //$NON-NLS-1$
					new StyleItem("SWT.LINE_DOT", SWT.LINE_DOT), }) { //$NON-NLS-1$
				@Override
				public void applyTo(Shape shape, int i) {
					shape.setLineStyle((int) elements[i].getValue());
				}
			},

			new StyleAxis("Line Cap", new StyleItem[] { new StyleItem("SWT.CAP_FLAT", SWT.CAP_FLAT), //$NON-NLS-1$ //$NON-NLS-2$
					new StyleItem("SWT.CAP_ROUND", SWT.CAP_ROUND), new StyleItem("SWT.CAP_SQUARE", SWT.CAP_SQUARE), }) { //$NON-NLS-1$ //$NON-NLS-2$
				@Override
				public void applyTo(Shape shape, int i) {
					shape.setLineCap((int) elements[i].getValue());
				}
			},

			new StyleAxis("Line Miter Limit", //$NON-NLS-1$
					new StyleItem[] { new StyleItem(0.0f), new StyleItem(0.5f), new StyleItem(1.0f),
							new StyleItem(2.0f), new StyleItem(5.0f), new StyleItem(10.0f), new StyleItem(50.0f), }) {
				@Override
				public void applyTo(Shape shape, int i) {
					shape.setLineMiterLimit(elements[i].getValue());
				}
			},

			new StyleAxis("Line Dash Offset", //$NON-NLS-1$
					new StyleItem[] { new StyleItem(0.0f), new StyleItem(0.25f), new StyleItem(0.5f),
							new StyleItem(1.0f), new StyleItem(2.0f), new StyleItem(2.5f), new StyleItem(5.0f),
							new StyleItem(10.0f), }) {
				@Override
				public void applyTo(Shape shape, int i) {
					shape.setLineDashOffset(elements[i].getValue());
				}
			},

			new StyleAxis("Anti-Aliasing", new StyleItem[] { new StyleItem("SWT.ON", SWT.ON), //$NON-NLS-1$ //$NON-NLS-2$
					new StyleItem("SWT.OFF", SWT.OFF), new StyleItem("SWT.DEFAULT", SWT.DEFAULT), }) { //$NON-NLS-1$ //$NON-NLS-2$
				@Override
				public void applyTo(Shape shape, int i) {
					shape.setAntialias((int) elements[i].getValue());
				}
			},

			new StyleAxis("Alpha", new StyleItem[] { new StyleItem(0), new StyleItem(10), new StyleItem(50), //$NON-NLS-1$
					new StyleItem(100), new StyleItem(150), new StyleItem(200), new StyleItem(255), }) {
				@Override
				public void applyTo(Shape shape, int i) {
					shape.setAlpha((int) elements[i].getValue());
				}
			},

			new StyleAxis("Enabled", new StyleItem[] { new StyleItem("true", 1), new StyleItem("false", 0), }) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				@Override
				public void applyTo(Shape shape, int i) {
					int setting = (int) elements[i].getValue();
					shape.setEnabled(setting == 1);
				}
			}, };

	static final String[] sampleShapeClasses = { Ellipse.class.getName(), RectangleFigure.class.getName(),
			RoundedRectangle.class.getName(), Triangle.class.getName(), Polyline.class.getName() };

	static final String[] defaultLineWidths = { "0", "0.5", "1", "2.5", "5", "10", "30" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

	private static Shell shell;
	private static Group styleGrid;

	private static StyleAxis xAxis;
	private static StyleAxis yAxis;
	private static float defaultLineWidth;
	private static Class<?> sampleShape;

	public static void main(String[] args) {

		// create outer shell/window
		Display display = Display.getDefault();
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("Shape Style Example"); //$NON-NLS-1$
		shell.setLayout(new GridLayout(2, false));

		// create control pane
		Group controlPane = new Group(shell, SWT.NONE);
		controlPane.setText("Style Axis Settings"); //$NON-NLS-1$
		controlPane.setLayout(new GridLayout(2, false));
		controlPane.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));

		// x-axis selection
		Label xAxisSelectionLabel = new Label(controlPane, SWT.NONE);
		xAxisSelectionLabel.setText("X Axis"); //$NON-NLS-1$
		final Combo xAxisSelection = new Combo(controlPane, SWT.READ_ONLY);
		for (StyleAxis element : styleAxes) {
			xAxisSelection.add(element.getName());
		}
		xAxisSelection.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				xAxis = styleAxes[xAxisSelection.getSelectionIndex()];
				refreshGrid();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// y-axis selection
		Label yAxisSelectionLabel = new Label(controlPane, SWT.NONE);
		yAxisSelectionLabel.setText("Y Axis"); //$NON-NLS-1$
		final Combo yAxisSelection = new Combo(controlPane, SWT.READ_ONLY);
		for (StyleAxis element : styleAxes) {
			yAxisSelection.add(element.getName());
		}
		yAxisSelection.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				yAxis = styleAxes[yAxisSelection.getSelectionIndex()];
				refreshGrid();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// shape selection
		Label shapeSelectionLabel = new Label(controlPane, SWT.NONE);
		shapeSelectionLabel.setText("Shape"); //$NON-NLS-1$
		final Combo shapeSelection = new Combo(controlPane, SWT.NONE);
		shapeSelection.setItems(sampleShapeClasses);
		shapeSelection.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					sampleShape = Class.forName(shapeSelection.getText());
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				refreshGrid();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// default line width selection
		Label lineWidthSelectionLabel = new Label(controlPane, SWT.NONE);
		lineWidthSelectionLabel.setText("Line Width"); //$NON-NLS-1$
		final Combo lineWidthSelection = new Combo(controlPane, SWT.NONE);
		lineWidthSelection.setItems(defaultLineWidths);
		lineWidthSelection.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				defaultLineWidth = Float.parseFloat(lineWidthSelection.getText());
				refreshGrid();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// set defaults
		xAxis = styleAxes[0];
		yAxis = styleAxes[1];
		defaultLineWidth = Float.parseFloat(defaultLineWidths[4]);
		try {
			sampleShape = Class.forName(sampleShapeClasses[0]);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		xAxisSelection.select(0);
		yAxisSelection.select(1);
		lineWidthSelection.select(4);
		shapeSelection.select(0);

		// display grid
		refreshGrid();

		shell.open();
		while (!shell.isDisposed()) {
			while (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	static void refreshGrid() {
		if (styleGrid != null) {
			styleGrid.dispose();
			shell.pack();
		}

		styleGrid = new Group(shell, SWT.NONE);
		styleGrid.setText(xAxis.getName() + " vs. " + yAxis.getName()); //$NON-NLS-1$
		styleGrid.setLayout(new GridLayout(xAxis.getCount() + 1, false));
		styleGrid.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1));

		// put blank label in top left corner
		Label label = new Label(styleGrid, SWT.NONE);

		// add x-axis labels
		for (int x = 0; x < xAxis.getCount(); x++) {
			label = new Label(styleGrid, SWT.NONE);
			label.setText(xAxis.getName() + " =\n" + xAxis.getAt(x).getName()); //$NON-NLS-1$
		}

		// iterate over the y-axis style settings
		for (int y = 1; y < (yAxis.getCount() + 1); y++) {

			// add y-axis label for this row
			label = new Label(styleGrid, SWT.NONE);
			label.setText(yAxis.getName() + " =\n" + yAxis.getAt(y - 1).getName()); //$NON-NLS-1$

			// iterate over the x-axis style settings
			for (int x = 1; x < (xAxis.getCount() + 1); x++) {

				// create a sample shape instance
				Shape shape;
				if (sampleShape == Polyline.class) {
					Polyline poly = new Polyline();
					poly.addPoint(new Point(20, 20));
					poly.addPoint(new Point(50, 80));
					poly.addPoint(new Point(10, 50));
					shape = poly;
				} else {
					try {
						shape = (Shape) sampleShape.getConstructor().newInstance();
					} catch (Exception e) {
						throw new RuntimeException("Could not find a no args constructor for " + sampleShape.getName()); //$NON-NLS-1$
					}
					shape.setBounds(new Rectangle(0, 0, 100, 75));
				}

				// apply default style
				shape.setBackgroundColor(ColorConstants.orange);
				shape.setLineWidthFloat(defaultLineWidth);
				shape.setAntialias(SWT.ON);

				// apply styles imposed by each axis
				xAxis.applyTo(shape, x - 1);
				yAxis.applyTo(shape, y - 1);

				FigureCanvas figureBox = new FigureCanvas(styleGrid);
				figureBox.setContents(shape);
			}
		}
		shell.pack();
	}

	abstract static class StyleAxis {
		private final String name;
		protected StyleItem[] elements;

		protected StyleAxis(String name, StyleItem[] elements) {
			this.name = name;
			this.elements = elements;
		}

		public int getCount() {
			return elements.length;
		}

		public StyleItem getAt(int i) {
			return elements[i];
		}

		public String getName() {
			return name;
		}

		public abstract void applyTo(Shape shape, int i);
	}

	static class StyleItem {
		private final String name;
		private final float value;

		public StyleItem(float value) {
			this.name = Float.toString(value);
			this.value = value;
		}

		public StyleItem(String name, float value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public float getValue() {
			return value;
		}
	}
}
