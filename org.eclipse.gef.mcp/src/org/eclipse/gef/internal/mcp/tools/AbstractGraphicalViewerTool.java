/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
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

package org.eclipse.gef.internal.mcp.tools;

import org.eclipse.swt.widgets.Display;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import org.eclipse.draw2d.mcp.AbstractTool;
import org.eclipse.draw2d.mcp.ErrorToolResponse;
import org.eclipse.draw2d.mcp.ITool;
import org.eclipse.draw2d.mcp.IToolRequest;
import org.eclipse.draw2d.mcp.IToolResponse;

import org.eclipse.gef.GraphicalViewer;

/**
 * Abstract base class for all tools that need to access the GEF graphical
 * viewer. This tool will throw an error if no editor isn't found or if the
 * editor doesn't expose its viewer.
 */
public abstract class AbstractGraphicalViewerTool extends AbstractTool {
	/**
	 * The property describing the name of an editor part.
	 */
	protected static final String PROP_EDITOR_NAME = "editorName"; //$NON-NLS-1$

	/**
	 * Utility method to find the editor part with the given name. Returns
	 * {@code null} if no matching editor part is found. If multiple editor parts
	 * with the same name exist, the first part is returned.
	 *
	 * @param name The name of the editor part.
	 * @return The editor part.
	 */
	private static IEditorPart getEditorPart(String name) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		for (IEditorReference editorReference : page.getEditorReferences()) {
			if (name.equals(editorReference.getPartName())) {
				return editorReference.getEditor(true);
			}
		}
		return null;
	}

	/**
	 * Utility class to access the workbench {@link Display}. This instance is
	 * necessary to synchronize access to the graphical viewer.
	 *
	 * @return The workbench display.
	 */
	protected static Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}

	/**
	 * @see ITool#execute(IToolRequest, IProgressMonitor)
	 */
	@Override
	public final IToolResponse execute(IToolRequest request, IProgressMonitor monitor) {
		String name = (String) request.getArguments().get(PROP_EDITOR_NAME);
		if (name == null) {
			return new ErrorToolResponse(Messages.ERROR_EditorNotSpecified);
		}

		IEditorPart editorPart = getDisplay().syncCall(() -> getEditorPart(name));
		if (editorPart == null) {
			return new ErrorToolResponse(NLS.bind(Messages.ERROR_EditorNotFound, name));
		}

		GraphicalViewer graphicalViewer = editorPart.getAdapter(GraphicalViewer.class);
		if (graphicalViewer == null) {
			return new ErrorToolResponse(NLS.bind(Messages.ERROR_NoGraphicalViewer, name));
		}

		return execute(graphicalViewer, request, monitor);
	}

	/**
	 * The callback function to perform the requested action.
	 *
	 * @param viewer  The graphical viewer of the specified editor part.
	 * @param request The request sent by the language model.
	 * @param monitor A progress monitor
	 * @return The server response sent back to the language model.
	 */
	protected abstract IToolResponse execute(GraphicalViewer viewer, IToolRequest request, IProgressMonitor monitor);
}
