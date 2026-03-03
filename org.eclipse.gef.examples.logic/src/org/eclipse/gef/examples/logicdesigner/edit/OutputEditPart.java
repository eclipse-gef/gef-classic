/*******************************************************************************
 * Copyright (c) 2000, 2022 IBM Corporation and others.
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
package org.eclipse.gef.examples.logicdesigner.edit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.accessibility.AccessibleEvent;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import org.eclipse.gef.AccessibleAnchorProvider;
import org.eclipse.gef.AccessibleEditPart;

import org.eclipse.gef.examples.logicdesigner.figures.GroundFigure;
import org.eclipse.gef.examples.logicdesigner.figures.LiveOutputFigure;
import org.eclipse.gef.examples.logicdesigner.figures.OutputFigure;
import org.eclipse.gef.examples.logicdesigner.model.GroundOutput;
import org.eclipse.gef.examples.logicdesigner.model.LiveOutput;
import org.eclipse.gef.examples.logicdesigner.model.SimpleOutput;

/**
 * EditPart for Output types in Logic Example
 */
public class OutputEditPart extends LogicEditPart {

	@Override
	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {
			@Override
			public void getName(AccessibleEvent e) {
				e.result = getSimpleOutput().toString();
			}
		};
	}

	/**
	 * Returns a newly created Figure.
	 */
	@Override
	protected IFigure createFigure() {
		OutputFigure figure;
		if (getModel() == null) {
			return null;
		}
		if (getModel() instanceof LiveOutput) {
			figure = new LiveOutputFigure();
		} else if (getModel() instanceof GroundOutput) {
			figure = new GroundFigure();
		} else {
			figure = new OutputFigure();
		}
		return figure;
	}

	@Override
	public <T> T getAdapter(final Class<T> key) {
		if (key == AccessibleAnchorProvider.class) {
			return key.cast(new DefaultAccessibleAnchorProvider() {
				@Override
				public List<Point> getSourceAnchorLocations() {
					List<Point> list = new ArrayList<>();
					List<ConnectionAnchor> sourceAnchors = getNodeFigure().getSourceConnectionAnchors();
					sourceAnchors.forEach(anchor -> list.add(anchor.getReferencePoint().getTranslated(0, -3)));
					return list;
				}

			});
		}
		return super.getAdapter(key);
	}

	/**
	 * Returns the Figure for this as an OutputFigure.
	 *
	 * @return Figure of this as a OutputFigure.
	 */
	protected OutputFigure getOutputFigure() {
		return (OutputFigure) getFigure();
	}

	/**
	 * Returns the model of this as a SimpleOutput.
	 *
	 * @return Model of this as a SimpleOutput.
	 */
	protected SimpleOutput getSimpleOutput() {
		return (SimpleOutput) getModel();
	}

}
