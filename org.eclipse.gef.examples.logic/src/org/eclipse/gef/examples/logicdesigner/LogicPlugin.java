/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
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
package org.eclipse.gef.examples.logicdesigner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.PaletteStack;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.tools.MarqueeSelectionTool;

import org.eclipse.gef.examples.logicdesigner.figures.AndGateFigure;
import org.eclipse.gef.examples.logicdesigner.figures.GroundFigure;
import org.eclipse.gef.examples.logicdesigner.figures.LEDFigure;
import org.eclipse.gef.examples.logicdesigner.figures.LiveOutputFigure;
import org.eclipse.gef.examples.logicdesigner.figures.OrGateFigure;
import org.eclipse.gef.examples.logicdesigner.figures.XOrGateFigure;
import org.eclipse.gef.examples.logicdesigner.model.AndGate;
import org.eclipse.gef.examples.logicdesigner.model.Circuit;
import org.eclipse.gef.examples.logicdesigner.model.GroundOutput;
import org.eclipse.gef.examples.logicdesigner.model.LED;
import org.eclipse.gef.examples.logicdesigner.model.LiveOutput;
import org.eclipse.gef.examples.logicdesigner.model.LogicDiagramFactory;
import org.eclipse.gef.examples.logicdesigner.model.LogicFlowContainer;
import org.eclipse.gef.examples.logicdesigner.model.LogicLabel;
import org.eclipse.gef.examples.logicdesigner.model.OrGate;
import org.eclipse.gef.examples.logicdesigner.model.XORGate;
import org.eclipse.gef.examples.logicdesigner.tools.LogicCreationTool;

public class LogicPlugin extends org.eclipse.ui.plugin.AbstractUIPlugin {

	private static LogicPlugin singleton;

	public static <T> Dimension getMaximumSizeFor(Class<T> modelClass) {
		if (LED.class.equals(modelClass)) {
			return LEDFigure.SIZE;
		}
		if (AndGate.class.equals(modelClass)) {
			return AndGateFigure.SIZE;
		}
		if (OrGate.class.equals(modelClass)) {
			return OrGateFigure.SIZE;
		}
		if (XORGate.class.equals(modelClass)) {
			return XOrGateFigure.SIZE;
		}
		if (GroundOutput.class.isAssignableFrom(modelClass)) {
			return GroundFigure.SIZE;
		}
		if (LiveOutput.class.equals(modelClass)) {
			return LiveOutputFigure.SIZE;
		}
		return IFigure.MAX_DIMENSION;
	}

	public static <T> Dimension getMinimumSizeFor(Class<T> modelClass) {
		if (LogicLabel.class.equals(modelClass)) {
			return new Dimension(IFigure.MIN_DIMENSION.width, 30);
		}
		if (Circuit.class.equals(modelClass)) {
			return new Dimension(25, 20);
		}
		if (LED.class.equals(modelClass)) {
			return LEDFigure.SIZE;
		}
		if (AndGate.class.equals(modelClass)) {
			return AndGateFigure.SIZE;
		}
		if (OrGate.class.equals(modelClass)) {
			return OrGateFigure.SIZE;
		}
		if (XORGate.class.equals(modelClass)) {
			return XOrGateFigure.SIZE;
		}
		if (GroundOutput.class.isAssignableFrom(modelClass)) {
			return GroundFigure.SIZE;
		}
		if (LiveOutput.class.equals(modelClass)) {
			return LiveOutputFigure.SIZE;
		}
		return IFigure.MIN_DIMENSION;
	}

	private static List<PaletteEntry> createCategories(PaletteRoot root) {
		List<PaletteEntry> categories = new ArrayList<>(3);

		categories.add(createControlGroup(root));
		categories.add(createComponentsDrawer());
		categories.add(createComplexPartsDrawer());
		// categories.add(createTemplateComponentsDrawer());
		// categories.add(createComplexTemplatePartsDrawer());

		return categories;
	}

	private static PaletteContainer createComplexPartsDrawer() {
		PaletteDrawer drawer = new PaletteDrawer(LogicMessages.LogicPlugin_Category_ComplexParts_Label,
				ImageDescriptor.createFromFile(Circuit.class, "icons/can.gif")); //$NON-NLS-1$

		List<PaletteEntry> entries = new ArrayList<>();

		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				LogicMessages.LogicPlugin_Tool_CreationTool_HalfAdder_Label,
				LogicMessages.LogicPlugin_Tool_CreationTool_HalfAdder_Description,
				LogicDiagramFactory.getHalfAdderFactory(),
				ImageDescriptor.createFromFile(Circuit.class, "icons/halfadder16.gif"), //$NON-NLS-1$
				ImageDescriptor.createFromFile(Circuit.class, "icons/halfadder24.gif") //$NON-NLS-1$
		);
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry(LogicMessages.LogicPlugin_Tool_CreationTool_FullAdder_Label,
				LogicMessages.LogicPlugin_Tool_CreationTool_FullAdder_Description,
				LogicDiagramFactory.getFullAdderFactory(),
				ImageDescriptor.createFromFile(Circuit.class, "icons/fulladder16.gif"), //$NON-NLS-1$
				ImageDescriptor.createFromFile(Circuit.class, "icons/fulladder24.gif") //$NON-NLS-1$
		);
		entries.add(combined);

		drawer.addAll(entries);
		return drawer;
	}

	private static PaletteContainer createComponentsDrawer() {

		PaletteDrawer drawer = new PaletteDrawer(LogicMessages.LogicPlugin_Category_Components_Label,
				ImageDescriptor.createFromFile(Circuit.class, "icons/comp.gif"));//$NON-NLS-1$

		List<PaletteEntry> entries = new ArrayList<>();

		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				LogicMessages.LogicPlugin_Tool_CreationTool_FlowContainer_Label,
				LogicMessages.LogicPlugin_Tool_CreationTool_FlowContainer_Description,
				new SimpleFactory<>(LogicFlowContainer.class),
				ImageDescriptor.createFromFile(Circuit.class, "icons/logicflow16.gif"), //$NON-NLS-1$
				ImageDescriptor.createFromFile(Circuit.class, "icons/logicflow24.gif")//$NON-NLS-1$
		);
		combined.setToolClass(LogicCreationTool.class);
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry(LogicMessages.LogicPlugin_Tool_CreationTool_Circuit_Label,
				LogicMessages.LogicPlugin_Tool_CreationTool_Circuit_Description, new SimpleFactory<>(Circuit.class),
				ImageDescriptor.createFromFile(Circuit.class, "icons/circuit16.gif"), //$NON-NLS-1$
				ImageDescriptor.createFromFile(Circuit.class, "icons/circuit24.gif")//$NON-NLS-1$
		);
		combined.setToolClass(LogicCreationTool.class);
		entries.add(combined);

		entries.add(new PaletteSeparator());

		combined = new CombinedTemplateCreationEntry(LogicMessages.LogicPlugin_Tool_CreationTool_Label_Label,
				LogicMessages.LogicPlugin_Tool_CreationTool_Label_Description, new SimpleFactory<>(LogicLabel.class),
				ImageDescriptor.createFromFile(Circuit.class, "icons/label16.gif"), //$NON-NLS-1$
				ImageDescriptor.createFromFile(Circuit.class, "icons/label24.gif")//$NON-NLS-1$
		);
		combined.setToolClass(LogicCreationTool.class);
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry(LogicMessages.LogicPlugin_Tool_CreationTool_LED_Label,
				LogicMessages.LogicPlugin_Tool_CreationTool_LED_Description, new SimpleFactory<>(LED.class),
				ImageDescriptor.createFromFile(Circuit.class, "icons/ledicon16.gif"), //$NON-NLS-1$
				ImageDescriptor.createFromFile(Circuit.class, "icons/ledicon24.gif")//$NON-NLS-1$
		);
		combined.setToolClass(LogicCreationTool.class);
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry(LogicMessages.LogicPlugin_Tool_CreationTool_ORGate_Label,
				LogicMessages.LogicPlugin_Tool_CreationTool_ORGate_Description, new SimpleFactory<>(OrGate.class),
				ImageDescriptor.createFromFile(Circuit.class, "icons/or16.gif"), //$NON-NLS-1$
				ImageDescriptor.createFromFile(Circuit.class, "icons/or24.gif")//$NON-NLS-1$
		);
		combined.setToolClass(LogicCreationTool.class);
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry(LogicMessages.LogicPlugin_Tool_CreationTool_XORGate_Label,
				LogicMessages.LogicPlugin_Tool_CreationTool_XORGate_Description, new SimpleFactory<>(XORGate.class),
				ImageDescriptor.createFromFile(Circuit.class, "icons/xor16.gif"), //$NON-NLS-1$
				ImageDescriptor.createFromFile(Circuit.class, "icons/xor24.gif")//$NON-NLS-1$
		);
		combined.setToolClass(LogicCreationTool.class);
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry(LogicMessages.LogicPlugin_Tool_CreationTool_ANDGate_Label,
				LogicMessages.LogicPlugin_Tool_CreationTool_ANDGate_Description, new SimpleFactory<>(AndGate.class),
				ImageDescriptor.createFromFile(Circuit.class, "icons/and16.gif"), //$NON-NLS-1$
				ImageDescriptor.createFromFile(Circuit.class, "icons/and24.gif")//$NON-NLS-1$
		);
		combined.setToolClass(LogicCreationTool.class);
		entries.add(combined);

		PaletteStack liveGroundStack = new PaletteStack(
				LogicMessages.LogicPlugin_Tool_CreationTool_LiveGroundStack_Label,
				LogicMessages.LogicPlugin_Tool_CreationTool_LiveGroundStack_Description, null);

		combined = new CombinedTemplateCreationEntry(LogicMessages.LogicPlugin_Tool_CreationTool_LiveOutput_Label,
				LogicMessages.LogicPlugin_Tool_CreationTool_LiveOutput_Description,
				new SimpleFactory<>(LiveOutput.class),
				ImageDescriptor.createFromFile(Circuit.class, "icons/live16.gif"), //$NON-NLS-1$
				ImageDescriptor.createFromFile(Circuit.class, "icons/live24.gif")//$NON-NLS-1$
		);
		combined.setToolClass(LogicCreationTool.class);
		liveGroundStack.add(combined);

		combined = new CombinedTemplateCreationEntry(LogicMessages.LogicPlugin_Tool_CreationTool_Ground_Label,
				LogicMessages.LogicPlugin_Tool_CreationTool_Ground_Description, new SimpleFactory<>(GroundOutput.class),
				ImageDescriptor.createFromFile(Circuit.class, "icons/ground16.gif"), //$NON-NLS-1$
				ImageDescriptor.createFromFile(Circuit.class, "icons/ground24.gif")//$NON-NLS-1$
		);
		combined.setToolClass(LogicCreationTool.class);
		liveGroundStack.add(combined);

		entries.add(liveGroundStack);

		drawer.addAll(entries);
		return drawer;
	}

	private static PaletteContainer createControlGroup(PaletteRoot root) {
		PaletteGroup controlGroup = new PaletteGroup(LogicMessages.LogicPlugin_Category_ControlGroup_Label);

		List<PaletteEntry> entries = new ArrayList<>();

		ToolEntry tool = new PanningSelectionToolEntry();
		entries.add(tool);
		root.setDefaultEntry(tool);

		PaletteStack marqueeStack = new PaletteStack(LogicMessages.Marquee_Stack, "", null); //$NON-NLS-1$

		// NODES CONTAINED (default)
		marqueeStack.add(new MarqueeToolEntry());

		// NODES TOUCHED
		MarqueeToolEntry marquee = new MarqueeToolEntry();
		marquee.setToolProperty(MarqueeSelectionTool.PROPERTY_MARQUEE_BEHAVIOR,
				Integer.valueOf(MarqueeSelectionTool.BEHAVIOR_NODES_TOUCHED));
		marqueeStack.add(marquee);

		// NODES CONTAINED AND RELATED CONNECTIONS

		marquee = new MarqueeToolEntry();
		marquee.setToolProperty(MarqueeSelectionTool.PROPERTY_MARQUEE_BEHAVIOR,
				Integer.valueOf(MarqueeSelectionTool.BEHAVIOR_NODES_CONTAINED_AND_RELATED_CONNECTIONS));
		marqueeStack.add(marquee);

		// NODES TOUCHED AND RELATED CONNECTIONS
		marquee = new MarqueeToolEntry();
		marquee.setToolProperty(MarqueeSelectionTool.PROPERTY_MARQUEE_BEHAVIOR,
				Integer.valueOf(MarqueeSelectionTool.BEHAVIOR_NODES_TOUCHED_AND_RELATED_CONNECTIONS));
		marqueeStack.add(marquee);

		// CONNECTIONS CONTAINED
		marquee = new MarqueeToolEntry();
		marquee.setToolProperty(MarqueeSelectionTool.PROPERTY_MARQUEE_BEHAVIOR,
				Integer.valueOf(MarqueeSelectionTool.BEHAVIOR_CONNECTIONS_CONTAINED));
		marqueeStack.add(marquee);

		// CONNECTIONS TOUCHED
		marquee = new MarqueeToolEntry();
		marquee.setToolProperty(MarqueeSelectionTool.PROPERTY_MARQUEE_BEHAVIOR,
				Integer.valueOf(MarqueeSelectionTool.BEHAVIOR_CONNECTIONS_TOUCHED));
		marqueeStack.add(marquee);

		marqueeStack.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
		entries.add(marqueeStack);

		tool = new ConnectionCreationToolEntry(
				LogicMessages.LogicPlugin_Tool_ConnectionCreationTool_ConnectionCreationTool_Label,
				LogicMessages.LogicPlugin_Tool_ConnectionCreationTool_ConnectionCreationTool_Description, null,
				ImageDescriptor.createFromFile(Circuit.class, "icons/connection16.gif"), //$NON-NLS-1$
				ImageDescriptor.createFromFile(Circuit.class, "icons/connection24.gif")//$NON-NLS-1$
		);
		entries.add(tool);
		controlGroup.addAll(entries);
		return controlGroup;
	}

	static PaletteRoot createPalette() {
		PaletteRoot logicPalette = new PaletteRoot();
		logicPalette.addAll(createCategories(logicPalette));
		return logicPalette;
	}

	public static LogicPlugin getDefault() {
		return singleton;
	}

	public LogicPlugin() {
		if (singleton == null) {
			singleton = this;
		}
	}

}
