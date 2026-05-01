/*******************************************************************************
 * Copyright (c) 2006, 2026 IBM Corporation and others.
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

package org.eclipse.gef.internal;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import org.eclipse.draw2d.ToolTipHelper;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.ui.parts.DomainEventDispatcher;
import org.eclipse.gef.util.IToolTipHelperFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

public class InternalGEFPlugin implements BundleActivator {
	/** Monitor scale property */
	public static final String MONITOR_SCALE_PROPERTY = "monitorScale"; //$NON-NLS-1$

	private static BundleContext context;
	private static InternalGEFPlugin singleton;
	private static Boolean requiresDisabledIcons;
	private static Collection<ServiceReference<IToolTipHelperFactory>> toolTipProviderRefs;
	private static Collection<IToolTipHelperFactory> toolTipProviders;
	private ImageRegistry imageRegistry;
	private IEclipsePreferences preferences;

	public InternalGEFPlugin() {
		singleton = this;
	}

	@Override
	public void start(BundleContext bc) throws Exception {
		context = bc;
		toolTipProviders = new ArrayList<>();
		toolTipProviderRefs = bc.getServiceReferences(IToolTipHelperFactory.class, null);
		for (ServiceReference<IToolTipHelperFactory> toolTipProviderRef : toolTipProviderRefs) {
			toolTipProviders.add(bc.getService(toolTipProviderRef));
		}
		imageRegistry = new ImageRegistry();
		preferences = InstanceScope.INSTANCE.getNode(bc.getBundle().getSymbolicName());
		Logger.setContext(new LoggerContext());
	}

	@Override
	public void stop(BundleContext bc) throws Exception {
		imageRegistry.dispose();
		toolTipProviders.clear();
		for (ServiceReference<IToolTipHelperFactory> toolTipProviderRef : toolTipProviderRefs) {
			bc.ungetService(toolTipProviderRef);
		}
	}

	public static BundleContext getContext() {
		return context;
	}

	public static InternalGEFPlugin getDefault() {
		return singleton;
	}

	/**
	 * Returns the image registry for this plug-in.
	 * <p>
	 * The image registry contains the images used by this plug-in that are very
	 * frequently used and so need to be globally shared within the plug-in. Since
	 * many OSs have a severe limit on the number of images that can be in memory at
	 * any given time, a plug-in should only keep a small number of images in their
	 * registry.
	 *
	 * @return the image registry
	 */
	public ImageRegistry getImageRegistry() {
		return imageRegistry;
	}

	/**
	 * Returns the preference store for this plug-in. This preference store is used
	 * to hold persistent settings for this plug-in in the context of a workbench.
	 * Some of these settings will be user controlled, whereas others may be
	 * internal setting that are never exposed to the user.
	 *
	 * @return the plug-in preferences
	 */
	public IEclipsePreferences getStorePreferences() {
		return preferences;
	}

	/**
	 * Returns all registered {@link ToolTipHelper} factories that can be used in
	 * the {@link DomainEventDispatcher}.
	 */
	public static Collection<IToolTipHelperFactory> getToolTipHelperFactories() {
		return Collections.unmodifiableCollection(toolTipProviders);
	}

	/**
	 * This method attempts to create the cursor using a constructor introduced in
	 * SWT 3.131.0 that takes an {@link ImageDataProvider}. If this constructor is
	 * not available (SWT versions prior to 3.131.0), it falls back to using the
	 * older constructor that accepts {@link ImageData}.
	 */
	public static Cursor createCursor(ImageDescriptor source, int hotspotX, int hotspotY) {
		try {
			ImageDataProvider provider = zoom -> {
				if (zoom < 150) {
					return source.getImageData(100);
				}
				if (zoom < 200) {
					return source.getImageData(150);
				}
				return source.getImageData(200);
			};
			Constructor<Cursor> ctor = Cursor.class.getConstructor(Device.class, ImageDataProvider.class, int.class,
					int.class);
			return ctor.newInstance(null, provider, hotspotX, hotspotY);
		} catch (NoSuchMethodException e) {
			// SWT version < 3.131.0 (no ImageDataProvider-based constructor)
			return new Cursor(null, source.getImageData(100), hotspotX, hotspotY); // older constructor
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("Failed to instantiate Cursor", e); //$NON-NLS-1$
		}
	}

	public static EditPartListener createAutoscaleEditPartListener(Consumer<Double> consumer) {
		final PropertyChangeListener autoScaleListener = evt -> {
			if (InternalGEFPlugin.MONITOR_SCALE_PROPERTY.equals(evt.getPropertyName()) && evt.getNewValue() != null) {
				double newValue = (double) evt.getNewValue();
				consumer.accept(newValue);
			}
		};

		return new EditPartListener.Stub() {
			@Override
			public void partActivated(EditPart editpart) {
				editpart.getViewer().addPropertyChangeListener(autoScaleListener);
				try {
					double scale = (double) editpart.getViewer().getProperty(InternalGEFPlugin.MONITOR_SCALE_PROPERTY);
					consumer.accept(scale);
				} catch (NullPointerException | ClassCastException e) {
					// no value available
				}
			}

			@Override
			public void partDeactivated(EditPart editpart) {
				editpart.getViewer().removePropertyChangeListener(autoScaleListener);
			}
		};
	}

	/**
	 * With Eclipse 4.36 (and therefore SWT 3.130.0), it is no longer necessary to
	 * set a "disabled" icon in e.g. {@code Actions}.
	 */
	public static boolean requiresDisabledIcon() {
		if (requiresDisabledIcons == null) {
			Version minVersion = new Version(3, 130, 0);
			requiresDisabledIcons = FrameworkUtil.getBundle(SWT.class).getVersion().compareTo(minVersion) < 0;
		}
		return requiresDisabledIcons;
	}
}
