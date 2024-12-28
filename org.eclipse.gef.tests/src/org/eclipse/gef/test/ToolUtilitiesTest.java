/*******************************************************************************
 * Copyright (c) 2006, 2024 IBM Corporation and others.
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

package org.eclipse.gef.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.tools.ToolUtilities;

import org.junit.jupiter.api.Test;

public class ToolUtilitiesTest {

	private static class TestGraphicalEditPart extends AbstractGraphicalEditPart {

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
		 */
		public void addChild(EditPart ep) {
			addChild(ep, 0);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.gef.editparts.AbstractEditPart#register()
		 */
		@Override
		protected void register() {
			// do nothing
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
		 */
		@Override
		protected IFigure createFigure() {
			return new Figure();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
		 */
		@Override
		protected void createEditPolicies() {
			// do nothing
		}
	}

	@SuppressWarnings("static-method")
	@Test
	public void testFindCommonAncestorHappypath() {
		TestGraphicalEditPart editpartParent = new TestGraphicalEditPart();
		TestGraphicalEditPart editpartChild1 = new TestGraphicalEditPart();
		TestGraphicalEditPart editpartChild2 = new TestGraphicalEditPart();
		TestGraphicalEditPart editpartChild3 = new TestGraphicalEditPart();

		editpartParent.addChild(editpartChild1);
		editpartChild1.addChild(editpartChild2);
		editpartParent.addChild(editpartChild3);

		EditPart result = ToolUtilities.findCommonAncestor(editpartChild2, editpartChild3);
		assertTrue(editpartParent == result);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testFindCommonAncestorBugzilla130042() {
		TestGraphicalEditPart editpartParent = new TestGraphicalEditPart();
		EditPart editpartChild = new TestGraphicalEditPart();
		editpartParent.addChild(editpartChild);

		EditPart result = ToolUtilities.findCommonAncestor(editpartParent, editpartChild);
		assertTrue(editpartParent == result);
	}
}
