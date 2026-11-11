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

public class DeleteEditPartToolTests extends AbstractMCPTests {

	@Override
	protected String getToolId() {
		return "org.eclipse.gef.graphical.parts.delete"; //$NON-NLS-1$
	}

	@Test
	public void test_deleteEditPart() throws Exception {
		JsonNode response = client.callTool(getToolId(), """
				{
					"editorName": "GEF Flow Example",
					"editPartNames": ["SequentialActivity(Wake up)", "Activity(Get out of bed)"]
				}
				"""); //$NON-NLS-1$
		JsonNode result = response.get("result"); //$NON-NLS-1$

		assertNoError(result);

		JsonNode content = result.get("content"); //$NON-NLS-1$
		assertEquals(2, content.size());

		JsonNode structuredContent = result.get("structuredContent"); //$NON-NLS-1$
		assertEquals(2, structuredContent.size());

		assertText("SequentialActivity(Wake up)", content.get(0)); //$NON-NLS-1$
		assertEquals("""
				{
				  "name" : "SequentialActivity(Wake up)",
				  "selection" : "Unselected",
				  "x" : 133,
				  "y" : 95,
				  "width" : 150,
				  "height" : 241
				}""", structuredContent.get(0).toPrettyString()); //$NON-NLS-1$

		assertText("Activity(Get out of bed)", content.get(1)); //$NON-NLS-1$
		assertEquals("""
				{
				  "name" : "Activity(Get out of bed)",
				  "selection" : "Unselected",
				  "x" : 141,
				  "y" : 284,
				  "width" : 109,
				  "height" : 19
				}""", structuredContent.get(1).toPrettyString()); //$NON-NLS-1$
	}

	@Test
	public void test_deleteEditPart_cantDelete() throws Exception {
		JsonNode response = client.callTool(getToolId(), """
				{
					"editorName": "GEF Flow Example",
					"editPartNames": ["ActivityDiagram(Activity)"]
				}
				"""); //$NON-NLS-1$
		JsonNode result = response.get("result"); //$NON-NLS-1$

		assertError(result);

		JsonNode content = result.get("content"); //$NON-NLS-1$
		assertEquals(1, content.size());
		assertText("One or more commands are not executable.", content.get(0)); //$NON-NLS-1$
	}
}
