/*******************************************************************************
 * Copyright (c) 2005, 2026 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.examples.connections;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GroupBoxBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MidpointLocator;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.examples.AbstractExample;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * This class demonstrates the use of connections, anchors, and locators in
 * draw2d.
 *
 * @author delee
 */
public class ConnectionAndAnchorExample extends AbstractExample {

	public static void main(String[] args) {
		new ConnectionAndAnchorExample().run();
	}

	/**
	 * @see org.eclipse.draw2d.examples.AbstractExample#createContents()
	 */
	@Override
	protected IFigure createContents() {
		IFigure panel = new Figure();

		Figure endPointPanel = new Figure();
		endPointPanel.setBounds(new Rectangle(0, 0, 400, 400));

		endPointPanel.setBorder(new GroupBoxBorder("Midpoint Locator")); //$NON-NLS-1$

		RectangleFigure node1 = new RectangleFigure(), node2 = new RectangleFigure();
		node1.setBackgroundColor(ColorConstants.red);
		node1.setBounds(new Rectangle(40, 40, 50, 30));
		node2.setBackgroundColor(ColorConstants.blue);
		node2.setBounds(new Rectangle(200, 40, 50, 30));

		PolylineConnection conn = new PolylineConnection();
		conn.setSourceAnchor(new ChopboxAnchor(node1));
		conn.setTargetAnchor(new ChopboxAnchor(node2));
		conn.setTargetDecoration(new PolygonDecoration());

		Label label = new Label("Midpoint"); //$NON-NLS-1$
		label.setOpaque(true);
		label.setBackgroundColor(ColorConstants.buttonLightest);
		label.setBorder(new LineBorder());
		conn.add(label, new MidpointLocator(conn, 0));
		// conn.setSourceDecoration(new ConnectionLabel());

		endPointPanel.add(node1);
		endPointPanel.add(node2);
		endPointPanel.add(conn);
		new Dragger(node1);
		new Dragger(node2);

		Figure ellipsePanel = new Figure();
		ellipsePanel.setBounds(new Rectangle(420, 0, 400, 400));

		ellipsePanel.setBorder(new GroupBoxBorder("Connection endpoint Locator")); //$NON-NLS-1$

		Shape node3 = new RectangleFigure(), node4 = new RectangleFigure();
		node3.setBackgroundColor(ColorConstants.green);
		node3.setBounds(new Rectangle(480, 40, 50, 30));
		node4.setBackgroundColor(ColorConstants.black);
		node4.setBounds(new Rectangle(480, 230, 50, 30));

		PolylineConnection conn2 = new PolylineConnection();
		conn2.setSourceAnchor(new ChopboxAnchor(node3));
		conn2.setTargetAnchor(new ChopboxAnchor(node4));
		conn2.setTargetDecoration(new PolygonDecoration());

		Label endPointLabel = new Label("Endpoint"); //$NON-NLS-1$
		endPointLabel.setOpaque(true);
		endPointLabel.setBackgroundColor(ColorConstants.buttonLightest);
		endPointLabel.setBorder(new LineBorder());
		conn2.add(endPointLabel, new ConnectionEndpointLocator(conn2, true));

		Label endPointLabel2 = new Label("Endpoint"); //$NON-NLS-1$
		endPointLabel2.setOpaque(true);
		endPointLabel2.setBackgroundColor(ColorConstants.buttonLightest);
		endPointLabel2.setBorder(new LineBorder());
		ConnectionEndpointLocator endpointLocator = new ConnectionEndpointLocator(conn2, false);
		endpointLocator.setUDistance(4);
		endpointLocator.setVDistance(-3);
		conn2.add(endPointLabel2, endpointLocator);

		Label instructions = new Label(" Drag the shapes to see the Locators in action "); //$NON-NLS-1$
		instructions.setBorder(new LineBorder());
		instructions.setLocation(new Point(10, 420));
		instructions.setSize(instructions.getPreferredSize());

		ellipsePanel.add(node3);
		ellipsePanel.add(node4);
		ellipsePanel.add(conn2);

		new Dragger(node3);
		new Dragger(node4);

		panel.add(endPointPanel);
		panel.add(ellipsePanel);
		panel.add(instructions);
		return panel;
	}

	/**
	 * @see org.eclipse.draw2d.examples.AbstractExample#hookShell(Shell shell)
	 */
	@Override
	protected void hookShell(Shell shell) {
		getFigureCanvas().setSize(820, 440);
	}

	static class Dragger extends MouseMotionListener.Stub implements MouseListener {
		public Dragger(IFigure figure) {
			figure.addMouseMotionListener(this);
			figure.addMouseListener(this);
		}

		Point last;

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseDoubleClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			last = e.getLocation();
			e.consume();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// if mouse was pressed outside the figure and then dragged over figure
			if (last == null) {
				return;
			}
			Point p = e.getLocation();
			Dimension delta = p.getDifference(last);
			last = p;
			Figure f = ((Figure) e.getSource());
			f.setBounds(f.getBounds().getTranslated(delta.width, delta.height));
		}
	}

}
