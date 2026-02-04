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

import java.beans.PropertyChangeSupport;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * This page paints a rectangle. The user can customize the colors, whether the
 * rectangle is filled and has round corners.
 */
public class GraphicsShapePage extends AbstractGraphicsPage {
	private static final String PROP_BG_COLOR = "bg-color"; //$NON-NLS-1$
	private static final String PROP_FG_COLOR = "fg-color"; //$NON-NLS-1$
	private static final String PROP_ROUND = "text"; //$NON-NLS-1$
	private static final String PROP_FILL = "fill"; //$NON-NLS-1$
	private static final String PROP_SHAPE = "shape"; //$NON-NLS-1$
	private static final String PROP_VERTICAL = "vertical"; //$NON-NLS-1$
	private PropertyChangeSupport propertyChangeSupport;

	private Color bgColor;
	private Color fgColor;
	private boolean round;
	private boolean fill;
	private boolean vertical;
	private Shape shape;

	public GraphicsShapePage(Composite parent, int style) throws Exception {
		super(parent, style);
		setAppliedBackground(ColorConstants.blue);
		setAppliedForeground(getForeground());
		setAppliedRound(false);
		setAppliedFill(true);
		setAppliedShape(Shape.RECTANGLE);
		setAppliedVertical(false);
	}

	@Override
	protected Control createControlPanel() {
		propertyChangeSupport = new PropertyChangeSupport(this);

		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.pack = false;

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(layout);

		createShapePanel(composite);
		createBackgroundColorPanel(composite);
		createForegroundColorPanel(composite);
		createRoundCheckPanel(composite);
		createFillCheckPanel(composite);
		createVerticalCheckPanel(composite);

		return composite;
	}

	private void createShapePanel(Composite parent) {
		String[] items = Arrays.stream(Shape.values()).map(Shape::getDisplayName).toArray(String[]::new);

		Combo combo = new Combo(parent, SWT.NONE);
		combo.setItems(items);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = ((Combo) e.widget).getSelectionIndex();
				setAppliedShape(Shape.values()[index]);
			}
		});
		propertyChangeSupport.addPropertyChangeListener(event -> {
			if (PROP_SHAPE.equals(event.getPropertyName())) {
				Shape shape = (Shape) event.getNewValue();
				for (int i = 0; i < items.length; ++i) {
					if (items[i].equals(shape.getDisplayName())) {
						combo.select(i);
						break;
					}
				}
			}
		});
	}

	private void createBackgroundColorPanel(Composite parent) {
		Button button = new Button(parent, SWT.NONE);
		button.setText(Messages.getString("GraphicsShapePage.BUTTON_BG_COLOR_LABEL")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog dialog = new ColorDialog(Display.getCurrent().getActiveShell());
				RGB rgb = dialog.open();
				if (rgb == null) {
					return;
				}
				setAppliedBackground(new Color(rgb));
			}
		});

		Label label = new Label(parent, SWT.BORDER);
		propertyChangeSupport.addPropertyChangeListener(event -> {
			if (PROP_FILL.equals(event.getPropertyName())) {
				button.setEnabled((boolean) event.getNewValue());
			}
			// BG color is ignored if fill is disabled
			if (PROP_SHAPE.equals(event.getPropertyName())) {
				Shape shape = (Shape) event.getNewValue();
				button.setEnabled((fill && shape.fillSupported()) || shape.verticalSupported());
			}
			if (PROP_BG_COLOR.equals(event.getPropertyName())) {
				label.setBackground((Color) event.getNewValue());
			}
		});
	}

	private void createForegroundColorPanel(Composite parent) {
		Button button = new Button(parent, SWT.NONE);
		button.setText(Messages.getString("GraphicsShapePage.BUTTON_FG_COLOR_LABEL")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog dialog = new ColorDialog(Display.getCurrent().getActiveShell());
				RGB rgb = dialog.open();
				if (rgb == null) {
					return;
				}
				setAppliedForeground(new Color(rgb));
			}
		});

		Label label = new Label(parent, SWT.BORDER);
		propertyChangeSupport.addPropertyChangeListener(event -> {
			if (PROP_FILL.equals(event.getPropertyName())) {
				button.setEnabled(!(boolean) event.getNewValue());
			}
			if (PROP_SHAPE.equals(event.getPropertyName())) {
				// FG color is ignored if fill is enabled
				Shape shape = (Shape) event.getNewValue();
				button.setEnabled(!fill || !shape.fillSupported() || shape.verticalSupported());
			}
			if (PROP_FG_COLOR.equals(event.getPropertyName())) {
				label.setBackground((Color) event.getNewValue());
			}
		});
	}

	private void createRoundCheckPanel(Composite parent) {
		Button button = new Button(parent, SWT.CHECK);
		button.setText(Messages.getString("GraphicsShapePage.BUTTON_ROUND_LABEL")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAppliedRound(((Button) (e.widget)).getSelection());
			}
		});
		propertyChangeSupport.addPropertyChangeListener(event -> {
			if (PROP_SHAPE.equals(event.getPropertyName())) {
				button.setEnabled(((Shape) event.getNewValue()).roundSupported());
			}
			if (PROP_ROUND.equals(event.getPropertyName())) {
				button.setSelection((boolean) event.getNewValue());
			}
		});
	}

	private void createFillCheckPanel(Composite parent) {
		Button button = new Button(parent, SWT.CHECK);
		button.setText(Messages.getString("GraphicsShapePage.BUTTON_FILL_LABEL")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAppliedFill(((Button) (e.widget)).getSelection());
			}
		});
		propertyChangeSupport.addPropertyChangeListener(event -> {
			if (PROP_SHAPE.equals(event.getPropertyName())) {
				button.setEnabled(((Shape) event.getNewValue()).fillSupported());
			}
			if (PROP_FILL.equals(event.getPropertyName())) {
				button.setSelection((boolean) event.getNewValue());
			}
		});
	}

	private void createVerticalCheckPanel(Composite parent) {
		Button button = new Button(parent, SWT.CHECK);
		button.setText(Messages.getString("GraphicsShapePage.BUTTON_VERTICAL_LABEL")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAppliedVertical(((Button) (e.widget)).getSelection());
			}
		});
		propertyChangeSupport.addPropertyChangeListener(event -> {
			if (PROP_SHAPE.equals(event.getPropertyName())) {
				button.setEnabled(((Shape) event.getNewValue()).verticalSupported());
			}
			if (PROP_VERTICAL.equals(event.getPropertyName())) {
				button.setSelection((boolean) event.getNewValue());
			}
		});
	}

	private void setAppliedForeground(Color newColor) {
		Color oldColor = fgColor;
		fgColor = newColor;
		propertyChangeSupport.firePropertyChange(PROP_FG_COLOR, oldColor, newColor);
		redraw();
	}

	private void setAppliedBackground(Color newColor) {
		Color oldColor = bgColor;
		bgColor = newColor;
		propertyChangeSupport.firePropertyChange(PROP_BG_COLOR, oldColor, newColor);
		redraw();
	}

	private void setAppliedRound(boolean newRound) {
		boolean oldRound = round;
		round = newRound;
		propertyChangeSupport.firePropertyChange(PROP_ROUND, oldRound, newRound);
		redraw();
	}

	private void setAppliedFill(boolean newFill) {
		boolean oldFill = fill;
		fill = newFill;
		propertyChangeSupport.firePropertyChange(PROP_FILL, oldFill, newFill);
		redraw();
	}

	private void setAppliedVertical(boolean newVertical) {
		boolean oldVertical = vertical;
		vertical = newVertical;
		propertyChangeSupport.firePropertyChange(PROP_VERTICAL, oldVertical, newVertical);
		redraw();
	}

	private void setAppliedShape(Shape newShape) {
		Shape oldShape = shape;
		shape = newShape;
		propertyChangeSupport.firePropertyChange(PROP_SHAPE, oldShape, newShape);
		redraw();
	}

	@Override
	protected void paint(Graphics g) {
		g.setBackgroundColor(bgColor);
		g.setForegroundColor(fgColor);
		switch (shape) {
		case RECTANGLE -> paintRectangle(g);
		case ARC -> paintArc(g);
		case OVAL -> paintOval(g);
		case LINE -> paintLine(g);
		case POLYGON -> paintPolygon(g);
		case POLYLINE -> paintPolyline(g);
		case GRADIENT -> paintGradient(g);
		default -> throw new IllegalArgumentException("Unknown shape: " + shape); //$NON-NLS-1$
		}
	}

	private void paintRectangle(Graphics g) {
		Rectangle r = new Rectangle(0, 0, 100, 100);
		if (round) {
			if (fill) {
				g.fillRoundRectangle(r, 60, 40);
			} else {
				g.drawRoundRectangle(r, 60, 40);
			}
		} else {
			if (fill) {
				g.fillRectangle(r);
			} else {
				g.drawRectangle(r);
			}
		}
	}

	private void paintArc(Graphics g) {
		Rectangle r = new Rectangle(0, 0, 100, 100);
		if (fill) {
			g.fillArc(r, 0, 90);
		} else {
			g.drawArc(r, 0, 90);
		}
	}

	private void paintOval(Graphics g) {
		Rectangle r = new Rectangle(0, 0, 100, 100);
		if (fill) {
			g.fillOval(r);
		} else {
			g.drawOval(r);
		}
	}

	private static void paintLine(Graphics g) {
		g.drawLine(0, 0, 100, 100);
	}

	private void paintPolygon(Graphics g) {
		int[] points = { 10, 15, 37, 39, 91, 91, 44, 34 };
		if (fill) {
			g.fillPolygon(points);
		} else {
			g.drawPolygon(points);
		}
	}

	private static void paintPolyline(Graphics g) {
		int[] points = { 10, 15, 37, 39, 91, 91, 44, 34 };
		g.drawPolyline(points);
	}

	private void paintGradient(Graphics g) {
		g.fillGradient(0, 0, 100, 100, vertical);
	}

	private static enum Shape {
		RECTANGLE(Messages.getString("GraphicsShapePage.SHAPE_RECTANGLE"), true, true, false), //$NON-NLS-1$
		ARC(Messages.getString("GraphicsShapePage.SHAPE_ARC"), true, false, false), //$NON-NLS-1$
		OVAL(Messages.getString("GraphicsShapePage.SHAPE_OVAL"), true, false, false), //$NON-NLS-1$
		LINE(Messages.getString("GraphicsShapePage.SHAPE_LINE"), false, false, false), //$NON-NLS-1$ 1$
		POLYGON(Messages.getString("GraphicsShapePage.SHAPE_POLYGON"), true, false, false), //$NON-NLS-1$
		POLYLINE(Messages.getString("GraphicsShapePage.SHAPE_POLYLINE"), false, false, false), //$NON-NLS-1$
		GRADIENT(Messages.getString("GraphicsShapePage.SHAPE_GRADIENT"), false, false, true); //$NON-NLS-1$

		private final String displayName;
		private final boolean fillSupported;
		private final boolean roundSupported;
		private final boolean verticalSupported;

		private Shape(String displayName, boolean fillSupported, boolean roundSupported, boolean verticalSupported) {
			this.displayName = displayName;
			this.fillSupported = fillSupported;
			this.roundSupported = roundSupported;
			this.verticalSupported = verticalSupported;
		}

		public boolean fillSupported() {
			return fillSupported;
		}

		public boolean roundSupported() {
			return roundSupported;
		}

		public boolean verticalSupported() {
			return verticalSupported;
		}

		public String getDisplayName() {
			return displayName;
		}
	}
}
