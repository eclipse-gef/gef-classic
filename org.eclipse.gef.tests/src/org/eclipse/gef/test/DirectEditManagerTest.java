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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.ui.PlatformUI;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.test.utils.TestGraphicalEditPart;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.gef.ui.parts.AbstractEditPartViewer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DirectEditManagerTest {
	private Shell shell;
	private EditDomain editDomain;
	private TestDirectEditManager editManager;
	private List<Throwable> throwables;

	@BeforeEach
	public void setUp() {
		shell = PlatformUI.getWorkbench().getDisplay()
				.syncCall(() -> PlatformUI.getWorkbench().getDisplay().getActiveShell());
		editDomain = new EditDomain();
		throwables = new ArrayList<>();
		editManager = null;
	}

	@Test
	public void testComboCellEditorChangeProcessed() {
		AtomicBoolean comboChangeCommandSubmitted = new AtomicBoolean();
		editDomain.getCommandStack().addCommandStackEventListener(event -> comboChangeCommandSubmitted.set(true));

		testWith(new DirectGraphicalEditPart() {
			@Override
			public Command getCommand(Request request) {
				return new Command() {
				};
			}
		});

		assertTrue(comboChangeCommandSubmitted.get());
		assertTrue(throwables.isEmpty(), () -> "Internal exception occurred: " + throwables); //$NON-NLS-1$
	}

	@Test
	public void testComboCellEditorDisposedTwice() {
		testWith(new DirectGraphicalEditPart() {
			@Override
			public Command getCommand(Request request) {
				return new Command() {
					@Override
					public void execute() {
						editManager.bringDown();
					}
				};
			}
		});

		assertTrue(throwables.isEmpty(), () -> "Internal exception occurred: " + throwables); //$NON-NLS-1$
	}

	private void testWith(GraphicalEditPart editPart) {
		editManager = new TestDirectEditManager(editPart);

		PlatformUI.getWorkbench().getDisplay().syncExec(() -> {
			editManager.show();
			// Change value of combo cell editor
			editManager.getCellEditor().setValue(0);
			// Make combo cell editor lose focus to apply its value
			shell.setFocus();
		});
	}

	private class TestDirectEditManager extends DirectEditManager {
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
			try {
				return super.getCellEditor();
			} catch (Throwable exception) {
				throwables.add(exception);
				throw exception;
			}
		}

		@Override
		public void bringDown() {
			try {
				super.bringDown();
			} catch (Throwable exception) {
				throwables.add(exception);
				throw exception;
			}
		}

		@Override
		public void showFeedback() {
		}

		@Override
		protected void initCellEditor() {
		}
	}

	private class DirectGraphicalEditPart extends TestGraphicalEditPart {
		@Override
		public EditPartViewer getViewer() {
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
					return shell;
				}

				@Override
				public EditDomain getEditDomain() {
					return editDomain;
				}
			};
		}
	}

}
