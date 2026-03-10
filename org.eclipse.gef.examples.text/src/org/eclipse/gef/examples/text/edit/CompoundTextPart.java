/*******************************************************************************
 * Copyright (c) 2004, 2023 IBM Corporation and others.
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

package org.eclipse.gef.examples.text.edit;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.text.BlockFlow;
import org.eclipse.draw2d.text.CaretInfo;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.InlineFlow;

import org.eclipse.gef.examples.text.figures.CommentPage;
import org.eclipse.gef.examples.text.model.Container;
import org.eclipse.gef.examples.text.model.ModelElement;
import org.eclipse.gef.examples.text.requests.CaretRequest;
import org.eclipse.gef.examples.text.requests.SearchResult;

/**
 * @since 3.1
 */
public abstract class CompoundTextPart extends AbstractTextPart {

	protected CompoundTextPart(Container model) {
		setModel(model);
	}

	@Override
	public boolean acceptsCaret() {
		return getChildren().stream().anyMatch(TextEditPart::acceptsCaret);
	}

	@Override
	public void activate() {
		super.activate();
		getModel().getStyle().addPropertyChangeListener(this);
	}

	@Override
	protected void createEditPolicies() {
	}

	@Override
	protected IFigure createFigure() {
		return switch (getModel().getType()) {
		case Container.TYPE_INLINE -> new InlineFlow();
		case Container.TYPE_COMMENT -> new CommentPage();
		case Container.TYPE_PARAGRAPH -> {
			IFigure figure = new BlockFlow();
			figure.setBorder(new MarginBorder(4, 2, 4, 0));
			yield figure;
		}
		case Container.TYPE_ROOT -> {
			IFigure figure = new FlowPage();
			figure.setBorder(new MarginBorder(4));
			yield figure;
		}
		default -> throw new RuntimeException("unexpected container"); //$NON-NLS-1$
		};
	}

	@Override
	public void deactivate() {
		getModel().getStyle().removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public CaretInfo getCaretPlacement(int offset, boolean trailing) {
		throw new RuntimeException("This part cannot place the caret"); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends TextEditPart> getChildren() {
		return (List<? extends TextEditPart>) super.getChildren();
	}

	/**
	 * @see TextEditPart#getLength()
	 */
	@Override
	public int getLength() {
		return getChildren().size();
	}

	@Override
	public Container getModel() {
		return (Container) super.getModel();
	}

	@Override
	protected List<ModelElement> getModelChildren() {
		return getModel().getChildren();
	}

	/**
	 * @see TextEditPart#getTextLocation(CaretRequest, SearchResult)
	 */
	@Override
	public void getTextLocation(CaretRequest search, SearchResult result) {
		if (search.getType() == CaretRequest.LINE_BOUNDARY) {
			if (search.isForward) {
				searchLineEnd(search, result);
			} else {
				searchLineBegin(search, result);
			}
		} else if (search.getType() == CaretRequest.ROW || search.getType() == CaretRequest.LOCATION) {
			if (search.isForward) {
				searchLineBelow(search, result);
			} else {
				searchLineAbove(search, result);
			}
		} else if (search.getType() == CaretRequest.COLUMN || search.getType() == CaretRequest.WORD_BOUNDARY) {
			if (search.isForward) {
				searchForward(search, result);
			} else {
				searchBackward(search, result);
			}
		} else if (getParent() instanceof TextEditPart) {
			getTextParent().getTextLocation(search, result);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Container.CHILDREN_PROPERTY)) {
			refreshChildren();
		}
	}

	protected void searchBackward(CaretRequest search, SearchResult result) {
		int childIndex = search.isRecursive ? getChildren().size() - 1 : getChildren().indexOf(search.where.part) - 1;
		boolean wasRecursive = search.isRecursive;
		search.setRecursive(true);
		while (childIndex >= 0) {
			TextEditPart part = getChildren().get(childIndex);
			childIndex--;
			part.getTextLocation(search, result);
			if (result.location != null) {
				return;
			}
		}
		search.setRecursive(wasRecursive);
		if (!search.isRecursive && getParent() instanceof TextEditPart) {
			if (this instanceof BlockTextPart) {
				search.isInto = true;
			}
			search.setReferenceTextLocation(this, 0);
			getTextParent().getTextLocation(search, result);
		}
	}

	protected void searchForward(CaretRequest search, SearchResult result) {
		int childIndex = search.isRecursive ? 0 : getChildren().indexOf(search.where.part) + 1;
		int childCount = getChildren().size();
		boolean wasRecursive = search.isRecursive;
		search.setRecursive(true);
		while (childIndex < childCount) {
			TextEditPart part = getChildren().get(childIndex);
			childIndex++;
			part.getTextLocation(search, result);
			if (result.location != null) {
				return;
			}
		}
		search.setRecursive(wasRecursive);
		if (!search.isRecursive && getParent() instanceof TextEditPart) {
			if (this instanceof BlockTextPart) {
				search.isInto = true;
			}
			search.setReferenceTextLocation(this, getLength());
			getTextParent().getTextLocation(search, result);
		}
	}

	protected void searchLineAbove(CaretRequest search, SearchResult result) {
		int childIndex;
		TextEditPart part;
		if (search.isRecursive) {
			childIndex = getChildren().size() - 1;
		} else {
			childIndex = getChildren().indexOf(search.where.part);
			if (search.where.offset == 0) {
				childIndex--;
			}
		}

		boolean wasRecursive = search.isRecursive;
		search.setRecursive(true);
		while (childIndex >= 0) {
			part = getChildren().get(childIndex);
			childIndex--;
			part.getTextLocation(search, result);
			if (result.bestMatchFound) {
				return;
			}
		}
		search.setRecursive(wasRecursive);
		if (!search.isRecursive && getParent() instanceof TextEditPart) {
			search.setReferenceTextLocation(this, 0);
			getTextParent().getTextLocation(search, result);
		}
	}

	protected void searchLineBegin(CaretRequest search, SearchResult result) {
		int childIndex = 0;
		int childCount = getChildren().size();
		search.setRecursive(true);
		while (childIndex < childCount) {
			TextEditPart newPart = getChildren().get(childIndex);
			childIndex++;
			newPart.getTextLocation(search, result);
			if (result.location != null) {
				return;
			}
		}
	}

	protected void searchLineBelow(CaretRequest search, SearchResult result) {
		// The top of this figure must be below the bottom of the caret
		// if (getFigure().getBounds().y < caret.bottom())
		// return null;

		int childIndex;
		int childCount = getChildren().size();
		if (search.isRecursive || (!search.isRecursive && search.where == null)) {
			childIndex = 0;
		} else {
			childIndex = getChildren().indexOf(search.where.part);
			if (search.where.offset == search.where.part.getLength()) {
				childIndex++;
			}
		}

		boolean wasRecursive = search.isRecursive;
		search.setRecursive(true);
		while (childIndex < childCount) {
			TextEditPart part = getChildren().get(childIndex);
			childIndex++;
			part.getTextLocation(search, result);
			if (result.bestMatchFound) {
				return;
			}
		}
		search.setRecursive(wasRecursive);
		if (!search.isRecursive && getParent() instanceof TextEditPart) {
			search.setReferenceTextLocation(this, getLength());
			getTextParent().getTextLocation(search, result);
		}
	}

	protected void searchLineEnd(CaretRequest search, SearchResult result) {
		int childIndex = getChildren().size() - 1;
		TextEditPart child;
		search.setRecursive(true);
		while (childIndex >= 0) {
			child = getChildren().get(childIndex);
			childIndex--;
			child.getTextLocation(search, result);
			if (result.location != null) {
				return;
			}
		}
	}

}
