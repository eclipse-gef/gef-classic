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

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

public class MoveEditPartToolTests extends AbstractMCPTests {

	@Override
	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();

		// Populate the empty editor
		SWTBotGefEditor editor = bot.gefEditor(getFileName());
		editor.activateTool("Flow Container"); //$NON-NLS-1$
		editor.click(5, 5);
		waitEventLoop(50);
	}

	@Override
	protected String getToolId() {
		return "org.eclipse.gef.graphical.parts.move"; //$NON-NLS-1$
	}

	@Override
	protected String getWizardId() {
		return "org.eclipse.gef.examples.logic.wizard.new.file"; //$NON-NLS-1$
	}

	@Override
	protected String getFileName() {
		return "emptyModel1.logic"; //$NON-NLS-1$
	}

	@Test
	public void test_moveEditPart() throws Exception {
		JsonNode response = client.callTool(getToolId(), """
				{
					"editorName": "emptyModel1.logic",
					"editPartNames": ["Flow Container"],
					"moveX": 50,
					"moveY": 100
				}
				"""); //$NON-NLS-1$
		JsonNode result = response.get("result"); //$NON-NLS-1$

		assertNoError(result);

		JsonNode content = result.get("content"); //$NON-NLS-1$
		assertEquals(1, content.size());

		JsonNode structuredContent = result.get("structuredContent"); //$NON-NLS-1$
		assertEquals(1, structuredContent.size());

		assertText("Flow Container", content.get(0)); //$NON-NLS-1$
		assertEquals("""
				{
				  "name" : "Flow Container",
				  "selection" : "Primary Selected",
				  "x" : 55,
				  "y" : 105,
				  "width" : 200,
				  "height" : 200
				}""", structuredContent.get(0).toPrettyString()); //$NON-NLS-1$

	}
}
