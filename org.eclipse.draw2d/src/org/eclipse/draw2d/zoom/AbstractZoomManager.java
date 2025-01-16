/*******************************************************************************
 * Copyright (c) 2000, 2023 IBM Corporation and others.
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
package org.eclipse.draw2d.zoom;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.FreeformFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Manage the primary zoom function in a graphical viewer.
 * <P>
 * A ZoomManager controls how zoom in and zoom out are performed. It also
 * determines the list of choices the user sees in the drop-down Combo on the
 * toolbar. The zoom manager controls a <code>ScalableFigure</code>, which
 * performs the actual zoom, and also a <code>Viewport</code>. The viewport is
 * needed so that the scrolled location is preserved as the zoom level changes.
 * <p>
 * <b>NOTE:</b> For the settings of {@link #isFitAll(String) Page},
 * {@link #isFitWidth(String) Width} and {@link #isFitHeight(String) Height} to
 * work properly, the given <code>Viewport</code> should have its scrollbars
 * always visible or never visible. Otherwise, these settings may cause
 * undesired effects.
 *
 * @author Dan Lee
 * @author Eric Bordeau
 * @since 3.13
 */
public abstract class AbstractZoomManager {

	/** Style bit meaning don't animate any zooms */
	public static final int ANIMATE_NEVER = 0;
	/** Style bit meaning animate during {@link #zoomIn()} and {@link #zoomOut()} */
	public static final int ANIMATE_ZOOM_IN_OUT = 1;

	private final List<ZoomListener> listeners = new CopyOnWriteArrayList<>();

	private double multiplier = 1.0;
	private final ScalableFigure pane;
	private final Viewport viewport;
	private double zoom = 1.0;
	private double[] zoomLevels = { .5, .75, 1.0, 1.5, 2.0, 2.5, 3, 4 };

	private List<String> zoomLevelContributions = Collections.emptyList();

	DecimalFormat format = new DecimalFormat("####%"); //$NON-NLS-1$

	/**
	 * The zoom scroll policy associated with this zoom manager. Zoom scroll
	 * policies regulate how the position of the scroll bars are treated during
	 * zooming.
	 */
	private IZoomScrollPolicy scrollPolicy;

	/**
	 * Creates a new ZoomManager.
	 *
	 * @param pane         The ScalableFigure associated with this ZoomManager
	 * @param viewport     The Viewport associated with this ZoomManager
	 * @param scrollPolicy The zoom scroll policy to be used with this ZoomManager
	 *
	 * @since 3.12
	 */
	protected AbstractZoomManager(ScalableFigure pane, Viewport viewport, IZoomScrollPolicy scrollPolicy) {
		this.pane = pane;
		this.viewport = viewport;
		this.scrollPolicy = scrollPolicy;
	}

	/**
	 * Creates a new ZoomManager.
	 *
	 * @param pane     The ScalableFigure associated with this ZoomManager
	 * @param viewport The Viewport associated with this ZoomManager
	 */
	protected AbstractZoomManager(ScalableFigure pane, Viewport viewport) {
		this(pane, viewport, new DefaultScrollPolicy());
	}

	/**
	 * Adds the given ZoomListener to this ZoomManager's list of listeners.
	 *
	 * @param listener the ZoomListener to be added
	 */
	public void addZoomListener(ZoomListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the given ZoomListener from this ZoomManager's list of listeners.
	 *
	 * @param listener the ZoomListener to be removed
	 */
	public void removeZoomListener(ZoomListener listener) {
		listeners.remove(listener);
	}

	/**
	 * returns <code>true</code> if the zoommanager can perform
	 * <code>zoomIn()</code>.
	 *
	 * @return boolean true if zoomIn can be called
	 */
	public boolean canZoomIn() {
		return getZoom() < getMaxZoom();
	}

	/**
	 * returns <code>true</code> if the zoommanager can perform
	 * <code>zoomOut()</code>.
	 *
	 * @return boolean true if zoomOut can be called
	 */
	public boolean canZoomOut() {
		return getZoom() > getMinZoom();
	}

	/**
	 * Notifies listeners that the zoom level has changed.
	 */
	protected void fireZoomChanged() {
		listeners.forEach(listener -> listener.zoomChanged(zoom));
	}

	private double getFitXZoomLevel(int which) {
		IFigure fig = getScalableFigure();
		final Dimension available = getViewport().getClientArea().getSize();
		final Dimension desired = (fig instanceof FreeformFigure freeFormFig)
				? freeFormFig.getFreeformExtent().getCopy().union(0, 0).getSize()
				: fig.getPreferredSize().getCopy();

		desired.width -= fig.getInsets().getWidth();
		desired.height -= fig.getInsets().getHeight();

		while (fig != getViewport()) {
			available.width -= fig.getInsets().getWidth();
			available.height -= fig.getInsets().getHeight();
			fig = fig.getParent();
		}

		double scaleX = Math.min(available.width * zoom / desired.width, getMaxZoom());
		double scaleY = Math.min(available.height * zoom / desired.height, getMaxZoom());
		if (which == 0) {
			return scaleX;
		}
		if (which == 1) {
			return scaleY;
		}
		return Math.min(scaleX, scaleY);
	}

	/**
	 * Calculates and returns the zoom percent required so that the entire height of
	 * the {@link #getScalableFigure() scalable figure} is visible on the screen.
	 * This is the zoom level associated with {@link #isFitHeight(String)}.
	 *
	 * @return zoom setting required to fit the scalable figure vertically on the
	 *         screen
	 */
	protected double getFitHeightZoomLevel() {
		return getFitXZoomLevel(1);
	}

	/**
	 * Calculates and returns the zoom percentage required to fit the entire
	 * {@link #getScalableFigure() scalable figure} on the screen. This is the zoom
	 * setting associated with {@link #isFitAll(String)}. It is the minimum of
	 * {@link #getFitHeightZoomLevel()} and {@link #getFitWidthZoomLevel()}.
	 *
	 * @return zoom setting required to fit the entire scalable figure on the screen
	 */
	protected double getFitPageZoomLevel() {
		return getFitXZoomLevel(2);
	}

	/**
	 * Calculates and returns the zoom percentage required so that the entire width
	 * of the {@link #getScalableFigure() scalable figure} is visible on the screen.
	 * This is the zoom setting associated with {@link #isFitWidth(String)}.
	 *
	 * @return zoom setting required to fit the scalable figure horizontally on the
	 *         screen
	 */
	protected double getFitWidthZoomLevel() {
		return getFitXZoomLevel(0);
	}

	/**
	 * Returns the maxZoom.
	 *
	 * @return double
	 */
	public double getMaxZoom() {
		return getZoomLevels()[getZoomLevels().length - 1];
	}

	/**
	 * Returns the minZoom.
	 *
	 * @return double
	 */
	public double getMinZoom() {
		return getZoomLevels()[0];
	}

	/**
	 * Returns the mutltiplier. This value is used to use zoom levels internally
	 * that are proportionally different than those displayed to the user. e.g. with
	 * a multiplier value of 2.0, the zoom level 1.0 will be displayed as "200%".
	 *
	 * @return double The multiplier
	 */
	public double getUIMultiplier() {
		return multiplier;
	}

	/**
	 * Returns the zoom level that is one level higher than the current level. If
	 * zoom level is at maximum, returns the maximum.
	 *
	 * @return double The next zoom level
	 */
	public double getNextZoomLevel() {
		for (double zoomLevel : zoomLevels) {
			if (zoomLevel > zoom) {
				return zoomLevel;
			}
		}
		return getMaxZoom();
	}

	/**
	 * Returns the zoom level that is one level higher than the current level. If
	 * zoom level is at maximum, returns the maximum.
	 *
	 * @return double The previous zoom level
	 */
	public double getPreviousZoomLevel() {
		for (int i = 1; i < zoomLevels.length; i++) {
			if (zoomLevels[i] >= zoom) {
				return zoomLevels[i - 1];
			}
		}
		return getMinZoom();
	}

	/**
	 * Returns the figure which performs the actual zooming.
	 *
	 * @return the scalable figure
	 */
	public ScalableFigure getScalableFigure() {
		return pane;
	}

	/**
	 * Returns the viewport.
	 *
	 * @return Viewport
	 */
	public Viewport getViewport() {
		return viewport;
	}

	/**
	 * Returns the current zoom level.
	 *
	 * @return double the zoom level
	 */
	public double getZoom() {
		return zoom;
	}

	/**
	 * Returns the current zoom level as a percentage formatted String
	 *
	 * @return String The current zoom level as a String
	 */
	public String getZoomAsText() {
		return format.format(zoom * multiplier);
	}

	/**
	 * Returns the list of strings that should be appended to the list of numerical
	 * zoom levels. These could be things such as Fit Width, Fit Page, etc. May
	 * return <code>null</code>.
	 *
	 * @return the list of contributed zoom levels
	 */
	public List<String> getZoomLevelContributions() {
		return zoomLevelContributions;
	}

	/**
	 * Returns the zoomLevels.
	 *
	 * @return double[]
	 */
	public double[] getZoomLevels() {
		return zoomLevels;
	}

	/**
	 * Returns the list of zoom levels as Strings in percent notation, plus any
	 * additional zoom levels that were contributed using
	 * {@link #setZoomLevelContributions(List)}.
	 *
	 * @return List The list of zoom levels
	 */
	public String[] getZoomLevelsAsText() {
		String[] zoomLevelStrings = new String[zoomLevels.length + zoomLevelContributions.size()];
		for (int i = 0; i < zoomLevels.length; i++) {
			zoomLevelStrings[i] = format.format(zoomLevels[i] * multiplier);
		}
		for (int i = 0; i < zoomLevelContributions.size(); i++) {
			zoomLevelStrings[i + zoomLevels.length] = zoomLevelContributions.get(i);
		}
		return zoomLevelStrings;
	}

	/**
	 * Sets the zoom level to the given value. Min-max range check is not done.
	 *
	 * @param zoom the new zoom level
	 */
	protected void primSetZoom(double zoom) {
		Point newLocation = scrollPolicy.calcNewViewLocation(getViewport(), this.zoom, zoom);
		this.zoom = zoom;
		pane.setScale(zoom);
		fireZoomChanged();
		getViewport().validate();
		setViewLocation(newLocation);
	}

	/**
	 * Sets the UI multiplier. The UI multiplier is applied to all zoom settings
	 * when they are presented to the user ({@link #getZoomAsText()}). Similarly,
	 * the multiplier is inversely applied when the user specifies a zoom level
	 * ({@link #setZoomAsText(String)}).
	 * <P>
	 * When the UI multiplier is <code>1.0</code>, the User will see the exact zoom
	 * level that is being applied. If the value is <code>2.0</code>, then a scale
	 * of <code>0.5</code> will be labeled "100%" to the User.
	 *
	 * @param multiplier The mutltiplier to set
	 */
	public void setUIMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}

	/**
	 * Sets the Viewport's view associated with this ZoomManager to the passed Point
	 *
	 * @param p The new location for the Viewport's view.
	 */
	public void setViewLocation(Point p) {
		viewport.setViewLocation(p.x, p.y);

	}

	/**
	 * Sets the zoom level to the given value. If the zoom is out of the min-max
	 * range, it will be ignored.
	 *
	 * @param zoom the new zoom level
	 */
	public void setZoom(double zoom) {
		zoom = Math.min(getMaxZoom(), zoom);
		zoom = Math.max(getMinZoom(), zoom);
		if (this.zoom != zoom) {
			primSetZoom(zoom);
		}
	}

	/**
	 * Sets which zoom methods get animated.
	 *
	 * @param style the style bits determining the zoom methods to be animated.
	 */
	public void setZoomAnimationStyle(int style) {
		// zoomAnimationStyle = style;
	}

	/**
	 * Sets zoom to the passed string. The string must be composed of numeric
	 * characters only with the exception of a decimal point and a '%' as the last
	 * character. If the zoom level contribution list has been set, this method
	 * should be overridden to provide the appropriate zoom implementation for the
	 * new zoom levels.
	 *
	 * @param zoomString The new zoom level
	 */
	public void setZoomAsText(String zoomString) {
		if (isFitHeight(zoomString)) {
			primSetZoom(getFitHeightZoomLevel());
			viewport.getUpdateManager().performUpdate();
			viewport.setViewLocation(viewport.getHorizontalRangeModel().getValue(),
					viewport.getVerticalRangeModel().getMinimum());
		} else if (isFitAll(zoomString)) {
			primSetZoom(getFitPageZoomLevel());
			viewport.getUpdateManager().performUpdate();
			viewport.setViewLocation(viewport.getHorizontalRangeModel().getMinimum(),
					viewport.getVerticalRangeModel().getMinimum());
		} else if (isFitWidth(zoomString)) {
			primSetZoom(getFitWidthZoomLevel());
			viewport.getUpdateManager().performUpdate();
			viewport.setViewLocation(viewport.getHorizontalRangeModel().getMinimum(),
					viewport.getVerticalRangeModel().getValue());
		} else {
			try {
				// Trim off the '%'
				if (zoomString.charAt(zoomString.length() - 1) == '%') {
					zoomString = zoomString.substring(0, zoomString.length() - 1);
				}
				double newZoom = NumberFormat.getInstance().parse(zoomString).doubleValue() / 100;
				setZoom(newZoom / multiplier);
			} catch (Exception e) {
				Display.getCurrent().beep();
			}
		}
	}

	/**
	 * Check if the given zoom string represents the fit width representation.
	 *
	 * Has to provided by subclasses.
	 *
	 * @param zoomString the zoomstring use to set a zoom level
	 * @return true if the the zoomString represents a fit width
	 * @since 3.12
	 */
	protected abstract boolean isFitWidth(String zoomString);

	/**
	 * Check if the given zoom string represents the fit all representation.
	 *
	 * Has to provided by subclasses.
	 *
	 * @param zoomString the zoomstring use to set a zoom level
	 * @return true if the the zoomString represents fit all
	 * @since 3.12
	 */
	protected abstract boolean isFitAll(String zoomString);

	/**
	 * Check if the given zoom string represents the fit height representation.
	 *
	 * Has to provided by subclasses.
	 *
	 * @param zoomString the zoomstring use to set a zoom level
	 * @return true if the the zoomString represents fit height
	 * @since 3.12
	 */
	protected abstract boolean isFitHeight(String zoomString);

	/**
	 * Sets the list of zoom level contributions (as strings). If you contribute
	 * something <b>other than</b> {@link #isFitHeight(String)},
	 * {@link #isFitWidth(String)} and {@link #isFitAll(String)} you must subclass
	 * this class and override the {@link #setZoomAsText(String)} method to
	 * implement your contributed zoom function.
	 *
	 * @param contributions the list of contributed zoom levels
	 */
	public void setZoomLevelContributions(List<String> contributions) {
		if (contributions != null) {
			zoomLevelContributions = contributions;
		} else {
			zoomLevelContributions = Collections.emptyList();
		}
	}

	/**
	 * Sets the zoomLevels.
	 *
	 * @param zoomLevels The zoomLevels to set
	 */
	public void setZoomLevels(double[] zoomLevels) {
		this.zoomLevels = zoomLevels;
	}

	/**
	 * @since 3.12
	 */
	public void setScrollPolicy(IZoomScrollPolicy scrollPolicy) {
		this.scrollPolicy = scrollPolicy;
	}

	/**
	 * Sets the zoom level to be one level higher
	 */
	public void zoomIn() {
		setZoom(getNextZoomLevel());
	}

	/**
	 * Currently does nothing.
	 *
	 * @param rect a rectangle
	 */
	public void zoomTo(Rectangle rect) {
	}

	// private void performAnimatedZoom(Rectangle rect, boolean zoomIn, int
	// iterationCount) {
	// double finalRatio;
	// double zoomIncrement;
	//
	// if (zoomIn) {
	// finalRatio = zoom / getNextZoomLevel();
	// zoomIncrement = (getNextZoomLevel() - zoom) / iterationCount;
	// } else {
	// finalRatio = zoom / getPreviousZoomLevel();
	// zoomIncrement = (getPreviousZoomLevel() - zoom) / iterationCount;
	// }
	//
	// getScalableFigure().translateToRelative(rect);
	// Point originalViewLocation = getViewport().getViewLocation();
	// Point finalViewLocation = calculateViewLocation(rect, finalRatio);
	//
	// double xIncrement =
	// (double) (finalViewLocation.x - originalViewLocation.x) / iterationCount;
	// double yIncrement =
	// (double) (finalViewLocation.y - originalViewLocation.y) / iterationCount;
	//
	// double originalZoom = zoom;
	// Point currentViewLocation = new Point();
	// for (int i = 1; i < iterationCount; i++) {
	// currentViewLocation.x = (int)(originalViewLocation.x + (xIncrement * i));
	// currentViewLocation.y = (int)(originalViewLocation.y + (yIncrement * i));
	// setZoom(originalZoom + zoomIncrement * i);
	// getViewport().validate();
	// setViewLocation(currentViewLocation);
	// getViewport().getUpdateManager().performUpdate();
	// }
	//
	// if (zoomIn)
	// setZoom(getNextZoomLevel());
	// else
	// setZoom(getPreviousZoomLevel());
	//
	// getViewport().validate();
	// setViewLocation(finalViewLocation);
	// }
	//
	// private Point calculateViewLocation(Rectangle zoomRect, double ratio) {
	// Point viewLocation = new Point();
	// viewLocation.x = (int)(zoomRect.x / ratio);
	// viewLocation.y = (int)(zoomRect.y / ratio);
	// return viewLocation;
	// }

	/**
	 * Sets the zoom level to be one level lower
	 */
	public void zoomOut() {
		setZoom(getPreviousZoomLevel());
	}

}
