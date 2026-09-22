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
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/

package org.eclipse.zest.dot.ui.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.dot.core.internal.DOTDocumentModel;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

public class DOTContentOutlinePage extends Page implements IContentOutlinePage {
	private final IDocument document;
	private GraphViewer graphViewer;

	public DOTContentOutlinePage(ITextEditor editor) {
		this.document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
	}

	@Override
	public void createControl(Composite parent) {
		graphViewer = new GraphViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		graphViewer.setContentProvider(new DOTContentProvider());
		graphViewer.setLabelProvider(new DOTLabelDecorator());
		graphViewer.setLayoutAlgorithm(new SpringLayoutAlgorithm());
		document.addDocumentListener(new IDocumentListener() {
			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
				// nothing to do
			}

			@Override
			public void documentChanged(DocumentEvent event) {
				refreshGraphInput(new DOTDocumentModel(event.getDocument().get()));
			}
		});
		refreshGraphInput(new DOTDocumentModel(document.get()));
	}

	private void refreshGraphInput(DOTDocumentModel model) {
		final boolean directed = model.getGraph().directed();
		final int connectionStyle = directed ? ZestStyles.CONNECTIONS_DIRECTED : ZestStyles.NONE;
		graphViewer.setConnectionStyle(connectionStyle);
		graphViewer.setInput(model);
	}

	@Override
	public ISelection getSelection() {
		if (graphViewer != null) {
			return graphViewer.getSelection();
		}
		return null;
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (graphViewer != null) {
			graphViewer.addSelectionChangedListener(listener);
		}
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		if (graphViewer != null) {
			graphViewer.removeSelectionChangedListener(listener);
		}
	}

	@Override
	public void setSelection(ISelection selection) {
		if (graphViewer != null) {
			graphViewer.setSelection(selection);
		}
	}

	@Override
	public Control getControl() {
		if (graphViewer != null) {
			return graphViewer.getControl();
		}
		return null;
	}

	@Override
	public void setFocus() {
		if (graphViewer != null) {
			graphViewer.getControl().setFocus();
		}
	}

}
