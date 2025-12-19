/*******************************************************************************
 * Copyright 2005, 2025 CHISEL Group, University of Victoria, Victoria,
 *                      BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.zest.layouts.progress;

/**
 * When an algorithm wants to notify everyone it has completely part of its
 * task, it throws a ProgressEvent. The progress is a number (currentProgress)
 * representing the current steps completed out of the total number of steps
 * (totalProgress)
 *
 * @author Casey Best
 * @deprecated No longer used in Zest 2.x. This class will be removed in a
 *             future release.
 * @noextend This class is not intended to be subclassed by clients.
 * @noreference This class is not intended to be referenced by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@Deprecated(since = "2.0", forRemoval = true)
public class ProgressEvent {
	@Deprecated
	int stepsCompleted;
	@Deprecated
	int totalSteps;

	/**
	 * Creates a progress event.
	 *
	 * @param stepsCompleted     The current progress out of the total
	 * @param totalNumberOfSteps The number used to indicate when the algorithm will
	 *                           finish
	 */
	@Deprecated
	public ProgressEvent(int stepsCompleted, int totalNumberOfSteps) {
		this.stepsCompleted = stepsCompleted;
		this.totalSteps = totalNumberOfSteps;
	}

	/**
	 * Returns the number of steps already completed.
	 */
	@Deprecated
	public int getStepsCompleted() {
		return stepsCompleted;
	}

	/**
	 * Returns the total number of steps to complete.
	 */
	@Deprecated
	public int getTotalNumberOfSteps() {
		return totalSteps;
	}
}
