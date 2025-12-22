/*******************************************************************************
 * Copyright (c) 2025 Patrick Ziegler and others.
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
package org.eclipse.zest.examples.jface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

/**
 * This snippet shows how to use the "zoom to" mechanism of the zoom manager.
 * When double-clicking on a graph item, the viewer jumps and zooms into the
 * selected node.
 */
public class GraphJFaceSnippet10 {
	static class MyContentProvider implements IGraphEntityContentProvider {
		@Override
		public Object[] getConnectedTo(Object entity) {
			return null;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return new String[] { Messages.First, Messages.Second, Messages.Third };
		}
	}

	static GraphViewer viewer = null;

	public static void main(String[] args) {
		Shell shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
		shell.setLayout(new GridLayout());
		shell.setSize(400, 400);
		shell.setText(Messages.GraphJFaceSnippet10_Title);
		Button button = new Button(shell, SWT.PUSH);
		button.setText(Messages.GraphJFaceSnippet10_Reload);
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.zoomTo(0, 0, 0, 0);
			}
		});

		viewer = new GraphViewer(shell, SWT.NONE);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.setContentProvider(new MyContentProvider());
		viewer.setLayoutAlgorithm(new GridLayoutAlgorithm());
		viewer.addDoubleClickListener(event -> {
			Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();
			GraphNode node = viewer.getNodesMap().get(element);
			if (node != null) {
				Point location = node.getLocation();
				Dimension size = node.getSize();
				viewer.zoomTo(location.x, location.y, size.width, size.height);
			}
		});
		viewer.setInput(new Object());

		shell.open();

		Display d = shell.getDisplay();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
