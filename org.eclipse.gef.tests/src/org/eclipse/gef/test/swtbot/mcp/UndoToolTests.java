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

public class UndoToolTests extends AbstractMCPTests {
	@Override
	protected String getToolId() {
		return "org.eclipse.gef.command.undo"; //$NON-NLS-1$
	}

	@Test
	public void test_undoCommand() throws Exception {
		bot.gefEditor("GEF Flow Example").select("Wake up"); //$NON-NLS-1$ //$NON-NLS-2$
		bot.menu("Edit").menu("Delete").click(); //$NON-NLS-1$ //$NON-NLS-2$

		JsonNode response = client.callTool(getToolId(), """
				{
					"editorName": "GEF Flow Example"
				}
				"""); //$NON-NLS-1$
		JsonNode result = response.get("result"); //$NON-NLS-1$

		assertNoError(result);

		JsonNode content = result.get("content"); //$NON-NLS-1$
		assertEquals(1, content.size());
		assertText("Delete", content.get(0)); //$NON-NLS-1$
	}

	@Test
	public void test_undoCommand_noCommand() throws Exception {
		JsonNode response = client.callTool(getToolId(), """
				{
					"editorName": "GEF Flow Example"
				}
				"""); //$NON-NLS-1$
		JsonNode result = response.get("result"); //$NON-NLS-1$

		assertError(result);

		JsonNode content = result.get("content"); //$NON-NLS-1$
		assertEquals(1, content.size());
		assertText("Nothing to undo.", content.get(0)); //$NON-NLS-1$
	}
}
