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
package org.eclipse.gef.examples.logicdesigner.rulers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.rulers.RulerChangeListener;
import org.eclipse.gef.rulers.RulerProvider;

import org.eclipse.gef.examples.logicdesigner.model.LogicGuide;
import org.eclipse.gef.examples.logicdesigner.model.LogicRuler;
import org.eclipse.gef.examples.logicdesigner.model.LogicSubpart;
import org.eclipse.gef.examples.logicdesigner.model.commands.CreateGuideCommand;
import org.eclipse.gef.examples.logicdesigner.model.commands.DeleteGuideCommand;
import org.eclipse.gef.examples.logicdesigner.model.commands.MoveGuideCommand;

/**
 * @author Pratik Shah
 */
public class LogicRulerProvider extends RulerProvider {

	private final LogicRuler ruler;
	private final PropertyChangeListener rulerListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(LogicRuler.PROPERTY_CHILDREN)) {
				LogicGuide guide = (LogicGuide) evt.getNewValue();
				if (getGuides().contains(guide)) {
					guide.addPropertyChangeListener(guideListener);
				} else {
					guide.removePropertyChangeListener(guideListener);
				}
				for (RulerChangeListener listener : listeners) {
					listener.notifyGuideReparented(guide);
				}
			} else {
				for (RulerChangeListener listener : listeners) {
					listener.notifyUnitsChanged(ruler.getUnit());
				}
			}
		}
	};
	private final PropertyChangeListener guideListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(LogicGuide.PROPERTY_CHILDREN)) {
				for (RulerChangeListener listener : listeners) {
					listener.notifyPartAttachmentChanged(evt.getNewValue(), evt.getSource());
				}
			} else {
				for (RulerChangeListener listener : listeners) {
					listener.notifyGuideMoved(evt.getSource());
				}
			}
		}
	};

	public LogicRulerProvider(LogicRuler ruler) {
		this.ruler = ruler;
		this.ruler.addPropertyChangeListener(rulerListener);
		getGuides().forEach(guide -> guide.addPropertyChangeListener(guideListener));
	}

	@Override
	public List<LogicSubpart> getAttachedModelObjects(Object guide) {
		return new ArrayList<>(((LogicGuide) guide).getParts());
	}

	@Override
	public Command getCreateGuideCommand(int position) {
		return new CreateGuideCommand(ruler, position);
	}

	@Override
	public Command getDeleteGuideCommand(Object guide) {
		return new DeleteGuideCommand((LogicGuide) guide, ruler);
	}

	@Override
	public Command getMoveGuideCommand(Object guide, int pDelta) {
		return new MoveGuideCommand((LogicGuide) guide, pDelta);
	}

	@Override
	public int[] getGuidePositions() {
		List<LogicGuide> guides = getGuides();
		int[] result = new int[guides.size()];
		for (int i = 0; i < guides.size(); i++) {
			result[i] = guides.get(i).getPosition();
		}
		return result;
	}

	@Override
	public Object getRuler() {
		return ruler;
	}

	@Override
	public int getUnit() {
		return ruler.getUnit();
	}

	@Override
	public void setUnit(int newUnit) {
		ruler.setUnit(newUnit);
	}

	@Override
	public int getGuidePosition(Object guide) {
		return ((LogicGuide) guide).getPosition();
	}

	@Override
	public List<LogicGuide> getGuides() {
		return ruler.getGuides();
	}

}
