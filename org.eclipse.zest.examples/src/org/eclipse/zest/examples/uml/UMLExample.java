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
package org.eclipse.zest.examples.uml;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.IContainer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;

/**
 * Adds a selection listener to the nodes to tell when a selection event has
 * happened.
 *
 * @author Ian Bull
 *
 */
public class UMLExample {
	public static Color classColor = null;
	private static Graph g;

	public static IFigure createClassFigure1(Font classFont, Image classImage, Image publicField, Image privateField) {
		Label classLabel1 = new Label("Table", classImage);
		classLabel1.setFont(classFont);

		UMLClassFigure classFigure = new UMLClassFigure(classLabel1);
		Label attribute1 = new Label("columns: Column[]", privateField);

		Label attribute2 = new Label("rows: Row[]", privateField);

		Label method1 = new Label("getColumns(): Column[]", publicField);
		Label method2 = new Label("getRows(): Row[]", publicField);
		classFigure.getAttributesCompartment().add(attribute1);
		classFigure.getAttributesCompartment().add(attribute2);
		classFigure.getMethodsCompartment().add(method1);
		classFigure.getMethodsCompartment().add(method2);
		classFigure.setSize(-1, -1);

		return classFigure;
	}

	public static IFigure createClassFigure2(Font classFont, Image classImage, Image publicField, Image privateField) {
		Label classLabel2 = new Label("Column", classImage);
		classLabel2.setFont(classFont);

		UMLClassFigure classFigure = new UMLClassFigure(classLabel2);
		Label attribute3 = new Label("columnID: int", privateField);
		Label attribute4 = new Label("items: List", privateField);

		Label method3 = new Label("getColumnID(): int", publicField);
		Label method4 = new Label("getItems(): List", publicField);

		classFigure.getAttributesCompartment().add(attribute3);
		classFigure.getAttributesCompartment().add(attribute4);
		classFigure.getMethodsCompartment().add(method3);
		classFigure.getMethodsCompartment().add(method4);
		classFigure.setSize(-1, -1);

		return classFigure;
	}

	static class UMLNode extends GraphNode {

		IFigure customFigure = null;

		public UMLNode(IContainer graphModel, int style, IFigure figure) {
			super(graphModel, style, figure);
		}

		@Override
		protected IFigure createFigureForModel() {
			return (IFigure) this.getData();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Shell shell = new Shell();
		Display d = shell.getDisplay();
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);
		classColor = new Color(255, 255, 206);

		Font classFont = new Font(null, "Arial", 12, SWT.BOLD);
		Image classImage = new Image(Display.getDefault(), UMLClassFigure.class.getResourceAsStream("class_obj.gif"));
		Image privateField = new Image(Display.getDefault(),
				UMLClassFigure.class.getResourceAsStream("field_private_obj.gif"));
		Image publicField = new Image(Display.getDefault(),
				UMLClassFigure.class.getResourceAsStream("methpub_obj.gif"));

		g = new Graph(shell, SWT.NONE);
		g.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		GraphContainer c = new GraphContainer(g, SWT.NONE);
		c.setText("A UML Container");
		UMLNode n = new UMLNode(c, SWT.NONE, createClassFigure1(classFont, classImage, publicField, privateField));

		GraphNode n1 = new UMLNode(g, SWT.NONE, createClassFigure1(classFont, classImage, publicField, privateField));
		GraphNode n2 = new UMLNode(g, SWT.NONE, createClassFigure2(classFont, classImage, publicField, privateField));

		new GraphConnection(g, SWT.NONE, n1, n2);
		new GraphConnection(g, SWT.NONE, n, n1);

		c.setLayoutAlgorithm(new SpringLayoutAlgorithm(), true);
		g.setLayoutAlgorithm(new SpringLayoutAlgorithm(), true);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
		classColor.dispose();
		classFont.dispose();
		classImage.dispose();
		publicField.dispose();
		privateField.dispose();

	}
}
