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

package org.eclipse.gef.test;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

import org.junit.Assert;
import org.junit.Test;

public class DragEditPartsTrackerTest extends Assert {

	private static class TestGraphicalEditPart extends AbstractGraphicalEditPart {

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

	private class DummyEditorPart implements org.eclipse.ui.IEditorPart {

		@Override
		public void addPropertyListener(IPropertyListener listener) {

		}

		@Override
		public void createPartControl(Composite parent) {

		}

		@Override
		public void dispose() {

		}

		@Override
		public IWorkbenchPartSite getSite() {
			return null;
		}

		@Override
		public String getTitle() {
			return null;
		}

		@Override
		public Image getTitleImage() {
			return null;
		}

		@Override
		public String getTitleToolTip() {
			return null;
		}

		@Override
		public void removePropertyListener(IPropertyListener listener) {

		}

		@Override
		public void setFocus() {

		}

		@Override
		public IEditorInput getEditorInput() {
			return null;
		}

		@Override
		public IEditorSite getEditorSite() {
			return null;
		}

		@Override
		public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		}

		@Override
		public void doSave(IProgressMonitor monitor) {

		}

		@Override
		public void doSaveAs() {

		}

		@Override
		public boolean isDirty() {
			return false;
		}

		@Override
		public boolean isSaveAsAllowed() {
			return false;
		}

		@Override
		public boolean isSaveOnCloseNeeded() {
			return false;
		}

		@Override
		public <T> T getAdapter(final Class<T> adapter) {
			return null;
		}

	}

	private class TestDragEditPartsTracker extends DragEditPartsTracker {

		public TestDragEditPartsTracker(EditPart sourceEditPart) {
			super(sourceEditPart);
		}

		@Override
		public List createOperationSet() {
			return super.createOperationSet();
		}
	};

	@Test
	public void test_createOperationSet() {
		TestDragEditPartsTracker dept = new TestDragEditPartsTracker(new TestGraphicalEditPart());

		dept.setEditDomain(new DefaultEditDomain(new DummyEditorPart()));
		dept.activate();
		List operationSet = dept.createOperationSet();
		assertTrue(operationSet != null);
		dept.deactivate();
	}

}
