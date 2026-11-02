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
import org.eclipse.gef.internal.mcp.tools.GetPaletteItemsTool;

public class GEFToolProvider implements IToolProvider {
	@Override
	public List<ITool> getTools() {
		List<ITool> tools = new ArrayList<>();
		tools.add(new GetPaletteItemsTool());
		return Collections.unmodifiableList(tools);
	}
}
