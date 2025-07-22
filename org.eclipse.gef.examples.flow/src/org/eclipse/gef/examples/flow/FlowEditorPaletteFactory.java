/*******************************************************************************
 * Copyright (c) 2003, 2025 IBM Corporation and others.
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
package org.eclipse.gef.examples.flow;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.draw2d.internal.ImageUtils;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;

import org.eclipse.gef.examples.flow.model.Activity;
import org.eclipse.gef.examples.flow.model.ParallelActivity;
import org.eclipse.gef.examples.flow.model.SequentialActivity;

/**
 * Handles the creation of the palette for the Flow Editor.
 *
 * @author Daniel Lee
 */
public final class FlowEditorPaletteFactory {

	private static List<PaletteEntry> createCategories(PaletteRoot root) {
		List<PaletteEntry> categories = new ArrayList<>(2);
		categories.add(createControlGroup(root));
		categories.add(createComponentsDrawer());
		return categories;
	}

	private static PaletteContainer createComponentsDrawer() {

		PaletteDrawer drawer = new PaletteDrawer("Components", null); //$NON-NLS-1$

		List<PaletteEntry> entries = new ArrayList<>();

		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry("Activity", //$NON-NLS-1$
				"Create a new Activity Node", Activity.class, new SimpleFactory<>(Activity.class), //$NON-NLS-1$
				ImageDescriptor.createFromFile(FlowPlugin.class,
						ImageUtils.getEffectiveFileName("images/gear16.svg")), //$NON-NLS-1$
				ImageDescriptor.createFromFile(Activity.class,
						ImageUtils.getEffectiveFileName("images/gear24.svg"))); //$NON-NLS-1$
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("Sequential Activity", "Create a Sequential Activity", //$NON-NLS-1$ //$NON-NLS-2$
				SequentialActivity.class, new SimpleFactory<>(SequentialActivity.class),
				ImageDescriptor.createFromFile(FlowPlugin.class,
						ImageUtils.getEffectiveFileName("images/sequence16.svg")), //$NON-NLS-1$
				ImageDescriptor.createFromFile(FlowPlugin.class,
						ImageUtils.getEffectiveFileName("images/sequence24.svg"))); //$NON-NLS-1$
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("Parallel Activity", "Create a  Parallel Activity", //$NON-NLS-1$ //$NON-NLS-2$
				ParallelActivity.class, new SimpleFactory<>(ParallelActivity.class),
				ImageDescriptor.createFromFile(FlowPlugin.class,
						ImageUtils.getEffectiveFileName("images/parallel16.svg")), //$NON-NLS-1$
				ImageDescriptor.createFromFile(FlowPlugin.class,
						ImageUtils.getEffectiveFileName("images/parallel24.svg"))); //$NON-NLS-1$
		entries.add(combined);

		drawer.addAll(entries);
		return drawer;
	}

	private static PaletteContainer createControlGroup(PaletteRoot root) {
		PaletteGroup controlGroup = new PaletteGroup("Control Group"); //$NON-NLS-1$

		List<PaletteEntry> entries = new ArrayList<>();

		ToolEntry tool = new SelectionToolEntry();
		entries.add(tool);
		root.setDefaultEntry(tool);

		tool = new MarqueeToolEntry();
		entries.add(tool);

		PaletteSeparator sep = new PaletteSeparator("org.eclipse.gef.examples.flow.flowplugin.sep2"); //$NON-NLS-1$
		sep.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
		entries.add(sep);

		tool = new ConnectionCreationToolEntry("Connection Creation", "Creating connections", null, //$NON-NLS-1$ //$NON-NLS-2$
				ImageDescriptor.createFromFile(FlowPlugin.class,
						ImageUtils.getEffectiveFileName("images/connection16.svg")), //$NON-NLS-1$
				ImageDescriptor.createFromFile(FlowPlugin.class,
						ImageUtils.getEffectiveFileName("images/connection24.svg"))); //$NON-NLS-1$
		entries.add(tool);
		controlGroup.addAll(entries);
		return controlGroup;
	}

	/**
	 * Creates the PaletteRoot and adds all Palette elements.
	 *
	 * @return the root
	 */
	public static PaletteRoot createPalette() {
		PaletteRoot flowPalette = new PaletteRoot();
		flowPalette.addAll(createCategories(flowPalette));
		return flowPalette;
	}

	private FlowEditorPaletteFactory() {
		throw new UnsupportedOperationException("Utility class shell not be instantiated!"); //$NON-NLS-1$
	}
}
