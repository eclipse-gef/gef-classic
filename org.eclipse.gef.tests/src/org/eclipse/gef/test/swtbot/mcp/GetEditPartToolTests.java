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

public class GetEditPartToolTests extends AbstractMCPTests {

	@Override
	protected String getToolId() {
		return "org.eclipse.gef.graphical.parts"; //$NON-NLS-1$
	}

	@Test
	public void test_getEditParts() throws Exception {
		JsonNode response = client.callTool(getToolId(), """
				{
					"editorName": "GEF Flow Example"
				}
				"""); //$NON-NLS-1$
		JsonNode result = response.get("result"); //$NON-NLS-1$

		assertNoError(result);

		JsonNode content = result.get("content"); //$NON-NLS-1$
		assertEquals(21, content.size());

		JsonNode structuredContent = result.get("structuredContent"); //$NON-NLS-1$
		assertEquals(21, structuredContent.size());

		assertText("ActivityDiagram(Activity)", content.get(0)); //$NON-NLS-1$
		assertEquals("""
				{
				  "name" : "ActivityDiagram(Activity)",
				  "selection" : "Unselected",
				  "x" : 0,
				  "y" : 0,
				  "width" : 619,
				  "height" : 762
				}""", structuredContent.get(0).toPrettyString()); //$NON-NLS-1$

		assertText("ParallelActivity(Morning relaxation ritual)", content.get(10)); //$NON-NLS-1$
		assertEquals("""
				{
				  "name" : "ParallelActivity(Morning relaxation ritual)",
				  "selection" : "Unselected",
				  "x" : 15,
				  "y" : 648,
				  "width" : 247,
				  "height" : 77
				}""", structuredContent.get(10).toPrettyString()); //$NON-NLS-1$
	}

}
