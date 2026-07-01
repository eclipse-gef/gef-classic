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
package org.eclipse.gef.ui.palette.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.ui.IMemento;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.TextFlow;

import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.gef.ui.palette.PaletteColorProvider;
import org.eclipse.gef.ui.palette.PaletteMessages;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerPreferences;

/**
 * The abstract implementation of palette edit parts. All edit parts used in the
 * palette must extend this class.
 */
public abstract class PaletteEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

	/**
	 * The name of each entry in the palette, used to restore the state.
	 */
	public static final String XML_NAME = "entry"; //$NON-NLS-1$
	private static final Border TOOLTIP_BORDER = new MarginBorder(0, 2, 1, 0);
	/**
	 * @deprecated Use {@code getViewer().getResourceManager()} instead. This field
	 *             will be removed after the 2025-12 release.
	 */
	@Deprecated(forRemoval = true, since = "2025-12")
	private static ImageCache globalImageCache;
	private AccessibleEditPart acc;
	private final PropertyChangeListener childListener = evt -> {
		if (evt.getPropertyName().equals(PaletteEntry.PROPERTY_VISIBLE)) {
			refreshChildren();
		}
	};
	private Image image;
	private ImageDescriptor imgDescriptor;

	/**
	 * Constructor for the PaletteEditPart.
	 *
	 * @param model The model element for this edit part.
	 */
	public PaletteEditPart(PaletteEntry model) {
		setModel(model);
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		PaletteEntry model = getModel();
		model.addPropertyChangeListener(this);
		traverseChildren(model, true);
	}

	/**
	 * returns the AccessibleEditPart for this EditPart. This method is called
	 * lazily from {@link #getAccessibleEditPart()}.
	 *
	 * @return the AccessibleEditPart.
	 */
	@SuppressWarnings("static-method") // allow subclasses to create their AccesibleEditPart
	protected AccessibleEditPart createAccessible() {
		return null;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	public void createEditPolicies() {
	}

	/**
	 * Create the tool tip for this palette edit part.
	 *
	 * @return the tool tip figure.
	 */
	protected IFigure createToolTip() {
		String message = getToolTipText();
		if (message == null || message.length() == 0) {
			return null;
		}

		FlowPage fp = new FlowPage() {
			@Override
			public Dimension getPreferredSize(int w, int h) {
				Dimension d = super.getPreferredSize(-1, -1);
				if (d.width > 150) {
					d = super.getPreferredSize(150, -1);
				}
				return d;
			}
		};
		fp.setOpaque(true);
		fp.setBorder(TOOLTIP_BORDER);
		TextFlow tf = new TextFlow();
		tf.setText(message);
		fp.add(tf);
		return fp;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		PaletteEntry model = getModel();
		model.removePropertyChangeListener(this);
		traverseChildren(model, false);
		if (image != null) {
			image.dispose();
			image = null;
		}
		super.deactivate();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getAccessibleEditPart()
	 */
	@Override
	protected AccessibleEditPart getAccessibleEditPart() {
		if (acc == null) {
			acc = createAccessible();
		}
		return acc;
	}

	/**
	 * A selection tracker for the edit part.
	 */
	public class SingleSelectionTracker extends SelectEditPartTracker {
		/**
		 * Constructor for a SingleSelectionTracker.
		 */
		public SingleSelectionTracker() {
			super(PaletteEditPart.this);
		}

		/**
		 * @see org.eclipse.gef.tools.SelectEditPartTracker#performSelection()
		 */
		@Override
		protected void performSelection() {
			if (hasSelectionOccurred()) {
				return;
			}
			setFlag(FLAG_SELECTION_PERFORMED, true);
			getCurrentViewer().select(getSourceEditPart());
		}
	}

	@SuppressWarnings("unchecked") // PaletteEditPart may only have PaletteEditParts as children
	@Override
	public List<? extends PaletteEditPart> getChildren() {
		return (List<? extends PaletteEditPart>) super.getChildren();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getDragTracker(Request)
	 */
	@Override
	public DragTracker getDragTracker(Request request) {
		return new SingleSelectionTracker();
	}

	/**
	 * Returns the image cache. The cache is global, and is shared by all palette
	 * edit parts. This has the disadvantage that once an image is allocated, it is
	 * never freed until the display is disposed. However, it has the advantage that
	 * the same image in different palettes is only ever created once.
	 *
	 * @return the image cache.
	 * @deprecated Use {@code getViewer().getResourceManager()} instead. This method
	 *             will be removed after the 2025-12 release.
	 */
	@Deprecated(forRemoval = true, since = "2025-12")
	protected static ImageCache getImageCache() {
		ImageCache cache = globalImageCache;
		if (cache == null) {
			globalImageCache = cache = new ImageCache();
			Display display = Display.getDefault();
			if (display != null) {
				display.disposeExec(() -> {
					if (globalImageCache != null) {
						globalImageCache.dispose();
						globalImageCache = null;
					}
				});
			}
		}
		return cache;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	@Override
	public List<PaletteEntry> getModelChildren() {
		List<PaletteEntry> modelChildren;
		if (!(getModel() instanceof PaletteContainer palCont)) {
			return Collections.emptyList();
		}
		modelChildren = new ArrayList<>(palCont.getChildren());

		PaletteEntry prevVisibleEntry = null;
		for (Iterator<PaletteEntry> iter = modelChildren.iterator(); iter.hasNext();) {
			PaletteEntry entry = iter.next();
			if (!entry.isVisible()) {
				// not visible
				iter.remove();
			} else if (entry instanceof PaletteSeparator && prevVisibleEntry == null) {
				// first visible item in a group is a separator, don't need it
				iter.remove();
			} else if (entry instanceof PaletteSeparator && prevVisibleEntry instanceof PaletteSeparator) {
				// previous visible entry was a separator, don't need it
				iter.remove();
			} else {
				prevVisibleEntry = entry;
			}
		}
		// check to see if last visible entry was a separator, and thus should
		// be hidden
		if (prevVisibleEntry instanceof PaletteSeparator) {
			modelChildren.remove(prevVisibleEntry);
		}

		return modelChildren;
	}

	/**
	 * Get the casted model element for this palette edit part.
	 *
	 * @return the model element.
	 * @since 3.15
	 */
	@Override
	public PaletteEntry getModel() {
		return (PaletteEntry) super.getModel();
	}

	/**
	 * Get the model element for this palette edit part.
	 *
	 * @return the model element.
	 * @deprecated use {@link #getModel()} instead
	 */
	@Deprecated(since = "3.19", forRemoval = true)
	protected PaletteEntry getPaletteEntry() {
		return getModel();
	}

	/**
	 * Get the palette viewer for this palette edit part.
	 *
	 * @return the palette viewer.
	 * @deprecated use {@link #getViewer()} instead
	 */
	@Deprecated(since = "3.19", forRemoval = true)
	protected PaletteViewer getPaletteViewer() {
		return getViewer();
	}

	/**
	 * Get the palette viewer preferences for this palette edit part.
	 *
	 * @return the palette viewer preferences.
	 */
	protected PaletteViewerPreferences getPreferenceSource() {
		return getViewer().getPaletteViewerPreferences();
	}

	/**
	 * Get the tool tip figure for this palette edit part.
	 *
	 * @return the tool tip figure.
	 */
	protected IFigure getToolTipFigure() {
		return getFigure();
	}

	/**
	 * Get the tool tip text for this palette edit part.
	 *
	 * @return the tool tip text.
	 */
	protected String getToolTipText() {
		String text = null;
		PaletteEntry entry = getModel();
		String desc = entry.getDescription();
		boolean needName = nameNeededInToolTip();
		if (desc == null || desc.trim().equals(entry.getLabel()) || desc.trim().equals("")) { //$NON-NLS-1$
			if (needName) {
				text = entry.getLabel();
			}
		} else {
			if (needName) {
				text = entry.getLabel() + " " //$NON-NLS-1$
						+ PaletteMessages.NAME_DESCRIPTION_SEPARATOR + " " + desc; //$NON-NLS-1$
			} else {
				text = desc;
			}
		}
		if (text != null && text.trim().equals("")) { //$NON-NLS-1$
			return null;
		}
		return text;
	}

	/**
	 * @see org.eclipse.gef.EditPart#getViewer()
	 * @since 3.19
	 */
	@Override
	public PaletteViewer getViewer() {
		return (PaletteViewer) super.getViewer();
	}

	/**
	 * Convenience method that simply delegates to
	 * {@link PaletteViewer#getColorProvider()}.
	 *
	 * @return The color provider used for this edit part.
	 * @since 3.20
	 */
	protected final PaletteColorProvider getColorProvider() {
		return getViewer().getColorProvider();
	}

	/**
	 * Determine if the name is needed in the tool tip.
	 *
	 * @return <code>true</code> if the name is needed in the tool tip.
	 * @since 3.2
	 */
	protected boolean nameNeededInToolTip() {
		return getLayoutSetting() == PaletteViewerPreferences.LAYOUT_ICONS;
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String property = evt.getPropertyName();
		if (property.equals(PaletteContainer.PROPERTY_CHILDREN)) {
			traverseChildren((List<PaletteEntry>) evt.getOldValue(), false);
			refreshChildren();
			traverseChildren((List<PaletteEntry>) evt.getNewValue(), true);
		} else if (property.equals(PaletteEntry.PROPERTY_LABEL) || property.equals(PaletteEntry.PROPERTY_SMALL_ICON)
				|| property.equals(PaletteEntry.PROPERTY_LARGE_ICON)
				|| property.equals(PaletteEntry.PROPERTY_DESCRIPTION)) {
			refreshVisuals();
		}
	}

	/**
	 * Restore the state of the palette entry.
	 *
	 * @param memento the saved state of the palette entry.
	 */
	public void restoreState(IMemento memento) {
		IMemento[] childMementos = memento.getChildren(XML_NAME);
		int index = 0;
		for (PaletteEditPart part : getChildren()) {
			part.restoreState(childMementos[index]);
			++index;
		}
	}

	/**
	 * Save the state of the palette entry.
	 *
	 * @param memento the saved state of the palette entry.
	 */
	public void saveState(IMemento memento) {
		getChildren().forEach(part -> part.saveState(memento.createChild(XML_NAME)));
	}

	/**
	 * Set the image for this palette edit part.
	 *
	 * @param desc the image descriptor.
	 */
	protected void setImageDescriptor(ImageDescriptor desc) {
		if (desc == imgDescriptor) {
			return;
		}
		ResourceManager resourceManager = getViewer().getResourceManager();
		if (imgDescriptor != null) {
			resourceManager.destroy(imgDescriptor);
		}
		imgDescriptor = desc;
		setImageInFigure(resourceManager.create(imgDescriptor));
	}

	/**
	 * Set the image to be used in the figure for this edit edit.
	 *
	 * @param image the image
	 */
	protected void setImageInFigure(Image image) {
	}

	private void traverseChildren(PaletteEntry parent, boolean add) {
		if (!(parent instanceof PaletteContainer container)) {
			return;
		}
		traverseChildren(container.getChildren(), add);
	}

	private void traverseChildren(List<? extends PaletteEntry> children, boolean add) {
		for (PaletteEntry entry : children) {
			if (add) {
				entry.addPropertyChangeListener(childListener);
			} else {
				entry.removePropertyChangeListener(childListener);
			}
		}
	}

	/**
	 * The image cache for this edit part.
	 *
	 * @deprecated Use {@code getViewer().getResourceManager()} instead. This class
	 *             will be removed after the 2025-12 release.
	 */
	@Deprecated(forRemoval = true, since = "2025-12")
	protected static class ImageCache {
		/** Map from ImageDescriptor to Image */
		private final Map<ImageDescriptor, Image> images = new HashMap<>(11);

		@Deprecated
		Image getImage(ImageDescriptor desc) {
			if (desc == null) {
				return null;
			}
			return images.computeIfAbsent(desc, ImageDescriptor::createImage);
		}

		@Deprecated
		Image getMissingImage() {
			return getImage(ImageDescriptor.getMissingImageDescriptor());
		}

		@Deprecated
		void dispose() {
			images.values().forEach(Image::dispose);
			images.clear();
		}
	}

	/**
	 * Returns the current layout setting.
	 *
	 * @return the current layout setting.
	 * @see PaletteViewerPreferences#getLayoutSetting()
	 * @since 3.4
	 */
	protected int getLayoutSetting() {
		if (getParent() instanceof PaletteEditPart palEP) {
			return palEP.getLayoutSetting();
		}
		return getPreferenceSource().getLayoutSetting();
	}

	/**
	 * Overwritten to ensure palette entries are always selectable, even if their
	 * figure is not showing).
	 *
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#isSelectable()
	 */
	@Override
	public boolean isSelectable() {
		// bug #349124
		return true;
	}

	/**
	 * Returns true if this item is on the palette toolbar.
	 *
	 * @return true if this item is on the palette toolbar; false otherwise
	 * @since 3.4
	 */
	public boolean isToolbarItem() {
		if (getParent() instanceof PaletteEditPart palEP) {
			return palEP.isToolbarItem();
		}
		return false;
	}
}
