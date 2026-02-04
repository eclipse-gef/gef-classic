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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;

public class GraphicsTransformPage extends AbstractGraphicsPage {
	private static final String PROP_SCALE_X = "scale-x"; //$NON-NLS-1$
	private static final String PROP_SCALE_Y = "scale-y"; //$NON-NLS-1$
	private static final String PROP_TRANSLATE_X = "translate-x"; //$NON-NLS-1$
	private static final String PROP_TRANSLATE_Y = "translate-y"; //$NON-NLS-1$
	private static final String PROP_ROTATE = "rotate"; //$NON-NLS-1$
	private static final String PROP_SHEAR_X = "shear-x"; //$NON-NLS-1$
	private static final String PROP_SHEAR_Y = "shear-y"; //$NON-NLS-1$
	private PropertyChangeSupport propertyChangeSupport;

	private int x;
	private int y;
	private int scaleX;
	private int scaleY;
	private int shearX;
	private int shearY;
	private int rotate;

	public GraphicsTransformPage(Composite parent, int style) throws Exception {
		super(parent, style);
		setAppliedX(0);
		setAppliedY(0);
		setAppliedScaleX(10);
		setAppliedScaleY(10);
	}

	@Override
	protected Control createControlPanel() {
		propertyChangeSupport = new PropertyChangeSupport(this);

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		createIntPanel(composite, Messages.getString("GraphicsTransformPage.TRANSLATE_X_LABEL"), PROP_TRANSLATE_X, 0, //$NON-NLS-1$
				this::setAppliedX);
		createIntPanel(composite, Messages.getString("GraphicsTransformPage.TRANSLATE_Y_LABEL"), PROP_TRANSLATE_Y, 0, //$NON-NLS-1$
				this::setAppliedY);
		createIntPanel(composite, Messages.getString("GraphicsTransformPage.TRANSLATE_SCALE_X_LABEL"), PROP_SCALE_X, 1, //$NON-NLS-1$
				this::setAppliedScaleX);
		createIntPanel(composite, Messages.getString("GraphicsTransformPage.TRANSLATE_SCALE_Y_LABEL"), PROP_SCALE_Y, 1, //$NON-NLS-1$
				this::setAppliedScaleY);
		createIntPanel(composite, Messages.getString("GraphicsTransformPage.TRANSLATE_SHEAR_X_LABEL"), PROP_SHEAR_X, 1, //$NON-NLS-1$
				this::setAppliedShearX);
		createIntPanel(composite, Messages.getString("GraphicsTransformPage.TRANSLATE_SHEAR_Y_LABEL"), PROP_SHEAR_Y, 1, //$NON-NLS-1$
				this::setAppliedShearY);
		createRotatePanel(composite);

		return composite;
	}

	private void createIntPanel(Composite parent, String title, String prop, int digits, Consumer<Integer> consumer) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(title);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		spinner.setDigits(digits);
		spinner.setMinimum(1);
		spinner.setMaximum(100);
		spinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				consumer.accept(((Spinner) e.widget).getSelection());
			}
		});
		propertyChangeSupport.addPropertyChangeListener(event -> {
			if (prop.equals(event.getPropertyName())) {
				spinner.setSelection((int) event.getNewValue());
			}
		});
	}

	private void createRotatePanel(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(Messages.getString("GraphicsTransformPage.TRANSLATE_ROTATE_LABEL")); //$NON-NLS-1$
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		spinner.setDigits(0);
		spinner.setMinimum(0);
		spinner.setMaximum(360);
		spinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAppliedRotate(((Spinner) e.widget).getSelection());
			}
		});
		propertyChangeSupport.addPropertyChangeListener(event -> {
			if (PROP_ROTATE.equals(event.getPropertyName())) {
				spinner.setSelection((int) event.getNewValue());
			}
		});
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

	private void setAppliedScaleX(int newScaleX) {
		float oldScaleX = scaleX;
		scaleX = newScaleX;
		propertyChangeSupport.firePropertyChange(PROP_SCALE_X, oldScaleX, newScaleX);
		redraw();
	}

	private void setAppliedScaleY(int newScaleY) {
		float oldScaleY = scaleY;
		scaleY = newScaleY;
		propertyChangeSupport.firePropertyChange(PROP_SCALE_Y, oldScaleY, newScaleY);
		redraw();
	}

	private void setAppliedShearX(int newShearX) {
		int oldShearX = shearX;
		shearX = newShearX;
		propertyChangeSupport.firePropertyChange(PROP_SHEAR_X, oldShearX, newShearX);
		redraw();
	}

	private void setAppliedShearY(int newShearY) {
		int oldShearY = shearY;
		shearY = newShearY;
		propertyChangeSupport.firePropertyChange(PROP_SHEAR_Y, oldShearY, newShearY);
		redraw();
	}

	private void setAppliedRotate(int newRotate) {
		float oldRotate = rotate;
		rotate = newRotate;
		propertyChangeSupport.firePropertyChange(PROP_ROTATE, oldRotate, newRotate);
		redraw();
	}

	@Override
	protected void paint(Graphics g) {
		g.translate(x, y);
		g.scale(scaleX / 10f, scaleY / 10f);
		g.shear(shearX / 10f, shearY / 10f);
		g.rotate(rotate);
		g.setBackgroundColor(ColorConstants.blue);
		g.fillRectangle(0, 0, 100, 100);
		g.scale(1.0f / scaleX, 1.0f / scaleY);
		g.translate(-x, -y);
	}
}
