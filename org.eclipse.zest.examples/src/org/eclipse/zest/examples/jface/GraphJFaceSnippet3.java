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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;

/**
 * This snippet uses a very simple file format to read a graph. Edges are listed
 * on a new line in a file as such: a calls b b calls c c calld d
 *
 * The content provider creates an edge for each line in the file and names the
 * sources and destination from the line.
 *
 *
 * @author Ian Bull
 *
 */
public class GraphJFaceSnippet3 {

	static class SimpleGraphContentProvider implements IGraphContentProvider {

		private StringTokenizer graph;

		@Override
		public Object getDestination(Object rel) {
			String string = (String) rel;
			String[] parts = string.split(" "); //$NON-NLS-1$
			return parts[2];
		}

		@Override
		public Object[] getElements(Object input) {
			List<String> listOfEdges = new ArrayList<>();
			while (graph.hasMoreTokens()) {
				listOfEdges.add(graph.nextToken());
			}
			return listOfEdges.toArray();
		}

		@Override
		public Object getSource(Object rel) {
			String string = (String) rel;
			String[] parts = string.split(" "); //$NON-NLS-1$
			return parts[0];
		}

		@Override
		public void dispose() {

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput != null) {
				graph = new StringTokenizer((String) newInput, System.lineSeparator());
			}
		}

	}

	static GraphViewer viewer = null;

	public static void main(String[] args) throws IOException {
		Shell shell = new Shell();
		Display display = shell.getDisplay();
		shell.setText(Messages.GraphJFaceSnippet3_Title);

		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setSize(400, 400);

		viewer = new GraphViewer(shell, SWT.NONE);
		viewer.setContentProvider(new SimpleGraphContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		viewer.setLayoutAlgorithm(new RadialLayoutAlgorithm());

		shell.open();

		try (InputStream is = GraphJFaceSnippet3.class.getResourceAsStream("SimpleGraph.sgf")) { //$NON-NLS-1$
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
			StringBuilder stringBuffer = new StringBuilder();
			while (bufferedReader.ready()) {
				stringBuffer.append(bufferedReader.readLine()).append(System.lineSeparator());
			}
			viewer.setInput(stringBuffer.toString());
		}

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
