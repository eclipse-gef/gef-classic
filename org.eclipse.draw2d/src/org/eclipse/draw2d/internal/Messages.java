/*******************************************************************************
 * Copyright (c) 2024, 2025 Patrick Ziegler and others.
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

package org.eclipse.draw2d.internal;

import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = Messages.class.getName(); // $NON-NLS-1$
	private static final ResourceBundle BUNDLE;
	static {
		// initialize resource bundle
		BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	}

	private Messages() {
		// this class is not intended to be instantiated
	}

	public static final String LayoutManager_InvalidConstraint = BUNDLE.getString("LayoutManager_InvalidConstraint"); //$NON-NLS-1$
	public static final String AbstractZoomManager_PercentFormat = BUNDLE.getString("AbstractZoomManager_PercentFormat"); //$NON-NLS-1$
}
