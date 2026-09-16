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

import java.io.IOException;
import java.io.Writer;

import org.eclipse.draw2d.AWTGraphics;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Dimension;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.batik.ext.awt.image.codec.png.PNGImageWriter;
import org.apache.batik.svggen.SVGGraphics2D;
import org.osgi.annotation.bundle.Referenced;
import org.w3c.dom.Document;

/**
 * The {@link SVGGraphics} class transforms the contents of a
 * {@link FigureCanvas} into an SVG. Use the {@link #stream(Writer)} method to
 * export the XML structure.
 *
 * @noextend This class is not intended to be subclassed by clients.
 */
@Referenced(PNGImageWriter.class)
public class SVGGraphics extends AWTGraphics {
	private static final int DPI = 72; // SVGs use a DPI value of 72 by default
	private boolean ownGraphics; // Indicates that the Graphics2D object was created by us

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
	 * @return a new {@link SVGGraphics} instance.
	 */
	private static SVGGraphics2D createGraphics() {
		return new SVGGraphics2D(createDocument());
	}

	/**
	 * Convenience method for converting a Draw2D dimension to an AWT dimension.
	 */
	private static java.awt.Dimension getDimension(Dimension dimension) {
		if (dimension == null) {
			return null;
		}
		return new java.awt.Dimension(dimension.width, dimension.height);
	}

	/**
	 * The size of the outmost SVG element (i.e. the viewbox).
	 */
	private Dimension viewport;

	/**
	 * Constructs a new SWTGraphics that draws to the Canvas using the default GC.
	 */
	public SVGGraphics() {
		this(createGraphics());
		ownGraphics = true;
	}

	/**
	 * Constructs a new SWTGraphics that draws to the Canvas using the given GC.
	 */
	public SVGGraphics(SVGGraphics2D gc) {
		super(gc);
	}

	/**
	 * @see AWTGraphics#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (ownGraphics) {
			gc.dispose();
		}
	}

	/**
	 * @see AWTGraphics#getDPI()
	 */
	@Override
	protected int getDPI() {
		return DPI;
	}

	/**
	 * Set the Canvas size, this is used to set the width and height attributes on
	 * the outermost 'svg' element (i.e. the viewbox).
	 *
	 * @param viewport SVG Canvas size. May be null (equivalent to 100%, 100%)
	 */
	public void setViewport(Dimension viewport) {
		this.viewport = viewport;
	}

	/**
	 * @param writer used to writer out the SVG content
	 */
	public void stream(Writer writer) throws IOException {
		// setSVGCanvasSize(...) doesn't support null values, even thought the JavaDoc
		// says it does...
		if (viewport != null) {
			((SVGGraphics2D) gc).setSVGCanvasSize(getDimension(viewport));
		}
		((SVGGraphics2D) gc).stream(writer);
	}
}
