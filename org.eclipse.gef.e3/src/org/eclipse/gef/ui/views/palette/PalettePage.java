/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
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
package org.eclipse.gef.ui.views.palette;

import org.eclipse.ui.part.IPageBookViewPage;

/**
 * An interface representing a page to be used in the PaletteView.
 * PalettePage.class is passed as the argument to the getAdapter(Object) method
 * of a GEF-based editor (or view) to retrieve the page to be displayed in the
 * PaletteView.
 *
 * @author Pratik Shah
 * @since 3.0
 */
public interface PalettePage extends IPageBookViewPage {
}
