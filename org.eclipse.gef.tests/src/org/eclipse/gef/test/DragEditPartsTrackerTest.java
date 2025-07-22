/*******************************************************************************
 * Copyright (c) 2006, 2025 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.test.utils.TestGraphicalEditPart;
import org.eclipse.gef.tools.DragEditPartsTracker;

import org.junit.jupiter.api.Test;

public class DragEditPartsTrackerTest {

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
		public List<? extends EditPart> createOperationSet() {
			return super.createOperationSet();
		}
	}

	@Test
	public void testCreateOperationSet() {
		TestDragEditPartsTracker dept = new TestDragEditPartsTracker(new TestGraphicalEditPart());

		dept.setEditDomain(new DefaultEditDomain(new DummyEditorPart()));
		dept.activate();
		List<? extends EditPart> operationSet = dept.createOperationSet();
		assertTrue(operationSet != null);
		dept.deactivate();
	}

}
