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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;

public class Bug23147 {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell();
		LightweightSystem lws = new LightweightSystem(shell);

		Figure panel = new Figure();
		panel.setLayoutManager(new FlowLayout());
		lws.setContents(panel);

		Image image = new Image(null, 20, 20);
		Label label = new Label("foo", image); //$NON-NLS-1$
		panel.add(label);
		label.setBackgroundColor(ColorConstants.yellow);
		label.setOpaque(true);

		shell.setSize(400, 300);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}