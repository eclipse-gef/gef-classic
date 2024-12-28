/*******************************************************************************
 * Copyright (c) 2024 Patrick Ziegler and others.
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

package org.eclipse.gef.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.Platform;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.internal.ui.rulers.RulerFigure;
import org.eclipse.gef.internal.ui.rulers.RulerLayout;
import org.eclipse.gef.rulers.RulerProvider;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RulerLayoutTests {
	private static final Rectangle EMPTY_RECTANGLE = new Rectangle(0, 0, 0, 0);
	private RulerLayout rulerLayout;
	private Figure figure;
	private Figure dummy;
	private Figure container;
	private int constraint;
	protected ILog log;
	protected ILogListener logListener;
	protected List<String> logMessages;

	@BeforeEach
	public void setUp() {
		constraint = 42;
		figure = new Figure();
		dummy = new Figure();
		container = new RulerFigure(true, new RulerProvider() {
			@Override
			public int getUnit() {
				return RulerProvider.UNIT_CENTIMETERS;
			}
		});
		container.add(figure, (Object) constraint);
		container.add(dummy);
		rulerLayout = (RulerLayout) container.getLayoutManager();
		logMessages = new ArrayList<>();
		logListener = (status, pluginId) -> logMessages.add(status.getMessage());
		log = Platform.getLog(RulerLayout.class);
		log.addLogListener(logListener);
	}

	@AfterEach
	public void tearDown() {
		log.removeLogListener(logListener);
	}

	/**
	 * Use Case: Constraint must be of type int -> no error
	 */
	@Test
	public void testConstraint() {
		int c = rulerLayout.getConstraint(figure);
		assertEquals(constraint, c);
	}

	/**
	 * Use Case: Constraint must be of type int -> error
	 */
	@Test
	public void testInvalidConstraint() {
		container.add(figure, new Object());

		assertEquals(logMessages.size(), 1);
		assertEquals(logMessages.get(0), "RulerLayout was given Object as constraint for Figure. Integer expected!"); //$NON-NLS-1$
	}

	/**
	 * Use Case: No error is logged for valid constraints
	 */
	@Test
	public void testValidConstraint() {
		container.add(figure, (Integer) 1);

		assertTrue(logMessages.isEmpty());
		assertNotNull(rulerLayout.getConstraint(figure));
	}

	/**
	 * Use Case: No constraint specified for dummy figure -> skip
	 */
	@Test
	public void testLayoutMissingConstraint() {
		assertEquals(dummy.getBounds(), EMPTY_RECTANGLE);
		rulerLayout.layout(container);
		assertEquals(dummy.getBounds(), EMPTY_RECTANGLE);
	}

	/**
	 * Use Case: Constraint describes the position of the figure
	 */
	@Test
	public void testLayout() {
		assertEquals(figure.getBounds(), EMPTY_RECTANGLE);
		rulerLayout.layout(container);
		assertNotEquals(figure.getBounds(), EMPTY_RECTANGLE);
	}
}
