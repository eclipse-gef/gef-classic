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

import org.eclipse.ui.actions.LabelRetargetAction;

import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.internal.InternalGEFPlugin;
import org.eclipse.gef.internal.InternalImages;

/**
 * A LabelRetargetAction for MatchHeightAction.
 */
public class MatchHeightRetargetAction extends LabelRetargetAction {

	/**
	 * Constructs a <code>MatchHeightRetargetAction</code>.
	 */
	@SuppressWarnings("deprecation")
	public MatchHeightRetargetAction() {
		super(GEFActionConstants.MATCH_HEIGHT, GEFMessages.MatchHeightAction_Label);
		setImageDescriptor(InternalImages.DESC_MATCH_HEIGHT);
		if (InternalGEFPlugin.requiresDisabledIcon()) {
			setDisabledImageDescriptor(InternalImages.DESC_MATCH_HEIGHT_DIS);
		}
		setToolTipText(GEFMessages.MatchHeightAction_Tooltip);
	}

}
