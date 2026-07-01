/*******************************************************************************
 * Copyright (c) 2008, 2024 IBM Corporation and others.
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

package org.eclipse.gef.internal.ui.palette.editparts;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ButtonModel;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Toggle;

import org.eclipse.gef.internal.InternalImages;
import org.eclipse.gef.ui.palette.PaletteColorProvider;
import org.eclipse.gef.ui.palette.PaletteMessages;

/**
 * This is the figure for the pinned and unpinned toggle.
 *
 * @author crevells
 * @since 3.4
 */
public class PinFigure extends Toggle {

	private static final Border TOOLTIP_BORDER = new MarginBorder(0, 2, 1, 0);
	private final PaletteColorProvider colorProvider;

	public PinFigure(PaletteColorProvider colorProvider) {
		super(new ImageFigure(InternalImages.get(InternalImages.IMG_UNPINNED)));
		this.colorProvider = colorProvider;
		setRolloverEnabled(true);
		setRequestFocusEnabled(false);
		Label tooltip = new Label(PaletteMessages.TOOLTIP_PIN_FIGURE);
		tooltip.setBorder(TOOLTIP_BORDER);
		setToolTip(tooltip);
		setOpaque(false);

		addChangeListener(e -> {
			if (e.getPropertyName().equals(ButtonModel.SELECTED_PROPERTY)) {
				if (isSelected()) {
					getImageFigure().setImage(InternalImages.get(InternalImages.IMG_PINNED));
					getToolTip().setText(PaletteMessages.TOOLTIP_UNPIN_FIGURE);
				} else {
					((ImageFigure) (getChildren().get(0))).setImage(InternalImages.get(InternalImages.IMG_UNPINNED));
					getToolTip().setText(PaletteMessages.TOOLTIP_PIN_FIGURE);
				}
			}
		});
	}

	@Override
	public Label getToolTip() {
		return (Label) super.getToolTip();
	}

	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);

		ButtonModel model = getModel();
		if (isRolloverEnabled() && model.isMouseOver()) {
			graphics.setBackgroundColor(colorProvider.getHotspotColor());
			graphics.fillRoundRectangle(getClientArea().getCopy().shrink(1, 1), 3, 3);
		}
	}

	private ImageFigure getImageFigure() {
		return (ImageFigure) (getChildren().get(0));
	}
}
