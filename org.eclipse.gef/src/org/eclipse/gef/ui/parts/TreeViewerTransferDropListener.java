/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
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
package org.eclipse.gef.ui.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.requests.ChangeBoundsRequest;

class TreeViewerTransferDropListener extends AbstractTransferDropTargetListener {

	public TreeViewerTransferDropListener(EditPartViewer viewer) {
		super(viewer, TreeViewerTransfer.getInstance());
		setEnablementDeterminedByCommand(true);
	}

	@Override
	protected Request createTargetRequest() {
		ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_MOVE);
		request.setEditParts(getTransferedEditParts());
		return request;
	}

	@Override
	protected Command getCommand() {
		final CompoundCommand command = new CompoundCommand();
		final Request request = getTargetRequest();
		request.setType(isMove() ? RequestConstants.REQ_MOVE : RequestConstants.REQ_ORPHAN);

		getTransferedEditParts().stream().forEach(ep -> command.add(ep.getCommand(request)));

		// If reparenting, add all editparts to target editpart.
		if (!isMove()) {
			request.setType(RequestConstants.REQ_ADD);
			if (getTargetEditPart() == null) {
				command.add(UnexecutableCommand.INSTANCE);
			} else {
				command.add(getTargetEditPart().getCommand(getTargetRequest()));
			}
		}
		return command;
	}

	protected String getCommandName() {
		if (isMove()) {
			return RequestConstants.REQ_MOVE;
		}
		return RequestConstants.REQ_ADD;
	}

	@Override
	protected Collection<EditPart> getExclusionSet() {
		List<? extends EditPart> selection = getViewer().getSelectedEditParts();
		List<EditPart> exclude = new ArrayList<>(selection);
		exclude.addAll(includeChildren(selection));
		return exclude;
	}

	@SuppressWarnings("unchecked") // we are putting a List<? extends EditPart> into the object of the transfer so
									// casting is save
	private static List<? extends EditPart> getTransferedEditParts() {
		return (List<? extends EditPart>) TreeViewerTransfer.getInstance().getObject();
	}

	@Override
	protected void handleDragOver() {
		if (TreeViewerTransfer.getInstance().getViewer() != getViewer()) {
			getCurrentEvent().detail = DND.DROP_NONE;
			return;
		}
		getCurrentEvent().feedback = DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
		super.handleDragOver();
	}

	@SuppressWarnings("static-method")
	protected EditPart getSourceEditPart() {
		List<? extends EditPart> selection = getTransferedEditParts();
		if (selection == null || selection.isEmpty()) {
			return null;
		}
		return selection.get(0);
	}

	@Override
	protected ChangeBoundsRequest getTargetRequest() {
		return (ChangeBoundsRequest) super.getTargetRequest();
	}

	protected List<EditPart> includeChildren(List<? extends EditPart> list) {
		List<EditPart> result = new ArrayList<>();
		for (EditPart element : list) {
			List<? extends EditPart> children = element.getChildren();
			result.addAll(children);
			result.addAll(includeChildren(children));
		}
		return result;
	}

	@Override
	public boolean isEnabled(DropTargetEvent event) {
		if (event.detail != DND.DROP_MOVE) {
			return false;
		}
		return super.isEnabled(event);
	}

	protected boolean isMove() {
		EditPart source = getSourceEditPart();
		for (EditPart ep : getTransferedEditParts()) {
			if (ep.getParent() != source.getParent()) {
				return false;
			}
		}
		return source.getParent() == getTargetEditPart();
	}

	@Override
	protected void updateTargetRequest() {
		ChangeBoundsRequest request = getTargetRequest();
		request.setLocation(getDropLocation());
		request.setType(getCommandName());
	}

	@Override
	protected void updateTargetEditPart() {
		setTargetEditPart(calculateTargetEditPart());
	}

	private EditPart calculateTargetEditPart() {
		EditPart ep = getViewer().findObjectAtExcluding(getDropLocation(), Collections.emptyList(),
				getTargetingConditional());
		if (ep != null) {
			ep = ep.getTargetEditPart(getTargetRequest());
		}
		return ep;
	}

	private EditPartViewer.Conditional getTargetingConditional() {
		Collection<EditPart> excludes = getExclusionSet();
		return editPart -> !excludes.contains(editPart) && editPart.getTargetEditPart(getTargetRequest()) != null;
	}

}
