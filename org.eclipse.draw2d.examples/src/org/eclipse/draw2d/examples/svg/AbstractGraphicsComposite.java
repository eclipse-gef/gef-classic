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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.function.BiFunction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.BorderData;
import org.eclipse.swt.layout.BorderLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
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
public abstract class AbstractGraphicsComposite extends Composite {

	public AbstractGraphicsComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));

		Composite swtSurface = createGraphicsComposite(composite, Messages.getString("AbstractGraphicsComposite.SWT"), //$NON-NLS-1$
				Composite::new);
		Browser svgSurface = createGraphicsComposite(composite, Messages.getString("AbstractGraphicsComposite.SVG"), //$NON-NLS-1$
				Browser::new);

		swtSurface.addPaintListener(event -> {
			Point surfaceSize = swtSurface.getSize();
			Dimension graphicsSize = getGraphicsSize();

			SWTGraphics g = new SWTGraphics(event.gc);
			if (graphicsSize != null) {
				g.translate((surfaceSize.x - graphicsSize.width) / 2, (surfaceSize.y - graphicsSize.height) / 2);
				g.clipRect(new Rectangle(0, 0, graphicsSize.width, graphicsSize.height));
			}
			paint(g);
			g.dispose();
		});

		swtSurface.addPaintListener(event -> {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try (Writer writer = new OutputStreamWriter(os)) {
				Point surfaceSize = swtSurface.getSize();
				SVGGraphics g = new SVGGraphics();
				g.setViewport(getGraphicsSize());
				g.setClip(new Rectangle(0, 0, surfaceSize.x, surfaceSize.y));
				paint(g);
				g.stream(writer);
				g.dispose();

				svgSurface.setText("""
						<!DOCTYPE html>
						<html>
						<body style="margin:0; height:100vh; display:flex; justify-content:center; align-items:center;">
						%s
						</body>
						</html>""".formatted(new String(os.toByteArray()))); //$NON-NLS-1$
			} catch (SWTException e) {
				e.printStackTrace();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private static <T extends Control> T createGraphicsComposite(Composite parent, String text,
			BiFunction<Composite, Integer, T> function) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new BorderLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new BorderData(SWT.TOP));
		label.setText(text);

		T body = function.apply(composite, SWT.BORDER);
		body.setLayoutData(new BorderData(SWT.CENTER));
		return body;
	}

	protected abstract Dimension getGraphicsSize();

	protected abstract void paint(Graphics g);
}
