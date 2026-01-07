/*******************************************************************************
 * Copyright (c) 2024, 2026 Patrick Ziegler and others.
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

package org.eclipse.gef.test.swtbot;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefViewer;

import org.eclipse.draw2d.Animation;
import org.eclipse.draw2d.FigureCanvas;

import org.eclipse.gef.EditPart;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * To be able to execute these tests in the Eclipse IDE, the tests must
 * <b>NOT</b> be run in the UI thread.
 */
@SuppressWarnings("nls")
public abstract class AbstractSWTBotTests {
	private ILogListener problemListener;
	private List<Throwable> problems;
	protected SWTGefBot bot;

	@BeforeEach
	public void setUp() throws Exception {
		// Keep track of all unchecked exceptions
		problems = new ArrayList<>();
		problemListener = (status, plugin) -> {
			Throwable exception = status.getException();
			if (isRelevant(plugin, exception)) {
				problems.add(exception);
			}
		};
		Platform.addLogListener(problemListener);
		// Create SWTBot instance
		bot = new SWTGefBot();
	}

	/**
	 * Convenience method to filter all exceptions that are not thrown within
	 * GEF/Draw2D. This is to make sure that tests won't randomly fail due to
	 * (unrelated) exceptions from e.g. the Eclipse IDE.
	 */
	private static final boolean isRelevant(String plugin, Throwable throwable) {
		return throwable != null && plugin.matches("org.eclipse.(draw2d|gef).*");
	}

	@AfterEach
	public void tearDown() throws Exception {
		// Cleanup problem listener and check for exceptions
		Platform.removeLogListener(problemListener);
		problems.forEach(Throwable::printStackTrace);
		assertTrue(problems.isEmpty(), "Test threw an unchecked exception. Check logs for details");
	}

	/**
	 * Convenience method that forces an update of the GEF viewer. This is necessary
	 * when e.g. moving a figure, as the new position of the edit part is calculated
	 * asynchronously by the update manager (which is not handled by SWTBot). This
	 * method must be run from within the UI thread.
	 */
	protected static void forceUpdate(SWTBotGefViewer viewer) {
		SWTBotGefEditPart editPart = viewer.rootEditPart();
		EditPart gefEditPart = editPart.part();
		FigureCanvas figureCanvas = (FigureCanvas) gefEditPart.getViewer().getControl();
		figureCanvas.getLightweightSystem().getUpdateManager().performUpdate();
		waitEventLoop(0);
	}

	/**
	 * Blocks until the current animation has finished.
	 */
	protected static void waitForAnimation() {
		while (getAnimationState() != 0) {
			waitEventLoop(0);
		}
	}

	/**
	 * Waits given number of milliseconds and pumps the SWT event queue.<br>
	 * At least one events loop will be executed.
	 */
	protected static void waitEventLoop(int timeMillis) {
		long start = System.currentTimeMillis();
		do {
			// One of the events might dispose the shell...
			while (Display.getCurrent().readAndDispatch()) {
				// dispatch all updates
			}
		} while (System.currentTimeMillis() - start < timeMillis);
	}

	private static final int getAnimationState() {
		try {
			Field f = Animation.class.getDeclaredField("state");
			f.setAccessible(true);
			return f.getInt(null);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
