/*******************************************************************************
 * Copyright (c) 2004, 2023 Elias Volanakis and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Elias Volanakis - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.examples.shapes.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import org.eclipse.gef.examples.shapes.model.Connection;
import org.eclipse.gef.examples.shapes.model.Shape;
import org.eclipse.gef.examples.shapes.model.ShapesDiagram;

/**
 * Factory that maps model elements to edit parts.
 *
 * @author Elias Volanakis
 */
public class ShapesEditPartFactory implements EditPartFactory {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart,
	 * java.lang.Object)
	 */
	@Override
	public EditPart createEditPart(EditPart context, Object modelElement) {
		// get EditPart for model element
		EditPart part = getPartForElement(modelElement);
		// store model element in EditPart
		part.setModel(modelElement);
		return part;
	}

	/**
	 * Maps an object to an EditPart.
	 *
	 * @throws RuntimeException if no match was found (programming error)
	 */
	private static EditPart getPartForElement(Object modelElement) {
		if (modelElement instanceof ShapesDiagram) {
			return new DiagramEditPart();
		}
		if (modelElement instanceof Shape) {
			return new ShapeEditPart();
		}
		if (modelElement instanceof Connection) {
			return new ConnectionEditPart();
		}
		throw new RuntimeException("Can't create part for model element: "
				+ ((modelElement != null) ? modelElement.getClass().getName() : "null"));
	}

}