/*******************************************************************************
 * Copyright (c) 2000, 2026 IBM Corporation and others.
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
package org.eclipse.gef.ui.actions;

import java.util.List;

import org.eclipse.ui.IWorkbenchPart;

import org.eclipse.draw2d.geometry.PrecisionDimension;
import org.eclipse.draw2d.geometry.PrecisionRectangle;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.internal.InternalGEFPlugin;
import org.eclipse.gef.internal.InternalImages;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * An action that matches the size of all selected EditPart's Figures to the
 * size of the Primary Selection EditPart's Figure.
 *
 * @since 3.7
 */
public class MatchSizeAction extends SelectionAction {

	/**
	 * Constructs a <code>MatchSizeAction</code> and associates it with the given
	 * part.
	 *
	 * @param part The workbench part associated with this MatchSizeAction
	 */
	@SuppressWarnings("deprecation")
	public MatchSizeAction(IWorkbenchPart part) {
		super(part);
		setText(GEFMessages.MatchSizeAction_Label);
		setImageDescriptor(InternalImages.DESC_MATCH_SIZE);
		if (InternalGEFPlugin.requiresDisabledIcon()) {
			setDisabledImageDescriptor(InternalImages.DESC_MATCH_SIZE_DIS);
		}
		setToolTipText(GEFMessages.MatchSizeAction_Tooltip);
		setId(GEFActionConstants.MATCH_SIZE);
	}

	/**
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	@Override
	protected boolean calculateEnabled() {
		Command cmd = createMatchSizeCommand(getSelectedEditParts());
		if (cmd == null) {
			return false;
		}
		return cmd.canExecute();
	}

	/**
	 * Create a command to resize the selected objects.
	 *
	 * @param editParts The objects to be resized.
	 * @return The command to resize the selected objects.
	 */
	private Command createMatchSizeCommand(List<EditPart> editParts) {
		if (editParts.isEmpty()) {
			return null;
		}
		if (!(editParts.get(0) instanceof GraphicalEditPart)) {
			return null;
		}

		GraphicalEditPart primarySelection = getPrimarySelectionEditPart(editParts);

		if (primarySelection == null) {
			return null;
		}

		final CompoundCommand command = new CompoundCommand();
		final PrecisionRectangle precisePrimaryBounds = new PrecisionRectangle(
				primarySelection.getFigure().getBounds().getCopy());
		primarySelection.getFigure().translateToAbsolute(precisePrimaryBounds);

		editParts.stream().filter(GraphicalEditPart.class::isInstance).map(GraphicalEditPart.class::cast)
				.forEach(part -> {
					if (!part.equals(primarySelection)) {
						ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE);

						PrecisionRectangle precisePartBounds = new PrecisionRectangle(
								part.getFigure().getBounds().getCopy());
						part.getFigure().translateToAbsolute(precisePartBounds);

						PrecisionDimension preciseDimension = new PrecisionDimension();
						preciseDimension.setPreciseWidth(getPreciseWidthDelta(precisePartBounds, precisePrimaryBounds));
						preciseDimension
								.setPreciseHeight(getPreciseHeightDelta(precisePartBounds, precisePrimaryBounds));

						request.setSizeDelta(preciseDimension);

						Command cmd = part.getCommand(request);
						if (cmd != null) {
							command.add(cmd);
						}
					}
				});

		return command;
	}

	/**
	 * Returns the height delta between the two bounds. Separated into a method so
	 * that it can be overwritten to return 0 in the case of a width-only action.
	 *
	 * @param precisePartBounds    the precise bounds of the EditPart's Figure to be
	 *                             matched
	 * @param precisePrimaryBounds the precise bounds of the Primary Selection
	 *                             EditPart's Figure
	 * @return the delta between the two heights to be used in the Request.
	 */
	@SuppressWarnings("static-method")
	protected double getPreciseHeightDelta(PrecisionRectangle precisePartBounds,
			PrecisionRectangle precisePrimaryBounds) {
		return precisePrimaryBounds.preciseHeight() - precisePartBounds.preciseHeight();
	}

	private static GraphicalEditPart getPrimarySelectionEditPart(List<EditPart> editParts) {
		for (EditPart editPart : editParts) {
			if (editPart.getSelected() == EditPart.SELECTED_PRIMARY && editPart instanceof GraphicalEditPart gEP) {
				return gEP;
			}
		}
		return null;
	}

	/**
	 * Returns the width delta between the two bounds. Separated into a method so
	 * that it can be overriden to return 0 in the case of a height-only action.
	 *
	 * @param precisePartBounds    the precise bounds of the EditPart's Figure to be
	 *                             matched
	 * @param precisePrimaryBounds the precise bounds of the Primary Selection
	 *                             EditPart's Figure
	 * @return the delta between the two widths to be used in the Request.
	 */
	@SuppressWarnings("static-method")
	protected double getPreciseWidthDelta(PrecisionRectangle precisePartBounds,
			PrecisionRectangle precisePrimaryBounds) {
		return precisePrimaryBounds.preciseWidth() - precisePartBounds.preciseWidth();
	}

	/**
	 * Executes this action, cycling through the selected EditParts in the Action's
	 * viewer, and matching the size of the selected EditPart's Figures to that of
	 * the Primary Selection's Figure.
	 */
	@Override
	public void run() {
		execute(createMatchSizeCommand(getSelectedEditParts()));
	}

}
