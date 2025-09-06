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
package org.eclipse.gef.palette;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.gef.Tool;

/**
 * A factory for returning Tools.
 */
public abstract class ToolEntry extends PaletteEntry {

	/** Type Identifier **/
	public static final Object PALETTE_TYPE_TOOL = "$Palette Tool";//$NON-NLS-1$

	private Map<Object, Object> map;
	private Class<? extends Tool> toolClass;

	/**
	 * Creates a new ToolEntry. Any parameter can be <code>null</code>.
	 *
	 * @param label     the entry's name
	 * @param shortDesc the entry's description
	 * @param iconSmall the entry's small icon
	 * @param iconLarge the entry's large icon
	 */
	public ToolEntry(String label, String shortDesc, ImageDescriptor iconSmall, ImageDescriptor iconLarge) {
		this(label, shortDesc, iconSmall, iconLarge, null);
	}

	/**
	 * Constructor to create a new ToolEntry. Any parameter can be
	 * <code>null</code>.
	 *
	 * @param label       the entry's name
	 * @param description the entry's description
	 * @param iconSmall   the entry's small icon
	 * @param iconLarge   the entry's large icon
	 * @param tool        the type of tool that this entry uses
	 * @since 3.1
	 */
	public ToolEntry(String label, String description, ImageDescriptor iconSmall, ImageDescriptor iconLarge,
			Class<? extends Tool> tool) {
		super(label, description, iconSmall, iconLarge, PALETTE_TYPE_TOOL);
		setToolClass(tool);
	}

	/**
	 * Creates the tool of the type specified by {@link #setToolClass(Class)} for
	 * this ToolEntry. The tool is also configured with the properties set in
	 * {@link #setToolProperty(Object, Object)}. Sub-classes overriding this method
	 * should ensure that their tools are also configured with those properties.
	 *
	 * @return the tool for this entry
	 */
	public Tool createTool() {
		if (toolClass == null) {
			return null;
		}
		Tool tool;
		try {
			tool = toolClass.getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			return null;
		}
		tool.setProperties(getToolProperties());
		return tool;
	}

	/**
	 * @return the properties set in {@link #setToolProperty(Object, Object)}
	 * @since 3.1
	 */
	protected Map getToolProperties() {
		return map;
	}

	/**
	 * Returns the property value for the specified property key.
	 *
	 * @param key the property key
	 * @return the value for the requested property
	 * @since 3.1
	 */
	public Object getToolProperty(Object key) {
		if (map != null) {
			return map.get(key);
		}
		return null;
	}

	/**
	 * Sets the type of tool to be created. This provides clients with a method of
	 * specifying a different type of tool to be created without having to
	 * sub-class. The provided class should have a default constructor for this to
	 * work successfully.
	 *
	 * @param toolClass the type of tool to be created by this entry
	 * @since 3.1
	 */
	public void setToolClass(Class<? extends Tool> toolClass) {
		this.toolClass = toolClass;
	}

	/**
	 * Clients can use this method to configure the associated tool without having
	 * to sub-class.
	 *
	 * @param key   the property name
	 * @param value a value of type associated with the given property
	 * @since 3.1
	 * @see Tool#setProperties(Map)
	 */
	public void setToolProperty(Object key, Object value) {
		if (map == null) {
			map = new HashMap<>();
		}
		map.put(key, value);
	}

}
