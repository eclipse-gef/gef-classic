/*******************************************************************************
 * Copyright (c) 2005, 2025 IBM Corporation and others.
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
package org.eclipse.gef.examples.text.wizard;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (text).
 */
public class NewFileWizardPage extends WizardNewFileCreationPage {

	/**
	 * Constructor for SampleNewWizardPage.
	 *
	 */
	public NewFileWizardPage(IStructuredSelection selection) {
		super("wizardPage", selection); //$NON-NLS-1$
		setTitle("GEF WYSIWYG Text Document"); //$NON-NLS-1$
		setDescription("""
				This wizard creates a GEF-based WYSIWYG text document with \
				a *.text. extension.  Choose a container and file name for the new\
				 resource."""); //$NON-NLS-1$
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		setFileName("new_file.text"); //$NON-NLS-1$
		setPageComplete(validatePage());
	}
}