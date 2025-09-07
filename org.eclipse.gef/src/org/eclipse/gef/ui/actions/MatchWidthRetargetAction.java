/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
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
 * A LabelRetargetAction for MatchWidthAction.
 */
public class MatchWidthRetargetAction extends LabelRetargetAction {

	/**
	 * Constructs a <code>MatchWidthRetargetAction</code>.
	 */
	public MatchWidthRetargetAction() {
		super(GEFActionConstants.MATCH_WIDTH, GEFMessages.MatchWidthAction_Label);
		setImageDescriptor(InternalImages.DESC_MATCH_WIDTH);
		if (InternalGEFPlugin.requiresDisabledIcon()) {
			setDisabledImageDescriptor(InternalImages.DESC_MATCH_WIDTH_DIS);
		}
		setToolTipText(GEFMessages.MatchWidthAction_Tooltip);
	}

}
