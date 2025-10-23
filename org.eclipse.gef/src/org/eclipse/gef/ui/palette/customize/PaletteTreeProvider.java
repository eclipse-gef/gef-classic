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
package org.eclipse.gef.ui.palette.customize;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;

/**
 * This is the {@link org.eclipse.jface.viewers.IContentProvider} for the
 * {@link org.eclipse.jface.viewers.TreeViewer} used in
 * {@link org.eclipse.gef.ui.palette.customize.PaletteCustomizerDialog}.
 *
 * @author Pratik Shah
 * @see org.eclipse.jface.viewer.TreeViewer
 * @see org.eclipse.gef.ui.palette.customize.PaletteCustomizerDialog
 */
class PaletteTreeProvider implements ITreeContentProvider {

	private final TreeViewer viewer;
	private PaletteRoot root;
	private final PropertyChangeListener modelListener = this::handlePropertyChanged;

	/**
	 * Constructor
	 *
	 * @param treeviewer The TreeViewer whose ContentProvider this
	 *                   PaletteTreeProvider is
	 */
	public PaletteTreeProvider(TreeViewer treeviewer) {
		this.viewer = treeviewer;
	}

	/**
	 * Stops listening to the model
	 *
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		traverseModel(root, false);
	}

	/**
	 * If the given element does not have any children, this method should return
	 * <code>null</code>. This fixes the problem where a "+" sign is incorrectly
	 * placed next to an empty container in the tree.
	 *
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof PaletteContainer pc) {
			List<? extends PaletteEntry> children = pc.getChildren();
			if (!children.isEmpty()) {
				return children.toArray();
			}
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element) != null;
	}

	/**
	 * This method should not return <code>null</code>.
	 *
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		Object[] elements = getChildren(inputElement);
		if (elements == null) {
			elements = new Object[0];
		}
		return elements;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(Object)
	 */
	@Override
	public Object getParent(Object element) {
		return ((PaletteEntry) element).getParent();
	}

	/**
	 * This method is invoked whenever there is any change in the model. It updates
	 * the viewer with the changes that were made to the model. Sub-classes may
	 * override this method to change or extend its functionality.
	 *
	 * @param evt The {@link PropertyChangeEvent} that was fired from the model
	 */
	protected void handlePropertyChanged(PropertyChangeEvent evt) {
		PaletteEntry entry = ((PaletteEntry) evt.getSource());
		String property = evt.getPropertyName();
		switch (property) {
		case PaletteEntry.PROPERTY_LABEL:
		case PaletteEntry.PROPERTY_SMALL_ICON:
			viewer.update(entry, null);
			break;
		case PaletteEntry.PROPERTY_VISIBLE:
			viewer.refresh(entry);
			break;
		case PaletteContainer.PROPERTY_CHILDREN: {
			viewer.refresh(entry);
			@SuppressWarnings("unchecked")
			List<PaletteEntry> oldChildren = (List<PaletteEntry>) evt.getOldValue();
			oldChildren.forEach(child -> traverseModel(child, false));
			traverseModel(entry, true);
			break;
		}
		default:
			break;
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(Viewer, Object,
	 *      Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (root != null) {
			traverseModel(root, false);
		}
		if (newInput != null) {
			root = (PaletteRoot) newInput;
			traverseModel(root, true);
		}
	}

	private void traverseModel(PaletteEntry entry, boolean isHook) {
		if (isHook) {
			entry.addPropertyChangeListener(modelListener);
		} else {
			entry.removePropertyChangeListener(modelListener);
		}

		if (hasChildren(entry)) {
			Object[] children = getChildren(entry);
			for (Object child : children) {
				traverseModel((PaletteEntry) child, isHook);
			}
		}
	}

}
