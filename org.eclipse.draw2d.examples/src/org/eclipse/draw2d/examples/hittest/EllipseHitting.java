/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d.examples.hittest;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Dimension;

public class EllipseHitting {

public static void main(String[] args) {
	Display display = new Display();
	Shell shell = new Shell();
	
	LightweightSystem lws = new LightweightSystem(shell);
	
	Figure panel = new Figure();
	panel.setLayoutManager(new FlowLayout());
	panel.setBackgroundColor(ColorConstants.white);

	MouseMotionListener listener = new MouseMotionListener.Stub() {
		public void mouseEntered(MouseEvent me) {
			((Shape)me.getSource()).setBackgroundColor(ColorConstants.yellow);
		}
		public void mouseExited(MouseEvent me) {
			((Shape)me.getSource()).setBackgroundColor(ColorConstants.white);
		}
	};

	for (int i=1; i <= 4; i++){
		Ellipse e = new Ellipse();
		e.setFill(true);
		e.setPreferredSize(new Dimension (20 + 10*i + i%2, 60 - 10*i + i/2));
		e.addMouseMotionListener(listener);
		panel.add(e);
	}
	
	lws.setContents(panel);
	shell.setSize(400,300);
	shell.open();
	
	while (!shell.isDisposed())
		if (!display.readAndDispatch())
			display.sleep();

}

}
