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

package org.eclipse.gef.test.swtbot.mcp;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test suite containing all MCP server tests.
 */
@Suite
@SelectClasses({ //
		RedoToolTests.class, //
		UndoToolTests.class, //
		CreateEditPartToolTests.class, //
		DeleteEditPartToolTests.class, //
		GetEditPartToolTests.class, //
		GetPaletteItemToolTests.class, //
		GetSelectedEditPartToolTests.class, //
		GetSelectedPaletteItemToolTests.class, //
		MoveEditPartToolTests.class, //
		SelectEditPartToolTests.class, //
		SelectPaletteItemToolTests.class })
public class MCPServerTestSuite {

}
