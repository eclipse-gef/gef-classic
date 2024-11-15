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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;

import org.junit.Before;
import org.junit.Test;

public class GraphicalViewerTest {
	private GraphicalViewer viewer;

	@Before
	public void setUp() {
		viewer = PlatformUI.getWorkbench().getDisplay().syncCall(GraphicalViewerImpl::new);
	}

	/**
	 * Appending a {@code null} edit part shouldn't leave the viewer in an
	 * inconsistent state.
	 */
	@Test
	public void testAppendNullSelection() {
		assertThrows(NullPointerException.class, () -> viewer.appendSelection(null));
		assertTrue(viewer.getSelectedEditParts().isEmpty());
	}

	/**
	 * Setting a {@code null} edit part shouldn't leave the viewer in an
	 * inconsistent state.
	 */
	@Test
	public void testSetNullSelection() {
		IStructuredSelection selection = new StructuredSelection(new Object[] { null });
		assertThrows(NullPointerException.class, () -> viewer.setSelection(selection));
		assertTrue(viewer.getSelectedEditParts().isEmpty());
	}

	@Test
	public void testDeleteSelection() {
		IStructuredSelection selection = new StructuredSelection(new DummyEditPart());
		viewer.setSelection(selection);

		DeleteAction deleteAction = new DeleteAction((IWorkbenchPart) null);
		deleteAction.setSelectionProvider(viewer);
		deleteAction.update();
		deleteAction.run();
	}

	private static class DummyEditPart extends AbstractGraphicalEditPart {
		@Override
		protected IFigure createFigure() {
			return new Figure();
		}

		@Override
		protected void createEditPolicies() {
			// nothing to do
		}

		@Override
		public Command getCommand(Request request) {
			assertEquals(request.getType(), REQ_DELETE);
			if (REQ_DELETE.equals(request.getType())) {
				GroupRequest groupRequest = (GroupRequest) request;
				DummyEditPart newEditPart = new DummyEditPart();

				@SuppressWarnings("unchecked")
				List<EditPart> editParts = (List<EditPart>) groupRequest.getEditParts();
				editParts.set(0, newEditPart);
				assertEquals(groupRequest.getEditParts(), List.of(newEditPart));
			}
			return super.getCommand(request);
		}
	}
}
