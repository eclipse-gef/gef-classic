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

public class GetSelectedEditPartToolTests extends AbstractMCPTests {

	@Override
	protected String getToolId() {
		return "org.eclipse.gef.graphical.parts.selected"; //$NON-NLS-1$
	}

	@Test
	public void test_getSelectedEditParts() throws Exception {
		bot.gefEditor("GEF Flow Example").select("Take shower"); //$NON-NLS-1$//$NON-NLS-2$

		JsonNode response = client.callTool(getToolId(), """
				{
					"editorName": "GEF Flow Example"
				}
				"""); //$NON-NLS-1$
		JsonNode result = response.get("result"); //$NON-NLS-1$

		assertNoError(result);

		JsonNode content = result.get("content"); //$NON-NLS-1$
		assertEquals(1, content.size());

		JsonNode structuredContent = result.get("structuredContent"); //$NON-NLS-1$
		assertEquals(1, structuredContent.size());

		assertText("Activity(Take shower)", content.get(0)); //$NON-NLS-1$
		assertEquals("""
				{
				  "name" : "Activity(Take shower)",
				  "selection" : "Primary Selected",
				  "x" : 147,
				  "y" : 424,
				  "width" : 96,
				  "height" : 19
				}""", structuredContent.get(0).toPrettyString()); //$NON-NLS-1$
	}

	@Test
	public void test_getSelectedEditParts_noSelection() throws Exception {
		JsonNode response = client.callTool(getToolId(), """
				{
					"editorName": "GEF Flow Example"
				}
				"""); //$NON-NLS-1$
		JsonNode result = response.get("result"); //$NON-NLS-1$

		assertNoError(result);

		JsonNode content = result.get("content"); //$NON-NLS-1$
		assertEquals(0, content.size());

		JsonNode structuredContent = result.get("structuredContent"); //$NON-NLS-1$
		assertEquals(0, structuredContent.size());
	}
}
