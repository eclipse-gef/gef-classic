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
import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.rulers.RulerProvider;

/**
 * An action that toggles the ruler
 * {@link org.eclipse.gef.rulers.RulerProvider#PROPERTY_RULER_VISIBILITY
 * visibility} property on the given viewer. This action can handle the case
 * where that property is not set on the viewer initially.
 *
 * @author Pratik Shah
 * @since 3.0
 */
public class ToggleRulerVisibilityAction extends Action {

	/**
	 * The viewer whose ruler visibility property is to be toggled
	 */
	protected GraphicalViewer diagramViewer;

	/**
	 * Constructor
	 *
	 * @param diagramViewer the viewer whose ruler visibility property is to be
	 *                      toggled
	 */
	public ToggleRulerVisibilityAction(GraphicalViewer diagramViewer) {
		super(GEFMessages.ToggleRulerVisibility_Label, AS_CHECK_BOX);
		this.diagramViewer = diagramViewer;
		setToolTipText(GEFMessages.ToggleRulerVisibility_Tooltip);
		setId(GEFActionConstants.TOGGLE_RULER_VISIBILITY);
		setActionDefinitionId(GEFActionConstants.TOGGLE_RULER_VISIBILITY);
		setChecked(isChecked());
	}

	/**
	 * @see org.eclipse.jface.action.IAction#isChecked()
	 */
	@Override
	public boolean isChecked() {
		Boolean val = ((Boolean) diagramViewer.getProperty(RulerProvider.PROPERTY_RULER_VISIBILITY));
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
		diagramViewer.setProperty(RulerProvider.PROPERTY_RULER_VISIBILITY, Boolean.valueOf(!isChecked()));
	}

}
