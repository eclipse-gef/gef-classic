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

package org.eclipse.gef.internal.mcp.tools;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
	public static String DESCRIPTION_RedoTool;
	public static String DESCRIPTION_UndoTool;
	public static String DESCRIPTION_CreateEditPartTool;
	public static String DESCRIPTION_DeleteEditPartTool;
	public static String DESCRIPTION_GetEditPartTool;
	public static String DESCRIPTION_GetSelectedEditPartTool;
	public static String DESCRIPTION_MoveEditPartTool;
	public static String DESCRIPTION_SelectEditPartTool;
	public static String DESCRIPTION_GetPaletteItemTool;
	public static String DESCRIPTION_GetSelectedPaletteItemTool;
	public static String DESCRIPTION_SelectPaletteItemTool;
	public static String ERROR_CommandNotExecutable;
	public static String ERROR_EditorNotFound;
	public static String ERROR_EditorNotSpecified;
	public static String ERROR_NoCreationTool;
	public static String ERROR_NoEditDomain;
	public static String ERROR_NoGraphicalViewer;
	public static String ERROR_NoPaletteTool;
	public static String ERROR_NoPaletteViewer;
	public static String ERROR_NoRedo;
	public static String ERROR_NoUndo;
	public static String SELECTION_PrimarySelected;
	public static String SELECTION_Selected;
	public static String SELECTION_Unknown;
	public static String SELECTION_Unselected;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
