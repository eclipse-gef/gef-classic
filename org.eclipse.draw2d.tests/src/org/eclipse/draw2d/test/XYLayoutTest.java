/*******************************************************************************
 * Copyright (c) 2000, 2024 IBM Corporation and others.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.draw2d.Container;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class XYLayoutTest {
	protected static final Rectangle EMPTY_RECTANGLE = new Rectangle(0, 0, 0, 0);
	protected XYLayout layout;
	protected RectangleFigure figure;
	protected RectangleFigure dummy;
	protected Container contents;
	protected Rectangle constraint;
	protected Logger log;
	protected Handler logListener;
	protected List<String> logMessages;

	@BeforeEach
	public void setUp() {
		constraint = new Rectangle(0, 0, 100, -1);
		figure = new RectangleFigure();
		figure.setPreferredSize(100, 150);
		dummy = new RectangleFigure();
		layout = new XYLayout();
		contents = new Container(layout);
		contents.add(figure, constraint);
		contents.add(dummy);
		logMessages = new ArrayList<>();
		logListener = new Handler() {
			@Override
			public void publish(LogRecord record) {
				logMessages.add(record.getMessage());
			}

			@Override
			public void flush() {
			}

			@Override
			public void close() throws SecurityException {
			}
		};
		log = Logger.getLogger(XYLayout.class.getName());
		log.addHandler(logListener);
	}

	@AfterEach
	public void tearDown() {
		log.removeHandler(logListener);
	}

	@Test
	public void testPreferredSize() {
		Dimension d = contents.getPreferredSize();

		assertEquals(100, d.width);
		assertEquals(150, d.height);
	}

	/**
	 * Use Case: Constraint must be of type {@link Rectangle} -> no error
	 */
	@Test
	public void testConstraint() {
		Rectangle r = (Rectangle) layout.getConstraint(figure);
		assertEquals(constraint, r);
	}

	/**
	 * Use Case: Constraint must be of type {@link Rectangle} -> error
	 */
	@Test
	public void testInvalidConstraint() {
		contents.add(figure, (Object) 1);

		assertEquals(logMessages.size(), 1);
		assertEquals(logMessages.get(0), "XYLayout was given Integer as constraint for Figure. Rectangle expected!"); //$NON-NLS-1$
	}

	/**
	 * Use Case: No error is logged for valid constraints
	 */
	@Test
	public void testValidConstraint() {
		contents.add(figure, new Rectangle());

		assertTrue(logMessages.isEmpty());
		assertNotNull(layout.getConstraint(figure));
	}

	/**
	 * Use Case: No constraint specified for dummy figure -> skip
	 */
	@Test
	public void testLayoutMissingConstraint() {
		assertEquals(dummy.getBounds(), EMPTY_RECTANGLE);
		layout.layout(contents);
		assertEquals(dummy.getBounds(), EMPTY_RECTANGLE);
	}

	/**
	 * Use Case: Constraint describes the position of the figure
	 */
	@Test
	public void testLayout() {
		assertEquals(figure.getBounds(), EMPTY_RECTANGLE);
		layout.layout(contents);
		assertNotEquals(figure.getBounds(), EMPTY_RECTANGLE);
	}
}
