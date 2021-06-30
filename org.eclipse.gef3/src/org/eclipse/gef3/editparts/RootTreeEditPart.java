/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef3.editparts;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

import org.eclipse.gef3.DragTracker;
import org.eclipse.gef3.EditPart;
import org.eclipse.gef3.EditPartViewer;
import org.eclipse.gef3.Request;
import org.eclipse.gef3.RootEditPart;
import org.eclipse.gef3.TreeEditPart;
import org.eclipse.gef3.commands.Command;
import org.eclipse.gef3.commands.UnexecutableCommand;

/**
 * The root editpart for a <code>TreeViewer</code>. There is limited control of
 * a Tree, so this root implementation should work for all purposes. This
 * implementation does little more than hold onto the viewer, and pass the
 * <code>Tree</code> to the contents as its widget.
 * 
 * @author hudsonr
 */
public class RootTreeEditPart extends
    AbstractEditPart implements RootEditPart,
		TreeEditPart {

	private EditPartViewer viewer;
	private Widget widget;
	private TreeEditPart contents;

	/**
	 * This is where the child gets added. No TreeItem is needed here because
	 * the contents is actually represented by the Tree iteself.
	 * 
	 * @param childEditPart
	 *            EditPart of child to be added.
	 * @param index
	 *            Position where it is to be added.
	 */
	protected void addChildVisual(EditPart childEditPart, int index) {
		((TreeEditPart) childEditPart).setWidget(widget);
	}

	/**
	 * @see AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
	}

	/**
	 * @see org.eclipse.gef3.EditPart#getCommand(org.eclipse.gef3.Request)
	 */
	public Command getCommand(Request request) {
		return UnexecutableCommand.INSTANCE;
	}

	/**
	 * @see org.eclipse.gef3.RootEditPart#getContents()
	 */
	public EditPart getContents() {
		return contents;
	}

	/**
	 * This method will never be called on a tree root.
	 * 
	 * @see org.eclipse.gef3.EditPart#getDragTracker(org.eclipse.gef3.Request)
	 */
	public DragTracker getDragTracker(Request request) {
		return null;
	}

	/**
	 * Returns <code>this</code>.
	 * 
	 * @see org.eclipse.gef3.EditPart#getRoot()
	 */
	public RootEditPart getRoot() {
		return this;
	}

	/**
	 * @see org.eclipse.gef3.RootEditPart#getViewer()
	 */
	public EditPartViewer getViewer() {
		return viewer;
	}

	/**
	 * The editpart holds onto the SWT Tree, which is also the contents' widget.
	 * 
	 * @see org.eclipse.gef3.TreeEditPart#getWidget()
	 */
	public Widget getWidget() {
		return widget;
	}

	/**
	 * Overridden to do nothing since the child is explicitly set.
	 * 
	 * @see AbstractEditPart#refreshChildren()
	 */
	protected void refreshChildren() {
	}

	/**
	 * This is where the child gets removed. This method is overridden here so
	 * that the AbstractTreeEditPart does not dispose the widget, which is the
	 * Tree in this case. The tree is owned by the viewer, not the child.
	 * 
	 * @param childEditPart
	 *            EditPart of child to be removed.
	 */
	protected void removeChildVisual(EditPart childEditPart) {
		((TreeEditPart) childEditPart).setWidget(null);
	}

	/**
	 * @see org.eclipse.gef3.RootEditPart#setContents(org.eclipse.gef3.EditPart)
	 */
	public void setContents(EditPart editpart) {
		if (contents != null) {
			if (getWidget() != null)
				((Tree) getWidget()).removeAll();
			removeChild(contents);
		}
		contents = (TreeEditPart) editpart;

		if (contents != null)
			addChild(contents, -1);
	}

	/**
	 * @see org.eclipse.gef3.RootEditPart#setViewer(org.eclipse.gef3.EditPartViewer)
	 */
	public void setViewer(EditPartViewer epviewer) {
		viewer = epviewer;
	}

	/**
	 * Called by <code>TreeViewer</code> to set the <code>Tree</code> into the
	 * root. The root simply holds onto this widget and passes it to the
	 * contents when the contents is added.
	 * 
	 * @see org.eclipse.gef3.TreeEditPart#setWidget(org.eclipse.swt.widgets.Widget)
	 */
	public void setWidget(Widget w) {
		widget = w;
		if (contents != null)
			contents.setWidget(w);
	}

}
