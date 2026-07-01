/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.examples.e4;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;

import jakarta.annotation.PostConstruct;

/**
 * Creates an Eclipse E4 part with a graphical editor.
 */
public class GraphicalViewerPart {
	private GraphicalViewer viewer;

	@PostConstruct
	/* package */ void postConstruct(Composite parent) {
		viewer = new ScrollingGraphicalViewer();
		viewer.createControl(parent);
		viewer.setEditPartFactory(new TestEditPartFactory());
		viewer.setContents(new TestModel[] { //
				new TestModel(ColorConstants.green, new Rectangle(100, 100, 50, 50)), //
				new TestModel(ColorConstants.red, new Rectangle(125, 125, 50, 50)), //
				new TestModel(ColorConstants.blue, new Rectangle(200, 225, 50, 50)), //
		});
	}

	private static final class TestEditPartFactory implements EditPartFactory {
		@Override
		public EditPart createEditPart(EditPart context, Object object) {
			if (object instanceof TestModel[] models) {
				return new TestEditPartRoot(models);
			}
			if (object instanceof TestModel model) {
				return new TestEditPart(model);
			}
			return null;
		}
	}

	private static final class TestEditPartRoot extends AbstractGraphicalEditPart {
		private TestEditPartRoot(TestModel[] models) {
			setModel(models);
		}

		@Override
		public TestModel[] getModel() {
			return (TestModel[]) super.getModel();
		}

		@Override
		protected IFigure createFigure() {
			return new Figure();
		}

		@Override
		protected List<? extends Object> getModelChildren() {
			return Arrays.asList(getModel());
		}

		@Override
		protected void createEditPolicies() {
		}

	}

	private static final class TestEditPart extends AbstractGraphicalEditPart {
		public TestEditPart(TestModel model) {
			setModel(model);
		}

		@Override
		public TestModel getModel() {
			return (TestModel) super.getModel();
		}

		@Override
		protected IFigure createFigure() {
			IFigure figure = new RectangleFigure();
			figure.setBackgroundColor(getModel().color());
			figure.setBounds(getModel().bounds());
			return figure;
		}

		@Override
		protected void createEditPolicies() {
		}
	}

	private static final record TestModel(Color color, Rectangle bounds) {
		// methods generated automatically by the JDK
	}
}
