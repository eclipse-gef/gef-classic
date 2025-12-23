/*******************************************************************************
 * Copyright (c) 2004, 2025 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d.test;

import java.util.ArrayDeque;
import java.util.Deque;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.SWTGraphics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AdvancedGraphicsTests extends BaseTestCase {

	static final int[] LINE = { 5, 5, 20, 20, 35, 5, 50, 5 };
	static final int[] POLY = { 5, 5, 45, 15, 20, 30, 20, 20, 45, 35, 5, 45 };
	private SWTGraphics g;

	private Image image;
	private final Deque<Resource> resources = new ArrayDeque<>();

	private void assertImageEquality(int width, int height) {
		ImageData data = image.getImageData();
		int src;
		int dst;
		PaletteData palette = data.palette;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				src = data.getPixel(x, y);
				dst = data.getPixel(x, y + height);

				if (src != dst) {
					RGB rgb1 = palette.getRGB(src);
					RGB rgb2 = palette.getRGB(dst);
					// HACK, image operations seem to differ by as much as 4
					if (Math.abs(rgb1.red - rgb2.red) > 4 || Math.abs(rgb1.green - rgb2.green) > 4
							|| Math.abs(rgb1.blue - rgb2.blue) > 4) {
						assertEquals(rgb1, rgb2, "Discrepancy at coordinates <" + x + ", " + y + ">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
			}
		}
	}

	private void displayImage() {
		final Shell shell = new Shell(SWT.DIALOG_TRIM);
		shell.addPaintListener(e -> e.gc.drawImage(image, 0, 0));
		shell.setBounds(100, 100, 800, 600);
		shell.open();
		Display d = shell.getDisplay();
		d.asyncExec(() -> {
			if (!shell.isDisposed()) {
				shell.close();
			}
		});
		waitEventLoop(shell, 100);
	}

	private void performTestcase(Runnable painter, Runnable[] tests) {
		g.pushState();
		painter.run();
		for (Runnable test : tests) {
			g.translate(100, 0);
			test.run();
			g.pushState();
			painter.run();
		}

		for (int i = 0; i <= tests.length; i++) {
			g.popState();
			g.translate(0, 100);
			painter.run();
		}

		displayImage();
		assertImageEquality(100 * tests.length, 100);
	}

	@BeforeEach
	public void setUp() throws Exception {
		Path path1 = new Path(null);
		path1.moveTo(20, 5);
		path1.quadTo(40, 5, 50, 25);
		path1.quadTo(20, 25, 20, 45);
		path1.lineTo(0, 25);
		path1.close();

		Path path2 = new Path(null);
		path2.moveTo(15, 30);
		path2.cubicTo(50, 0, 50, 30, 20, 60);
		path2.close();

		image = new Image(Display.getDefault(), 800, 600);
		GC imageGC = new GC(image, SWT.NONE);
		g = new SWTGraphics(imageGC);

		resources.push(path1);
		resources.push(path2);
		resources.push(image);
		resources.push(imageGC);
	}

	@AfterEach
	public void tearDown() {
		g.dispose();
		while (!resources.isEmpty()) {
			resources.pop().dispose();
		}
	}

	@Test
	public void testAntialias() {
		class AntialiasSettings implements Runnable {
			private final Color color;
			private final int normal;
			private final int text;

			AntialiasSettings(int text, int normal, Color color) {
				this.text = text;
				this.normal = normal;
				this.color = color;
			}

			@Override
			public void run() {
				g.setAntialias(normal);
				g.setTextAntialias(text);
				g.setForegroundColor(color);
			}
		}

		g.setLineWidthFloat(9);
		g.pushState();

		Runnable[] tests = new Runnable[4];
		tests[0] = new AntialiasSettings(SWT.ON, SWT.ON, ColorConstants.red);
		tests[1] = new AntialiasSettings(SWT.OFF, SWT.OFF, ColorConstants.blue);
		tests[2] = new AntialiasSettings(SWT.DEFAULT, SWT.ON, ColorConstants.black);
		tests[3] = new AntialiasSettings(SWT.ON, SWT.DEFAULT, ColorConstants.darkGreen);
		performTestcase(() -> {
			g.drawPolyline(LINE);
			g.drawString("OWVO", 35, 20); //$NON-NLS-1$
		}, tests);
	}

	@Test
	public void testFillRules() {

		class FillRules implements Runnable {
			private final int aa;
			private final int rule;

			FillRules(int rule, int aa) {
				this.rule = rule;
				this.aa = aa;
			}

			@Override
			public void run() {
				g.setFillRule(rule);
				// $TODO
				g.setAntialias(aa);
			}
		}
		g.setBackgroundColor(ColorConstants.red);
		g.pushState();

		Runnable[] tests = new Runnable[3];
		tests[0] = new FillRules(SWT.FILL_EVEN_ODD, SWT.ON);
		tests[1] = new FillRules(SWT.FILL_WINDING, SWT.OFF);
		tests[2] = new FillRules(SWT.FILL_EVEN_ODD, SWT.DEFAULT);
		performTestcase(() -> g.fillPolygon(POLY), tests);
	}

	// public void testInterpolation() {
	// class InterpolationSettings implements Runnable {
	// private final int level;
	// InterpolationSettings (int level) {
	// this.level = level;
	// }
	// public void run() {
	// g.setInterpolation(level);
	// }
	// }
	//
	// g.pushState();
	//
	// Runnable tests[] = new Runnable[4];
	// tests[0] = new InterpolationSettings(SWT.HIGH);
	// tests[1] = new InterpolationSettings(SWT.LOW);
	// tests[2] = new InterpolationSettings(SWT.NONE);
	// tests[3] = new InterpolationSettings(SWT.DEFAULT);
	// performTestcase(new Runnable() {
	// public void run() {
	// g.drawImage(TestImages.depth_24, 0, 0, 400, 400, 0, 0, 75, 75);
	// }
	// }, tests);
	// }

	@Test
	public void testLineJoinCap() {

		class LineSettings implements Runnable {
			private final int cap;
			private final int style;
			private final int join;

			LineSettings(int join, int cap, int style) {
				this.join = join;
				this.cap = cap;
				this.style = style;
			}

			@Override
			public void run() {
				g.setLineCap(cap);
				g.setLineJoin(join);
				g.setLineStyle(style);
			}
		}

		g.setLineWidthFloat(9);
		g.pushState();

		Runnable[] tests = { new LineSettings(SWT.JOIN_ROUND, SWT.CAP_ROUND, SWT.LINE_DASH),
				new LineSettings(SWT.JOIN_BEVEL, SWT.CAP_FLAT, SWT.LINE_DOT),
				new LineSettings(SWT.JOIN_ROUND, SWT.CAP_SQUARE, SWT.LINE_SOLID) };

		performTestcase(() -> g.drawPolyline(LINE), tests);
	}

	@Test
	public void testLineJoinCapAA() {
		g.setAntialias(SWT.ON);
		testLineJoinCap();
	}

	@Test
	public void testLineAttributes() {
		class LineSettings implements Runnable {
			private final LineAttributes attributes;

			public LineSettings(LineAttributes attributes) {
				this.attributes = attributes;
			}

			@Override
			public void run() {
				g.setLineAttributes(attributes);
			}
		}

		float[] dash = { 2.5f, 3, 8 };

		Runnable[] tests = {
				new LineSettings(new LineAttributes(0.0f, SWT.CAP_FLAT, SWT.JOIN_MITER, SWT.LINE_SOLID, null, 0, 10)),
				new LineSettings(new LineAttributes(1.0f, SWT.CAP_FLAT, SWT.JOIN_MITER, SWT.LINE_SOLID, null, 0, 10)),
				new LineSettings(new LineAttributes(2.5f, SWT.CAP_FLAT, SWT.JOIN_MITER, SWT.LINE_SOLID, null, 0, 10)),
				new LineSettings(new LineAttributes(5.0f, SWT.CAP_FLAT, SWT.JOIN_MITER, SWT.LINE_DASH, null, 0, 10)),
				new LineSettings(
						new LineAttributes(5.0f, SWT.CAP_FLAT, SWT.JOIN_ROUND, SWT.LINE_DASHDOTDOT, null, 0, 10)),
				new LineSettings(new LineAttributes(4.5f, SWT.CAP_FLAT, SWT.JOIN_MITER, SWT.LINE_SOLID, null, 0, 10)),
				new LineSettings(new LineAttributes(9.0f, SWT.CAP_FLAT, SWT.JOIN_ROUND, SWT.LINE_CUSTOM, dash, 0, 10)),
				new LineSettings(
						new LineAttributes(9.5f, SWT.CAP_FLAT, SWT.JOIN_ROUND, SWT.LINE_CUSTOM, dash, 5, 10)), };

		performTestcase(() -> g.drawPolyline(LINE), tests);
	}

	@Test
	public void testLineAttributesAA() {
		g.setAntialias(SWT.ON);
		testLineAttributes();
	}

	// public void testPathDraw() {
	//
	// class PathSettings implements Runnable {
	// private final int antialias;
	// private final Color color;
	// private final int style;
	// private final int width;
	// PathSettings(int antialias, int width, int style, Color color) {
	// this.antialias = antialias;
	// this.width = width;
	// this.style = style;
	// this.color = color;
	// }
	// public void run() {
	// g.setAntialias(antialias);
	// g.setLineWidth(width);
	// g.setLineStyle(style);
	// g.setForegroundColor(color);
	// }
	// }
	//
	// g.setBackgroundColor(ColorConstants.darkBlue);
	//
	// Runnable tests[] = new Runnable[5];
	// tests[0] = new PathSettings(SWT.ON, 3, SWT.LINE_SOLID,
	// ColorConstants.darkBlue);
	// tests[1] = new PathSettings(SWT.OFF, 0, SWT.LINE_DOT,
	// ColorConstants.red);
	// tests[2] = new PathSettings(SWT.DEFAULT, 1, SWT.LINE_DOT,
	// ColorConstants.darkBlue);
	// tests[3] = new PathSettings(SWT.DEFAULT, 2, SWT.LINE_DOT,
	// ColorConstants.darkGreen);
	// tests[4] = new PathSettings(SWT.ON, 2, SWT.LINE_DASHDOTDOT,
	// ColorConstants.black);
	// performTestcase(new Runnable() {
	// public void run() {
	// g.drawPath(path1);
	// g.drawPath(path2);
	// }
	// }, tests);
	//
	// path1.dispose();
	// }

	// public void testPathFill() {
	//
	// class PathSettings implements Runnable {
	// private final int antialias;
	// private final int alpha;
	// PathSettings(int antialias, int alpha) {
	// this.antialias = antialias;
	// this.alpha = alpha;
	// }
	// public void run() {
	// g.setAntialias(antialias);
	// g.setAlpha(alpha);
	// }
	// }
	//
	// g.setBackgroundColor(ColorConstants.darkBlue);
	//
	// Runnable tests[] = new Runnable[4];
	// tests[0] = new PathSettings(SWT.ON, 200);
	// tests[1] = new PathSettings(SWT.OFF, 100);
	// tests[2] = new PathSettings(SWT.DEFAULT, 200);
	// tests[3] = new PathSettings(SWT.ON, 150);
	// performTestcase(new Runnable() {
	// public void run() {
	// g.setFillRule(SWT.FILL_EVEN_ODD);
	// g.fillPath(path1);
	// g.fillPath(path2);
	// }
	// }, tests);
	//
	// path1.dispose();
	// }

	@Test
	public void testPatterns() {

		class SetPattern implements Runnable {
			private final Pattern bg;
			private final Pattern fg;

			SetPattern(Pattern bg, Pattern fg) {
				this.bg = bg;
				this.fg = fg;
			}

			@Override
			public void run() {
				g.setBackgroundPattern(bg);
				g.setForegroundPattern(fg);
			}
		}

		// Initial state
		Font f = new Font(null, "Helvetica", 50, SWT.BOLD); //$NON-NLS-1$
		resources.push(f);
		g.setFont(f);
		g.setBackgroundColor(ColorConstants.yellow);
		g.pushState();

		Pattern gradient = new Pattern(null, 0, 0, 80, 40, ColorConstants.gray, ColorConstants.black);
		Pattern image = new Pattern(null, TestImages.depth_24);

		resources.push(gradient);
		resources.push(image);

		Runnable[] tests = new Runnable[1];
		tests[0] = new SetPattern(image, gradient);
		performTestcase(() -> g.fillText("W", 0, 0), tests); //$NON-NLS-1$

	}

	@Test
	public void testGetAbsoluteScale() {
		g.pushState();
		assertEquals(1.0, g.getAbsoluteScale());

		g.scale(2.0);
		assertEquals(2.0, g.getAbsoluteScale());
		g.restoreState();

		g.scale(2.0f, 1.5f);
		g.rotate(45);
		assertEquals(1.73, g.getAbsoluteScale(), 1e-2);
		g.restoreState();

		g.scale(7.79f, 1.3333f);
		g.rotate(14.753f);
		assertEquals(3.22, g.getAbsoluteScale(), 1e-2);
		g.restoreState();
	}
}