/*******************************************************************************
 * Copyright (c) 2024 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/

package org.eclipse.gef.test.swtbot;

import org.eclipse.swt.widgets.Display;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.osgi.framework.Bundle;

@SuppressWarnings("nls")
public abstract class AbstractSWTBotEditorTests extends AbstractSWTBotTests {
	private static final String PROJECT_NAME = "TestProject";

	@BeforeEach
	@Override
	public void setUp() throws Exception {
		super.setUp();
		// This bundle disables the examples via an activityPatternBinding
		Bundle bundle = Platform.getBundle("org.eclipse.gef.examples.ui.capabilities");
		if (bundle != null) {
			bundle.uninstall();
		}
		IWorkbench wb = PlatformUI.getWorkbench();
		Display display = wb.getDisplay();
		// Close "Welcome" page
		IIntroManager im = wb.getIntroManager();
		IIntroPart intro = wb.getIntroManager().getIntro();
		if (intro != null) {
			display.syncExec(() -> im.closeIntro(intro));
		}
		// Switch to the "Resource" perspective
		display.syncCall(() -> {
			IWorkbenchWindow ww = wb.getActiveWorkbenchWindow();
			return wb.showPerspective("org.eclipse.ui.resourcePerspective", ww);
		});
		// Create & open new test project
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
		display.syncCall(() -> {
			if (!project.exists()) {
				project.create(null);
			}
			project.open(null);
			return null;
		});
		// Create new example file
		IWizardRegistry wr = wb.getNewWizardRegistry();
		IWizardDescriptor wd = wr.findWizard(getWizardId());
		display.syncCall(() -> {
			IWorkbenchWizard wizard = wd.createWizard();
			wizard.init(wb, new StructuredSelection(project));
			WizardDialog wizardDialog = new WizardDialog(display.getActiveShell(), wizard);
			wizardDialog.setBlockOnOpen(false);
			wizardDialog.open();
			WizardNewFileCreationPage wizardPage = (WizardNewFileCreationPage) wizardDialog.getCurrentPage();
			wizardPage.setFileName(getFileName());
			wizard.performFinish();
			wizardDialog.close();
			return null;
		});
	}

	@AfterEach
	@Override
	public void tearDown() throws Exception {
		IWorkbench wb = PlatformUI.getWorkbench();
		Display display = wb.getDisplay();
		// Close all open editors
		display.syncCall(() -> {
			IWorkbenchWindow ww = wb.getActiveWorkbenchWindow();
			IWorkbenchPage page = ww.getActivePage();
			return page.closeAllEditors(false);
		});
		// Delete test project
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
		display.syncCall(() -> {
			project.delete(true, null);
			return null;
		});
		super.tearDown();
	}

	/**
	 * @return The id of the wizard with which the test resources is created.
	 */
	protected abstract String getWizardId();

	/**
	 * @return The file name (with extension) of the test resource.
	 */
	protected abstract String getFileName();

}
