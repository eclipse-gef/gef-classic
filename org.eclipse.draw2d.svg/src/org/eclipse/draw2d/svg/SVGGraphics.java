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
package org.eclipse.draw2d.svg;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Writer;

import org.eclipse.draw2d.FigureCanvas;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Document;

/**
 * The {@link SVGGraphics} class transforms the contents of a
 * {@link FigureCanvas} into an SVG. Use the {@link #stream(Writer)} method to
 * export the XML structure.
 */
public class SVGGraphics extends AWTGraphics {
	private static final int DPI = 72; // SVGs use a DPI value of 72 by default
	private final Rectangle documentSize;

	/**
	 * Constructs a new SWTGraphics that draws to the Canvas using the default GC.
	 */
	public SVGGraphics() {
		this(createGraphics());
	}

	/**
	 * Constructs a new SWTGraphics that draws to the Canvas using the given GC.
	 */
	public SVGGraphics(SVGGraphics2D gc) {
		super(gc);
		documentSize = new Rectangle();
	}

	// #################################################################################################################
	//
	// GC
	//
	// #################################################################################################################

	private void updateSize(double x, double y, double width, double height) {
		updateSize(new Rectangle2D.Double(x, y, width, height));
	}

	private void updateSize(Shape shape) {
		Shape transformed = currentState.affineTransform.createTransformedShape(shape);
		documentSize.add(transformed.getBounds2D());
		((SVGGraphics2D) gc).setSVGCanvasSize(documentSize.getSize());
	}

	@Override
	/* package */ int getDPI() {
		return DPI;
	}

	// #################################################################################################################
	//
	// Shape
	//
	// #################################################################################################################

	@Override
	/* package */ void doDrawShape(Shape shape) {
		updateSize(shape);
		super.doDrawShape(shape);
	}

	@Override
	/* package */ void doFillShape(Shape shape) {
		updateSize(shape);
		super.doFillShape(shape);
	}

	// #################################################################################################################
	//
	// String
	//
	// #################################################################################################################

	@Override
	/* package */ void doDrawString(String s, int x, int y, Rectangle2D bounds, boolean fill) {
		updateSize(x, y, bounds.getWidth(), bounds.getHeight());
		super.doDrawString(s, x, y, bounds, fill);
	}

	// #################################################################################################################
	//
	// SVG
	//
	// #################################################################################################################

	/**
	 * @return a new {@link SVGGraphics} instace.
	 */
	private static SVGGraphics2D createGraphics() {
		return new SVGGraphics2D(createDocument());
	}

	/**
	 * @return the factory which will produce Elements for the DOM tree this
	 *         Graphics generates.
	 */
	private static Document createDocument() {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			return documentBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param writer used to writer out the SVG content
	 */
	public void stream(Writer writer) throws IOException {
		((SVGGraphics2D) gc).stream(writer);
	}
}
