/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
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

package org.eclipse.gef.print;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImagePrintFigureOperation;
import org.eclipse.draw2d.Layer;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;

/**
 * Implementation of GEF image drawing capabilities.
 *
 * @since 3.25
 */
public class ImagePrintGraphicalViewerOperation extends ImagePrintFigureOperation {
	private final GraphicalViewer viewer;
	private List<EditPart> selectedEditParts;

	/**
	 * Constructor for PrintGraphicalViewerOperation
	 *
	 * @param viewer The viewer containing what is to be printed NOTE: The
	 *               GraphicalViewer to be printed must have a {@link Layer Layer}
	 *               with the {@link LayerConstants PRINTABLE_LAYERS} key.
	 */
	public ImagePrintGraphicalViewerOperation(GraphicalViewer viewer) {
		super(viewer.getControl());
		this.viewer = viewer;
		LayerManager layerManager = LayerManager.Helper.find(viewer);
		IFigure layer = layerManager.getLayer(LayerConstants.PRINTABLE_LAYERS);
		setPrintSource(layer);
	}

	/**
	 * @see ImagePrintFigureOperation#preparePrintSource(Graphics)
	 */
	@Override
	protected void preparePrintSource(Graphics graphics) {
		super.preparePrintSource(graphics);
		selectedEditParts = new ArrayList<>(viewer.getSelectedEditParts());
		viewer.deselectAll();
	}

	/**
	 * @see ImagePrintFigureOperation#restorePrintSource(Graphics)
	 */
	@Override
	protected void restorePrintSource(Graphics graphics) {
		viewer.setSelection(new StructuredSelection(selectedEditParts));
		super.restorePrintSource(graphics);
	}
}
