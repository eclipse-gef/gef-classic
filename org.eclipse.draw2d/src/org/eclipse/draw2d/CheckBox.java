/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
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

import org.eclipse.swt.graphics.Image;

import org.eclipse.draw2d.internal.FileImageFileNameProvider;

/**
 * A Checkbox is a toggle figure which toggles between the checked and unchecked
 * figures to simulate a check box. A check box contains a text label to
 * represent it.
 */
public final class CheckBox extends Toggle {
	private Label label = null;

	static final Image UNCHECKED = FileImageFileNameProvider.createImage(CheckBox.class, "images/checkboxenabledoff.svg"); //$NON-NLS-1$
	static final Image CHECKED = FileImageFileNameProvider.createImage(CheckBox.class, "images/checkboxenabledon.svg"); //$NON-NLS-1$

	/**
	 * Constructs a CheckBox with no text.
	 *
	 * @since 2.0
	 */
	public CheckBox() {
		this(""); //$NON-NLS-1$
	}

	/**
	 * Constructs a CheckBox with the passed text in its label.
	 *
	 * @param text The label text
	 * @since 2.0
	 */
	public CheckBox(String text) {
		label = new Label(text, UNCHECKED);
		setContents(label);
	}

	/**
	 * Adjusts CheckBox's icon depending on selection status.
	 *
	 * @since 2.0
	 */
	protected void handleSelectionChanged() {
		if (isSelected()) {
			label.setIcon(CHECKED);
		} else {
			label.setIcon(UNCHECKED);
		}
	}

	/**
	 * Initializes this Clickable by setting a default model and adding a clickable
	 * event handler for that model. Also adds a ChangeListener to update icon when
	 * selection status changes.
	 *
	 * @since 2.0
	 */
	@Override
	protected void init() {
		super.init();
		addChangeListener(new ChangeListener() {
			@Override
			public void handleStateChanged(ChangeEvent changeEvent) {
				if (changeEvent.getPropertyName().equals(ButtonModel.SELECTED_PROPERTY)) {
					handleSelectionChanged();
				}
			}
		});
	}

}
