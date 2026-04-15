
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @since 3.1
 */
public class InfoTextLayoutHitTest {

	static Image image;
	static Font font = new Font(null, "", 30, 0); //$NON-NLS-1$
	static Button selectFont;
	static TextLayout layout;
	static Color wheel[] = new Color[6];
	static int wheelIndex;
	static Color defaultColor;
	static Point mouse;

	public static void main(String[] args) {

		final Display display = Display.getDefault();
		final Shell shell = new Shell();

		wheel[0] = new Color(255, 255, 100);
		wheel[1] = new Color(255, 160, 160);
		wheel[2] = new Color(255, 100, 255);
		wheel[3] = new Color(160, 160, 255);
		wheel[4] = new Color(100, 255, 255);
		wheel[5] = new Color(160, 255, 160);
		defaultColor = new Color(255, 50, 50);

		layout = new TextLayout(display);
		layout.setFont(font);
		layout.setText("GEF! @ This is a test for hit testing \ufeec\ufeeb\ufeed bidi"); ////$NON-NLS-1$
		// layout.setStyle(new TextStyle(null, new Color(100, 200, 150), null),
		// 10, 13);

		int width = 290;
		int height;
		layout.setWidth(width - 40);
		height = layout.getBounds().height + 40;

		image = new Image(display, width, height);
		GC gc = new GC(image);

		int trailing[] = new int[1];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int offset = layout.getOffset(x - 20, y - 20, trailing);

				if (offset == -1) {
					gc.setForeground(defaultColor);
				} else {
					gc.setForeground(wheel[(offset + trailing[0]) % 6]);
				}
				if (layout.getLocation(offset, trailing[0] == 1).x != x - 20) {
					gc.drawPoint(x, y);
				}
			}
		}
		gc.setForeground(new Color(0, 0, 0));
		layout.draw(gc, 20, 20);
		gc.dispose();

		shell.addPaintListener(e -> {
			e.gc.drawImage(image, 0, 0);
			if (mouse != null) {
				int[] trail = new int[1];
				int offset = layout.getOffset(mouse, trail);
				Point where = layout.getLocation(offset, trail[0] == 1);
				e.gc.drawOval(where.x + 19, where.y + 19, 2, 2);
			}
		});

		shell.addMouseMoveListener(e -> {
			mouse = new Point(e.x - 20, e.y - 20);
			shell.redraw();
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