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
package org.eclipse.gef.ui.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IAction;

import org.eclipse.gef.Disposable;

/**
 * A container for editor actions. You must register the actions before they
 * will be available to the editor.
 */
public class ActionRegistry {

	/*
	 * A hashmap that contains the actions.
	 */
	private final Map<String, IAction> map = new HashMap<>(15);

	/**
	 * Calls dispose on all actions which implement the {@link Disposable} interface
	 * so they can perform their own clean-up.
	 */
	public void dispose() {
		map.values().stream().filter(Disposable.class::isInstance).map(Disposable.class::cast)
				.forEach(Disposable::dispose);
	}

	/**
	 * Returns <code>null</code> or the <code>IAction</code> with the given key.
	 *
	 * @param key the ID of the action being requested
	 * @return <code>null</code> or the action with the corresponding ID
	 */
	public IAction getAction(Object key) {
		return map.get(key);
	}

	/**
	 * Returns an {@link Iterator} over all the actions.
	 *
	 * @return an iterator over all actions
	 */
	public Iterator<IAction> getActions() {
		return map.values().iterator();
	}

	/**
	 * Register an action with this registry. The action must have an ID.
	 *
	 * @param action the action being registered.
	 */
	public void registerAction(IAction action) {
		Assert.isNotNull(action.getId(), "action must have an ID in " + //$NON-NLS-1$
				getClass().getName() + " :registerAction(IAction)");//$NON-NLS-1$
		registerAction(action.getId(), action);
	}

	/**
	 * Register an action with this registry using the given id.
	 */
	private void registerAction(String id, IAction action) {
		map.put(id, action);
	}

	/**
	 * Removes an action from this registry. The action must have an ID.
	 *
	 * @param action the action to remove
	 */
	public void removeAction(IAction action) {
		map.remove(action.getId());
	}

}
