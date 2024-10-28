/*******************************************************************************
 * Copyright (c) 2024 Patrick Ziegler and others.
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

package org.eclipse.draw2d.test.swtbot;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.test.utils.Snippet;

import gef.bugs.BugWithLocalCoordinateSystem;
import org.junit.jupiter.api.Test;

public class FigureTest extends AbstractSWTBotTests {

	@Test
	@Snippet(type = BugWithLocalCoordinateSystem.class)
	public void testMouseEventTargetWithLocalCoordinates() {
		IFigure label = root.findFigureAt(20, 125);
		IFigure rectangle = root.findFigureAt(20, 25);

		assertNotNull(label);
		assertNotNull(rectangle);
		assertNull(rectangle.getBorder());

		bot.click(20, 125);
		assertNull(rectangle.getBorder());
	}
}
