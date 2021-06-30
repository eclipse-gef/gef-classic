/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef3.internal.ui.palette.editparts;

import org.eclipse.draw2dl.ColorConstants;
import org.eclipse.draw2dl.Figure;
import org.eclipse.draw2dl.Graphics;
import org.eclipse.draw2dl.IFigure;
import org.eclipse.draw2dl.geometry.Dimension;
import org.eclipse.draw2dl.geometry.Insets;
import org.eclipse.draw2dl.geometry.Rectangle;

import org.eclipse.gef3.internal.ui.palette.PaletteColorUtil;
import org.eclipse.gef3.palette.PaletteSeparator;
import org.eclipse.gef3.ui.palette.editparts.PaletteEditPart;
import org.eclipse.gef3.editparts.AbstractGraphicalEditPart;

/**
 * EditPart for the PaletteSeparator
 * 
 * @author Pratik Shah
 */
public class SeparatorEditPart extends PaletteEditPart {

	/**
	 * Constructor
	 * 
	 * @param separator
	 *            The PaletteSeparator for which this EditPart is being created
	 */
	public SeparatorEditPart(PaletteSeparator separator) {
		super(separator);
	}

	/**
	 * @see AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		return new SeparatorFigure();
	}

	/**
	 * @see org.eclipse.gef3.ui.palette.editparts.PaletteEditPart#getToolTipText()
	 */
	protected String getToolTipText() {
		return null;
	}

	/**
	 * Figure for the separator
	 * 
	 * @author Pratik Shah
	 */
	static class SeparatorFigure extends Figure {
		/**
		 * @see IFigure#getPreferredSize(int, int)
		 */
		public Dimension getPreferredSize(int wHint, int hHint) {
			if (getBackgroundColor().equals(PaletteColorUtil.WIDGET_BACKGROUND))
				return new Dimension(wHint, 4);
			return new Dimension(wHint, 5);
		}

		private static final Insets CROP = new Insets(1, 3, 2, 4);

		/**
		 * 
		 * @see Figure#paintFigure(Graphics)
		 */
		protected void paintFigure(Graphics g) {
			Rectangle r = getBounds().getCropped(CROP);
			if (getBackgroundColor().equals(
					PaletteColorUtil.WIDGET_LIST_BACKGROUND)) {
				g.setForegroundColor(PaletteColorUtil.WIDGET_NORMAL_SHADOW);
				g.drawLine(r.getLeft(), r.getRight());
			} else {
				g.setForegroundColor(PaletteColorUtil.WIDGET_NORMAL_SHADOW);
				g.drawLine(r.getBottomLeft(), r.getTopLeft());
				g.drawLine(r.getTopLeft(), r.getTopRight());

				g.setForegroundColor(ColorConstants.buttonLightest);
				g.drawLine(r.getBottomLeft(), r.getBottomRight());
				g.drawLine(r.getBottomRight(), r.getTopRight());
			}
		}
	}

}
