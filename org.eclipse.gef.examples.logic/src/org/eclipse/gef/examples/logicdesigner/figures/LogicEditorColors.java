/*******************************************************************************
 * Copyright (c) 2000, 2026 IBM Corporation and others.
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
package org.eclipse.gef.examples.logicdesigner.figures;

import org.eclipse.swt.graphics.Color;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.IThemeManager;

public final class LogicEditorColors {

	private static final String AND_GATE_ID = "org.eclipse.gef.examples.logic.theme.andGate"; //$NON-NLS-1$
	private static final String OR_GATE_ID = "org.eclipse.gef.examples.logic.theme.orGate"; //$NON-NLS-1$
	private static final String XOR_GATE_ID = "org.eclipse.gef.examples.logic.theme.xorGate"; //$NON-NLS-1$
	private static final String OUTLINE_ID = "org.eclipse.gef.examples.logic.theme.outline"; //$NON-NLS-1$
	private static final String OUTPUT_FIGURE_ID = "org.eclipse.gef.examples.logic.theme.outputFigure"; //$NON-NLS-1$
	private static final String LOGIC_ID = "org.eclipse.gef.examples.logic.theme.logic"; //$NON-NLS-1$
	private static final String LOGIC_HIGHLIGHT_ID = "org.eclipse.gef.examples.logic.theme.logicHighlight"; //$NON-NLS-1$
	private static final String CONNECTOR_ID = "org.eclipse.gef.examples.logic.theme.connector"; //$NON-NLS-1$
	private static final String LOGIC_BACKGROUND_ID = "org.eclipse.gef.examples.logic.theme.logicBackground"; //$NON-NLS-1$
	private static final String GHOST_FILL_ID = "org.eclipse.gef.examples.logic.theme.ghostFill"; //$NON-NLS-1$
	private static final String DISPLAY_TEXT_LED = "org.eclipse.gef.examples.logic.theme.displayTextLED"; //$NON-NLS-1$
	private static final String FEEDBACK_FILL_ID = "org.eclipse.gef.examples.logic.theme.feedbackFill"; //$NON-NLS-1$
	private static final String FEEDBACK_OUTLINE_ID = "org.eclipse.gef.examples.logic.theme.feedbackOutline"; //$NON-NLS-1$
	private static final String WIRE_TRUE_ID = "org.eclipse.gef.examples.logic.theme.wireTrue"; //$NON-NLS-1$
	private static final String WIRE_FALSE_ID = "org.eclipse.gef.examples.logic.theme.wireFalse"; //$NON-NLS-1$

	private final IThemeManager themeManager;

	public static final LogicEditorColors INSTANCE = new LogicEditorColors();

	private LogicEditorColors() {
		themeManager = PlatformUI.getWorkbench().getThemeManager();
	}

	public Color getAndGate() {
		return themeManager.getCurrentTheme().getColorRegistry().get(AND_GATE_ID);
	}

	public Color getOrGate() {
		return themeManager.getCurrentTheme().getColorRegistry().get(OR_GATE_ID);
	}

	public Color getXorGate() {
		return themeManager.getCurrentTheme().getColorRegistry().get(XOR_GATE_ID);
	}

	public Color getOutline() {
		return themeManager.getCurrentTheme().getColorRegistry().get(OUTLINE_ID);
	}

	public Color getOutputFigure() {
		return themeManager.getCurrentTheme().getColorRegistry().get(OUTPUT_FIGURE_ID);
	}

	public Color getLogic() {
		return themeManager.getCurrentTheme().getColorRegistry().get(LOGIC_ID);
	}

	public Color getLogicHighlight() {
		return themeManager.getCurrentTheme().getColorRegistry().get(LOGIC_HIGHLIGHT_ID);
	}

	public Color getConnector() {
		return themeManager.getCurrentTheme().getColorRegistry().get(CONNECTOR_ID);
	}

	public Color getLogicBackground() {
		return themeManager.getCurrentTheme().getColorRegistry().get(LOGIC_BACKGROUND_ID);
	}

	public Color getGhostFill() {
		return themeManager.getCurrentTheme().getColorRegistry().get(GHOST_FILL_ID);
	}

	public Color getDisplayTextLed() {
		return themeManager.getCurrentTheme().getColorRegistry().get(DISPLAY_TEXT_LED);
	}

	public Color getFeedbackFill() {
		return themeManager.getCurrentTheme().getColorRegistry().get(FEEDBACK_FILL_ID);
	}

	public Color getFeedbackOutline() {
		return themeManager.getCurrentTheme().getColorRegistry().get(FEEDBACK_OUTLINE_ID);
	}

	public Color getWireTrue() {
		return themeManager.getCurrentTheme().getColorRegistry().get(WIRE_TRUE_ID);
	}

	public Color getWireFalse() {
		return themeManager.getCurrentTheme().getColorRegistry().get(WIRE_FALSE_ID);
	}

}
