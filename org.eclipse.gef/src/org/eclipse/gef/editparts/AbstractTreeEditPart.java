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
package org.eclipse.gef.editparts;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.TreeEditPart;

/**
 * Default implementation for {@link TreeEditPart}s used in GEF
 * {@link org.eclipse.gef.ui.parts.TreeViewer}s.
 * <P>
 * This is an implementation class, and the documentation here is targeted at
 * <em>subclassing</em> this class. Callers of public API should refer to the
 * interface's documentation.
 */
public abstract class AbstractTreeEditPart extends AbstractEditPart implements TreeEditPart {

	/**
	 * Either a Tree or TreeItem
	 */
	protected Widget widget;

	private boolean expanded;

	/**
	 * Constructs a new EditPart with the specified model.
	 *
	 * @param model the model
	 */
	public AbstractTreeEditPart(Object model) {
		setModel(model);
	}

	/**
	 * Default constructor
	 */
	public AbstractTreeEditPart() {
	}

	/**
	 * Implemented to assign the child its {@link TreeEditPart#setWidget(Widget)
	 * widget}. Subclasses should not call or override this method.
	 *
	 * @see AbstractEditPart#addChildVisual(EditPart, int)
	 */
	@Override
	protected void addChildVisual(EditPart childEditPart, int index) {
		Widget curWidget = getWidget();
		TreeItem item;
		if (curWidget instanceof Tree tree) {
			item = new TreeItem(tree, 0, index);
		} else {
			item = new TreeItem((TreeItem) curWidget, 0, index);
		}
		((TreeEditPart) childEditPart).setWidget(item);
	}

	/**
	 * Convenience method that returns <code>true</code> if the widget is a TreeItem
	 * and is safe to use.
	 *
	 * @return <code>true</code> if the widget is a <code>TreeItem</code> and is not
	 *         disposed
	 */
	protected final boolean checkTreeItem() {
		return !(widget == null || widget.isDisposed() || widget instanceof Tree);
	}

	/**
	 * Override this method to install the EditPolicies for your EditPart.
	 *
	 * @see AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends TreeEditPart> getChildren() {
		return (List<? extends TreeEditPart>) super.getChildren();
	}

	/**
	 * @see EditPart#getDragTracker(Request)
	 */
	@Override
	public DragTracker getDragTracker(Request req) {
		return null;
	}

	/**
	 * Override this method to return the <code>Image</code> for this EditPart's
	 * {@link #widget}. This method is called from {@link #refreshVisuals()}.
	 *
	 * @return the Image to be displayed in the TreeItem
	 */
	@SuppressWarnings("static-method")
	protected Image getImage() {
		return null;
	}

	/**
	 * Override this method to return the String to be used in this EditPart's
	 * {@link #widget}. This method is called from {@link #refreshVisuals()}.
	 *
	 * @return the String to be displayed by the TreeItem
	 */
	protected String getText() {
		return getClass().getName();
	}

	/**
	 * @see TreeEditPart#getWidget()
	 */
	@Override
	public Widget getWidget() {
		return widget;
	}

	/**
	 * By default, this method will apply an <code>Image</code> and
	 * <code>String</code> to the widget if it is a <code>TreeItem</code>.
	 * Subclasses should override {@link #getImage()} and {@link #getText()} to
	 * provide the <code>Image</code> and <code>String</code> used.
	 * <P>
	 * Subclasses might extend this method if they also want to change the
	 * TreeItem's foreground or background color.
	 *
	 * @see AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		setWidgetImage(getImage());
		setWidgetText(getText());
	}

	/**
	 * Disposes the child's <code>widget</code> and sets it to <code>null</code> .
	 *
	 * @see AbstractEditPart#removeChildVisual(EditPart)
	 */
	@Override
	protected void removeChildVisual(EditPart childEditPart) {
		TreeEditPart treeEditPart = (TreeEditPart) childEditPart;
		treeEditPart.getWidget().dispose();
		treeEditPart.setWidget(null);
	}

	/**
	 * @see AbstractEditPart#reorderChild(EditPart, int)
	 */
	@Override
	protected void reorderChild(EditPart editpart, int index) {
		super.reorderChild(editpart, index);
		// Reordering assigns a new Widget to the child. Call refresh() to
		// update widget.
		editpart.refresh();
	}

	/**
	 * Sets the {@link #widget}.
	 *
	 * @see org.eclipse.gef.TreeEditPart#setWidget(Widget)
	 */
	@Override
	public void setWidget(Widget widget) {
		if (widget != null) {
			widget.setData(this);
			if (widget instanceof TreeItem item) {
				item.addDisposeListener(e -> expanded = item.getExpanded());
			}
			for (TreeEditPart tep : getChildren()) {
				if (widget instanceof TreeItem ti) {
					tep.setWidget(new TreeItem(ti, 0));
				} else {
					tep.setWidget(new TreeItem((Tree) widget, 0));
				}

				// We have just assigned a new TreeItem to the EditPart
				tep.refresh();
			}
			if (widget instanceof TreeItem item) {
				item.setExpanded(expanded);
			}
		} else {
			getChildren().forEach(child -> child.setWidget(null));
		}
		this.widget = widget;
	}

	/**
	 * Sets a specified <code>Image</code> into the widget iff it is a
	 * <code>TreeItem</code>.
	 *
	 * @param image the Image
	 */
	protected final void setWidgetImage(Image image) {
		if (checkTreeItem()) {
			((TreeItem) getWidget()).setImage(image);
		}
	}

	/**
	 * Sets a specified <code>String</code> into the widget iff it is a
	 * <code>TreeItem</code>.
	 *
	 * @param text the String
	 */
	protected final void setWidgetText(String text) {
		if (checkTreeItem()) {
			((TreeItem) getWidget()).setText(text);
		}
	}

}
