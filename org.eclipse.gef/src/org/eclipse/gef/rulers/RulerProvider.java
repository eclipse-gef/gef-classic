/*******************************************************************************
 * Copyright (c) 2003, 2024 IBM Corporation and others.
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
package org.eclipse.gef.rulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.internal.GEFMessages;

/**
 * This class provides an interface to interact with the ruler/guide feature
 * provided in GEF. A <code>RulerProvider</code> represents a ruler (and the
 * guides contained within), and provides the necessary information about them.
 * <p>
 * Clients wishing to utilize this GEF feature should do the following:
 * <ul>
 * <li>Extend this class and override the necessary methods. Also provide a
 * notification mechanism to notify registered <code>RulerChangeListener</code>s
 * of changes in ruler properties.</li>
 * <li>Set instances of that class as properties on the diagram's graphical
 * viewer (PROPERTY_HORIZONTAL_RULER and/or PROPERTY_VERTICAL_RULER)</li>
 * <li>Set PROPERTY_RULER_VISIBILITY to be <code>true</code> on the graphical
 * viewer.</li>
 * </ul>
 *
 * @author Pratik Shah
 * @since 3.0
 */
public abstract class RulerProvider {

	/**
	 * The following property should be set on the graphical viewer.
	 * PROPERTY_HORIZONTAL_RULER should have a RulerProvider as its value.
	 */
	public static final String PROPERTY_HORIZONTAL_RULER = "horizontal ruler"; //$NON-NLS-1$
	/**
	 * The following property should be set on the graphical viewer.
	 * PROPERTY_RULER_VISIBILITY should have a Boolean value.
	 */
	public static final String PROPERTY_RULER_VISIBILITY = "ruler$visibility"; //$NON-NLS-1$
	/**
	 * The following property should be set on the graphical viewer.
	 * PROPERTY_VERTICAL_RULER should have a RulerProvider as its value.
	 */
	public static final String PROPERTY_VERTICAL_RULER = "vertical ruler"; //$NON-NLS-1$

	/**
	 * Constant indicating that the ruler should display centimeters. Note that this
	 * setting does not affect how a guide's position is interpreted (it is always
	 * taken as pixels).
	 */
	public static final int UNIT_CENTIMETERS = 1;
	/**
	 * Constant indicating that the ruler should display inches. Note that this
	 * setting does not affect how a guide's position is interpreted (it is always
	 * taken as pixels).
	 */
	public static final int UNIT_INCHES = 0;
	/**
	 * Constant indicating that the ruler should display pixel count.
	 */
	public static final int UNIT_PIXELS = 2;
	/**
	 * Constant indicating that the ruler should display a custom unit per pixel
	 * count.
	 *
	 * The unit per pixel count shall be provided via {@link #getCustomRulerDPU()}.
	 *
	 * @since 3.20
	 */
	public static final int UNIT_CUSTOM = 3;

	/**
	 * A list of <code>RulerChangeListener</code>s that have to be notified of
	 * changes in ruler/guide properties.
	 */
	protected List<RulerChangeListener> listeners = new CopyOnWriteArrayList<>();

	/**
	 * The given listener will be notified of changes in ruler properties.
	 *
	 * @param listener the listener that is to be notified of changes in ruler
	 *                 properties
	 */
	public void addRulerChangeListener(RulerChangeListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Return the description of the control or specified child in the
	 * <code>result</code> field of the event object. Returning an empty string
	 * tells the client that the control or child does not have a description, and
	 * returning null tells the client to use the platform description.
	 *
	 * @param e     AccessibleEvent
	 * @param guide The guide whose accessibility information is requested
	 * @see org.eclipse.swt.accessibility.AccessibleAdapter#getDescription(AccessibleEvent)
	 */
	@SuppressWarnings("static-method")
	public void getAccGuideDescription(AccessibleEvent e, Object guide) {
		e.result = GEFMessages.Guide_Desc;
	}

	/**
	 * Return the given guide's name/label in the <code>result</code> field of the
	 * given event.
	 *
	 * @param e     AccessibleEvent
	 * @param guide The guide whose accessibility information is requested
	 * @see org.eclipse.swt.accessibility.AccessibleAdapter#getName(AccessibleEvent)
	 */
	@SuppressWarnings("static-method")
	public void getAccGuideName(AccessibleEvent e, Object guide) {
		e.result = GEFMessages.Guide_Label;
	}

	/**
	 * Return the guide's position in the <code>result</code> field of the given
	 * event.
	 *
	 * @param e     AccessibleEvent
	 * @param guide The guide whose accessibility information is requested
	 * @see org.eclipse.swt.accessibility.AccessibleControlAdapter#getValue(AccessibleControlEvent)
	 */
	public void getAccGuideValue(AccessibleControlEvent e, Object guide) {
		e.result = "" + getGuidePosition(guide); //$NON-NLS-1$
	}

	/**
	 * Returns a List of model objects that are attached to the given guide.
	 *
	 * @param guide the guide to which the model parts are attached.
	 * @return list of attached model objects
	 */
	@SuppressWarnings("static-method")
	public List<? extends Object> getAttachedModelObjects(Object guide) {
		return Collections.emptyList();
	}

	/**
	 * Returns a List of EditParts that are attached to the given guide.
	 *
	 * @param guide  the guide to which the EditParts are attached.
	 * @param viewer the GraphicalViewer in which these EditParts are shown.
	 * @return list of attached edit parts
	 */
	public List<? extends EditPart> getAttachedEditParts(Object guide, GraphicalViewer viewer) {
		List<? extends Object> attachedModelObjects = getAttachedModelObjects(guide);
		List<EditPart> attachedEditParts = new ArrayList<>(attachedModelObjects.size());

		for (Object attachedModelObject : attachedModelObjects) {
			EditPart editPart = viewer.getEditPartForModel(attachedModelObject);
			if (editPart != null) {
				attachedEditParts.add(editPart);
			}
		}

		return attachedEditParts;

	}

	/**
	 * Clients should override this method to return a Command to create a new guide
	 * at the given position.
	 *
	 * @param position The pixel position where the new guide is to be created
	 * @return UnexecutableCommand
	 */
	@SuppressWarnings("static-method")
	public Command getCreateGuideCommand(int position) {
		return UnexecutableCommand.INSTANCE;
	}

	/**
	 * When the unit to be used for the ruler is {@link #UNIT_CUSTOM} clients need
	 * to override this method to return the pixel dots per unit to be used for this
	 * ruler. Clients should override this method to return
	 *
	 * @return the dots per unit for the ruler, default is 1 which is equal to use
	 *         {@link #UNIT_PIXELS}.
	 *
	 * @since 3.20
	 */
	@SuppressWarnings("static-method")
	public int getCustomRulerDPU() {
		return 1;
	}

	/**
	 * Clients should override this method to return a Command to delete the given
	 * guide.
	 *
	 * @param guide The guide that is to be deleted
	 * @return UnexecutableCommand
	 */
	@SuppressWarnings("static-method")
	public Command getDeleteGuideCommand(Object guide) {
		return UnexecutableCommand.INSTANCE;
	}

	/**
	 * In most cases, there should be no need for clients to override this method.
	 *
	 * @param position The position of the guide that is to be found
	 * @return The guide object at the given position; <code>null</code> if no guide
	 *         exists at the given position
	 */
	public Object getGuideAt(int position) {
		for (Object guide : getGuides()) {
			if (position == getGuidePosition(guide)) {
				return guide;
			}
		}
		return null;
	}

	/**
	 * Clients should override this method to return a Command to move the given
	 * guide by the given amount.
	 *
	 * @param guide         The guide that is to be moved
	 * @param positionDelta The amount by which the guide is to be moved
	 * @return UnexecutableCommand
	 */
	@SuppressWarnings("static-method")
	public Command getMoveGuideCommand(Object guide, int positionDelta) {
		return UnexecutableCommand.INSTANCE;
	}

	/**
	 * Clients should override this method to return a list of all the guides set on
	 * this ruler.
	 *
	 * @return an empty list
	 */
	@SuppressWarnings("static-method")
	public List<? extends Object> getGuides() {
		return Collections.emptyList();
	}

	/**
	 * Clients should override this method to return an array of all the positions
	 * of all the guides on this ruler.
	 *
	 * @return an empty array
	 */
	@SuppressWarnings("static-method")
	public int[] getGuidePositions() {
		return new int[0];
	}

	/**
	 * Clients should override this method to return the position (in pixels) of the
	 * given guide.
	 *
	 * @param guide The guide whose position is to be determined
	 * @return <code>Integer.MIN_VALUE</code>
	 */
	@SuppressWarnings("static-method")
	public int getGuidePosition(Object guide) {
		return Integer.MIN_VALUE;
	}

	/**
	 * Clients should override this method to return a model representation of the
	 * ruler.
	 *
	 * @return <code>null</code>
	 */
	@SuppressWarnings("static-method")
	public Object getRuler() {
		return null;
	}

	/**
	 * Clients should override this method to return the units that the ruler should
	 * display: one of UNIT_INCHES, UNIT_CENTIMETERS, UNIT_PIXELS.
	 *
	 * @return UNIT_INCHES
	 */
	@SuppressWarnings("static-method")
	public int getUnit() {
		return UNIT_INCHES;
	}

	/**
	 * The given listener will not be notified of changes in the ruler anymore.
	 *
	 * @param listener the listener that is to be removed
	 */
	public void removeRulerChangeListener(RulerChangeListener listener) {
		listeners.remove(listener);
	}

	/**
	 * This method will be invoked when the user requests that the ruler display a
	 * different measurement. The default implementation ignores the user's request.
	 *
	 * @param newUnit the new unit of measurement; will be one of
	 *                {@link #UNIT_CENTIMETERS}, {@link #UNIT_INCHES}, or
	 *                {@link #UNIT_PIXELS}
	 */
	public void setUnit(int newUnit) {
	}

}
