/*******************************************************************************
 * Copyright (c) 2000, 2022 IBM Corporation and others.
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
package org.eclipse.gef.examples.logicdesigner.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;

import org.eclipse.draw2d.PositionConstants;

import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.AlignmentRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.MatchHeightRetargetAction;
import org.eclipse.gef.ui.actions.MatchSizeRetargetAction;
import org.eclipse.gef.ui.actions.MatchWidthRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;

import org.eclipse.gef.examples.logicdesigner.LogicMessages;

public class LogicActionBarContributor extends ActionBarContributor {

	/**
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
	@Override
	protected void buildActions() {
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());

		addRetargetAction(new IncrementRetargetAction());
		addRetargetAction(new DecrementRetargetAction());

		addRetargetAction(new AlignmentRetargetAction(PositionConstants.LEFT));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.CENTER));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.RIGHT));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.TOP));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.MIDDLE));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.BOTTOM));

		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());

		addRetargetAction(new MatchSizeRetargetAction());
		addRetargetAction(new MatchWidthRetargetAction());
		addRetargetAction(new MatchHeightRetargetAction());

		addRetargetAction(new RetargetAction(GEFActionConstants.TOGGLE_RULER_VISIBILITY,
				GEFMessages.ToggleRulerVisibility_Label, IAction.AS_CHECK_BOX));

		addRetargetAction(new RetargetAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY,
				GEFMessages.ToggleSnapToGeometry_Label, IAction.AS_CHECK_BOX));

		addRetargetAction(new RetargetAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY, GEFMessages.ToggleGrid_Label,
				IAction.AS_CHECK_BOX));
	}

	/**
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
	 */
	@Override
	protected void declareGlobalActionKeys() {
		addGlobalActionKey(ActionFactory.PRINT.getId());
		addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
		addGlobalActionKey(ActionFactory.PASTE.getId());
		addGlobalActionKey(ActionFactory.DELETE.getId());
	}

	/**
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(IToolBarManager)
	 */
	@Override
	public void contributeToToolBar(IToolBarManager tbm) {
		tbm.add(getAction(ActionFactory.UNDO.getId()));
		tbm.add(getAction(ActionFactory.REDO.getId()));

		tbm.add(new Separator());
		tbm.add(getAction(IncrementDecrementAction.DECREMENT));
		tbm.add(getAction(IncrementDecrementAction.INCREMENT));

		tbm.add(new Separator());
		tbm.add(getAction(GEFActionConstants.ALIGN_LEFT));
		tbm.add(getAction(GEFActionConstants.ALIGN_CENTER));
		tbm.add(getAction(GEFActionConstants.ALIGN_RIGHT));
		tbm.add(new Separator());
		tbm.add(getAction(GEFActionConstants.ALIGN_TOP));
		tbm.add(getAction(GEFActionConstants.ALIGN_MIDDLE));
		tbm.add(getAction(GEFActionConstants.ALIGN_BOTTOM));

		tbm.add(new Separator());
		tbm.add(getAction(GEFActionConstants.MATCH_SIZE));
		tbm.add(getAction(GEFActionConstants.MATCH_WIDTH));
		tbm.add(getAction(GEFActionConstants.MATCH_HEIGHT));

		tbm.add(new Separator());
		String[] zoomStrings = { ZoomManager.FIT_ALL, ZoomManager.FIT_HEIGHT, ZoomManager.FIT_WIDTH };
		tbm.add(new ZoomComboContributionItem(getPage(), zoomStrings));
	}

	/**
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(IMenuManager)
	 */
	@Override
	public void contributeToMenu(IMenuManager menubar) {
		super.contributeToMenu(menubar);
		MenuManager viewMenu = new MenuManager(LogicMessages.ViewMenu_LabelText);
		viewMenu.add(getAction(GEFActionConstants.ZOOM_IN));
		viewMenu.add(getAction(GEFActionConstants.ZOOM_OUT));
		viewMenu.add(new Separator());
		viewMenu.add(getAction(GEFActionConstants.TOGGLE_RULER_VISIBILITY));
		viewMenu.add(getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY));
		viewMenu.add(getAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY));
		viewMenu.add(new Separator());
		viewMenu.add(getAction(GEFActionConstants.MATCH_SIZE));
		viewMenu.add(getAction(GEFActionConstants.MATCH_WIDTH));
		viewMenu.add(getAction(GEFActionConstants.MATCH_HEIGHT));
		menubar.insertAfter(IWorkbenchActionConstants.M_EDIT, viewMenu);
	}

}
