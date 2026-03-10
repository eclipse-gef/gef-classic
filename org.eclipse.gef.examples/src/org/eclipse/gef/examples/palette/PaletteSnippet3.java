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

package org.eclipse.gef.examples.palette;

import java.util.function.Function;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.ui.part.ViewPart;

import org.eclipse.draw2d.AbstractBackground;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ButtonModel;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteColorProvider;
import org.eclipse.gef.ui.palette.PaletteEditPartFactory;
import org.eclipse.gef.ui.palette.PaletteViewer;

import org.eclipse.gef.examples.Messages;

/**
 * This snippet shows how to change the theme for the GEF palette by injecting a
 * custom color provider and edit-part factory.
 */
public class PaletteSnippet3 extends ViewPart {
	private final PaletteViewer paletteViewer = new PaletteViewer();

	@Override
	public void createPartControl(Composite parent) {
		paletteViewer.createControl(parent);
		paletteViewer.setColorProvider(new ShapesColorProvider());
		paletteViewer.setEditPartFactory(new ShapesPaletteEditPartFactory());
		paletteViewer.setPaletteRoot(createPalette());
	}

	private static PaletteRoot createPalette() {
		PaletteDrawer shapes = new PaletteDrawer(Messages.PaletteSnippet3_Shapes);
		shapes.add(new ToolEntry(Messages.PaletteSnippet3_Ellipse, null, null, null) {
		});
		PaletteDrawer system = new PaletteDrawer(Messages.PaletteSnippet3_System);
		system.add(new SelectionToolEntry());
		PaletteRoot paletteRoot = new PaletteRoot();
		paletteRoot.add(system);
		paletteRoot.add(shapes);
		return paletteRoot;
	}

	@Override
	public void setFocus() {
		paletteViewer.getControl().setFocus();
	}

	public PaletteViewer getViewer() {
		return paletteViewer;
	}

	/**
	 * Defines arbitrary colors that distinguish themselves from the default
	 * palette.
	 */
	private static class ShapesColorProvider extends PaletteColorProvider {
		public static final Color COLOR_PALETTE_BACKGROUND = ColorConstants.button;
		public static final Color COLOR_ENTRY_SELECTED = getShiftedColor(COLOR_PALETTE_BACKGROUND, 24);
		public static final Color COLOR_DRAWER_GRAD_BEGIN = getShiftedColor(COLOR_PALETTE_BACKGROUND, -8);
		public static final Color COLOR_DRAWER_GRAD_END = getShiftedColor(COLOR_PALETTE_BACKGROUND, 16);

		@Override
		public Color getListSelectedBackgroundColor() {
			return COLOR_ENTRY_SELECTED;
		}

		@Override
		public Color getListHoverBackgroundColor() {
			return COLOR_PALETTE_BACKGROUND;
		}

		@Override
		public Color getListBackground() {
			return COLOR_PALETTE_BACKGROUND;
		}

		/**
		 * @return new {@link Color} based on given {@link Color} and shifted on given
		 *         value to make it darker or lighter.
		 */
		private static Color getShiftedColor(Color color, int delta) {
			int r = Math.max(0, Math.min(color.getRed() + delta, 255));
			int g = Math.max(0, Math.min(color.getGreen() + delta, 255));
			int b = Math.max(0, Math.min(color.getBlue() + delta, 255));
			return new Color(color.getDevice(), r, g, b);
		}
	}

	/**
	 * This factory is used to inject our own {@link Border}s into the created
	 * palette figure, which provide a 3D look.
	 */
	private static class ShapesPaletteEditPartFactory extends PaletteEditPartFactory {
		@Override
		public EditPart createEditPart(EditPart parentEditPart, Object model) {
			EditPart editPart = super.createEditPart(parentEditPart, model);
			editPart.addEditPartListener(new EditPartListener.Stub() {
				@Override
				public void childAdded(EditPart child, int index) {
					if (child.getModel() instanceof PaletteDrawer) {
						updateFigure(child, DrawerBackground::new);
					} else if (child.getModel() instanceof ToolEntry) {
						updateFigure(child, ToolEntryBackground::new);
					}
				}
			});
			return editPart;
		}

		private static void updateFigure(EditPart editPart, Function<ButtonModel, AbstractBackground> factory) {
			Clickable toggle = editPart.getAdapter(Clickable.class);
			if (toggle != null) {
				ButtonModel toggleModel = toggle.getModel();
				toggle.setBorder(factory.apply(toggleModel));
			}
		}

		private static class ToolEntryBackground extends AbstractBackground {
			private final ButtonModel buttonModel;

			public ToolEntryBackground(ButtonModel buttonModel) {
				this.buttonModel = buttonModel;
			}

			@Override
			public void paintBackground(IFigure figure, Graphics g, Insets insets) {
				if (buttonModel.isMouseOver() || buttonModel.isSelected()) {
					Rectangle r = Rectangle.SINGLETON;
					r.setBounds(figure.getBounds()).shrink(insets);
					if (buttonModel.isSelected()) {
						g.setBackgroundColor(ShapesColorProvider.COLOR_ENTRY_SELECTED);
						g.fillRectangle(r);
					}
					drawRectangle3D(g, r, !buttonModel.isSelected());
				}
			}
		}

		private static class DrawerBackground extends AbstractBackground {
			private final ButtonModel buttonModel;

			public DrawerBackground(ButtonModel buttonModel) {
				this.buttonModel = buttonModel;
			}

			@Override
			public void paintBackground(IFigure figure, Graphics g, Insets insets) {
				Rectangle r = Rectangle.SINGLETON;
				r.setBounds(figure.getBounds()).shrink(insets);
				if (buttonModel.isMouseOver()) {
					g.setForegroundColor(ShapesColorProvider.COLOR_DRAWER_GRAD_END);
					g.setBackgroundColor(ShapesColorProvider.COLOR_DRAWER_GRAD_BEGIN);
				} else {
					g.setForegroundColor(ShapesColorProvider.COLOR_DRAWER_GRAD_BEGIN);
					g.setBackgroundColor(ShapesColorProvider.COLOR_DRAWER_GRAD_END);
				}
				g.fillGradient(r, true);
				drawRectangle3D(g, r, !buttonModel.isPressed());
			}
		}

		/**
		 * Draws 3D highlight rectangle.
		 */
		private static void drawRectangle3D(Graphics g, Rectangle r, boolean up) {
			int x = r.x;
			int y = r.y;
			int right = r.right() - 1;
			int bottom = r.bottom() - 1;
			//
			if (up) {
				g.setForegroundColor(ColorConstants.buttonLightest);
			} else {
				g.setForegroundColor(ColorConstants.buttonDarker);
			}
			g.drawLine(x, y, right, y);
			g.drawLine(x, y, x, bottom);
			//
			if (up) {
				g.setForegroundColor(ColorConstants.buttonDarker);
			} else {
				g.setForegroundColor(ColorConstants.buttonLightest);
			}
			g.drawLine(right, y, right, bottom);
			g.drawLine(x, bottom, right, bottom);
		}
	}
}
