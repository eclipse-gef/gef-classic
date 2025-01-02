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
package org.eclipse.zest.examples.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.zest.core.widgets.CGraphNode;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.examples.Messages;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PolylineShape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 *
 * This snippet shows how to create a curved connection using Zest.
 *
 * @author Ian Bull
 *
 */
public class GraphSnippet13 {
	private static Graph g;

	public static IFigure createPersonFigure(Image headImage) {
		Figure person = new Figure();
		person.setLayoutManager(new FreeformLayout());
		IFigure head = null;
		if (headImage != null) {
			headImage = new Image(headImage.getDevice(), headImage.getImageData().scaledTo(40, 50));
			head = new ImageFigure(headImage);
		} else {
			head = new Ellipse();
		}
		head.setSize(40, 50);

		PolylineShape body = new PolylineShape();
		body.setLineWidth(1);
		body.setStart(new Point(20, 40));
		body.setEnd(new Point(20, 100));
		body.setBounds(new Rectangle(0, 0, 40, 100));

		PolylineShape leftLeg = new PolylineShape();
		leftLeg.setLineWidth(1);
		leftLeg.setStart(new Point(20, 100));
		leftLeg.setEnd(new Point(0, 130));
		leftLeg.setBounds(new Rectangle(0, 0, 40, 130));

		PolylineShape rightLeg = new PolylineShape();
		rightLeg.setLineWidth(1);
		rightLeg.setStart(new Point(20, 100));
		rightLeg.setEnd(new Point(40, 130));
		rightLeg.setBounds(new Rectangle(0, 0, 40, 130));

		PolylineShape leftArm = new PolylineShape();
		leftArm.setLineWidth(1);
		leftArm.setStart(new Point(20, 60));
		leftArm.setEnd(new Point(0, 50));
		leftArm.setBounds(new Rectangle(0, 0, 40, 130));

		PolylineShape rightArm = new PolylineShape();
		rightArm.setLineWidth(1);
		rightArm.setStart(new Point(20, 60));
		rightArm.setEnd(new Point(40, 50));
		rightArm.setBounds(new Rectangle(0, 0, 40, 130));

		person.add(head);
		person.add(body);
		person.add(leftLeg);
		person.add(rightLeg);
		person.add(leftArm);
		person.add(rightArm);
		person.setSize(40, 130);
		return person;
	}

	public static void main(String[] args) {
		Shell shell = new Shell();
		final Display d = shell.getDisplay();
		shell.setText(Messages.GraphSnippet13_Title);
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		g = new Graph(shell, SWT.NONE);
		g.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				for (GraphItem graphItem : g.getSelection()) {
					if (graphItem instanceof CGraphNode graphNode) {
						IFigure figure = graphNode.getFigure();
						figure.setBackgroundColor(ColorConstants.blue);
						figure.setForegroundColor(ColorConstants.blue);
					}
				}
				for (GraphNode graphItem : g.getNodes()) {
					if (graphItem instanceof CGraphNode graphNode) {
						if (!g.getSelection().contains(graphNode)) {
							IFigure figure = graphNode.getFigure();
							figure.setBackgroundColor(ColorConstants.black);
							figure.setForegroundColor(ColorConstants.black);
						}
					}
				}
			}
		});

		Image zx = new Image(d, GraphSnippet13.class.getResourceAsStream("/zxsnow.png")); //$NON-NLS-1$
		IFigure tooltip = new Figure();
		tooltip.setBorder(new MarginBorder(5, 5, 5, 5));
		FlowLayout layout = new FlowLayout(false);
		layout.setMajorSpacing(3);
		layout.setMinorAlignment(3);
		tooltip.setLayoutManager(new FlowLayout(false));
		tooltip.add(new ImageFigure(zx));
		tooltip.add(new Label(Messages.GraphSnippet13_Tooltip_Name));
		tooltip.add(new Label(Messages.GraphSnippet13_Tooltip_Location));

		Image ibull = new Image(d, GraphSnippet13.class.getResourceAsStream("/ibull.jpg")); //$NON-NLS-1$
		GraphContainer c1 = new GraphContainer(g, SWT.NONE);
		c1.setText(Messages.GraphSnippet13_Canada);
		GraphContainer c2 = new GraphContainer(g, SWT.NONE);
		c2.setText(Messages.GraphSnippet13_USA);

		GraphNode n1 = new GraphNode(c1, SWT.NONE);
		n1.setText(Messages.GraphSnippet13_Node1);
		n1.setSize(200, 100);
		GraphNode n2 = new GraphNode(c2, SWT.NONE);
		n2.setText(Messages.GraphSnippet13_Node2);
		n2.setTooltip(tooltip);

		GraphConnection connection = new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, n1, n2);
		connection.setCurveDepth(-30);
		GraphConnection connection2 = new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, n2, n1);
		connection2.setCurveDepth(-30);

		g.setLayoutAlgorithm(new SpringLayoutAlgorithm(), true);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
		zx.dispose();
		ibull.dispose();
	}

}
