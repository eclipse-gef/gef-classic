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
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;

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
	/** Label provider property */
	public static final String LABEL_PROVIDER_PROPERTY = "labelProvider"; //$NON-NLS-1$

	private static BundleContext context;
	private static InternalGEFPlugin singleton;
	private static Boolean requiresDisabledIcons;
	private static Collection<ServiceReference<IToolTipHelperFactory>> toolTipProviderRefs;
	private static Collection<IToolTipHelperFactory> toolTipProviders;
	private final ReentrantLock lock = new ReentrantLock();
	private IPersistentPreferenceStore preferenceStore;

	private static Constructor<?> UI_SCOPED_PREFERENCE_STORE_CONSTRUCTOR = getConstructor(
			"org.eclipse.ui.preferences.ScopedPreferenceStore", IScopeContext.class, String.class); //$NON-NLS-1$
	private static Constructor<?> JFACE_SCOPED_PREFERENCE_STORE_CONSTRUCTOR = getConstructor(
			"org.eclipse.jface.preference.ScopedPreferenceStore", IScopeContext.class, String.class); //$NON-NLS-1$

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
		Logger.setContext(new LoggerContext());
	}

	@Override
	public void stop(BundleContext bc) throws Exception {
		toolTipProviders.clear();
		for (ServiceReference<IToolTipHelperFactory> toolTipProviderRef : toolTipProviderRefs) {
			bc.ungetService(toolTipProviderRef);
		}
		if (preferenceStore != null) {
			try {
				preferenceStore.save();
				preferenceStore = null;
			} catch (IOException e) {
				getLog().log(Status.error(e.getMessage(), e));
			}
		}
		InternalImages.dispose();
	}

	public static BundleContext getContext() {
		return context;
	}

	public static InternalGEFPlugin getDefault() {
		return singleton;
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

	/**
	 * Returns the class for the given class name, or null if the class cannot be
	 * found.
	 *
	 * @param className the fully qualified name of the class to load
	 * @return the class for the given class name, or null if the class cannot be
	 *         found
	 */
	private static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Returns the constructor for the given class name and parameter types, or null
	 * if the class or constructor cannot be found.
	 *
	 * @param name           the fully qualified name of the class to load
	 * @param parameterTypes the parameter types of the constructor to find
	 * @return the constructor for the given class name and parameter types, or null
	 *         if the class or constructor cannot be found
	 */
	private static Constructor<?> getConstructor(String name, Class<?>... parameterTypes) {
		Class<?> clazz = getClass(name);
		if (clazz == null) {
			return null;
		}
		try {
			return clazz.getConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	/**
	 * Returns the preference store for this UI plug-in. This preference store is
	 * used to hold persistent settings for this plug-in in the context of a
	 * workbench. Some of these settings will be user controlled, whereas others may
	 * be internal setting that are never exposed to the user.
	 * <p>
	 * <strong>NOTE:</strong> This method may be called from a non UI-Thread.
	 * </p>
	 *
	 * @return the preference store
	 */
	public IPreferenceStore getPreferenceStore() {
		lock.lock();
		try {
			if (preferenceStore == null) {
				// ScopedPreferenceStore was moved to org.eclipse.jface in Eclipse 2026-12
				if (JFACE_SCOPED_PREFERENCE_STORE_CONSTRUCTOR != null) {
					preferenceStore = (IPersistentPreferenceStore) JFACE_SCOPED_PREFERENCE_STORE_CONSTRUCTOR
							.newInstance(InstanceScope.INSTANCE, context.getBundle().getSymbolicName());
				} else if (UI_SCOPED_PREFERENCE_STORE_CONSTRUCTOR != null) {
					preferenceStore = (IPersistentPreferenceStore) UI_SCOPED_PREFERENCE_STORE_CONSTRUCTOR
							.newInstance(InstanceScope.INSTANCE, context.getBundle().getSymbolicName());
				} else {
					throw new UnsupportedOperationException("No suitable ScopedPreferenceStore implementation found"); //$NON-NLS-1$
				}
			}
		} catch (ReflectiveOperationException e) {
			getLog().log(Status.error(e.getMessage(), e));
			throw new IllegalStateException("Unable to create preference store", e); //$NON-NLS-1$
		} finally {
			lock.unlock();
		}
		return preferenceStore;
	}

	private static ILog getLog() {
		return Platform.getLog(context.getBundle());
	}

}
