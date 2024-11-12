/*******************************************************************************
 * Copyright (c) 2000, 2024 IBM Corporation and others.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is intended for internal use only. TODO: If this is for internal
 * use only, we should move it to the internal package.
 */
public final class EventListenerList {

	private final Map<Class<?>, List<Object>> listeners = new HashMap<>();

	/**
	 * Adds a listener of type <i>c</i> to the list.
	 *
	 * @param c        the class
	 * @param listener the listener
	 */
	public synchronized <T> void addListener(Class<T> c, Object listener) {
		if (listener == null || c == null) {
			throw new IllegalArgumentException();
		}

		listeners.computeIfAbsent(c, newC -> new CopyOnWriteArrayList<>()).add(listener);
	}

	/**
	 * Returns <code>true</code> if this list of listeners contains a listener of
	 * type <i>c</i>.
	 *
	 * @param c the type
	 * @return whether this list contains a listener of type <i>c</i>
	 */
	public synchronized <T> boolean containsListener(Class<T> c) {
		return listeners.containsKey(c);
	}

	/**
	 * Returns an Iterator of all the listeners of type <i>c</i>.
	 *
	 * @param listenerType the type
	 * @return an Iterator of all the listeners of type <i>c</i>
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T> Iterator<T> getListeners(final Class<T> listenerType) {
		return (Iterator<T>) listeners.getOrDefault(listenerType, Collections.emptyList()).iterator();
	}

	/**
	 * Returns a typed Iterable of all listeners of a of type <i>c</i>.
	 *
	 * @param listenerType the type
	 * @return an Iterable of all the listeners of type <i>c</i>
	 * @since 3.13
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T> Iterable<T> getListenersIterable(final Class<T> listenerType) {
		return (Iterable<T>) listeners.getOrDefault(listenerType, Collections.emptyList());
	}

	/**
	 * Removes the first <i>listener</i> of the specified type by identity.
	 *
	 * @param c        the type
	 * @param listener the listener
	 */
	public synchronized <T> void removeListener(Class<T> c, Object listener) {
		if (listener == null || c == null) {
			throw new IllegalArgumentException();
		}

		List<Object> specListeners = listeners.get(c);
		if (specListeners != null) {
			specListeners.remove(listener);
			if (specListeners.isEmpty()) {
				listeners.remove(c);
			}
		}
	}

}
