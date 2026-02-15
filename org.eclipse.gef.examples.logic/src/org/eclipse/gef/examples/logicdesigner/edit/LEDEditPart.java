/*******************************************************************************
 * Copyright (c) 2000 IBM Corporation and others.
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
package org.eclipse.gef.examples.logicdesigner.edit;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import org.eclipse.gef.AccessibleAnchorProvider;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.EditPolicy;

import org.eclipse.gef.examples.logicdesigner.LogicMessages;
import org.eclipse.gef.examples.logicdesigner.figures.LEDFigure;
import org.eclipse.gef.examples.logicdesigner.model.LED;

/**
 * Holds the EditPart signifying an LED.
 */
public class LEDEditPart extends LogicEditPart {

	private static Image LED_SEL_PRIM_BG;
	private static Image LED_SEL_SECD_BG;
	private static FontDescriptor ledDisplayFontDescriptor = null;

	private static Image createImage(String name) {
		try (InputStream stream = LEDFigure.class.getResourceAsStream(name)) {
			return new Image(null, stream);
		} catch (IOException ioe) {
		}
		return null;
	}

	@Override
	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {

			@Override
			public void getName(AccessibleEvent e) {
				e.result = LogicMessages.LogicPlugin_Tool_CreationTool_LED_Label;
			}

			@Override
			public void getValue(AccessibleControlEvent e) {
				e.result = Integer.toString(getLEDModel().getValue());
			}

		};
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new LEDEditPolicy());
	}

	/**
	 * Returns a newly created Figure to represent this.
	 *
	 * @return Figure of this.
	 */
	@Override
	protected IFigure createFigure() {
		IFigure ledFigure = new LEDFigure();
		ledFigure.setFont(getLEDDisplayFont());
		return ledFigure;
	}

	@Override
	public <T> T getAdapter(final Class<T> key) {
		if (key == AccessibleAnchorProvider.class) {
			return key.cast(new DefaultAccessibleAnchorProvider() {
				@Override
				public List<Point> getSourceAnchorLocations() {
					List<Point> list = new ArrayList<>();
					List<ConnectionAnchor> sourceAnchors = getNodeFigure().getSourceConnectionAnchors();
					sourceAnchors.forEach(anchor -> list.add(anchor.getReferencePoint().getTranslated(0, -3)));
					return list;
				}

				@Override
				public List<Point> getTargetAnchorLocations() {
					List<Point> list = new ArrayList<>();
					List<ConnectionAnchor> targetAnchors = getNodeFigure().getTargetConnectionAnchors();
					targetAnchors.forEach(anchor -> list.add(anchor.getReferencePoint().getTranslated(0, 3)));
					return list;
				}
			});
		}
		return super.getAdapter(key);
	}

	private static Image getBackgroundImage(int state) {
		if (state == SELECTED_PRIMARY) {
			if (LED_SEL_PRIM_BG == null) {
				LED_SEL_PRIM_BG = createImage("icons/ledbgprim.gif"); //$NON-NLS-1$
			}
			return LED_SEL_PRIM_BG;
		}
		if (state == SELECTED) {
			if (LED_SEL_SECD_BG == null) {
				LED_SEL_SECD_BG = createImage("icons/ledbgsel.gif"); //$NON-NLS-1$
			}
			return LED_SEL_SECD_BG;
		}
		return null;
	}

	@Override
	public LEDFigure getFigure() {
		return (LEDFigure) super.getFigure();
	}

	private Font getLEDDisplayFont() {
		return getViewer().getResourceManager().create(getLEDDisplayFontDescriptor());
	}

	private static FontDescriptor getLEDDisplayFontDescriptor() {
		if (ledDisplayFontDescriptor == null) {
			ledDisplayFontDescriptor = JFaceResources.getTextFontDescriptor().setHeight(24);
		}
		return ledDisplayFontDescriptor;
	}

	/**
	 * Returns the model of this as a LED.
	 *
	 * @return Model of this as an LED.
	 */
	protected LED getLEDModel() {
		return (LED) getModel();
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent change) {
		if (change.getPropertyName().equals(LED.P_VALUE)) {
			refreshVisuals();
		} else {
			super.propertyChange(change);
		}
	}

	/**
	 * Apart from the usual visual update, it also updates the numeric contents of
	 * the LED.
	 */
	@Override
	public void refreshVisuals() {
		getFigure().setValue(getLEDModel().getValue());
		super.refreshVisuals();
	}

	@Override
	public void setSelected(int i) {
		super.setSelected(i);
		refreshVisuals();
	}

}
