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

package org.eclipse.gef.internal.mcp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.mcp.ITool;
import org.eclipse.draw2d.mcp.IToolProvider;

import org.eclipse.gef.internal.mcp.tools.command.RedoTool;
import org.eclipse.gef.internal.mcp.tools.command.UndoTool;
import org.eclipse.gef.internal.mcp.tools.graphical.CreateEditPartTool;
import org.eclipse.gef.internal.mcp.tools.graphical.DeleteEditPartTool;
import org.eclipse.gef.internal.mcp.tools.graphical.GetEditPartTool;
import org.eclipse.gef.internal.mcp.tools.graphical.GetSelectedEditPartTool;
import org.eclipse.gef.internal.mcp.tools.graphical.MoveEditPartTool;
import org.eclipse.gef.internal.mcp.tools.graphical.SelectEditPartTool;
import org.eclipse.gef.internal.mcp.tools.palette.GetPaletteItemTool;
import org.eclipse.gef.internal.mcp.tools.palette.GetSelectedPaletteItemTool;
import org.eclipse.gef.internal.mcp.tools.palette.SelectPaletteItemTool;

public class GEFToolProvider implements IToolProvider {
	@Override
	public List<ITool> getTools() {
		List<ITool> tools = new ArrayList<>();
		tools.add(new RedoTool());
		tools.add(new UndoTool());

		tools.add(new GetSelectedPaletteItemTool());
		tools.add(new GetPaletteItemTool());
		tools.add(new SelectPaletteItemTool());

		tools.add(new CreateEditPartTool());
		tools.add(new DeleteEditPartTool());
		tools.add(new GetEditPartTool());
		tools.add(new GetSelectedEditPartTool());
		tools.add(new MoveEditPartTool());
		tools.add(new SelectEditPartTool());
		return Collections.unmodifiableList(tools);
	}
}
