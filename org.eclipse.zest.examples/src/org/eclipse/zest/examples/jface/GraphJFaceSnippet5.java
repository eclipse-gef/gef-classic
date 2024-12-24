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
 * Contributors: The Chisel Group, University of Victoria
 ******************************************************************************/
package org.eclipse.zest.examples.jface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

/**
 * This snippet shows how the refresh works on a Zest viewer.
 */
public class GraphJFaceSnippet5 {

	static class MyContentProvider implements IGraphContentProvider {

		Object[] elements = { Messages.Rock2Paper, Messages.Paper2Scissors, Messages.Scissors2Rock };

		@Override
		public Object getDestination(Object rel) {
			if (Messages.Rock2Paper.equals(rel)) {
				return Messages.Rock;
			}
			if (Messages.Paper2Scissors.equals(rel) || Messages.Scissors2Paper.equals(rel)) {
				return Messages.Paper;
			} else if (Messages.Scissors2Rock.equals(rel)) {
				return Messages.Scissors;
			}
			return null;
		}

		@Override
		public Object[] getElements(Object input) {
			return elements;
		}

		@Override
		public Object getSource(Object rel) {
			if (Messages.Rock2Paper.equals(rel)) {
				return Messages.Paper;
			}
			if (Messages.Paper2Scissors.equals(rel) || Messages.Scissors2Paper.equals(rel)) {
				return Messages.Scissors;
			} else if (Messages.Scissors2Rock.equals(rel)) {
				return Messages.Rock;
			}
			return null;
		}

		public void setElements(Object[] elements) {
			this.elements = elements;
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

		public MyLabelProvider() {

		}

		@Override
		public String getText(Object element) {
			return element.toString();
		}

		@Override
		public Image getImage(Object element) {
			return image;
		}
	}

	static GraphViewer viewer = null;
	private static MyContentProvider contentProvider;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Shell shell = new Shell();
		Display d = shell.getDisplay();
		shell.setText(Messages.GraphJFaceSnippet5_Title);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setSize(400, 400);
		Composite parent = new Composite(shell, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		buildViewer(parent);
		buildButton(parent);
		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}

	}

	private static void buildButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(Messages.GraphJFaceSnippet5_Refresh);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				contentProvider.setElements(
						new Object[] { Messages.Rock2Paper, Messages.Scissors2Paper, Messages.Scissors2Rock });
				viewer.refresh();
			}
		});
	}

	private static void buildViewer(Composite parent) {
		viewer = new GraphViewer(parent, SWT.NONE);
		viewer.getGraphControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		contentProvider = new MyContentProvider();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new MyLabelProvider());
		viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm());
		viewer.setInput(new Object());
	}
}
