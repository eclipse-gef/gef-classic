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
public class GraphicsRectanglePage extends AbstractGraphicsPage {
	private static final String PROP_BG_COLOR = "bg-color"; //$NON-NLS-1$
	private static final String PROP_FG_COLOR = "fg-color"; //$NON-NLS-1$
	private static final String PROP_ROUND = "text"; //$NON-NLS-1$
	private static final String PROP_FILL = "fill"; //$NON-NLS-1$
	private static final String PROP_SHAPE = "shape"; //$NON-NLS-1$
	private PropertyChangeSupport propertyChangeSupport;

	private Color bgColor;
	private Color fgColor;
	private boolean round;
	private boolean fill;
	private Shape shape;

	public GraphicsRectanglePage(Composite parent, int style) throws Exception {
		super(parent, style);
		setAppliedBackground(ColorConstants.blue);
		setAppliedForeground(getForeground());
		setAppliedRound(false);
		setAppliedFill(true);
		setAppliedShape(Shape.RECTANGLE);
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
		button.setText(Messages.getString("GraphicsRectanglePage.BUTTON_BG_COLOR_LABEL")); //$NON-NLS-1$
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
			if (PROP_SHAPE.equals(event.getPropertyName())) {
				button.setEnabled(fill && !Shape.LINE.equals(event.getNewValue()));
			}
			if (PROP_BG_COLOR.equals(event.getPropertyName())) {
				label.setBackground((Color) event.getNewValue());
			}
		});
	}

	private void createForegroundColorPanel(Composite parent) {
		Button button = new Button(parent, SWT.NONE);
		button.setText(Messages.getString("GraphicsRectanglePage.BUTTON_FG_COLOR_LABEL")); //$NON-NLS-1$
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
				button.setEnabled(!fill || Shape.LINE.equals(event.getNewValue()));
			}
			if (PROP_FG_COLOR.equals(event.getPropertyName())) {
				label.setBackground((Color) event.getNewValue());
			}
		});
	}

	private void createRoundCheckPanel(Composite parent) {
		Button button = new Button(parent, SWT.CHECK);
		button.setText(Messages.getString("GraphicsRectanglePage.BUTTON_ROUND_LABEL")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAppliedRound(((Button) (e.widget)).getSelection());
			}
		});
		propertyChangeSupport.addPropertyChangeListener(event -> {
			if (PROP_SHAPE.equals(event.getPropertyName())) {
				button.setEnabled(Shape.RECTANGLE.equals(event.getNewValue()));
			}
			if (PROP_ROUND.equals(event.getPropertyName())) {
				button.setSelection((boolean) event.getNewValue());
			}
		});
	}

	private void createFillCheckPanel(Composite parent) {
		Button button = new Button(parent, SWT.CHECK);
		button.setText(Messages.getString("GraphicsRectanglePage.BUTTON_FILL_LABEL")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAppliedFill(((Button) (e.widget)).getSelection());
			}
		});
		propertyChangeSupport.addPropertyChangeListener(event -> {
			if (PROP_SHAPE.equals(event.getPropertyName())) {
				button.setEnabled(!Shape.LINE.equals(event.getNewValue()));
			}
			if (PROP_FILL.equals(event.getPropertyName())) {
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

	private static enum Shape {
		RECTANGLE(Messages.getString("GraphicsRectanglePage.SHAPE_RECTANGLE")), //$NON-NLS-1$
		ARC(Messages.getString("GraphicsRectanglePage.SHAPE_ARC")), //$NON-NLS-1$
		OVAL(Messages.getString("GraphicsRectanglePage.SHAPE_OVAL")), //$NON-NLS-1$
		LINE(Messages.getString("GraphicsRectanglePage.SHAPE_LINE")); //$NON-NLS-1$

		private final String displayName;

		private Shape(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}
	}
}
