/*******************************************************************************
 * Copyright (c) 2004, 2023 IBM Corporation and others.
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

package org.eclipse.gef.examples.text.model.commands;

import org.eclipse.gef.examples.text.model.ModelLocation;
import org.eclipse.gef.examples.text.model.TextRun;

/**
 * Divides a TextRun into itself and another TextRun.
 * 
 * @since 3.1
 */
public class SubdivideElement extends MiniEdit {

	private final TextRun run;
	private final int offset;
	private TextRun inserted;

	public SubdivideElement(TextRun run, int offset) {
		this.run = run;
		this.offset = offset;
	}

	@Override
	public void apply() {
		inserted = run.subdivideRun(offset);
		int index = run.getContainer().getChildren().indexOf(run);
		run.getContainer().add(inserted, index + 1);
	}

	@Override
	public boolean canApply() {
		return true;
	}

	@Override
	public void reapply() {
		throw new RuntimeException("Need to implement"); //$NON-NLS-1$
	}

	@Override
	public ModelLocation getResultingLocation() {
		return new ModelLocation(inserted, 0);
	}

	@Override
	public void rollback() {
		inserted.getContainer().remove(inserted);
		run.insertText(inserted.getText(), run.size());
		inserted.setText(""); //$NON-NLS-1$
	}

}
