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

import org.eclipse.swt.SWT;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.AbstractFlowBorder;
import org.eclipse.draw2d.text.FlowFigure;

public class TestBorder extends AbstractFlowBorder {

	private Insets insets;

	@Override
	public Insets getInsets(IFigure figure) {
		if (insets == null) {
			insets = new Insets();
			Dimension size = FigureUtilities.getStringExtents("begin", figure.getFont()); //$NON-NLS-1$
			insets.left = size.width + 3 + (size.height / 2);
			size = FigureUtilities.getStringExtents("end", figure.getFont()); //$NON-NLS-1$
			insets.right = size.width + 3 + (size.height / 2);
		}
		return insets;
	}

	@Override
	public void paint(FlowFigure figure, Graphics g, Rectangle where, int sides) {
		where.resize(-1, -1);
		if ((sides & SWT.LEAD) != 0) {
			PointList points = new PointList(5);
			points.addPoint(where.getTopLeft());
			points.addPoint(where.getTopRight().translate(-where.height / 2, 0));
			points.addPoint(where.getRight());
			points.addPoint(where.getBottomRight().translate(-where.height / 2, 0));
			points.addPoint(where.getBottomLeft());
			g.setBackgroundColor(ColorConstants.tooltipBackground);
			g.fillPolygon(points);
			g.drawPolygon(points);
			g.drawString("begin", where.x + 1, where.y); //$NON-NLS-1$
		}
		if ((sides & SWT.TRAIL) != 0) {
			PointList points = new PointList(5);
			points.addPoint(where.getLeft());
			points.addPoint(where.getTopLeft().translate(where.height / 2, 0));
			points.addPoint(where.getTopRight());
			points.addPoint(where.getBottomRight());
			points.addPoint(where.getBottomLeft().translate(where.height / 2, 0));
			g.setBackgroundColor(ColorConstants.tooltipBackground);
			g.fillPolygon(points);
			g.drawPolygon(points);
			g.drawString("end", where.x + (where.height / 2), where.y); //$NON-NLS-1$
		}
	}

}
