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

package org.eclipse.gef.internal.mcp.tools.graphical.dto;

import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.internal.mcp.tools.Messages;
import org.eclipse.gef.internal.mcp.tools.graphical.AbstractEditPartTool;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * A small DTO object describing the important properties of an edit part. This
 * includes:
 * <ul>
 * <li>It's humand-readable name</li>
 * <li>It's selection state</li>
 * <li>It's position in the Draw2D coordinate system</li>
 * </ul>
 */
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "name", "selection", "x", "y", "width", "height" })
public class EditPartDTO {
	@JsonProperty
	private final String name;
	@JsonProperty
	private final String selection;
	@JsonProperty
	private Integer x;
	@JsonProperty
	private Integer y;
	@JsonProperty
	private Integer width;
	@JsonProperty
	private Integer height;

	public EditPartDTO(EditPart editPart) {
		this.name = AbstractEditPartTool.getDisplayName(editPart);
		this.selection = getSelection(editPart);
		if (editPart instanceof GraphicalEditPart graphicalEditPart) {
			Rectangle bounds = graphicalEditPart.getFigure().getBounds();
			x = bounds.x;
			y = bounds.y;
			width = bounds.width;
			height = bounds.height;
		}
	}

	private static String getSelection(EditPart editPart) {
		if (editPart.getSelected() == EditPart.SELECTED) {
			return Messages.SELECTION_Selected;
		}
		if (editPart.getSelected() == EditPart.SELECTED_NONE) {
			return Messages.SELECTION_Unselected;
		}
		if (editPart.getSelected() == EditPart.SELECTED_PRIMARY) {
			return Messages.SELECTION_PrimarySelected;
		}
		return Messages.SELECTION_Unknown;
	}
}
