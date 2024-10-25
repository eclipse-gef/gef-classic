/*******************************************************************************
 * Copyright (c) 2000, 2024 IBM Corporation and others.
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

import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;

import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RangeModel;
import org.eclipse.draw2d.geometry.Insets;

import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.ExposeHelper;
import org.eclipse.gef.MouseWheelHelper;
import org.eclipse.gef.editparts.ViewportExposeHelper;
import org.eclipse.gef.editparts.ViewportMouseWheelHelper;
import org.eclipse.gef.internal.InternalImages;
import org.eclipse.gef.internal.ui.palette.PaletteColorUtil;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteTemplateEntry;
import org.eclipse.gef.ui.palette.PaletteViewerPreferences;
import org.eclipse.gef.ui.palette.editparts.IPinnableEditPart;
import org.eclipse.gef.ui.palette.editparts.PaletteAnimator;
import org.eclipse.gef.ui.palette.editparts.PaletteEditPart;

/**
 * EditPart for a PaletteDrawer
 *
 * @author Pratik Shah
 */
public class DrawerEditPart extends PaletteEditPart implements IPinnableEditPart {

	private static final String PROPERTY_EXPANSION_STATE = "expansion"; //$NON-NLS-1$
	private static final String PROPERTY_PINNED_STATE = "pinned"; //$NON-NLS-1$

	/**
	 * Constructor
	 *
	 * @param drawer The PaletteDrawer that this EditPart is representing
	 */
	public DrawerEditPart(PaletteDrawer drawer) {
		super(drawer);
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	public IFigure createFigure() {
		DrawerFigure fig = new DrawerFigure(getViewer().getControl(), getColorPalette()) {
			@Override
			IFigure buildTooltip() {
				return createToolTip();
			}
		};
		fig.setExpanded(getModel().isInitiallyOpen());
		fig.setPinned(getModel().isInitiallyPinned());

		fig.getCollapseToggle().addFocusListener(new FocusListener.Stub() {
			@Override
			public void focusGained(FocusEvent fe) {
				getViewer().select(DrawerEditPart.this);
			}
		});

		fig.getScrollpane().getContents().addLayoutListener(getPaletteAnimator());

		return fig;
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(Class)
	 */
	@Override
	public <T> T getAdapter(final Class<T> key) {
		if (key == ExposeHelper.class) {
			ViewportExposeHelper helper = new ViewportExposeHelper(this);
			helper.setMinimumFrameCount(6);
			helper.setMargin(new Insets(PaletteScrollBar.BUTTON_HEIGHT, 0, PaletteScrollBar.BUTTON_HEIGHT, 0));
			return key.cast(helper);
		}
		if (key == MouseWheelHelper.class) {
			return key.cast(new ViewportMouseWheelHelper(this));
		}
		return super.getAdapter(key);
	}

	private PaletteAnimator getPaletteAnimator() {
		return getViewer().getPaletteAnimator();
	}

	@Override
	public PaletteDrawer getModel() {
		return (PaletteDrawer) super.getModel();
	}

	@Override
	public DrawerFigure getFigure() {
		return (DrawerFigure) super.getFigure();
	}

	/**
	 * @see org.eclipse.gef.GraphicalEditPart#getContentPane()
	 */
	@Override
	public IFigure getContentPane() {
		return getFigure().getContentPane();
	}

	@Override
	public boolean isExpanded() {
		return getFigure().isExpanded();
	}

	@Override
	public boolean isPinnedOpen() {
		return getFigure().isPinnedOpen();
	}

	/**
	 * @see org.eclipse.gef.ui.palette.editparts.PaletteEditPart#nameNeededInToolTip()
	 */
	@Override
	protected boolean nameNeededInToolTip() {
		return false;
	}

	/**
	 * @return <code>true</code> if the DrawerFigure can be pinned open. This is
	 *         only true when the drawer is expanded and the auto-collapse strategy
	 *         is <code>PaletteViewerPreferences.COLLAPSE_AS_NEEDED</code>.
	 */
	@Override
	public boolean canBePinned() {
		return getFigure().isPinShowing();
	}

	/**
	 * @see org.eclipse.gef.ui.palette.editparts.PaletteEditPart#createAccessible()
	 */
	@Override
	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {
			@Override
			public void getDescription(AccessibleEvent e) {
				e.result = getModel().getDescription();
			}

			@Override
			public void getName(AccessibleEvent e) {
				e.result = getModel().getLabel();
			}

			@Override
			public void getRole(AccessibleControlEvent e) {
				e.detail = ACC.ROLE_TREE;
			}

			@Override
			public void getState(AccessibleControlEvent e) {
				super.getState(e);
				e.detail |= isExpanded() ? ACC.STATE_EXPANDED : ACC.STATE_COLLAPSED;
			}
		};
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		getFigure().setToolTip(createToolTip());

		ImageDescriptor img = getModel().getSmallIcon();
		if (img == null && getModel().showDefaultIcon()) {
			img = InternalImages.DESC_FOLDER_OPEN;
		}
		setImageDescriptor(img);

		getFigure().setTitle(getModel().getLabel());
		getFigure().setLayoutMode(getLayoutSetting());

		boolean showPin = getPreferenceSource().getAutoCollapseSetting() == PaletteViewerPreferences.COLLAPSE_AS_NEEDED;
		getFigure().showPin(showPin);

		Color background = getModel().getDrawerType().equals(PaletteTemplateEntry.PALETTE_TYPE_TEMPLATE)
				? PaletteColorUtil.WIDGET_LIST_BACKGROUND
				: null;
		getFigure().getScrollpane().setBackgroundColor(background);
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#register()
	 */
	@Override
	protected void register() {
		super.register();
		getPaletteAnimator().addDrawer(this);
		getFigure().addLayoutListener(getPaletteAnimator());
	}

	/**
	 * @see org.eclipse.gef.ui.palette.editparts.PaletteEditPart#restoreState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void restoreState(IMemento memento) {
		setExpanded(Boolean.parseBoolean(memento.getString(PROPERTY_EXPANSION_STATE)));
		setPinnedOpen(Boolean.parseBoolean(memento.getString(PROPERTY_PINNED_STATE)));
		RangeModel rModel = getFigure().getScrollpane().getViewport().getVerticalRangeModel();
		rModel.setMinimum(memento.getInteger(RangeModel.PROPERTY_MINIMUM).intValue());
		rModel.setMaximum(memento.getInteger(RangeModel.PROPERTY_MAXIMUM).intValue());
		rModel.setExtent(memento.getInteger(RangeModel.PROPERTY_EXTENT).intValue());
		rModel.setValue(memento.getInteger(RangeModel.PROPERTY_VALUE).intValue());
		super.restoreState(memento);
	}

	/**
	 * @see org.eclipse.gef.ui.palette.editparts.PaletteEditPart#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		memento.putString(PROPERTY_EXPANSION_STATE, Boolean.toString(isExpanded()));
		memento.putString(PROPERTY_PINNED_STATE, Boolean.toString(isPinnedOpen()));
		RangeModel rModel = getFigure().getScrollpane().getViewport().getVerticalRangeModel();
		memento.putInteger(RangeModel.PROPERTY_MINIMUM, rModel.getMinimum());
		memento.putInteger(RangeModel.PROPERTY_MAXIMUM, rModel.getMaximum());
		memento.putInteger(RangeModel.PROPERTY_EXTENT, rModel.getExtent());
		memento.putInteger(RangeModel.PROPERTY_VALUE, rModel.getValue());
		super.saveState(memento);
	}

	/**
	 * Sets the expansion state of the DrawerFigure
	 *
	 * @param expanded <code>true</code> if the drawer is expanded; false otherwise.
	 */
	public void setExpanded(boolean expanded) {
		getFigure().setExpanded(expanded);
	}

	/**
	 * @see org.eclipse.gef.ui.palette.editparts.PaletteEditPart#setImageInFigure(Image)
	 */
	@Override
	protected void setImageInFigure(Image image) {
		getFigure().setTitleIcon(image);
	}

	@Override
	public void setPinnedOpen(boolean pinned) {
		getFigure().setPinned(pinned);
	}

	/**
	 * @see org.eclipse.gef.EditPart#setSelected(int)
	 */
	@Override
	public void setSelected(int value) {
		super.setSelected(value);
		getFigure().getCollapseToggle().requestFocus();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#unregister()
	 */
	@Override
	protected void unregister() {
		getPaletteAnimator().removeDrawer(this);
		super.unregister();
	}

}
