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

package org.eclipse.draw2d.test;

import org.eclipse.swt.internal.DPIUtil;

import org.junit.platform.suite.api.AfterSuite;
import org.junit.platform.suite.api.BeforeSuite;

/**
 * Executes the Draw2D test suite with fractional scaling.
 *
 * Inspired by:
 * https://github.com/eclipse-platform/eclipse.platform.swt/pull/2967
 */
public class Draw2dTestSuite_NonDefaultAutoScale extends Draw2dTestSuite {

	@BeforeSuite
	public static void setUp() {
		DPIUtil.setDeviceZoom(125);
	}

	@AfterSuite
	public static void tearDown() {
		DPIUtil.setDeviceZoom(100);
	}
}
