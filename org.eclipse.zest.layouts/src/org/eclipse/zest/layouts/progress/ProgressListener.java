/*******************************************************************************
 * Copyright 2005, 2024 CHISEL Group, University of Victoria, Victoria,
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
 * Listens for ProgressEvents which are thrown by layout algorithms at frequent
 * intervals.
 *
 * @author Ian Bull
 * @author Casey Best
 * @deprecated No longer used in Zest 2.x. This interface will be removed in a
 *             future release.
 * @noextend This interface is not intended to be extended by clients.
 * @noreference This interface is not intended to be referenced by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
@Deprecated(since = "2.0", forRemoval = true)
public interface ProgressListener {
	/**
	 * An empty implementation of {@link ProgressListener} for convenience.
	 *
	 * @deprecated No longer used in Zest 2.x. This interface will be removed in a
	 *             future release.
	 * @noextend This interface is not intended to be extended by clients.
	 * @noreference This interface is not intended to be referenced by clients.
	 * @noimplement This interface is not intended to be implemented by clients.
	 * @since 1.5
	 */
	@Deprecated(since = "2.0", forRemoval = true)
	public static class Stub implements ProgressListener {
		@Override
		public void progressStarted(ProgressEvent e) {
		}

		@Override
		public void progressUpdated(ProgressEvent e) {
		}

		@Override
		public void progressEnded(ProgressEvent e) {
		}
	}

	/**
	 *
	 * @param e
	 */
	public void progressStarted(ProgressEvent e);

	/**
	 * Called when the progress of a layout changes
	 */
	public void progressUpdated(ProgressEvent e);

	/**
	 *
	 * @param e
	 */
	public void progressEnded(ProgressEvent e);
}
