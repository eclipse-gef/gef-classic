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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.mcp.AbstractToolResponse;
import org.eclipse.draw2d.mcp.ErrorToolResponse;
import org.eclipse.draw2d.mcp.IContent;
import org.eclipse.draw2d.mcp.ITool;
import org.eclipse.draw2d.mcp.IToolRequest;
import org.eclipse.draw2d.mcp.IToolResponse;
import org.eclipse.draw2d.mcp.TextContent;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class GetPaletteItemsTool implements ITool {

	@Override
	public String getName() {
		return "gef.palette.items";
	}

	@Override
	public String getDescription() {
		return "Returns a list of all GEF palette items in either the specified editors or the currently active editor, if none is given. If multiple editors with the same name are open, the first editor is selected.";
	}

	@Override
	public String getSchema() {
		return """
				{
					"type": "object",
					"properties": {
						"editorName": {
							"type": "string",
							"description": "Name of the GEF editor"
						}
					}
				}
				""";
	}

	@Override
	public IToolResponse execute(IToolRequest request, IProgressMonitor monitor) {
		String name = (String) request.getArguments().get("editorName");

		IEditorPart editorPart = PlatformUI.getWorkbench().getDisplay().syncCall(() -> getEditorPart(name));
		if (editorPart == null) {
			return new ErrorToolResponse("Unable to find editor with name \"%s\"".formatted(name));
		}

		GraphicalViewer graphicalViewer = editorPart.getAdapter(GraphicalViewer.class);
		if (graphicalViewer == null) {
			return new ErrorToolResponse("Unable to extract graphical viewer from editor with name \"%s\"".formatted(name));
		}

		EditDomain editDomain = graphicalViewer.getEditDomain();
		if (editDomain == null) {
			return new ErrorToolResponse("Unable to extract edit domain from editor with name \"%s\"".formatted(name));
		}

		PaletteViewer paletteViewer = editDomain.getPaletteViewer();
		if (paletteViewer == null) {
			return new ErrorToolResponse("Unable to extract palette viewer from editor with name \"%s\"".formatted(name));
		}

		List<IContent> elements = findElements(paletteViewer.getPaletteRoot());
		return new AbstractToolResponse() {
			@Override
			public List<IContent> getContent() {
				return elements;
			}
		};
	}

	private List<IContent> findElements(PaletteEntry entry) {
		if (entry instanceof ToolEntry) {
			return Collections.singletonList(new TextContent(entry.getLabel()));
		}
		if (entry instanceof PaletteContainer container) {
			List<IContent> content = new ArrayList<>();
			for (PaletteEntry child : container.getChildren()) {
				content.addAll(findElements(child));
			}
			return Collections.unmodifiableList(content);
		}
		return Collections.emptyList();
	}

	private IEditorPart getEditorPart(String name) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (name == null) {
			return page.getActiveEditor();
		}

		for (IEditorReference editorReference : page.getEditorReferences()) {
			if (name.equals(editorReference.getPartName())) {
				return editorReference.getEditor(true);
			}
		}
		return null;
	}

}
