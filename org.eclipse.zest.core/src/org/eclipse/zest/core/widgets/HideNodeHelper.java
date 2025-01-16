/*******************************************************************************
 * Copyright 2023, Sebastian Hollersbacher, Johannes Kepler Universität Linz
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors: Sebastian Hollersbacher
 ******************************************************************************/

package org.eclipse.zest.core.widgets;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.zest.core.widgets.internal.ContainerFigure;
import org.eclipse.zest.core.widgets.internal.GraphLabel;

import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A ContainerFigure that container buttons and labels for hiding and revealing
 * nodes. This class also provides helper methods for hiding/revealing nodes.
 *
 * @author Sebastian Hollersbacher
 * @since 1.8
 */
public class HideNodeHelper extends ContainerFigure {
	public static final int MARGIN = 5;

	private final GraphNode node;

	private final Button hideButton = new Button("-"); //$NON-NLS-1$
	private final Button revealButton = new Button("+"); //$NON-NLS-1$
	private int hiddenNodeCount = 0;
	private final GraphLabel hiddenNodesLabel = new GraphLabel("0", false); //$NON-NLS-1$

	private final HideNodeListener thisHideNodeListener;
	private final List<HideNodeListener> hideNodeListeners = new CopyOnWriteArrayList<>();

	/**
	 * Create a HideNodeHelper and add it to the node's nodeFigure
	 *
	 * @param node
	 */
	public HideNodeHelper(GraphNode node) {
		this.node = node;
		thisHideNodeListener = new HideNodeListener(node);
		createHideButtons(node.getNodeFigure());
	}

	private void createHideButtons(IFigure figure) {
		// create buttons and label for hiding nodes
		hideButton.setVisible(false);
		revealButton.setVisible(false);
		hiddenNodesLabel.setVisible(false);

		hideButton.addActionListener(event -> {
			node.setVisible(false);
			notifyHideNodeListener(false);
		});
		revealButton.addActionListener(event -> {
			for (HideNodeListener hideNodeListener : hideNodeListeners) {
				hideNodeListener.revealNode(); // try to reveal all connected nodes
			}
			revealButton.setVisible(false);
		});

		hiddenNodesLabel.setBackgroundColor(ColorConstants.red);
		hiddenNodesLabel.setForegroundColor(ColorConstants.black);

		this.add(hideButton);
		this.add(revealButton);
		this.add(hiddenNodesLabel);

		figure.add(this);
	}

	private void updateHideButtonFigure() {
		Rectangle bounds = node.getHideContainerBounds();
		int hideButtonSize = revealButton.getPreferredSize().width;

		this.setBounds(bounds);
		hideButton.setBounds(new Rectangle(bounds.x, bounds.y, hideButtonSize, hideButtonSize));
		revealButton.setBounds(new Rectangle(bounds.x + bounds.width - hideButtonSize,
				bounds.y + bounds.height - hideButtonSize, hideButtonSize, hideButtonSize));
		hiddenNodesLabel.setBounds(new Rectangle(node.getLocation().x + bounds.width - hideButtonSize,
				node.getLocation().y, hideButtonSize, hideButtonSize));
	}

	/**
	 * Show button for hiding a node
	 *
	 * @param visible
	 */
	public void setHideButtonVisible(boolean visible) {
		hideButton.setVisible(visible);
		updateHideButtonFigure();
	}

	/**
	 * Show button for revealing a node
	 *
	 * @param visible
	 */
	public void setRevealButtonVisible(boolean visible) {
		if (hiddenNodeCount > 0) {
			revealButton.setVisible(visible);
			updateHideButtonFigure();
		}
	}

	/**
	 * Resets buttons and label
	 */
	public void resetCounter() {
		hiddenNodeCount = 0;
		hideButton.setVisible(false);
		revealButton.setVisible(false);
		hiddenNodesLabel.setVisible(false);
	}

	private void notifyHideNodeListener(boolean visible) {
		if (visible) {
			// node has been revealed
			for (HideNodeListener hideNodeListenere : hideNodeListeners) {
				hideNodeListenere.fireNodeRevealed();
			}
		} else {
			// node has been hidden
			for (HideNodeListener hideNodeListener : hideNodeListeners) {
				hideNodeListener.fireNodeHidden();
			}
		}
	}

	/**
	 * Updates bounds of the node and its modelFigure
	 *
	 * @param bounds of the whole node including the margin for the buttons
	 */
	public void updateNodeBounds(Rectangle bounds) {
		node.getNodeFigure().setBounds(bounds);
		node.getModelFigure()
				.setBounds(new Rectangle(bounds.x + HideNodeHelper.MARGIN, bounds.y + HideNodeHelper.MARGIN,
						bounds.width - HideNodeHelper.MARGIN * 2, bounds.height - HideNodeHelper.MARGIN * 2));

	}

	/**
	 * Add listener to be notified when the node gets hidden/revealed
	 *
	 * @param listener of connected node
	 */
	public void addHideNodeListener(HideNodeListener listener) {
		if (!this.hideNodeListeners.contains(listener) && listener != thisHideNodeListener) {
			// new HideNodeListener of other node
			this.hideNodeListeners.add(listener);
		}
	}

	/**
	 * Remove listener that gets notified when the node gets hidden/revealed
	 *
	 * @param listener of connected node
	 */
	public void removeHideNodeListener(HideNodeListener listener) {
		this.hideNodeListeners.remove(listener);
	}

	/**
	 * Get HideNodeListener corresponding to the node of this HideNodeHelper
	 *
	 * @return HideNodeListener
	 */
	public HideNodeListener getHideNodesListener() {
		return this.thisHideNodeListener;
	}

	class HideNodeListener {
		private final GraphNode node;

		public HideNodeListener(GraphNode node) {
			this.node = node;
		}

		/**
		 * Fire if connected node got hidden
		 */
		public void fireNodeHidden() {
			increaseHiddenNodes();
		}

		/**
		 * Fire if connected node got revealed
		 */
		public void fireNodeRevealed() {
			decreaseHiddenNodes();
		}

		/**
		 * Fire this node should get revealed
		 */
		public void revealNode() {
			if (this.node.isVisible()) {
				return; // node already visible
			}
			this.node.setVisible(true);
			notifyHideNodeListener(true);
		}

		private void increaseHiddenNodes() {
			hiddenNodeCount++;
			hiddenNodesLabel.setVisible(true);
			hiddenNodesLabel.setText(Integer.toString(hiddenNodeCount));
			updateHideButtonFigure();
		}

		private void decreaseHiddenNodes() {
			hiddenNodeCount--;
			hiddenNodesLabel.setVisible(hiddenNodeCount > 0); // true if hidden node still exists
			hiddenNodesLabel.setText(Integer.toString(hiddenNodeCount));
			updateHideButtonFigure();
		}
	}
}
