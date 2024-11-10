/*******************************************************************************
 * Copyright (c) 2004, 2024 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gef.examples.text.model;

import java.util.List;

import junit.framework.TestCase;

/**
 * @since 3.1
 */
public class TestModelUtil extends TestCase {

	static List<ModelElement> result;

	@SuppressWarnings("static-method")
	public void testNestedBegin() {
		Container doc = new Block(Container.TYPE_ROOT);
		Container branch1 = new Block(0);
		TextRun start = new TextRun("12345"); //$NON-NLS-1$

		doc.add(branch1);
		branch1.add(start);

		TextRun end = new TextRun("ABCDE"); //$NON-NLS-1$
		doc.add(end);

		for (int i = 0; i < 3; i++) {
			result = ModelUtil.getModelSpan(start, 0, end, 5);
			compareList(result, new Object[] { branch1, end });

			result = ModelUtil.getModelSpan(start, 1, end, 5);
			compareList(result, new Object[] { end });

			result = ModelUtil.getModelSpan(start, 0, end, 3);
			compareList(result, new Object[] { branch1 });

			result = ModelUtil.getModelSpan(start, 1, end, 3);
			assertTrue(result.isEmpty());
			doc.add(new TextRun("bogus"), 0); //$NON-NLS-1$
		}

		TextRun middle = new TextRun("I'm in the middle"); //$NON-NLS-1$
		doc.add(middle, doc.getChildren().indexOf(end));

		result = ModelUtil.getModelSpan(start, 0, end, 5);
		compareList(result, new Object[] { branch1, middle, end });

		result = ModelUtil.getModelSpan(start, 1, end, 4);
		compareList(result, new Object[] { middle });

	}

	@SuppressWarnings("static-method")
	public void testNestedEnd() {
		Container doc = new Block(Container.TYPE_ROOT);
		Container branch1 = new Block(0);
		TextRun run123 = new TextRun("12345"); //$NON-NLS-1$

		branch1.add(run123);

		TextRun runABC = new TextRun("ABCDE"); //$NON-NLS-1$
		doc.add(runABC);
		doc.add(branch1);

		for (int i = 0; i < 3; i++) {
			result = ModelUtil.getModelSpan(runABC, 0, run123, 5);
			compareList(result, new Object[] { runABC, branch1 });

			result = ModelUtil.getModelSpan(runABC, 1, run123, 5);
			compareList(result, new Object[] { branch1 });

			result = ModelUtil.getModelSpan(runABC, 0, run123, 4);
			compareList(result, new Object[] { runABC });

			assertTrue(ModelUtil.getModelSpan(runABC, 1, run123, 4).isEmpty());
			doc.add(new TextRun("bogus"), 0); //$NON-NLS-1$
			doc.add(new TextRun("bogus"), doc.getChildren().size()); //$NON-NLS-1$
		}

		TextRun middle = new TextRun("I'm in the middle"); //$NON-NLS-1$
		doc.add(middle, doc.getChildren().indexOf(branch1));

		result = ModelUtil.getModelSpan(runABC, 0, run123, 5);
		compareList(result, new Object[] { runABC, middle, branch1 });

		result = ModelUtil.getModelSpan(runABC, 1, run123, 4);
		compareList(result, new Object[] { middle });
	}

	private static void compareList(List<ModelElement> result, Object[] array) {
		Object[] compare = result.toArray();
		assertEquals(array.length, compare.length);
		for (int i = 0; i < compare.length; i++) {
			assertEquals(compare[i], array[i]);
		}
	}

}
