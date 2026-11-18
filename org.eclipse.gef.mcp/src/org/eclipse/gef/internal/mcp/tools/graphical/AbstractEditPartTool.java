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

package org.eclipse.gef.internal.mcp.tools.graphical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.eclipse.jface.viewers.ILabelProvider;

import org.eclipse.draw2d.mcp.AbstractToolResponse;
import org.eclipse.draw2d.mcp.ErrorToolResponse;
import org.eclipse.draw2d.mcp.IContent;
import org.eclipse.draw2d.mcp.IToolRequest;
import org.eclipse.draw2d.mcp.IToolResponse;
import org.eclipse.draw2d.mcp.TextContent;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.internal.InternalGEFPlugin;
import org.eclipse.gef.internal.mcp.tools.AbstractGraphicalViewerTool;
import org.eclipse.gef.internal.mcp.tools.Messages;
import org.eclipse.gef.internal.mcp.tools.graphical.dto.EditPartDTO;

/**
 * Abstract base class for all tools that interact with GEF edit parts
 */
public abstract class AbstractEditPartTool extends AbstractGraphicalViewerTool {
	/**
	 * The property describing the human-readable name of an edit-part.
	 */
	private static final String PROP_EDITPART_NAMES = "editPartNames"; //$NON-NLS-1$

	/**
	 * Utility method that returns the human-readable name of the given edit-part.
	 * The name is usually determined via a label provider, which is set as property
	 * for the graphical viewer. If no label provider is found, the
	 * {@code toString()} method of the model is used. If no model is set, the
	 * {@code toString()} method of the edit part is used.
	 *
	 * @param editPart The edit part.
	 * @return The human-readable name of the edit-part.
	 */
	public static String getDisplayName(EditPart editPart) {
		EditPartViewer viewer = editPart.getViewer();

		// Might be null if e.g. the edit part was deleted
		if (viewer != null) {
			// Try to find a human-readable name for the edit-part
			ILabelProvider labelProvider = (ILabelProvider) viewer
					.getProperty(InternalGEFPlugin.LABEL_PROVIDER_PROPERTY);
			if (labelProvider != null) {
				String displayName = labelProvider.getText(editPart);
				if (displayName != null) {
					return displayName;
				}
			}
		}

		// Use toString() on model as fallback
		Object model = editPart.getModel();
		if (model != null) {
			return model.toString();
		}

		// Use toString() on edit-part if no model is set
		return editPart.toString();
	}

	/**
	 * Utility method that recursively traverses all edit parts in pre-order.
	 *
	 * @param editPart The currently visited edit-part.
	 * @param consumer Callback function for the current edit-part.
	 */
	private static void traverse(EditPart editPart, Consumer<EditPart> consumer) {
		consumer.accept(editPart);

		for (EditPart child : editPart.getChildren()) {
			traverse(child, consumer);
		}
	}

	protected IToolResponse execute(EditPartViewer viewer, List<? extends EditPart> editParts, Request request) {
		CompoundCommand compoundCommand = new CompoundCommand();
		for (EditPart editPart : editParts) {
			compoundCommand.add(getCommand(editPart, request));
		}

		Command command = compoundCommand.unwrap();
		if (!command.canExecute()) {
			return new ErrorToolResponse(Messages.ERROR_CommandNotExecutable);
		}

		getDisplay().syncExec(() -> executeCommand(viewer, command));

		return new EditPartToolResponse(editParts);
	}

	/**
	 * Convenience method for executing the given command in the UI thread. May be
	 * overridden.
	 *
	 * @param viewer  The active edit-part viewer.
	 * @param command The command to execute.
	 */
	@SuppressWarnings("static-method")
	protected void executeCommand(EditPartViewer viewer, Command command) {
		viewer.getEditDomain().getCommandStack().execute(command);
	}

	/**
	 * Convenience method for getting the command from the given edit-part using the
	 * given request. Calls {@link EditPart#getCommand(Request)} by default. May be
	 * overridden.
	 *
	 * @param editPart The current edit-part.
	 * @param request  The simulated user request.
	 * @return The command to execute.
	 */
	@SuppressWarnings("static-method")
	protected Command getCommand(EditPart editPart, Request request) {
		return editPart.getCommand(request);
	}

	protected static List<EditPart> getEditParts(EditPartViewer viewer, IToolRequest request) {
		@SuppressWarnings("unchecked")
		List<String> editPartNames = (List<String>) request.getArguments().get(PROP_EDITPART_NAMES); // $NON-NLS-1$
		if (editPartNames == null) {
			return Collections.emptyList();
		}
		List<EditPart> editParts = new ArrayList<>();
		// Preserve order of edit parts
		for (String editPartName : editPartNames) {
			editParts.addAll(getEditParts(viewer, displayName -> Objects.equals(editPartName, displayName)));
		}
		return Collections.unmodifiableList(editParts);
	}

	/**
	 * Convenience method for getting all edit-parts whose name match the given
	 * predicate.
	 *
	 * @param viewer  The active edit-part viewer.
	 * @param checker The condition by which the display names are filtered.
	 * @return A list of all edit-parts matching the expected pattern.
	 */
	protected static List<EditPart> getEditParts(EditPartViewer viewer, Predicate<String> checker) {
		List<EditPart> editParts = new ArrayList<>();
		traverse(viewer.getContents(), editPart -> {
			if (checker.test(getDisplayName(editPart))) {
				editParts.add(editPart);
			}
		});
		return Collections.unmodifiableList(editParts);
	}

	/**
	 * Default response for one or more edit parts.
	 */
	protected static class EditPartToolResponse extends AbstractToolResponse {
		private final List<IContent> content = new ArrayList<>();
		private final List<EditPartDTO> structuredContent = new ArrayList<>();

		public EditPartToolResponse(List<? extends EditPart> editParts) {
			for (EditPart editPart : editParts) {
				content.add(new TextContent(getDisplayName(editPart)));
				structuredContent.add(new EditPartDTO(editPart));
			}
		}

		@Override
		public List<IContent> getContent() {
			return Collections.unmodifiableList(content);
		}

		@Override
		public Object getStructuredContent() {
			return Collections.unmodifiableList(structuredContent);
		}
	}
}
