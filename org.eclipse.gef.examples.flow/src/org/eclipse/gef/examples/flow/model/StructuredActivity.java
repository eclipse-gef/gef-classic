/*******************************************************************************
 * Copyright (c) 2000, 2023 IBM Corporation and others.
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
package org.eclipse.gef.examples.flow.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A Structured activity is an activity whose execution is determined by some
 * internal structure.
 *
 * @author hudsonr
 */
public class StructuredActivity extends Activity {

	static final long serialVersionUID = 1;

	private final List<Activity> children = new ArrayList<>();

	public void addChild(Activity child) {
		addChild(child, -1);
	}

	public void addChild(Activity child, int index) {
		if (index >= 0) {
			children.add(index, child);
		} else {
			children.add(child);
		}
		fireStructureChange(CHILDREN, child);
	}

	public List<Activity> getChildren() {
		return children;
	}

	public void removeChild(FlowElement child) {
		children.remove(child);
		fireStructureChange(CHILDREN, child);
	}

}
