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

import org.eclipse.swt.graphics.Image;

import org.eclipse.pde.api.tools.annotations.NoImplement;

/**
 * Interface for image figures
 *
 * <P>
 * WARNING: This interface is not intended to be implemented by clients. Extend
 * {@link AbstractImageFigure} instead.
 * 
 * @since 3.6
 */
@NoImplement
public interface IImageFigure extends IFigure {

	/**
	 * Returns the SWT Image contained by the figure
	 *
	 * @return the Image contained by the figure
	 * @since 3.6
	 */
	public Image getImage();

	/**
	 * Attaches <code>ImageChangedListener</code> to the figure
	 *
	 * @param listener
	 * @since 3.6
	 */
	public void addImageChangedListener(ImageChangedListener listener);

	/**
	 * Detaches <code>ImageChangedListener</code> from the figure
	 *
	 * @param listener
	 * @since 3.6
	 */
	public void removeImageChangedListener(ImageChangedListener listener);

	/**
	 * Listener to the image figure to track changes to the containede SWT Image
	 *
	 * @since 3.6
	 */
	public interface ImageChangedListener {

		/**
		 * Notifies about a change to SWT Image contained by the
		 * <code>IIMageFigure</code>
		 *
		 * @since 3.6
		 */
		public void imageChanged();

	}

}
