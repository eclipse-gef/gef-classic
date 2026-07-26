/*******************************************************************************
 * Copyright (c) 2024, 2026 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/

package org.eclipse.zest.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.progress.ProgressEvent;
import org.eclipse.zest.layouts.progress.ProgressListener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "removal", "deprecation" })
public class LayoutAlgorithmTest {
	private LayoutAlgorithm.Zest1 layoutAlgorithm;
	private Set<String> logger;

	@BeforeEach
	public void setUp() {
		logger = new HashSet<>();
		layoutAlgorithm = new GridLayoutAlgorithm.Zest1();
	}

	// Check for ConcurrentModificationException when the listeners are
	// added/removed while an event is fired.
	// See https://github.com/eclipse-gef/gef-classic/issues/398

	@Test
	public void testWithConcurrentModification1() throws InvalidLayoutConfiguration {
		testWith(new ProgressListener.Stub() {
			@Override
			public void progressStarted(ProgressEvent e) {
				logger.add("progressStarted()"); //$NON-NLS-1$
				layoutAlgorithm.removeProgressListener(this);
			}
		});
	}

	/**
	 * Test with {@link ProgressListener}.
	 */
	@Test
	public void testWithConcurrentModification2() throws InvalidLayoutConfiguration {
		testWith(new ProgressListener.Stub() {
			@Override
			public void progressUpdated(ProgressEvent e) {
				logger.add("progressUpdated()"); //$NON-NLS-1$
				layoutAlgorithm.removeProgressListener(this);
			}
		});
	}

	/**
	 * Test with {@link ProgressListener}.
	 */
	@Test
	public void testWithConcurrentModification3() throws InvalidLayoutConfiguration {
		testWith(new ProgressListener.Stub() {
			@Override
			public void progressEnded(ProgressEvent e) {
				logger.add("progressEnded()"); //$NON-NLS-1$
				layoutAlgorithm.removeProgressListener(this);
			}
		});
	}

	private void testWith(ProgressListener progressListener) throws InvalidLayoutConfiguration {
		LayoutEntity[] nodes = {};
		layoutAlgorithm.addProgressListener(progressListener);
		layoutAlgorithm.addProgressListener(new ProgressListener.Stub());

		layoutAlgorithm.applyLayout(nodes, new LayoutRelationship[0], 0, 0, 0, 0, false, false);
		assertEquals(logger.size(), 1); // $NON-NLS-1$
	}
}
