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

package org.eclipse.gef.internal.mcp.tools.palette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.draw2d.mcp.ErrorToolResponse;
import org.eclipse.draw2d.mcp.IToolRequest;
import org.eclipse.draw2d.mcp.IToolResponse;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.internal.mcp.tools.AbstractGraphicalViewerTool;
import org.eclipse.gef.internal.mcp.tools.Messages;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;

/**
 * Abstract base class for all tools that need to access the GEF palette viewer.
 * In addition to the errors thrown by the {@link AbstractGraphicalViewerTool},
 * this class will also throw an error if the graphical viewer doesn't have an
 * edit domain, or if the edit domain doesn't have a palette viewer.
 */
public abstract class AbstractPaletteViewerTool extends AbstractGraphicalViewerTool {

	/**
	 * Utility class for recursively finding all tool entries with a given property.
	 * For example a name.
	 *
	 * @param entry   The current tool
	 * @param matcher A matcher testing a tool entry for a specific property.
	 * @return An unmodifiable list of all tool entries with the desired property.
	 */
	protected List<ToolEntry> getTools(PaletteEntry entry, Predicate<ToolEntry> matcher) {
		if (entry instanceof ToolEntry tool && matcher.test(tool)) {
			return Collections.singletonList(tool);
		}
		if (entry instanceof PaletteContainer container) {
			List<ToolEntry> tools = new ArrayList<>();
			for (PaletteEntry child : container.getChildren()) {
				tools.addAll(getTools(child, matcher));
			}
			return Collections.unmodifiableList(tools);
		}
		return Collections.emptyList();
	}

	/**
	 * Utility method to recursively search for the tool entry with the given name
	 * in the given palette viewer.
	 *
	 * @param viewer   The palette viewer.
	 * @param toolName The tool name.
	 * @return The first tool with the given name or {@code null}, if no matching
	 *         tool is found.
	 */
	protected ToolEntry getTool(PaletteViewer viewer, String toolName) {
		List<ToolEntry> tools = getTools(viewer.getPaletteRoot(), tool -> Objects.equals(tool.getLabel(), toolName));
		if (tools.isEmpty()) {
			return null;
		}
		return tools.get(0);
	}

	/**
	 * @see AbstractGraphicalViewerTool#execute(GraphicalViewer, IToolRequest,
	 *      IProgressMonitor)
	 */
	@Override
	protected final IToolResponse execute(GraphicalViewer viewer, IToolRequest request, IProgressMonitor monitor) {
		EditDomain editDomain = viewer.getEditDomain();
		if (editDomain == null) {
			return new ErrorToolResponse(Messages.ERROR_NoEditDomain);
		}

		PaletteViewer paletteViewer = editDomain.getPaletteViewer();
		if (paletteViewer == null) {
			return new ErrorToolResponse(Messages.ERROR_NoPaletteViewer);
		}

		return execute(paletteViewer, request, monitor);
	}

	/**
	 * The callback function to perform the requested action.
	 *
	 * @param viewer  The palette viewer of the specified editor part.
	 * @param request The request sent by the language model.
	 * @param monitor A progress monitor
	 * @return The server response sent back to the language model.
	 */
	protected abstract IToolResponse execute(PaletteViewer viewer, IToolRequest request, IProgressMonitor monitor);
}
