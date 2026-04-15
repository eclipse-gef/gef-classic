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
package swt.bugs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class BidiMixedOrientations {

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell();

		shell.addPaintListener(e -> {
			TextLayout tl = new TextLayout(display);
			tl.setOrientation(SWT.RIGHT_TO_LEFT);
			tl.setText("\u202BIBM ABC \ufeeb\ufeec\ufeec"); //$NON-NLS-1$
			tl.setStyle(new TextStyle(null, new Color(100, 200, 150), null), 2, 2);
			tl.setStyle(new TextStyle(null, new Color(100, 200, 150), null), 10, 10);
			tl.draw(e.gc, 10, 10);
			tl.dispose();
		});

		shell.setSize(400, 300);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}

}