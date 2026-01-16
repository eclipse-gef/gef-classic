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
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;

public class GraphicsTransformPage extends AbstractGraphicsPage {
	private static final String PROP_SCALE_X = "scale-x"; //$NON-NLS-1$
	private static final String PROP_SCALE_Y = "scale-y"; //$NON-NLS-1$
	private static final String PROP_TRANSLATE_X = "translate-x"; //$NON-NLS-1$
	private static final String PROP_TRANSLATE_Y = "translate-y"; //$NON-NLS-1$
	private PropertyChangeSupport propertyChangeSupport;

	private int x;
	private int y;
	private float scaleX;
	private float scaleY;

	public GraphicsTransformPage(Composite parent, int style) throws Exception {
		super(parent, style);
		setAppliedX(0);
		setAppliedY(0);
		setAppliedScaleX(1.0f);
		setAppliedScaleY(1.0f);
	}

	@Override
	protected Control createControlPanel() {
		propertyChangeSupport = new PropertyChangeSupport(this);

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		createIntPanel(composite, Messages.getString("GraphicsTransformPage.TRANSLATE_X_LABEL"), PROP_TRANSLATE_X, //$NON-NLS-1$
				this::setAppliedX);
		createIntPanel(composite, Messages.getString("GraphicsTransformPage.TRANSLATE_Y_LABEL"), PROP_TRANSLATE_Y, //$NON-NLS-1$
				this::setAppliedY);
		createFloatPanel(composite, Messages.getString("GraphicsTransformPage.SCALE_X_LABEL"), PROP_SCALE_X, //$NON-NLS-1$
				this::setAppliedScaleX);
		createFloatPanel(composite, Messages.getString("GraphicsTransformPage.SCALE_Y_LABEL"), PROP_SCALE_Y, //$NON-NLS-1$
				this::setAppliedScaleY);

		return composite;
	}

	private void createIntPanel(Composite parent, String title, String prop, Consumer<Integer> consumer) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(title);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		Text text = new Text(parent, SWT.BORDER);
		text.setText("0"); //$NON-NLS-1$
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				Text widget = (Text) e.widget;
				String s = widget.getText();
				if (!isInteger(s)) {
					widget.setBackground(ColorConstants.red);
					return;
				}
				widget.setBackground(null);
				consumer.accept(Integer.parseInt(s));
			}
		});
		propertyChangeSupport.addPropertyChangeListener(event -> {
			if (prop.equals(event.getPropertyName())) {
				text.setText(Integer.toString((int) event.getNewValue()));
			}
		});
	}

	private static boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private void createFloatPanel(Composite parent, String title, String prop, Consumer<Float> consumer) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(title);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				Text widget = (Text) e.widget;
				String s = widget.getText();
				if (!isValidFloat(s)) {
					widget.setBackground(ColorConstants.red);
					return;
				}
				widget.setBackground(null);
				consumer.accept(Float.parseFloat(s));
			}
		});
		propertyChangeSupport.addPropertyChangeListener(event -> {
			if (prop.equals(event.getPropertyName())) {
				text.setText(Float.toString((float) event.getNewValue()));
			}
		});
	}

	private static boolean isValidFloat(String string) {
		try {
			float f = Float.parseFloat(string);
			return f > 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private void setAppliedX(int newX) {
		int oldX = x;
		x = newX;
		propertyChangeSupport.firePropertyChange(PROP_TRANSLATE_X, oldX, newX);
		redraw();
	}

	private void setAppliedY(int newY) {
		int oldY = y;
		y = newY;
		propertyChangeSupport.firePropertyChange(PROP_TRANSLATE_Y, oldY, newY);
		redraw();
	}

	private void setAppliedScaleX(float newScaleX) {
		float oldScaleX = scaleX;
		scaleX = newScaleX;
		propertyChangeSupport.firePropertyChange(PROP_SCALE_X, oldScaleX, newScaleX);
		redraw();
	}

	private void setAppliedScaleY(float newScaleY) {
		float oldScaleY = scaleY;
		scaleY = newScaleY;
		propertyChangeSupport.firePropertyChange(PROP_SCALE_Y, oldScaleY, newScaleY);
		redraw();
	}

	@Override
	protected void paint(Graphics g) {
		g.translate(x, y);
		g.scale(scaleX, scaleY);
		g.setBackgroundColor(ColorConstants.blue);
		g.fillRectangle(0, 0, 100, 100);
		g.scale(1.0f / scaleX, 1.0f / scaleY);
		g.translate(-x, -y);
	}
}
