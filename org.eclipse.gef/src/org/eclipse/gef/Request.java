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
package org.eclipse.gef;

import java.util.HashMap;
import java.util.Map;

/**
 * An Object used to communicate with EditParts. Request encapsulates the
 * information EditParts need to perform various functions. Requests are used
 * for obtaining commands, showing feedback, and performing generic operations.
 * TODO: This should probably go into the org.eclipse.gef.requests package.
 */
public class Request {

	private Object type;
	private Map<Object, Object> extendedData;

	/**
	 * Constructs an empty Request
	 */
	public Request() {
	}

	/**
	 * Constructs a Request with the specified <i>type</i>
	 *
	 * @param type the Request type
	 * @see #getType()
	 */
	public Request(Object type) {
		setType(type);
	}

	/**
	 * Returns a Map that can be used to save useful information in this request.
	 *
	 * @return a map to store useful information
	 */
	public Map<Object, Object> getExtendedData() {
		if (extendedData == null) {
			extendedData = new HashMap<>();
		}
		return extendedData;
	}

	/**
	 * Returns the type of the request. The type is often used as a quick way to
	 * filter recognized Requests. Once the type is identified, the Request is
	 * usually cast to a more specific subclass containing additional data.
	 *
	 * @return the Request <i>type</i>
	 */
	public Object getType() {
		return type;
	}

	/**
	 * Sets the given map to be the new extended data (by reference) for this
	 * request.
	 *
	 * @param map The new map
	 */
	@SuppressWarnings("unchecked")
	public void setExtendedData(Map<?, ?> map) {
		extendedData = (Map<Object, Object>) map;
	}

	/**
	 * Sets the type of the Request.
	 *
	 * @param type the Request type
	 */
	public void setType(Object type) {
		this.type = type;
	}

}
