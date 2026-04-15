/*******************************************************************************
 * Copyright (c) 2011, 2024 Stephan Schwiebert and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Stephan Schwiebert - initial API and implementation
 *******************************************************************************/
package org.eclipse.zest.tests.cloudio;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.zest.cloudio.ICloudLabelProvider;

public class TestLabelProvider extends BaseLabelProvider implements ICloudLabelProvider {

	public static final double WEIGHT = 0.987D;
	public static final float ANGLE = 12.34F;
	public static Color COLOR = new Color(new RGB(100, 100, 100));
	public static FontData[] FONT_DATA = Display.getDefault().getShells()[0].getFont().getFontData();

	@Override
	public String getLabel(Object element) {
		return element.toString();
	}

	@Override
	public double getWeight(Object element) {
		return WEIGHT;
	}

	@Override
	public Color getColor(Object element) {
		return COLOR;
	}

	@Override
	public FontData[] getFontData(Object element) {
		return FONT_DATA.clone();
	}

	@Override
	public float getAngle(Object element) {
		return ANGLE;
	}

	@Override
	public String getToolTip(Object element) {
		return getLabel(element);
	}

}
