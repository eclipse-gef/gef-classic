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
package org.eclipse.draw2d.examples.zoom;

import org.eclipse.swt.graphics.Color;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;

/**
 * A simple Figure that represents a UML Class Diagram.
 */
public class UMLClassFigure extends Figure {

	/** Background color of UMLFigure */
	public static final Color CLASS_COLOR = new Color(255, 255, 206);

	/** CompartmentFigures */
	private final CompartmentFigure attributeFigure = new CompartmentFigure();
	private final CompartmentFigure methodFigure = new CompartmentFigure();

	public UMLClassFigure(Label name) {
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setBackgroundColor(CLASS_COLOR);
		setOpaque(true);

		add(name);
		add(attributeFigure);
		add(methodFigure);
	}

	/**
	 * Returns the "attributes" figure
	 */
	public CompartmentFigure getAttributesCompartment() {
		return attributeFigure;
	}

	/**
	 * Returns the "methods" figure
	 */
	public CompartmentFigure getMethodsCompartment() {
		return methodFigure;
	}

}