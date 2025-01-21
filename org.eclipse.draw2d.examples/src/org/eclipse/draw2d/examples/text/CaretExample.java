/*******************************************************************************
 * Copyright (c) 2004, 2023 IBM Corporation and others.
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
package org.eclipse.draw2d.examples.text;

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.examples.AbstractExample;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.text.CaretInfo;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.TextFlow;

public class CaretExample extends AbstractExample {

	TextFlow caretOwner;
	TextFlow[] content;
	Caret caret;
	private Label offsetLabel;
	private Label trailLabel;
	private Label charLabel;

	@Override
	protected IFigure createContents() {
		caret = new Caret(getFigureCanvas(), 0);
		final FlowPage page = new FlowPage() {
			@Override
			protected void paintFigure(org.eclipse.draw2d.Graphics graphics) {
				graphics.setBackgroundColor(new Color(null, 190, 220, 250));
				graphics.fillRectangle(getBounds());
			}
		};

		page.setBorder(new LineBorder(ColorConstants.darkGray, 10));
		page.setHorizontalAligment(PositionConstants.CENTER);

		final TextFlow text = new TextFlow();
		text.setText("""
				A TextFlow can be queried for an offset for a given Point relative\s\
				to it. The returned offset will be between 0 and N, where N is the length\s\
				of the figure's text.
				<Chinese>\u6700\u65B0\u6D88\u606F</Chinese>.
				<Japanese>\u65e5\u672c\u8a9e\u306e\u30da\u30fc\u30b8\u3092\u691c\u7d22\
				</Japanese>.
				<Hebrew>דפיםֱ מֱ- ישרֱֹאןץֱֹ\
				</Hebrew>
				<Arabic>نصً ثُ سٍَبُس</Arabic>"""); //$NON-NLS-1$

		text.setBorder(new TestBorder());
		text.setFont(new Font(null, "Tahoma", 20, 0)); //$NON-NLS-1$
		page.add(text);

		page.addMouseMotionListener(new MouseMotionListener.Stub() {
			@Override
			public void mouseMoved(MouseEvent me) {
				int[] trail = new int[1];
				Point where = me.getLocation();
				page.translateFromParent(where);
				int offset = text.getOffset(where, trail, null);
				showCaret(text, offset, trail);
			}

			@Override
			public void mouseExited(MouseEvent me) {

			}
		});
		return page;
	}

	@Override
	protected void hookShell(Shell shell) {
		getFigureCanvas().addKeyListener(new KeyAdapter() {
		});
		Group panel = new Group(shell, 0);
		panel.setLayout(new GridLayout());
		panel.setText("Hit info"); //$NON-NLS-1$
		panel.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		offsetLabel = new Label(panel, 0);
		offsetLabel.setText("Offset: 999"); //$NON-NLS-1$
		trailLabel = new Label(panel, 0);
		trailLabel.setText("Trail: 0"); //$NON-NLS-1$
		charLabel = new Label(panel, 0);
		charLabel.setText("Char: WW"); //$NON-NLS-1$
	}

	void showCaret(TextFlow text, int offset, int[] trailing) {
		if (caretOwner != null) {
			caretOwner.setSelection(-1, -1);
		}

		offsetLabel.setText("Offset: " + offset); //$NON-NLS-1$
		trailLabel.setText("Trail: " + trailing[0]); //$NON-NLS-1$

		caretOwner = text;
		caret.setVisible(text != null);
		CaretInfo info = text.getCaretPlacement(offset, trailing[0] != 0);
		caret.setSize(1, info.getHeight());
		caret.setLocation(info.getX(), info.getY());
		text.setSelection(offset, offset + 1);
		if (text.getText().length() == offset) {
			charLabel.setText("Char: ?"); //$NON-NLS-1$
		} else {
			charLabel.setText("Char: " + text.getText().charAt(offset)); //$NON-NLS-1$
		}
	}

	public static void main(String[] args) {
		new CaretExample().run();
	}

	@Override
	protected void sizeShell() {
		getShell().setSize(getShell().computeSize(400, -1));
	}

}
