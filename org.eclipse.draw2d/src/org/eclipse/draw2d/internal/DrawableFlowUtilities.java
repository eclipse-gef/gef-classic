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

package org.eclipse.draw2d.internal;

import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.text.FlowUtilities;

/**
 * Provides miscellaneous flow operations calculated with the zoom context of
 * the {@code TextUtilities}.
 */
public class DrawableFlowUtilities extends FlowUtilities {
	private final TextUtilities textUtilities;

	public DrawableFlowUtilities(TextUtilities textUtilities) {
		this.textUtilities = textUtilities;
	}

	@Override
	protected TextUtilities getTextUtilities() {
		return textUtilities;
	}
}
