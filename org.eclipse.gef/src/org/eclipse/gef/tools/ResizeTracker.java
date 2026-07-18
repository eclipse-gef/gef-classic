/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
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
package org.eclipse.gef.tools;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;

import org.eclipse.core.runtime.Platform;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionDimension;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.PrecisionRectangle;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.SharedCursors;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * A Tracker for dragging a resize handle. The ResizeTracker will resize all of
 * the selected editparts in the viewer which understand a RESIZE request. A
 * {@link ChangeBoundsRequest} is sent to each member of the operation set. The
 * tracker allows for the resize direction to be specified in the constructor.
 *
 * @author hudsonr
 */
public class ResizeTracker extends SimpleDragTracker {

	/**
	 * Key modifier for centered resizing. It's ALT on the Mac and MOD1 on all other
	 * platforms.
	 */
	static int MODIFIER_CENTERED_RESIZE;

	static {
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			MODIFIER_CENTERED_RESIZE = SWT.ALT;
		} else {
			MODIFIER_CENTERED_RESIZE = SWT.MOD1;
		}
	}

	/**
	 * Key modifier for constrained resizing. It's SHIFT on all platforms.
	 */
	static final int MODIFIER_CONSTRAINED_RESIZE = SWT.SHIFT;

	private static int FLAG_TARGET_FEEDBACK = SimpleDragTracker.MAX_FLAG << 1;

	/**
	 * The maximum flag used by this class.
	 */
	protected static final int MAX_FLAG = FLAG_TARGET_FEEDBACK;

	private final int direction;
	private final GraphicalEditPart owner;
	private PrecisionRectangle sourceRect;
	private SnapToHelper snapToHelper;

	/**
	 * Constructs a resize tracker that resizes in the specified direction. The
	 * direction is specified using {@link PositionConstants#NORTH},
	 * {@link PositionConstants#NORTH_EAST}, etc.
	 *
	 * @deprecated use ResizeTracker(GraphicalEditPart, int) instead
	 * @param direction the direction
	 */
	@Deprecated
	public ResizeTracker(int direction) {
		this(null, direction);
	}

	/**
	 * Constructs a resize tracker that resizes in the specified direction. The
	 * direction is specified using {@link PositionConstants#NORTH},
	 * {@link PositionConstants#NORTH_EAST}, etc.
	 *
	 * @param owner     of the resize handle which returned this tracker
	 * @param direction the direction
	 */
	public ResizeTracker(GraphicalEditPart owner, int direction) {
		this.owner = owner;
		this.direction = direction;
		setDisabledCursor(SharedCursors.NO);
	}

	/**
	 * @see org.eclipse.gef.Tool#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		if (owner != null) {
			if (getTargetEditPart() != null) {
				snapToHelper = getTargetEditPart().getAdapter(SnapToHelper.class);
			}

			IFigure figure = owner.getFigure();
			if (figure instanceof HandleBounds handleBounds) {
				sourceRect = new PrecisionRectangle(handleBounds.getHandleBounds());
			} else {
				sourceRect = new PrecisionRectangle(figure.getBounds());
			}
			figure.translateToAbsolute(sourceRect);
		}
	}

	/**
	 * @see org.eclipse.gef.tools.AbstractTool#commitDrag()
	 */
	@Override
	public void commitDrag() {
		eraseTargetFeedback();
		super.commitDrag();
	}

	/**
	 * Returns all selected parts which understand resizing.
	 *
	 * @see org.eclipse.gef.tools.AbstractTool#createOperationSet()
	 */
	@Override
	protected List<? extends EditPart> createOperationSet() {
		List<? extends EditPart> list = super.createOperationSet();
		ToolUtilities.filterEditPartsUnderstanding(list, getSourceRequest());
		return list;
	}

	/**
	 * @see org.eclipse.gef.tools.SimpleDragTracker#createSourceRequest()
	 */
	@Override
	protected Request createSourceRequest() {
		ChangeBoundsRequest request;
		request = new ChangeBoundsRequest(REQ_RESIZE);
		request.setResizeDirection(getResizeDirection());
		return request;
	}

	/**
	 * @see org.eclipse.gef.tools.AbstractTool#deactivate()
	 */
	@Override
	public void deactivate() {
		// For the case where ESC key was hit while resizing
		eraseTargetFeedback();

		sourceRect = null;
		snapToHelper = null;
		super.deactivate();
	}

	/**
	 * This method is invoked when the resize operation is complete. It notifies the
	 * {@link #getTargetEditPart() target} to erase target feedback.
	 */
	protected void eraseTargetFeedback() {
		if (!getFlag(FLAG_TARGET_FEEDBACK)) {
			return;
		}
		if (getTargetEditPart() != null) {
			getTargetEditPart().eraseTargetFeedback(getSourceRequest());
		}
		setFlag(FLAG_TARGET_FEEDBACK, false);
	}

	/**
	 * @see org.eclipse.gef.tools.AbstractTool#getCommand()
	 */
	@Override
	protected Command getCommand() {
		CompoundCommand command = new CompoundCommand();
		command.setDebugLabel("Resize Handle Tracker");//$NON-NLS-1$
		getOperationSet().forEach(ep -> command.add(ep.getCommand(getSourceRequest())));
		return command.unwrap();
	}

	/**
	 * @see org.eclipse.gef.tools.AbstractTool#getCommandName()
	 */
	@Override
	protected String getCommandName() {
		return REQ_RESIZE;
	}

	/**
	 * @see org.eclipse.gef.tools.AbstractTool#getDefaultCursor()
	 */
	@Override
	protected Cursor getDefaultCursor() {
		if (getTargetEditPart() != null) {
			return Cursors.getDirectionalCursor(direction, getTargetEditPart().getFigure().isMirrored());
		}
		return Cursors.getDirectionalCursor(direction);
	}

	/**
	 * @see org.eclipse.gef.tools.AbstractTool#getDebugName()
	 */
	@Override
	protected String getDebugName() {
		return "Resize Handle Tracker";//$NON-NLS-1$
	}

	/**
	 * Returns the owner of this resize tracker.
	 *
	 * @since 3.7
	 */
	protected GraphicalEditPart getOwner() {
		return owner;
	}

	/**
	 * Returns the direction of the resize (NORTH, EAST, NORTH_EAST, etc.). These
	 * constants are from {@link PositionConstants}.
	 *
	 * @return the resize direction.
	 */
	protected int getResizeDirection() {
		return direction;
	}

	/**
	 * The TargetEditPart is the parent of the EditPart being resized.
	 *
	 * @return The target EditPart; may be <code>null</code>.
	 */
	protected GraphicalEditPart getTargetEditPart() {
		if (owner != null) {
			return (GraphicalEditPart) owner.getParent();
		}
		return null;
	}

	/**
	 * If dragging is in progress, cleans up feedback and calls performDrag().
	 *
	 * @see org.eclipse.gef.tools.SimpleDragTracker#handleButtonUp(int)
	 */
	@Override
	protected boolean handleButtonUp(int button) {
		if (stateTransition(STATE_DRAG_IN_PROGRESS, STATE_TERMINAL)) {
			eraseSourceFeedback();
			eraseTargetFeedback();
			performDrag();
		}
		return true;
	}

	/**
	 * Updates the command and the source request, and shows feedback.
	 *
	 * @see org.eclipse.gef.tools.SimpleDragTracker#handleDragInProgress()
	 */
	@Override
	protected boolean handleDragInProgress() {
		if (isInDragInProgress()) {
			updateSourceRequest();
			showSourceFeedback();
			showTargetFeedback();
			setCurrentCommand(getCommand());
		}
		return true;
	}

	/**
	 * This method is invoked as the drag is happening. It notifies the
	 * {@link #getTargetEditPart() target} to show target feedback.
	 */
	protected void showTargetFeedback() {
		setFlag(FLAG_TARGET_FEEDBACK, true);
		if (getTargetEditPart() != null) {
			getTargetEditPart().showTargetFeedback(getSourceRequest());
		}
	}

	/**
	 * @see org.eclipse.gef.tools.SimpleDragTracker#updateSourceRequest()
	 */
	@Override
	protected void updateSourceRequest() {
		ChangeBoundsRequest request = (ChangeBoundsRequest) getSourceRequest();
		Dimension d = getDragMoveDelta();

		Point location = new Point(getLocation());
		Point moveDelta = new Point(0, 0);
		Dimension resizeDelta = new Dimension(0, 0);

		request.setConstrainedResize(getCurrentInput().isModKeyDown(MODIFIER_CONSTRAINED_RESIZE));
		request.setCenteredResize(getCurrentInput().isModKeyDown(MODIFIER_CENTERED_RESIZE));
		request.setSnapToEnabled(!getCurrentInput().isModKeyDown(MODIFIER_NO_SNAPPING));

		if (request.isConstrainedResize() && owner != null) {
			request.setConstrainedResize(true);

			int origHeight = owner.getFigure().getBounds().height;
			int origWidth = owner.getFigure().getBounds().width;
			float ratio = 1;

			if (origWidth != 0 && origHeight != 0) {
				ratio = ((float) origHeight / (float) origWidth);
			}

			if (getResizeDirection() == PositionConstants.SOUTH_EAST) {
				if (d.height > (d.width * ratio)) {
					d.width = (int) (d.height / ratio);
				} else {
					d.height = (int) (d.width * ratio);
				}
			} else if (getResizeDirection() == PositionConstants.NORTH_WEST) {
				if (d.height < (d.width * ratio)) {
					d.width = (int) (d.height / ratio);
				} else {
					d.height = (int) (d.width * ratio);
				}
			} else if (getResizeDirection() == PositionConstants.NORTH_EAST) {
				if (-(d.height) > (d.width * ratio)) {
					d.width = -(int) (d.height / ratio);
				} else {
					d.height = -(int) (d.width * ratio);
				}
			} else if (getResizeDirection() == PositionConstants.SOUTH_WEST) {
				if (-(d.height) < (d.width * ratio)) {
					d.width = -(int) (d.height / ratio);
				} else {
					d.height = -(int) (d.width * ratio);
				}
			}
		}

		if ((getResizeDirection() & PositionConstants.NORTH) != 0) {
			if (request.isCenteredResize()) {
				resizeDelta.height -= d.height;
			}
			moveDelta.y += d.height;
			resizeDelta.height -= d.height;
		}
		if ((getResizeDirection() & PositionConstants.SOUTH) != 0) {
			if (request.isCenteredResize()) {
				moveDelta.y -= d.height;
				resizeDelta.height += d.height;
			}
			resizeDelta.height += d.height;
		}
		if ((getResizeDirection() & PositionConstants.WEST) != 0) {
			if (request.isCenteredResize()) {
				resizeDelta.width -= d.width;
			}
			moveDelta.x += d.width;
			resizeDelta.width -= d.width;
		}
		if ((getResizeDirection() & PositionConstants.EAST) != 0) {
			if (request.isCenteredResize()) {
				moveDelta.x -= d.width;
				resizeDelta.width += d.width;
			}
			resizeDelta.width += d.width;
		}

		request.setMoveDelta(moveDelta);
		request.setSizeDelta(resizeDelta);
		request.setLocation(location);
		request.setEditParts(getOperationSet());
		request.getExtendedData().clear();
		request.setResizeDirection(getResizeDirection());

		if (request.isSnapToEnabled() && snapToHelper != null) {
			PrecisionRectangle rect = sourceRect.getPreciseCopy();
			rect.translate(moveDelta);
			rect.resize(resizeDelta);
			PrecisionRectangle result = new PrecisionRectangle();

			snapToHelper.snapRectangle(request, request.getResizeDirection(), rect, result);
			if (request.isCenteredResize()) {
				if (result.preciseX() != 0.0) {
					result.setPreciseWidth(result.preciseWidth() - result.preciseX());
				} else if (result.preciseWidth() != 0.0) {
					result.setPreciseX(-result.preciseWidth());
					result.setPreciseWidth(result.preciseWidth() * 2.0);
				}

				if (result.preciseY() != 0.0) {
					result.setPreciseHeight(result.preciseHeight() - result.preciseY());
				} else if (result.preciseHeight() != 0.0) {
					result.setPreciseY(-result.preciseHeight());
					result.setPreciseHeight(result.preciseHeight() * 2.0);
				}
			}

			PrecisionPoint preciseMove = new PrecisionPoint(result.preciseX() + moveDelta.x,
					result.preciseY() + moveDelta.y);

			PrecisionDimension preciseResize = new PrecisionDimension(result.preciseWidth() + resizeDelta.width,
					result.preciseHeight() + resizeDelta.height);

			request.setMoveDelta(preciseMove);
			request.setSizeDelta(preciseResize);
		}

		enforceConstraintsForResize(request);
	}

	/**
	 * Ensures size constraints (by default minimum and maximum) are respected by
	 * the given request. May be overwritten by clients to enforce additional
	 * constraints.
	 *
	 * @param changeBoundsRequest The request to validate
	 * @since 3.7
	 */
	protected void enforceConstraintsForResize(ChangeBoundsRequest changeBoundsRequest) {
		// adjust request, so that minimum and maximum size constraints are
		// respected
		if (owner != null) {
			PrecisionRectangle originalConstraint = new PrecisionRectangle(owner.getFigure().getBounds());
			owner.getFigure().translateToAbsolute(originalConstraint);
			PrecisionRectangle manipulatedConstraint = new PrecisionRectangle(
					changeBoundsRequest.getTransformedRectangle(originalConstraint));
			owner.getFigure().translateToRelative(manipulatedConstraint);
			// validate constraint (maximum and minimum size are regarded to be
			// 'normalized', i.e. relative to this figure's bounds coordinates).
			manipulatedConstraint
					.setSize(Dimension.max(manipulatedConstraint.getSize(), getMinimumSizeFor(changeBoundsRequest)));
			manipulatedConstraint
					.setSize(Dimension.min(manipulatedConstraint.getSize(), getMaximumSizeFor(changeBoundsRequest)));
			// translate back to absolute
			owner.getFigure().translateToAbsolute(manipulatedConstraint);
			Dimension newSizeDelta = manipulatedConstraint.getSize().getShrinked(originalConstraint.getSize());
			changeBoundsRequest.setSizeDelta(newSizeDelta);
			if (isTopOrLeftResize()) {
				enforceResizeConstraintsForBottomRightCorner(changeBoundsRequest);
			}
		}
	}

	/**
	 * Ensures that the in addition to the size constraints also the position is
	 * respected by the given request. It ensure that on resize the bottom right
	 * corner is fixed when resized from any top/left direction. May be overwritten
	 * by clients to enforce additional constraints.
	 *
	 * @param changeBoundsRequest The request to validate
	 * @since 3.13
	 */
	protected void enforceResizeConstraintsForBottomRightCorner(final ChangeBoundsRequest changeBoundsRequest) {
		final PrecisionRectangle originalConstraint = new PrecisionRectangle(getOwner().getFigure().getBounds());
		getOwner().getFigure().translateToAbsolute(originalConstraint);
		final PrecisionRectangle manipulatedConstraint = new PrecisionRectangle(
				changeBoundsRequest.getTransformedRectangle(originalConstraint));
		final Point origBR = originalConstraint.getBottomRight();
		final Point newBR = manipulatedConstraint.getBottomRight();

		if (isLeftResize() && (origBR.x < newBR.x)) {
			final Point moveDelta = changeBoundsRequest.getMoveDelta();
			moveDelta.x -= (newBR.x - origBR.x);
			changeBoundsRequest.setMoveDelta(moveDelta);
		}

		if (isTopResize() && (origBR.y < newBR.y)) {
			final Point moveDelta = changeBoundsRequest.getMoveDelta();
			moveDelta.y -= (newBR.y - origBR.y);
			changeBoundsRequest.setMoveDelta(moveDelta);
		}
	}

	/**
	 * Determines the <em>maximum</em> size that the host can be resized to for a
	 * given request. By default, a default value is returned. The value is
	 * interpreted to be a dimension in the host figure's coordinate system (i.e.
	 * relative to its bounds), so it is not affected by zooming affects.
	 *
	 * @param request the ChangeBoundsRequest
	 * @return the minimum size
	 * @since 3.7
	 */
	@SuppressWarnings("static-method")
	protected Dimension getMaximumSizeFor(ChangeBoundsRequest request) {
		return IFigure.MAX_DIMENSION;
	}

	/**
	 * Determines the <em>minimum</em> size that the specified child can be resized
	 * to.By default, a default value is returned. The value is interpreted to be a
	 * dimension in the host figure's coordinate system (i.e. relative to its
	 * bounds), so it is not affected by zooming effects.
	 *
	 * @param request the ChangeBoundsRequest
	 * @return the minimum size
	 * @since 3.7
	 */
	@SuppressWarnings("static-method")
	protected Dimension getMinimumSizeFor(ChangeBoundsRequest request) {
		return IFigure.MIN_DIMENSION;
	}

	private boolean isTopOrLeftResize() {
		return isLeftResize() || isTopResize();
	}

	private boolean isLeftResize() {
		return getResizeDirection() == PositionConstants.WEST || getResizeDirection() == PositionConstants.NORTH_WEST
				|| getResizeDirection() == PositionConstants.SOUTH_WEST;
	}

	private boolean isTopResize() {
		return getResizeDirection() == PositionConstants.NORTH || getResizeDirection() == PositionConstants.NORTH_WEST
				|| getResizeDirection() == PositionConstants.NORTH_EAST;
	}

}
