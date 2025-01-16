/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
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

package org.eclipse.draw2d;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Abstract implementation of the image figure. Implements attaching/detaching
 * mechanism for <code>ImageChangedListener</code>
 *
 * @author aboyko
 * @since 3.6
 */
public abstract class AbstractImageFigure extends Figure implements IImageFigure {

	private final List<ImageChangedListener> imageListeners = new CopyOnWriteArrayList<>();

	@Override
	public final void addImageChangedListener(ImageChangedListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		imageListeners.add(listener);
	}

	@Override
	public final void removeImageChangedListener(ImageChangedListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		imageListeners.remove(listener);
	}

	protected final void notifyImageChanged() {
		imageListeners.forEach(ImageChangedListener::imageChanged);
	}

}
