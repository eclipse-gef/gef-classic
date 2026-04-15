/*******************************************************************************
 * Copyright (c) 2005, 2023 IBM Corporation and others.
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
package swt.transforms;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TextSize extends AbstractSWTTransform {

	public static void main(String[] args) {
		new TextSize().runTransformTest();
	}

	@Override
	protected void performPaint(PaintEvent e) {
		GC gc = e.gc;
		Point p = gc.stringExtent("Something"); //$NON-NLS-1$
		System.out.println("Un-rotated size = " + p.x + ", " + p.y); //$NON-NLS-1$ //$NON-NLS-2$
		Transform t = new Transform(null);
		t.translate(150, 30);
		t.rotate(180);
		gc.setTransform(t);
		p = gc.stringExtent("Something"); //$NON-NLS-1$
		System.out.println("Rotated size = " + p.x + ", " + p.y); //$NON-NLS-1$ //$NON-NLS-2$
		t.dispose();
		gc.drawString("Something", 0, 0, false); //$NON-NLS-1$
		gc.drawRectangle(0, 0, p.x, p.y);
	}

	@Override
	protected Shell createShell(Display display) {
		Shell shell = super.createShell(display);
		shell.setBackground(new Color(60, 100, 255));
		return shell;
	}
}