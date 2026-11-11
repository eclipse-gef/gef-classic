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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

public class GetPaletteItemToolTests extends AbstractMCPTests {

	@Override
	protected String getToolId() {
		return "org.eclipse.gef.palette.items"; //$NON-NLS-1$
	}

	@Test
	public void test_getTools() throws Exception {
		JsonNode response = client.callTool(getToolId(), """
				{
					"editorName": "GEF Flow Example"
				}
				"""); //$NON-NLS-1$
		JsonNode result = response.get("result"); //$NON-NLS-1$

		assertNoError(result);
		JsonNode content = result.get("content"); //$NON-NLS-1$
		assertEquals(6, content.size());
		assertText("Select", content.get(0)); //$NON-NLS-1$
		assertText("Marquee", content.get(1)); //$NON-NLS-1$
		assertText("Connection Creation", content.get(2)); //$NON-NLS-1$
		assertText("Activity", content.get(3)); //$NON-NLS-1$
		assertText("Sequential Activity", content.get(4)); //$NON-NLS-1$
		assertText("Parallel Activity", content.get(5)); //$NON-NLS-1$
	}
}
