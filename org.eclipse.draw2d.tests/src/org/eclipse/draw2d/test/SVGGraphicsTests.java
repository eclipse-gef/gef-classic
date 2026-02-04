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

package org.eclipse.draw2d.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;

import org.eclipse.swt.graphics.Color;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.svg.AWTGraphics;
import org.eclipse.draw2d.svg.SVGGraphics;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

public class SVGGraphicsTests {
	private static DocumentBuilderFactory documentBuilderFactory;
	private static DocumentBuilder documentBuilder;
	private static Transformer transformer;
	private SVGGraphics graphics;
	private java.awt.Graphics2D graphics2d;

	@BeforeAll
	public static void setUpAll() throws TransformerConfigurationException, ParserConfigurationException {
		transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); //$NON-NLS-1$ //$NON-NLS-2$
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
		documentBuilder = documentBuilderFactory.newDocumentBuilder();
	}

	@BeforeEach
	public void setUp() {
		graphics = new SVGGraphics();
		graphics2d = getGraphics2D();
	}

	@AfterEach
	public void tearDown() {
		graphics.dispose();
	}

	@Test
	public void testDrawArc() {
		graphics.drawArc(new Rectangle(10, 15, 25, 30), 45, 90);
		assertSVG("""
				<g>
				  <path d="M31.3388 19.3934 A12.5 15 0 0 0 13.6612 19.3934" fill="none"/>
				</g>"""); //$NON-NLS-1$
	}

	@Test
	public void testDrawLine() {
		graphics.drawLine(0, 15, 25, 30);
		assertSVG("""
				<g>
				  <line fill="none" x1="0" x2="25" y1="15" y2="30"/>
				</g>"""); //$NON-NLS-1$
	}

	@Test
	public void testDrawOval() {
		graphics.drawOval(10, 15, 25, 30);
		assertSVG("""
				<g>
				  <ellipse cx="22.5" cy="30" fill="none" rx="12.5" ry="15"/>
				</g>"""); //$NON-NLS-1$
	}

	@Test
	public void testDrawRectangle() {
		graphics.drawRectangle(10, 15, 25, 30);
		assertSVG("""
				<g>
				  <rect fill="none" height="30" width="25" x="10" y="15"/>
				</g>"""); //$NON-NLS-1$
	}

	@Test
	public void testDrawRoundRectangle() {
		graphics.drawRoundRectangle(new Rectangle(10, 15, 25, 30), 45, 60);
		assertSVG("""
				<g>
				  <rect fill="none" height="30" rx="22.5" ry="30" width="25" x="10" y="15"/>
				</g>"""); //$NON-NLS-1$
	}

	@Test
	public void testFillArc() {
		graphics.fillArc(new Rectangle(10, 15, 25, 30), 45, 90);
		assertSVG("""
				<g fill="rgb(0,0,0)" fill-opacity="0" stroke="rgb(0,0,0)" stroke-opacity="0">
				  <path d="M31.3388 19.3934 A12.5 15 0 0 0 13.6612 19.3934L 22.5 30 Z" stroke="none"/>
				</g>"""); //$NON-NLS-1$
	}

	@Test
	public void testFillOval() {
		graphics.fillOval(10, 15, 25, 30);
		assertSVG("""
				<g fill="rgb(0,0,0)" fill-opacity="0" stroke="rgb(0,0,0)" stroke-opacity="0">
				  <ellipse cx="22.5" cy="30" rx="12.5" ry="15" stroke="none"/>
				</g>"""); //$NON-NLS-1$
	}

	@Test
	public void testFillRectangle() {
		graphics.fillRectangle(10, 15, 25, 30);
		assertSVG("""
				<g fill="rgb(0,0,0)" fill-opacity="0" stroke="rgb(0,0,0)" stroke-opacity="0">
				  <rect height="30" stroke="none" width="25" x="10" y="15"/>
				</g>"""); //$NON-NLS-1$
	}

	@Test
	public void testFillRoundRectangle() {
		graphics.fillRoundRectangle(new Rectangle(10, 15, 25, 30), 45, 60);
		assertSVG("""
				<g fill="rgb(0,0,0)" fill-opacity="0" stroke="rgb(0,0,0)" stroke-opacity="0">
				  <rect height="30" rx="22.5" ry="30" stroke="none" width="25" x="10" y="15"/>
				</g>"""); //$NON-NLS-1$
	}

	@Test
	public void testSetBackgroundColor() {
		graphics.setBackgroundColor(ColorConstants.red);
		graphics.fillRectangle(0, 10, 20, 30);
		assertSVG("""
				<g fill="red" stroke="red">
				  <rect height="30" stroke="none" width="20" x="0" y="10"/>
				</g>
				"""); //$NON-NLS-1$
	}

	@Test
	public void testSetForegroundColor() {
		graphics.setForegroundColor(ColorConstants.red);
		graphics.drawRectangle(0, 10, 20, 30);
		assertSVG("""
				<g fill="red" stroke="red">
				  <rect fill="none" height="30" width="20" x="0" y="10"/>
				</g>
				"""); //$NON-NLS-1$
	}

	@Test
	public void testFillString1() {
		Rectangle r = getStringBounds("Hello World"); //$NON-NLS-1$
		graphics.setBackgroundColor(ColorConstants.blue);
		graphics.fillString("Hello World", 0, 0); //$NON-NLS-1$
		assertSVG("""
				<g fill="blue" stroke="blue">
				  <rect height="%d" stroke="none" width="%d" x="0" y="0"/>
				  <text fill="black" stroke="none" x="0" xml:space="preserve" y="%d">Hello World</text>
				</g>""".formatted(r.height, r.width, r.y)); //$NON-NLS-1$
	}

	@Test
	public void testFillString2() {
		Rectangle r = getStringBounds("Hello World"); //$NON-NLS-1$
		graphics.setForegroundColor(ColorConstants.blue);
		graphics.fillString("Hello World", 0, 0); //$NON-NLS-1$
		assertSVG("""
				<g fill="rgb(0,0,0)" fill-opacity="0" stroke="rgb(0,0,0)" stroke-opacity="0">
				  <rect height="%d" stroke="none" width="%d" x="0" y="0"/>
				</g>
				<g fill="blue" stroke="blue">
				  <text stroke="none" x="0" xml:space="preserve" y="%d">Hello World</text>
				</g>""".formatted(r.height, r.width, r.y)); //$NON-NLS-1$
	}

	@Test
	public void testDrawString() {
		Rectangle r = getStringBounds("Hello World"); //$NON-NLS-1$
		graphics.setForegroundColor(ColorConstants.yellow);
		graphics.drawString("Hello World", 0, 0); //$NON-NLS-1$
		assertSVG("""
				<g fill="yellow" stroke="yellow">
				  <text stroke="none" x="0" xml:space="preserve" y="%d">Hello World</text>
				</g>""".formatted(r.y)); //$NON-NLS-1$
	}

	@Test
	public void testFillText1() {
		Rectangle r = getStringBounds("Hello World"); //$NON-NLS-1$
		graphics.setBackgroundColor(ColorConstants.blue);
		graphics.fillText("Hello World", 0, 0); //$NON-NLS-1$
		assertSVG("""
				<g fill="blue" stroke="blue">
				  <rect height="%d" stroke="none" width="%d" x="0" y="0"/>
				  <text fill="black" stroke="none" x="0" xml:space="preserve" y="%d">Hello World</text>
				</g>""".formatted(r.height, r.width, r.y)); //$NON-NLS-1$
	}

	@Test
	public void testFillText2() {
		Rectangle r = getStringBounds("Hello World"); //$NON-NLS-1$
		graphics.setForegroundColor(ColorConstants.blue);
		graphics.fillText("Hello World", 0, 0); //$NON-NLS-1$
		assertSVG("""
				<g fill="rgb(0,0,0)" fill-opacity="0" stroke="rgb(0,0,0)" stroke-opacity="0">
				  <rect height="%d" stroke="none" width="%d" x="0" y="0"/>
				</g>
				<g fill="blue" stroke="blue">
				  <text stroke="none" x="0" xml:space="preserve" y="%d">Hello World</text>
				</g>""".formatted(r.height, r.width, r.y)); //$NON-NLS-1$
	}

	@Test
	public void testDrawText() {
		Rectangle r = getStringBounds("Hello World"); //$NON-NLS-1$
		graphics.setForegroundColor(ColorConstants.yellow);
		graphics.drawText("Hello World", 0, 0); //$NON-NLS-1$
		assertSVG("""
				<g fill="yellow" stroke="yellow">
				  <text stroke="none" x="0" xml:space="preserve" y="%d">Hello World</text>
				</g>""".formatted(r.y)); //$NON-NLS-1$
	}

	@Test
	public void testTranslate() {
		graphics.translate(15, 25);
		graphics.drawRectangle(10, 15, 25, 30);
		assertSVG("""
				<g transform="matrix(1,0,0,1,15,25)">
				  <rect fill="none" height="30" width="25" x="10" y="15"/>
				</g>"""); //$NON-NLS-1$
	}

	@Test
	public void testScale1() {
		graphics.scale(2);
		graphics.drawRectangle(10, 15, 25, 30);
		assertSVG("""
				<g transform="matrix(2,0,0,2,0,0)">
				  <rect fill="none" height="30" width="25" x="10" y="15"/>
				</g>"""); //$NON-NLS-1$
	}

	@Test
	public void testScale2() {
		graphics.scale(2, 3);
		graphics.drawRectangle(10, 15, 25, 30);
		assertSVG("""
				<g transform="matrix(2,0,0,3,0,0)">
				  <rect fill="none" height="30" width="25" x="10" y="15"/>
				</g>"""); //$NON-NLS-1$
	}

	@Test
	public void testGetBackgroundColor() {
		assertEquals(new Color(0, 0, 0, 0), graphics.getBackgroundColor());
		graphics.setBackgroundColor(ColorConstants.cyan);
		assertSame(ColorConstants.cyan, graphics.getBackgroundColor());
	}

	@Test
	public void testGetForegroundColor() {
		assertEquals(new Color(0, 0, 0, 255), graphics.getForegroundColor());
		graphics.setForegroundColor(ColorConstants.gray);
		assertSame(ColorConstants.gray, graphics.getForegroundColor());
	}

	private void assertSVG(String shape) {
		String expected = """
				<?xml version="1.0" encoding="UTF-8" standalone="no"?>
				<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" color-interpolation="auto" color-rendering="auto" fill="black" fill-opacity="1" font-family="'Dialog'" font-size="12px" font-style="normal" font-weight="normal" image-rendering="auto" shape-rendering="auto" stroke="black" stroke-dasharray="none" stroke-dashoffset="0" stroke-linecap="square" stroke-linejoin="miter" stroke-miterlimit="10" stroke-opacity="1" stroke-width="1" text-rendering="auto">
				  <!--Generated by the Batik Graphics2D SVG Generator-->
				  <defs id="genericDefs"/>
				  <g>
				%s
				  </g>
				</svg>
				""" //$NON-NLS-1$
				.formatted(shape.indent(4).replaceAll("\n$", "")); //$NON-NLS-1$//$NON-NLS-2$
		try (StringWriter writer = new StringWriter()) {
			graphics.stream(writer);
			assertEquals(expected, prettyPrint(writer.toString()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String prettyPrint(String content) throws Exception {
		try (InputStream is = new ByteArrayInputStream(content.getBytes())) {
			Document document = documentBuilder.parse(is);
			document.getLastChild().getAttributes().removeNamedItem("width"); //$NON-NLS-1$
			document.getLastChild().getAttributes().removeNamedItem("height"); //$NON-NLS-1$
			try (StringWriter writer = new StringWriter()) {
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(writer);
				transformer.transform(source, result);
				return writer.toString().replace("\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	private java.awt.Graphics2D getGraphics2D() {
		try {
			Field f = AWTGraphics.class.getDeclaredField("gc"); //$NON-NLS-1$
			f.setAccessible(true);
			return (java.awt.Graphics2D) f.get(graphics);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	private Rectangle getStringBounds(String string) {
		java.awt.geom.Rectangle2D rect = graphics2d.getFontMetrics().getStringBounds(string, graphics2d);
		int x = (int) Math.round(rect.getX());
		int y = (int) Math.round(-rect.getY());
		int width = (int) Math.round(rect.getWidth());
		int height = (int) Math.round(rect.getHeight());
		return new Rectangle(x, y, width, height);
	}
}
