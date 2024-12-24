/*******************************************************************************
 * Copyright 2005-2007, 2024, CHISEL Group, University of Victoria, Victoria,
 *                            BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.zest.examples.jface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

/**
 * This snippet shows how to use the IGraphEntityContentProvider to build a
 * graph.
 *
 * @author Ian Bull
 *
 */
public class GraphJFaceSnippet1 {

	/**
	 * The Content Provider
	 *
	 * @author irbull
	 *
	 */
	static class MyContentProvider implements IGraphEntityContentProvider {

		@Override
		public Object[] getConnectedTo(Object entity) {
			if (entity.equals(Messages.First)) {
				return new Object[] { Messages.Second };
			}
			if (entity.equals(Messages.Second)) {
				return new Object[] { Messages.Third };
			}
			if (entity.equals(Messages.Third)) {
				return new Object[] { Messages.First };
			}
			return null;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return new String[] { Messages.First, Messages.Second, Messages.Third };
		}

		@Override
		public void dispose() {

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}
	}

	static class MyLabelProvider extends LabelProvider {
		final Image image = Display.getDefault().getSystemImage(SWT.ICON_WARNING);

		@Override
		public Image getImage(Object element) {
			if (element instanceof String) {
				return image;
			}
			return null;
		}

		@Override
		public String getText(Object element) {
			if (element instanceof String) {
				return element.toString();
			}
			return null;
		}

	}

	static GraphViewer viewer = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Shell shell = new Shell();
		Display d = shell.getDisplay();
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setSize(400, 400);
		shell.setText(Messages.GraphJFaceSnippet1_Title);
		Button button = new Button(shell, SWT.PUSH);
		button.setText(Messages.GraphJFaceSnippet1_Reload);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setInput(new Object());
			}

		});
		viewer = new GraphViewer(shell, SWT.NONE);

		viewer.setContentProvider(new MyContentProvider());
		viewer.setLabelProvider(new MyLabelProvider());
		viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm());
		viewer.addSelectionChangedListener(event -> System.out.println("Selection changed: " + (event.getSelection()))); //$NON-NLS-1$
		viewer.setInput(new Object());

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}

	}
}
