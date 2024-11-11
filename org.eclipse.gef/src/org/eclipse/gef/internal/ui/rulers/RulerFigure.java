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
package org.eclipse.gef.internal.ui.rulers;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.geometry.Transposer;
import org.eclipse.draw2d.zoom.ZoomListener;

import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.rulers.RulerProvider;

/**
 * @author Pratik Shah
 */
public class RulerFigure extends Figure {

	/**
	 * These fields allow the client to customize the look of the ruler.
	 */
	public int smallMarkWidth = 1;
	public int mediumMarkWidth = 3;
	public int textMargin = 3;
	public int minPixelsBetweenMarks = 7;
	public int minPixelsBetweenMajorMarks = 47;

	protected Transposer transposer = new Transposer();
	protected ZoomManager zoomManager;
	private boolean drawFocus = false;

	/*
	 * This is an artificial border. When asked for the preferred size, the figure
	 * adds this width to its preferred width. The border is painted in the
	 * paintFigure(Graphics) method.
	 */
	private static final int BORDER_WIDTH = 3;

	private boolean horizontal;
	private int unit;
	private int interval;
	private int divisions;
	private double dpu = -1.0;

	private final ZoomListener zoomListener = newZoomValue -> handleZoomChanged();
	private final RulerProvider rulerProvider;

	public RulerFigure(boolean isHorizontal, RulerProvider rulerProvider) {
		this.rulerProvider = rulerProvider;
		setHorizontal(isHorizontal);
		setUnit(rulerProvider.getUnit());
		setBackgroundColor(ColorConstants.listBackground);
		setForegroundColor(ColorConstants.listForeground);
		setOpaque(true);
		setLayoutManager(new RulerLayout());
	}

	protected double getDPU() {
		if (dpu <= 0) {
			switch (getUnit()) {
			case RulerProvider.UNIT_PIXELS -> dpu = 1.0;
			case RulerProvider.UNIT_CUSTOM -> dpu = rulerProvider.getCustomRulerDPU();
			default -> {
				dpu = transposer.t(new Dimension(Display.getCurrent().getDPI())).height;
				if (getUnit() == RulerProvider.UNIT_CENTIMETERS) {
					dpu = dpu / 2.54;
				}
			}
			}

			if (zoomManager != null) {
				dpu = dpu * zoomManager.getZoom();
			}
		}
		return dpu;
	}

	public boolean getDrawFocus() {
		return drawFocus;
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		Dimension prefSize = new Dimension();
		if (isHorizontal()) {
			prefSize.height = (textMargin * 2) + BORDER_WIDTH + FigureUtilities.getFontMetrics(getFont()).getAscent();
		} else {
			prefSize.width = (textMargin * 2) + BORDER_WIDTH + FigureUtilities.getFontMetrics(getFont()).getAscent();
		}
		return prefSize;
	}

	public int getUnit() {
		return unit;
	}

	protected void handleZoomChanged() {
		dpu = -1.0;
		repaint();
		layout();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.draw2d.Figure#invalidate()
	 */
	@Override
	public void invalidate() {
		super.invalidate();
		dpu = -1.0;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	/*
	 * @TODO:Pratik re-comment this algorithm and the setInterval method
	 */

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	@Override
	protected void paintFigure(Graphics graphics) {
		double dotsPerUnit = getDPU();
		Rectangle clip = transposer.t(graphics.getClip(Rectangle.SINGLETON));
		Rectangle figClientArea = transposer.t(getClientArea());
		// Use the x and width of the client area, but the y and height of the
		// clip as the
		// bounds of the area which is to be repainted. This will increase
		// performance as the
		// entire ruler will not be repainted everytime.
		Rectangle clippedBounds = clip;
		clippedBounds.x = figClientArea.x;
		clippedBounds.width = figClientArea.width - BORDER_WIDTH;

		// Paint the background
		if (isOpaque()) {
			graphics.fillRectangle(transposer.t(clippedBounds));
		}

		/*
		 * A major mark is one that goes all the way from the left edge to the right
		 * edge of a ruler and for which a number is displayed. Determine the minimum
		 * number of pixels that are to be left between major marks. This will, in turn,
		 * help determine how many units are to be displayed per major mark. A major
		 * mark should have at least enough pixels to display the text and its padding.
		 * We take into the consideration the max of text's width and height so that for
		 * horizontal and vertical rulers that are of the same height, the number of
		 * units per major mark is the same.
		 */
		int unitsPerMajorMark = getUnitsPerMajorMark(dotsPerUnit);

		/*
		 * divsPerMajorMark indicates the number of divisions that a major mark should
		 * be divided into. for eg., a value of 2 would mean that a major mark would be
		 * shown as having two parts. that means that there would be a marker showing
		 * the beginning and end of the major marker and another right in the middle.
		 */
		int divsPerMajorMark = getDivsPerMajorMark(dotsPerUnit, unitsPerMajorMark);

		/*
		 * mediumMarkerDivNum is used to determine which mark (line drawn to indicate a
		 * point on the ruler) in a major mark will be of medium size. If its value is 1
		 * then every mark will be of medium size. If its value is 5, then every 5th
		 * mark will be of medium size (the rest being of small size).
		 */
		int mediumMarkerDivNum = getMediumMarkerDivNum(divsPerMajorMark);

		/*
		 * dotsPerDivision = number of pixels between each mark = number of pixels in a
		 * division
		 */
		double dotsPerDivision = dotsPerUnit * unitsPerMajorMark / divsPerMajorMark;
		/*
		 * startMark is the division/mark from which we are going to start painting. It
		 * should be the last major mark (one for which a number is displayed) that is
		 * before the top of the clip rectangle.
		 */
		int startMark = (int) (clippedBounds.y / (dotsPerUnit * unitsPerMajorMark)) * divsPerMajorMark;
		if (clippedBounds.y < 0) {
			// -2 / 10 = 0, not -1. so, if the top of the clip is negative, we need to move
			// the startMark back by a whole major mark.
			startMark -= divsPerMajorMark;
		}
		// endMark is the first non-visible mark (doesn't have to be a major mark) that
		// is beyond the end of the clip region
		int endMark = (int) ((clippedBounds.y + clippedBounds.height) / dotsPerDivision) + 1;
		int leading = FigureUtilities.getFontMetrics(getFont()).getLeading();
		Rectangle forbiddenZone = new Rectangle();
		for (int div = startMark; div <= endMark; div++) {
			// y is the vertical position of the mark
			int y = (int) (div * dotsPerDivision);
			if (div % divsPerMajorMark == 0) {
				String num = "" + (div / divsPerMajorMark) * unitsPerMajorMark; //$NON-NLS-1$
				if (isHorizontal()) {
					drawHorizontalMajorMark(graphics, clippedBounds, leading, forbiddenZone, y, num);
				} else {
					drawVerticalMajorMark(graphics, clippedBounds, forbiddenZone, y, num);
				}
			} else if ((div % divsPerMajorMark) % mediumMarkerDivNum == 0) {
				// this is a medium mark, so its length should be longer than the small marks
				drawMediumMark(graphics, clippedBounds, forbiddenZone, y);
			} else {
				// small mark
				drawSmallMark(graphics, clippedBounds, forbiddenZone, y);
			}
		}
		// paint the border
		clippedBounds.expand(BORDER_WIDTH, 0);
		graphics.setForegroundColor(ColorConstants.buttonDarker);
		graphics.drawLine(transposer.t(clippedBounds.getTopRight().translate(-1, -1)),
				transposer.t(clippedBounds.getBottomRight().translate(-1, -1)));
	}

	private void drawHorizontalMajorMark(Graphics graphics, Rectangle clippedBounds, int leading,
			Rectangle forbiddenZone, int y, String num) {
		Dimension numSize = FigureUtilities.getStringExtents(num, getFont());
		// If the width is even, we want to increase it by 1. This will ensure that when
		// marks are erased because they are too close to the number, they are erased
		// from both sides of that number.
		if (numSize.width % 2 == 0) {
			numSize.width++;
		}
		Point textLocation = new Point(y - (numSize.width / 2), clippedBounds.x + textMargin - leading);
		forbiddenZone.setLocation(textLocation);
		forbiddenZone.setSize(numSize);
		forbiddenZone.expand(1, 1);
		graphics.fillRectangle(forbiddenZone);
		// Uncomment the following line of code if you want to see a line at the exact
		// position of the major mark
		// graphics.drawLine(y, clippedBounds.x, y, clippedBounds.x +
		// clippedBounds.width);
		graphics.drawText(num, textLocation);
	}

	private void drawVerticalMajorMark(Graphics graphics, Rectangle clippedBounds, Rectangle forbiddenZone, int y,
			String num) {
		Image numImage = ImageUtilities.createRotatedImageOfString(num, getFont(), getForegroundColor(),
				getBackgroundColor());
		Point textLocation = new Point(clippedBounds.x + textMargin, y - (numImage.getBounds().height / 2));
		forbiddenZone.setLocation(textLocation);
		forbiddenZone.setSize(numImage.getBounds().width, numImage.getBounds().height);
		forbiddenZone.expand(1, 1 + (numImage.getBounds().height % 2 == 0 ? 1 : 0));
		graphics.fillRectangle(forbiddenZone);
		graphics.drawImage(numImage, textLocation);
		numImage.dispose();
	}

	private void drawMediumMark(Graphics graphics, Rectangle clippedBounds, Rectangle forbiddenZone, int y) {
		Point start = transposer.t(new Point((clippedBounds.getRight().x - mediumMarkWidth) / 2, y));
		Point end = transposer.t(new Point(((clippedBounds.getRight().x - mediumMarkWidth) / 2) + mediumMarkWidth, y));
		if (!forbiddenZone.contains(start)) {
			graphics.drawLine(start, end);
		}
	}

	private void drawSmallMark(Graphics graphics, Rectangle clippedBounds, Rectangle forbiddenZone, int y) {
		Point start = transposer.t(new Point((clippedBounds.getRight().x - smallMarkWidth) / 2, y));
		Point end = transposer.t(new Point(((clippedBounds.getRight().x - smallMarkWidth) / 2) + smallMarkWidth, y));
		if (!forbiddenZone.contains(start)) {
			graphics.drawLine(start, end);
		}
	}

	private int getUnitsPerMajorMark(double dotsPerUnit) {
		int unitsPerMajorMark = (int) (minPixelsBetweenMajorMarks / dotsPerUnit);
		if (minPixelsBetweenMajorMarks % dotsPerUnit != 0.0) {
			unitsPerMajorMark++;
		}
		if (interval > 0) {
			// If the client specified how many units are to be displayed per major mark,
			// use that. If, however, showing that many units wouldn't leave enough room for
			// the text, than take its smallest multiple that would leave enough room.
			int intervalMultiple = interval;
			while (intervalMultiple < unitsPerMajorMark) {
				intervalMultiple += interval;
			}
			unitsPerMajorMark = intervalMultiple;
		} else if (unitsPerMajorMark != 1 && unitsPerMajorMark % 2 != 0) {
			// if the number of units per major mark is calculated dynamically, ensure that
			// it is an even number.
			unitsPerMajorMark++;
		}
		return unitsPerMajorMark;
	}

	private int getDivsPerMajorMark(double dotsPerUnit, int unitsPerMajorMark) {
		int divsPerMajorMark;
		if (divisions > 0 && dotsPerUnit * unitsPerMajorMark / divisions >= minPixelsBetweenMarks) {
			// If the client has specified the number of divisions per major mark, use that
			// unless it would cause the minimum space between marks to be less than
			// minPixelsBetweenMarks
			return divisions;
		}
		// If the client hasn't specified the number of divisions per major mark or the
		// one that the client has specified is invalid, then calculate it dynamically.
		// This algorithm will try to display 10 divisions per CM, and 16 per INCH.
		// However, if that puts the marks too close together (i.e., the space between
		// them is less than minPixelsBetweenMarks), then it keeps decreasing the number
		// of divisions by a factor of 2 until there is enough space between them.
		divsPerMajorMark = 2;
		if (getUnit() == RulerProvider.UNIT_CENTIMETERS) {
			divsPerMajorMark = 10;
		} else if (getUnit() == RulerProvider.UNIT_INCHES) {
			divsPerMajorMark = 8;
		}
		while (dotsPerUnit * unitsPerMajorMark / divsPerMajorMark < minPixelsBetweenMarks) {
			divsPerMajorMark /= 2;
			if (divsPerMajorMark == 0) {
				break;
			}
		}
		// This should never happen unless the client has specified a
		// minPixelsBetweenMarks that is larger than minPixelsBetweenMajorMarks (which
		// is calculated using the text's size -- size of the largest number to be
		// displayed).
		if (divsPerMajorMark == 0) {
			divsPerMajorMark = 1;
		}
		return divsPerMajorMark;
	}

	private static int getMediumMarkerDivNum(int divsPerMajorMark) {
		return switch (divsPerMajorMark) {
		case 20, 10, 5 -> 5;
		case 16, 8 -> 4;
		case 4 -> 2;
		case 2 -> 1;
		default -> 1;
		};
	}

	public void setDrawFocus(boolean drawFocus) {
		if (this.drawFocus != drawFocus) {
			this.drawFocus = drawFocus;
			repaint();
		}
	}

	public void setHorizontal(boolean isHorizontal) {
		horizontal = isHorizontal;
		transposer.setEnabled(isHorizontal);
	}

	/**
	 * Allows the client to set the number of units to be displayed per major mark,
	 * and the number of divisions to be shown per major mark.
	 *
	 * A number on the ruler is considered to be a major mark.
	 *
	 * @param unitsPerMajorMark     if less than 1, it will be ignored; if there is
	 *                              not enough space to display that many units per
	 *                              major mark, its smallest multiple that leaves
	 *                              enough room will be used.
	 * @param divisionsPerMajorMark if less than 1, it will be ignored; if
	 *                              displaying that many divisions does not leave
	 *                              enough room between marks, it will be ignored.
	 *
	 */
	public void setInterval(int unitsPerMajorMark, int divisionsPerMajorMark) {
		interval = unitsPerMajorMark;
		divisions = divisionsPerMajorMark;
		repaint();
	}

	public void setUnit(int newUnit) {
		if (unit != newUnit) {
			unit = newUnit;
			dpu = -1.0;
			repaint();
		}
	}

	public void setZoomManager(ZoomManager manager) {
		if (zoomManager != manager) {
			if (zoomManager != null) {
				zoomManager.removeZoomListener(zoomListener);
			}
			zoomManager = manager;
			if (zoomManager != null) {
				zoomManager.addZoomListener(zoomListener);
			}
		}
	}

}
