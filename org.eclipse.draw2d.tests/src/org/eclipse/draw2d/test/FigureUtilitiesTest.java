/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;

import org.junit.Test;

public class FigureUtilitiesTest {

	@SuppressWarnings("static-method")
	@Test
	public void testFindCommonAncestorHappypath() {
		IFigure figureParent = new Figure();
		IFigure figureChild1 = new Figure();
		IFigure figureChild2 = new Figure();
		IFigure figureChild3 = new Figure();

		figureParent.add(figureChild1);
		figureChild1.add(figureChild2);
		figureParent.add(figureChild3);

		IFigure result = FigureUtilities.findCommonAncestor(figureChild2, figureChild3);
		assertTrue(figureParent == result);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testFindCommonAncestorBugzilla130042() {
		IFigure figureParent = new Figure();
		IFigure figureChild = new Figure();
		figureParent.add(figureChild);

		IFigure result = FigureUtilities.findCommonAncestor(figureParent, figureChild);
		assertTrue(figureParent == result);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testFindCommonAncestorCheckFindsNearestAncestor() {
		IFigure figureGrandParent = new Figure();
		IFigure figureParent = new Figure();
		IFigure figureChild1 = new Figure();
		IFigure figureChild2 = new Figure();
		figureGrandParent.add(figureParent);
		figureParent.add(figureChild1);
		figureParent.add(figureChild2);

		IFigure result = FigureUtilities.findCommonAncestor(figureChild1, figureChild2);
		assertTrue(figureParent == result);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testFindCommonAncestorParentIsCommonAncestor() {
		IFigure figureParent = new Figure();
		IFigure figureChild1 = new Figure();
		IFigure figureChild2 = new Figure();
		figureParent.add(figureChild1);
		figureParent.add(figureChild2);

		IFigure result = FigureUtilities.findCommonAncestor(figureChild1, figureChild2);
		assertTrue(figureParent == result);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testFindCommonAncestorOrphanedChild() {
		IFigure orphanFigure = new Figure();
		IFigure figureParent = new Figure();
		IFigure figureChild = new Figure();
		figureParent.add(figureChild);

		IFigure result = FigureUtilities.findCommonAncestor(figureChild, orphanFigure);
		assertNull(result);
	}
}
