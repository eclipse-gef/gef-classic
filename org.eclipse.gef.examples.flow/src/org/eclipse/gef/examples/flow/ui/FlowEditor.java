/*******************************************************************************
 * Copyright (c) 2003, 2025 IBM Corporation and others.
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
package org.eclipse.gef.examples.flow.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.eclipse.swt.SWT;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.commands.CommandStackEvent;
import org.eclipse.gef.commands.CommandStackEventListener;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;

import org.eclipse.gef.examples.flow.FlowEditorPaletteFactory;
import org.eclipse.gef.examples.flow.actions.FlowContextMenuProvider;
import org.eclipse.gef.examples.flow.model.ActivityDiagram;
import org.eclipse.gef.examples.flow.parts.ActivityPartFactory;

/**
 *
 * @author hudsonr Created on Jun 27, 2003
 */
public class FlowEditor extends GraphicalEditorWithPalette {

	ActivityDiagram diagram;
	private PaletteRoot root;
	private KeyHandler sharedKeyHandler;

	public FlowEditor() {
		DefaultEditDomain defaultEditDomain = new DefaultEditDomain(this);
		setEditDomain(defaultEditDomain);
	}

	/**
	 * @see CommandStackEventListener#stackChanged(CommandStackEvent event)
	 */
	@Override
	public void stackChanged(CommandStackEvent event) {
		if (event.isPostChangeEvent()) {
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
		super.stackChanged(event);
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#createActions()
	 */
	@Override
	protected void createActions() {
		super.createActions();
		ActionRegistry registry = getActionRegistry();
		IAction action;

		action = new DirectEditAction((IWorkbenchPart) this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
	}

	/**
	 * Creates an appropriate output stream and writes the activity diagram out to
	 * this stream.
	 *
	 * @param os the base output stream
	 * @throws IOException
	 */
	protected void createOutputStream(OutputStream os) throws IOException {
		try (ObjectOutputStream out = new ObjectOutputStream(os)) {
			out.writeObject(diagram);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		getGraphicalViewer().setRootEditPart(new ScalableRootEditPart());
		getGraphicalViewer().setEditPartFactory(new ActivityPartFactory());
		getGraphicalViewer()
				.setKeyHandler(new GraphicalViewerKeyHandler(getGraphicalViewer()).setParent(getCommonKeyHandler()));

		ContextMenuProvider provider = new FlowContextMenuProvider(getGraphicalViewer(), getActionRegistry());
		getGraphicalViewer().setContextMenu(provider);
		getSite().registerContextMenu("org.eclipse.gef.examples.flow.editor.contextmenu", //$NON-NLS-1$
				provider, getGraphicalViewer());

	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	@Override
	protected void initializeGraphicalViewer() {
		getGraphicalViewer().setContents(diagram);
		getGraphicalViewer().addDropTargetListener(
				(TransferDropTargetListener) new TemplateTransferDropTargetListener(getGraphicalViewer()));

	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#initializePaletteViewer()
	 */
	@Override
	protected void initializePaletteViewer() {
		super.initializePaletteViewer();
		getPaletteViewer().addDragSourceListener(
				(TransferDragSourceListener) new TemplateTransferDragSourceListener(getPaletteViewer()));
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			createOutputStream(out);
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			file.setContents(new ByteArrayInputStream(out.toByteArray()), true, false, monitor);
			getCommandStack().markSaveLocation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		SaveAsDialog dialog = new SaveAsDialog(getSite().getWorkbenchWindow().getShell());
		dialog.setOriginalFile(((IFileEditorInput) getEditorInput()).getFile());
		dialog.open();
		IPath path = dialog.getResult();

		if (path == null) {
			return;
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IFile file = workspace.getRoot().getFile(path);

		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			@Override
			public void execute(final IProgressMonitor monitor) throws CoreException {
				try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
					createOutputStream(out);
					file.create(new ByteArrayInputStream(out.toByteArray()), true, monitor);
				} catch (Exception e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
		};

		try {
			new ProgressMonitorDialog(getSite().getWorkbenchWindow().getShell()).run(false, true, op);
			setInput(new FileEditorInput(file));
			getCommandStack().markSaveLocation();
		} catch (Exception e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

	protected KeyHandler getCommonKeyHandler() {
		if (sharedKeyHandler == null) {
			sharedKeyHandler = new KeyHandler();
			sharedKeyHandler.put(KeyStroke.getPressed(SWT.DEL, 127, 0),
					getActionRegistry().getAction(ActionFactory.DELETE.getId()));
			sharedKeyHandler.put(KeyStroke.getPressed(SWT.F2, 0),
					getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT));
		}
		return sharedKeyHandler;
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#getPaletteRoot()
	 */
	@Override
	protected PaletteRoot getPaletteRoot() {
		if (root == null) {
			root = FlowEditorPaletteFactory.createPalette();
		}
		return root;
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);

		IFile file = ((IFileEditorInput) input).getFile();
		try (InputStream is = file.getContents(false); // - inserted for having a line break
				ObjectInputStream ois = new ObjectInputStream(is)) {
			diagram = (ActivityDiagram) ois.readObject();
		} catch (Exception e) {
			// This is just an example. All exceptions caught here.
			e.printStackTrace();
			diagram = new ActivityDiagram();
		}
	}

}
