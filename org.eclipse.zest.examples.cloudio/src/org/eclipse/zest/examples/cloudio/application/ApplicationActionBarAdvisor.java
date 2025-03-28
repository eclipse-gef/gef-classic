/*******************************************************************************
 * Copyright (c) 2011, 2024 Stephan Schwiebert and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Stephan Schwiebert - initial API and implementation
 ******************************************************************************/
package org.eclipse.zest.examples.cloudio.application;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.zest.examples.cloudio.application.actions.AboutAction;

/**
 *
 * @author sschwieb
 *
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction exitAction;
	private IWorkbenchAction aboutAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window) {
		exitAction = ActionFactory.QUIT.create(window);
		AboutAction about = new AboutAction();
		register(about);
		register(exitAction);
		this.aboutAction = about;

	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_M_File,
				IWorkbenchActionConstants.M_FILE);
		menuBar.add(fileMenu);
		ActionContributionItem aboutActionItem = new ActionContributionItem(aboutAction);
		fileMenu.add(aboutActionItem);
		fileMenu.add(new Separator());
		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		fileMenu.add(new Separator());
		MenuManager editMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_M_Edit,
				IWorkbenchActionConstants.M_EDIT);
		editMenu.add(new GroupMarker(Messages.ApplicationActionBarAdvisor_Select));
		editMenu.add(new Separator());
		editMenu.add(new GroupMarker(Messages.ApplicationActionBarAdvisor_Zoom));
		menuBar.add(editMenu);
		super.fillMenuBar(menuBar);
		ActionContributionItem exitActionItem = new ActionContributionItem(exitAction);
		fileMenu.add(exitActionItem);
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			aboutActionItem.setVisible(false);
			exitActionItem.setVisible(false);
		}
	}

}
