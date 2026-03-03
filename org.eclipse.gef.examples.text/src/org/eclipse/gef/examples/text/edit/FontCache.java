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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**
 * @author Pratik Shah
 * @since 3.1
 */
public class FontCache {

	private static Map<Key, Value> table = new HashMap<>();

	private FontCache() {
	}

	/**
	 * Clients should only check in fonts that they checked out from this cache, and
	 * should do only one check-in per checkout. If the given font is not found, a
	 * null pointer exception will be encountered.
	 */
	public static void checkIn(Font font) {
		FontData fd = font.getFontData()[0];
		Key key = new Key(fd.getName(), fd.getHeight(), (fd.getStyle() & SWT.BOLD) != 0,
				(fd.getStyle() & SWT.ITALIC) != 0);
		Value value = table.get(key);
		value.refCount--;
		if (value.refCount == 0) {
			table.remove(key);
			value.font.dispose();
		}
	}

	public static Font checkOut(String fontName, int fontHeight, boolean isBold, boolean isItalic) {
		Key key = new Key(fontName, fontHeight, isBold, isItalic);
		Value value = table.get(key);
		if (value == null) {
			value = new Value();
			int style = (isBold ? SWT.BOLD : SWT.NONE) | (isItalic ? SWT.ITALIC : SWT.NONE);
			value.font = new Font(null, new FontData(fontName, fontHeight, style));
			table.put(key, value);
		}
		value.refCount++;
		return value.font;
	}

	private static final class Key {
		private final String fontName;
		private final int height;
		private final boolean isBold, isItalic;

		private Key(String fontName, int height, boolean isBold, boolean isItalic) {
			this.fontName = fontName;
			this.height = height;
			this.isBold = isBold;
			this.isItalic = isItalic;
		}

		@Override
		public boolean equals(Object obj) {
			boolean result = obj == this;
			if (!result && (obj instanceof Key key)) {
				result = fontName.equalsIgnoreCase(key.fontName) && height == key.height && isBold == key.isBold
						&& isItalic == key.isItalic;
			}
			return result;
		}

		@Override
		public int hashCode() {
			return fontName.toLowerCase().hashCode() + height;
		}
	}

	private static class Value {
		private Font font;
		private int refCount;
	}

}
