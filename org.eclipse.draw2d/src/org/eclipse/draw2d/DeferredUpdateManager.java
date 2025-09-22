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
package org.eclipse.draw2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.geometry.Rectangle;

/**
 * An UpdateManager that asynchronously updates the affected figures.
 */
public class DeferredUpdateManager extends UpdateManager {

	/**
	 * Calls {@link DeferredUpdateManager#performUpdate()}.
	 */
	protected class UpdateRequest implements Runnable {

		public UpdateRequest() {
			// this constructor is here for the visibility of the constructor of this class
		}

		/**
		 * Calls {@link DeferredUpdateManager#performUpdate()}.
		 */
		@Override
		public void run() {
			performUpdate();
		}
	}

	private Rectangle damage;
	private Map<IFigure, Rectangle> dirtyRegions = new HashMap<>();

	private GraphicsSource graphicsSource;
	private final List<IFigure> invalidFigures = new ArrayList<>();
	private IFigure root;
	private boolean updateQueued;

	private boolean updating;
	private boolean validating;
	private List<Runnable> afterUpdate = new ArrayList<>();
	private int refreshRate = -1;

	/**
	 * Empty constructor.
	 */
	public DeferredUpdateManager() {
	}

	/**
	 * Constructs a new DererredUpdateManager with the given GraphicsSource.
	 *
	 * @param gs the graphics source
	 */
	public DeferredUpdateManager(GraphicsSource gs) {
		setGraphicsSource(gs);
	}

	/**
	 * Adds a dirty region (defined by the rectangle <i>x, y, w, h</i>) to the
	 * update queue. If the figure isn't visible or either the width or height are
	 * 0, the method returns without queueing the dirty region.
	 *
	 * @param figure the figure that contains the dirty region
	 * @param x      the x coordinate of the dirty region
	 * @param y      the y coordinate of the dirty region
	 * @param w      the width of the dirty region
	 * @param h      the height of the dirty region
	 */
	@Override
	public synchronized void addDirtyRegion(IFigure figure, int x, int y, int w, int h) {
		if (w == 0 || h == 0 || !figure.isShowing()) {
			return;
		}

		Rectangle rect = dirtyRegions.get(figure);
		if (rect == null) {
			rect = new Rectangle(x, y, w, h);
			dirtyRegions.put(figure, rect);
		} else {
			rect.union(x, y, w, h);
		}

		queueWork();
	}

	/**
	 * Adds the given figure to the update queue. Invalid figures will be validated
	 * before the damaged regions are repainted.
	 *
	 * @param f the invalid figure
	 */
	@Override
	public synchronized void addInvalidFigure(IFigure f) {
		if (invalidFigures.contains(f)) {
			return;
		}
		queueWork();
		invalidFigures.add(f);
	}

	/**
	 * Returns a Graphics object for the given region.
	 *
	 * @param region the region to be repainted
	 * @return the Graphics object
	 */
	protected Graphics getGraphics(Rectangle region) {
		if (graphicsSource == null) {
			return null;
		}
		return graphicsSource.getGraphics(region);
	}

	/**
	 * @since 3.10
	 */
	@Override
	protected void paint(GC gc) {
		if (!validating) {
			SWTGraphics graphics = new SWTGraphics(gc);
			if (!updating) {
				/**
				 * If a paint occurs not as part of an update, we should notify that the region
				 * is being painted. Otherwise, notification already occurs in repairDamage().
				 */
				Rectangle rect = graphics.getClip(new Rectangle());
				HashMap<IFigure, Rectangle> map = new HashMap<>();
				map.put(root, rect);
				firePainting(rect, map);
			}
			performValidation();
			root.paint(graphics);
			graphics.dispose();
		} else {
			/*
			 * If figures are being validated then we can simply add a dirty region here and
			 * update will repaint this region with other dirty regions when it gets to
			 * painting. We can't paint if we're not sure that all figures are valid.
			 */
			addDirtyRegion(root, new Rectangle(gc.getClipping()));
		}
	}

	/**
	 * Performs the update. Validates the invalid figures and then repaints the
	 * dirty regions.
	 *
	 * @see #validateFigures()
	 * @see #repairDamage()
	 */
	@Override
	public synchronized void performUpdate() {
		if (isDisposed() || updating) {
			return;
		}
		updating = true;
		try {
			performValidation();
			updateQueued = false;
			repairDamage();
			if (!afterUpdate.isEmpty()) {
				List<Runnable> chain = afterUpdate;
				afterUpdate = new ArrayList<>();
				chain.forEach(Runnable::run); // chain may queue additional Runnable.
				if (!afterUpdate.isEmpty()) {
					queueWork();
				}
			}
		} finally {
			updating = false;
		}
	}

	/**
	 * @see UpdateManager#performValidation()
	 */
	@Override
	public synchronized void performValidation() {
		if (invalidFigures.isEmpty() || validating) {
			return;
		}
		try {
			IFigure fig;
			validating = true;
			fireValidating();
			for (int i = 0; i < invalidFigures.size(); i++) {
				fig = invalidFigures.get(i);
				invalidFigures.set(i, null);
				fig.validate();
			}
		} finally {
			invalidFigures.clear();
			validating = false;
		}
	}

	/**
	 * Adds the given exposed region to the update queue and then performs the
	 * update.
	 *
	 * @param exposed the exposed region
	 */
	@Override
	public synchronized void performUpdate(Rectangle exposed) {
		addDirtyRegion(root, exposed);
		performUpdate();
	}

	/**
	 * Posts an {@link UpdateRequest} using {@link Display#asyncExec(Runnable)}. If
	 * work has already been queued, a new request is not needed.
	 */
	protected void queueWork() {
		if (!updateQueued) {
			sendUpdateRequest();
			updateQueued = true;
		}
	}

	/**
	 * Fires the <code>UpdateRequest</code> to the current display asynchronously.
	 *
	 * @since 3.2
	 */
	protected void sendUpdateRequest() {
		Display display = Display.getCurrent();
		if (display == null) {
			throw new SWTException(SWT.ERROR_THREAD_INVALID_ACCESS);
		}
		if (refreshRate <= 0) {
			display.asyncExec(new UpdateRequest());
		} else {
			display.timerExec(refreshRate, new UpdateRequest());
		}
	}

	/**
	 * Releases the graphics object, which causes the GraphicsSource to flush.
	 *
	 * @param graphics the graphics object
	 */
	protected void releaseGraphics(Graphics graphics) {
		graphics.dispose();
		graphicsSource.flushGraphics(damage);
	}

	/**
	 * Repaints the dirty regions on the update queue and calls
	 * {@link UpdateManager#firePainting(Rectangle, Map)}, unless there are no dirty
	 * regions.
	 */
	protected void repairDamage() {
		// Any regions marked dirty while calculating
		// the damage are processed in the next cycle
		Map<IFigure, Rectangle> oldRegions = dirtyRegions;
		dirtyRegions = new HashMap<>();

		oldRegions.forEach((figure, contribution) -> {
			IFigure walker = figure.getParent();
			// A figure can't paint beyond its own bounds
			contribution.intersect(figure.getBounds());
			while (!contribution.isEmpty() && walker != null) {
				walker.translateToParent(contribution);
				contribution.intersect(walker.getBounds());
				walker = walker.getParent();
			}
			if (damage == null) {
				damage = new Rectangle(contribution);
			} else {
				damage.union(contribution);
			}
		});

		if (!oldRegions.isEmpty()) {
			firePainting(damage, oldRegions);
		}

		if (damage != null && !damage.isEmpty()) {
			Graphics graphics = getGraphics(damage);
			if (graphics != null) {
				root.paint(graphics);
				releaseGraphics(graphics);
			}
		}
		damage = null;
	}

	/**
	 * Adds the given runnable and queues an update if an update is not under
	 * progress.
	 *
	 * @param runnable the runnable
	 */
	@Override
	public synchronized void runWithUpdate(Runnable runnable) {
		afterUpdate.add(runnable);
		if (!updating) {
			queueWork();
		}
	}

	/**
	 * Sets the graphics source.
	 *
	 * @param gs the graphics source
	 */
	@Override
	public void setGraphicsSource(GraphicsSource gs) {
		graphicsSource = gs;
	}

	/**
	 * Sets the root figure.
	 *
	 * @param figure the root figure
	 */
	@Override
	public void setRoot(IFigure figure) {
		root = figure;
	}

	/**
	 * Sets the rate with paint requests are executed. If set to either {@code 0} or
	 * a negative value, requests are executed as fast as possible (default
	 * behavior), otherwise every {@code refreshRate}ms.
	 *
	 * Example:
	 *
	 * <pre>
	 * setRefreshRate(500); // Paints every 500ms
	 * </pre>
	 *
	 * @param refreshRate The rate with which paint requests are executed.
	 * @since 3.18
	 */
	public void setRefreshRate(int refreshRate) {
		this.refreshRate = refreshRate;
	}

	/**
	 * Validates all invalid figures on the update queue and calls
	 * {@link UpdateManager#fireValidating()} unless there are no invalid figures.
	 */
	protected void validateFigures() {
		performValidation();
	}

}
