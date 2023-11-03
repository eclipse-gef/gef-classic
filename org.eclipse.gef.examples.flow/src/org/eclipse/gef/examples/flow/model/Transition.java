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
package org.eclipse.gef.examples.flow.model;

/**
 * @author hudsonr Created on Jun 30, 2003
 */
public class Transition extends FlowElement {

	private static final long serialVersionUID = 4486688831285730788L;
	public Activity source, target;

	public Transition(Activity source, Activity target) {
		this.source = source;
		this.target = target;

		source.addOutput(this);
		target.addInput(this);
	}

}
