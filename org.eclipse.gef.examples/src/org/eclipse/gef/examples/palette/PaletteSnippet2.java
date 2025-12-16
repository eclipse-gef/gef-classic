/*******************************************************************************
 * Copyright (c) 2025 IBM Corporation and others.
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

package org.eclipse.gef.examples.palette;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.ui.part.ViewPart;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolTipHelper;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.util.IToolTipHelperFactory;

import org.eclipse.gef.examples.Messages;

import org.osgi.service.component.annotations.Component;

/**
 * This snippet shows how to customize the tool-tip for the palette viewer. By
 * using a custom tool-tip helper, we can intercept the generated tool-tip
 * figure just as it is about to be shown and replace it with our own figure.
 */
public class PaletteSnippet2 extends ViewPart {
	private static final Font BOLD_FONT = new Font(null, "Arial", 12, SWT.BOLD); //$NON-NLS-1$
	private final PaletteRoot paletteRoot;
	private PaletteViewer paletteViewer;

	public PaletteSnippet2() {
		PaletteDrawer paletteDrawer = new PaletteDrawer(Messages.PaletteSnippet2_System);
		paletteDrawer.setDescription(Messages.PaletteSnippet2_System_Desc);
		ToolEntry paletteEntry = new SelectionToolEntry();
		paletteEntry.setDescription(Messages.PaletteSnippet2_Selection_Desc);
		paletteDrawer.add(paletteEntry);
		paletteRoot = new PaletteRoot();
		paletteRoot.add(paletteDrawer);
	}

	@Override
	public void createPartControl(Composite parent) {
		paletteViewer = new CustomPaletteViewer();
		paletteViewer.createControl(parent);
		paletteViewer.setPaletteRoot(paletteRoot);
	}

	@Override
	public void setFocus() {
		if (paletteViewer != null) {
			paletteViewer.getControl().setFocus();
		}
	}

	public static class CustomPaletteViewer extends PaletteViewer {
		// Sub-classed for the PaletteToolTipHelperFactory
	}

	public static class CustomToolTipHelper extends ToolTipHelper {
		private final EditPartViewer viewer;

		public CustomToolTipHelper(Control c, EditPartViewer viewer) {
			super(c);
			this.viewer = viewer;
		}

		@Override
		public void displayToolTipNear(IFigure hoverSource, IFigure tip, int eventX, int eventY) {
			IFigure figureToShow = createCustomToolTip(hoverSource);
			if (figureToShow == null) {
				figureToShow = tip;
			}
			super.displayToolTipNear(hoverSource, figureToShow, eventX, eventY);
		}

		private IFigure createCustomToolTip(IFigure hoverSource) {
			EditPart editPart = findEditPart(hoverSource);
			if (editPart == null || !(editPart.getModel() instanceof PaletteEntry paletteEntry)) {
				return null;
			}
			IFigure customFigure = new Figure();
			customFigure.setLayoutManager(new GridLayout());
			Label label1 = new Label(paletteEntry.getLabel());
			label1.setFont(BOLD_FONT);
			customFigure.add(label1, new GridData(SWT.FILL, SWT.FILL, true, false));
			Label label2 = new Label(paletteEntry.getDescription());
			customFigure.add(label2, new GridData(SWT.FILL, SWT.FILL, true, true));
			return customFigure;
		}

		private EditPart findEditPart(IFigure fig) {
			EditPart editPart = viewer.getVisualPartMap().get(fig);
			if (editPart != null) {
				return editPart;
			}

			if (fig.getParent() != null) {
				return findEditPart(fig.getParent());
			}

			return null;
		}
	}

	@Component
	public static class PaletteToolTipHelperFactory implements IToolTipHelperFactory {
		@Override
		public ToolTipHelper create(Control control, EditPartViewer viewer) {
			if (viewer.getClass() == CustomPaletteViewer.class) {
				return new CustomToolTipHelper(control, viewer);
			}
			return null;
		}
	}
}
