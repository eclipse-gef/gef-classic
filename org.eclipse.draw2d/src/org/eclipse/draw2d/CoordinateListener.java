/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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

package org.eclipse.draw2d;

/**
 * @since 3.1
 */
public interface CoordinateListener {

	/**
	 * Indicates that the coordinate system has changed in a way that affects the
	 * absolute locations of contained figures.
	 * 
	 * @param source the figure whose coordinate system changed
	 * @since 3.1
	 */
	void coordinateSystemChanged(IFigure source);

}
