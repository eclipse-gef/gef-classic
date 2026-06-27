/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.zest.core.viewers.decorators.GraphLabelDecorator;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

import org.eclipse.draw2d.ColorConstants;

/**
 * This snippet shows how to use the {@link GraphLabelDecorator} as label
 * provider.
 */
public class GraphJFaceSnippet11 {

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
	}

	static class MyGraphLabelDecorator extends GraphLabelDecorator {
		@Override
		public void decorateConnection(GraphConnection connection) {
			connection.setLineColor(ColorConstants.red);
			connection.setText(connection.getSource().getText() + '-' + connection.getDestination().getText());
		}

		@Override
		public void decorateNode(GraphNode node) {
			node.setBackgroundColor(ColorConstants.blue);
		}
	}

	static GraphViewer viewer = null;

	public static void main(String[] args) {
		Shell shell = new Shell();
		Display d = shell.getDisplay();
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);
		shell.setText(Messages.GraphJFaceSnippet11_Title);

		viewer = new GraphViewer(shell, SWT.NONE);
		viewer.setContentProvider(new MyContentProvider());
		viewer.setLabelProvider(new MyGraphLabelDecorator());
		viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm());
		viewer.setInput(new Object());

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
