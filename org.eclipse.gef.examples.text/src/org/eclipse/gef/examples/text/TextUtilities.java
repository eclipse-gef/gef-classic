/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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

package org.eclipse.gef.examples.text;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.tools.ToolUtilities;

/**
 * @since 3.1
 */
public class TextUtilities {

	public static boolean isForward(TextLocation beginLoc, TextLocation endLoc) {
		EditPart end = endLoc.part;
		EditPart begin = beginLoc.part;

		if (end == begin) {
			return endLoc.offset >= beginLoc.offset; // Bias towards forward
		}
		EditPart ancestor = ToolUtilities.findCommonAncestor(end, begin);
		while (end.getParent() != ancestor) {
			end = end.getParent();
		}
		while (begin.getParent() != ancestor) {
			begin = begin.getParent();
		}
		return ancestor.getChildren().indexOf(end) > ancestor.getChildren().indexOf(begin);
	}

}
