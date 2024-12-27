/*******************************************************************************
 * Copyright (c) 2000, 2024 IBM Corporation and others.
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
package org.eclipse.gef.ui.palette.customize;

import java.util.List;

import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;

/**
 * Abstract factory for <code>PaletteContainer</code>s
 *
 * <p>
 * This class does not create <code>PaletteContainer</code>s within other
 * <code>PaletteContainer</code>s. The necessary methods may be overridden
 * should such functionality be desired.
 * </p>
 *
 * @author Pratik Shah
 */
public abstract class PaletteContainerFactory extends PaletteEntryFactory {

	/**
	 * @see PaletteEntryFactory#determineContainerForNewEntry(PaletteEntry)
	 */
	@Override
	protected PaletteContainer determineContainerForNewEntry(PaletteEntry selected) {
		if (selected instanceof PaletteRoot) {
			return (PaletteContainer) selected;
		}
		PaletteContainer current = selected.getParent();
		while (!(current instanceof PaletteRoot)) {
			current = current.getParent();
		}
		return current;
	}

	/**
	 * @see PaletteEntryFactory#determineIndexForNewEntry(PaletteContainer,
	 *      PaletteEntry)
	 */
	@Override
	protected int determineIndexForNewEntry(PaletteContainer parent, PaletteEntry selected) {
		if (parent == selected) {
			return 0;
		}

		List<? extends PaletteEntry> children = parent.getChildren();
		PaletteEntry current = selected;
		while (!children.contains(current)) {
			current = current.getParent();
		}
		return children.indexOf(current) + 1;
	}

	/**
	 * You can always create a new container. So, this method always returns true.
	 *
	 * @see org.eclipse.gef.ui.palette.customize.PaletteEntryFactory#canCreate(PaletteEntry)
	 */
	@Override
	public boolean canCreate(PaletteEntry selected) {
		return true;
	}

}
