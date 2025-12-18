/*******************************************************************************
 * Copyright (c) 2003, 2025 IBM Corporation and others.
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

import java.util.Iterator;

import org.eclipse.swt.graphics.Cursor;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.tools.SimpleDragTracker;

/**
 * @author Pratik Shah
 */
public class RulerDragTracker extends SimpleDragTracker {

	protected RulerEditPart source;
	private final IFigure guide, guideline;

	public RulerDragTracker(RulerEditPart source) {
		this.source = source;
		guide = new GuideFigure(!source.isHorizontal());
		guide.setVisible(false);
		guideline = new GuideEditPart.GuideLineFigure();
		guideline.setVisible(false);
	}

	@Override
	protected void eraseSourceFeedback() {
		if (guide.getParent() != null) {
			guide.getParent().remove(guide);
		}
		if (guideline.getParent() != null) {
			guideline.getParent().remove(guideline);
		}
	}

	@Override
	protected Command getCommand() {
		if (isCreationValid() && !isDelete()) {
			return source.getRulerProvider().getCreateGuideCommand(getCurrentPosition());
		}
		return UnexecutableCommand.INSTANCE;
	}

	@Override
	protected String getCommandName() {
		return REQ_CREATE;
	}

	protected int getCurrentPositionZoomed() {
		/*
		 * @TODO:Pratik you should cache this, current position, isDelete boolean and
		 * isCreationValid boolean
		 */
		Point pt = getLocation();
		source.getFigure().translateToRelative(pt);
		return source.isHorizontal() ? pt.x : pt.y;
	}

	protected int getCurrentPosition() {
		int position = getCurrentPositionZoomed();
		ZoomManager zoomManager = source.getZoomManager();
		if (zoomManager != null) {
			position = (int) Math.round(position / zoomManager.getZoom());
		}
		return position;
	}

	@Override
	protected String getDebugName() {
		return "Guide creation"; //$NON-NLS-1$
	}

	@Override
	protected Cursor getDefaultCursor() {
		if (isDelete()) {
			return super.getDefaultCursor();
		}
		if (isCreationValid()) {
			return source.isHorizontal() ? Cursors.SIZEE : Cursors.SIZEN;
		}
		return Cursors.NO;
	}

	@Override
	protected boolean handleButtonDown(int button) {
		stateTransition(STATE_INITIAL, STATE_DRAG_IN_PROGRESS);
		showSourceFeedback();
		return true;
	}

	@Override
	protected boolean handleButtonUp(int button) {
		if (stateTransition(STATE_DRAG_IN_PROGRESS, STATE_TERMINAL)) {
			setCurrentCommand(getCommand());
			executeCurrentCommand();
		}
		return true;
	}

	protected boolean isCreationValid() {
		if (getState() == STATE_INVALID) {
			return false;
		}
		int position = getCurrentPosition();
		Iterator<?> guides = source.getRulerProvider().getGuides().iterator();
		while (guides.hasNext()) {
			int guidePos = source.getRulerProvider().getGuidePosition(guides.next());
			if (Math.abs(guidePos - position) < GuideEditPart.MIN_DISTANCE_BW_GUIDES) {
				return false;
			}
		}
		return true;
	}

	protected boolean isDelete() {
		int pos;
		int max;
		int min;
		if (!source.isHorizontal()) {
			pos = getLocation().x;
			Rectangle zone = guide.getBounds().getExpanded(GuideEditPart.DELETE_THRESHOLD, 0);
			min = zone.x;
			max = min + zone.width;
		} else {
			pos = getLocation().y;
			Rectangle zone = guide.getBounds().getExpanded(0, GuideEditPart.DELETE_THRESHOLD);
			min = zone.y;
			max = min + zone.height;
		}
		return pos < min || pos > max;
	}

	@Override
	protected boolean movedPastThreshold() {
		return true;
	}

	@Override
	protected void showSourceFeedback() {
		if (guide.getParent() == null) {
			getCurrentViewer().deselectAll();
			source.getFigure().add(guide);
		}
		if (guideline.getParent() == null) {
			source.getGuideLayer().add(guideline);
		}
		source.setLayoutConstraint(null, guide, Integer.valueOf(getCurrentPositionZoomed()));
		Rectangle bounds = Rectangle.SINGLETON;
		if (source.isHorizontal()) {
			bounds.x = getCurrentPositionZoomed();
			bounds.y = source.getGuideLayer().getBounds().y;
			bounds.width = 1;
			bounds.height = source.getGuideLayer().getBounds().height;
		} else {
			bounds.x = source.getGuideLayer().getBounds().x;
			bounds.y = getCurrentPositionZoomed();
			bounds.width = source.getGuideLayer().getBounds().width;
			bounds.height = 1;
		}
		guideline.setBounds(bounds);
		guide.setVisible(isCreationValid() && !isDelete());
		guideline.setVisible(isCreationValid() && !isDelete());
		guideline.validate();
	}

}
