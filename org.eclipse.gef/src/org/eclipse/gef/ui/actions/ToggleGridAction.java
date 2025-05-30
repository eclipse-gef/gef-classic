/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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

import org.eclipse.jface.action.Action;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.internal.GEFMessages;

/**
 * An action that toggles the grid. This action keeps the
 * {@link org.eclipse.gef.SnapToGrid#PROPERTY_GRID_VISIBLE visibility} and
 * {@link org.eclipse.gef.SnapToGrid#PROPERTY_GRID_ENABLED enabled} properties
 * in sync, i.e., it toggles both at the same time. This action can handle the
 * case where these properties are not set on the given viewer initially.
 *
 * @author Pratik Shah
 * @since 3.0
 */
public class ToggleGridAction extends Action {

	private final GraphicalViewer diagramViewer;

	/**
	 * Constructor
	 *
	 * @param diagramViewer the GraphicalViewer whose grid enablement and visibility
	 *                      properties are to be toggled
	 */
	public ToggleGridAction(GraphicalViewer diagramViewer) {
		super(GEFMessages.ToggleGrid_Label, AS_CHECK_BOX);
		this.diagramViewer = diagramViewer;
		setToolTipText(GEFMessages.ToggleGrid_Tooltip);
		setId(GEFActionConstants.TOGGLE_GRID_VISIBILITY);
		setActionDefinitionId(GEFActionConstants.TOGGLE_GRID_VISIBILITY);
		setChecked(isChecked());
	}

	/**
	 * @see org.eclipse.jface.action.IAction#isChecked()
	 */
	@Override
	public boolean isChecked() {
		Boolean val = (Boolean) diagramViewer.getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
		if (val != null) {
			return val.booleanValue();
		}
		return false;
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
	public void run() {
		boolean val = !isChecked();
		diagramViewer.setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, Boolean.valueOf(val));
		diagramViewer.setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, Boolean.valueOf(val));
	}

}
