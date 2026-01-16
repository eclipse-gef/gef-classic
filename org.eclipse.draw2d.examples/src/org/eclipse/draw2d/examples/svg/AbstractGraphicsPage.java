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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.BorderData;
import org.eclipse.swt.layout.BorderLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.svg.SVGGraphics;

/**
 * Base class for all controls that are show in the tab items of
 * {@link SVGExample}.
 * <p>
 * This control is split into two segments:
 * <ul>
 * <li>On the right side are two composites, which draw an element using the
 * {@link SWTGraphics} and the {@link SVGGraphics}.</li>
 * <li>On the left side is a "control" panel where the user can modify the
 * properties of the drawn element.</li>
 * </ul>
 * </p>
 */
public abstract class AbstractGraphicsPage extends Composite {
	public AbstractGraphicsPage(Composite parent, int style) throws Exception {
		super(parent, style);
		setLayout(new BorderLayout());

		Control control = createControlPanel();
		control.setLayoutData(new BorderData(SWT.LEFT));

		Text text = new Text(this, SWT.MULTI);
		text.setEditable(false);
		text.setLayoutData(new BorderData(SWT.BOTTOM));
		text.setSize(SWT.DEFAULT, 200);

		Composite center = new Composite(this, SWT.NONE);
		center.setLayout(new GridLayout(2, true));
		center.setLayoutData(new BorderData(SWT.CENTER));

		createGraphicsComposite(center, Messages.getString("AbstractGraphicsPage.SWT"), gc -> { //$NON-NLS-1$
			SWTGraphics g = new SWTGraphics(gc);
			paint(g);
			g.dispose();
		});

		createGraphicsComposite(center, Messages.getString("AbstractGraphicsPage.SVG"), gc -> { //$NON-NLS-1$
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try (Writer writer = new OutputStreamWriter(os)) {
				SVGGraphics g = new SVGGraphics();
				paint(g);
				g.stream(writer);
				g.dispose();

				text.setText(new String(os.toByteArray()));
				AbstractGraphicsPage.this.requestLayout();

				if (os.size() != 0) {
					Image img = new Image(Display.getCurrent(), new ByteArrayInputStream(os.toByteArray()));
					gc.drawImage(img, 0, 0);
					img.dispose();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		});
	}

	private static Control createGraphicsComposite(Composite parent, String text, Consumer<GC> consumer) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new BorderLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new BorderData(SWT.TOP));
		label.setText(text);

		Composite body = new Composite(composite, SWT.BORDER);
		body.setLayoutData(new BorderData(SWT.CENTER));
		body.addPaintListener(event -> consumer.accept(event.gc));
		return composite;
	}

	protected abstract Control createControlPanel();

	protected abstract void paint(Graphics g);
}
