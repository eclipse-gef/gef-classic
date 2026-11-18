/*******************************************************************************
 * Copyright (c) 2026 DBeaver Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     DBeaver Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import org.eclipse.draw2d.BasicColorProvider;
import org.eclipse.draw2d.ColorProvider;
import org.eclipse.draw2d.ColorProvider.SystemColorFactory;

import org.eclipse.gef.GEFColorProvider;
import org.eclipse.gef.internal.ColorProviderService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

@SuppressWarnings("restriction")
public class ColorProviderServiceTest {

	private BundleContext context;
	private ColorProvider originalProvider;
	private ColorProviderService tracker;

	@BeforeEach
	public void setUp() {
		context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		originalProvider = SystemColorFactory.getColorProvider();
		tracker = new ColorProviderService(context);
	}

	@AfterEach
	public void tearDown() {
		tracker.close();
		SystemColorFactory.setColorProvider(originalProvider);
	}

	@Test
	public void testDoesNotInstallProviderUntilOpened() {
		ColorProvider basicProvider = new BasicColorProvider();
		SystemColorFactory.setColorProvider(basicProvider);
		tracker = new ColorProviderService(context);
		assertSame(basicProvider, SystemColorFactory.getColorProvider());
	}

	@Test
	public void testInstallsProviderForExistingWorkbench() {
		SystemColorFactory.setColorProvider(new BasicColorProvider());
		tracker.open();
		assertInstanceOf(GEFColorProvider.class, SystemColorFactory.getColorProvider());
	}

	@Test
	public void testInstallsProviderForNewWorkbenchService() {
		tracker.open();
		SystemColorFactory.setColorProvider(new BasicColorProvider());
		ServiceRegistration<IWorkbench> registration = context.registerService(IWorkbench.class,
				PlatformUI.getWorkbench(), null);
		try {
			assertSame(PlatformUI.getWorkbench(), tracker.getService(registration.getReference()));
			assertInstanceOf(GEFColorProvider.class, SystemColorFactory.getColorProvider());
		} finally {
			registration.unregister();
		}
	}

	@Test
	public void testPreservesCustomProvider() {
		ColorProvider customProvider = new GEFColorProvider();
		SystemColorFactory.setColorProvider(customProvider);
		tracker.open();
		assertSame(customProvider, SystemColorFactory.getColorProvider());
	}

	@Test
	public void testStopsTrackingWhenClosed() {
		tracker.open();
		tracker.close();
		ServiceRegistration<IWorkbench> registration = context.registerService(IWorkbench.class,
				PlatformUI.getWorkbench(), null);
		try {
			assertNull(tracker.getService(registration.getReference()));
		} finally {
			registration.unregister();
		}
	}
}
