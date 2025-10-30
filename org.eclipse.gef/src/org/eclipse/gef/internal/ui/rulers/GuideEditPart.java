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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.graphics.Cursor;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.zoom.ZoomListener;

import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.AccessibleHandleProvider;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.gef.internal.InternalGEFPlugin;
import org.eclipse.gef.rulers.RulerChangeListener;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.tools.DragEditPartsTracker;

/**
 * @author Pratik Shah
 * @since 3.0
 */
public class GuideEditPart extends AbstractGraphicalEditPart {

	public static final int MIN_DISTANCE_BW_GUIDES = 5;
	public static final int DELETE_THRESHOLD = 20;

	private AccessibleEditPart accPart;
	private GuideLineFigure guideLineFig;
	private Cursor cursor = null;
	private final ZoomListener zoomListener = zoom -> handleZoomChanged();
	private double monitorScale = 1.0;

	private final RulerChangeListener listener = new RulerChangeListener.Stub() {
		@Override
		public void notifyGuideMoved(Object guide) {
			if (getModel() == guide) {
				handleGuideMoved();
			}
		}

		@Override
		public void notifyPartAttachmentChanged(Object part, Object guide) {
			if (getModel() == guide) {
				handlePartAttachmentChanged(part);
			}
		}
	};

	public GuideEditPart(Object model) {
		setModel(model);
		this.addEditPartListener(InternalGEFPlugin.createAutoscaleEditPartListener(newScale -> {
			this.monitorScale = newScale;
			updateLocationOfFigures(getZoomedPosition());
		}));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		getRulerProvider().addRulerChangeListener(listener);
		if (getZoomManager() != null) {
			getZoomManager().addZoomListener(zoomListener);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new GuideSelectionPolicy());
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new DragGuidePolicy());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		guideLineFig = new GuideLineFigure();
		getGuideLayer().add(getGuideLineFigure());
		getGuideLayer().setConstraint(getGuideLineFigure(), Boolean.valueOf(isHorizontal()));
		return new GuideFigure(isHorizontal());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		if (getZoomManager() != null) {
			getZoomManager().removeZoomListener(zoomListener);
		}
		getRulerProvider().removeRulerChangeListener(listener);
		if (getGuideLineFigure().getParent() != null) {
			getGuideLineFigure().getParent().remove(getGuideLineFigure());
		}
		super.deactivate();
	}

	@Override
	protected AccessibleEditPart getAccessibleEditPart() {
		if (accPart == null) {
			accPart = new AccessibleGraphicalEditPart() {
				@Override
				public void getDescription(AccessibleEvent e) {
					if (getRulerProvider() != null) {
						getRulerProvider().getAccGuideDescription(e, getModel());
					}
				}

				@Override
				public void getName(AccessibleEvent e) {
					if (getRulerProvider() != null) {
						getRulerProvider().getAccGuideName(e, getModel());
					}
				}

				@Override
				public void getValue(AccessibleControlEvent e) {
					if (getRulerProvider() != null) {
						getRulerProvider().getAccGuideValue(e, getModel());
					}
				}
			};
		}
		return accPart;
	}

	@Override
	public <T> T getAdapter(final Class<T> key) {
		if (key == AccessibleHandleProvider.class) {
			return key.cast((AccessibleHandleProvider) () -> {
				List<Point> result = new ArrayList<>();
				Point pt = getFigure().getBounds().getCenter();
				getFigure().translateToAbsolute(pt);
				result.add(pt);
				return result;
			});
		}
		return super.getAdapter(key);
	}

	public Cursor getCurrentCursor() {
		if (cursor == null) {
			return getFigure().getCursor();
		}
		return cursor;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getDragTracker(org
	 * .eclipse.gef.Request)
	 */
	@Override
	public DragTracker getDragTracker(Request request) {
		return new DragEditPartsTracker(this) {
			@Override
			protected Cursor calculateCursor() {
				if (isInState(STATE_INVALID)) {
					return Cursors.NO;
				}
				return getCurrentCursor();
			}

			@Override
			protected boolean isMove() {
				return true;
			}
		};
	}

	public IFigure getGuideLayer() {
		return getParent().getGuideLayer();
	}

	public IFigure getGuideLineFigure() {
		return guideLineFig;
	}

	@Override
	public RulerEditPart getParent() {
		return (RulerEditPart) super.getParent();
	}

	public RulerProvider getRulerProvider() {
		return getParent().getRulerProvider();
	}

	public int getZoomedPosition() {
		double position = getRulerProvider().getGuidePosition(getModel());
		if (getZoomManager() != null) {
			position = Math.round(position * getZoomManager().getZoom());
		}
		return (int) position;
	}

	public ZoomManager getZoomManager() {
		return getParent().getZoomManager();
	}

	protected void handleGuideMoved() {
		refreshVisuals();
	}

	protected void handlePartAttachmentChanged(Object part) {
		// nothing to do here
	}

	protected void handleZoomChanged() {
		refreshVisuals();
	}

	public boolean isHorizontal() {
		return !getParent().isHorizontal();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		updateLocationOfFigures(getZoomedPosition());
	}

	@Override
	public void removeNotify() {
		GraphicalEditPart nextSelection = null;
		if (getParent().isActive()) {
			// This guide is being deleted (but the ruler is still active). If this guide is
			// selected, determine which part is to be selected next.
			int thisPos = getRulerProvider().getGuidePosition(getModel());
			if (getSelected() != SELECTED_NONE || hasFocus()) {
				int minDistance = -1;
				for (GuideEditPart guide : getParent().getChildren()) {
					if (guide == this) {
						continue;
					}
					int posDiff = Math.abs(thisPos - getRulerProvider().getGuidePosition(guide.getModel()));
					if (minDistance == -1 || posDiff < minDistance) {
						minDistance = posDiff;
						nextSelection = guide;
					}
				}
				if (nextSelection == null) {
					nextSelection = getParent();
				}
			}
		}
		super.removeNotify();
		if (nextSelection != null) {
			getViewer().select(nextSelection);
		}
	}

	public void setCurrentCursor(Cursor c) {
		cursor = c;
	}

	public void updateLocationOfFigures(int position) {
		position = (int) Math.floor(position / monitorScale);
		getParent().setLayoutConstraint(this, getFigure(), Integer.valueOf(position));
		Point guideFeedbackLocation = getGuideLineFigure().getBounds().getLocation();
		if (isHorizontal()) {
			guideFeedbackLocation.y = position;
		} else {
			guideFeedbackLocation.x = position;
		}
		getGuideLineFigure().setLocation(guideFeedbackLocation);
		getGuideLineFigure().revalidate();
	}

	public static class GuideLineFigure extends Figure {
		public GuideLineFigure() {
			setPreferredSize(1, 1);
		}

		@Override
		protected void paintFigure(Graphics g) {
			g.setLineStyle(Graphics.LINE_DOT);
			g.setXORMode(true);
			g.setForegroundColor(ColorConstants.darkGray);
			if (bounds.width > bounds.height) {
				g.drawLine(bounds.x, bounds.y, bounds.right(), bounds.y);
				g.drawLine(bounds.x + 2, bounds.y, bounds.right(), bounds.y);
			} else {
				g.drawLine(bounds.x, bounds.y, bounds.x, bounds.bottom());
				g.drawLine(bounds.x, bounds.y + 2, bounds.x, bounds.bottom());
			}
		}
	}

	public static class GuideSelectionPolicy extends SelectionEditPolicy {
		@Override
		protected void hideFocus() {
			((GuideFigure) getHostFigure()).setDrawFocus(false);
		}

		@Override
		protected void hideSelection() {
			((GuideFigure) getHostFigure()).setDrawFocus(false);
		}

		@Override
		protected void showFocus() {
			((GuideFigure) getHostFigure()).setDrawFocus(true);
		}

		@Override
		protected void showSelection() {
			((GuideFigure) getHostFigure()).setDrawFocus(true);
		}
	}

}
