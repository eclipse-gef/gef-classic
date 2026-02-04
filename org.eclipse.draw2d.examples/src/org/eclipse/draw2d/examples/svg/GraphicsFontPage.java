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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;

/**
 * This page paints a generic string. The user has the option to customize the
 * font, the colors and whether a background should be drawn.
 */
public class GraphicsFontPage extends AbstractGraphicsPage {
	private static final String PROP_FONT = "font"; //$NON-NLS-1$
	private static final String PROP_BG_COLOR = "bg-color"; //$NON-NLS-1$
	private static final String PROP_FG_COLOR = "fg-color"; //$NON-NLS-1$
	private static final String PROP_TEXT = "text"; //$NON-NLS-1$
	private static final String PROP_FILL = "fill"; //$NON-NLS-1$
	private PropertyChangeSupport propertyChangeSupport;

	private Font font;
	private Color fgColor;
	private Color bgColor;
	private String text;
	private boolean fill;

	public GraphicsFontPage(Composite parent, int style) throws Exception {
		super(parent, style);
		setAppliedFont(getFont());
		setAppliedForeground(getForeground());
		setAppliedBackground(getBackground());
		setAppliedText(Messages.getString("GraphicsFontPage.DEFAULT_TEXT")); //$NON-NLS-1$
		setAppliedFill(false);

		addDisposeListener(event -> {
			if (font != null) {
				font.dispose();
			}
		});
	}

	@Override
	protected Control createControlPanel() {
		propertyChangeSupport = new PropertyChangeSupport(this);

		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.pack = false;

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(layout);

		createTextPanel(composite);
		createFontPanel(composite);
		createForegroundColorPanel(composite);
		createBackgroundColorPanel(composite);
		createFillPanel(composite);

		return composite;
	}

	private void createFontPanel(Composite parent) {
		Button button = new Button(parent, SWT.NONE);
		button.setText(Messages.getString("GraphicsFontPage.BUTTON_FONT_LABEL")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FontDialog dialog = new FontDialog(Display.getCurrent().getActiveShell());
				FontData fd = dialog.open();
				if (fd == null) {
					return;
				}
				setAppliedFont(new Font(null, fd));
			}
		});

		Label label = new Label(parent, SWT.NONE);
		propertyChangeSupport.addPropertyChangeListener(event -> {
			if (PROP_FONT.equals(event.getPropertyName())) {
				label.setText(((Font) (event.getNewValue())).getFontData()[0].toString());
			}
		});
	}

	private void createBackgroundColorPanel(Composite parent) {
		Button button = new Button(parent, SWT.NONE);
		button.setText(Messages.getString("GraphicsFontPage.BUTTON_BG_LABEL")); //$NON-NLS-1$
		button.setEnabled(fill);
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
			if (PROP_BG_COLOR.equals(event.getPropertyName())) {
				label.setBackground((Color) event.getNewValue());
			}
		});
	}

	private void createForegroundColorPanel(Composite parent) {
		Button button = new Button(parent, SWT.NONE);
		button.setText(Messages.getString("GraphicsFontPage.BUTTON_FG_LABEL")); //$NON-NLS-1$
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
			if (PROP_FG_COLOR.equals(event.getPropertyName())) {
				label.setBackground((Color) event.getNewValue());
			}
		});
	}

	private void createFillPanel(Composite parent) {
		Button button = new Button(parent, SWT.CHECK);
		button.setText(Messages.getString("GraphicsFontPage.BUTTON_FILL_LABEL")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAppliedFill(((Button) e.widget).getSelection());
			}
		});
		propertyChangeSupport.addPropertyChangeListener(event -> {
			if (PROP_FILL.equals(event.getPropertyName())) {
				button.setSelection((boolean) event.getNewValue());
			}
		});
	}

	private void createTextPanel(Composite parent) {
		Text text = new Text(parent, SWT.BORDER);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String s = ((Text) e.widget).getText();
				if (s.isEmpty()) {
					text.setBackground(ColorConstants.red);
					return;
				}
				text.setBackground(null);
				setAppliedText(s);
			}
		});
		propertyChangeSupport.addPropertyChangeListener(event -> {
			if (PROP_TEXT.equals(event.getPropertyName())) {
				text.setText((String) event.getNewValue());
			}
		});
	}

	private void setAppliedFont(Font newFont) {
		if (font != null && font != getDisplay().getSystemFont()) {
			font.dispose();
		}
		Font oldFont = font;
		font = newFont;
		propertyChangeSupport.firePropertyChange(PROP_FONT, oldFont, newFont);
		redraw();
	}

	private void setAppliedBackground(Color newColor) {
		Color oldColor = bgColor;
		bgColor = newColor;
		propertyChangeSupport.firePropertyChange(PROP_BG_COLOR, oldColor, newColor);
		redraw();
	}

	private void setAppliedForeground(Color newColor) {
		Color oldColor = fgColor;
		fgColor = newColor;
		propertyChangeSupport.firePropertyChange(PROP_FG_COLOR, oldColor, newColor);
		redraw();
	}

	private void setAppliedText(String newText) {
		String oldText = text;
		text = newText;
		propertyChangeSupport.firePropertyChange(PROP_TEXT, oldText, newText);
		redraw();
	}

	private void setAppliedFill(boolean newFill) {
		boolean oldFill = fill;
		fill = newFill;
		propertyChangeSupport.firePropertyChange(PROP_FILL, oldFill, newFill);
		redraw();
	}

	@Override
	protected void paint(Graphics g) {
		g.setForegroundColor(fgColor);
		g.setBackgroundColor(bgColor);
		g.setFont(font);

		if (fill) {
			g.fillString(text, 0, 0);
		} else {
			g.drawString(text, 0, 0);
		}
	}

}
