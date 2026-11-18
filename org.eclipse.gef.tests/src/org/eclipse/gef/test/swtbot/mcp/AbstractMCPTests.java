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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.gef.test.swtbot.AbstractSWTBotEditorTests;
import org.eclipse.gef.test.utils.HttpMCPClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

public abstract class AbstractMCPTests extends AbstractSWTBotEditorTests {

	protected static void assertError(JsonNode node) {
		assertTrue(node.get("isError").asBoolean()); //$NON-NLS-1$
	}

	protected static void assertNoError(JsonNode node) {
		assertFalse(node.get("isError").asBoolean()); //$NON-NLS-1$
	}

	protected static void assertText(String value, JsonNode node) {
		assertEquals("text", node.get("type").asString()); //$NON-NLS-1$//$NON-NLS-2$
		assertEquals(value, node.get("text").asString()); //$NON-NLS-1$
	}

	protected HttpMCPClient client;

	@Override
	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();

		client = new HttpMCPClient("http://localhost:8080/mcp"); //$NON-NLS-1$
		client.initialize();
	}

	@Override
	protected String getFileName() {
		return "flowDiagram.flow"; //$NON-NLS-1$
	}

	@Override
	protected String getWizardId() {
		return "org.eclipse.gef.examples.flow.wizard.new.file"; //$NON-NLS-1$
	}

	protected abstract String getToolId();

	@Test
	public void test_unknownEditor() throws Exception {
		JsonNode response = client.callTool(getToolId(), """
				{
					"parentEditPartName": "unknown",
					"editorName": "invalid.flow",
					"editPartNames": [],
					"toolName": "unknown",
					"moveX": 0,
					"moveY": 0
				}
				"""); //$NON-NLS-1$
		JsonNode result = response.get("result"); //$NON-NLS-1$

		assertError(result);
		JsonNode content = result.get("content"); //$NON-NLS-1$
		assertEquals(1, content.size());
		assertText("Unable to find editor with name \"invalid.flow\".", content.get(0)); //$NON-NLS-1$
	}
}
