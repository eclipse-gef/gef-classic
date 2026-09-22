/*******************************************************************************
 * Copyright (c) 2026 IBM Corporation and others.
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

package org.eclipse.zest.dot.ui.internal;

/**
 * A non-exclusive list of supported DOT attributes.
 *
 * @since 3.2
 */
public enum DOTAttribute {
	LABEL("label"); //$NON-NLS-1$

	private final String key;

	private DOTAttribute(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
