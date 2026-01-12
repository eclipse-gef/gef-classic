/*******************************************************************************
 * Copyright (c) 2000, 2026 IBM Corporation and others.
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
package org.eclipse.gef.test;

import org.eclipse.gef.test.swtbot.SWTBotTestSuite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * The main test suite for GEF.
 *
 * @author Eric Bordeau
 */
@Suite
@SelectClasses({
	PaletteCustomizerTest.class,
	ToolUtilitiesTest.class,
	DragEditPartsTrackerTest.class,
	CommandStackTest.class,
	RulerLayoutTests.class,
	GraphicalViewerTest.class,
	PaletteColorProviderTest.class,
	SWTBotTestSuite.class,
	DirectEditManagerTest.class,
	MoveHandleLocatorTests.class,
	PinnablePaletteStackEditPartTests.class
})
public class GEFTestSuite {
}
