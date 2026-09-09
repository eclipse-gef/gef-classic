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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.AdapterTypes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

@AdapterTypes(adaptableClass = { ITextEditor.class }, adapterNames = { IContentOutlinePage.class })
public class DOTContentOutlineAdapter implements IAdapterFactory {
	private static final ILog LOG = Platform.getLog(DOTContentOutlineAdapter.class);
	private static final String DOT_CONTENT_TYPE_ID = "org.eclipse.zest.dot.ui.content-type"; //$NON-NLS-1$

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adaptableObject instanceof ITextEditor editor) {
			IContentType contentType = getContentType(editor.getEditorInput());
			if (contentType != null && DOT_CONTENT_TYPE_ID.equals(contentType.getId())) {
				return adapterType.cast(new DOTContentOutlinePage(editor));
			}
		}
		return null;
	}

	private static IContentType getContentType(IEditorInput editorInput) {
		IFile inputFile = editorInput.getAdapter(IFile.class);
		if (inputFile == null) {
			return null;
		}
		IContentDescription contentDescription = null;
		try {
			contentDescription = inputFile.getContentDescription();
		} catch (CoreException e) {
			LOG.error(e.getMessage(), e);
		}
		if (contentDescription == null) {
			return null;
		}
		return contentDescription.getContentType();
	}
}
