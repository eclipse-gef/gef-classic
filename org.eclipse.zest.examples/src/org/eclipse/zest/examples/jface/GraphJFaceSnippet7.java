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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IFigureProvider;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.zest.core.viewers.INestedContentProvider;
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.examples.uml.UMLClassFigure;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;

/**
 * This snippet shows how to use the INestedGraphContentProvider to create a
 * graph with Zest. In this example, getElements returns 3 edges: * Rock2Paper *
 * Paper2Scissors * Scissors2Rock
 *
 * And for each of these, the source and destination are returned in getSource
 * and getDestination.
 *
 * A label provider is also used to create the text and icons for the graph.
 *
 * @author Ian Bull
 *
 */
public class GraphJFaceSnippet7 {

	public static IFigure createClassFigure1(Font classFont, Image classImage, Image publicField, Image privateField) {
		Label classLabel1 = new Label(Messages.GraphJFaceSnippet7_Class, classImage);
		classLabel1.setFont(classFont);

		UMLClassFigure classFigure = new UMLClassFigure(classLabel1);
		Label attribute1 = new Label(Messages.GraphJFaceSnippet7_Field_Columns, privateField);

		Label attribute2 = new Label(Messages.GraphJFaceSnippet7_Field_Rows, privateField);

		Label method1 = new Label(Messages.GraphJFaceSnippet7_Method_GetColumns, publicField);
		Label method2 = new Label(Messages.GraphJFaceSnippet7_Method_GetRows, publicField);
		classFigure.getAttributesCompartment().add(attribute1);
		classFigure.getAttributesCompartment().add(attribute2);
		classFigure.getMethodsCompartment().add(method1);
		classFigure.getMethodsCompartment().add(method2);
		classFigure.setSize(-1, -1);

		return classFigure;
	}

	static class MyContentProvider implements IGraphEntityContentProvider, INestedContentProvider {

		@Override
		public Object[] getConnectedTo(Object entity) {
			if (entity.equals(Messages.First)) {
				return new Object[] { Messages.Second };
			}
			if (entity.equals(Messages.Second)) {
				return new Object[] { Messages.Third, Messages.Rock };
			}
			if (entity.equals(Messages.Third)) {
				return new Object[] { Messages.First };
			}
			if (entity.equals(Messages.Rock)) {
				return new Object[] { Messages.Paper };
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

		@Override
		public Object[] getChildren(Object element) {
			return new Object[] { Messages.Rock, Messages.Paper, Messages.Scissors };
		}

		@Override
		public boolean hasChildren(Object element) {
			return element.equals(Messages.First);
		}

	}

	static class MyLabelProvider extends LabelProvider implements IFigureProvider {
		final Font classFont = new Font(null, "Arial", 12, SWT.BOLD); //$NON-NLS-1$
		final Image image = Display.getDefault().getSystemImage(SWT.ICON_WARNING);
		final Image classImage = new Image(Display.getDefault(),
				UMLClassFigure.class.getResourceAsStream("class_obj.gif")); //$NON-NLS-1$
		final Image privateField = new Image(Display.getDefault(),
				UMLClassFigure.class.getResourceAsStream("field_private_obj.gif")); //$NON-NLS-1$
		final Image publicField = new Image(Display.getDefault(),
				UMLClassFigure.class.getResourceAsStream("methpub_obj.gif")); //$NON-NLS-1$

		@Override
		public Image getImage(Object element) {
			if (Messages.Rock.equals(element) || Messages.Paper.equals(element) || Messages.Scissors.equals(element)) {
				return image;
			}
			return null;
		}

		@Override
		public String getText(Object element) {
			if (element instanceof EntityConnectionData) {
				return ""; //$NON-NLS-1$
			}
			return element.toString();
		}

		@Override
		public IFigure getFigure(Object element) {
			return createClassFigure1(classFont, classImage, publicField, privateField);
		}

		@Override
		public void dispose() {
			super.dispose();
			classFont.dispose();
			classImage.dispose();
			publicField.dispose();
			privateField.dispose();
		}
	}

	static GraphViewer viewer = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Shell shell = new Shell();
		Display d = shell.getDisplay();
		shell.setText(Messages.GraphJFaceSnippet7_Title);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setSize(400, 400);
		viewer = new GraphViewer(shell, SWT.NONE);
		viewer.setContentProvider(new MyContentProvider());
		viewer.setLabelProvider(new MyLabelProvider());
		viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm());
		viewer.setInput(new Object());

		Button button = new Button(shell, SWT.PUSH);
		button.setText(Messages.GraphJFaceSnippet7_Push);
		button.addListener(SWT.Selection, e -> viewer.setInput(new Object()));

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}

	}

}
