/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.examples.flow.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.examples.flow.FlowPlugin;
import org.eclipse.gef.examples.flow.model.Activity;
import org.eclipse.gef.examples.flow.model.ActivityDiagram;
import org.eclipse.gef.examples.flow.model.ParallelActivity;
import org.eclipse.gef.examples.flow.model.SequentialActivity;
import org.eclipse.gef.examples.flow.model.Transition;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;

/**
 * FlowWizardPage1
 * 
 * @author Daniel Lee
 */
public class FlowWizardPage1 extends WizardNewFileCreationPage {

	private IWorkbench workbench;
	private static int exampleCount = 1;

	public FlowWizardPage1(IWorkbench aWorkbench, IStructuredSelection selection) {
		super("sampleFlowPage1", selection); //$NON-NLS-1$
		this.setTitle("Create Flow Example File"); //$NON-NLS-1$
		this.setDescription("Create a new flow file resource"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromFile(FlowPlugin.class, "images/flowbanner.gif")); //$NON-NLS-1$
		this.workbench = aWorkbench;
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		this.setFileName("flowExample" + exampleCount + ".flow"); //$NON-NLS-1$ //$NON-NLS-2$
		setPageComplete(validatePage());
	}

	private static ActivityDiagram createWakeupModel() {
		ActivityDiagram diagram = new ActivityDiagram();
		SequentialActivity wakeup = new SequentialActivity();
		Activity backToSleep = new Activity("Go back to sleep"); //$NON-NLS-1$
		Activity turnOff = new Activity("Turn off alarm"); //$NON-NLS-1$
		wakeup.setName("Wake up"); //$NON-NLS-1$
		wakeup.addChild(new Activity("Hit snooze button")); //$NON-NLS-1$
		wakeup.addChild(backToSleep);
		wakeup.addChild(turnOff);
		wakeup.addChild(new Activity("Get out of bed")); //$NON-NLS-1$
		diagram.addChild(wakeup);

		SequentialActivity bathroom = new SequentialActivity();
		bathroom.addChild(new Activity("Brush teeth")); //$NON-NLS-1$
		bathroom.addChild(new Activity("Take shower")); //$NON-NLS-1$
		bathroom.addChild(new Activity("Comb hair")); //$NON-NLS-1$
		bathroom.setName("Bathroom activities"); //$NON-NLS-1$
		diagram.addChild(bathroom);

		ParallelActivity relaxation = new ParallelActivity();
		relaxation.addChild(new Activity("Watch cartoons")); //$NON-NLS-1$
		relaxation.addChild(new Activity("Power Yoga")); //$NON-NLS-1$
		relaxation.setName("Morning relaxation ritual"); //$NON-NLS-1$
		diagram.addChild(relaxation);

		Activity sleep, alarm, alarm2, clothes, spare, no, yes, drive;
		diagram.addChild(sleep = new Activity("Sleep.....")); //$NON-NLS-1$
		diagram.addChild(alarm = new Activity("Alarm!!!")); //$NON-NLS-1$
		diagram.addChild(alarm2 = new Activity("Alarm!!!")); //$NON-NLS-1$
		diagram.addChild(clothes = new Activity("Put on clothes")); //$NON-NLS-1$
		diagram.addChild(spare = new Activity("Is there time to spare?")); //$NON-NLS-1$
		diagram.addChild(yes = new Activity("YES")); //$NON-NLS-1$
		diagram.addChild(no = new Activity("NO")); //$NON-NLS-1$
		diagram.addChild(drive = new Activity("Drive to work")); //$NON-NLS-1$

		new Transition(sleep, alarm);
		new Transition(alarm, wakeup);
		new Transition(backToSleep, alarm2);
		new Transition(alarm2, turnOff);
		new Transition(wakeup, bathroom);
		new Transition(bathroom, clothes);
		new Transition(clothes, spare);
		new Transition(spare, yes);
		new Transition(spare, no);
		new Transition(yes, relaxation);
		new Transition(no, drive);
		new Transition(relaxation, drive);
		return diagram;
	}

	@Override
	protected InputStream getInitialContents() {
		ActivityDiagram diag = createWakeupModel();
		ByteArrayInputStream bais = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(diag);
			oos.flush();
			oos.close();
			baos.close();
			bais = new ByteArrayInputStream(baos.toByteArray());
			bais.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bais;
	}

	public boolean finish() {
		IFile newFile = createNewFile();
		if (newFile == null)
			return false; // ie.- creation was unsuccessful

		// Since the file resource was created fine, open it for editing
		// iff requested by the user
		try {
			IWorkbenchWindow dwindow = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = dwindow.getActivePage();
			if (page != null)
				IDE.openEditor(page, newFile, true);
		} catch (org.eclipse.ui.PartInitException e) {
			e.printStackTrace();
			return false;
		}
		exampleCount++;
		return true;
	}
}
