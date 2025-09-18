/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
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
package org.eclipse.gef.requests;

import org.eclipse.draw2d.geometry.Dimension;

import org.eclipse.gef.RequestConstants;

/**
 * A Request to create a new object.
 */
public class CreateRequest extends LocationRequest implements DropRequest {

	private Object newObject;

	private Dimension size;
	private int flags = 0;
	private static final int SNAP_TO = 1;

	private CreationFactory creationFactory;

	/**
	 * Creates a CreateRequest with the default type.
	 */
	public CreateRequest() {
		setType(RequestConstants.REQ_CREATE);
	}

	/**
	 * Creates a CreateRequest with the given type.
	 *
	 * @param type The type of request.
	 */
	public CreateRequest(Object type) {
		setType(type);
	}

	/**
	 * Returns the CreationFactory for this request.
	 *
	 * @return the CreationFactory
	 */
	protected CreationFactory getFactory() {
		return creationFactory;
	}

	/**
	 * Gets the new object from the factory and returns that object.
	 *
	 * @return the new object
	 */
	public Object getNewObject() {
		if (newObject == null) {
			if (getFactory() == null) {
				throw new IllegalArgumentException("CreateRequest has unspecified CreationFactory"); //$NON-NLS-1$
			}
			newObject = getFactory().getNewObject();
		}
		return newObject;
	}

	/**
	 * Returns the type of the new object.
	 *
	 * @return the type of the new object
	 */
	public Object getNewObjectType() {
		if (getFactory() == null) {
			throw new IllegalArgumentException("CreateRequest has unspecified CreationFactory"); //$NON-NLS-1$
		}
		return getFactory().getObjectType();
	}

	/**
	 * Returns the size of the object to be created.
	 *
	 * @return the size
	 */
	public Dimension getSize() {
		return size;
	}

	/**
	 * Returns <code>true</code> if snap-to is enabled
	 *
	 * @since 3.7
	 * @return <code>true</code> if the request is for a creation with snap-to
	 *         enabled
	 */
	public boolean isSnapToEnabled() {
		return (flags & SNAP_TO) != 0;
	}

	/**
	 * Sets the factory to be used when creating the new object.
	 *
	 * @param factory the factory
	 */
	public void setFactory(CreationFactory factory) {
		creationFactory = factory;
	}

	/**
	 * Sets the size of the new object.
	 *
	 * @param size the size
	 */
	public void setSize(Dimension size) {
		this.size = size;
	}

	/**
	 * Used to set whether snap-to is being performed.
	 *
	 * @since 3.7
	 * @param value <code>true</code> if the request is for a creation with snap-to
	 *              enabled
	 */
	public void setSnapToEnabled(boolean value) {
		flags = value ? (flags | SNAP_TO) : (flags & ~SNAP_TO);
	}
}
