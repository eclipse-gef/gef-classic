/*******************************************************************************
 * Copyright (c) 2003, 2024 IBM Corporation and others.
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

package org.eclipse.draw2d.examples.uml;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.SeparatorBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;

public class UMLClassFigure extends Figure {

	static final Color BG = new Color(242, 240, 255);

	static Image classImage = new Image(null, UMLClassFigure.class.getResourceAsStream("class_obj.gif")); //$NON-NLS-1$

	static Font BOLD = new Font(null, "", 10, SWT.BOLD); //$NON-NLS-1$

	public UMLClassFigure() {
		Label header = new Label("ClassName", classImage); //$NON-NLS-1$
		header.setFont(BOLD);
		header.setBorder(new MarginBorder(3, 5, 3, 5));

		Figure attributes = new Figure();
		ToolbarLayout layout = new ToolbarLayout();
		attributes.setLayoutManager(layout);
		layout.setStretchMinorAxis(false);
		attributes.add(new Label("name : String")); //$NON-NLS-1$
		attributes.add(new Label("ID : String")); //$NON-NLS-1$
		Border attributeBorder = new SeparatorBorder(new Insets(3, 5, 3, 5), PositionConstants.TOP);
		attributes.setBorder(attributeBorder);

		Figure methods = new Figure();
		layout = new ToolbarLayout();
		methods.setLayoutManager(layout);
		layout.setStretchMinorAxis(false);
		methods.add(new Label("foo() : int")); //$NON-NLS-1$
		methods.add(new Label("bar() : char")); //$NON-NLS-1$
		Border methodBorder = new SeparatorBorder(new Insets(3, 5, 3, 5), PositionConstants.TOP);
		methods.setBorder(methodBorder);

		setBorder(new LineBorder());
		setLayoutManager(new ToolbarLayout());

		add(header);
		add(attributes);
		add(methods);
		setOpaque(true);
		setBackgroundColor(BG);
	}

}
