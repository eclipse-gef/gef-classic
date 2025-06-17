/*******************************************************************************
 * Copyright (c) 2025 Vector Informatik GmbH and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.gef.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.ui.PlatformUI;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.gef.ui.parts.AbstractEditPartViewer;

import org.junit.jupiter.api.Test;

public class DirectEditManagerTest {

	@SuppressWarnings("static-method")
	@Test
	public void testComboCellEditorChangeProcessed() {
		Shell shell = PlatformUI.getWorkbench().getDisplay()
				.syncCall(() -> PlatformUI.getWorkbench().getDisplay().getActiveShell());
		EditDomain editDomain = new EditDomain();
		TestDirectEditManager editManager = new TestDirectEditManager(createDummyEditPart(shell, editDomain));
		AtomicBoolean comboChangeCommandSubmitted = new AtomicBoolean();
		editDomain.getCommandStack().addCommandStackEventListener(event -> comboChangeCommandSubmitted.set(true));

		PlatformUI.getWorkbench().getDisplay().syncExec(() -> {
			editManager.show();
			// Change value of combo cell editor
			editManager.getCellEditor().setValue(0);
			// Make combo cell editor lose focus to apply its value
			shell.setFocus();
		});

		assertTrue(comboChangeCommandSubmitted.get());
	}

	private static class TestDirectEditManager extends DirectEditManager {
		public TestDirectEditManager(GraphicalEditPart source) {
			super(source, ComboBoxCellEditor.class, cellEditor -> {
			});
		}

		@Override
		protected CellEditor createCellEditorOn(Composite composite) {
			return new ComboBoxCellEditor(composite, new String[] { "test" }); //$NON-NLS-1$
		}

		@Override
		public CellEditor getCellEditor() {
			return super.getCellEditor();
		}

		@Override
		public void showFeedback() {
		}

		@Override
		protected void initCellEditor() {
		}
	}

	private static GraphicalEditPart createDummyEditPart(Control control, EditDomain editDomain) {
		return new AbstractGraphicalEditPart() {
			@Override
			protected void createEditPolicies() {
			}

			@Override
			public org.eclipse.gef.EditPartViewer getViewer() {
				return new AbstractEditPartViewer() {
					@Override
					public EditPart findObjectAtExcluding(Point location, Collection<IFigure> exclusionSet,
							Conditional conditional) {
						return null;
					}

					@Override
					public Control createControl(Composite parent) {
						return parent;
					}

					@Override
					public Control getControl() {
						return control;
					}

					@Override
					public org.eclipse.gef.EditDomain getEditDomain() {
						return editDomain;
					}
				};
			}

			@Override
			public Command getCommand(Request request) {
				return new Command() {
				};
			}

			@Override
			protected IFigure createFigure() {
				return new Figure();
			}
		};
	}

}
